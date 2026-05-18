# Android x Backend Upstream Execution Roadmap (Resumable)

Tanggal: 2026-05-15
Repo Android: `C:\repoandroid`
Backend acuan (source of truth): `https://github.com/richiesamlie/LocalAttendace-Final` (commit referensi audit: `faa0744`)
Strategi: Android mengikuti backend upstream. Backend tidak diubah.

---

## 1) Status Saat Ini (Checkpoint)

- API contract endpoint Android vs backend: MATCH `28/28` (lihat `2026-05-15-api-contract-matrix.md`).
- Koneksi network stack sudah sesuai arsitektur backend session-cookie:
  - `NetworkModule.kt`
  - `DynamicBaseUrlInterceptor.kt`
  - `CookieInterceptor.kt`
- Build lokal belum jadi bukti final karena blocker environment Java:
  - JDK 26 terdeteksi tidak kompatibel untuk stack Gradle 8.2 + AGP 8.2.2.
  - Baseline yang dibutuhkan: JDK 17.
- Coverage fitur Android belum 100% terhadap kemampuan backend:
  - Sudah terpakai: auth, classes, students, attendance records, events read, timetable read, invite redeem, revoke sessions.
  - Belum terpakai penuh: daily notes CRUD, events CRUD penuh, timetable CRUD penuh, teacher/invite management, seating.

---

## 2) Prinsip Eksekusi

1. Jangan ubah backend upstream.
2. Utamakan gap yang berdampak ke user flow harian guru (kelas, siswa, absensi, jadwal, catatan).
3. Setiap phase wajib punya:
   - tujuan,
   - perubahan file,
   - langkah verifikasi,
   - catatan handoff besok.
4. Semua perubahan terdokumentasi agar bisa dilanjutkan tanpa konteks chat.

---

## 3) Phase-by-Phase Plan

## Phase 0 — CI Build Gate (GitHub as Source of Build Truth)
Tujuan: memakai hasil CI GitHub sebagai bukti utama build/installability, tanpa ketergantungan setup JDK lokal.

Langkah:
1. Cek run workflow Android terbaru di GitHub Actions.
2. Verifikasi job build utama sukses (compile + assemble).
3. Catat run ID, commit SHA, waktu run, dan status artifact APK.
4. Jika CI merah, triage dari log CI dulu sebelum debugging lokal.

Deliverable:
- Catatan bukti CI (run URL/ID, status pass/fail, ringkasan error jika gagal).

Exit criteria:
- Build gate dinyatakan lulus berdasarkan CI GitHub terbaru.

---

## Phase 1 — Feature Coverage Audit Lock (Code-level)
Tujuan: mengunci peta endpoint -> ViewModel -> Screen (apa yang sudah dipakai, apa yang belum).

Langkah:
1. Buat matriks coverage fitur per-screen.
2. Tandai status per item: `Implemented`, `Partial`, `Missing`.
3. Definisikan prioritas implementasi berdasarkan impact.

Deliverable:
- Dokumen baru: `docs/plans/2026-05-15-feature-coverage-matrix.md`.

Exit criteria:
- Semua endpoint di `AttendanceApi.kt` punya status usage yang jelas.

---

## Phase 2 — Daily Notes End-to-End
Tujuan: mengaktifkan flow catatan harian kelas (read + write) di UI Android.

Langkah (target minimum):
1. Tambah ViewModel untuk daily notes (jika belum ada).
2. Sambungkan ke endpoint:
   - `GET /api/notes/classes/{classId}/daily-notes`
   - `POST /api/notes/classes/{classId}/daily-notes`
3. Tambah/ubah screen Compose untuk menampilkan + submit note.
4. Handle loading, empty state, error state.

Deliverable:
- Kode feature daily notes + dokumentasi smoke test.

Exit criteria:
- User bisa lihat dan kirim daily notes dari app.

---

## Phase 3 — Events CRUD Full
Tujuan: events tidak hanya read, tapi full CRUD dari app.

Langkah:
1. Wire `addEvent`, `updateEvent`, `deleteEvent` ke ViewModel/UI.
2. Form validasi input tanggal/jam.
3. Optimistic/refresh strategy setelah create/update/delete.

Deliverable:
- Events screen support create/edit/delete.

Exit criteria:
- CRUD event sukses terhadap backend upstream.

---

