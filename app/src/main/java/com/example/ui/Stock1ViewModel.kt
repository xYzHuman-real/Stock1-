package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.LocalStore
import com.example.data.MarketRepository
import com.example.data.PortfolioSummary
import com.example.data.TradeResult
import com.example.data.MarketNewsRepository
import com.example.data.NewsUiState
import com.example.data.model.AlertCondition
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.Instrument
import com.example.data.model.InstrumentTutorContext
import com.example.data.model.Lesson
import com.example.data.model.MarketCategory
import com.example.data.model.OrderType
import com.example.data.model.PriceAlert
import com.example.data.model.TimeRange
import com.example.data.model.TradeAction
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppDestination {
    HOME,
    MARKETS,
    PORTFOLIO,
    LEARN,
    PROFILE
}

enum class MarketSortOption(val label: String) {
    POPULAR("Popular"),
    TOP_GAINERS("Top Gainers"),
    TOP_LOSERS("Top Losers"),
    PRICE_HIGH_LOW("Price: High to Low"),
    PRICE_LOW_HIGH("Price: Low to High"),
    NAME_AZ("Name: A to Z")
}

data class TradeDialogState(
    val isOpen: Boolean = false,
    val instrument: Instrument? = null,
    val initialAction: TradeAction = TradeAction.BUY
)

data class CreateAlertDialogState(
    val isOpen: Boolean = false,
    val instrument: Instrument? = null
)

class Stock1ViewModel(application: Application) : AndroidViewModel(application) {

    private val localStore = LocalStore(application.applicationContext)
    val repository = MarketRepository(localStore)

    // Navigation state
    private val _currentDestination = MutableStateFlow(AppDestination.HOME)
    val currentDestination: StateFlow<AppDestination> = _currentDestination.asStateFlow()

    // Onboarding state
    private val _isOnboardingVisible = MutableStateFlow(!localStore.isOnboardingCompleted())
    val isOnboardingVisible: StateFlow<Boolean> = _isOnboardingVisible.asStateFlow()

    fun completeOnboarding() {
        localStore.setOnboardingCompleted(true)
        _isOnboardingVisible.value = false
    }

    fun revisitOnboarding() {
        _isOnboardingVisible.value = true
    }

    // Sub-screen navigation states
    private val _selectedInstrumentId = MutableStateFlow<String?>(null)
    val selectedInstrumentId: StateFlow<String?> = _selectedInstrumentId.asStateFlow()

    private val _selectedLessonId = MutableStateFlow<String?>(null)
    val selectedLessonId: StateFlow<String?> = _selectedLessonId.asStateFlow()

    private val _isAiTutorOpen = MutableStateFlow(false)
    val isAiTutorOpen: StateFlow<Boolean> = _isAiTutorOpen.asStateFlow()

    private val _tutorContext = MutableStateFlow<InstrumentTutorContext?>(null)
    val tutorContext: StateFlow<InstrumentTutorContext?> = _tutorContext.asStateFlow()

    // Trade Dialog / Bottom Sheet
    private val _tradeDialogState = MutableStateFlow(TradeDialogState())
    val tradeDialogState: StateFlow<TradeDialogState> = _tradeDialogState.asStateFlow()

    // News Repository
    val newsRepository = MarketNewsRepository()
    val newsState = newsRepository.newsState

    // Alerts state
    val alerts = repository.alerts
    private val _createAlertDialogState = MutableStateFlow(CreateAlertDialogState())
    val createAlertDialogState: StateFlow<CreateAlertDialogState> = _createAlertDialogState.asStateFlow()

    private val _isAlertsSheetOpen = MutableStateFlow(false)
    val isAlertsSheetOpen: StateFlow<Boolean> = _isAlertsSheetOpen.asStateFlow()

    init {
        // Collect triggered alerts to show in-app banner/snackbar
        viewModelScope.launch {
            repository.triggeredAlertEvent.collectLatest { alert ->
                _snackbarEvent.emit("🔔 Alert Triggered: ${alert.symbol} reached ${alert.currency}%,.2f!".format(alert.triggeredPrice ?: alert.targetPrice))
            }
        }
        // Simulated live price ticks every 3.5 seconds for a dynamic, smooth market feel
        viewModelScope.launch {
            while (true) {
                kotlinx.coroutines.delay(3500)
                try {
                    repository.tickSimulatedPrices(getApplication<Application>().applicationContext)
                } catch (_: Exception) {}
            }
        }
    }

