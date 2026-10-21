package com.example.data

import com.example.data.model.Instrument
import com.example.data.model.Lesson
import com.example.data.model.LessonSection
import com.example.data.model.MarketCategory
import com.example.data.model.QuizQuestion

object SampleDataProvider {

    // Helper to generate realistic chart curves
    private fun generateCurve(base: Double, volatility: Double, count: Int, trend: Double = 0.01): List<Double> {
        val points = mutableListOf<Double>()
        var current = base
        for (i in 0 until count) {
            val delta = (Math.sin(i * 0.45) * volatility * 0.7) +
                    (Math.cos(i * 0.25) * volatility * 0.5) +
                    (trend * i * (base * 0.002))
            val price = (current + delta).coerceAtLeast(base * 0.4)
            points.add(Math.round(price * 100.0) / 100.0)
            current = price
        }
        return points
    }

    val instruments: List<Instrument> by lazy {
        listOf(
            // 1. INDICES
            Instrument(
                id = "idx_nifty50",
                symbol = "NIFTY 50",
                name = "Nifty 50 Index (NSE)",
                category = MarketCategory.INDICES,
                currentPrice = 24850.30,
                currency = "₹",
                change = 182.40,
                changePercent = 0.74,
                sparkline = generateCurve(24700.0, 45.0, 15, 0.04),
                dayHigh = 24890.10,
                dayLow = 24690.40,
                fiftyTwoWeekHigh = 26277.35,
                fiftyTwoWeekLow = 18837.85,
                volume = "328.4M",
                marketCap = "₹185.4T",
                peRatio = 22.8,
                dividendYield = 1.28,
                beta = 1.0,
                description = "The flagship benchmark index on the National Stock Exchange of India (NSE), tracking the 50 largest and most actively traded Indian companies across 13 sectors.",
                whyItMatters = "NIFTY 50 represents the pulse of the Indian economy. Most domestic index mutual funds and ETFs mirror this basket.",
                riskProfile = "Moderate",
                exchange = "NSE",
                historicalData = mapOf(
                    "1D" to generateCurve(24700.0, 40.0, 24, 0.02),
                    "1W" to generateCurve(24500.0, 90.0, 24, 0.04),
                    "1M" to generateCurve(24200.0, 180.0, 24, 0.06),
                    "1Y" to generateCurve(21800.0, 420.0, 24, 0.15),
                    "ALL" to generateCurve(11500.0, 950.0, 24, 0.35)
                )
            ),
            Instrument(
                id = "idx_sensex",
                symbol = "SENSEX",
                name = "BSE SENSEX 30",
                category = MarketCategory.INDICES,
                currentPrice = 81385.60,
                currency = "₹",
                change = 542.20,
                changePercent = 0.67,
                sparkline = generateCurve(80900.0, 140.0, 15, 0.03),
                dayHigh = 81520.00,
                dayLow = 80850.30,
                fiftyTwoWeekHigh = 85978.25,
                fiftyTwoWeekLow = 63183.77,
                volume = "21.5M",
                marketCap = "₹162.1T",
                peRatio = 23.4,
                dividendYield = 1.15,
                beta = 0.98,
                description = "India's oldest stock index on the Bombay Stock Exchange (BSE), comprising 30 financially sound blue-chip companies.",
                whyItMatters = "SENSEX is widely cited globally as the primary barometer of the Indian equity markets.",
                riskProfile = "Moderate",
                exchange = "BSE",
                historicalData = mapOf(
                    "1D" to generateCurve(80900.0, 120.0, 24, 0.02),
                    "1W" to generateCurve(80400.0, 250.0, 24, 0.04),
                    "1M" to generateCurve(79500.0, 500.0, 24, 0.05),
                    "1Y" to generateCurve(72000.0, 1200.0, 24, 0.14),
                    "ALL" to generateCurve(38000.0, 2500.0, 24, 0.32)
                )
            ),
            Instrument(
                id = "idx_banknifty",
                symbol = "BANK NIFTY",
                name = "Nifty Bank Index",
                category = MarketCategory.INDICES,
                currentPrice = 51240.80,
                currency = "₹",
                change = -145.50,
                changePercent = -0.28,
                sparkline = generateCurve(51400.0, 120.0, 15, -0.02),
                dayHigh = 51520.40,
                dayLow = 51080.20,
                fiftyTwoWeekHigh = 54467.35,
                fiftyTwoWeekLow = 42105.40,
                volume = "165.2M",
                marketCap = "₹48.9T",
                peRatio = 16.2,
                dividendYield = 0.95,
                beta = 1.25,
                description = "Sectoral index comprising the 12 most liquid and large-capitalised Indian banking stocks from both public and private sectors.",
                whyItMatters = "Banking is the financial backbone of credit growth in India; high volatility makes it popular with traders.",
                riskProfile = "High",
                exchange = "NSE",
                historicalData = mapOf(
                    "1D" to generateCurve(51400.0, 110.0, 24, -0.01),
                    "1W" to generateCurve(51800.0, 220.0, 24, -0.02),
                    "1M" to generateCurve(50500.0, 480.0, 24, 0.03),
                    "1Y" to generateCurve(44000.0, 1100.0, 24, 0.12),
                    "ALL" to generateCurve(22000.0, 2100.0, 24, 0.28)
                )
            ),

            // 2. INDIAN STOCKS
            Instrument(
                id = "stk_reliance",
                symbol = "RELIANCE",
                name = "Reliance Industries Ltd.",
                category = MarketCategory.INDIAN_STOCKS,
                currentPrice = 2948.75,
                currency = "₹",
                change = 38.25,
                changePercent = 1.31,
                sparkline = generateCurve(2915.0, 8.5, 15, 0.03),
                dayHigh = 2962.00,
                dayLow = 2908.10,
                fiftyTwoWeekHigh = 3217.90,
                fiftyTwoWeekLow = 2221.05,
                volume = "6.4M",
                marketCap = "₹19.9T",
                peRatio = 27.6,
                dividendYield = 0.35,
                beta = 0.88,
                description = "India's highest market-cap conglomerate with leadership across petrochemicals, retail (Reliance Retail), and telecom (Jio).",
                whyItMatters = "Heavyweight in NIFTY 50 (~9.5% weight); its movement strongly steers overall market sentiment.",
                riskProfile = "Moderate",
                exchange = "NSE",
                historicalData = mapOf(
                    "1D" to generateCurve(2915.0, 7.0, 24, 0.02),
                    "1W" to generateCurve(2880.0, 18.0, 24, 0.03),
                    "1M" to generateCurve(2820.0, 35.0, 24, 0.05),
                    "1Y" to generateCurve(2350.0, 95.0, 24, 0.18),
                    "ALL" to generateCurve(1200.0, 240.0, 24, 0.38)
                )
            ),
            Instrument(
                id = "stk_tcs",
                symbol = "TCS",
                name = "Tata Consultancy Services",
                category = MarketCategory.INDIAN_STOCKS,
                currentPrice = 4142.10,
                currency = "₹",
                change = -18.60,
                changePercent = -0.45,
                sparkline = generateCurve(4165.0, 12.0, 15, -0.01),
                dayHigh = 4180.00,
                dayLow = 4125.50,
                fiftyTwoWeekHigh = 4585.90,
                fiftyTwoWeekLow = 3313.00,
                volume = "2.1M",
                marketCap = "₹15.0T",
                peRatio = 31.2,
                dividendYield = 1.45,
                beta = 0.65,
                description = "Global IT services, consulting and business solutions leader, flagship cash generator of the Tata Group.",
                whyItMatters = "Known for strong free cash flow, high ROCE, consistent dividends, and defensive portfolio characteristics.",
                riskProfile = "Low",
                exchange = "NSE",
                historicalData = mapOf(
                    "1D" to generateCurve(4165.0, 10.0, 24, -0.01),
                    "1W" to generateCurve(4200.0, 25.0, 24, -0.02),
                    "1M" to generateCurve(4100.0, 50.0, 24, 0.02),
                    "1Y" to generateCurve(3500.0, 120.0, 24, 0.16),
                    "ALL" to generateCurve(1900.0, 300.0, 24, 0.32)
                )
            ),
            Instrument(
                id = "stk_hdfcbank",
                symbol = "HDFCBANK",
                name = "HDFC Bank Ltd.",
                category = MarketCategory.INDIAN_STOCKS,
                currentPrice = 1684.50,
                currency = "₹",
                change = 22.30,
                changePercent = 1.34,
                sparkline = generateCurve(1665.0, 5.5, 15, 0.03),
                dayHigh = 1692.00,
                dayLow = 1658.00,
                fiftyTwoWeekHigh = 1794.00,
                fiftyTwoWeekLow = 1363.55,
                volume = "14.2M",
                marketCap = "₹12.8T",
                peRatio = 19.1,
                dividendYield = 1.18,
                beta = 0.95,
                description = "India's largest private sector bank following its mega-merger with housing parent HDFC Ltd.",
                whyItMatters = "Top individual weight in both NIFTY 50 and BANK NIFTY; considered a premier banking franchise.",
                riskProfile = "Moderate",
                exchange = "NSE",
                historicalData = mapOf(
                    "1D" to generateCurve(1665.0, 4.0, 24, 0.02),
                    "1W" to generateCurve(1640.0, 12.0, 24, 0.03),
                    "1M" to generateCurve(1610.0, 24.0, 24, 0.04),
                    "1Y" to generateCurve(1490.0, 60.0, 24, 0.10),
                    "ALL" to generateCurve(980.0, 150.0, 24, 0.25)
                )
            ),
            Instrument(
                id = "stk_infy",
                symbol = "INFY",
                name = "Infosys Limited",
                category = MarketCategory.INDIAN_STOCKS,
                currentPrice = 1892.40,
                currency = "₹",
                change = 14.80,
                changePercent = 0.79,
                sparkline = generateCurve(1880.0, 6.0, 15, 0.02),
                dayHigh = 1905.00,
                dayLow = 1872.20,
                fiftyTwoWeekHigh = 1991.45,
                fiftyTwoWeekLow = 1358.35,
                volume = "5.8M",
                marketCap = "₹7.8T",
                peRatio = 29.5,
                dividendYield = 1.85,
                beta = 0.82,
                description = "Pioneer in Indian software exports providing next-generation digital services and consulting.",
                whyItMatters = "Sensitive to US and European corporate tech spending and interest rate cycles.",
                riskProfile = "Moderate",
                exchange = "NSE",
                historicalData = mapOf(
                    "1D" to generateCurve(1880.0, 5.0, 24, 0.01),
                    "1W" to generateCurve(1860.0, 14.0, 24, 0.02),
                    "1M" to generateCurve(1820.0, 30.0, 24, 0.04),
                    "1Y" to generateCurve(1450.0, 80.0, 24, 0.22),
                    "ALL" to generateCurve(820.0, 180.0, 24, 0.35)
                )
            ),
            Instrument(
                id = "stk_tatamotors",
                symbol = "TATAMOTORS",
                name = "Tata Motors Ltd.",
                category = MarketCategory.INDIAN_STOCKS,
                currentPrice = 964.80,
                currency = "₹",
                change = -8.40,
                changePercent = -0.86,
                sparkline = generateCurve(975.0, 4.5, 15, -0.02),
                dayHigh = 978.50,
                dayLow = 958.00,
                fiftyTwoWeekHigh = 1179.05,
                fiftyTwoWeekLow = 621.50,
                volume = "8.9M",
                marketCap = "₹3.5T",
                peRatio = 10.4,
                dividendYield = 0.62,
                beta = 1.42,
                description = "Leading automobile manufacturer across passenger cars, commercial vehicles, EVs in India, and Jaguar Land Rover globally.",
                whyItMatters = "Turnaround story driven by EV leadership in India and strong JLR margins in global markets.",
                riskProfile = "High",
                exchange = "NSE",
                historicalData = mapOf(
                    "1D" to generateCurve(975.0, 4.0, 24, -0.01),
                    "1W" to generateCurve(990.0, 10.0, 24, -0.02),
                    "1M" to generateCurve(950.0, 25.0, 24, 0.02),
                    "1Y" to generateCurve(680.0, 65.0, 24, 0.32),
                    "ALL" to generateCurve(310.0, 120.0, 24, 0.50)
                )
            ),

            // 3. GLOBAL STOCKS
            Instrument(
                id = "stk_apple",
                symbol = "AAPL",
                name = "Apple Inc.",
                category = MarketCategory.GLOBAL_STOCKS,
                currentPrice = 227.65,
                currency = "$",
                change = 3.15,
                changePercent = 1.40,
                sparkline = generateCurve(224.0, 0.9, 15, 0.03),
                dayHigh = 228.80,
                dayLow = 224.50,
                fiftyTwoWeekHigh = 237.23,
                fiftyTwoWeekLow = 164.08,
                volume = "48.2M",
                marketCap = "$3.48T",
                peRatio = 34.2,
                dividendYield = 0.44,
                beta = 0.85,
                description = "World's leading consumer tech giant with iconic hardware (iPhone, Mac), Services ecosystem, and Apple Intelligence.",
                whyItMatters = "Highest consumer brand loyalty, massive share buybacks, and cornerstone of Berkshire Hathaway's portfolio.",
                riskProfile = "Moderate",
                exchange = "NASDAQ",
                historicalData = mapOf(
                    "1D" to generateCurve(224.0, 0.8, 24, 0.02),
                    "1W" to generateCurve(220.0, 2.2, 24, 0.03),
                    "1M" to generateCurve(215.0, 4.5, 24, 0.05),
                    "1Y" to generateCurve(175.0, 12.0, 24, 0.22),
                    "ALL" to generateCurve(95.0, 32.0, 24, 0.45)
                )
            ),
            Instrument(
                id = "stk_nvidia",
                symbol = "NVDA",
                name = "NVIDIA Corporation",
                category = MarketCategory.GLOBAL_STOCKS,
                currentPrice = 138.20,
                currency = "$",
                change = 4.80,
                changePercent = 3.60,
                sparkline = generateCurve(133.0, 1.2, 15, 0.05),
                dayHigh = 139.50,
                dayLow = 132.80,
                fiftyTwoWeekHigh = 140.76,
                fiftyTwoWeekLow = 39.23,
                volume = "74.1M",
                marketCap = "$3.39T",
                peRatio = 54.8,
                dividendYield = 0.03,
                beta = 1.68,
                description = "Pioneer in GPU hardware, CUDA compute software stack, and enterprise AI accelerators driving the generative AI revolution.",
                whyItMatters = "Primary beneficiary of global data center AI buildouts by Microsoft, Google, Meta, and Amazon.",
                riskProfile = "High",
                exchange = "NASDAQ",
                historicalData = mapOf(
                    "1D" to generateCurve(133.0, 1.1, 24, 0.04),
                    "1W" to generateCurve(128.0, 3.5, 24, 0.06),
                    "1M" to generateCurve(118.0, 7.0, 24, 0.12),
                    "1Y" to generateCurve(45.0, 22.0, 24, 0.65),
                    "ALL" to generateCurve(15.0, 40.0, 24, 0.90)
                )
            ),
            Instrument(
                id = "stk_microsoft",
                symbol = "MSFT",
                name = "Microsoft Corporation",
                category = MarketCategory.GLOBAL_STOCKS,
                currentPrice = 418.50,
                currency = "$",
                change = 2.10,
                changePercent = 0.50,
                sparkline = generateCurve(416.0, 1.4, 15, 0.01),
                dayHigh = 421.00,
                dayLow = 415.20,
                fiftyTwoWeekHigh = 468.35,
                fiftyTwoWeekLow = 327.00,
                volume = "18.3M",
                marketCap = "$3.11T",
                peRatio = 32.8,
                dividendYield = 0.72,
                beta = 0.90,
                description = "Enterprise cloud computing (Azure), enterprise productivity software (Office 365), and major investor in OpenAI.",
                whyItMatters = "Diversified, defensive enterprise tech with durable subscription cash flow.",
                riskProfile = "Low",
                exchange = "NASDAQ",
                historicalData = mapOf(
                    "1D" to generateCurve(416.0, 1.2, 24, 0.01),
                    "1W" to generateCurve(412.0, 3.8, 24, 0.02),
                    "1M" to generateCurve(405.0, 8.0, 24, 0.04),
                    "1Y" to generateCurve(330.0, 25.0, 24, 0.20),
                    "ALL" to generateCurve(160.0, 60.0, 24, 0.42)
                )
            ),

            // 4. ETFS
            Instrument(
                id = "etf_niftybees",
                symbol = "NIFTYBEES",
                name = "Nippon India ETF Nifty 50 BeES",
                category = MarketCategory.ETFS,
                currentPrice = 271.85,
                currency = "₹",
                change = 1.95,
                changePercent = 0.72,
                sparkline = generateCurve(270.0, 0.6, 15, 0.02),
                dayHigh = 272.50,
                dayLow = 269.80,
                fiftyTwoWeekHigh = 286.40,
                fiftyTwoWeekLow = 205.20,
                volume = "12.4M",
                marketCap = "₹28.5B",
                peRatio = 22.8,
                dividendYield = 1.20,
                beta = 1.0,
                description = "India's most liquid exchange-traded fund replicating the Nifty 50 Index. Trades like a stock in 1/100th unit values.",
                whyItMatters = "Low expense ratio (~0.04%) and high liquidity make it the #1 choice for passive core Indian equity allocation.",
                riskProfile = "Moderate",
                exchange = "NSE",
                historicalData = mapOf(
                    "1D" to generateCurve(270.0, 0.5, 24, 0.02),
                    "1W" to generateCurve(268.0, 1.2, 24, 0.03),
                    "1M" to generateCurve(264.0, 2.5, 24, 0.05),
                    "1Y" to generateCurve(220.0, 8.0, 24, 0.16),
                    "ALL" to generateCurve(110.0, 22.0, 24, 0.35)
                )
            ),
            Instrument(
                id = "etf_goldbees",
                symbol = "GOLDBEES",
                name = "Nippon India ETF Gold BeES",
                category = MarketCategory.ETFS,
                currentPrice = 68.45,
                currency = "₹",
                change = 0.55,
                changePercent = 0.81,
                sparkline = generateCurve(67.9, 0.2, 15, 0.03),
                dayHigh = 68.70,
                dayLow = 67.80,
                fiftyTwoWeekHigh = 70.15,
                fiftyTwoWeekLow = 51.20,
                volume = "7.8M",
                marketCap = "₹14.2B",
                peRatio = null,
                dividendYield = 0.0,
                beta = 0.12,
                description = "Physical gold-backed ETF where each unit represents approximately 0.01 gram of 99.5% pure physical gold bullion.",
                whyItMatters = "Convenient hedge against inflation and currency depreciation without physical locker or making-charge costs.",
                riskProfile = "Low",
                exchange = "NSE",
                historicalData = mapOf(
                    "1D" to generateCurve(67.9, 0.15, 24, 0.02),
                    "1W" to generateCurve(67.2, 0.4, 24, 0.03),
                    "1M" to generateCurve(65.5, 0.9, 24, 0.05),
                    "1Y" to generateCurve(54.0, 2.8, 24, 0.22),
                    "ALL" to generateCurve(35.0, 6.5, 24, 0.30)
                )
            ),

            // 5. MUTUAL FUNDS
            Instrument(
                id = "mf_paragparikh",
                symbol = "PPFAS-FC",
                name = "Parag Parikh Flexi Cap Fund",
                category = MarketCategory.MUTUAL_FUNDS,
                currentPrice = 82.64,
                currency = "₹",
                change = 0.42,
                changePercent = 0.51,
                sparkline = generateCurve(82.2, 0.15, 15, 0.02),
                dayHigh = 82.64,
                dayLow = 82.20,
                fiftyTwoWeekHigh = 84.10,
                fiftyTwoWeekLow = 61.50,
                volume = "N/A (NAV)",
                marketCap = "₹78.4B (AUM)",
                peRatio = 24.1,
                dividendYield = null,
                beta = 0.74,
                description = "Active equity mutual fund investing across large, mid, and small cap companies with selective exposure to international tech leaders.",
                whyItMatters = "Follows disciplined value investing principles with strong downside protection in bear markets.",
                riskProfile = "Moderate",
                exchange = "AMFI",
                historicalData = mapOf(
                    "1D" to generateCurve(82.2, 0.1, 24, 0.01),
                    "1W" to generateCurve(81.8, 0.3, 24, 0.02),
                    "1M" to generateCurve(80.5, 0.8, 24, 0.04),
                    "1Y" to generateCurve(68.0, 2.5, 24, 0.18),
                    "ALL" to generateCurve(32.0, 7.5, 24, 0.38)
                )
            ),

            // 6. COMMODITIES
            Instrument(
                id = "cmd_gold",
                symbol = "GOLD",
                name = "Gold 24K (MCX 10g)",
                category = MarketCategory.COMMODITIES,
                currentPrice = 77850.00,
                currency = "₹",
                change = 620.00,
                changePercent = 0.80,
                sparkline = generateCurve(77200.0, 160.0, 15, 0.03),
                dayHigh = 78100.00,
                dayLow = 77150.00,
                fiftyTwoWeekHigh = 78900.00,
                fiftyTwoWeekLow = 59800.00,
                volume = "42.8K lots",
                marketCap = "$16.8T (Global)",
                peRatio = null,
                dividendYield = 0.0,
                beta = 0.08,
                description = "Benchmark 999 purity gold contract traded on the Multi Commodity Exchange of India (MCX).",
                whyItMatters = "Historically proven store of value during geopolitical turbulence and fiat currency debasement.",
                riskProfile = "Low",
                exchange = "MCX",
                historicalData = mapOf(
                    "1D" to generateCurve(77200.0, 120.0, 24, 0.02),
                    "1W" to generateCurve(76800.0, 320.0, 24, 0.03),
                    "1M" to generateCurve(75200.0, 750.0, 24, 0.05),
                    "1Y" to generateCurve(61500.0, 2200.0, 24, 0.24),
                    "ALL" to generateCurve(39000.0, 4800.0, 24, 0.35)
                )
            ),
            Instrument(
                id = "cmd_silver",
                symbol = "SILVER",
                name = "Silver (MCX 1kg)",
                category = MarketCategory.COMMODITIES,
                currentPrice = 92450.00,
                currency = "₹",
                change = 1120.00,
                changePercent = 1.23,
                sparkline = generateCurve(91300.0, 280.0, 15, 0.04),
                dayHigh = 92900.00,
                dayLow = 91100.00,
                fiftyTwoWeekHigh = 96490.00,
                fiftyTwoWeekLow = 68500.00,
                volume = "38.1K lots",
                marketCap = "$1.8T (Global)",
                peRatio = null,
                dividendYield = 0.0,
                beta = 0.35,
                description = "Precious and industrial metal vital for solar photovoltaic cells, electrical contacts, and electronics manufacturing.",
                whyItMatters = "Higher beta than gold due to dual demand as both precious monetary metal and green transition commodity.",
                riskProfile = "Moderate",
                exchange = "MCX",
                historicalData = mapOf(
                    "1D" to generateCurve(91300.0, 250.0, 24, 0.03),
                    "1W" to generateCurve(90200.0, 600.0, 24, 0.04),
                    "1M" to generateCurve(88000.0, 1400.0, 24, 0.06),
                    "1Y" to generateCurve(71000.0, 3800.0, 24, 0.25),
                    "ALL" to generateCurve(42000.0, 7500.0, 24, 0.38)
                )
            ),
            Instrument(
                id = "cmd_crude",
                symbol = "CRUDEOIL",
                name = "Crude Oil Brent (Barrel)",
                category = MarketCategory.COMMODITIES,
                currentPrice = 6210.00,
                currency = "₹",
                change = -85.00,
                changePercent = -1.35,
                sparkline = generateCurve(6300.0, 22.0, 15, -0.03),
                dayHigh = 6340.00,
                dayLow = 6180.00,
                fiftyTwoWeekHigh = 7450.00,
                fiftyTwoWeekLow = 5720.00,
                volume = "82.5K lots",
                marketCap = "N/A",
                peRatio = null,
                dividendYield = 0.0,
                beta = 0.88,
                description = "Global energy commodity benchmark reflecting international supply/demand, OPEC+ quotas, and macroeconomic activity.",
                whyItMatters = "India imports over 85% of its crude oil needs; oil spikes directly impact domestic inflation and the Indian Rupee.",
                riskProfile = "High",
                exchange = "MCX",
                historicalData = mapOf(
                    "1D" to generateCurve(6300.0, 18.0, 24, -0.02),
                    "1W" to generateCurve(6420.0, 45.0, 24, -0.03),
                    "1M" to generateCurve(6280.0, 110.0, 24, -0.01),
                    "1Y" to generateCurve(6800.0, 280.0, 24, -0.08),
                    "ALL" to generateCurve(4500.0, 620.0, 24, 0.20)
                )
            ),

            // 7. FOREX
            Instrument(
                id = "fx_usdinr",
                symbol = "USD/INR",
                name = "US Dollar / Indian Rupee",
                category = MarketCategory.FOREX,
                currentPrice = 84.08,
                currency = "₹",
                change = 0.06,
                changePercent = 0.07,
                sparkline = generateCurve(84.02, 0.02, 15, 0.01),
                dayHigh = 84.12,
                dayLow = 84.01,
                fiftyTwoWeekHigh = 84.22,
                fiftyTwoWeekLow = 82.95,
                volume = "$6.2B",
                marketCap = "N/A",
                peRatio = null,
                dividendYield = null,
                beta = 0.15,
                description = "Exchange rate representing the price in Indian Rupees needed to buy one US Dollar. Heavily monitored by the RBI.",
                whyItMatters = "Impacts importers, IT sector earnings, overseas study costs, and foreign portfolio investment flows.",
                riskProfile = "Low",
                exchange = "FX Spot",
                historicalData = mapOf(
                    "1D" to generateCurve(84.02, 0.015, 24, 0.01),
                    "1W" to generateCurve(83.95, 0.04, 24, 0.02),
                    "1M" to generateCurve(83.82, 0.08, 24, 0.03),
                    "1Y" to generateCurve(83.15, 0.25, 24, 0.08),
                    "ALL" to generateCurve(68.50, 0.90, 24, 0.20)
                )
            ),
            Instrument(
                id = "fx_eurinr",
                symbol = "EUR/INR",
                name = "Euro / Indian Rupee",
                category = MarketCategory.FOREX,
                currentPrice = 91.42,
                currency = "₹",
                change = -0.22,
                changePercent = -0.24,
                sparkline = generateCurve(91.65, 0.06, 15, -0.01),
                dayHigh = 91.75,
                dayLow = 91.30,
                fiftyTwoWeekHigh = 93.45,
                fiftyTwoWeekLow = 88.20,
                volume = "€1.8B",
                marketCap = "N/A",
                peRatio = null,
                dividendYield = null,
                beta = 0.28,
                description = "Cross-currency exchange pair between the single European currency (Euro) and the Indian Rupee.",
                whyItMatters = "Crucial for European trade pacts, machinery imports, and engineering consulting businesses.",
                riskProfile = "Moderate",
                exchange = "FX Spot",
                historicalData = mapOf(
                    "1D" to generateCurve(91.65, 0.05, 24, -0.01),
                    "1W" to generateCurve(91.90, 0.12, 24, -0.02),
                    "1M" to generateCurve(92.40, 0.30, 24, -0.02),
                    "1Y" to generateCurve(89.50, 0.85, 24, 0.04),
                    "ALL" to generateCurve(78.00, 2.20, 24, 0.14)
                )
            ),

            // 8. CRYPTOCURRENCY
            Instrument(
                id = "cr_btc",
                symbol = "BTC",
                name = "Bitcoin (BTC)",
                category = MarketCategory.CRYPTO,
                currentPrice = 68420.00,
                currency = "$",
                change = 1840.00,
                changePercent = 2.76,
                sparkline = generateCurve(66500.0, 480.0, 15, 0.04),
                dayHigh = 68950.00,
                dayLow = 66200.00,
                fiftyTwoWeekHigh = 73750.00,
                fiftyTwoWeekLow = 28400.00,
                volume = "$34.5B",
                marketCap = "$1.35T",
                peRatio = null,
                dividendYield = 0.0,
                beta = 2.45,
                description = "The world's first decentralized digital currency based on proof-of-work blockchain consensus, with a fixed supply cap of 21 million coins.",
                whyItMatters = "Viewed by proponents as digital gold and sovereign-free store of value; exhibits high price volatility.",
                riskProfile = "Very High",
                exchange = "Global Spot",
                historicalData = mapOf(
                    "1D" to generateCurve(66500.0, 350.0, 24, 0.03),
                    "1W" to generateCurve(64200.0, 950.0, 24, 0.05),
                    "1M" to generateCurve(61000.0, 2100.0, 24, 0.08),
                    "1Y" to generateCurve(34000.0, 6800.0, 24, 0.65),
                    "ALL" to generateCurve(9500.0, 12000.0, 24, 0.95)
                )
            ),
            Instrument(
                id = "cr_eth",
                symbol = "ETH",
                name = "Ethereum (ETH)",
                category = MarketCategory.CRYPTO,
                currentPrice = 2640.50,
                currency = "$",
                change = 52.80,
                changePercent = 2.04,
                sparkline = generateCurve(2580.0, 22.0, 15, 0.03),
                dayHigh = 2665.00,
                dayLow = 2560.00,
                fiftyTwoWeekHigh = 4093.80,
                fiftyTwoWeekLow = 1520.00,
                volume = "$18.2B",
                marketCap = "$318B",
                peRatio = null,
                dividendYield = 3.20, // Staking yield
                beta = 2.70,
                description = "Decentralized programmable smart contract blockchain platform powering DeFi protocols, tokenization, and Web3 infrastructure.",
                whyItMatters = "The settlement layer for majority of decentralized finance; generates staking rewards for validators.",
                riskProfile = "Very High",
                exchange = "Global Spot",
                historicalData = mapOf(
                    "1D" to generateCurve(2580.0, 18.0, 24, 0.02),
                    "1W" to generateCurve(2520.0, 48.0, 24, 0.04),
                    "1M" to generateCurve(2410.0, 95.0, 24, 0.06),
                    "1Y" to generateCurve(1750.0, 320.0, 24, 0.42),
                    "ALL" to generateCurve(450.0, 650.0, 24, 0.75)
                )
            ),

            // 9. BONDS & FIXED INCOME
            Instrument(
                id = "bnd_goi10y",
                symbol = "GOI-10Y",
                name = "Government of India 10Y Benchmark",
                category = MarketCategory.BONDS,
                currentPrice = 100.85,
                currency = "₹",
                change = -0.12,
                changePercent = -0.12,
                sparkline = generateCurve(100.95, 0.04, 15, -0.01),
                dayHigh = 101.05,
                dayLow = 100.75,
                fiftyTwoWeekHigh = 102.30,
                fiftyTwoWeekLow = 98.40,
                volume = "₹4,200 Cr",
                marketCap = "Sovereign Debt",
                peRatio = null,
                dividendYield = 6.82, // Yield to maturity
                beta = 0.05,
                description = "10-Year Indian Government Sovereign Bond. The yield on this bond represents the risk-free rate of return in India.",
                whyItMatters = "Benchmark across corporate borrowing rates, home loan interest rates, and equity valuation discount rates.",
                riskProfile = "Low",
                exchange = "RBI NDS-OM",
                historicalData = mapOf(
                    "1D" to generateCurve(100.95, 0.03, 24, -0.01),
                    "1W" to generateCurve(101.10, 0.08, 24, -0.02),
                    "1M" to generateCurve(100.60, 0.15, 24, 0.01),
                    "1Y" to generateCurve(99.20, 0.45, 24, 0.05),
                    "ALL" to generateCurve(96.00, 1.20, 24, 0.12)
                )
            ),
            Instrument(
                id = "bnd_sgb",
                symbol = "SGB-2031",
                name = "Sovereign Gold Bond Series",
                category = MarketCategory.BONDS,
                currentPrice = 7640.00,
                currency = "₹",
                change = 55.00,
                changePercent = 0.73,
                sparkline = generateCurve(7585.0, 14.0, 15, 0.02),
                dayHigh = 7670.00,
                dayLow = 7580.00,
                fiftyTwoWeekHigh = 7780.00,
                fiftyTwoWeekLow = 6010.00,
                volume = "125K units",
                marketCap = "RBI Guaranteed",
                peRatio = null,
                dividendYield = 2.50, // 2.5% semi-annual interest
                beta = 0.10,
                description = "Government security denominated in grams of gold issued by the RBI, paying 2.50% annual interest on initial value plus gold appreciation.",
                whyItMatters = "Tax-free capital gains on maturity with guaranteed sovereign backing and passive semi-annual coupon payouts.",
                riskProfile = "Low",
                exchange = "NSE Debt",
                historicalData = mapOf(
                    "1D" to generateCurve(7585.0, 12.0, 24, 0.02),
                    "1W" to generateCurve(7520.0, 30.0, 24, 0.03),
                    "1M" to generateCurve(7400.0, 75.0, 24, 0.05),
                    "1Y" to generateCurve(6100.0, 220.0, 24, 0.22),
                    "ALL" to generateCurve(4500.0, 480.0, 24, 0.35)
                )
            )
        )
    }