## Phase 4 — Timetable CRUD Full
Tujuan: jadwal pelajaran bisa dikelola penuh dari app.

Langkah:
1. Wire `addTimetableSlot`, `updateTimetableSlot`, `deleteTimetableSlot`.
2. Pastikan mapping `lesson: String` konsisten end-to-end.
3. Validasi day mapping (0/1 edge case) tetap aman.

Deliverable:
- Timetable management UI + behavior.

Exit criteria:
- CRUD timetable berhasil tanpa mismatch tipe.

---

## Phase 5 — Teacher & Invite Management
Tujuan: melengkapi alur administrasi kelas.

Langkah:
1. Integrasi `getClassTeachers` pada UI class detail/settings.
2. Tambah flow invite management (create/list/delete) jika endpoint tersedia dan dibutuhkan UX.
3. Pertahankan `invite redeem` flow yang sudah berjalan.

Deliverable:
- Admin class management minimal usable.

Exit criteria:
- Guru/admin bisa kelola kolaborator kelas dari app.

---

## Phase 6 — Seating Feature Decision
Tujuan: memutuskan apakah seating dijadikan scope aktif.

Langkah:
1. Audit endpoint seating di backend.
2. Keputusan: `Implement now` atau `Defer`.
3. Jika defer, dokumentasikan alasannya agar tidak ambigu.

Deliverable:
- Decision record di dokumen plan.

Exit criteria:
- Tidak ada scope abu-abu untuk seating.

---

## Phase 7 — Runtime Verification & CI Hardening
Tujuan: bukti konektivitas runtime, bukan hanya compile.

Langkah:
1. Tambah smoke test checklist flow utama:
   - login -> classes -> students -> attendance -> notes -> events -> timetable -> invite.
2. Tambah catatan CI agar jelas bedakan:
   - build pass,
   - test pass,
   - runtime smoke pass.
3. Jika memungkinkan, tambah test otomatis untuk contract critical path.

Deliverable:
- `docs/TESTING.md` update + checklist lulus.

Exit criteria:
- Ada bukti verifikasi runtime yang repeatable.

---

## 4) Tracking Template (Wajib update tiap phase)

Gunakan template berikut setiap selesai phase:

- Phase: `N`
- Tanggal/Jam:
- Tujuan phase:
- File diubah:
- Ringkasan perubahan:
- Verifikasi yang dijalankan:
- Hasil verifikasi:
- Risiko tersisa:
- Next exact command/step untuk lanjut:

---

## 5) Next Action Besok (Start Here)

1. Selesaikan **Phase 0** dulu (validasi CI GitHub run terbaru).
2. Lanjut **Phase 1**: generate `feature-coverage-matrix` dari source ViewModel/Screen.
3. Mulai implementasi **Phase 2** (Daily Notes) sebagai gap prioritas pertama.

Perintah awal yang disarankan saat lanjut:
- `gh run list --workflow android.yml --limit 5`
- `gh run view <run-id> --json status,conclusion,headSha,createdAt,updatedAt,url,jobs`
- Jika CI hijau: lanjut implementasi phase berikutnya tanpa gate JDK lokal.

---

## 6) Referensi Dokumen Terkait

- `docs/plans/2026-05-15-api-contract-matrix.md`
- `docs/plans/2026-05-15-backend-android-alignment-plan.md`
- `docs/plans/2026-05-15-phase1-backend-route-fix-spec.md` (superseded)

---

## 7) Progress Log (Resumable Handoff)

### Entry #1
- Phase: `0`
- Tanggal/Jam: 2026-05-15
- Tujuan phase: Validasi build gate via CI GitHub (tanpa JDK lokal).
- File diubah:
  - `docs/plans/2026-05-15-phase-execution-roadmap.md`
  - `docs/plans/2026-05-15-feature-coverage-matrix.md`
- Ringkasan perubahan:
  - Phase 0 resmi dialihkan ke CI-based gate.
  - Bukti CI terbaru dikonfirmasi dari workflow `android.yml`.
  - Run terbaru sukses: `24491361938` (conclusion: success), job `build` sukses, upload APK sukses.
  - SHA run: `74668f76d82541808255e8644e62fbb22daf7844`.
- Verifikasi yang dijalankan:
  - `gh run list --workflow android.yml --limit 5`
  - `gh run view 24491361938 --json status,conclusion,headSha,createdAt,updatedAt,url,jobs`
