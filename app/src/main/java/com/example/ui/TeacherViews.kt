package com.example.ui

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.ui.theme.RoleTeacherColor
import com.example.ui.theme.Slate900
import com.example.util.PdfGenerator
import java.util.UUID

@Composable
fun TeacherLocationTab(
    currentUser: UserAccount,
    syllabusTopics: List<SyllabusTopic>,
    onAddSyllabusTopic: (SyllabusTopic) -> Unit,
    onToggleTopicCompleted: (SyllabusTopic) -> Unit
) {
    var showAddTopicDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Teacher Assigned Location & Classroom Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("teacher_location_card"),
                colors = CardDefaults.cardColors(containerColor = RoleTeacherColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.MeetingRoom,
                                contentDescription = null,
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ASSIGNED CLASSROOM & LOCATION",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = currentUser.assignedLocation ?: "Campus Science Hall 204",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Faculty In-Charge: ${currentUser.displayName}",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "Authorized for syllabus updates, exam formulation, and student assessments.",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Syllabus section header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SYLLABUS MODULES & OBJECTIVES (${syllabusTopics.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AcademyBlueDark,
                    letterSpacing = 1.sp
                )
                OutlinedButton(
                    onClick = { showAddTopicDialog = true },
                    modifier = Modifier.testTag("add_syllabus_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add Module", fontSize = 11.sp)
                }
            }
        }

        items(syllabusTopics) { topic ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("syllabus_card_${topic.id}"),
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
                    IconButton(
                        onClick = { onToggleTopicCompleted(topic) },
                        modifier = Modifier.testTag("toggle_topic_${topic.id}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(if (topic.isCompleted) Color(0xFF10B981) else Color(0xFFE2E8F0)),
                            contentAlignment = Alignment.Center
                        ) {
                            if (topic.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = topic.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = Slate900
                            )
                        }
                        Text(
                            text = "${topic.subject} • ${topic.gradeLevel} • ${topic.term}",
                            fontSize = 11.sp,
                            color = AcademyBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = topic.learningObjectives,
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }

    if (showAddTopicDialog) {
        AddSyllabusDialog(
            onDismiss = { showAddTopicDialog = false },
            onConfirm = { topic ->
                onAddSyllabusTopic(topic)
                showAddTopicDialog = false
            }
        )
    }
}

@Composable
fun AddSyllabusDialog(
    onDismiss: () -> Unit,
    onConfirm: (SyllabusTopic) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Physics") }
    var grade by remember { mutableStateOf("Grade 10 - STEM") }
    var term by remember { mutableStateOf("Term 2") }
    var objectives by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Syllabus Module", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Module / Topic Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_syllabus_title_input")
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = objectives,
                    onValueChange = { objectives = it },
                    label = { Text("Learning Objectives & Deliverables") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            SyllabusTopic(
                                id = "syl_${UUID.randomUUID().toString().take(8)}",
                                subject = subject,
                                title = title,
                                gradeLevel = grade,
                                term = term,
                                learningObjectives = objectives.ifBlank { "Standard core curricular competence." },
                                assignedTeacherId = "current",
                                isCompleted = false
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Add to Syllabus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun TeacherStudentsRosterTab(students: List<Student>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "ASSIGNED CLASSROOM STUDENT ROSTER (${students.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AcademyBlueDark,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        items(students) { student ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("teacher_student_row_${student.admissionNo}"),
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFCCFBF1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = student.fullName.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = RoleTeacherColor,
                            fontSize = 14.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = student.fullName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Slate900
                        )
                        Text(
                            text = "Admission ID: ${student.admissionNo} • ${student.gradeLevel}",
                            fontSize = 12.sp,
                            color = AcademyBlue
                        )
                        Text(
                            text = "Assigned Location: ${student.assignedLocation}",
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
fun TeacherTestsAndPdfTab(
    tests: List<ExamTest>,
    onAddTest: (ExamTest) -> Unit,
    teacherName: String
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "MY EXAMS & PRINTABLE A4 PDF GENERATOR (${tests.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AcademyBlueDark,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(tests) { test ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_exam_${test.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = test.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Slate900
                        )
                        Text(
                            text = "${test.subject} • ${test.totalMarks} Marks • ${test.durationMinutes} Mins",
                            fontSize = 12.sp,
                            color = AcademyBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Instructions: ${test.instructions}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // A4 PDF Generation Action
                        Button(
                            onClick = {
                                try {
                                    val pdf = PdfGenerator.generateExamPdf(context, test, teacherName)
                                    Toast.makeText(
                                        context,
                                        "A4 PDF Ready: ${pdf.name} (595x842 pt)",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("teacher_generate_a4_pdf_${test.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = RoleTeacherColor),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(imageVector = Icons.Default.PictureAsPdf, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Printable A4 Exam PDF (595 x 842 pt)")
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("teacher_add_test_fab"),
            containerColor = RoleTeacherColor,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Create Test")
        }
    }

    if (showAddDialog) {
        CreateTestDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { test ->
                onAddTest(test)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun TeacherTestRecordsTab(
    records: List<TestRecord>,
    students: List<Student>,
    tests: List<ExamTest>,
    onAddRecord: (TestRecord) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "STUDENT MARKS & REMARKS LEDGER (${records.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AcademyBlueDark,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(records) { record ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("record_card_${record.id}"),
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
                                .size(46.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFE0F2FE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${record.marksObtained}/${record.totalMarks}",
                                fontWeight = FontWeight.ExtraBold,
                                color = AcademyBlueDark,
                                fontSize = 12.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = record.studentName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Slate900
                            )
                            Text(
                                text = "${record.testTitle} • ID: ${record.studentAdmissionNo}",
                                fontSize = 12.sp,
                                color = AcademyBlue
                            )
                            Text(
                                text = "Faculty Remarks: \"${record.remarks}\"",
                                fontSize = 11.sp,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("add_marks_fab"),
            containerColor = RoleTeacherColor,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Grade, contentDescription = "Log Marks")
        }
    }

    if (showAddDialog) {
        LogMarksDialog(
            students = students,
            tests = tests,
            onDismiss = { showAddDialog = false },
            onConfirm = { record ->
                onAddRecord(record)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun LogMarksDialog(
    students: List<Student>,
    tests: List<ExamTest>,
    onDismiss: () -> Unit,
    onConfirm: (TestRecord) -> Unit
) {
    var selectedStudentIndex by remember { mutableStateOf(0) }
    var selectedTestIndex by remember { mutableStateOf(0) }
    var marks by remember { mutableStateOf("45") }
    var remarks by remember { mutableStateOf("Good understanding of theoretical principles.") }

    val currentStudent = students.getOrNull(selectedStudentIndex)
    val currentTest = tests.getOrNull(selectedTestIndex)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log & Grade Student Examination", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                if (currentStudent != null) {
                    Text(
                        text = "Student: ${currentStudent.fullName} (${currentStudent.admissionNo})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                if (currentTest != null) {
                    Text(
                        text = "Test: ${currentTest.title} (Max: ${currentTest.totalMarks})",
                        fontSize = 12.sp,
                        color = AcademyBlue
                    )
                }
                OutlinedTextField(
                    value = marks,
                    onValueChange = { marks = it },
                    label = { Text("Marks Obtained") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("marks_input")
                )
                OutlinedTextField(
                    value = remarks,
                    onValueChange = { remarks = it },
                    label = { Text("Teacher Remarks & Recommendations") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth().testTag("remarks_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (currentStudent != null && currentTest != null) {
                        onConfirm(
                            TestRecord(
                                id = "rec_${UUID.randomUUID().toString().take(8)}",
                                testId = currentTest.id,
                                testTitle = currentTest.title,
                                studentAdmissionNo = currentStudent.admissionNo,
                                studentName = currentStudent.fullName,
                                marksObtained = marks.toIntOrNull() ?: 45,
                                totalMarks = currentTest.totalMarks,
                                remarks = remarks
                            )
                        )
                    }
                },
                modifier = Modifier.testTag("confirm_log_marks_button")
            ) {
                Text("Save Assessment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
