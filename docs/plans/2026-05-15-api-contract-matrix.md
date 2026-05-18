# Android ↔ Backend API Contract Matrix (Backend-authoritative)

- Generated: 2026-05-15T13:16:21
- Android source: `C:\repoandroid\app\src\main\java\com\localattendance\client\data\api\AttendanceApi.kt`
- Backend source: `C:\Users\dewa5\AppData\Local\Temp\localattendance-backend-upstream` (commit faa0744)
- Contract policy: Android client follows backend API as-is (no backend changes).

## Summary
- Android endpoints checked: 28
- Exact matches: 28
- Mismatches: 0

## Matrix

| Method | Android Endpoint | Status | Backend Candidate(s) | Android Function |
|---|---|---|---|---|
| POST | `/api/auth/login` | MATCH | — | `login` |
| GET | `/api/auth/verify` | MATCH | — | `verifySession` |
| GET | `/api/health` | MATCH | — | `healthCheck` |
| GET | `/api/auth/me` | MATCH | — | `getMe` |
| POST | `/api/auth/logout` | MATCH | — | `logout` |
| GET | `/api/classes` | MATCH | — | `getClasses` |
| POST | `/api/classes` | MATCH | — | `createClass` |
| PUT | `/api/classes/{id}` | MATCH | — | `updateClass` |
| DELETE | `/api/classes/{id}` | MATCH | — | `deleteClass` |
| GET | `/api/classes/{classId}/teachers` | MATCH | — | `getClassTeachers` |
| GET | `/api/students/{classId}/students` | MATCH | — | `getStudents` |
| POST | `/api/students/{classId}/students` | MATCH | — | `addStudent` |
| PUT | `/api/students/{id}` | MATCH | — | `updateStudent` |
| DELETE | `/api/students/{id}` | MATCH | — | `deleteStudent` |
| GET | `/api/records/classes/{classId}/records` | MATCH | — | `getAttendanceRecords` |
| POST | `/api/records` | MATCH | — | `saveAttendance` |
| GET | `/api/notes/classes/{classId}/daily-notes` | MATCH | — | `getDailyNotes` |
| POST | `/api/notes/classes/{classId}/daily-notes` | MATCH | — | `saveDailyNote` |
| GET | `/api/events/classes/{classId}/events` | MATCH | — | `getEvents` |
| POST | `/api/events/classes/{classId}/events` | MATCH | — | `addEvent` |
| PUT | `/api/events/{id}` | MATCH | — | `updateEvent` |
| DELETE | `/api/events/{id}` | MATCH | — | `deleteEvent` |
| GET | `/api/timetable/classes/{classId}/timetable` | MATCH | — | `getTimetable` |
| POST | `/api/timetable/classes/{classId}/timetable` | MATCH | — | `addTimetableSlot` |
| PUT | `/api/timetable/{id}` | MATCH | — | `updateTimetableSlot` |
| DELETE | `/api/timetable/{id}` | MATCH | — | `deleteTimetableSlot` |
| POST | `/api/sessions/revoke` | MATCH | — | `revokeSessions` |
| POST | `/api/invites/invites/redeem` | MATCH | — | `redeemInvite` |

## Result
- All Android API endpoints now match backend routes from LocalAttendace-Final.
