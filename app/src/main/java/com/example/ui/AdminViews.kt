package com.example.ui

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.core.content.FileProvider
import com.example.model.CustomFeature
import com.example.model.ExamTest
import com.example.model.Student
import com.example.model.Teacher
import com.example.model.UserAccount
import com.example.model.UserRole
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyBlueDark
import com.example.ui.theme.AcademyGold
import com.example.ui.theme.RoleAdminColor
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.util.PdfGenerator
import kotlinx.coroutines.launch
import java.util.UUID

@Composable
fun AdminTeachersTab(
    teachers: List<Teacher>,
    onAddTeacher: (Teacher, String) -> Unit,
    onDeleteTeacher: (Teacher) -> Unit
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
                    text = "ACADEMY FACULTY DIRECTORY (${teachers.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AcademyBlueDark,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(teachers) { teacher ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("teacher_card_${teacher.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
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
                                .background(Color(0xFFCCFBF1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = teacher.fullName.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F766E),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = teacher.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Slate900
                            )
                            Text(
                                text = teacher.specialization,
                                fontSize = 12.sp,
                                color = AcademyBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(top = 2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = Color(0xFF64748B),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${teacher.assignedLocation} • ${teacher.activeClassroom}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF64748B)
                                )
                            }
                            Text(
                                text = "Contact: ${teacher.contactNumber} | ${teacher.email}",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        IconButton(
                            onClick = { onDeleteTeacher(teacher) },
                            modifier = Modifier.testTag("delete_teacher_${teacher.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Teacher",
                                tint = Color(0xFFEF4444)
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
                .testTag("add_teacher_fab"),
            containerColor = AcademyBlueDark,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Teacher")
        }
    }

    if (showAddDialog) {
        AddTeacherDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { teacher, password ->
                onAddTeacher(teacher, password)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddTeacherDialog(
    onDismiss: () -> Unit,
    onConfirm: (Teacher, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var spec by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("Campus North Wing") }
    var classroom by remember { mutableStateOf("Room 201") }
    var contact by remember { mutableStateOf("+1 555-0100") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Appoint Faculty & Create Login", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_teacher_name_input")
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Faculty Email (Login ID)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_teacher_email_input")
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Initial Login Password") },
                    placeholder = { Text("e.g. Teacher@123") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_teacher_password_input")
                )
                OutlinedTextField(
                    value = spec,
                    onValueChange = { spec = it },
                    label = { Text("Specialization (e.g. Mathematics)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Assigned Campus Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = classroom,
                    onValueChange = { classroom = it },
                    label = { Text("Classroom / Lab") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank()) {
                        val pass = password.ifBlank { "Teacher@123" }
                        onConfirm(
                            Teacher(
                                id = "tch_${UUID.randomUUID().toString().take(8)}",
                                fullName = name,
                                email = email.trim(),
                                specialization = spec.ifBlank { "General Education" },
                                assignedLocation = location,
                                activeClassroom = classroom,
                                contactNumber = contact
                            ),
                            pass
                        )
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_teacher_button")
            ) {
                Text("Appoint & Create Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdminStudentsTab(
    students: List<Student>,
    onAddStudent: (Student) -> Unit,
    onDeleteStudent: (Student) -> Unit
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
                    text = "ENROLLED STUDENT CADETS (${students.size})",
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
                        .testTag("student_row_${student.admissionNo}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
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
                                .background(Color(0xFFDBEAFE)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.fullName.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = AcademyBlueDark,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = student.fullName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = Slate900
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = Color(0xFFEFF6FF),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = student.admissionNo,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AcademyBlue,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "${student.gradeLevel} • ${student.assignedLocation}",
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )
                            Text(
                                text = "Guardian: ${student.guardianName} (${student.guardianContact})",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }
                        IconButton(
                            onClick = { onDeleteStudent(student) },
                            modifier = Modifier.testTag("delete_student_${student.admissionNo}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Student",
                                tint = Color(0xFFEF4444)
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
                .testTag("add_student_fab"),
            containerColor = AcademyBlueDark,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Student")
        }
    }

    if (showAddDialog) {
        AddStudentDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { student ->
                onAddStudent(student)
                showAddDialog = false
            }
        )
    }
}

@Composable
fun AddStudentDialog(
    onDismiss: () -> Unit,
    onConfirm: (Student) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var admissionNo by remember { mutableStateOf("OA-2026-${(100..999).random()}") }
    var grade by remember { mutableStateOf("Grade 10 - STEM") }
    var location by remember { mutableStateOf("Campus North Wing - Room 101") }
    var guardian by remember { mutableStateOf("Guardian / Shelter") }
    var contact by remember { mutableStateOf("+1 555-0200") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Enroll New Student", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_student_name_input")
                )
                OutlinedTextField(
                    value = admissionNo,
                    onValueChange = { admissionNo = it },
                    label = { Text("Admission ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_student_admission_input")
                )
                OutlinedTextField(
                    value = grade,
                    onValueChange = { grade = it },
                    label = { Text("Grade & Stream") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Classroom / Location") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = guardian,
                    onValueChange = { guardian = it },
                    label = { Text("Guardian Name / Shelter House") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            Student(
                                id = "std_${UUID.randomUUID().toString().take(8)}",
                                admissionNo = admissionNo,
                                fullName = name,
                                gradeLevel = grade,
                                assignedLocation = location,
                                guardianName = guardian,
                                guardianContact = contact
                            )
                        )
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_student_button")
            ) {
                Text("Enroll Student")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdminTestsTab(
    tests: List<ExamTest>,
    onAddTest: (ExamTest) -> Unit,
    onDeleteTest: (ExamTest) -> Unit
) {
    val context = LocalContext.current
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
                    text = "EXAMINATION & A4 PDF GENERATION CENTER (${tests.size})",
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
                        .testTag("test_item_${test.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = test.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = Slate900,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(onClick = { onDeleteTest(test) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Test",
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Text(
                            text = "${test.subject} • ${test.gradeLevel} • ${test.totalMarks} Marks • ${test.durationMinutes} Mins",
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

                        // A4 PDF Generation Action Button
                        Button(
                            onClick = {
                                try {
                                    val pdfFile = PdfGenerator.generateExamPdf(context, test, "Department of Academic Affairs")
                                    Toast.makeText(
                                        context,
                                        "A4 PDF Generated: ${pdfFile.name} (595x842 pt)",
                                        Toast.LENGTH_LONG
                                    ).show()
                                } catch (e: Exception) {
                                    Toast.makeText(context, "PDF Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("generate_a4_pdf_button_${test.id}"),
                            colors = ButtonDefaults.buttonColors(containerColor = AcademyBlueDark),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Generate Printable A4 Exam PDF (595x842 pt)", fontSize = 12.sp)
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
                .testTag("add_test_fab"),
            containerColor = AcademyBlueDark,
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
fun CreateTestDialog(
    onDismiss: () -> Unit,
    onConfirm: (ExamTest) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Physics") }
    var grade by remember { mutableStateOf("Grade 10 - STEM") }
    var marks by remember { mutableStateOf("50") }
    var duration by remember { mutableStateOf("90") }
    var questions by remember {
        mutableStateOf(
            "State and explain the primary governing laws for this module.\nCalculate the resultant force and show diagram with free body vectors.\nDiscuss implications in a real-world scenario with 3 examples."
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Examination Test Paper", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Exam Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("exam_title_input")
                )
                OutlinedTextField(
                    value = subject,
                    onValueChange = { subject = it },
                    label = { Text("Subject") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = marks,
                        onValueChange = { marks = it },
                        label = { Text("Total Marks") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Minutes") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = questions,
                    onValueChange = { questions = it },
                    label = { Text("Questions (One per line)") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            ExamTest(
                                id = "test_${UUID.randomUUID().toString().take(8)}",
                                title = title,
                                subject = subject,
                                gradeLevel = grade,
                                totalMarks = marks.toIntOrNull() ?: 50,
                                durationMinutes = duration.toIntOrNull() ?: 90,
                                examDate = "2026-11-20",
                                instructions = "Answer all questions in complete sentences. Neat diagrams carry additional marks.",
                                questionsJson = questions,
                                createdByTeacherId = "admin"
                            )
                        )
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("confirm_create_test_button")
            ) {
                Text("Publish Test")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdminFeaturesTab(
    features: List<CustomFeature>,
    onAddFeature: (CustomFeature) -> Unit,
    onDeleteFeature: (CustomFeature) -> Unit,
    isManagerMode: Boolean = false
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
                    text = "DYNAMIC FEATURE & OPERATIONS REGISTRY (${features.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = AcademyBlueDark,
                    letterSpacing = 1.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )
            }

            items(features) { feature ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("feature_card_${feature.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = when (feature.category) {
                                        "Rations" -> Color(0xFFFEF3C7)
                                        "Inventory" -> Color(0xFFEDE9FE)
                                        "Health" -> Color(0xFFDCFCE7)
                                        else -> Color(0xFFF1F5F9)
                                    },
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = feature.category.uppercase(),
                                        color = when (feature.category) {
                                            "Rations" -> Color(0xFF92400E)
                                            "Inventory" -> Color(0xFF5B21B6)
                                            "Health" -> Color(0xFF166534)
                                            else -> Color(0xFF334155)
                                        },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = feature.status,
                                        fontSize = 10.sp,
                                        color = Color(0xFF475569),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = feature.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Slate900
                            )
                            Text(
                                text = feature.description,
                                fontSize = 12.sp,
                                color = Color(0xFF475569)
                            )
                            Text(
                                text = "Logged by: ${feature.updatedBy}",
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8)
                            )
                        }

                        IconButton(
                            onClick = { onDeleteFeature(feature) },
                            modifier = Modifier.testTag("delete_feature_${feature.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Feature",
                                tint = Color(0xFFEF4444)
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
                .testTag("add_feature_fab"),
            containerColor = AcademyBlueDark,
            contentColor = Color.White
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Feature")
        }
    }

    if (showAddDialog) {
        AddFeatureDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { feature ->
                onAddFeature(feature)
                showAddDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddFeatureDialog(
    onDismiss: () -> Unit,
    onConfirm: (CustomFeature) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Rations") }
    var description by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Active") }

    val categories = listOf("Rations", "Inventory", "Operations", "Health", "Logistics")
    var categoryExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Register New Academy Feature / Module", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Module / Feature Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_feature_name_input")
                )

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description & Specifications") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Current Status (e.g. Active, In Stock)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(
                            CustomFeature(
                                id = "feat_${UUID.randomUUID().toString().take(8)}",
                                name = name,
                                category = category,
                                description = description.ifBlank { "Operational module for academy logistics." },
                                status = status,
                                updatedBy = "System Administrator"
                            )
                        )
                    }
                },
                enabled = name.isNotBlank(),
                modifier = Modifier.testTag("confirm_add_feature_button")
            ) {
                Text("Add Feature")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdminSettingsTab(
    userAccounts: List<UserAccount>,
    onCreateUser: (name: String, email: String, password: String, role: UserRole, location: String?, admissionNo: String?) -> Unit,
    onResetPassword: (targetUid: String, newPassword: String) -> Unit,
    onDeleteUser: (targetUid: String) -> Unit,
    onUpdateUserRole: (targetUid: String, newRole: UserRole) -> Unit,
    onForceSync: () -> Unit,
    isOnline: Boolean
) {
    var showCreateUserDialog by remember { mutableStateOf(false) }
    var resetPasswordTargetUser by remember { mutableStateOf<UserAccount?>(null) }
    var deleteConfirmTargetUser by remember { mutableStateOf<UserAccount?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Slate900),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "User Management & Cloud Sync",
                        color = AcademyGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Manage faculty and staff accounts, assign roles, reset credentials, and sync data.",
                        color = Color(0xFFCBD5E1),
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showCreateUserDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AcademyGold),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("admin_create_user_button")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Slate900)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create User", color = Slate900, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = onForceSync,
                            colors = ButtonDefaults.buttonColors(containerColor = AcademyBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f).testTag("admin_sync_all_button")
                        ) {
                            Icon(imageVector = Icons.Default.CloudSync, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cloud Sync", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "MANAGED USER ACCOUNTS (${userAccounts.size})",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = AcademyBlueDark,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(userAccounts) { account ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_account_item_${account.uid}"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = account.displayName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Slate900
                            )
                            Text(
                                text = account.email,
                                fontSize = 12.sp,
                                color = Color(0xFF64748B)
                            )
                            if (!account.assignedLocation.isNullOrBlank()) {
                                Text(
                                    text = "Location: ${account.assignedLocation}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                            if (!account.studentAdmissionNo.isNullOrBlank()) {
                                Text(
                                    text = "Admission ID: ${account.studentAdmissionNo}",
                                    fontSize = 11.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }
                        }

                        Surface(
                            color = getRoleColor(account.role),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = account.role.name,
                                color = Color.White,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    if (account.isPendingCloudSync) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "⚠ Pending Cloud Sync to Firebase",
                            fontSize = 11.sp,
                            color = Color(0xFFD97706),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Password and Deletion controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = { resetPasswordTargetUser = account },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.testTag("reset_pass_btn_${account.uid}")
                        ) {
                            Text("Reset Password", fontSize = 11.sp)
                        }

                        if (account.email.lowercase() != "admin@orphan.com.pk") {
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = { deleteConfirmTargetUser = account },
                                modifier = Modifier.testTag("delete_user_btn_${account.uid}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete Account",
                                    tint = Color(0xFFEF4444)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Assign Role:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        UserRole.values().forEach { role ->
                            val isSelected = account.role == role
                            OutlinedButton(
                                onClick = { onUpdateUserRole(account.uid, role) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("assign_role_${role.name}_to_${account.uid}"),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isSelected) getRoleColor(role) else Color.Transparent,
                                    contentColor = if (isSelected) Color.White else getRoleColor(role)
                                ),
                                border = BorderStroke(1.dp, getRoleColor(role)),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = role.name.take(4),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Dialog for Admin Creating User
    if (showCreateUserDialog) {
        AdminCreateUserDialog(
            onDismiss = { showCreateUserDialog = false },
            onConfirm = { name, email, pass, role, loc, admission ->
                onCreateUser(name, email, pass, role, loc, admission)
                showCreateUserDialog = false
            }
        )
    }

    // Dialog for Reset Password
    resetPasswordTargetUser?.let { target ->
        AdminResetPasswordDialog(
            targetUser = target,
            onDismiss = { resetPasswordTargetUser = null },
            onConfirm = { newPass ->
                onResetPassword(target.uid, newPass)
                resetPasswordTargetUser = null
            }
        )
    }

    // Dialog for Delete Confirmation
    deleteConfirmTargetUser?.let { target ->
        AlertDialog(
            onDismissRequest = { deleteConfirmTargetUser = null },
            title = { Text("Delete Account", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to permanently delete account ${target.email} (${target.displayName})?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteUser(target.uid)
                        deleteConfirmTargetUser = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete Account")
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmTargetUser = null }) { Text("Cancel") }
            }
        )
    }
}

@Composable
fun AdminCreateUserDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, email: String, pass: String, role: UserRole, loc: String?, admission: String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.TEACHER) }
    var location by remember { mutableStateOf("Campus North Wing") }
    var admissionNo by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create User Account", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Full Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("create_user_name_input")
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email Address (Login)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("create_user_email_input")
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Login Password") },
                    placeholder = { Text("e.g. Pass@1234") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("create_user_password_input")
                )

                Text(
                    text = "Select Role:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    UserRole.values().forEach { role ->
                        val isSelected = selectedRole == role
                        OutlinedButton(
                            onClick = { selectedRole = role },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isSelected) getRoleColor(role) else Color.Transparent,
                                contentColor = if (isSelected) Color.White else getRoleColor(role)
                            ),
                            border = BorderStroke(1.dp, getRoleColor(role)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(text = role.name.take(4), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                if (selectedRole == UserRole.STUDENT) {
                    OutlinedTextField(
                        value = admissionNo,
                        onValueChange = { admissionNo = it },
                        label = { Text("Student Admission ID (e.g. OA-042)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("create_user_admission_input")
                    )
                } else {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = { Text("Assigned Campus Location") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("create_user_location_input")
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        onConfirm(
                            name.trim(),
                            email.trim(),
                            password,
                            selectedRole,
                            location.ifBlank { null },
                            admissionNo.ifBlank { null }
                        )
                    }
                },
                enabled = name.isNotBlank() && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.testTag("confirm_create_user_button")
            ) {
                Text("Create Account")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun AdminResetPasswordDialog(
    targetUser: UserAccount,
    onDismiss: () -> Unit,
    onConfirm: (newPass: String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reset User Password", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Reset login password for: ${targetUser.email}")
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("reset_password_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (newPassword.isNotBlank()) {
                        onConfirm(newPassword)
                    }
                },
                enabled = newPassword.isNotBlank(),
                modifier = Modifier.testTag("confirm_reset_password_button")
            ) {
                Text("Update Password")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
