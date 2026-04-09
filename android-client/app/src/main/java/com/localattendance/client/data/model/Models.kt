package com.localattendance.client.data.model

data class Teacher(
    val teacherId: String,
    val username: String,
    val name: String
)

data class ClassRoom(
    val id: String,
    val name: String
)

data class Student(
    val id: String,
    val name: String,
    val rollNumber: String,
    val email: String? = null
)

data class AttendanceRecord(
    val studentId: String,
    val classId: String,
    val date: String,
    val status: String, // Present, Absent, Sick, Late
    val reason: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val teacherId: String,
    val username: String,
    val name: String
)
