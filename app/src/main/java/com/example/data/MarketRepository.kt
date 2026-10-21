package com.example.data

import android.content.Context
import com.example.data.model.AlertCondition
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.Instrument
import com.example.data.model.InstrumentTutorContext
import com.example.data.model.Lesson
import com.example.data.model.MarketCategory
import com.example.data.model.OrderType
import com.example.data.model.PortfolioHolding
import com.example.data.model.PriceAlert
import com.example.data.model.TradeAction
import com.example.data.model.TransactionRecord
import com.example.data.service.GeminiTutorService
import com.example.util.NotificationHelper
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

data class PortfolioSummary(
    val totalPortfolioValue: Double,
    val investedValue: Double,
    val availableCash: Double,
    val unrealizedPnL: Double,
    val unrealizedPnLPercent: Double,
    val realizedPnL: Double,
    val holdings: List<HoldingViewItem>
)

data class HoldingViewItem(
    val holding: PortfolioHolding,
    val currentPrice: Double,
    val currentPriceRupees: Double,
    val currentValueRupees: Double,
    val investedValueRupees: Double,
    val unrealizedPnL: Double,
    val unrealizedPnLPercent: Double
)

sealed class TradeResult {
    data class Success(val message: String, val transaction: TransactionRecord) : TradeResult()
    data class Error(val errorMessage: String) : TradeResult()
}

class MarketRepository(private val localStore: LocalStore) {

    private val _instruments = MutableStateFlow<List<Instrument>>(SampleDataProvider.instruments)
    val instruments: StateFlow<List<Instrument>> = _instruments.asStateFlow()

    private val _watchlistIds = MutableStateFlow<Set<String>>(localStore.getWatchlist())
    val watchlistIds: StateFlow<Set<String>> = _watchlistIds.asStateFlow()

    private val _virtualCash = MutableStateFlow<Double>(localStore.getVirtualCash())
    val virtualCash: StateFlow<Double> = _virtualCash.asStateFlow()

    private val _holdings = MutableStateFlow<Map<String, PortfolioHolding>>(localStore.getHoldings())
    val holdings: StateFlow<Map<String, PortfolioHolding>> = _holdings.asStateFlow()

    private val _transactions = MutableStateFlow<List<TransactionRecord>>(localStore.getTransactions())
    val transactions: StateFlow<List<TransactionRecord>> = _transactions.asStateFlow()

    private val _completedLessonIds = MutableStateFlow<Set<String>>(localStore.getCompletedLessons())
    val completedLessonIds: StateFlow<Set<String>> = _completedLessonIds.asStateFlow()

    private val _alerts = MutableStateFlow<List<PriceAlert>>(localStore.getAlerts())
    val alerts: StateFlow<List<PriceAlert>> = _alerts.asStateFlow()

    private val _triggeredAlertEvent = MutableSharedFlow<PriceAlert>()
    val triggeredAlertEvent: SharedFlow<PriceAlert> = _triggeredAlertEvent.asSharedFlow()

    val lessons: List<Lesson> = SampleDataProvider.lessons

    // Fixed FX rate for simulated conversion between USD and INR
    val usdToInrRate = 84.08

    fun getInstrumentById(id: String): Instrument? {
        return _instruments.value.find { it.id == id }
    }

    fun getPriceInRupees(instrument: Instrument): Double {
        return if (instrument.currency == "$") {
            roundTwoDecimals(instrument.currentPrice * usdToInrRate)
        } else {
            instrument.currentPrice
        }
    }

    private fun roundTwoDecimals(value: Double): Double {
        return BigDecimal(value).setScale(2, RoundingMode.HALF_UP).toDouble()
    }

    fun toggleWatchlist(instrumentId: String): Boolean {
        val newState = localStore.toggleWatchlist(instrumentId)
        _watchlistIds.value = localStore.getWatchlist()
        return newState
    }

    fun isWatchlisted(instrumentId: String): Boolean {
        return _watchlistIds.value.contains(instrumentId)
    }

