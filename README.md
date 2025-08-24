# BarcodeScannerExcelApp

Android app (Kotlin) that scans barcodes with CameraX + ML Kit, prevents duplicates (beep + toast), and exports to Excel-compatible CSV.

## Build Instructions (beginner-safe)

1. Install Android Studio (latest). Open **Android Studio → Open** and select the folder `BarcodeScannerExcelApp`.
2. If prompted to **upgrade Gradle/AGP**, accept the recommended updates.
3. Click **Sync**. Then **Build > Build APK(s)**. The APK will appear under `app/build/outputs/apk/debug/`.
4. Copy the APK to your phone and install (enable "Install from unknown sources" if needed).

## Usage
- Launch the app. Grant camera permission.
- Point at barcodes. On a **duplicate**, you’ll hear a short beep and see “Duplicate: CODE”.
- Tap **Export CSV** to save a file like `barcodes_1699999999999.csv`. Open it in Excel/Google Sheets.
- Tap **Clear** to reset the list.

## Tech
- CameraX 1.3.4, ML Kit Barcode, Kotlin, minSdk 24.
