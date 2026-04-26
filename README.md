<div align="center">

<br/>

# 🛒 GRAB GULLY

### *"Har Deal Ka Baap."*

**India's most premium deal-discovery & price-tracking Android app**

[![Kotlin](https://img.shields.io/badge/Kotlin-2.0.21-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-2024.11-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Android](https://img.shields.io/badge/Android-API_26+-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com)
[![License](https://img.shields.io/badge/License-MIT-C9A84C?style=for-the-badge)](LICENSE)

<br/>

> **Ek tap mein sabse sasta** — One tap. Always the cheapest.

Aggregates live deals from Amazon, Flipkart, Myntra, Meesho, Ajio & Snapdeal  
into one beautifully designed, AMOLED-optimised dark-theme app.

<br/>

</div>

---

## 📱 Screenshots

> *Design system: Obsidian Black `#08080F` + Antique Gold `#C9A84C`*

| Home Feed | Compare | Track | Profile |
|---|---|---|---|
| Hero carousel + 2-col grid | Cross-platform price sort | Offline watchlist + alerts | XP ring + gamification |

---

## ✨ Features

### 🔍 Deal Discovery
- **Live aggregation** from 6 platforms: Amazon · Flipkart · Myntra · Meesho · Ajio · Snapdeal
- **Hero carousel** — auto-scrolling featured deals every 3 seconds
- **Category filters** — Electronics, Fashion, Home, Beauty, Sports, Books
- **Infinite scroll** with Paging 3 (no pagination buttons)

### 💰 Price Comparison
- **Side-by-side** price comparison across all platforms
- **"SABSE SASTA"** tag on the cheapest listing
- **90-day price history** chart (Vico line graph)
- **24h price drop banner** — alerts when price fell in last 24 hours

### 🔔 Price Tracking
- **Offline-first watchlist** — works without internet
- **Swipe-to-delete** via Material 3 SwipeToDismissBox
- **Target price alerts** — set ₹ threshold, get notified when price drops
- **FCM push notifications** — `price_drop` & `deal_flash` events with deep links

### 🏆 Gamification (PRD §4)
- **XP system** — earn points for: daily login (50), deal view (5), buy (25), alert (30)
- **10 levels** — Noob Bargainer → Gully King 👑
- **Monthly leaderboard** with animated podium (top 3)
- **Badge tiers** — Bronze / Silver / Gold / Platinum
- **Streak bonuses** — 7-day (500 XP), 30-day (2000 XP)
- **Referral rewards** — 200 XP per referred user

### 🔗 Affiliate Revenue
- **CueLinks SDK** — auto-converts Flipkart, Myntra, Meesho, Ajio, Snapdeal links
- **Amazon Creator tag** — injected by backend on all Amazon URLs
- **Share deals** — branded Hinglish share sheet copy
- **Zero budget** — 100% affiliate-first revenue model

---

## 🏗️ Architecture

```
MVVM + Clean Architecture + Hilt Dependency Injection

UI Layer (Jetpack Compose)
    ↕  StateFlow / collectAsState()
ViewModel Layer (HiltViewModel)
    ↕  suspend functions / Flow
Repository Layer
    ↕  Local (Room) + Remote (Retrofit)
Data Sources
    ├── GullyApi (Retrofit → Railway FastAPI)
    └── AppDatabase (Room → EncryptedSharedPreferences)
```

### Tech Stack

| Category | Technology |
|---|---|
| **Language** | Kotlin 2.0.21 |
| **UI** | Jetpack Compose (BOM 2024.11) |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Hilt 2.52 |
| **Navigation** | Compose Navigation 2.8.4 |
| **Networking** | Retrofit 2.11 + OkHttp 4.12 + Kotlinx Serialization |
| **Persistence** | Room 2.6 + EncryptedSharedPreferences |
| **Paging** | Paging 3 (infinite scroll) |
| **Images** | Coil 3 (OkHttp-backed) |
| **Charts** | Vico (Compose-native M3 charts) |
| **Fonts** | Poppins + Inter (Compose Downloadable Fonts) |
| **Animations** | Compose Animation + Canvas API |
| **Auth** | Supabase (Google OAuth) |
| **Push** | Firebase Cloud Messaging (FCM) |
| **Affiliate** | CueLinks SDK |
| **Backend** | FastAPI on Railway (separate repo) |

---

## 📁 Project Structure

```
app/src/main/java/com/grabgully/app/
│
├── GrabGullyApplication.kt       ← @HiltAndroidApp + CueLinks + Coil + FCM
├── MainActivity.kt               ← SplashScreen API + edge-to-edge
│
├── data/
│   ├── api/
│   │   ├── GullyApi.kt           ← Retrofit interface (13 endpoints)
│   │   └── AuthInterceptor.kt    ← JWT injection from EncryptedSharedPrefs
│   ├── model/
│   │   ├── Deal.kt               ← Core deal model
│   │   ├── CompareResult.kt      ← PlatformPrice + PricePoint
│   │   ├── WatchlistItem.kt      ← Watchlist + request/response models
│   │   └── SearchResult.kt
│   ├── db/
│   │   ├── AppDatabase.kt        ← Room database
│   │   ├── dao/                  ← WatchlistDao, CachedDealDao
│   │   └── entity/               ← WatchlistEntity, CachedDealEntity
│   └── repository/
│       ├── DealsRepository.kt    ← Paging 3
│       ├── CompareRepository.kt  ← Price compare + history
│       ├── WatchlistRepository.kt ← Offline-first sync
│       ├── SearchRepository.kt
│       └── AuthRepository.kt     ← Token management
│
├── di/
│   ├── NetworkModule.kt          ← Retrofit, OkHttp, TokenProvider
│   ├── DatabaseModule.kt         ← Room singleton
│   └── RepositoryModule.kt
│
├── service/
│   └── GullyFcmService.kt        ← FCM price_drop + deal_flash handlers
│
├── ui/
│   ├── theme/
│   │   ├── Color.kt              ← 30+ design tokens
│   │   ├── Type.kt               ← Poppins/Inter typography scale
│   │   └── Theme.kt              ← GrabGullyTheme (dark-only M3)
│   ├── components/               ← Reusable Compose components
│   │   ├── DealCard.kt
│   │   ├── PlatformBadge.kt
│   │   ├── SavingsBadge.kt
│   │   ├── CategoryChipRow.kt
│   │   ├── BottomNav.kt
│   │   ├── EmptyState.kt
│   │   ├── XpProgressRing.kt     ← Canvas-drawn animated arc
│   │   └── PriceHistoryChart.kt  ← Vico line chart
│   ├── navigation/
│   │   └── GullyNavGraph.kt      ← 6 routes + deep link handlers
│   └── screens/
│       ├── home/                 ← HomeScreen + HomeViewModel
│       ├── search/               ← SearchScreen + SearchViewModel
│       ├── compare/              ← CompareScreen + CompareViewModel
│       ├── track/                ← TrackScreen + TrackViewModel
│       ├── leaderboard/          ← LeaderboardScreen
│       ├── profile/              ← ProfileScreen + ProfileViewModel
│       └── onboarding/           ← OnboardingScreen (Google OAuth)
│
└── util/
    ├── AffiliateHelper.kt        ← open() + share() + CueLinks
    ├── XpCalculator.kt           ← Level math + badge tiers
    └── PriceFormatter.kt         ← Indian ₹ formatting
```

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** (OpenJDK works fine)
- **Android SDK** (command-line tools — Android Studio NOT required)
- **Android device** or emulator (API 26+)

### 1. Clone the Repository

```bash
git clone https://github.com/jatinchaurasiya/grabgully.app.git
cd grabgully.app
```

### 2. Install Android SDK (Command-Line Only)

```bash
# Create SDK directory
mkdir -p ~/Android/Sdk/cmdline-tools && cd ~/Android/Sdk/cmdline-tools

# Download tools (Linux)
wget https://dl.google.com/android/repository/commandlinetools-linux-11076708_latest.zip
unzip commandlinetools-linux-11076708_latest.zip && mv cmdline-tools latest

# Add to PATH (add to ~/.bashrc)
export ANDROID_HOME=~/Android/Sdk
export PATH=$PATH:$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools
source ~/.bashrc

# Install required SDK components
sdkmanager --install "platform-tools" "platforms;android-35" "build-tools;35.0.0"
```

### 3. Configure Secrets

```bash
# Create local.properties from template
cp local.properties.example local.properties

# Edit with your actual values
nano local.properties
```

> ⚠️ `local.properties` is in `.gitignore` — **never commit your actual keys**

```properties
# local.properties (fill these in)
sdk.dir=/home/YOUR_USERNAME/Android/Sdk
GULLY_BASE_URL=https://your-railway-domain.up.railway.app/
SUPABASE_URL=https://your-project-id.supabase.co
SUPABASE_ANON_KEY=eyYourSupabaseAnonKey
CUELINK_CHANNEL_ID=your_cuelink_channel_id
```

### 4. Add Firebase Config

- Go to [Firebase Console](https://console.firebase.google.com/)
- Select your project → Project Settings → Download `google-services.json`
- Place it in: `app/google-services.json`

> ⚠️ `google-services.json` is in `.gitignore` — never commit it

### 5. Build

```bash
# Make gradlew executable
chmod +x gradlew

# Build debug APK (first build downloads all deps ~5 min)
./gradlew assembleDebug

# Output: app/build/outputs/apk/debug/app-debug.apk
```

### 6. Install on Device

```bash
# Connect device via USB with USB debugging enabled
adb install app/build/outputs/apk/debug/app-debug.apk
```

---

## 🔑 Environment Variables

| Variable | Where to Get | Used For |
|---|---|---|
| `GULLY_BASE_URL` | Railway Dashboard → Settings → Domain | All API calls |
| `SUPABASE_URL` | Supabase → Project Settings → API | Auth + watchlist |
| `SUPABASE_ANON_KEY` | Supabase → Project Settings → API | Auth headers |
| `CUELINK_CHANNEL_ID` | CueLinks → My Publishers → Channel ID | Affiliate SDK |

---

## ☁️ CI/CD (GitHub Actions)

The workflow in `.github/workflows/build.yml` automatically:

1. Builds debug APK on every push to `main`
2. Uploads APK as a GitHub Actions artifact
3. Runs lint checks
4. Injects secrets from GitHub Repository Secrets

**To enable CI**, add these to **GitHub → Settings → Secrets → Actions**:

```
GULLY_BASE_URL
SUPABASE_URL
SUPABASE_ANON_KEY
CUELINK_CHANNEL_ID
FIREBASE_SERVICE_ACCOUNT   (for google-services.json)
```

---

## 🔔 FCM Notification Payload Format

The backend (FastAPI) must send FCM data-only messages in this format:

### Price Drop
```json
{
  "to": "FCM_TOKEN",
  "priority": "high",
  "data": {
    "type": "price_drop",
    "listing_id": "abc123",
    "title": "Price Gira! OnePlus 12R",
    "body": "Now ₹29,999 on Amazon — ₹5,000 off!",
    "current_price": "29999",
    "target_price": "30000"
  }
}
```

### Flash Deal
```json
{
  "to": "FCM_TOKEN",
  "priority": "high",
  "data": {
    "type": "deal_flash",
    "listing_id": "xyz789",
    "title": "🔥 Flash Deal!",
    "body": "boAt Airdopes 131 — 85% OFF for 2 hours only!",
    "discount_pct": "85",
    "platform": "Amazon"
  }
}
```

---

## 🎮 Gamification System

### XP Events

| Action | XP Earned |
|---|---|
| Daily login | +50 XP |
| Deal viewed | +5 XP |
| Deal clicked/bought | +25 XP |
| Price alert set | +30 XP |
| Search performed | +10 XP |
| Referral sent | +200 XP |
| 7-day streak | +500 XP bonus |
| 30-day streak | +2000 XP bonus |

### Level Titles

| Level | XP Required | Title |
|---|---|---|
| 1 | 0 | Noob Bargainer |
| 2 | 500 | Discount Dost |
| 3 | 1,500 | Deal Dhundhak |
| 4 | 3,000 | Savings Sipahi |
| 5 | 5,000 | Bazaar Bhai |
| 6 | 8,000 | Price Predator |
| 7 | 12,000 | Deal Ninja |
| 8 | 18,000 | Market Maharaj |
| 9 | 25,000 | Savings Sultan |
| 10 | 35,000 | **Gully King 👑** |

---

## 🗺️ Roadmap

### ✅ v1.0 — Current (Scaffold Complete)
- [x] All 6 screens built
- [x] Full data layer (Retrofit + Room + Paging 3)
- [x] FCM notifications
- [x] CueLinks affiliate SDK
- [x] Gamification UI (XP rings, leaderboard, badges)
- [x] Offline-first watchlist

### 🔜 v1.1 — Next Sprint
- [ ] Supabase Google OAuth (sign-in wiring)
- [ ] XP events wired to ViewModels
- [ ] CountdownTimer component (flash deals)
- [ ] Real leaderboard data from Supabase

### 🔮 v2.0 — Pro Tier
- [ ] Pro upgrade screen (₹99/month)
- [ ] Unlimited price alerts (free = 3)
- [ ] 90-day history (free = 7 days)
- [ ] Ad-free + Pro badge on leaderboard
- [ ] Google Play Billing integration

---

## 🤝 Related Repositories

| Repo | Purpose |
|---|---|
| [grabgully.app](https://github.com/jatinchaurasiya/grabgully.app) | This repo — Android app |
| grab-gully-scraper | Backend FastAPI + Scrapling scraper (Railway) |

---

## 📄 License

```
MIT License — © 2025 Grab Gully

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction.
```

---

<div align="center">

**Made with ❤️ and ₹ savings in mind**

*Har Deal Ka Baap. 🛒*

</div>
