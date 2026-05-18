# Android-Backend Contract Alignment Implementation Plan

> Status update (2026-05-15): this plan is retained for historical context. Active execution roadmap has moved to `docs/plans/2026-05-15-phase-execution-roadmap.md` with backend-upstream/no-backend-change strategy and resumable phase handoff format.

> For Hermes: execute phase-by-phase; keep AGENT handoff notes updated after each phase.

Goal: make this Android app reliably work against `LocalAttendace-Final` backend with verified endpoint/payload compatibility.

Architecture: contract-first alignment. Freeze API matrix, pick canonical contract, patch Android client endpoints/models to follow backend contract (no backend changes), then validate with build + smoke tests.

Tech stack: Kotlin + Retrofit + OkHttp (Android), Node/Express + TypeScript + Zod (backend).

---

## Phase 0 — Discovery and Contract Freeze

Objective: lock a shared API truth before modifications.

Files:
- Created: `docs/plans/2026-05-15-api-contract-matrix.md`
- Create/modify: `docs/plans/2026-05-15-backend-android-alignment-plan.md`

Steps:
1. Compare all Retrofit paths in `AttendanceApi.kt` with backend routes in `/src/routes` + `routes.ts`.
2. Mark each endpoint as MATCH/MISMATCH with candidate equivalent route.
3. Identify payload type mismatches (e.g., timetable lesson type).
4. Freeze contract decision: keep Android/web-facing contract stable, patch backend composition.

Exit criteria:
- Matrix exists and includes all Android endpoints.
- Mismatch list is explicit and actionable.

---

## Phase 1 — Backend Route Composition Fixes

Objective: make backend expose expected paths under `/api`.

Files:
- Modify backend clone for PR prep:
  - `C:/Users/dewa5/AppData/Local/Temp/localattendance-backend/routes.ts`
  - Possibly route module files under `src/routes/*.ts` where needed.

Steps:
1. Re-map mounts so class-scoped endpoints resolve exactly as:
   - `/api/classes/:classId/students`
   - `/api/classes/:classId/records`
   - `/api/classes/:classId/events`
   - `/api/classes/:classId/timetable`
   - `/api/classes/:classId/daily-notes`
2. Ensure invites redeem resolves to `/api/invites/redeem` (single `invites` segment).
3. Ensure attendance save resolves to `/api/records`.
4. Keep existing auth and class endpoints stable.

Verification:
- Route listing/inspection confirms expected public paths.
- Existing auth/class behavior remains unchanged.

---

## Phase 2 — Payload/Schema Alignment

Objective: prevent 400 errors caused by data shape drift.

Files:
- Android: `app/src/main/java/com/localattendance/client/data/model/Models.kt`
- Android: any serializer/model mapping files if needed.
- Backend: `src/lib/validation.ts` (only if contract decision requires backend type change).

Steps:
1. Align timetable `lesson` field type between Android and backend.
2. Validate student, records, notes, events payload fields against zod schemas.
3. Add serialization annotations if naming mismatch appears.

Verification:
- Contract matrix updates from MISMATCH to MATCH for payload compatibility items.

---

## Phase 3 — Test & Build Verification

Objective: establish repeatable checks.

Files:
- Android docs: `docs/TESTING.md` (update with concrete commands)
- Optional backend test notes in plan docs.

Steps:
1. Backend smoke tests for key routes (health/login/classes/students/records/events/notes/timetable/invites).
2. Android compile check: `./gradlew :app:compileDebugKotlin`.
3. Android package check: `./gradlew :app:assembleDebug`.
4. Manual LAN test: set server URL in app settings and verify end-to-end login + data read/write.

Verification:
- All smoke checks pass.
- Debug APK builds successfully.

---

## Phase 4 — Documentation Accuracy

Objective: remove stale architecture assumptions.

Files:
- `docs/ARCHITECTURE.md`
- `README.md`

Steps:
1. Update auth description to cookie-based session flow (not OAuth2).
2. Update state management wording to reflect current Compose/ViewModel usage.
3. Add clear “connect to local backend” steps and troubleshooting.

Verification:
- Docs reflect implemented system accurately.

---

## Execution Notes

- Current blocker in this environment: `JAVA_HOME` is not set, so Android compile/build verification is pending until JDK path is configured.
- Contract mismatch is currently the primary functional risk; fix this before UI-level debugging.

## Deliverables

1. `docs/plans/2026-05-15-api-contract-matrix.md`
2. Backend route alignment patch set
3. Android model/schema alignment patch set
4. Updated test/build runbook
5. Updated architecture/readme docs
