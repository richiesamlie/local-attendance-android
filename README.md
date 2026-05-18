# Local Attendance Android Client

Android client untuk backend LocalAttendance.

## Status Singkat
- API contract Android vs backend: 28/28 endpoint ter-cover di level ViewModel/UI.
- Session auto-restore: sudah terhubung via `verifySession`.
- Build lokal di host ini bisa terkendala environment Java; gunakan CI GitHub sebagai build gate utama.

## Setup Cepat
1. Install APK ke device Android.
2. Saat pertama buka app, isi URL server backend (contoh):
   - `http://192.168.1.50:3000`
   - `http://localhost:3000`
3. Login dengan akun teacher.

Catatan:
- Backend saat ini HTTP-only (tanpa HTTPS), jadi dipakai untuk environment dev/local network terpercaya.

## Build
### Debug
```bash
./gradlew assembleDebug
```
Output:
- `app/build/outputs/apk/debug/app-debug.apk`

### Release
```bash
./gradlew assembleRelease
```
Output:
- `app/build/outputs/apk/release/app-release.apk`

## CI Build Gate (Recommended)
Gunakan GitHub Actions sebagai source of truth bila host lokal tidak bisa compile:
```bash
gh run list --workflow android.yml --limit 5
gh run view <run-id> --json status,conclusion,headSha,createdAt,updatedAt,url,jobs
```

## Release APK ke GitHub Releases
Workflow sudah mendukung publish APK otomatis ke Release page saat push tag versi (`v*`).

Langkah:
```bash
git tag v1.0.0
git push origin v1.0.0
```

Hasil:
- CI build jalan
- APK debug di-upload sebagai workflow artifact
- GitHub Release dibuat/diupdate otomatis untuk tag tersebut + APK dilampirkan

## Runtime Smoke Checklist
Lihat:
- `docs/TESTING.md`

Checklist utama mencakup:
- login flow
- session restore flow (`verifySession`)
- logout flow
- sanity flow classes/students/events/timetable

## Dokumentasi Implementasi
- Roadmap phase-by-phase:
  - `docs/plans/2026-05-15-phase-execution-roadmap.md`
- Feature coverage matrix:
  - `docs/plans/2026-05-15-feature-coverage-matrix.md`
- API contract matrix:
  - `docs/plans/2026-05-15-api-contract-matrix.md`
- Alignment/backend notes:
  - `docs/plans/2026-05-15-backend-android-alignment-plan.md`

## Kontribusi
Lihat:
- `CONTRIBUTING.md`
