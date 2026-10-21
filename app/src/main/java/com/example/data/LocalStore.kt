package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.AlertCondition
import com.example.data.model.MarketCategory
import com.example.data.model.OrderType
import com.example.data.model.PriceAlert
import com.example.data.model.PortfolioHolding
import com.example.data.model.TradeAction
import com.example.data.model.TransactionRecord
import org.json.JSONArray
import org.json.JSONObject

class LocalStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("stock1_prefs", Context.MODE_PRIVATE)

    companion object {
        const val INITIAL_VIRTUAL_CASH = 1000000.0 // ₹10,00,000 (10 Lakhs)
        private const val KEY_VIRTUAL_CASH = "virtual_cash"
        private const val KEY_REALIZED_PL = "realized_pl"
        private const val KEY_WATCHLIST = "watchlist_ids"
        private const val KEY_HOLDINGS = "holdings_json"
        private const val KEY_TRANSACTIONS = "transactions_json"
        private const val KEY_COMPLETED_LESSONS = "completed_lessons"
        private const val KEY_ONBOARDING_COMPLETED = "onboarding_completed"
        private const val KEY_ALERTS = "price_alerts_json"
    }

    // --- Virtual Cash ---
    fun getVirtualCash(): Double {
        return prefs.getString(KEY_VIRTUAL_CASH, null)?.toDoubleOrNull() ?: INITIAL_VIRTUAL_CASH
    }

    fun setVirtualCash(cash: Double) {
        prefs.edit().putString(KEY_VIRTUAL_CASH, cash.toString()).apply()
    }

    // --- Realized P&L ---
    fun getRealizedPnL(): Double {
        return prefs.getString(KEY_REALIZED_PL, "0.0")?.toDoubleOrNull() ?: 0.0
    }

    fun addRealizedPnL(amount: Double) {
        val current = getRealizedPnL()
        prefs.edit().putString(KEY_REALIZED_PL, (current + amount).toString()).apply()
    }

    // --- Watchlist ---
    fun getWatchlist(): Set<String> {
        val defaultList = setOf("idx_nifty50", "stk_reliance", "stk_apple", "cmd_gold", "cr_btc")
        return prefs.getStringSet(KEY_WATCHLIST, defaultList) ?: defaultList
    }

    fun toggleWatchlist(instrumentId: String): Boolean {
        val current = getWatchlist().toMutableSet()
        val isAdded: Boolean
        if (current.contains(instrumentId)) {
            current.remove(instrumentId)
            isAdded = false
        } else {
            current.add(instrumentId)
            isAdded = true
        }
        prefs.edit().putStringSet(KEY_WATCHLIST, current).apply()
        return isAdded
    }

    fun isInWatchlist(instrumentId: String): Boolean {
        return getWatchlist().contains(instrumentId)
    }

    // --- Holdings ---
    fun getHoldings(): Map<String, PortfolioHolding> {
        val jsonString = prefs.getString(KEY_HOLDINGS, null) ?: return emptyMap()
        val map = mutableMapOf<String, PortfolioHolding>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.getString("instrumentId")
                val holding = PortfolioHolding(
                    instrumentId = id,
                    symbol = obj.getString("symbol"),
                    name = obj.getString("name"),
                    category = MarketCategory.valueOf(obj.optString("category", MarketCategory.INDIAN_STOCKS.name)),
                    quantity = obj.getInt("quantity"),
                    averageBuyPrice = obj.getDouble("averageBuyPrice"),
                    currency = obj.optString("currency", "₹")
                )
                if (holding.quantity > 0) {
                    map[id] = holding
                }
            }
        } catch (_: Exception) {}
        return map
    }

    fun saveHoldings(holdings: Map<String, PortfolioHolding>) {
        val jsonArray = JSONArray()
        holdings.values.filter { it.quantity > 0 }.forEach { h ->
            val obj = JSONObject().apply {
                put("instrumentId", h.instrumentId)
                put("symbol", h.symbol)
                put("name", h.name)
                put("category", h.category.name)
                put("quantity", h.quantity)
                put("averageBuyPrice", h.averageBuyPrice)
                put("currency", h.currency)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HOLDINGS, jsonArray.toString()).apply()
    }

    // --- Transactions ---
    fun getTransactions(): List<TransactionRecord> {
        val jsonString = prefs.getString(KEY_TRANSACTIONS, null) ?: return emptyList()
        val list = mutableListOf<TransactionRecord>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    TransactionRecord(
                        id = obj.getString("id"),
                        instrumentId = obj.getString("instrumentId"),
                        symbol = obj.getString("symbol"),
                        name = obj.getString("name"),
                        action = TradeAction.valueOf(obj.getString("action")),
                        orderType = OrderType.valueOf(obj.optString("orderType", OrderType.MARKET.name)),
                        quantity = obj.getInt("quantity"),
                        executedPrice = obj.getDouble("executedPrice"),
                        totalAmount = obj.getDouble("totalAmount"),
                        timestamp = obj.getLong("timestamp"),
                        formattedDate = obj.getString("formattedDate")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun addTransaction(record: TransactionRecord) {
        val current = getTransactions().toMutableList()
        current.add(0, record) // Most recent first
        val jsonArray = JSONArray()
        current.take(100).forEach { r ->
            val obj = JSONObject().apply {
                put("id", r.id)
                put("instrumentId", r.instrumentId)
                put("symbol", r.symbol)
                put("name", r.name)
                put("action", r.action.name)
                put("orderType", r.orderType.name)
                put("quantity", r.quantity)
                put("executedPrice", r.executedPrice)
                put("totalAmount", r.totalAmount)
                put("timestamp", r.timestamp)
                put("formattedDate", r.formattedDate)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_TRANSACTIONS, jsonArray.toString()).apply()
    }

    // --- Completed Lessons ---
    fun getCompletedLessons(): Set<String> {
        return prefs.getStringSet(KEY_COMPLETED_LESSONS, emptySet()) ?: emptySet()
    }

    fun markLessonCompleted(lessonId: String) {
        val current = getCompletedLessons().toMutableSet()
        current.add(lessonId)
        prefs.edit().putStringSet(KEY_COMPLETED_LESSONS, current).apply()
    }

    // --- Onboarding ---
    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
    }

    fun setOnboardingCompleted(completed: Boolean = true) {
        prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, completed).apply()
    }

    // --- Price Target Alerts ---
    fun getAlerts(): List<PriceAlert> {
        val jsonString = prefs.getString(KEY_ALERTS, null) ?: return emptyList()
        val list = mutableListOf<PriceAlert>()
        try {
            val jsonArray = JSONArray(jsonString)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                list.add(
                    PriceAlert(
                        id = obj.getString("id"),
                        instrumentId = obj.getString("instrumentId"),
                        symbol = obj.getString("symbol"),
                        name = obj.getString("name"),
                        targetPrice = obj.getDouble("targetPrice"),
                        condition = AlertCondition.valueOf(obj.getString("condition")),
                        currency = obj.optString("currency", "₹"),
                        isEnabled = obj.optBoolean("isEnabled", true),
                        isTriggered = obj.optBoolean("isTriggered", false),
                        triggeredPrice = if (obj.has("triggeredPrice")) obj.getDouble("triggeredPrice") else null,
                        triggeredAtFormatted = if (obj.has("triggeredAtFormatted")) obj.getString("triggeredAtFormatted") else null,
                        createdAtFormatted = obj.getString("createdAtFormatted")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun saveAlerts(alerts: List<PriceAlert>) {
        val jsonArray = JSONArray()
        alerts.forEach { alert ->
            val obj = JSONObject().apply {
                put("id", alert.id)
                put("instrumentId", alert.instrumentId)
                put("symbol", alert.symbol)
                put("name", alert.name)
                put("targetPrice", alert.targetPrice)
                put("condition", alert.condition.name)
                put("currency", alert.currency)
                put("isEnabled", alert.isEnabled)
                put("isTriggered", alert.isTriggered)
                alert.triggeredPrice?.let { put("triggeredPrice", it) }
                alert.triggeredAtFormatted?.let { put("triggeredAtFormatted", it) }
                put("createdAtFormatted", alert.createdAtFormatted)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_ALERTS, jsonArray.toString()).apply()
    }

    fun addAlert(alert: PriceAlert) {
        val list = getAlerts().toMutableList()
        list.add(0, alert)
        saveAlerts(list)
    }

    fun updateAlert(alert: PriceAlert) {
        val list = getAlerts().map { if (it.id == alert.id) alert else it }
        saveAlerts(list)
    }

    fun deleteAlert(alertId: String) {
        val list = getAlerts().filter { it.id != alertId }
        saveAlerts(list)
    }

    // --- Reset ---
    fun resetAllData() {
        prefs.edit().clear().apply()
    }
}
