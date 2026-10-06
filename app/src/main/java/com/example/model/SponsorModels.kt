package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sponsors")
data class Sponsor(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val phone: String,
    val organization: String? = null,
    val sponsoredStudentAdmissionNo: String, // Linked student
    val monthlyPledgeAmount: Double,
    val currency: String = "USD",
    val activeSince: String,
    val status: String = "ACTIVE", // ACTIVE, PAUSED, COMPLETED
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "donations")
data class DonationRecord(
    @PrimaryKey val id: String,
    val sponsorId: String,
    val sponsorName: String,
    val studentAdmissionNo: String,
    val amount: Double,
    val currency: String = "USD",
    val date: String,
    val purpose: String, // e.g. "Monthly Tuition & Nutrition", "Winter Uniforms", "Medical Care"
    val receiptNumber: String,
    val syncStatus: String = "SYNCED", // SYNCED, PENDING
    val timestamp: Long = System.currentTimeMillis()
)

data class MonthlyDonorReport(
    val reportId: String,
    val monthYear: String,
    val sponsorName: String,
    val studentName: String,
    val studentAdmissionNo: String,
    val gradeLevel: String,
    val totalPledged: Double,
    val totalReceived: Double,
    val currency: String,
    val academicSummary: String,
    val attendancePercentage: Int,
    val healthAndWelfareNotes: String,
    val academyMessage: String
)
