package com.localattendance.client.data.api

import com.localattendance.client.data.model.*
import retrofit2.http.*

interface AttendanceApi {
    // Auth
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @GET("api/auth/verify")
    suspend fun verifySession(): retrofit2.Response<Unit>

    @GET("api/auth/me")
    suspend fun getMe(): Teacher

    @POST("api/auth/logout")
    suspend fun logout(): retrofit2.Response<Unit>

    // Classes
    @GET("api/classes")
    suspend fun getClasses(): List<ClassRoom>

    @POST("api/classes")
    suspend fun createClass(@Body classRoom: ClassRoom): ClassRoom

    @PUT("api/classes/{id}")
    suspend fun updateClass(@Path("id") id: String, @Body classRoom: ClassRoom): ClassRoom

    @DELETE("api/classes/{id}")
    suspend fun deleteClass(@Path("id") id: String): retrofit2.Response<Unit>

    // Students
    @GET("api/classes/{classId}/students")
    suspend fun getStudents(@Path("classId") classId: String): List<Student>

    @POST("api/classes/{classId}/students")
    suspend fun addStudent(@Path("classId") classId: String, @Body student: Student): Student

    @PUT("api/students/{id}")
    suspend fun updateStudent(@Path("id") id: String, @Body student: Student): Student

    @DELETE("api/students/{id}")
    suspend fun deleteStudent(@Path("id") id: String): retrofit2.Response<Unit>

    // Attendance
    @GET("api/classes/{classId}/records")
    suspend fun getAttendanceRecords(
        @Path("classId") classId: String,
        @Query("date") date: String? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): List<AttendanceRecord>

    @POST("api/records")
    suspend fun saveAttendance(@Body records: List<AttendanceRecord>): retrofit2.Response<Unit>
}
