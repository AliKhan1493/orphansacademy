package com.example.model

enum class UserRole(val displayName: String, val badgeTitle: String) {
    ADMIN("Administrator", "Admin • Full Access"),
    MANAGER("Operations Manager", "Manager • Ops & Student Admin"),
    TEACHER("Faculty Teacher", "Teacher • Academic & Tests"),
    STUDENT("Enrolled Student", "Student • Portal");

    // Granular Permission Checking Helpers
    fun canManageTeachers(): Boolean = this == ADMIN
    fun canViewTeachers(): Boolean = this == ADMIN || this == MANAGER
    fun canManageStudents(): Boolean = this == ADMIN || this == MANAGER
    fun canViewAllStudents(): Boolean = this == ADMIN || this == MANAGER
    fun canGenerateIDCards(): Boolean = this == ADMIN || this == MANAGER
    fun canAccessFeatureRegistry(): Boolean = this == ADMIN || this == MANAGER
    fun canModifyFeatureRegistry(): Boolean = this == ADMIN || this == MANAGER
    fun canAccessAdminSettings(): Boolean = this == ADMIN
    fun canAssignRoles(): Boolean = this == ADMIN
    fun canCreateTests(): Boolean = this == ADMIN || this == TEACHER
    fun canGenerateA4Pdf(): Boolean = this == ADMIN || this == TEACHER
    fun canLogTestMarks(): Boolean = this == ADMIN || this == TEACHER
    fun canManageSyllabus(): Boolean = this == ADMIN || this == TEACHER
    fun canViewSyllabus(): Boolean = true
    fun canViewExamResults(): Boolean = true
    fun canViewPersonalDigitalId(): Boolean = this == STUDENT || this == ADMIN || this == MANAGER

    companion object {
        fun fromString(value: String?): UserRole {
            return when (value?.uppercase()?.trim()) {
                "ADMIN" -> ADMIN
                "MANAGER" -> MANAGER
                "TEACHER" -> TEACHER
                "STUDENT" -> STUDENT
                else -> STUDENT
            }
        }
    }
}