    // Virtual Trading Simulator Execution
    fun executeTrade(
        instrumentId: String,
        action: TradeAction,
        orderType: OrderType,
        quantity: Int,
        customLimitPrice: Double? = null
    ): TradeResult {
        val instrument = getInstrumentById(instrumentId)
            ?: return TradeResult.Error("Instrument not found.")

        if (quantity <= 0) {
            return TradeResult.Error("Quantity must be greater than zero.")
        }

        val executionPrice = customLimitPrice ?: instrument.currentPrice
        val executionPriceRupees = if (instrument.currency == "$") {
            roundTwoDecimals(executionPrice * usdToInrRate)
        } else {
            executionPrice
        }

        val totalCostRupees = roundTwoDecimals(executionPriceRupees * quantity)
        val currentCash = _virtualCash.value
        val currentHoldings = _holdings.value.toMutableMap()
        val currentHolding = currentHoldings[instrumentId]

        val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())

        when (action) {
            TradeAction.BUY -> {
                if (totalCostRupees > currentCash) {
                    val deficit = roundTwoDecimals(totalCostRupees - currentCash)
                    return TradeResult.Error(
                        "Insufficient virtual funds. Order needs ₹%,.2f, but you have ₹%,.2f (Short by ₹%,.2f)."
                            .format(totalCostRupees, currentCash, deficit)
                    )
                }

                val newCash = roundTwoDecimals(currentCash - totalCostRupees)
                val newQuantity = (currentHolding?.quantity ?: 0) + quantity
                val previousInvested = (currentHolding?.quantity ?: 0) * (currentHolding?.averageBuyPrice ?: 0.0)
                val newAvgPrice = roundTwoDecimals((previousInvested + (executionPrice * quantity)) / newQuantity)

                val updatedHolding = PortfolioHolding(
                    instrumentId = instrument.id,
                    symbol = instrument.symbol,
                    name = instrument.name,
                    category = instrument.category,
                    quantity = newQuantity,
                    averageBuyPrice = newAvgPrice,
                    currency = instrument.currency
                )

                currentHoldings[instrumentId] = updatedHolding

                val transaction = TransactionRecord(
                    id = UUID.randomUUID().toString(),
                    instrumentId = instrument.id,
                    symbol = instrument.symbol,
                    name = instrument.name,
                    action = TradeAction.BUY,
                    orderType = orderType,
                    quantity = quantity,
                    executedPrice = executionPrice,
                    totalAmount = totalCostRupees,
                    timestamp = System.currentTimeMillis(),
                    formattedDate = formattedDate
                )

                // Persist
                localStore.setVirtualCash(newCash)
                localStore.saveHoldings(currentHoldings)
                localStore.addTransaction(transaction)

                _virtualCash.value = newCash
                _holdings.value = currentHoldings
                _transactions.value = localStore.getTransactions()

                return TradeResult.Success(
                    "Simulated Buy: Bought $quantity units of ${instrument.symbol} for ₹%,.2f."
                        .format(totalCostRupees),
                    transaction
                )
            }

            TradeAction.SELL -> {
                val ownedQuantity = currentHolding?.quantity ?: 0
                if (ownedQuantity < quantity) {
                    return TradeResult.Error(
                        "Insufficient holdings. You own $ownedQuantity units of ${instrument.symbol}, cannot sell $quantity units."
                    )
                }

                val proceedsRupees = totalCostRupees
                val newCash = roundTwoDecimals(currentCash + proceedsRupees)
                val remainingQuantity = ownedQuantity - quantity

                // Calculate realized P&L on this trade
                val avgBuyPriceRupees = if (instrument.currency == "$") {
                    roundTwoDecimals((currentHolding?.averageBuyPrice ?: executionPrice) * usdToInrRate)
                } else {
                    currentHolding?.averageBuyPrice ?: executionPrice
                }
                val realizedTradePnL = roundTwoDecimals((executionPriceRupees - avgBuyPriceRupees) * quantity)
                localStore.addRealizedPnL(realizedTradePnL)

                if (remainingQuantity > 0) {
                    currentHoldings[instrumentId] = currentHolding!!.copy(quantity = remainingQuantity)
                } else {
                    currentHoldings.remove(instrumentId)
                }

                val transaction = TransactionRecord(
                    id = UUID.randomUUID().toString(),
                    instrumentId = instrument.id,
                    symbol = instrument.symbol,
                    name = instrument.name,
                    action = TradeAction.SELL,
                    orderType = orderType,
                    quantity = quantity,
                    executedPrice = executionPrice,
                    totalAmount = proceedsRupees,
                    timestamp = System.currentTimeMillis(),
                    formattedDate = formattedDate
                )

                localStore.setVirtualCash(newCash)
                localStore.saveHoldings(currentHoldings)
                localStore.addTransaction(transaction)

                _virtualCash.value = newCash
                _holdings.value = currentHoldings
                _transactions.value = localStore.getTransactions()

                val pnlSign = if (realizedTradePnL >= 0) "+₹%,.2f".format(realizedTradePnL) else "-₹%,.2f".format(Math.abs(realizedTradePnL))
                return TradeResult.Success(
                    "Simulated Sell: Sold $quantity units of ${instrument.symbol} for ₹%,.2f (P&L: $pnlSign)."
                        .format(proceedsRupees),
                    transaction
                )
            }
        }
    }

    fun getPortfolioSummary(): PortfolioSummary {
        val cash = _virtualCash.value
        val holdingsMap = _holdings.value
        val instrumentMap = _instruments.value.associateBy { it.id }

        val holdingItems = mutableListOf<HoldingViewItem>()
        var totalHoldingsValueRupees = 0.0
        var totalInvestedRupees = 0.0

        for ((id, holding) in holdingsMap) {
            val instrument = instrumentMap[id] ?: continue
            val currentPrice = instrument.currentPrice
            val currentPriceRupees = getPriceInRupees(instrument)

            val avgBuyRupees = if (instrument.currency == "$") {
                roundTwoDecimals(holding.averageBuyPrice * usdToInrRate)
            } else {
                holding.averageBuyPrice
            }

            val currentValue = roundTwoDecimals(currentPriceRupees * holding.quantity)
            val invested = roundTwoDecimals(avgBuyRupees * holding.quantity)
            val pnl = roundTwoDecimals(currentValue - invested)
            val pnlPercent = if (invested > 0) roundTwoDecimals((pnl / invested) * 100.0) else 0.0

            totalHoldingsValueRupees += currentValue
            totalInvestedRupees += invested

            holdingItems.add(
                HoldingViewItem(
                    holding = holding,
                    currentPrice = currentPrice,
                    currentPriceRupees = currentPriceRupees,
                    currentValueRupees = currentValue,
                    investedValueRupees = invested,
                    unrealizedPnL = pnl,
                    unrealizedPnLPercent = pnlPercent
                )
            )
        }

        val totalPortfolioValue = roundTwoDecimals(cash + totalHoldingsValueRupees)
        val unrealizedPnL = roundTwoDecimals(totalHoldingsValueRupees - totalInvestedRupees)
        val unrealizedPnLPercent = if (totalInvestedRupees > 0) {
            roundTwoDecimals((unrealizedPnL / totalInvestedRupees) * 100.0)
        } else {
            0.0
        }

        return PortfolioSummary(
            totalPortfolioValue = totalPortfolioValue,
            investedValue = roundTwoDecimals(totalInvestedRupees),
            availableCash = roundTwoDecimals(cash),
            unrealizedPnL = unrealizedPnL,
            unrealizedPnLPercent = unrealizedPnLPercent,
            realizedPnL = roundTwoDecimals(localStore.getRealizedPnL()),
            holdings = holdingItems.sortedByDescending { it.currentValueRupees }
        )
    }

    fun markLessonCompleted(lessonId: String) {
        localStore.markLessonCompleted(lessonId)
        _completedLessonIds.value = localStore.getCompletedLessons()
    }

    fun createAlert(instrumentId: String, targetPrice: Double, condition: AlertCondition): Result<PriceAlert> {
        val instrument = getInstrumentById(instrumentId) ?: return Result.failure(Exception("Instrument not found"))
        if (targetPrice <= 0.0) return Result.failure(Exception("Target price must be greater than zero"))

        val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        val alert = PriceAlert(
            id = UUID.randomUUID().toString(),
            instrumentId = instrument.id,
            symbol = instrument.symbol,
            name = instrument.name,
            targetPrice = roundTwoDecimals(targetPrice),
            condition = condition,
            currency = instrument.currency,
            isEnabled = true,
            isTriggered = false,
            createdAtFormatted = dateFormat.format(Date())
        )
        localStore.addAlert(alert)
        _alerts.value = localStore.getAlerts()
        return Result.success(alert)
    }

    fun toggleAlert(alertId: String) {
        val current = _alerts.value.find { it.id == alertId } ?: return
        val updated = current.copy(isEnabled = !current.isEnabled)
        localStore.updateAlert(updated)
        _alerts.value = localStore.getAlerts()
    }

    fun deleteAlert(alertId: String) {
        localStore.deleteAlert(alertId)
        _alerts.value = localStore.getAlerts()
    }

    suspend fun tickSimulatedPrices(context: Context? = null) {
        val currentList = _instruments.value
        val updatedList = mutableListOf<Instrument>()

        for (inst in currentList) {
            val factor = (Math.sin((System.currentTimeMillis() / 2500.0) + inst.id.hashCode()) * 0.0016)
            val newPrice = roundTwoDecimals((inst.currentPrice * (1.0 + factor)).coerceAtLeast(1.0))
            val delta = roundTwoDecimals(newPrice - (inst.currentPrice - inst.change))
            val deltaPct = roundTwoDecimals((delta / (newPrice - delta).coerceAtLeast(1.0)) * 100.0)

            val updatedSparkline = if (inst.sparkline.isNotEmpty()) {
                inst.sparkline.drop(1) + newPrice
            } else {
                listOf(newPrice)
            }

            val updated1D = inst.historicalData["1D"]?.let {
                if (it.isNotEmpty()) it.drop(1) + newPrice else listOf(newPrice)
            } ?: updatedSparkline

            val updatedHistory = inst.historicalData.toMutableMap().apply {
                put("1D", updated1D)
            }

            val updatedInst = inst.copy(
                currentPrice = newPrice,
                change = delta,
                changePercent = deltaPct,
                dayHigh = maxOf(inst.dayHigh, newPrice),
                dayLow = minOf(inst.dayLow, newPrice),
                sparkline = updatedSparkline,
                historicalData = updatedHistory
            )
            updatedList.add(updatedInst)
        }

        _instruments.value = updatedList

        // Check alerts
        val currentAlerts = _alerts.value
        val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
        val instMap = updatedList.associateBy { it.id }

        for (alert in currentAlerts) {
            if (alert.isEnabled && !alert.isTriggered) {
                val inst = instMap[alert.instrumentId] ?: continue
                val triggered = when (alert.condition) {
                    AlertCondition.ABOVE -> inst.currentPrice >= alert.targetPrice
                    AlertCondition.BELOW -> inst.currentPrice <= alert.targetPrice
                }
                if (triggered) {
                    val updatedAlert = alert.copy(
                        isTriggered = true,
                        triggeredPrice = inst.currentPrice,
                        triggeredAtFormatted = dateFormat.format(Date())
                    )
                    localStore.updateAlert(updatedAlert)
                    _alerts.value = localStore.getAlerts()
                    if (context != null) {
                        NotificationHelper.sendPriceAlertNotification(context, updatedAlert, inst.currentPrice)
                    }
                    _triggeredAlertEvent.emit(updatedAlert)
                }
            }
        }
    }

    fun resetAllData() {
        localStore.resetAllData()
        _virtualCash.value = LocalStore.INITIAL_VIRTUAL_CASH
        _holdings.value = emptyMap()
        _transactions.value = emptyList()
        _watchlistIds.value = localStore.getWatchlist()
        _completedLessonIds.value = emptySet()
        _alerts.value = emptyList()
    }

    private val geminiTutorService = GeminiTutorService()

    // AI Financial Tutor Logic powered by Gemini & contextual guardrails
    suspend fun askTutor(query: String, context: InstrumentTutorContext? = null): ChatMessage {
        return geminiTutorService.generateTutorResponse(query, context)
    }

    fun isGeminiConfigured(): Boolean = geminiTutorService.isGeminiConfigured()
}
