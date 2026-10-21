package com.example.data

import com.example.data.model.MarketNewsItem
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

sealed class NewsUiState {
    object Loading : NewsUiState()
    data class Success(val items: List<MarketNewsItem>, val lastFetchedTime: String) : NewsUiState()
    data class Error(val message: String) : NewsUiState()
    object Offline : NewsUiState()
}

class MarketNewsRepository {

    private val _newsState = MutableStateFlow<NewsUiState>(NewsUiState.Loading)
    val newsState: StateFlow<NewsUiState> = _newsState.asStateFlow()

    private val sampleArticles = listOf(
        MarketNewsItem(
            id = "news_1",
            title = "Reserve Bank of India Holds Benchmark Repo Rate Steady at 6.50% Amid Inflation Moderation",
            summary = "The Monetary Policy Committee (MPC) unanimously decided to maintain repo rates, noting strong GDP growth resilience across domestic manufacturing and capital expenditure sectors.",
            sourceName = "The Economic Times",
            category = "Indian Markets",
            publishedTimeFormatted = "Published today, 10:30 AM",
            fetchedTimeFormatted = "",
            url = "https://economictimes.indiatimes.com/markets",
            isSampleFeed = true
        ),
        MarketNewsItem(
            id = "news_2",
            title = "NIFTY 50 and SENSEX Hit Fresh Mid-Session Peaks Driven by Banking and IT Weight Heavyweights",
            summary = "Foreign Institutional Investors (FIIs) turned net buyers in Indian equities, adding ₹2,450 crore into blue-chips following strong quarterly earnings from leading private lenders.",
            sourceName = "Livemint",
            category = "Indian Markets",
            publishedTimeFormatted = "Published today, 01:15 PM",
            fetchedTimeFormatted = "",
            url = "https://www.livemint.com/market",
            isSampleFeed = true
        ),
        MarketNewsItem(
            id = "news_3",
            title = "Tech Megacaps Propel NASDAQ Higher as Enterprise AI Compute Demand Accelerates",
            summary = "Cloud infrastructure spending revisions from top technology hyperscalers pushed enterprise semiconductor equities to new multi-month highs on Wall Street.",
            sourceName = "Bloomberg Markets",
            category = "Global Markets",
            publishedTimeFormatted = "Published today, 03:45 PM",
            fetchedTimeFormatted = "",
            url = "https://www.bloomberg.com/markets",
            isSampleFeed = true
        ),
        MarketNewsItem(
            id = "news_4",
            title = "Gold Stays Elevated Near Historic Highs as Central Banks Continue Diversifying Reserves",
            summary = "Precious metals saw steady accumulation from European and Asian sovereign funds seeking safety against geopolitical headwinds and currency volatility.",
            sourceName = "Reuters Financial",
            category = "Commodities",
            publishedTimeFormatted = "Published yesterday, 05:20 PM",
            fetchedTimeFormatted = "",
            url = "https://www.reuters.com/markets/commodities",
            isSampleFeed = true
        ),
        MarketNewsItem(
            id = "news_5",
            title = "Mutual Fund SIP Inflows in India Surpass ₹23,000 Crore Milestone in Monthly Contributions",
            summary = "Retail investor participation via Systematic Investment Plans (SIPs) demonstrated structural resilience, sustaining uninterrupted monthly inflows for the 38th consecutive month.",
            sourceName = "Moneycontrol",
            category = "Indian Markets",
            publishedTimeFormatted = "Published 2 days ago",
            fetchedTimeFormatted = "",
            url = "https://www.moneycontrol.com/news/business/markets",
            isSampleFeed = true
        )
    )

    init {
        loadNews()
    }

    fun loadNews() {
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val fetchedTime = timeFormat.format(Date())
        val updated = sampleArticles.map { it.copy(fetchedTimeFormatted = fetchedTime) }
        _newsState.value = NewsUiState.Success(updated, fetchedTime)
    }

    suspend fun refreshNews(): Result<Unit> {
        _newsState.value = NewsUiState.Loading
        delay(600) // Simulated network latency for educational feed
        val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val fetchedTime = timeFormat.format(Date())
        val updated = sampleArticles.map { it.copy(fetchedTimeFormatted = fetchedTime) }
        _newsState.value = NewsUiState.Success(updated, fetchedTime)
        return Result.success(Unit)
    }
}