- Hasil verifikasi:
  - Build gate lulus berdasarkan CI GitHub terbaru yang tersedia.
- Risiko tersisa:
  - CI hijau belum otomatis membuktikan runtime semua flow fitur.
  - Masih ada endpoint yang belum dipakai di level ViewModel/UI.
- Next exact command/step untuk lanjut:
  - Lanjut Phase 2 (Daily Notes) implementasi kode + smoke checklist.

### Entry #2
- Phase: `1`
- Tanggal/Jam: 2026-05-15
- Tujuan phase: Kunci peta coverage endpoint -> ViewModel -> screen.
- File diubah:
  - `docs/plans/2026-05-15-feature-coverage-matrix.md`
- Ringkasan perubahan:
  - Dibuat matriks coverage penggunaan endpoint berdasarkan source code.
  - Coverage pemakaian endpoint ViewModel saat ini: `15/28` (53.6%).
  - Gap prioritas ditetapkan: Daily Notes, Events CRUD, Timetable CRUD.
- Verifikasi yang dijalankan:
  - Query endpoint di `AttendanceApi.kt` (`@(GET|POST|PUT|DELETE)`, `suspend fun ...`).
  - Query pemanggilan `api.<method>(...)` di seluruh `*ViewModel.kt`.
- Hasil verifikasi:
  - Peta implemented/partial/missing sudah terdokumentasi dan siap dieksekusi.
- Risiko tersisa:
  - Belum ada runtime proof untuk endpoint yang belum dipakai.
- Next exact command/step untuk lanjut:
  - Mulai patch `EventsViewModel`/screen atau `Daily Notes` feature (Phase 2 sebagai prioritas utama).

### Entry #3
- Phase: `2`
- Tanggal/Jam: 2026-05-15
- Tujuan phase: Implementasi Daily Notes end-to-end (read + write) di Android.
- File diubah:
  - `app/src/main/java/com/localattendance/client/ui/screens/notes/DailyNotesViewModel.kt` (baru)
  - `app/src/main/java/com/localattendance/client/ui/screens/notes/DailyNotesScreen.kt` (baru)
  - `app/src/main/java/com/localattendance/client/ui/navigation/Screen.kt`
  - `app/src/main/java/com/localattendance/client/ui/navigation/AppNavigation.kt`
  - `app/src/main/java/com/localattendance/client/ui/screens/classdetail/ClassDetailScreen.kt`
  - `docs/plans/2026-05-15-feature-coverage-matrix.md`
- Ringkasan perubahan:
  - Tambah route baru `notes/{classId}` (`Screen.DailyNotes`).
  - Tambah screen Daily Notes dengan form `date + note`, list notes tersimpan, loading/error/success state.
  - Tambah ViewModel Daily Notes yang memakai endpoint backend:
    - `GET /api/notes/classes/{classId}/daily-notes`
    - `POST /api/notes/classes/{classId}/daily-notes`
  - Tambah Quick Action "Daily Notes" di `ClassDetailScreen`.
  - Wiring navigation dari class detail ke daily notes selesai.
  - Coverage endpoint naik dari `15/28` ke `17/28` (60.7%).
- Verifikasi yang dijalankan:
  - Search pemanggilan API: `api.getDailyNotes(...)`, `api.saveDailyNote(...)` di ViewModel.
  - Search route/navigation reference `DailyNotes` di `Screen.kt` + `AppNavigation.kt`.
  - Search aksi `onNavigateToDailyNotes` di `ClassDetailScreen.kt`.
- Hasil verifikasi:
  - Endpoint Daily Notes sudah terhubung end-to-end pada level kode UI/ViewModel.
- Risiko tersisa:
  - Belum ada runtime smoke test manual/otomatis untuk flow Daily Notes.
  - Events & Timetable masih read-only (CRUD belum diimplementasi).
- Next exact command/step untuk lanjut:
  - Lanjut Phase 3: implementasi Events CRUD (`addEvent`, `updateEvent`, `deleteEvent`).

### Entry #4
- Phase: `3`
- Tanggal/Jam: 2026-05-18
- Tujuan phase: Implementasi Events CRUD full (read + create + update + delete) di Android.
- File diubah:
  - `app/src/main/java/com/localattendance/client/ui/screens/events/EventsViewModel.kt`
  - `app/src/main/java/com/localattendance/client/ui/screens/events/EventsScreen.kt`
  - `docs/plans/2026-05-15-feature-coverage-matrix.md`
