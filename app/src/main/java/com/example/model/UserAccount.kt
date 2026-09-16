package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey val uid: String,
    val email: String,
    val passwordHash: String,
    val displayName: String,
    val role: UserRole,
    val assignedLocation: String? = null,
    val studentAdmissionNo: String? = null,
    val isPendingCloudSync: Boolean = false,
    val lastLoginTimestamp: Long = System.currentTimeMillis()
)
