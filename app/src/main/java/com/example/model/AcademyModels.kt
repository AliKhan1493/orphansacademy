package com.example.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey val id: String,
    val admissionNo: String,
    val fullName: String,
    val gradeLevel: String,
    val assignedLocation: String,
    val guardianName: String,
    val guardianContact: String,
    val bloodGroup: String = "O+",
    val dob: String = "2012-05-14",
    val photoUri: String? = null,
    val emergencyContact: String = "+1 555-0199"
)

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val specialization: String,
    val assignedLocation: String,
    val activeClassroom: String,
    val contactNumber: String
)

@Entity(tableName = "syllabus_topics")
data class SyllabusTopic(
    @PrimaryKey val id: String,
    val subject: String,
    val title: String,
    val gradeLevel: String,
    val term: String,
    val learningObjectives: String,
    val assignedTeacherId: String,
    val isCompleted: Boolean = false
)

@Entity(tableName = "exam_tests")
data class ExamTest(
    @PrimaryKey val id: String,
    val title: String,
    val subject: String,
    val gradeLevel: String,
    val totalMarks: Int,
    val durationMinutes: Int,
    val examDate: String,
    val instructions: String,
    val questionsJson: String, // newline-delimited or JSON string of exam questions
    val createdByTeacherId: String
)

@Entity(tableName = "test_records")
data class TestRecord(
    @PrimaryKey val id: String,
    val testId: String,
    val testTitle: String,
    val studentAdmissionNo: String,
    val studentName: String,
    val marksObtained: Int,
    val totalMarks: Int,
    val remarks: String,
    val gradedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_features")
data class CustomFeature(
    @PrimaryKey val id: String,
    val name: String,
    val category: String, // e.g. "Inventory", "Rations", "Operations", "Health"
    val description: String,
    val status: String, // e.g. "Active", "In Stock", "Reviewed"
    val lastUpdated: Long = System.currentTimeMillis(),
    val updatedBy: String
)
