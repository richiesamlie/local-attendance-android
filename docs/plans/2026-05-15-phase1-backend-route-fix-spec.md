# Phase 1 Spec — Backend Route Composition Fix (SUPERSEDED)

Status:
Dokumen ini tidak dipakai sesuai arahan terbaru user.
Keputusan final: repoandroid mengikuti backend `LocalAttendace-Final` apa adanya, tanpa mengubah backend server.

Rujukan aktif:
- `docs/plans/2026-05-15-backend-android-alignment-plan.md`
- `docs/plans/2026-05-15-api-contract-matrix.md`

---

Tujuan awal (sudah dibatalkan):

`routes.ts` melakukan mount per modul:
- `/classes` -> classRouter
- `/students` -> studentRouter
- `/records` -> recordRouter
- `/notes` -> noteRouter
- `/events` -> eventRouter
- `/timetable` -> timetableRouter
- `/invites` -> inviteRouter

Sementara beberapa route di file modul sudah mengandung prefix `classes/:classId/...`.
Akibatnya path jadi dobel/bergeser, misalnya:
- actual: `/api/students/:classId/students`
- expected: `/api/classes/:classId/students`

---

## 2) Kontrak endpoint yang harus dipenuhi

Wajib match dengan Android (`AttendanceApi.kt`):

- GET `/api/classes/:classId/students`
- POST `/api/classes/:classId/students`
- GET `/api/classes/:classId/records`
- POST `/api/records`
- GET `/api/classes/:classId/daily-notes`
- POST `/api/classes/:classId/daily-notes`
- GET `/api/classes/:classId/events`
- POST `/api/classes/:classId/events`
- GET `/api/classes/:classId/timetable`
- POST `/api/classes/:classId/timetable`
- POST `/api/invites/redeem`

---

## 3) Strategi patch (backend)

### Opsi A (disarankan, minimal invasive)
Tetap mount modul seperti sekarang, tapi perbaiki path di modul yang salah agar RELATIVE ke mount point.

Contoh target perubahan:

1. `src/routes/student.routes.ts`
- dari:
  - `get('/:classId/students', ...)`
  - `post('/:classId/students', ...)`
- menjadi:
  - `get('/classes/:classId/students', ...)` jika dipindah mount ke root
  ATAU
  - tetap mount `/students` tapi Android diubah (tidak disarankan)

2. `src/routes/record.routes.ts`
- pastikan kombinasi mount + path menghasilkan:
  - GET `/api/classes/:classId/records`
  - POST `/api/records`

3. `src/routes/note.routes.ts`
- hasil akhir:
  - GET `/api/classes/:classId/daily-notes`
  - POST `/api/classes/:classId/daily-notes`

4. `src/routes/event.routes.ts`
- hasil akhir:
  - GET `/api/classes/:classId/events`
  - POST `/api/classes/:classId/events`

5. `src/routes/timetable.routes.ts`
- hasil akhir:
  - GET `/api/classes/:classId/timetable`
  - POST `/api/classes/:classId/timetable`

6. `src/routes/invite.routes.ts`
- hilangkan duplikasi `invites`:
  - hasil akhir harus: POST `/api/invites/redeem`

Catatan:
Secara praktis, ini paling aman jika semua route class-scoped dipusatkan di classRouter ATAU semua mount class-scoped dipindah ke root `/` lalu path eksplisit `/classes/:classId/...` di masing-masing modul.

### Opsi B
Ubah Android endpoint mengikuti backend saat ini.
- Tidak disarankan, karena memperbesar drift dengan web client contract.

---

## 4) Verifikasi wajib setelah patch backend

Jalankan di repo backend:

1) Unit/integration test (kalau ada)
2) Smoke cURL minimal:
- `GET /api/health`
- `POST /api/auth/login`
- `GET /api/classes`
- `GET /api/classes/{id}/students`
- `POST /api/records`
- `POST /api/invites/redeem`

3) Regenerate matrix terhadap Android repo ini:
- target mismatch turun dari 10 -> 0

---

## 5) Risiko dan guardrail

- Risiko: break web UI existing jika web selama ini menyesuaikan path yang salah.
- Mitigasi:
  1. tambahkan backward-compatible alias route sementara (deprecation window)
  2. atau patch web client bersamaan dalam satu PR backend

---

## 6) Next immediate execution

1. Clone backend branch kerja (`fix/api-contract-android`)
2. Implement route composition fix
3. Jalankan smoke tests
4. Update matrix di repoandroid ini
5. Buat PR backend dengan daftar endpoint before/after
