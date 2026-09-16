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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CustomFeature
import com.example.model.Student
import com.example.model.Teacher
import com.example.model.TestRecord
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyBlueDark
import com.example.ui.theme.Slate900

@Composable
fun ManagerTeachersViewTab(teachers: List<Teacher>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Surface(
                color = Color(0xFFEFF6FF),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = AcademyBlue
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Read-Only Faculty Inspection Mode. Managers can monitor teacher room & facility allocations.",
                        fontSize = 11.sp,
                        color = AcademyBlueDark,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        item {
            Text(
                text = "FACULTY LOCATION ALLOCATIONS (${teachers.size})",
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
                    .testTag("manager_teacher_card_${teacher.id}"),
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
                            .background(Color(0xFFE0E7FF)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = teacher.fullName.take(2).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4338CA),
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
                                tint = Color(0xFFEF4444),
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${teacher.assignedLocation} • ${teacher.activeClassroom}",
                                fontSize = 12.sp,
                                color = Slate900,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ManagerStudentsAndIdTab(
    students: List<Student>,
    onAddStudent: (Student) -> Unit,
    onDeleteStudent: (Student) -> Unit
) {
    var selectedStudentForId by remember { mutableStateOf<Student?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "STUDENT ROSTER & DIGITAL ID CARDS (${students.size})",
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
                    .testTag("manager_student_card_${student.admissionNo}"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
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
                            Text(
                                text = student.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Slate900
                            )
                            Text(
                                text = "ID: ${student.admissionNo} • ${student.gradeLevel}",
                                fontSize = 12.sp,
                                color = AcademyBlue
                            )
                        }
                        TextButton(
                            onClick = {
                                selectedStudentForId = if (selectedStudentForId?.admissionNo == student.admissionNo) null else student
                            },
                            modifier = Modifier.testTag("toggle_id_card_${student.admissionNo}")
                        ) {
                            Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (selectedStudentForId?.admissionNo == student.admissionNo) "Close ID" else "View ID")
                        }
                    }

                    // Expandable Student Digital ID Card
                    if (selectedStudentForId?.admissionNo == student.admissionNo) {
                        Spacer(modifier = Modifier.height(10.dp))
                        StudentIdCardView(student = student)
                    }
                }
            }
        }
    }
}

@Composable
fun ManagerTestReportsTab(records: List<TestRecord>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "ACADEMY-WIDE EXAMINATION PERFORMANCE REPORTS (${records.size})",
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
                    .testTag("manager_report_${record.id}"),
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
                            .background(
                                if (record.marksObtained >= (record.totalMarks * 0.75)) Color(0xFFDCFCE7)
                                else Color(0xFFFEF3C7)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${record.marksObtained}/${record.totalMarks}",
                            fontWeight = FontWeight.ExtraBold,
                            color = if (record.marksObtained >= (record.totalMarks * 0.75)) Color(0xFF166534)
                            else Color(0xFF92400E),
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
                            text = "Remarks: ${record.remarks}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }
        }
    }
}
