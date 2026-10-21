package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.MarketCategory
import com.example.ui.AppDestination
import com.example.ui.Stock1ViewModel
import com.example.ui.components.AlertsManagerSheet
import com.example.ui.components.CreatePriceAlertDialog
import com.example.ui.components.GlassBottomNav
import com.example.ui.components.TradeBottomSheet
import com.example.ui.screens.AiTutorScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InstrumentDetailScreen
import com.example.ui.screens.EmailPasswordLoginScreen
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.MarketsScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PortfolioScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.Stock1Theme
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import kotlinx.coroutines.flow.collectLatest
import android.os.SystemClock
import android.widget.Toast

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Debug App Check tokens are for local testing only; register the token in Firebase Console.
        // Production builds must use a production attestation provider, not the debug provider.
        if (BuildConfig.DEBUG && FirebaseApp.initializeApp(this) != null) {
            FirebaseAppCheck.getInstance()
                .installAppCheckProviderFactory(DebugAppCheckProviderFactory.getInstance())
        }
        enableEdgeToEdge()
        setContent {
            Stock1Theme {
                Stock1App()
            }
        }
    }
}

@Composable
fun Stock1App(viewModel: Stock1ViewModel = viewModel()) {
    val currentDestination by viewModel.currentDestination.collectAsState()
    val isOnboardingVisible by viewModel.isOnboardingVisible.collectAsState()
    val selectedInstrumentId by viewModel.selectedInstrumentId.collectAsState()
    val selectedLessonId by viewModel.selectedLessonId.collectAsState()
    val isAiTutorOpen by viewModel.isAiTutorOpen.collectAsState()
    val tradeDialogState by viewModel.tradeDialogState.collectAsState()
    val createAlertDialogState by viewModel.createAlertDialogState.collectAsState()
    val isAlertsSheetOpen by viewModel.isAlertsSheetOpen.collectAsState()
    val alerts by viewModel.alerts.collectAsState()
    val newsState by viewModel.newsState.collectAsState()

    val instruments by viewModel.filteredInstruments.collectAsState()
    val allInstruments by viewModel.repository.instruments.collectAsState()
    val watchlist by viewModel.watchlistInstruments.collectAsState()
    val watchlistIds by viewModel.repository.watchlistIds.collectAsState()
    val portfolioSummary by viewModel.portfolioSummary.collectAsState()
    val transactions by viewModel.repository.transactions.collectAsState()
    val completedLessons by viewModel.repository.completedLessonIds.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val isTutorThinking by viewModel.isTutorThinking.collectAsState()
    val tutorContext by viewModel.tutorContext.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val selectedSortOption by viewModel.selectedSortOption.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    // Demo authentication is intentionally local-only until production auth is enabled.
    var isAuthenticated by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var lastHomeBackPress by remember { mutableLongStateOf(0L) }

    BackHandler(enabled = true) {
        when {
            tradeDialogState.isOpen -> viewModel.closeTradeSheet()
            createAlertDialogState.isOpen -> viewModel.closeCreateAlertDialog()
            isAlertsSheetOpen -> viewModel.closeAlertsSheet()
            isOnboardingVisible -> { /* onboarding owns its completion flow */ }
            !isAuthenticated -> { /* sign-in screen remains active */ }
            isAiTutorOpen -> viewModel.closeAiTutor()
            selectedInstrumentId != null -> viewModel.closeInstrumentDetail()
            selectedLessonId != null -> viewModel.closeLesson()
            currentDestination != AppDestination.HOME -> viewModel.navigateTo(AppDestination.HOME)
            else -> {
                val now = SystemClock.elapsedRealtime()
                if (now - lastHomeBackPress < 2000L) {
                    (context as? android.app.Activity)?.finish()
                } else {
                    lastHomeBackPress = now
                    Toast.makeText(
                        context,
                        "Press Back again to exit Stock1",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    val selectedInstrument = selectedInstrumentId?.let { id ->
        allInstruments.find { it.id == id }
    }

    val selectedLesson = selectedLessonId?.let { id ->
        viewModel.repository.lessons.find { it.id == id }
    }

    val isLoginVisible = !isOnboardingVisible && !isAuthenticated
    val isSubScreenOpen = isOnboardingVisible || isLoginVisible || isAiTutorOpen || selectedInstrumentId != null || selectedLessonId != null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (!isSubScreenOpen) {
                GlassBottomNav(
                    currentDestination = currentDestination,
                    onNavigate = { dest -> viewModel.navigateTo(dest) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFFAF9FD))
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            when {
                // First-time Onboarding Experience
                isOnboardingVisible -> {
                    OnboardingScreen(
                        onCompleteOnboarding = { viewModel.completeOnboarding() }
                    )
                }

                // Email/password authentication follows onboarding
                isLoginVisible -> {
                    EmailPasswordLoginScreen(onAuthenticated = { isAuthenticated = true })
                }

                // AI Financial Tutor Screen
                isAiTutorOpen -> {
                    AiTutorScreen(
                        messages = chatMessages,
                        isThinking = isTutorThinking,
                        onSendMessage = { query -> viewModel.sendTutorQuestion(query) },
                        onBack = { viewModel.closeAiTutor() },
                        context = tutorContext,
                        isGeminiConfigured = viewModel.isGeminiConfigured,
                        onRetry = { viewModel.retryLastQuestion() }
                    )
                }

                // Instrument Detail Screen
                selectedInstrument != null -> {
                    val related = allInstruments.filter {
                        it.category == selectedInstrument.category && it.id != selectedInstrument.id
                    }
                    val hasActiveAlert = alerts.any {
                        it.instrumentId == selectedInstrument.id && it.isEnabled && !it.isTriggered
                    }
                    InstrumentDetailScreen(
                        instrument = selectedInstrument,
                        relatedInstruments = related,
                        isWatchlisted = watchlistIds.contains(selectedInstrument.id),
                        hasActiveAlert = hasActiveAlert,
                        onOpenCreateAlert = { viewModel.openCreateAlertDialog(selectedInstrument) },
                        onOpenAiTutor = { inst, range -> viewModel.openAiTutorForInstrument(inst, range) },
                        onToggleWatchlist = { viewModel.toggleWatchlist(selectedInstrument.id) },
                        onBack = { viewModel.closeInstrumentDetail() },
                        onOpenTrade = { action -> viewModel.openTradeSheet(selectedInstrument, action) },
                        onSelectRelated = { id -> viewModel.openInstrumentDetail(id) }
                    )
                }

                // Main 5 Navigation Destinations
                else -> {
                    when (currentDestination) {
                        AppDestination.HOME -> {
                            val indices = allInstruments.filter { it.category == MarketCategory.INDICES }
                            val activeAlertsCount = alerts.count { it.isEnabled && !it.isTriggered }
                            HomeScreen(
                                portfolioSummary = portfolioSummary,
                                indices = indices,
                                watchlist = watchlist,
                                topMovers = allInstruments.sortedByDescending { it.changePercent },
                                newsState = newsState,
                                onRefreshNews = { viewModel.refreshNews() },
                                activeAlertsCount = activeAlertsCount,
                                onOpenAlertsManager = { viewModel.openAlertsSheet() },
                                onNavigate = { dest -> viewModel.navigateTo(dest) },
                                onInstrumentClick = { id -> viewModel.openInstrumentDetail(id) },
                                onCategoryClick = { cat ->
                                    viewModel.selectedCategoryFilter.value = cat
                                },
                                onAiTutorClick = { viewModel.openAiTutor() },
                                onOpenLesson = { lessonId ->
                                    val validId = viewModel.repository.lessons.firstOrNull { it.id == lessonId }?.id
                                        ?: viewModel.repository.lessons.firstOrNull()?.id
                                    if (validId != null) viewModel.openLesson(validId)
                                },
                                watchlistIds = watchlistIds,
                                onToggleWatchlist = { id -> viewModel.toggleWatchlist(id) }
                            )
                        }

                        AppDestination.MARKETS -> {
                            MarketsScreen(
                                instruments = instruments,
                                searchQuery = searchQuery,
                                onSearchQueryChange = { viewModel.searchQuery.value = it },
                                selectedCategory = selectedCategoryFilter,
                                onSelectCategory = { viewModel.selectedCategoryFilter.value = it },
                                sortOption = selectedSortOption,
                                onSelectSortOption = { viewModel.selectedSortOption.value = it },
                                watchlistIds = watchlistIds,
                                onToggleWatchlist = { id -> viewModel.toggleWatchlist(id) },
                                onInstrumentClick = { id -> viewModel.openInstrumentDetail(id) }
                            )
                        }

                        AppDestination.PORTFOLIO -> {
                            val instrumentsMap = allInstruments.associateBy { it.id }
                            PortfolioScreen(
                                summary = portfolioSummary,
                                transactions = transactions,
                                instrumentsMap = instrumentsMap,
                                onInstrumentClick = { id -> viewModel.openInstrumentDetail(id) },
                                onOpenTradeForHolding = { inst -> viewModel.openTradeSheet(inst) },
                                onNavigate = { dest -> viewModel.navigateTo(dest) },
                                onResetPortfolio = { viewModel.resetAllData() }
                            )
                        }

                        AppDestination.LEARN -> {
                            LearnScreen(
                                lessons = viewModel.repository.lessons,
                                completedLessonIds = completedLessons,
                                selectedLesson = selectedLesson,
                                onSelectLesson = { id -> viewModel.openLesson(id) },
                                onCloseLesson = { viewModel.closeLesson() },
                                onMarkCompleted = { id -> viewModel.markLessonCompleted(id) }
                            )
                        }

                        AppDestination.PROFILE -> {
                            ProfileScreen(
                                portfolioSummary = portfolioSummary,
                                completedLessonsCount = completedLessons.size,
                                totalLessonsCount = viewModel.repository.lessons.size,
                                tradesCount = transactions.size,
                                watchlistCount = watchlistIds.size,
                                onNavigate = { dest -> viewModel.navigateTo(dest) },
                                onOpenAiTutor = { viewModel.openAiTutor() },
                                onRevisitOnboarding = { viewModel.revisitOnboarding() },
                                onResetAllData = { viewModel.resetAllData() }
                            )
                        }
                    }
                }
            }

            // Virtual Trading Bottom Sheet Modal
            if (tradeDialogState.isOpen && tradeDialogState.instrument != null) {
                val inst = tradeDialogState.instrument!!
                val currentHolding = portfolioSummary.holdings.find { it.holding.instrumentId == inst.id }
                val ownedQuantity = currentHolding?.holding?.quantity ?: 0

                TradeBottomSheet(
                    instrument = inst,
                    initialAction = tradeDialogState.initialAction,
                    availableCash = portfolioSummary.availableCash,
                    ownedQuantity = ownedQuantity,
                    usdToInrRate = viewModel.repository.usdToInrRate,
                    onDismiss = { viewModel.closeTradeSheet() },
                    onExecuteTrade = { id, action, orderType, qty, limitPrice ->
                        viewModel.executeOrder(id, action, orderType, qty, limitPrice)
                    }
                )
            }

            // Create Price Target Alert Modal
            if (createAlertDialogState.isOpen && createAlertDialogState.instrument != null) {
                CreatePriceAlertDialog(
                    instrument = createAlertDialogState.instrument!!,
                    onDismiss = { viewModel.closeCreateAlertDialog() },
                    onSaveAlert = { id, price, condition ->
                        viewModel.createAlert(id, price, condition)
                    }
                )
            }

            // Price Alerts Manager & History Sheet
            if (isAlertsSheetOpen) {
                AlertsManagerSheet(
                    alerts = alerts,
                    onToggleAlert = { id -> viewModel.toggleAlert(id) },
                    onDeleteAlert = { id -> viewModel.deleteAlert(id) },
                    onDismiss = { viewModel.closeAlertsSheet() }
                )
            }
        }
    }
}