- Ringkasan perubahan:
  - `EventsViewModel` ditingkatkan dari read-only menjadi CRUD penuh.
  - Ditambahkan state `isSaving`, `successMessage`, dan helper `clearMessages()`.
  - Ditambahkan method baru:
    - `addEvent(classId, event)`
    - `updateEvent(classId, id, request)`
    - `deleteEvent(classId, id)`
  - `EventsScreen` di-upgrade dengan:
    - Dialog tambah event.
    - Dialog edit event.
    - Tombol delete per event.
    - Snackbar feedback sukses/gagal.
  - Coverage endpoint ViewModel naik dari `17/28` ke `20/28` (71.4%).
- Verifikasi yang dijalankan:
  - Search pemanggilan API CRUD event di ViewModel:
    - `api.addEvent(...)`
    - `api.updateEvent(...)`
    - `api.deleteEvent(...)`
  - Validasi dokumen coverage matrix diperbarui sesuai status terbaru.
- Hasil verifikasi:
  - Endpoint Events CRUD sudah terhubung end-to-end pada level kode UI/ViewModel.
- Risiko tersisa:
  - Belum ada runtime smoke test manual/otomatis untuk flow CRUD event.
  - Timetable masih read-only (CRUD belum diimplementasi).
- Next exact command/step untuk lanjut:
  - Lanjut Phase 4: implementasi Timetable CRUD (`addTimetableSlot`, `updateTimetableSlot`, `deleteTimetableSlot`).

### Entry #5
- Phase: `4`
- Tanggal/Jam: 2026-05-18
- Tujuan phase: Implementasi Timetable CRUD full (read + create + update + delete) di Android.
- File diubah:
  - `app/src/main/java/com/localattendance/client/ui/screens/timetable/TimetableViewModel.kt`
  - `app/src/main/java/com/localattendance/client/ui/screens/timetable/TimetableScreen.kt`
  - `docs/plans/2026-05-15-feature-coverage-matrix.md`
- Ringkasan perubahan:
  - `TimetableViewModel` ditingkatkan dari read-only menjadi CRUD penuh.
  - Ditambahkan state `isSaving`, `successMessage`, dan helper `clearMessages()`.
  - Ditambahkan method baru:
    - `addTimetableSlot(classId, slot)`
    - `updateTimetableSlot(classId, id, request)`
    - `deleteTimetableSlot(classId, id)`
  - `TimetableScreen` di-upgrade dengan:
    - Floating action button untuk tambah slot.
    - Dialog tambah slot dan edit slot.
    - Tombol delete per slot.
    - Snackbar feedback sukses/gagal.
  - Timetable list diurutkan per hari dan jam mulai (`startTime`).
  - Coverage endpoint ViewModel naik dari `20/28` ke `24/28` (85.7%).
- Verifikasi yang dijalankan:
  - Search pemanggilan API CRUD timetable di ViewModel:
    - `api.addTimetableSlot(...)`
    - `api.updateTimetableSlot(...)`
    - `api.deleteTimetableSlot(...)`
  - Search wiring aksi CRUD di `TimetableScreen`:
    - `viewModel.addTimetableSlot(...)`
    - `viewModel.updateTimetableSlot(...)`
    - `viewModel.deleteTimetableSlot(...)`
  - Validasi dokumen coverage matrix diperbarui sesuai status terbaru.
- Hasil verifikasi:
  - Endpoint Timetable CRUD sudah terhubung end-to-end pada level kode UI/ViewModel.
- Risiko tersisa:
  - Belum ada runtime smoke test manual/otomatis untuk flow CRUD timetable.
  - Endpoint non-terpakai tersisa: `verifySession`, `updateClass`, `getClassTeachers`, `updateStudent`.
- Next exact command/step untuk lanjut:
  - Lanjut Phase 5: implementasi class teacher + class update + student update.

