package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "attendance_records")
data class AttendanceRecord(
    @PrimaryKey val id: String,
    val personId: String, // student admissionNo or teacher ID
    val personName: String,
    val userRole: UserRole,
    val date: String, // e.g. "2026-10-06"
    val checkInTime: String, // e.g. "08:15 AM"
    val checkOutTime: String? = null,
    val status: String = "PRESENT", // PRESENT, LATE, ABSENT, LEAVE, OUT_OF_COVERAGE
    val checkInLatitude: Double? = null,
    val checkInLongitude: Double? = null,
    val checkInLocationName: String = "Main Campus - Gate A",
    val checkInDistanceMeters: Float = 0f,
    val isWithinGeofence: Boolean = true,
    val assignedRadiusMeters: Double = 150.0,
    val checkOutLatitude: Double? = null,
    val checkOutLongitude: Double? = null,
    val checkOutLocationName: String? = null,
    val checkOutDistanceMeters: Float = 0f,
    val syncStatus: String = "SYNCED", // SYNCED, PENDING
    val timestamp: Long = System.currentTimeMillis()
)

data class AcademyGeofence(
    val latitude: Double = 24.8607,
    val longitude: Double = 67.0011,
    val radiusMeters: Double = 250.0,
    val campusName: String = "Orphan's Academy Main Campus"
)
