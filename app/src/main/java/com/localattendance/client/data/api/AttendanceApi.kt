package com.localattendance.client.data.api

import com.localattendance.client.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface AttendanceApi {
    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/auth/verify")
    suspend fun verifySession(): Response<Map<String, Any>>

    @GET("api/health")
    suspend fun healthCheck(): Response<Map<String, Any>>

    @GET("api/auth/me")
    suspend fun getMe(): Response<Teacher>

    @POST("api/auth/logout")
    suspend fun logout(): Response<Map<String, Any>>

    // Classes
    @GET("api/classes")
    suspend fun getClasses(): Response<List<ClassRoom>>

    @POST("api/classes")
    suspend fun createClass(@Body request: Map<String, String>): Response<ClassRoom>

    @PUT("api/classes/{id}")
    suspend fun updateClass(@Path("id") id: String, @Body request: Map<String, String>): Response<Map<String, Any>>

    @DELETE("api/classes/{id}")
    suspend fun deleteClass(@Path("id") id: String): Response<Map<String, Any>>

    @GET("api/classes/{classId}/teachers")
    suspend fun getClassTeachers(@Path("classId") classId: String): Response<List<Teacher>>

    // Students
    @GET("api/classes/{classId}/students")
    suspend fun getStudents(
        @Path("classId") classId: String,
        @Query("includeArchived") includeArchived: Boolean = false
    ): Response<List<Student>>

    @POST("api/classes/{classId}/students")
    suspend fun addStudent(
        @Path("classId") classId: String,
        @Body student: Student
    ): Response<Map<String, Any>>

    @PUT("api/students/{id}")
    suspend fun updateStudent(
        @Path("id") id: String,
        @Body request: Map<String, Any>
    ): Response<Map<String, Any>>

    @DELETE("api/students/{id}")
    suspend fun deleteStudent(@Path("id") id: String): Response<Map<String, Any>>

    // Attendance Records
    @GET("api/classes/{classId}/records")
    suspend fun getAttendanceRecords(@Path("classId") classId: String): Response<List<AttendanceRecord>>

    @POST("api/records")
    suspend fun saveAttendance(@Body records: List<AttendanceRecord>): Response<Map<String, Any>>

    // Daily Notes
    @GET("api/classes/{classId}/daily-notes")
    suspend fun getDailyNotes(@Path("classId") classId: String): Response<Map<String, String>>

    @POST("api/classes/{classId}/daily-notes")
    suspend fun saveDailyNote(
        @Path("classId") classId: String,
        @Body request: Map<String, String>
    ): Response<Map<String, Any>>

    // Events
    @GET("api/classes/{classId}/events")
    suspend fun getEvents(@Path("classId") classId: String): Response<List<Event>>

    @POST("api/classes/{classId}/events")
    suspend fun addEvent(
        @Path("classId") classId: String,
        @Body event: Event
    ): Response<Map<String, Any>>

    @PUT("api/events/{id}")
    suspend fun updateEvent(
        @Path("id") id: String,
        @Body request: Map<String, Any>
    ): Response<Map<String, Any>>

    @DELETE("api/events/{id}")
    suspend fun deleteEvent(@Path("id") id: String): Response<Map<String, Any>>

    // Timetable
    @GET("api/classes/{classId}/timetable")
    suspend fun getTimetable(@Path("classId") classId: String): Response<List<TimetableSlot>>

    @POST("api/classes/{classId}/timetable")
    suspend fun addTimetableSlot(
        @Path("classId") classId: String,
        @Body slot: TimetableSlot
    ): Response<Map<String, Any>>

    @PUT("api/timetable/{id}")
    suspend fun updateTimetableSlot(
        @Path("id") id: String,
        @Body request: Map<String, Any>
    ): Response<Map<String, Any>>

    @DELETE("api/timetable/{id}")
    suspend fun deleteTimetableSlot(@Path("id") id: String): Response<Map<String, Any>>

    // Sessions
    @POST("api/sessions/revoke")
    suspend fun revokeSessions(@Body request: Map<String, String>): Response<Map<String, Any>>

    // Invites
    @POST("api/invites/redeem")
    suspend fun redeemInvite(@Body request: Map<String, String>): Response<Map<String, Any>>
}