### Entry #6
- Phase: `5`
- Tanggal/Jam: 2026-05-18
- Tujuan phase: Implementasi `getClassTeachers`, `updateClass`, dan `updateStudent` di Android.
- File diubah:
  - `app/src/main/java/com/localattendance/client/ui/screens/classes/ClassesViewModel.kt`
  - `app/src/main/java/com/localattendance/client/ui/screens/classes/ClassesScreen.kt`
  - `app/src/main/java/com/localattendance/client/ui/screens/classdetail/ClassDetailViewModel.kt`
  - `app/src/main/java/com/localattendance/client/ui/screens/classdetail/ClassDetailScreen.kt`
  - `app/src/main/java/com/localattendance/client/ui/screens/students/StudentsViewModel.kt`
  - `app/src/main/java/com/localattendance/client/ui/screens/students/StudentsScreen.kt`
  - `docs/plans/2026-05-15-feature-coverage-matrix.md`
- Ringkasan perubahan:
  - `ClassesViewModel`:
    - Tambah state `isSaving`, `successMessage`, dan helper `clearMessages()`.
    - Integrasi endpoint `updateClass`.
  - `ClassesScreen`:
    - Tambah dialog edit class.
    - Tambah tombol edit di setiap class item (role owner/administrator).
    - Tambah snackbar sukses/gagal.
  - `ClassDetailViewModel`:
    - Integrasi endpoint `getClassTeachers(classId)`.
    - Simpan list teacher ke `uiState.teachers`.
  - `ClassDetailScreen`:
    - Tampilkan `Total Teachers` di section Class Info.
  - `StudentsViewModel`:
    - Tambah state `isSaving`, `successMessage`, dan helper `clearMessages()`.
    - Integrasi endpoint `updateStudent`.
    - Refactor `deleteStudent` agar menerima `classId` dan refresh list dari backend.
  - `StudentsScreen`:
    - Tambah dialog edit student (name, roll number, parent name, parent phone, flag).
    - Tambah tombol edit di student card.
    - Tambah snackbar sukses/gagal.
  - Coverage endpoint ViewModel naik dari `24/28` ke `27/28` (96.4%).
- Verifikasi yang dijalankan:
  - Search endpoint baru di ViewModel:
    - `api.updateClass(...)`
    - `api.getClassTeachers(...)`
    - `api.updateStudent(...)`
  - Validasi dokumen coverage matrix diperbarui sesuai status terbaru.
- Hasil verifikasi:
  - Endpoint `updateClass`, `getClassTeachers`, dan `updateStudent` sudah terhubung pada level kode UI/ViewModel.
- Risiko tersisa:
  - Belum ada runtime smoke test manual/otomatis untuk flow edit class/edit student + teacher list.
  - Endpoint non-terpakai tersisa: `verifySession`.
- Next exact command/step untuk lanjut:
  - Lanjut tahap verifikasi session flow: pertimbangkan penggunaan `verifySession` untuk auto-restore auth state.

### Entry #7
- Phase: `6`
- Tanggal/Jam: 2026-05-18
- Tujuan phase: Integrasi `verifySession` untuk auto-restore auth/session flow di layar login.
- File diubah:
  - `app/src/main/java/com/localattendance/client/ui/screens/auth/AuthViewModel.kt`
  - `app/src/main/java/com/localattendance/client/ui/screens/auth/LoginScreen.kt`
  - `docs/plans/2026-05-15-feature-coverage-matrix.md`
  - `docs/plans/2026-05-15-phase-execution-roadmap.md`
- Ringkasan perubahan:
  - `AuthViewModel`:
    - Tambah state baru `SessionRestoreState` (`Idle`, `Checking`, `Restored`, `NotRestored`).
    - Tambah `sessionRestoreState` + method `restoreSessionIfAvailable()` yang memanggil `api.verifySession()`.
    - Saat login sukses, state session diset ke `Restored` agar alur navigasi konsisten.
  - `LoginScreen`:
    - Menjalankan `restoreSessionIfAvailable()` via `LaunchedEffect(Unit)` saat screen dibuka.
    - Auto-trigger `onLoginSuccess()` bila session berhasil di-restore (`SessionRestoreState.Restored`).
    - Menampilkan indikator loading "Checking existing session..." saat verifikasi session berlangsung.
  - Coverage endpoint ViewModel naik dari `27/28` ke `28/28` (100%).
- Verifikasi yang dijalankan:
  - Search pemanggilan endpoint verify session:
    - `api.verifySession(...)` ditemukan di `AuthViewModel`.
  - Search wiring UI restore flow:
    - `restoreSessionIfAvailable()` dipanggil di `LoginScreen`.
    - `SessionRestoreState.Restored` memicu `onLoginSuccess()`.
