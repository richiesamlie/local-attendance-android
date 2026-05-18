# Feature Coverage Matrix — Android Screen/ViewModel vs Backend Endpoint

Tanggal audit: 2026-05-15
Scope: `C:\repoandroid\app\src\main\java\com\localattendance\client\ui\screens\**\*ViewModel.kt`
Kontrak endpoint acuan: `docs/plans/2026-05-15-api-contract-matrix.md` (28 endpoint match)

## Ringkasan

- Total endpoint di `AttendanceApi`: 28
- Endpoint sudah dipanggil dari ViewModel: 28
- Endpoint belum dipanggil dari ViewModel: 0
- Coverage penggunaan endpoint di level ViewModel: 100%

Catatan: ini coverage pemanggilan endpoint dari kode UI/ViewModel, bukan coverage test runtime.

---

## A) Endpoint yang Sudah Dipakai (Implemented)

| API Function | Endpoint | Dipakai di ViewModel |
|---|---|---|
| `login` | `POST /api/auth/login` | `AuthViewModel` |
| `verifySession` | `GET /api/auth/verify` | `AuthViewModel` |
| `healthCheck` | `GET /api/health` | `ServerSettingsViewModel` |
| `getMe` | `GET /api/auth/me` | `DashboardViewModel`, `SettingsViewModel` |
| `logout` | `POST /api/auth/logout` | `SettingsViewModel` |
| `getClasses` | `GET /api/classes` | `DashboardViewModel`, `ClassesViewModel`, `ClassDetailViewModel` |
| `createClass` | `POST /api/classes` | `ClassesViewModel` |
| `updateClass` | `PUT /api/classes/{id}` | `ClassesViewModel` |
| `deleteClass` | `DELETE /api/classes/{id}` | `ClassesViewModel` |
| `getClassTeachers` | `GET /api/classes/{classId}/teachers` | `ClassDetailViewModel` |
| `getStudents` | `GET /api/students/{classId}/students` | `StudentsViewModel`, `ClassDetailViewModel`, `TakeAttendanceViewModel` |
| `addStudent` | `POST /api/students/{classId}/students` | `StudentsViewModel` |
| `updateStudent` | `PUT /api/students/{id}` | `StudentsViewModel` |
| `deleteStudent` | `DELETE /api/students/{id}` | `StudentsViewModel` |
| `getAttendanceRecords` | `GET /api/records/classes/{classId}/records` | `ReportsViewModel`, `TakeAttendanceViewModel` |
| `saveAttendance` | `POST /api/records` | `TakeAttendanceViewModel` |
| `getDailyNotes` | `GET /api/notes/classes/{classId}/daily-notes` | `DailyNotesViewModel` |
| `saveDailyNote` | `POST /api/notes/classes/{classId}/daily-notes` | `DailyNotesViewModel` |
| `getEvents` | `GET /api/events/classes/{classId}/events` | `EventsViewModel` |
| `addEvent` | `POST /api/events/classes/{classId}/events` | `EventsViewModel` |
| `updateEvent` | `PUT /api/events/{id}` | `EventsViewModel` |
| `deleteEvent` | `DELETE /api/events/{id}` | `EventsViewModel` |
| `getTimetable` | `GET /api/timetable/classes/{classId}/timetable` | `TimetableViewModel` |
| `addTimetableSlot` | `POST /api/timetable/classes/{classId}/timetable` | `TimetableViewModel` |
| `updateTimetableSlot` | `PUT /api/timetable/{id}` | `TimetableViewModel` |
| `deleteTimetableSlot` | `DELETE /api/timetable/{id}` | `TimetableViewModel` |
| `revokeSessions` | `POST /api/sessions/revoke` | `SettingsViewModel` |
| `redeemInvite` | `POST /api/invites/invites/redeem` | `JoinClassViewModel` |

---

## B) Endpoint Belum Dipakai di ViewModel (Missing/Partial)

Semua endpoint sudah dipakai di level ViewModel (`0 missing`).

---

## C) Coverage by Screen/ViewModel

| Screen/ViewModel | Endpoint yang dipakai | Status fitur |
|---|---|---|
| `AuthViewModel` | `login`, `verifySession` | Implemented |
| `ServerSettingsViewModel` | `healthCheck` | Implemented |
| `DashboardViewModel` | `getMe`, `getClasses` | Implemented |
| `SettingsViewModel` | `getMe`, `revokeSessions`, `logout` | Implemented |
| `ClassesViewModel` | `getClasses`, `createClass`, `updateClass`, `deleteClass` | Implemented |
| `ClassDetailViewModel` | `getClasses`, `getStudents`, `getClassTeachers` | Implemented |
| `StudentsViewModel` | `getStudents`, `addStudent`, `updateStudent`, `deleteStudent` | Implemented |
| `TakeAttendanceViewModel` | `getStudents`, `getAttendanceRecords`, `saveAttendance` | Implemented |
| `DailyNotesViewModel` | `getDailyNotes`, `saveDailyNote` | Implemented |
| `ReportsViewModel` | `getAttendanceRecords` | Implemented |
| `EventsViewModel` | `getEvents`, `addEvent`, `updateEvent`, `deleteEvent` | Implemented |
| `TimetableViewModel` | `getTimetable`, `addTimetableSlot`, `updateTimetableSlot`, `deleteTimetableSlot` | Implemented |
| `JoinClassViewModel` | `redeemInvite` | Implemented |

---

## D) Prioritas Implementasi Phase Berikutnya

1. Runtime smoke test session restore (`verifySession`) + flow login/logout lintas restart app.

---

## Bukti Query Audit (ringkas)

- CI list: `gh run list --workflow android.yml --limit 5`
- CI detail run: `gh run view 24491361938 --json status,conclusion,headSha,createdAt,updatedAt,url,jobs`
- Pemanggilan API di VM: regex `api\.([a-zA-Z0-9_]+)\(` pada `*ViewModel.kt`
- Daftar endpoint API: anotasi `@(GET|POST|PUT|DELETE)` dan `suspend fun` pada `AttendanceApi.kt`