    // 14 Comprehensive Lessons
    val lessons: List<Lesson> by lazy {
        listOf(
            Lesson(
                id = "les_1",
                title = "What is the Stock Market?",
                category = "Foundations",
                readTimeMinutes = 3,
                summary = "Discover how public stock markets connect ambitious companies with everyday investors.",
                sections = listOf(
                    LessonSection(
                        heading = "The Marketplace for Business Ownership",
                        text = "The stock market is a public venue where buyers and sellers gather to trade shares of publicly listed companies. Just like a farmers market where agricultural goods are bought and sold, a stock exchange facilitates transparent price discovery for fractional ownership of corporations.",
                        example = "If a bakery company is divided into 1,000 equal shares and you own 10 shares, you own exactly 1% of the company's assets and future profit distribution."
                    ),
                    LessonSection(
                        heading = "Why Do Companies Sell Shares?",
                        text = "Businesses need capital to build factories, hire engineering talent, launch products, or expand to new countries. Rather than taking heavy debt with bank interest, a company can issue shares to the public in an Initial Public Offering (IPO). In exchange, investors receive equity ownership.",
                        example = "When Zomato or Tata Technologies launched their IPOs, thousands of retail and institutional investors contributed money to become fractional owners."
                    )
                ),
                quiz = listOf(
                    QuizQuestion(
                        id = "q1_1",
                        question = "What does purchasing a stock primarily give an investor?",
                        options = listOf(
                            "Fractional ownership in the company",
                            "A guaranteed interest rate",
                            "Employment at the firm",
                            "A physical product voucher"
                        ),
                        correctIndex = 0,
                        explanation = "Buying a share represents fractional ownership of the corporation, entitling the shareholder to vote on board matters and receive dividends if distributed."
                    ),
                    QuizQuestion(
                        id = "q1_2",
                        question = "What is the primary reason companies go public via an IPO?",
                        options = listOf(
                            "To raise capital for expansion and operations",
                            "To close down their business",
                            "To avoid paying employee salaries",
                            "To guarantee profits without working"
                        ),
                        correctIndex = 0,
                        explanation = "An Initial Public Offering allows a company to raise non-debt capital from public investors to fund new growth and expansion."
                    )
                )
            ),
            Lesson(
                id = "les_2",
                title = "Understanding NIFTY 50 and SENSEX",
                category = "Foundations",
                readTimeMinutes = 4,
                summary = "Master how benchmark indices measure the health of the broader stock market.",
                sections = listOf(
                    LessonSection(
                        heading = "What is a Stock Market Index?",
                        text = "Tracking thousands of individual stocks simultaneously is impossible. An index acts as a representative sample or basket that measures the collective movement and financial temperature of the overall market or specific sectors.",
                        example = "Instead of examining 5,000 listed Indian stocks individually, investors check NIFTY 50 to instantly gauge whether Indian equities are trending up or down."
                    ),
                    LessonSection(
                        heading = "NIFTY 50 vs SENSEX",
                        text = "The NIFTY 50 is run by the National Stock Exchange (NSE) and tracks 50 of the largest, most liquid Indian companies weighted by free-float market capitalisation. The SENSEX is managed by the Bombay Stock Exchange (BSE) and comprises 30 prominent blue-chip companies.",
                        example = "When financial news reports 'The market gained 1% today', they almost always refer to the NIFTY 50 or SENSEX index percentage change."
                    )
                ),
                quiz = listOf(
                    QuizQuestion(
                        id = "q2_1",
                        question = "How many blue-chip companies constitute the BSE SENSEX index?",
                        options = listOf("30 companies", "50 companies", "100 companies", "500 companies"),
                        correctIndex = 0,
                        explanation = "The BSE SENSEX is composed of 30 well-established, financially sound companies listed on the Bombay Stock Exchange."
                    )
                )
            ),
            Lesson(
                id = "les_3",
                title = "Candlestick Charts & Price Action",
                category = "Technical Analysis",
                readTimeMinutes = 4,
                summary = "Learn how to read candlestick bars to understand buyers and sellers sentiment.",
                sections = listOf(
                    LessonSection(
                        heading = "The Four Data Points in Every Candle",
                        text = "Invented by 18th-century Japanese rice traders, a candlestick summarizes price behavior across a set time frame (e.g., 5 minutes, 1 day) using four metrics: Open, High, Low, and Close (OHLC). The thick colored body represents the distance between Open and Close, while the thin wicks show the extreme High and Low reached.",
                        example = "If a stock opens at ₹100, drops to ₹95, surges to ₹115, and closes at ₹112, the green body spans ₹100-₹112 with upper wick to ₹115 and lower wick to ₹95."
                    ),
                    LessonSection(
                        heading = "Green vs Red Candles",
                        text = "A green (or white) candle means buyers were in control: the Close was higher than the Open. A red candle means sellers dominated: the Close finished lower than the Open.",
                        example = "Long upper wicks indicate that bulls tried to push prices up, but sellers rejected higher levels and pushed price back down before close."
                    )
                ),
                quiz = listOf(
                    QuizQuestion(
                        id = "q3_1",
                        question = "What does a green candlestick indicate?",
                        options = listOf(
                            "The closing price was higher than the opening price",
                            "The stock price never moved",
                            "The market was closed that day",
                            "The company declared bankruptcy"
                        ),
                        correctIndex = 0,
                        explanation = "When the closing price is higher than the opening price during that period, the candlestick body is colored green."
                    )
                )
            ),
            Lesson(
                id = "les_4",
                title = "Market Orders vs Limit Orders",
                category = "Trading Mechanics",
                readTimeMinutes = 3,
                summary = "Understand order types so you never suffer unexpected execution prices.",
                sections = listOf(
                    LessonSection(
                        heading = "Market Order: Guaranteed Execution",
                        text = "A market order instructs your broker to execute immediately at the best currently available market price. Execution speed is guaranteed, but the exact execution price is not guaranteed, especially during volatile openings.",
                        example = "Placing a market order for 10 shares of Reliance will fill instantly against the lowest asking price in the order book."
                    ),
                    LessonSection(
                        heading = "Limit Order: Guaranteed Price",
                        text = "A limit order specifies the maximum price you are willing to pay when buying, or the minimum price you accept when selling. Your order will only execute if the market reaches your specified price or better.",
                        example = "If a stock trades at ₹1,050, you can place a buy limit order at ₹1,020. Your order waits in the order book until prices dip to ₹1,020."
                    )
                ),
                quiz = listOf(
                    QuizQuestion(
                        id = "q4_1",
                        question = "What is guaranteed when placing a Limit Order?",
                        options = listOf(
                            "The price will be at your limit or better",
                            "Instant execution within 1 second",
                            "Guaranteed profit within a month",
                            "Zero transaction fees"
                        ),
                        correctIndex = 0,
                        explanation = "Limit orders guarantee the transaction will only execute at your target price or better; however, if the market never touches your price, the order won't fill."
                    )
                )
            ),
            Lesson(
                id = "les_5",
                title = "Risk Management & Diversification",
                category = "Strategy",
                readTimeMinutes = 4,
                summary = "The timeless principle: Never put all your eggs in one corporate basket.",
                sections = listOf(
                    LessonSection(
                        heading = "The Principle of Diversification",
                        text = "Diversification is the practice of spreading investments across different companies, industry sectors, asset classes (equities, bonds, gold), and geographies to reduce portfolio risk without sacrificing expected returns.",
                        example = "If you invest 100% in an airline stock and fuel costs spike, your whole portfolio plummets. But if you also hold energy, IT, pharmaceuticals, and gold, losses in one sector are cushioned by gains in others."
                    ),
                    LessonSection(
                        heading = "Asset Allocation",
                        text = "Academic studies show that over 90% of long-term portfolio return variability comes from asset allocation (the ratio between stocks, bonds, and cash) rather than individual stock picking.",
                        example = "A classic balanced portfolio might hold 60% equities for capital appreciation, 30% bonds for stable income, and 10% gold/cash for liquidity."
                    )
                ),
                quiz = listOf(
                    QuizQuestion(
                        id = "q5_1",
                        question = "What is the primary benefit of portfolio diversification?",
                        options = listOf(
                            "Reducing unsystematic risk from individual company failures",
                            "Guaranteeing you will never experience a negative week",
                            "Eliminating all market taxation",
                            "Doubling your returns automatically"
                        ),
                        correctIndex = 0,
                        explanation = "Diversification dampens company-specific shocks so that an unexpected disaster in a single firm doesn't wipe out your hard-earned savings."
                    )
                )
            ),
            Lesson(
                id = "les_6",
                title = "ETFs vs Mutual Funds Explained",
                category = "Instruments",
                readTimeMinutes = 4,
                summary = "Compare low-cost passive index tracking against active fund manager expertise.",
                sections = listOf(
                    LessonSection(
                        heading = "Exchange-Traded Funds (ETFs)",
                        text = "An ETF is a pooled investment vehicle that trades on stock exchanges throughout the trading day just like regular shares. Most ETFs passively track a recognized index (like NIFTY 50, NASDAQ 100, or Gold) with ultra-low expense ratios.",
                        example = "NIFTYBEES lets an investor hold all 50 top Indian companies in a single unit priced around ₹270 with an expense ratio under 0.05%."
                    ),
                    LessonSection(
                        heading = "Mutual Funds",
                        text = "Mutual funds are managed by professional Asset Management Companies (AMCs). Orders are priced once per day at the official Net Asset Value (NAV) after markets close. Active funds employ research analysts to pick stocks they hope will outperform the market.",
                        example = "Flexi-cap mutual funds give the fund manager discretion to allocate between large, mid, and small-cap stocks depending on market valuation cycles."
                    )
                ),
                quiz = listOf(
                    QuizQuestion(
                        id = "q6_1",
                        question = "How are ETFs traded compared to standard Mutual Funds?",
                        options = listOf(
                            "ETFs trade continuously throughout the day on stock exchanges",
                            "ETFs only trade on weekends",
                            "Mutual funds can be bought and sold every minute like stocks",
                            "ETFs require government approval for each trade"
                        ),
                        correctIndex = 0,
                        explanation = "ETFs trade in real time during exchange market hours with continuous price updates, whereas mutual funds settle at post-market closing NAV."
                    )
                )
            ),
            Lesson(
                id = "les_7",
                title = "Fundamental Analysis: P/E, Moats & Balance Sheets",
                category = "Analysis",
                readTimeMinutes = 5,
                summary = "Discover how to evaluate whether a company is fundamentally healthy and reasonably priced.",
                sections = listOf(
                    LessonSection(
                        heading = "The Price-to-Earnings (P/E) Ratio",
                        text = "The P/E ratio is calculated by dividing the current stock price by annual earnings per share (EPS). It indicates how many rupees or dollars investors are willing to pay for every rupee of company net profit.",
                        example = "A P/E of 25 means you are paying ₹25 for every ₹1 of annual earnings the company produces."
                    ),
                    LessonSection(
                        heading = "Economic Moats",
                        text = "Popularised by Warren Buffett, an economic moat is a company's sustainable competitive advantage that prevents rivals from eroding its market share and profits.",
                        example = "Brands with moats include Apple (ecosystem switching costs), Asian Paints (unmatched distributor network), and Visa (network effects)."
                    )
                ),
                quiz = listOf(
                    QuizQuestion(
                        id = "q7_1",
                        question = "What does an economic moat protect a business from?",
                        options = listOf(
                            "Competitors eroding its market share and profit margins",
                            "Paying employee bonuses",
                            "Complying with accounting rules",
                            "Having to market products"
                        ),
                        correctIndex = 0,
                        explanation = "A moat is a durable competitive advantage—such as brand loyalty, patents, or high switching costs—that defends profitability against competitors."
                    )
                )
            ),
            Lesson(
                id = "les_8",
                title = "5 Common Mistakes Beginners Make",
                category = "Psychology",
                readTimeMinutes = 4,
                summary = "Avoid the costly pitfalls of FOMO, emotional trading, and over-leveraging.",
                sections = listOf(
                    LessonSection(
                        heading = "1. Fear Of Missing Out (FOMO)",
                        text = "Buying a stock simply because it surged 50% in the last month is a recipe for buying at the peak right before smarter institutions take profits.",
                        example = "Chasing speculative penny stocks trending on social media usually ends in painful 70% drawdowns."
                    ),
                    LessonSection(
                        heading = "2. Trying to Time the Market",
                        text = "Waiting for the 'perfect dip' often results in sitting on cash while markets compound higher. Time in the market consistently beats timing the market.",
                        example = "Missing just the 10 best trading days across a 20-year period cuts overall compounding returns by nearly half."
                    )
                ),
                quiz = listOf(
                    QuizQuestion(
                        id = "q8_1",
                        question = "What does 'Time in the market beats timing the market' mean?",
                        options = listOf(
                            "Holding quality investments over long horizons yields better results than attempting to predict short-term swings",
                            "You must stare at screens 24 hours a day",
                            "You should only buy stocks right at market close",
                            "Market crashes never occur"
                        ),
                        correctIndex = 0,
                        explanation = "Consistently investing and letting compounding work over years produces vastly superior outcomes than attempting to guess daily tops and bottoms."
                    )
                )
            )
        )
    }

