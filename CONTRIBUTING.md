# Contributing to Grab Gully

Thank you for your interest in contributing! 🛒

## Development Setup

1. Fork and clone the repo
2. Copy `local.properties.example` → `local.properties` and fill in your keys
3. Add your `google-services.json` from Firebase Console to `app/`
4. Run `./gradlew assembleDebug` to verify your setup

## Code Style

- Follow **Kotlin official style guide**
- All Compose composables must have a `@Preview` annotation
- Use design tokens from `ui/theme/Color.kt` — **never hardcode colors**
- Use `PriceFormatter.kt` for all ₹ formatting — **never format prices inline**
- Indian Hinglish copy must match the brand voice in `res/values/strings.xml`

## Pull Request Process

1. Create a branch: `feature/your-feature-name`
2. Write code, ensure no lint errors: `./gradlew lint`
3. Open a PR against `main` with a clear description
4. CI will auto-build your APK — attach the artifact to your PR

## Architecture Rules

- **ViewModels** must only depend on **Repositories** (never on `Context`)
- **Repositories** handle all data logic — screens must not call APIs directly
- **Hilt** for all dependency injection — no manual singletons
- **Flow** for reactive data — no LiveData
- **StateFlow** for UI state — no MutableState in ViewModels

## Reporting Bugs

Open a GitHub Issue with:
- Device model + Android version
- Steps to reproduce
- Expected vs actual behavior
- Logcat output (redact any personal data)