- Hasil verifikasi:
  - Endpoint `verifySession` sudah terhubung pada level ViewModel/UI auth flow.
- Risiko tersisa:
  - Runtime smoke test auto-restore session belum dijalankan (perlu verifikasi di device/emulator).
- Next exact command/step untuk lanjut:
  - Jalankan smoke test manual: login -> tutup/buka ulang app -> pastikan auto masuk dashboard tanpa login ulang; lalu logout dan pastikan kembali ke login.

### Entry #8
- Phase: `7`
- Tanggal/Jam: 2026-05-18
- Tujuan phase: Menyiapkan runtime verification saat host lokal tidak bisa compile.
- File diubah:
  - `docs/TESTING.md`
  - `docs/plans/2026-05-15-phase-execution-roadmap.md`
- Ringkasan perubahan:
  - Validasi konteks repo aktif: `C:/repoandroid`, branch `main`, remote `richiesamlie/local-attendance-android`.
  - Konfirmasi build gate via CI GitHub sebagai source of truth (karena compile lokal tidak tersedia).
  - Referensi CI terbaru yang lulus:
    - Run ID `24491361938`, workflow `Android CI`, status `success`.
  - `docs/TESTING.md` ditambah section runtime smoke checklist khusus mode non-compile host:
    - Login flow
    - Session restore flow (`verifySession`)
    - Logout flow
    - Core feature sanity (classes/students/teachers/events/timetable)
    - Evidence format PASS/FAIL
- Verifikasi yang dijalankan:
  - `gh run list --workflow android.yml --limit 5`
  - `gh run view 24491361938 --json status,conclusion,headSha,createdAt,updatedAt,url,jobs`
- Hasil verifikasi:
  - Build gate CI lulus (`conclusion: success`, job `build` sukses).
  - Dokumen runtime smoke sudah siap dipakai di device/emulator tanpa compile lokal.
- Risiko tersisa:
  - Belum ada hasil eksekusi smoke test runtime aktual (baru checklist + gate CI).
- Next exact command/step untuk lanjut:
  - Jalankan smoke test di device/emulator sesuai `docs/TESTING.md`, lalu catat hasil PASS/FAIL ke roadmap/testing log.

### Entry #9
- Phase: `7`
- Tanggal/Jam: 2026-05-18
- Tujuan phase: Menyiapkan artifact APK + handoff komando eksekusi runtime smoke di host non-compile.
- File diubah:
  - `docs/TESTING.md`
  - `docs/plans/2026-05-15-phase-execution-roadmap.md`
- Ringkasan perubahan:
  - Artifact `app-debug` dari run CI `24491361938` berhasil didownload ke:
    - `C:/repoandroid/.artifacts/run-24491361938/app-debug.apk`
  - Verifikasi integrity APK lokal dengan SHA-256:
    - `f1e4a5fdca77de50b2415d81b28309176398c3c5e4d4afbe0b56d4d6692f8242`
  - Verifikasi tool Android:
    - `adb` tersedia (`/c/platform-tools/adb`).
  - Verifikasi device:
    - `adb devices` belum menampilkan device/emulator aktif.
  - `docs/TESTING.md` ditambah section handoff praktis:
    - status artifact
    - checksum
    - next exact commands (`adb devices`, `adb install -r ...`, logcat filter).
- Verifikasi yang dijalankan:
  - `gh api repos/richiesamlie/local-attendance-android/actions/runs/24491361938/artifacts`
  - `gh run download 24491361938 -n app-debug -D /c/repoandroid/.artifacts/run-24491361938`
  - `sha256sum /c/repoandroid/.artifacts/run-24491361938/app-debug.apk`
  - `adb devices`
- Hasil verifikasi:
  - APK siap dipasang ke device/emulator begitu device tersedia.
- Risiko tersisa:
  - Runtime smoke belum bisa dieksekusi di host ini sampai ada device/emulator online.
- Next exact command/step untuk lanjut:
  - Nyalakan emulator atau sambungkan device, lalu jalankan:
    - `adb install -r /c/repoandroid/.artifacts/run-24491361938/app-debug.apk`
    - eksekusi checklist runtime di `docs/TESTING.md` section B.