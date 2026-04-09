package com.localattendance.client.data.model

data class Teacher(
    val id: String,
    val username: String,
    val name: String,
    val isAdmin: Boolean = false
)

data class ClassRoom(
    val id: String,
    val name: String,
    val teacherId: String? = null,
    val ownerName: String? = null,
    val role: String? = null
)

data class Student(
    val id: String,
    val name: String,
    val rollNumber: String,
    val parentName: String? = null,
    val parentPhone: String? = null,
    val isFlagged: Boolean = false,
    val isArchived: Boolean = false
)

data class AttendanceRecord(
    val studentId: String,
    val classId: String,
    val date: String,
    val status: String,
    val reason: String? = null
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class LoginResponse(
    val success: Boolean,
    val teacherId: String? = null,
    val username: String? = null,
    val name: String? = null,
    val isAdmin: Boolean = false,
    val error: String? = null
)

data class Event(
    val id: String,
    val date: String,
    val title: String,
    val type: String,
    val description: String? = null
)

data class TimetableSlot(
    val id: String,
    val dayOfWeek: Int,
    val startTime: String,
    val endTime: String,
    val subject: String,
    val lesson: Int
)

data class DailyNote(
    val date: String,
    val note: String
)
