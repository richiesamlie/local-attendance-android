# Testing Guide

This document provides a comprehensive guide on the various types of testing used in the project, including unit testing, integration testing, UI testing, as well as the overall test structure. Additionally, you will find instructions on how to run tests and generate coverage reports.

## Table of Contents
1. [Unit Testing](#unit-testing)
2. [Integration Testing](#integration-testing)
3. [UI Testing](#ui-testing)
4. [Test Structure](#test-structure)
5. [Running Tests](#running-tests)
6. [Coverage Reports](#coverage-reports)

## Unit Testing

Unit testing focuses on testing individual components or functions in isolation. In this project, the following practices should be followed:
- Ensure that every function has corresponding unit tests.
- Use mocking for external dependencies to isolate the unit being tested.
- Follow the Arrange-Act-Assert pattern for clear test structure.

### Example
```java
@Test
public void testExampleFunction() {
    // Arrange
    int expected = 10;
    int input = 5;

    // Act
    int result = exampleFunction(input);

    // Assert
    assertEquals(expected, result);
}
```

## Integration Testing

Integration testing focuses on verifying that different components work together correctly. Important guidelines include:
- Test the integration points between modules.
- Use real instances of database or web service when necessary, but consider using a staging environment.

### Example
```java
@Test
public void testIntegrationOfModuleAAndB() {
    // Code to test integrated modules
}
```

## UI Testing

UI testing ensures that the user interface behaves as expected. This can be achieved using tools such as Espresso or UI Automator. Testing should cover:
- User flows
- Button clicks
- Data entry forms

### Example
```java
@Test
public void testButtonClick() {
    onView(withId(R.id.button)).perform(click());
    onView(withId(R.id.textView)).check(matches(withText("Button Clicked")));
}
```

## Test Structure

The project should follow a structured approach to organizing tests:
- Place unit tests in the `src/test/java` directory.
- Place integration tests in the `src/integrationTest/java` directory.
- Place UI tests in the `src/androidTest/java` directory.

## Running Tests

To run the tests, use the following command commands:
- **Unit tests:** `./gradlew test`
- **Integration tests:** `./gradlew integrationTest`
- **UI tests:** `./gradlew connectedAndroidTest`

## Coverage Reports

To generate coverage reports, ensure that the following plugin is applied in your `build.gradle` file:
```groovy
apply plugin: 'jacoco'
```

Run the tests with coverage by using:
```bash
./gradlew test jacocoTestReport
```

This will generate a coverage report that can be found in `build/reports/jacoco/test/html`.

---

## LocalAttendance Android — Runtime Smoke Checklist (Host Tidak Bisa Compile)

Kondisi saat ini:
- Host lokal tidak bisa compile (kendala environment/JDK), jadi build gate mengacu ke GitHub Actions (CI) sebagai source of truth.
- CI referensi terbaru yang lulus:
  - Workflow: `Android CI`
  - Run ID: `24491361938`
  - Status: `success`
  - URL: `https://github.com/richiesamlie/local-attendance-android/actions/runs/24491361938`

### A. CI Build Gate (wajib sebelum runtime check)
1. Cek run terbaru:
   - `gh run list --workflow android.yml --limit 5`
2. Ambil detail run target:
   - `gh run view <run-id> --json status,conclusion,headSha,createdAt,updatedAt,url,jobs`
3. Gate lulus jika:
   - `conclusion == success`
   - Job `build` sukses
   - Step upload APK/artifact sukses

### B. Manual Runtime Smoke (Device/Emulator)
Gunakan APK dari artifact CI atau build dari mesin lain yang kompatibel.

1. Login Flow
- Buka app -> login dengan akun valid.
- Expected: masuk ke Dashboard.

2. Session Restore Flow (`verifySession`)
- Setelah login sukses, tutup app (swipe close), buka lagi.
- Expected: auto masuk Dashboard tanpa login ulang.
- Catatan teknis: flow ini memakai `AuthViewModel.restoreSessionIfAvailable()` -> `api.verifySession()`.

3. Logout Flow
- Dari Settings, lakukan logout.
- Expected: kembali ke layar Login.
- Buka ulang app.
- Expected: tetap di Login (session tidak auto-restore setelah logout).

4. Core Feature Sanity
- Classes: buka daftar class, edit class sederhana.
- Students: edit 1 student.
- Class Detail: pastikan `Total Teachers` tampil.
- Events: create/update/delete 1 event.
- Timetable: create/update/delete 1 slot.

### C. Evidence yang Harus Dicatat
- Tanggal/jam test
- Device/emulator yang dipakai
- Backend URL
- Hasil per langkah: PASS/FAIL
- Screenshot/log jika FAIL

### D. Handoff Eksekusi Smoke (Session Ini)
- APK artifact berhasil diambil dari GitHub Actions:
  - Run ID: `24491361938`
  - Artifact: `app-debug`
  - Lokasi lokal: `C:/repoandroid/.artifacts/run-24491361938/app-debug.apk`
- Checksum lokal APK (integrity check):
  - SHA-256: `f1e4a5fdca77de50b2415d81b28309176398c3c5e4d4afbe0b56d4d6692f8242`
- Tool Android tersedia di host:
  - `adb` terdeteksi di `/c/platform-tools/adb`
- Status device saat dicek:
  - `adb devices` belum menampilkan device/emulator aktif.

### E. Next Exact Commands (siap copy-paste)
1. Cek device/emulator:
   - `adb devices`
2. Install APK ke device:
   - `adb install -r /c/repoandroid/.artifacts/run-24491361938/app-debug.apk`
3. Jalankan app dan eksekusi checklist section B.
4. Ambil log bila perlu:
   - `adb logcat | grep -i -E "verifySession|auth|login|logout"`

For any issues related to testing, please refer to the documentation or reach out to the team.