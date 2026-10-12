package io.github.ethanscharlie.virtualbank

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

class Bank {
    val accounts = HashMap<String, Float>()
    val weeklyRules = ArrayList<String>()
    val biweeklyRules = ArrayList<String>()
    val monthlyRules = ArrayList<String>()

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

    fun applyRemainingRules(account: String) {
        save()
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
        deposit("a", 100.0f)
        deposit("b", 10.0f)
        runRule("if b > 2 then a 10% b")

        val j = toJson()
        return j.toString()
    }
}

