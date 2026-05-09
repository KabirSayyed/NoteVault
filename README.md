<div align="center">

# 🔒 NoteVault

### Private, offline-first note-taking app for Android

**Your notes. Your device. Nobody else.**






[📥 Download APK](#download) -  [✨ Features](#features) -  [📸 Screenshots](#screenshots) -  [🚀 Getting Started](#getting-started)

</div>

***

## 📖 About

**NoteVault** is a minimalist, privacy-focused note-taking app built for Android. No accounts. No cloud. No tracking. Your notes are stored only on your device — and they stay there.

Built for people who believe their thoughts are private by default.

***

## ✨ Features

- 📝 **Create, edit & delete notes** — Simple and fast note management
- 🔍 **Instant search** — Find any note immediately as you type
- 🌙 **Dark theme** — Easy on the eyes, day and night
- 🔒 **No trackers** — Zero analytics, zero logs, zero data collection
- 📱 **Local storage only** — Notes never leave your device
- ☁️ **No cloud sync** — Works fully offline, no internet required
- 🤖 **Optional AI assistant** — Add your own API key to enable AI features
- 🔑 **API key stored locally** — Only sent to your chosen provider, never to us

***

## 📥 Download

### Latest Release — v1.0.0

👉 **[Download app-release.apk](../../releases/latest)**

**Installation steps:**
1. Download the APK file above
2. On your Android phone, go to **Settings → Security**
3. Enable **"Install from unknown sources"** (or "Install unknown apps")
4. Open the downloaded APK and tap **Install**
5. Open **NoteVault** and start writing ✅

> Minimum Android version: **Android 7.0 (API 24)**

***

## 🚀 Getting Started

### For Users
Just [download the APK](#download) and install it. No sign-up, no permissions required beyond storage.

### For Developers

**Prerequisites:**
- Android Studio (latest)
- Android SDK API 24+
- JDK 17+

**Clone & Build:**
```bash
git clone https://github.com/KabirSayyed/NoteVault.git
cd NoteVault
```

Open in **Android Studio** → let Gradle sync → click **Run ▶**

***

## 🤖 Optional AI Feature

NoteVault supports an optional AI assistant. It is **disabled by default** and requires no internet when not in use.

To enable:
1. Open the app → tap **⋮ Menu → Settings**
2. Under **AI Assistant**, tap **API Key**
3. Enter your own API key (e.g. from OpenAI, Gemini, etc.)

### ⚠️ Important Privacy Notice for AI Users

> When the AI assistant is enabled, an **internet connection is required** to process your queries. Any text you send to the AI is transmitted to your chosen API provider (e.g. OpenAI, Google, etc.) and **may be visible to that provider** according to their own privacy policy and data retention rules.
>
> **NoteVault itself never sees, stores, or logs your AI conversations.** However, your API provider might. Please review your provider's privacy policy before using this feature.

| | AI Disabled (Default) | AI Enabled |
|---|---|---|
| **Internet required** | ❌ No | ✅ Yes (for AI queries only) |
| **Data sent to NoteVault** | ❌ Never | ❌ Never |
| **Data sent to API provider** | ❌ No | ⚠️ AI queries only |
| **Notes stored in cloud** | ❌ Never | ❌ Never |
| **API key stored** | — | ✅ Locally on device only |

***

## 🔐 Privacy

NoteVault is **private by design**:

| What | Status |
|------|--------|
| Analytics / tracking | ❌ None |
| Cloud sync | ❌ None |
| Internet permission (base app) | ❌ Not required |
| Internet permission (AI feature) | ⚠️ Required only when AI is enabled |
| Account / login | ❌ Not required |
| Data sent to NoteVault servers | ❌ Never |
| AI query data | ⚠️ Sent to your API provider only |
| Local note storage | ✅ Device only |

***

## 🛠️ Built With

- **Kotlin** — Primary language
- **Android Jetpack** — Architecture components
- **Room Database** — Local note storage
- **Material Design 3** — UI components
- **Android Studio** — IDE

***

## 📁 Project Structure

```
NoteVault/
├── app/src/main/
│   ├── java/com/notevault/app/
│   │   ├── MainActivity.kt
│   │   ├── NoteAdapter.kt
│   │   ├── NoteViewModel.kt
│   │   └── SettingsActivity.kt
│   └── res/
│       ├── layout/
│       ├── drawable/
│       └── values/
├── build.gradle.kts
└── README.md
```

***

## 🤝 Contributing

Contributions, issues and feature requests are welcome!

1. Fork the repository
2. Create your branch: `git checkout -b feature/YourFeature`
3. Commit your changes: `git commit -m 'Add YourFeature'`
4. Push to the branch: `git push origin feature/YourFeature`
5. Open a **Pull Request**

***

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.

***

<div align="center">

Made with ❤️ — **Private by design**

⭐ Star this repo if you find it useful!

</div>
