package io.github.ethanscharlie.virtualbank

import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDate

interface Rule {
    fun value(fromAccount: Account, toAccount: Account): Float
    fun toJson(): JSONObject
}

class Account {
    var balance = 0.0f

    fun toJson(): JSONObject {
        val j = JSONObject()
        j.put("balance", balance)
        return j
    }
}

class StaticRule(var v: Float) {
    fun value(fromAccount: Account, toAccount: Account): Float {
        return v
    }

    fun toJson(): JSONObject {
        val j = JSONObject()
        j.put("v", v)
        return j
    }
}

class PercentageRule(var percentage: Float) {
    fun value(fromAccount: Account, toAccount: Account): Float {
        return fromAccount.balance * (percentage / 100.0f)
    }

    fun toJson(): JSONObject {
        val j = JSONObject()
        j.put("percentage", percentage)
        return j
    }
}

class StaticRuleNonNegative(var v: Float) {
    fun value(fromAccount: Account, toAccount: Account): Float {
        return if (fromAccount.balance > v) v else fromAccount.balance
    }

    fun toJson(): JSONObject {
        val j = JSONObject()
        j.put("v", v)
        return j
    }
}

class PercentageOfExtraRule(var base: Float, var percentage: Float) {
    fun value(fromAccount: Account, toAccount: Account): Float {
        if (fromAccount.balance > base) {
            val extra = fromAccount.balance - base
            return extra * (percentage / 100.0f)
        } else {
            return 0.0f
        }
    }

    fun toJson(): JSONObject {
        val j = JSONObject()
        j.put("base", base)
        j.put("percentage", percentage)
        return j
    }
}

class Bank {
    val accounts = HashMap<String, Account>()
    val weeklyRules = ArrayList<Rule>()
    val biweeklyRules = ArrayList<Rule>()
    val monthlyRules = ArrayList<Rule>()

    fun createAccount(name: String) {
        accounts[name] = Account()
    }

    fun balance(account: String): Float {
        return 0.0f
    }

    fun deposit(account: String, v: Float) {
        if (!accounts.containsKey(account)) return
        accounts.get(account)?.balance += v
        save()
    }

    fun withdrawl(account: String, v: Float) {
        if (!accounts.containsKey(account)) return
        accounts.get(account)?.balance -= v
        save()
    }

    fun transfer(fromAccount: String, toAccount: String, v: Float) {
        if (!accounts.containsKey(fromAccount)) return
        if (!accounts.containsKey(toAccount)) return

        accounts.get(fromAccount)?.balance -= v
        accounts.get(toAccount)?.balance += v

        save()
    }

    fun applyRemainingRules(account: String) {
        save()
    }

    private fun toJson(): JSONObject {
        val j = JSONObject()

        val jWeeklyRules = JSONArray()
        weeklyRules.forEach { rule -> jWeeklyRules.put(rule.toJson()) }
        j.put("weeklyRules", jWeeklyRules)

        val jBiweeklyRules = JSONArray()
        biweeklyRules.forEach { rule -> jBiweeklyRules.put(rule.toJson()) }
        j.put("biweeklyRules", jBiweeklyRules)

        val jmonthlyRules = JSONArray()
        monthlyRules.forEach { rule -> jmonthlyRules.put(rule.toJson()) }
        j.put("monthlyRules", jmonthlyRules)

        val jAccounts = JSONObject()
        accounts.forEach { (name, account) -> jAccounts.put(name, account.toJson()) }
        j.put("accounts", jAccounts)

        return j
    }

    private fun save() {
        val j = toJson()
    }

    fun test(): String {
        val j = toJson()
        return j.toString()
    }
}