    // Markets Explorer filters
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<MarketCategory?>(null)
    val selectedSortOption = MutableStateFlow(MarketSortOption.POPULAR)

    // Filtered Instruments
    val filteredInstruments: StateFlow<List<Instrument>> = combine(
        repository.instruments,
        searchQuery,
        selectedCategoryFilter,
        selectedSortOption
    ) { all, query, category, sort ->
        var list = all

        if (category != null) {
            list = list.filter { it.category == category }
        }

        if (query.isNotBlank()) {
            val q = query.trim().lowercase()
            list = list.filter {
                it.symbol.lowercase().contains(q) ||
                it.name.lowercase().contains(q) ||
                it.category.displayName.lowercase().contains(q)
            }
        }

        when (sort) {
            MarketSortOption.POPULAR -> list
            MarketSortOption.TOP_GAINERS -> list.sortedByDescending { it.changePercent }
            MarketSortOption.TOP_LOSERS -> list.sortedBy { it.changePercent }
            MarketSortOption.PRICE_HIGH_LOW -> list.sortedByDescending { repository.getPriceInRupees(it) }
            MarketSortOption.PRICE_LOW_HIGH -> list.sortedBy { repository.getPriceInRupees(it) }
            MarketSortOption.NAME_AZ -> list.sortedBy { it.symbol }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Watchlist instruments
    val watchlistInstruments: StateFlow<List<Instrument>> = combine(
        repository.instruments,
        repository.watchlistIds
    ) { all, ids ->
        all.filter { ids.contains(it.id) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Portfolio summary
    val portfolioSummary: StateFlow<PortfolioSummary> = combine(
        repository.virtualCash,
        repository.holdings,
        repository.instruments
    ) { _, _, _ ->
        repository.getPortfolioSummary()
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        repository.getPortfolioSummary()
    )

    // UI Message Events (Snackbars / feedback)
    private val _snackbarEvent = MutableSharedFlow<String>()
    val snackbarEvent: SharedFlow<String> = _snackbarEvent.asSharedFlow()

    // AI Tutor Chat messages
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "welcome_msg",
                sender = ChatSender.TUTOR,
                text = "Hello! I am your Stock1 AI Tutor. Ask me anything about stock markets, candlestick charts, P/E ratios, ETFs, or risk diversification.",
                timestamp = System.currentTimeMillis()
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isTutorThinking = MutableStateFlow(false)
    val isTutorThinking: StateFlow<Boolean> = _isTutorThinking.asStateFlow()

    // Navigation actions
    fun navigateTo(destination: AppDestination) {
        _currentDestination.value = destination
        _selectedInstrumentId.value = null
        _selectedLessonId.value = null
        _isAiTutorOpen.value = false
    }

    fun openInstrumentDetail(instrumentId: String) {
        _selectedInstrumentId.value = instrumentId
    }

    fun closeInstrumentDetail() {
        _selectedInstrumentId.value = null
    }

    fun openLesson(lessonId: String) {
        _selectedLessonId.value = lessonId
    }

    fun closeLesson() {
        _selectedLessonId.value = null
    }

    fun openAiTutor(context: InstrumentTutorContext? = null) {
        _tutorContext.value = context
        _isAiTutorOpen.value = true
    }

    fun openAiTutorForInstrument(instrument: Instrument, selectedRange: TimeRange = TimeRange.ONE_DAY) {
        val history = instrument.historicalData[selectedRange.label].orEmpty()
        val currentNews = (newsState.value as? NewsUiState.Success)?.items ?: emptyList()
        val relevant = currentNews.filter { item ->
            item.category == instrument.category.displayName ||
            item.title.contains(instrument.symbol, ignoreCase = true) ||
            item.summary.contains(instrument.symbol, ignoreCase = true)
        }.take(3)
        val ctx = InstrumentTutorContext(
            instrument = instrument,
            selectedTimeRange = selectedRange,
            historicalPoints = history,
            relevantNews = relevant
        )
        openAiTutor(ctx)
    }

    fun closeAiTutor() {
        _isAiTutorOpen.value = false
        _tutorContext.value = null
    }

    fun openTradeSheet(instrument: Instrument, action: TradeAction = TradeAction.BUY) {
        _tradeDialogState.value = TradeDialogState(
            isOpen = true,
            instrument = instrument,
            initialAction = action
        )
    }

    fun closeTradeSheet() {
        _tradeDialogState.value = TradeDialogState(isOpen = false, instrument = null)
    }

    fun toggleWatchlist(instrumentId: String) {
        val isAdded = repository.toggleWatchlist(instrumentId)
        val symbol = repository.getInstrumentById(instrumentId)?.symbol ?: "Instrument"
        viewModelScope.launch {
            _snackbarEvent.emit(if (isAdded) "Added $symbol to Watchlist" else "Removed $symbol from Watchlist")
        }
    }

    fun executeOrder(
        instrumentId: String,
        action: TradeAction,
        orderType: OrderType,
        quantity: Int,
        customLimitPrice: Double? = null
    ) {
        viewModelScope.launch {
            when (val result = repository.executeTrade(instrumentId, action, orderType, quantity, customLimitPrice)) {
                is TradeResult.Success -> {
                    closeTradeSheet()
                    _snackbarEvent.emit(result.message)
                }
                is TradeResult.Error -> {
                    _snackbarEvent.emit(result.errorMessage)
                }
            }
        }
    }

    fun markLessonCompleted(lessonId: String) {
        repository.markLessonCompleted(lessonId)
        viewModelScope.launch {
            _snackbarEvent.emit("Lesson Completed! Progress saved.")
        }
    }

    fun resetAllData() {
        repository.resetAllData()
        _selectedInstrumentId.value = null
        _selectedLessonId.value = null
        _isAiTutorOpen.value = false
        closeTradeSheet()
        viewModelScope.launch {
            _snackbarEvent.emit("All virtual data reset to initial ₹10,00,000.")
        }
    }

    val isGeminiConfigured: Boolean get() = repository.isGeminiConfigured()

    private var _lastTutorQuestion: String? = null
    val lastTutorQuestion: String? get() = _lastTutorQuestion

    // AI Tutor Send
    fun sendTutorQuestion(questionText: String) {
        val clean = questionText.trim()
        if (clean.isBlank()) return
        _lastTutorQuestion = clean
        val previousMessages = _chatMessages.value.takeLast(10)

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = ChatSender.USER,
            text = clean,
            timestamp = System.currentTimeMillis()
        )

        _chatMessages.value = _chatMessages.value + userMsg
        _isTutorThinking.value = true

        viewModelScope.launch {
            val tutorReply = repository.askTutor(clean, _tutorContext.value, previousMessages)
            _chatMessages.value = _chatMessages.value + tutorReply
            _isTutorThinking.value = false
        }
    }

    fun retryLastQuestion() {
        _lastTutorQuestion?.let { sendTutorQuestion(it) }
    }

    // Price Target Alerts
    fun openCreateAlertDialog(instrument: Instrument) {
        _createAlertDialogState.value = CreateAlertDialogState(isOpen = true, instrument = instrument)
    }

    fun closeCreateAlertDialog() {
        _createAlertDialogState.value = CreateAlertDialogState(isOpen = false, instrument = null)
    }

    fun createAlert(instrumentId: String, targetPrice: Double, condition: AlertCondition) {
        val result = repository.createAlert(instrumentId, targetPrice, condition)
        viewModelScope.launch {
            if (result.isSuccess) {
                val alert = result.getOrThrow()
                closeCreateAlertDialog()
                _snackbarEvent.emit("Price alert set for ${alert.symbol} at ${alert.currency}%,.2f".format(alert.targetPrice))
            } else {
                _snackbarEvent.emit(result.exceptionOrNull()?.message ?: "Failed to set alert")
            }
        }
    }

    fun toggleAlert(alertId: String) {
        repository.toggleAlert(alertId)
    }

    fun deleteAlert(alertId: String) {
        repository.deleteAlert(alertId)
        viewModelScope.launch {
            _snackbarEvent.emit("Alert removed")
        }
    }

    fun openAlertsSheet() {
        _isAlertsSheetOpen.value = true
    }

    fun closeAlertsSheet() {
        _isAlertsSheetOpen.value = false
    }

    fun refreshNews() {
        viewModelScope.launch {
            newsRepository.refreshNews()
            _snackbarEvent.emit("Market News refreshed")
        }
    }
}
