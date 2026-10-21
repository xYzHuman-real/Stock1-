# Stock1 dashboard, chart, and AI Tutor upgrade

## What changed

- Kept the existing Home, Markets, instrument-detail, Portfolio, Learn, onboarding, alerts, and navigation structure.
- Added an explicit `DEMO DATA` label to the Home dashboard and clarified the simulated chart caption.
- Home, instrument detail, and instrument-specific AI Tutor context now use only the selected range's recorded history. They no longer silently substitute the generic sparkline when that range has no history.
- Kept Vico as the native Compose chart library and moved its shared version from the alpha line to stable Vico 2.2.0.
- Renamed the close-sample candle mode to `Derived Candles` and removed fabricated high/low wicks. It now explains that exchange OHLC data is unavailable.
- Migrated Gemini calls from the retired `gemini-1.5-flash` REST endpoint to Firebase AI Logic with `gemini-3.5-flash`. The Android client no longer reads or sends a private Gemini API key.
- Passed up to ten recent chat messages into the tutor prompt for conversational continuity.
- Added debug-only Firebase App Check initialization. Do not ship debug App Check tokens or use the debug provider in release builds.

## Required Firebase setup

1. Ensure the Android app is registered in the intended Firebase project and the project configuration file (`google-services.json`) is available to the local build environment. Do not commit credentials or debug tokens.
2. In Firebase Console, open **AI Logic** and enable the Gemini Developer API provider for this project.
3. For local debug testing, run the app and follow Firebase's App Check debug-token instructions. Register the generated debug token only in the Firebase Console for the intended development app.
4. Before a public release, configure a production App Check attestation provider (for Android, use an appropriate production provider such as Play Integrity) and verify enforcement in Firebase Console. Never ship the debug provider or a debug token in a release build.
5. If Firebase project configuration or AI Logic setup is missing, cloud Gemini responses will not work. The existing educational fallback remains available and the UI's configured status is based on whether Firebase app configuration is present; it cannot guarantee that the remote API is enabled or that quota is available.

## Data and chart truthfulness

- Stock1 prices are simulated demo values, not real-time exchange prices.
- A chart range with no recorded points intentionally shows the existing insufficient-history state instead of inventing a history from the sparkline.
- Derived candles use consecutive recorded simulated close values. They are not real exchange OHLC candles.

## Verification status

- Source edits were committed to the feature branch.
- Automated Gradle build and on-device interaction tests have **not** been run from this GitHub editing session.
- Before merging, run the Android Gradle build and test Home-to-detail navigation, all time ranges (including missing history), tutor context/fallback, and existing onboarding, watchlist, paper-trading, alerts, Market News, and Portfolio flows.