    // AI Financial Tutor Pre-canned High-Quality Educational Responses
    val tutorKnowledgeBase = mapOf(
        "pe" to "The Price-to-Earnings (P/E) ratio measures a company's current share price relative to its per-share earnings (EPS).\n\n• Formula: Share Price ÷ Annual Earnings Per Share\n• What it tells you: How many rupees or dollars investors pay for ₹1 or $1 of current company profit.\n• High P/E (e.g. 50+): Investors expect high future growth (or the stock may be overvalued).\n• Low P/E (e.g. 10-15): The company may be undervalued, mature, or facing business headwinds.\n\n⚠️ Important: Always compare P/E against peers in the SAME sector. A tech company and a utility company naturally command very different P/E multiples.",

        "nifty" to "The NIFTY 50 is India's premier benchmark stock market index, calculated and managed by NSE Indices.\n\n• Composition: 50 of the largest and most liquid Indian corporations across 13 major economic sectors.\n• Weighting Method: Free-Float Market Capitalisation (companies with larger market value and higher public float have higher index weight).\n• Top Heavyweights: HDFC Bank, Reliance Industries, ICICI Bank, Infosys, and TCS.\n\nWhen you hear news that 'the Indian market gained 150 points today', it usually refers to the NIFTY 50 index moving up.",

        "diversification" to "Diversification is the foundational pillar of modern risk management in financial markets.\n\n• The Core Idea: 'Don't put all your eggs in one basket.' By distributing your capital across unrelated companies, industries, asset classes, and geographies, you neutralize company-specific disasters.\n• Three-Tier Allocation:\n  1. Core Equities (NIFTY 50 / S&P 500 ETFs) for long-term compounding.\n  2. Fixed Income & Bonds for capital preservation and steady interest yields.\n  3. Commodities & Gold to hedge against high inflation and currency debasement.",

        "etf" to "An ETF (Exchange-Traded Fund) is a security that pools investor money to buy a basket of assets (stocks, bonds, or commodities) and trades on stock exchanges throughout the day.\n\nKey Differences:\n• Trading: ETFs trade like individual stocks with real-time fluctuating prices during market hours. Traditional mutual funds calculate price (NAV) only once at market close.\n• Expense Ratio: Passive ETFs typically have rock-bottom fees (often 0.05% to 0.20%), while active mutual funds may charge 0.8% to 2.0%.\n• Accessibility: You can buy even a single unit of an ETF through your standard trading account.",

        "order" to "In financial markets, understanding order types ensures you control your purchase price:\n\n1. Market Order:\n• Executes immediately at the best currently available market price.\n• Speed is guaranteed; exact execution price is NOT guaranteed.\n• Ideal for: Highly liquid blue-chips when you need to enter or exit right away.\n\n2. Limit Order:\n• You set the maximum price you will pay (Buy Limit) or minimum you will accept (Sell Limit).\n• Price is guaranteed; execution is NOT guaranteed (if market never reaches your price, it won't fill).\n• Ideal for: Volatile stocks, range-bound markets, or disciplined dip-buying."
    )
}
