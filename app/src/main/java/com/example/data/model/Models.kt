package com.example.data.model

enum class MarketCategory(val displayName: String, val shortLabel: String) {
    INDIAN_STOCKS("Indian Stocks", "Stocks"),
    GLOBAL_STOCKS("Global Stocks", "Global"),
    INDICES("Market Indices", "Indices"),
    ETFS("ETFs", "ETFs"),
    MUTUAL_FUNDS("Mutual Funds", "Funds"),
    COMMODITIES("Commodities", "Commodities"),
    FOREX("Currencies & Forex", "Forex"),
    CRYPTO("Cryptocurrency", "Crypto"),
    BONDS("Bonds & Fixed Income", "Bonds")
}

enum class TimeRange(val label: String) {
    ONE_DAY("1D"),
    ONE_WEEK("1W"),
    ONE_MONTH("1M"),
    ONE_YEAR("1Y"),
    ALL("ALL")
}

data class Instrument(
    val id: String,
    val symbol: String,
    val name: String,
    val category: MarketCategory,
    val currentPrice: Double,
    val currency: String = "₹",
    val change: Double,
    val changePercent: Double,
    val sparkline: List<Double>,
    val dayHigh: Double,
    val dayLow: Double,
    val fiftyTwoWeekHigh: Double,
    val fiftyTwoWeekLow: Double,
    val volume: String,
    val marketCap: String,
    val peRatio: Double? = null,
    val dividendYield: Double? = null,
    val beta: Double? = null,
    val description: String,
    val whyItMatters: String,
    val riskProfile: String, // "Low", "Moderate", "High", "Very High"
    val exchange: String, // NSE, BSE, NASDAQ, MCX, RBI, etc.
    val historicalData: Map<String, List<Double>> = emptyMap()
)

data class PortfolioHolding(
    val instrumentId: String,
    val symbol: String,
    val name: String,
    val category: MarketCategory,
    val quantity: Int,
    val averageBuyPrice: Double,
    val currency: String = "₹"
)

enum class OrderType(val label: String) {
    MARKET("Market Order"),
    LIMIT("Limit Order (Simulated)")
}

enum class TradeAction(val label: String) {
    BUY("Buy"),
    SELL("Sell")
}

data class TransactionRecord(
    val id: String,
    val instrumentId: String,
    val symbol: String,
    val name: String,
    val action: TradeAction,
    val orderType: OrderType,
    val quantity: Int,
    val executedPrice: Double,
    val totalAmount: Double,
    val timestamp: Long,
    val formattedDate: String
)

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class LessonSection(
    val heading: String,
    val text: String,
    val example: String? = null
)

data class Lesson(
    val id: String,
    val title: String,
    val category: String,
    val readTimeMinutes: Int,
    val summary: String,
    val sections: List<LessonSection>,
    val quiz: List<QuizQuestion>,
    val isCompleted: Boolean = false
)

enum class ChatSender {
    USER,
    TUTOR
}

data class ChatMessage(
    val id: String,
    val sender: ChatSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isEducationalFallback: Boolean = false
)

data class CandleStick(
    val open: Double,
    val high: Double,
    val low: Double,
    val close: Double
)

enum class AlertCondition(val label: String, val shortLabel: String) {
    ABOVE("Price rises above target", "≥ Target"),
    BELOW("Price falls below target", "≤ Target")
}

data class PriceAlert(
    val id: String,
    val instrumentId: String,
    val symbol: String,
    val name: String,
    val targetPrice: Double,
    val condition: AlertCondition,
    val currency: String = "₹",
    val isEnabled: Boolean = true,
    val isTriggered: Boolean = false,
    val triggeredPrice: Double? = null,
    val triggeredAtFormatted: String? = null,
    val createdAtFormatted: String
)

data class MarketNewsItem(
    val id: String,
    val title: String,
    val summary: String,
    val sourceName: String,
    val category: String, // "Indian Markets", "Global Markets", "Commodities", "Economy"
    val publishedTimeFormatted: String,
    val fetchedTimeFormatted: String,
    val url: String,
    val isSampleFeed: Boolean = true
)

data class InstrumentTutorContext(
    val instrument: Instrument,
    val selectedTimeRange: TimeRange = TimeRange.ONE_DAY,
    val historicalPoints: List<Double> = emptyList(),
    val relevantNews: List<MarketNewsItem> = emptyList()
)

