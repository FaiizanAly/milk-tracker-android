# 🥛 Milk Tracker — दूध का हिसाब

A simple, modern, and 100% offline Android app to track daily milk deliveries, quantities, and payment records effortlessly.

[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://github.com/FaiizanAly/milk-tracker-android/releases/tag/v1.0.2)
[![Release](https://img.shields.io/badge/Release-v1.0.2-007AFF?style=for-the-badge&logo=github&logoColor=white)](https://github.com/FaiizanAly/milk-tracker-android/releases/tag/v1.0.2)
[![Offline First](https://img.shields.io/badge/Offline%20First-100%25-34C759?style=for-the-badge&logo=offline&logoColor=white)](https://github.com/FaiizanAly/milk-tracker-android)
[![Privacy](https://img.shields.io/badge/Privacy-100%25%20Local-FF9500?style=for-the-badge&logo=shield&logoColor=white)](https://github.com/FaiizanAly/milk-tracker-android)

---

## 📥 Download & Install (Android)

Get the latest version directly on your Android phone:

> [!TIP]
> **Milk Tracker** is completely free, lightweight, and requires **no account, sign-up, or internet connection**.

### Quick Install Steps:

1. 🌐 Open the **[Milk Tracker v1.0.2 Release Page](https://github.com/FaiizanAly/milk-tracker-android/releases/tag/v1.0.2)**.
2. 📦 Scroll down to the **Assets** section and tap **`Milk Tracker.apk`** to download the app.
3. 📂 Open the downloaded **`Milk Tracker.apk`** file from your phone's notification bar or `Downloads` folder.
4. ⚙️ If Android asks for permission to install apps from unknown sources, tap **Settings**, allow it once, and tap **Install**.
5. 🚀 Launch **Milk Tracker** from your home screen—start tracking immediately!

---

## ✨ Why You'll Love It

- 📝 **Easy Daily Tracking:** Add morning and evening milk entries with a single tap. Review past entries anytime with direct **Edit (✏️)** and **Delete (🗑️)** buttons on each row.
- ➕ **Custom Daily Delivery Count (`+`):** Quickly pick `1` or `2` deliveries per day, or tap `+` to enter a custom delivery count (`3+`).
- ⚡ **Smart Rate Updates:** Update your milk price per liter or kilogram in Settings; the app recalculates everything and returns you straight to the home screen automatically.
- 💾 **One-Tap Backup & Restore:** Export clean **Excel (`.xlsx`)** spreadsheets and lightweight **JSON (`.json`)** backups directly into your phone's **Downloads** folder. Restore your data anytime with one tap.
- 🔒 **100% Offline & Private:** Operates entirely without an internet connection. Every record stays safely on your own device—nothing is ever uploaded to any cloud server or shared with third parties.
- 📱 **Fits Every Android Screen:** Locked WebView font scaling (`setTextZoom(100)`), smooth vertical scrolling, and responsive narrow-screen layout so nothing zooms or clips on any phone size.
- 💬 **Clean, Natural Labels:** Polished bilingual (English & Hindi) labels such as `"No Milk Today"` and refined payment/rate controls.
- 🎨 **Clean Liquid-Glass Design:** Crafted with an Apple iOS-inspired Light Mode aesthetic, smooth spring animations, custom app icon, and full bilingual support (English & Hindi).

---

## 📱 Quick Guide

1. **Log Daily Milk:**  
   Select the date, choose **1**, **2**, or tap **+** for custom deliveries per day, and pick the milk quantity (or tap **No Milk Today**). Your monthly total, overall cost, and dues update instantly.
2. **Set Your Milk Rate:**  
   Tap **Settings (⚙️)** in the top right, enter your rate per liter/kg, and tap **Save Rate**. The app saves your price and automatically brings you back to the home screen.
3. **Record Payments:**  
   Whenever you pay your milkman, enter the amount in the payment card to keep your pending balance clear and accurate.
4. **Take Backups:**  
   Open **Settings (⚙️)**, expand **Backup & Restore**, and tap **Download Excel Backup (`.xlsx`)** or **Download JSON Backup (`.json`)**. Your backup file will be saved immediately to your phone's **Downloads** directory.

---

## 👨‍💻 Created With ❤️ by Faizan Ali

- 🐙 **GitHub:** [@FaiizanAly](https://github.com/FaiizanAly)
- 💼 **LinkedIn:** [linkedin.com/in/faiizanaly](https://linkedin.com/in/faiizanaly/)
- 🌐 **Portfolio:** [dev-faizan.vercel.app](https://dev-faizan.vercel.app/)

---

<details>
<summary>🛠️ For Developers (Architecture, Setup & Build)</summary>

<br />

### Technical Overview

This repository contains the single-file web application and the native Android wrapper configured via Capacitor.

#### 1. Architecture & Frontend
- **Single-File Setup:** The application logic, styles, and markup reside in a single self-contained [`index.html`](./index.html) file (HTML5, CSS3, vanilla ES6+ JavaScript), mirrored to [`www/index.html`](./www/index.html) for native packaging.
- **UI & Styling:** iOS Light Mode liquid-glass surfaces (`backdrop-filter: blur`), SF Pro typography hierarchy, responsive touch targets, and native bottom sheets.
- **Local Persistence:** All entries, payments, language selection, unit, and rate configurations persist in browser / WebView `localStorage` under the key `doodhHisabV1`.
- **Spreadsheet Generation:** Employs SheetJS (`xlsx.full.min.js`) for generating `.xlsx` workbooks with multiple worksheets (`Hisab` and `Settings`).

#### 2. Android & Capacitor Platform
- **Capacitor Configuration:** Defined in [`capacitor.config.json`](./capacitor.config.json) (`appId: "com.faiizanaly.milktracker"`, `appName: "Milk Tracker"`, `webDir: "www"`).
- **Custom Launcher Icons:** Sourced from [`./app_icon.png`](./app_icon.png) (1024×1024) and mapped to all Android density buckets:
  - `mipmap-mdpi` (48×48 / 108×108 foreground)
  - `mipmap-hdpi` (72×72 / 162×162 foreground)
  - `mipmap-xhdpi` (96×96 / 216×216 foreground)
  - `mipmap-xxhdpi` (144×144 / 324×324 foreground)
  - `mipmap-xxxhdpi` (192×192 / 432×432 foreground)
- **Native WebView & Download Bridge:**
  - Configured inside [`android/app/src/main/java/com/faiizanaly/milktracker/MainActivity.java`](./android/app/src/main/java/com/faiizanaly/milktracker/MainActivity.java).
  - Exposes `window.AndroidDownloader.saveBase64File(filename, mimeType, base64)` to the WebView.
  - On **Android 10+ (API 29+)**, saves `.xlsx` and `.json` files directly into `MediaStore.Downloads` via `ContentResolver` (no runtime permission popups required). On older versions, writes to `Environment.DIRECTORY_DOWNLOADS` and registers with `MediaScannerConnection`.
  - Attaches an Android `DownloadListener` for handling `data:` and `blob:` downloads, triggering a native Toast notification: `"Backup saved to Downloads: <filename>"`.
- **Permissions & Storage:** Configured in [`android/app/src/main/AndroidManifest.xml`](./android/app/src/main/AndroidManifest.xml) with `INTERNET`, `READ_EXTERNAL_STORAGE`, `WRITE_EXTERNAL_STORAGE`, and `android:requestLegacyExternalStorage="true"`.

#### 3. Local Development & Build Steps

1. **Clone the repository:**
   ```bash
   git clone https://github.com/FaiizanAly/milk-tracker-android.git
   cd milk-tracker-android
   ```

2. **Install dependencies:**
   ```bash
   npm install
   ```

3. **Sync web assets to the native Android project:**
   ```bash
   npx cap sync android
   ```

4. **Compile the Debug APK:**
   - **Windows (PowerShell / Command Prompt):**
     ```powershell
     cd android
     .\gradlew.bat assembleDebug
     ```
   - **macOS / Linux:**
     ```bash
     cd android
     ./gradlew assembleDebug
     ```
   - **Built APK Location:**
     ```
     android/app/build/outputs/apk/debug/app-debug.apk
     ```

> **Note:** Build directories (`android/app/build/`, `.gradle/`) and `node_modules/` are excluded from Git via [`.gitignore`](./.gitignore).

</details>
