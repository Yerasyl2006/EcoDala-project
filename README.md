# EcoDala Android

EcoDala is an Android application designed to help users build eco-friendly habits through recycling, smart city infrastructure maps, gamification, and an AI-assisted waste scanner.

The project is built with Kotlin and Jetpack Compose and follows a feature-based structure with MVVM-style presentation logic. The app currently includes authentication screens, a home dashboard, an interactive map, recycling point details, waste submission, virtual tree growth, challenges, leaderboard, profile, achievements, notifications, settings, support, and an AI Waste Scanner demo flow.

## Main Features

- Splash, login, registration, email verification, and forgot password screens
- Home dashboard with EcoPoints, quick actions, achievements, and language switching
- Map screen with recycling points, biotoilets, water stations, and eco reports
- Recycling point details and route actions
- Waste submission flow with points reward logic
- AI Waste Scanner flow with CameraX and backend-ready scanner response handling
- Virtual Tree with animated growth from level 0 to level 10
- Challenges, leaderboard, achievements, and recycling history
- Profile, notifications, settings, support, dark mode, and localization
- English, Russian, and Kazakh localization support
- Backend-ready Retrofit integration with JWT token handling

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- MVVM-style ViewModels
- Retrofit and Moshi
- OkHttp logging interceptor
- DataStore Preferences
- CameraX
- OSMDroid map integration
- Google Play Services Location

## Project Structure

```text
com.ecodala
|-- core
|   |-- auth
|   |-- data
|   |   |-- dummy
|   |   |-- remote
|   |   `-- repository
|   |-- domain
|   |   |-- model
|   |   |-- repository
|   |   `-- usecase
|   |-- localization
|   |-- navigation
|   |-- session
|   |-- settings
|   |-- state
|   |-- ui
|   `-- validation
`-- feature
    |-- achievements
    |-- auth
    |-- challenges
    |-- home
    |-- leaderboard
    |-- map
    |-- profile
    |-- scanner
    |-- splash
    |-- submit
    `-- tree
```

## Backend Connection

The Android app is prepared to work with a Django REST Framework backend through Retrofit.

For emulator testing, the backend URL can use:

```text
http://10.0.2.2:8000/api/
```

For physical device testing with USB debugging, use ADB reverse:

```powershell
C:\Users\Erasyl\AppData\Local\Android\Sdk\platform-tools\adb.exe reverse tcp:8000 tcp:8000
```

Then the Android debug build can use:

```text
http://127.0.0.1:8000/api/
```

For physical device testing over Wi-Fi, use the laptop local IP address:

```text
http://YOUR_LAPTOP_IP:8000/api/
```

## Demo Account

```text
Email: demo@ecodala.kz
Password: DemoPass123!
```

## Build

Open the project in Android Studio and run the `app` configuration.

Command line build:

```powershell
.\gradlew.bat :app:assembleDebug
```

## Current Status

EcoDala is currently a working Android prototype with backend-ready architecture and multiple implemented screens. Some features still use demo data as fallback while backend endpoints are being completed.

Recommended next improvements:

- Add dependency injection with Hilt or a manual AppContainer
- Add a proper token refresh flow for JWT authentication
- Replace silent dummy fallback with explicit loading, empty, error, and retry states
- Move all hardcoded UI strings into localization
- Add UI and ViewModel tests
- Add production/staging/debug backend configuration
- Improve map routing and offline caching

## Goal

EcoDala aims to become a practical environmental platform for Kazakhstan by combining recycling services, city infrastructure discovery, community eco reports, gamified progress, and AI-assisted waste sorting in one mobile application.
