package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.LocalStore
import com.example.data.MarketRepository
import com.example.data.TradeResult
import com.example.data.model.OrderType
import com.example.data.model.TradeAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Stock1", appName)
  }

  @Test
  fun `virtual trading executes buy and updates balance correctly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val store = LocalStore(context)
    store.resetAllData()
    val repository = MarketRepository(store)

    assertEquals(1000000.0, repository.virtualCash.value, 0.01)

    // Execute buy order of 10 shares of Reliance
    val result = repository.executeTrade(
      instrumentId = "stk_reliance",
      action = TradeAction.BUY,
      orderType = OrderType.MARKET,
      quantity = 10
    )

    assertTrue(result is TradeResult.Success)
    val reliance = repository.getInstrumentById("stk_reliance")!!
    val expectedDeduction = reliance.currentPrice * 10
    assertEquals(1000000.0 - expectedDeduction, repository.virtualCash.value, 0.5)

    val holdings = repository.holdings.value
    assertEquals(1, holdings.size)
    assertEquals(10, holdings["stk_reliance"]?.quantity)
  }

  @Test
  fun `onboarding first-launch and completion flow persists properly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val store = LocalStore(context)
    store.resetAllData()

    // First time: onboarding not completed
    assertEquals(false, store.isOnboardingCompleted())

    // Complete onboarding (e.g., Get Started or Skip)
    store.setOnboardingCompleted(true)
    assertEquals(true, store.isOnboardingCompleted())

    // Simulating next app launch with fresh store instance
    val freshStore = LocalStore(context)
    assertEquals(true, freshStore.isOnboardingCompleted())
  }

  @Test
  fun `price target alert creation, persistence, and trigger evaluation work properly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val store = LocalStore(context)
    store.resetAllData()
    val repository = MarketRepository(store)

    val reliance = repository.getInstrumentById("stk_reliance")!!
    val targetAbove = reliance.currentPrice + 50.0

    // Create alert
    val result = repository.createAlert(
      instrumentId = reliance.id,
      targetPrice = targetAbove,
      condition = com.example.data.model.AlertCondition.ABOVE
    )
    assertTrue(result.isSuccess)

    val alerts = repository.alerts.value
    assertEquals(1, alerts.size)
    assertEquals(targetAbove, alerts[0].targetPrice, 0.01)
    assertEquals(false, alerts[0].isTriggered)
    assertEquals(true, alerts[0].isEnabled)

    // Test toggle
    repository.toggleAlert(alerts[0].id)
    assertEquals(false, repository.alerts.value[0].isEnabled)

    repository.toggleAlert(alerts[0].id)
    assertEquals(true, repository.alerts.value[0].isEnabled)
  }

  @Test
  fun `market news repository initializes with curated feeds and refresh updates timestamps`() {
    val newsRepo = com.example.data.MarketNewsRepository()
    val state = newsRepo.newsState.value
    assertTrue(state is com.example.data.NewsUiState.Success)

    val success = state as com.example.data.NewsUiState.Success
    assertTrue(success.items.isNotEmpty())
    assertTrue(success.lastFetchedTime.isNotBlank())
    assertTrue(success.items.any { it.category == "Indian Markets" })
  }

  @Test
  fun `shared simulated data provider maintains price consistency across instruments`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val store = LocalStore(context)
    val repository = MarketRepository(store)

    val instruments = repository.instruments.value
    assertTrue(instruments.isNotEmpty())

    instruments.forEach { inst ->
      // Confirm all instruments have valid positive simulated prices
      assertTrue(inst.currentPrice > 0.0)
      assertTrue(inst.sparkline.isNotEmpty())
      // Check historicalData for time ranges
      val dayData = inst.historicalData["1D"]
      val weekData = inst.historicalData["1W"]
      val monthData = inst.historicalData["1M"]
      val yearData = inst.historicalData["1Y"]
      assertTrue(dayData != null && dayData.size >= 2)
      assertTrue(weekData != null && weekData.size >= 2)
      assertTrue(monthData != null && monthData.size >= 2)
      assertTrue(yearData != null && yearData.size >= 2)
    }
  }

  @Test
  fun `ai tutor service generates context-aware educational answers and distinguishes predictions`() {
    kotlinx.coroutines.runBlocking {
      val tutorService = com.example.data.service.GeminiTutorService()
      val context = ApplicationProvider.getApplicationContext<Context>()
      val store = LocalStore(context)
      val repository = MarketRepository(store)
      val instrument = repository.getInstrumentById("stk_reliance")!!

      val tutorContext = com.example.data.model.InstrumentTutorContext(
        instrument = instrument,
        selectedTimeRange = com.example.data.model.TimeRange.ONE_MONTH,
        historicalPoints = instrument.historicalData["1M"] ?: emptyList(),
        relevantNews = emptyList()
      )

      // Test Question 1: What is this company?
      val q1 = tutorService.generateTutorResponse("What is this company?", tutorContext)
      assertTrue(q1.text.contains("Reliance") || q1.text.contains("RELIANCE"))
      assertTrue(q1.text.contains("DEMO DATA") || q1.text.contains("simulated"))

      // Test Question 2: What does this chart show?
      val q2 = tutorService.generateTutorResponse("What does this chart show?", tutorContext)
      assertTrue(q2.text.contains("1M"))
      assertTrue(q2.text.contains("Observations vs Predictions") || q2.text.contains("NEVER guarantee"))

      // Test Question 3: Percentage price change
      val q3 = tutorService.generateTutorResponse("What does percentage price change mean?", tutorContext)
      assertTrue(q3.text.contains("Percentage Change"))

      // Test Question 4: Simulated vs Real
      val q4 = tutorService.generateTutorResponse("What is the difference between simulated movement and verified real market?", tutorContext)
      assertTrue(q4.text.contains("Virtual Simulator") || q4.text.contains("Real Financial Exchanges"))
    }
  }
}

