package com.example.data

import androidx.room.TypeConverter
import com.example.model.UserRole

class Converters {
    @TypeConverter
    fun fromUserRole(role: UserRole?): String? {
        return role?.name
    }

    @TypeConverter
    fun toUserRole(value: String?): UserRole {
        return UserRole.fromString(value)
    }
}
