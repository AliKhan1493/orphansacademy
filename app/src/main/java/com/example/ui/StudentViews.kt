package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ExamTest
import com.example.model.Student
import com.example.model.SyllabusTopic
import com.example.model.TestRecord
import com.example.model.UserAccount
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyBlueDark
import com.example.ui.theme.AcademyGold
import com.example.ui.theme.RoleStudentColor
import com.example.ui.theme.Slate900

@Composable
fun StudentDigitalIdTab(
    currentUser: UserAccount,
    studentProfile: Student?
) {
    // If no explicit student profile found for admissionNo, build fallback from account
    val student = studentProfile ?: Student(
        id = currentUser.uid,
        admissionNo = currentUser.studentAdmissionNo ?: "OA-2026-042",
        fullName = currentUser.displayName,
        gradeLevel = "Grade 10 - STEM",
        assignedLocation = currentUser.assignedLocation ?: "Campus North Wing - Room 101",
        guardianName = "Mrs. Fatima Tariq",
        guardianContact = "+1 555-234-5678",
        bloodGroup = "O+",
        emergencyContact = "+1 555-901-2345"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "OFFICIAL DIGITAL STUDENT CREDENTIALS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AcademyBlueDark,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            StudentIdCardView(student = student)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = AcademyBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Institutional Security & Verification",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = AcademyBlueDark
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "This digital card represents your active enrollment in Orphan's Academy. Present this at meal distribution counters, library checkouts, and laboratory admissions.",
                        fontSize = 11.sp,
                        color = Color(0xFF334155)
                    )
                }
            }
        }
    }
}

@Composable
fun StudentSyllabusTab(
    syllabusTopics: List<SyllabusTopic>,
    tests: List<ExamTest>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Upcoming Exam Schedule section
        item {
            Text(
                text = "UPCOMING EXAM SCHEDULE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AcademyBlueDark,
                letterSpacing = 1.sp
            )
        }

        items(tests) { test ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_exam_schedule_${test.id}"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF3C7)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = Color(0xFFD97706)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = test.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate900
                        )
                        Text(
                            text = "${test.subject} • Date: ${test.examDate} • ${test.durationMinutes} Mins",
                            fontSize = 11.sp,
                            color = AcademyBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = test.instructions,
                            fontSize = 10.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        // Assigned Syllabus Topics
        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "ASSIGNED CURRICULAR SYLLABUS (${syllabusTopics.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AcademyBlueDark,
                letterSpacing = 1.sp
            )
        }

        items(syllabusTopics) { topic ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_syllabus_${topic.id}"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (topic.isCompleted) Color(0xFFDCFCE7) else Color(0xFFE0E7FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (topic.isCompleted) Icons.Default.CheckCircle else Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = if (topic.isCompleted) Color(0xFF166534) else Color(0xFF4338CA),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = topic.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = Slate900
                        )
                        Text(
                            text = "${topic.subject} • ${topic.gradeLevel} • ${topic.term}",
                            fontSize = 11.sp,
                            color = AcademyBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = topic.learningObjectives,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StudentExamResultsTab(
    studentAdmissionNo: String,
    allRecords: List<TestRecord>
) {
    val myRecords = allRecords.filter {
        it.studentAdmissionNo.equals(studentAdmissionNo, ignoreCase = true) || studentAdmissionNo.isBlank()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "MY OFFICIAL PUBLISHED TEST MARKS & REMARKS (${myRecords.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AcademyBlueDark,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        if (myRecords.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No exam marks published yet for Admission ID: $studentAdmissionNo",
                            color = Color(0xFF64748B),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(myRecords) { record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_result_${record.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDCFCE7)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${record.marksObtained}/${record.totalMarks}",
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF166534),
                                fontSize = 13.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = record.testTitle,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Slate900
                            )
                            Text(
                                text = "Score: ${((record.marksObtained.toFloat() / record.totalMarks) * 100).toInt()}% • Verified Grade",
                                fontSize = 12.sp,
                                color = AcademyBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "Faculty Remarks: \"${record.remarks}\"",
                                fontSize = 11.sp,
                                color = Color(0xFF475569)
                            )
                        }
                    }
                }
            }
        }
    }
}
