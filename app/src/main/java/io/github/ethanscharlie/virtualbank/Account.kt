package io.github.ethanscharlie.virtualbank

import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

class Bank {
    val accounts = HashMap<String, Float>()
    val weeklyRules = ArrayList<String>()
    val biweeklyRules = ArrayList<String>()
    val monthlyRules = ArrayList<String>()
    var lastSync = getLastSunday()


    fun createAccount(name: String) {
        accounts[name] = 0.0f
    }

    fun balance(account: String): Float {
        return accounts.getOrDefault(account, 0.0f)
    }

    fun deposit(account: String, v: Float) {
        accounts.merge(account, v, Float::plus)
        save()
    }

    fun withdrawl(account: String, v: Float) {
        accounts.merge(account, v, Float::minus)
        save()
    }

    fun transfer(fromAccount: String, toAccount: String, v: Float) {
        if (!accounts.containsKey(fromAccount)) return
        if (!accounts.containsKey(toAccount)) return

        accounts.merge(fromAccount, v, Float::minus)
        accounts.merge(toAccount, v, Float::plus)

        save()
    }

    fun addRule(period: String, rule: String) {
        when (period) {
            "weekly" -> weeklyRules.add(rule)
            "biweekly" -> biweeklyRules.add(rule)
            "monthly" -> monthlyRules.add(rule)
        }
    }

    fun sync() {
        var monthsSinceLastSync = ChronoUnit.MONTHS.between(lastSync, LocalDate.now())
        while (monthsSinceLastSync > 0) {
            monthlyRules.forEach { rule -> runRule(rule) }
            monthsSinceLastSync--
        }

        var biweeksSinceLastSync = ChronoUnit.WEEKS.between(lastSync, LocalDate.now()) / 2
        while (biweeksSinceLastSync > 0) {
            biweeklyRules.forEach { rule -> runRule(rule) }
            biweeksSinceLastSync--
        }

        var weeksSinceLastSync = ChronoUnit.WEEKS.between(lastSync, LocalDate.now())
        while (weeksSinceLastSync > 0) {
            weeklyRules.forEach { rule -> runRule(rule) }
            weeksSinceLastSync--
        }


        lastSync = getLastSunday()
        save()
    }

    private fun getLastSunday(): LocalDate {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    }

    private fun toJson(): JSONObject {
        val j = JSONObject()

        val jWeeklyRules = JSONArray()
        weeklyRules.forEach { rule -> jWeeklyRules.put(rule) }
        j.put("weeklyRules", jWeeklyRules)

        val jBiweeklyRules = JSONArray()
        biweeklyRules.forEach { rule -> jBiweeklyRules.put(rule) }
        j.put("biweeklyRules", jBiweeklyRules)

        val jmonthlyRules = JSONArray()
        monthlyRules.forEach { rule -> jmonthlyRules.put(rule) }
        j.put("monthlyRules", jmonthlyRules)

        val jAccounts = JSONObject()
        accounts.forEach { (name, account) -> jAccounts.put(name, account) }
        j.put("accounts", jAccounts)

        return j
    }

    private fun runRule(rule: String) {
        val words: List<String> = rule.split(" ")
        if (words.size < 3) return

        var shift = 0

        if (words.get(0) == "if") {
            if (words.size < 8) return
            val conditionAccount1 = words.getOrNull(1) ?: return
            val condition = words.getOrNull(2) ?: return
            val conditionValue = words.getOrNull(3)?.toFloat() ?: return

            val conditionAccount1Value = accounts.getOrDefault(conditionAccount1, 0.0f)

            if (!when (condition) {
                    "<" -> conditionAccount1Value < conditionValue
                    ">" -> conditionAccount1Value > conditionValue
                    "=" -> conditionAccount1Value == conditionValue
                    "!=" -> conditionAccount1Value != conditionValue
                    else -> false
                }
            ) {
                return
            }

            shift = 5
        }

        val fromAccount = words.getOrNull(shift) ?: return
        val toAccount = words.getOrNull(shift + 2) ?: return

        val vString = words.getOrNull(shift + 1)
        vString ?: return

        var v = 0.0f
        if (vString.endsWith("%")) {
            val p = vString.dropLast(1).toFloat() / 100.0f
            v = accounts.getOrDefault(fromAccount, 0.0f) * p
        } else if (vString.endsWith("x")) {
            val base = vString.dropLast(1).toFloat()
            v = accounts.getOrDefault(fromAccount, 0.0f) - base
        } else {
            v = vString.toFloat()
        }

        accounts.merge(fromAccount, v, Float::minus)
        accounts.merge(toAccount, v, Float::plus)
    }

    private fun save() {
        val j = toJson()
    }

    fun test(): String {
        deposit("Checking", 253.0f)
        deposit("Biweekly_Food1", 0.0f)
        deposit("Biweekly_Food2", 0.0f)
        deposit("Food", 0.0f)
        deposit("Saving", 0.0f)

        addRule("biweekly", "Checking 50% Biweekly_Food1")
        addRule("biweekly", "Checking 100% Biweekly_Food2")
        addRule("weekly", "Biweekly_Food1 100% Food")
        addRule("weekly", "if Biweekly_Food1 = 0 then Biweekly_Food2 100% Food")
        addRule("weekly", "if Food > 25 then Food 25x Saving")

        lastSync = getLastSunday().minusWeeks(2)
        sync()

        val j = toJson()
        return j.toString()
    }
}

