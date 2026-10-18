package io.github.ethanscharlie.virtualbank

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjuster
import java.time.temporal.TemporalAdjusters

class Bank(val context: Context) {
    private val accounts = HashMap<String, Float>()
    private var lastSync = getLastSunday()
    private var rules = ""

    init {
        val file = File(context.filesDir, "data.json") // TODO This is silly
        if (file.exists()) {
            val jString = context.openFileInput("data.json").bufferedReader().use { it.readText() }
            val j = JSONObject(jString)

            rules = j.getString("rules")
            lastSync = LocalDate.parse(j.getString("lastSync"))

            val jAccounts = j.getJSONObject("accounts")
            val keys = jAccounts.keys()

            while (keys.hasNext()) {
                val key = keys.next()
                val value = jAccounts.getDouble(key).toFloat()
                accounts[key] = value
            }
        }

        sync()
    }

    fun createAccount(name: String) {
        accounts[name] = 0.0f
        save()
    }

    fun removeAccount(name: String) {
        accounts.remove(name)
        save()
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

    fun getRules() = rules

    fun setRules(r: String) {
        rules = r
        save()
    }

    fun transfer(fromAccount: String, toAccount: String, v: Float) {
        if (!accounts.containsKey(fromAccount)) return
        if (!accounts.containsKey(toAccount)) return

        accounts.merge(fromAccount, v, Float::minus)
        accounts.merge(toAccount, v, Float::plus)

        save()
    }

    fun allAccountNames(): List<String> {
        return accounts.keys.toList()
    }

    fun allAccounts(): Map<String, Float> {
        return accounts.toMap()
    }

    fun sync() {
        var monthsSinceLastSync = ChronoUnit.MONTHS.between(lastSync, LocalDate.now())
        var biweeksSinceLastSync = ChronoUnit.WEEKS.between(lastSync, LocalDate.now()) / 2
        var weeksSinceLastSync = ChronoUnit.WEEKS.between(lastSync, LocalDate.now())
        val lines = rules.split("\n")

        while (
            monthsSinceLastSync > 0L ||
            biweeksSinceLastSync > 0L ||
            weeksSinceLastSync > 0L
        ) {
            lines.forEach { line ->
                val words = line.split(" ")
                if (words.isEmpty()) return

                if (when (line.split(" ").get(0)) {
                        "weekly" -> weeksSinceLastSync > 0L
                        "biweekly" -> biweeksSinceLastSync > 0L
                        "monthly" -> monthsSinceLastSync > 0L
                        else -> false
                    }
                ) {
                    runRule(words)
                }
            }

            monthsSinceLastSync--
            biweeksSinceLastSync--
            weeksSinceLastSync--
        }


        lastSync = getLastSunday()
        save()
    }

    private fun getLastSunday(): LocalDate {
        return LocalDate.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
    }

    private fun runRule(words: List<String>) {
        if (words.size < 4) return

        var shift = 1

        if (words.get(shift) == "if") {
            if (words.size < 9) return
            val conditionAccount1 = words.getOrNull(1 + shift) ?: return
            val condition = words.getOrNull(2 + shift) ?: return
            val conditionValue = words.getOrNull(3 + shift)?.toFloat() ?: return

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

            shift = 6
        }

        val fromAccount = words.getOrNull(shift) ?: return
        val toAccount = words.getOrNull(shift + 2) ?: return

        val vString = words.getOrNull(shift + 1) ?: return

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
        val j = JSONObject()

        j.put("rules", rules)
        j.put("lastSync", lastSync.toString())

        val jAccounts = JSONObject()
        accounts.forEach { (name, account) -> jAccounts.put(name, account) }
        j.put("accounts", jAccounts)

        context.openFileOutput("data.json", Context.MODE_PRIVATE).use { fos ->
            fos.write(j.toString().toByteArray())
        }
    }
}

