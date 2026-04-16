# Local Attendance Android Client

Android client for the LocalAttendance server.

## Build Instructions

### Debug APK
Run the following command to generate a debug APK:
```bash
./gradlew assembleDebug
```
The APK will be located at: `app/build/outputs/apk/debug/app-debug.apk`

### Release APK
1. Configure your signing key in `gradle.properties` or a separate `keystore` file.
2. Run:
```bash
./gradlew assembleRelease
```
The APK will be located at: `app/build/outputs/apk/release/app-release.apk`

## Setup
        1. Install the app on your Android device.
        2. On first launch, enter the local IP address of your running LocalAttendance server (e.g., `http://192.168.1.50:3000` or `http://localhost:3000`).
        3. Log in using your teacher credentials.

> **Note**: The app works with HTTP (not HTTPS) since the LocalAttendance server does not use SSL/TLS encryption.
