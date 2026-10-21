package com.example.data.service

import com.google.firebase.FirebaseApp
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.type.GenerativeBackend
import com.example.data.SampleDataProvider
import com.example.data.model.ChatMessage
import com.example.data.model.ChatSender
import com.example.data.model.InstrumentTutorContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

/**
 * Uses Firebase AI Logic so a Gemini Developer API key is not embedded in the Android APK.
 * Firebase AI Logic, Firebase project configuration, and App Check must be configured
 * in the Firebase console for cloud responses to work.
 */
class GeminiTutorService {

    private val model by lazy {
        FirebaseAI.getInstance(GenerativeBackend.googleAI())
            .generativeModel("gemini-3.5-flash")
    }

    suspend fun generateTutorResponse(
        userQuery: String,
        context: InstrumentTutorContext?,
        conversationHistory: List<ChatMessage> = emptyList()
    ): ChatMessage = withContext(Dispatchers.IO) {
        val conversation = conversationHistory.takeLast(10).joinToString("\n") { message ->
            val role = if (message.sender == ChatSender.USER) "Learner" else "Tutor"
            "$role: ${message.text}"
        }
        val prompt = buildString {
            append(buildSystemPrompt(context))
            if (conversation.isNotBlank()) {
                append("\n\nRECENT CONVERSATION (for continuity only):\n")
                append(conversation)
            }
            append("\n\nCURRENT LEARNER QUESTION:\n")
            append(userQuery)
            append("\n\nAnswer as an educational tutor. Separate facts from observations and predictions. ")
            append("Never present Stock1 demo prices or simulated movements as real market data.")
        }

        val apiResponse = if (isGeminiConfigured()) {
            try {
                model.generateContent(prompt).text
            } catch (_: Exception) {
                null
            }
        } else {
            null
        }

        if (!apiResponse.isNullOrBlank()) {
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.TUTOR,
                text = apiResponse.trim(),
                timestamp = System.currentTimeMillis(),
                isEducationalFallback = false
            )
        } else {
            // Keep the existing educational fallback available when Firebase or Gemini is not configured.
            ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = ChatSender.TUTOR,
                text = generateContextualFallback(userQuery, context),
                timestamp = System.currentTimeMillis(),
                isEducationalFallback = true
            )
        }
    }

    fun isGeminiConfigured(): Boolean = try {
        FirebaseApp.getInstance().options.apiKey?.isNotBlank() == true
    } catch (_: Exception) {
        false
    }

    private fun buildSystemPrompt(context: InstrumentTutorContext?): String {
        val baseRules = """
            You are the Stock1 AI Financial Tutor, an educational mentor built to teach beginner investors how financial markets, instruments, charts, and risk work in a risk-free virtual simulator.
            
            MANDATORY PEDAGOGICAL & FINANCIAL ETHICS:
            1. Clearly explain concepts in beginner-friendly, structured terms with bullet points and key takeaways.
            2. You MUST explicitly distinguish observations from predictions.
            3. Explicitly explain that simulated chart movements and demo prices in Stock1 are for safe educational practice only and are NOT evidence of actual market behaviour.
            4. Never invent company facts, market news, financial statistics, or live prices. Treat Stock1's built-in descriptions, ratios, and demo ranges as app-provided educational metadata, not independently verified facts. If reliable sourced information is unavailable, say so clearly.
            5. NEVER provide real-time investment advice, guarantee future returns, or encourage reckless trading. Always prioritize capital preservation, diversification, and disciplined risk management.
        """.trimIndent()

        if (context == null) {
            return baseRules
        }

        val inst = context.instrument
        val newsSummary = if (context.relevantNews.isNotEmpty()) {
            context.relevantNews.joinToString("\n") {
                "- [${it.category}] ${it.title} (Source: ${it.sourceName}, ${it.publishedTimeFormatted})"
            }
        } else {
            "No verified, instrument-specific news headlines are attached. Do not infer or invent news."
        }

        val chartStats = if (context.historicalPoints.isNotEmpty()) {
            val min = context.historicalPoints.minOrNull() ?: inst.currentPrice
            val max = context.historicalPoints.maxOrNull() ?: inst.currentPrice
            "Recorded ${context.historicalPoints.size} historical points for range ${context.selectedTimeRange.label}. Range Low: ${inst.currency}%,.2f, Range High: ${inst.currency}%,.2f.".format(min, max)
        } else {
            "No recorded historical points are available for this selected range."
        }

        return """
            $baseRules
            
            ACTIVE INSTRUMENT CONTEXT (CURRENTLY VIEWED BY USER IN STOCK1):
            - Instrument: ${inst.name} (${inst.symbol}) on ${inst.exchange}
            - Category: ${inst.category.displayName} (${inst.category.shortLabel})
            - Simulated Current Price: ${inst.currency}%,.2f (DEMO DATA - NOT REAL EXCHANGE PRICE)
            - Daily Movement: ${inst.currency}%,.2f (${inst.changePercent}%)
            - Day Range: ${inst.currency}%,.2f to ${inst.currency}%,.2f
            - 52-Week Range: ${inst.currency}%,.2f to ${inst.currency}%,.2f
            - App-provided sample metrics (not independently verified): P/E ${inst.peRatio ?: "N/A"}, Dividend Yield ${inst.dividendYield?.let { "$it%" } ?: "N/A"}, Beta ${inst.beta}
            - Risk Profile: ${inst.riskProfile}
            - App-provided educational description (not independently verified): ${inst.description}
            - App-provided educational context (not independently verified): ${inst.whyItMatters}
            - Active Chart Range: ${context.selectedTimeRange.label} ($chartStats)
            - Relevant News Feed:
            $newsSummary
        """.trimIndent()
    }

    private fun generateContextualFallback(
        userQuery: String,
        context: InstrumentTutorContext?
    ): String {
        val q = userQuery.lowercase(Locale.ROOT)

        if (context != null) {
            val inst = context.instrument

            // 1. What is this company or instrument?
            if (q.contains("what is this company") || q.contains("what is this instrument") ||
                q.contains("about this company") || q.contains("tell me about ${inst.symbol.lowercase()}") ||
                q.contains("who is") || q.contains("what is ${inst.symbol.lowercase()}") ||
                q.contains("overview")
            ) {
                val formattedPrice = "${inst.currency}%,.2f".format(inst.currentPrice)
                val formattedDayLow = "${inst.currency}%,.2f".format(inst.dayLow)
                val formattedDayHigh = "${inst.currency}%,.2f".format(inst.dayHigh)
                val formatted52Low = "${inst.currency}%,.2f".format(inst.fiftyTwoWeekLow)
                val formatted52High = "${inst.currency}%,.2f".format(inst.fiftyTwoWeekHigh)
                val divYieldStr = inst.dividendYield?.let { "$it%" } ?: "N/A"

                return """
                    🏢 **Overview of ${inst.name} (${inst.symbol})**
                    
                    • **Category:** ${inst.category.displayName} on ${inst.exchange}
                    • **About (Stock1 educational catalog; not independently verified):** ${inst.description}
                    • **Why It Matters:** ${inst.whyItMatters}
                    • **Risk Profile:** **${inst.riskProfile}** (Beta: ${inst.beta})
                    
                    📊 **Simulated Valuation (DEMO DATA):**
                    • Current Simulated Price: **$formattedPrice**
                    • Day Range: $formattedDayLow – $formattedDayHigh
                    • 52-Week Range: $formatted52Low – $formatted52High
                    • P/E Ratio: ${inst.peRatio ?: "N/A"} | Dividend Yield: $divYieldStr
                    
                    ⚠️ *Note: Stock1 prices are simulated for hands-on educational learning. They do not represent real-time exchange prices or investment advice.*
                """.trimIndent()
            }

            // 2. What does this chart show?
            if (q.contains("chart show") || q.contains("what does this chart") || q.contains("explain chart") || q.contains("understand the chart")) {
                val minP = context.historicalPoints.minOrNull() ?: inst.currentPrice
                val maxP = context.historicalPoints.maxOrNull() ?: inst.currentPrice
                val formattedMin = "${inst.currency}%,.2f".format(minP)
                val formattedMax = "${inst.currency}%,.2f".format(maxP)
                val formattedPrice = "${inst.currency}%,.2f".format(inst.currentPrice)
                val sign = if (inst.change >= 0) "+" else ""
                val formattedChange = "$sign${inst.currency}%,.2f".format(inst.change)

                return """
                    📈 **Understanding the ${context.selectedTimeRange.label} Chart for ${inst.symbol}**
                    
                    • **Time Frame:** You are currently viewing the **${context.selectedTimeRange.label}** period (${context.historicalPoints.size} recorded sample intervals).
                    • **Simulated High / Low:** The simulated price oscillated between **$formattedMin** and **$formattedMax**.
                    • **Current Price:** **$formattedPrice** ($formattedChange or ${inst.changePercent}% today).
                    
                    🔍 **How to Read It:**
                    1. **The Price Line:** Represents sequential simulated closing prices. Upward slopes depict simulated buying momentum; dips indicate selling pressure.
                    2. **Vico / Canvas Modes:** You can toggle between smooth line charts and Japanese Candlesticks to inspect open, high, low, and close levels.
                    3. **Observations vs Predictions:** Chart patterns reflect past recorded points. They NEVER guarantee future price action.
                    
                    ⚠️ *Educational Note: Simulated chart movements are generated for learning pattern recognition and risk habits, not evidence of actual financial exchange behaviour.*
                """.trimIndent()
            }

            // 3. What does a percentage price change mean?
            if (q.contains("percentage") || q.contains("price change mean") || q.contains("percent mean") || q.contains("change percent")) {
                val isUp = inst.change >= 0
                val sign = if (isUp) "+" else ""
                val formattedChange = "$sign${inst.currency}%,.2f".format(inst.change)

                return """
                    📊 **Understanding Percentage Price Change**
                    
                    Today, **${inst.symbol}** shows a daily movement of **$sign${inst.changePercent}%** ($formattedChange).
                    
                    • **Mathematical Formula:**
                      `Percentage Change = (Current Price - Previous Close) ÷ Previous Close × 100%`
                    
                    • **Why Percentage Matters More than Rupee Amount:**
                      If a ₹10,000 share gains ₹100, that is a **1%** gain.
                      If a ₹100 share gains ₹100, that is a **100%** gain!
                      Percentages allow you to fairly compare instruments of vastly different share values.
                    
                    • **In Simulated Trading:** A positive percentage green badge indicates current simulated capital gain on the asset for the session.
                    
                    ⚠️ *Simulated price changes are educational demo data.*
                """.trimIndent()
            }

            // 4. What is volatility?
            if (q.contains("volatilit") || q.contains("how volatile") || q.contains("swings") || q.contains("beta")) {
                val formattedDayLow = "${inst.currency}%,.2f".format(inst.dayLow)
                val formattedDayHigh = "${inst.currency}%,.2f".format(inst.dayHigh)
                val spreadFormatted = "%.2f".format(Math.abs(inst.dayHigh - inst.dayLow))

                return """
                    ⚡ **Understanding Volatility in Financial Markets**
                    
                    **Volatility** measures the frequency and magnitude of price fluctuations of an asset over time.
                    
                    • **${inst.symbol}'s Beta:** **${inst.beta}**
                      - Beta = 1.0: Moves in tandem with the broader market index.
                      - Beta > 1.0: Higher volatility than the benchmark (amplified gains and deeper drawdowns).
                      - Beta < 1.0: Lower volatility than the benchmark (more defensive stability).
                    
                    • **Day Swing:** Low of $formattedDayLow to High of $formattedDayHigh ($spreadFormatted spread).
                    
                    • **Investor Takeaway:** High volatility implies higher uncertainty and risk, which requires disciplined position sizing and stop-loss awareness.
                    
                    ⚠️ *Simulated price oscillations help you practise emotional discipline without risking real savings.*
                """.trimIndent()
            }

            // 5. What risks should a beginner understand?
            if (q.contains("risk") || q.contains("danger") || q.contains("downside") || q.contains("lose money")) {
                return """
                    🛡️ **Key Financial Risks Every Beginner Must Understand**
                    
                    For **${inst.symbol}** (${inst.category.displayName}, Risk Profile: **${inst.riskProfile}**):
                    
                    1. **Market Risk (Systematic):** Macro events (inflation, interest rates, geopolitics) can pull down entire asset classes regardless of company quality.
                    2. **Business / Sector Risk:** Specific competition, margin compression, or regulatory changes affecting ${inst.category.displayName}.
                    3. **Liquidity Risk:** The difficulty of selling an asset quickly without accepting a significant price concession.
                    4. **Psychological Risk:** FOMO (Fear of Missing Out) and panic selling during temporary pullbacks.
                    
                    💡 **Stock1 Rule of Thumb:**
                    Never allocate all your virtual capital into a single instrument. Diversify across Equities, Indices, Commodities, and Cash.
                """.trimIndent()
            }

            // 6. What is the difference between a simulated movement and a verified market movement?
            if (q.contains("simulated") || q.contains("difference between") || q.contains("real market") || q.contains("real price") || q.contains("demo vs real")) {
                return """
                    ⚖️ **Simulated Movement vs Verified Real-Market Movement**
                    
                    | Feature | Stock1 Virtual Simulator | Real Financial Exchanges (NSE / BSE / NASDAQ) |
                    | :--- | :--- | :--- |
                    | **Capital** | ₹10,00,000 Simulated Virtual Money | Real Hard-Earned Savings & Bank Deposits |
                    | **Price Driver** | Algorithmic Mock Price Engine | Global Order Matching Engine (Supply vs Demand) |
                    | **Liquidity & Slippage** | Instantaneous simulated fills | Real order book depth, bid-ask spreads, and slippage |
                    | **Financial Liability** | Zero financial liability or losses | Real taxable gains, real monetary losses |
                    | **Purpose** | Safe education & confidence building | Capital allocation & real wealth building |
                    
                    ⚠️ **Key Takeaway:**
                    Simulated chart movements are designed solely to teach mechanics and market concepts. They are NOT evidence of actual market behaviour or future real-world stock prices.
                """.trimIndent()
            }

            // News context question
            if (q.contains("news") || q.contains("headline") || q.contains("catalyst") || q.contains("story")) {
                if (context.relevantNews.isNotEmpty()) {
                    val newsList = context.relevantNews.joinToString("\n\n") {
                        "📰 **${it.title}**\n*Source: ${it.sourceName} • ${it.publishedTimeFormatted}*\n${it.summary}"
                    }
                    return """
                        🗞️ **Recent Market News Context for ${inst.category.displayName}**
                        
                        $newsList
                        
                        🔍 **Educational Analysis:**
                        News headlines often trigger short-term sentiment shifts among market participants. As a beginner, learn to distinguish between transient market noise and fundamental long-term corporate performance.
                    """.trimIndent()
                }
                return "No verified, instrument-specific news is available in the current Stock1 context. I won't invent headlines or claim that a demo price movement was caused by news."
            }
        }

        // General Financial Knowledge Base
        return when {
            q.contains("p/e") || q.contains("pe ratio") || q.contains("price to earnings") ->
                SampleDataProvider.tutorKnowledgeBase["pe"] ?: ""

            q.contains("nifty") || q.contains("sensex") || q.contains("index") ->
                SampleDataProvider.tutorKnowledgeBase["nifty"] ?: ""

            q.contains("diversif") || q.contains("portfolio") || q.contains("allocation") ->
                SampleDataProvider.tutorKnowledgeBase["diversification"] ?: ""

            q.contains("etf") || q.contains("mutual fund") || q.contains("fund") ->
                SampleDataProvider.tutorKnowledgeBase["etf"] ?: ""

            q.contains("order") || q.contains("market order") || q.contains("limit") ->
                SampleDataProvider.tutorKnowledgeBase["order"] ?: ""

            q.contains("dividend") ->
                "💰 **What is a Dividend?**\n\nA dividend is a payment made by a corporation to its shareholders out of its profits.\n\n• **Dividend Yield:** Annual dividend per share divided by the stock's current price, expressed as a percentage.\n• **Role:** Provides steady recurring income alongside capital appreciation. Mature, cash-flow generative companies frequently distribute dividends."

            q.contains("gold") || q.contains("commodity") ->
                "🪙 **Commodities & Gold in a Portfolio**\n\nGold is historically treated as a store of value and safe-haven hedge against currency depreciation and geopolitical uncertainty.\n\n• **Investment Vehicles:** Sovereign Gold Bonds (SGBs), Gold ETFs (e.g. GOLDBEES), or physical coins/bars.\n• **Allocation:** Many conservative financial planners recommend allocating 5% to 15% of an investment portfolio to gold as a risk shock-absorber."

            else ->
                "👋 **Welcome to Stock1 AI Financial Tutor!**\n\nI am here to help you learn financial markets safely:\n\n• *\"What is this company or instrument?\"*\n• *\"What does this chart show?\"*\n• *\"What does a percentage price change mean?\"*\n• *\"What is volatility and beta?\"*\n• *\"What risks should a beginner understand?\"*\n• *\"What is the difference between simulated and real market data?\"*\n\n*Stock1 is an educational simulator. Responses do not constitute personalized investment advice.*"
        }
    }
}
