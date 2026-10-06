package com.example.ui

import android.graphics.Bitmap
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AttendanceRecord
import com.example.model.ExamTest
import com.example.model.Student
import com.example.model.SyllabusTopic
import com.example.model.TestRecord
import com.example.model.UserAccount
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyBlueDark
import com.example.ui.theme.DarkMossGray
import com.example.ui.theme.DarkMossGrayMuted
import com.example.ui.theme.DeepForestTeal
import com.example.ui.theme.PaleSageOffWhite
import com.example.ui.theme.PastelSeafoam
import com.example.ui.theme.PastelSeafoamLight
import com.example.ui.theme.Slate900
import com.example.ui.theme.SoftAmberPeach
import com.example.ui.theme.SoftAmberPeachDark
import com.example.ui.theme.SoftAmberPeachLight

/**
 * STUDENT DASHBOARD: VISUAL & ENCOURAGING UX
 *
 * 1. Digital ID Prominence:
 *    - Focal point of the home screen: large, distinct, immediately accessible.
 *    - High-contrast student photo with persistent storage & readable emergency contact.
 * 2. Visual Data:
 *    - Heavy iconography over dense text.
 *    - Thick animated circular progress indicators for syllabus mastery.
 * 3. Gamified Feedback:
 *    - Soft Amber/Peach palette highlighting achievements, improvements, badges.
 *    - Stress-free and motivating.
 */

@Composable
fun StudentDigitalIdTab(
    currentUser: UserAccount,
    studentProfile: Student?,
    onPhotoCaptured: (Bitmap) -> Unit = {},
    todayAttendance: AttendanceRecord? = null,
    isCheckingLocation: Boolean = false,
    onCheckInLiveLocation: () -> Unit = {}
) {
    val student = studentProfile ?: Student(
        id = currentUser.uid,
        admissionNo = currentUser.studentAdmissionNo ?: "OA-2026-042",
        fullName = currentUser.displayName,
        gradeLevel = "Grade 10 - STEM",
        assignedLocation = currentUser.assignedLocation ?: "Campus North Wing - Room 101",
        guardianName = "Mrs. Fatima Tariq",
        guardianContact = "+1 (555) 234-5678",
        bloodGroup = "O+",
        emergencyContact = "+1 (555) 901-2345"
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Welcome Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "STUDENT PORTAL",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestTeal,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Hello, ${student.fullName.substringBefore(" ")}!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Slate900
                    )
                }

                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = SoftAmberPeachLight,
                    border = BorderStroke(1.dp, SoftAmberPeach)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = SoftAmberPeachDark, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Honor Roll", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftAmberPeachDark)
                    }
                }
            }
        }

        // DIGITAL ID CARD: FOCAL POINT
        item {
            StudentIdCardView(student = student, onPhotoCaptured = onPhotoCaptured)
        }

        // Emergency Contact Prominence Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_emergency_info_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, Color(0xFFFCA5A5))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEE2E2)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Phone, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "EMERGENCY & GUARDIAN CONTACT",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF991B1B)
                        )
                        Text(
                            text = "${student.guardianName}: ${student.emergencyContact}",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900
                        )
                    }
                    Surface(
                        color = Color(0xFFFEF2F2),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Blood: ${student.bloodGroup}",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFDC2626)
                        )
                    }
                }
            }
        }

        // Academy Geolocation Check-In Station
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_attendance_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, PastelSeafoam)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(PastelSeafoam),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.NearMe, contentDescription = null, tint = DeepForestTeal, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "ACADEMY CHECK-IN STATION",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestTeal
                                )
                                Text(
                                    text = if (todayAttendance != null) "Verified: ${todayAttendance.checkInLocationName}" else "Not Checked-In Today",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DarkMossGray
                                )
                            }
                        }
                        Surface(
                            color = if (todayAttendance != null) PastelSeafoamLight else PaleSageOffWhite,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = if (todayAttendance != null) "PRESENT (${todayAttendance.checkInTime})" else "AWAITING",
                                color = if (todayAttendance != null) DeepForestTeal else Color(0xFF64748B),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = onCheckInLiveLocation,
                        enabled = !isCheckingLocation && todayAttendance == null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp) // Touch target >= 48dp
                            .testTag("student_gps_checkin_button"),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = DeepForestTeal)
                    ) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (todayAttendance != null) "Daily Attendance Verified" else if (isCheckingLocation) "Verifying GPS..." else "Check-In at Academy Gates",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * STUDENT SYLLABUS TAB: HEAVY ICONOGRAPHY & VISUAL DATA
 *
 * Thick animated circular progress indicator for overall mastery.
 * Progress bars per subject and gamified milestones.
 */
@Composable
fun StudentSyllabusTab(
    syllabusTopics: List<SyllabusTopic>,
    tests: List<ExamTest>
) {
    val completedCount = syllabusTopics.count { it.isCompleted }
    val totalCount = syllabusTopics.size.coerceAtLeast(1)
    val progressFloat = completedCount.toFloat() / totalCount.toFloat()

    var animatedProgress by remember { mutableStateOf(0f) }
    val progressAnim by animateFloatAsState(
        targetValue = animatedProgress,
        animationSpec = tween(durationMillis = 1000),
        label = "syllabus_progress"
    )

    LaunchedEffect(progressFloat) {
        animatedProgress = progressFloat
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Visual Mastery Focal Card (Thick Circular Progress Indicator)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_mastery_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PastelSeafoam)
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "OVERALL CURRICULUM MASTERY",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepForestTeal,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Thick Circular Progress Dial
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(130.dp)
                    ) {
                        CircularProgressIndicator(
                            progress = { progressAnim },
                            modifier = Modifier.fillMaxSize(),
                            color = SoftAmberPeach,
                            trackColor = Color(0xFFFFEDD5),
                            strokeWidth = 12.dp, // Thick, friendly stroke
                            strokeCap = StrokeCap.Round
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${(progressAnim * 100).toInt()}%",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Slate900
                            )
                            Text(
                                text = "$completedCount of $totalCount Modules",
                                fontSize = 11.sp,
                                color = DarkMossGrayMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = if (progressFloat >= 0.7f) "🎉 Outstanding progress! Keep up the brilliant study!" else "📚 Steady progress! Complete lessons to unlock new awards.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepForestTeal,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        // Gamified Achievement Badges Bar
        item {
            Text(
                text = "MY ACADEMIC HONORS & BADGES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AcademyBlueDark,
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    modifier = Modifier.weight(1f),
                    color = SoftAmberPeachLight,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, SoftAmberPeach)
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = SoftAmberPeachDark, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Top Scorer", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftAmberPeachDark)
                        Text(text = "Physics 95%", fontSize = 9.sp, color = DarkMossGrayMuted)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFDCFCE7),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF86EFAC))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = Color(0xFF166534), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "100% Streak", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF166534))
                        Text(text = "Always Present", fontSize = 9.sp, color = DarkMossGrayMuted)
                    }
                }

                Surface(
                    modifier = Modifier.weight(1f),
                    color = Color(0xFFEDE9FE),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFDDD6FE))
                ) {
                    Column(
                        modifier = Modifier.padding(10.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.MilitaryTech, contentDescription = null, tint = Color(0xFF6D28D9), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "STEM Star", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6D28D9))
                        Text(text = "Curricular Medal", fontSize = 9.sp, color = DarkMossGrayMuted)
                    }
                }
            }
        }

        // Upcoming Exam Schedule
        item {
            Text(
                text = "UPCOMING EXAM SCHEDULE (${tests.size})",
                fontSize = 11.sp,
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
                        Icon(Icons.Default.Event, contentDescription = null, tint = Color(0xFFD97706))
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
                    }
                }
            }
        }

        // Syllabus Topics List
        item {
            Text(
                text = "ASSIGNED MODULES & TOPICS (${syllabusTopics.size})",
                fontSize = 11.sp,
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
                border = BorderStroke(
                    1.dp,
                    if (topic.isCompleted) Color(0xFFBBF7D0) else Color(0xFFE2E8F0)
                )
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
                            .background(if (topic.isCompleted) Color(0xFFDCFCE7) else Color(0xFFF1F5F9)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (topic.isCompleted) Icons.Default.CheckCircle else Icons.Default.MenuBook,
                            contentDescription = null,
                            tint = if (topic.isCompleted) Color(0xFF166534) else Color(0xFF64748B),
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
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * STUDENT EXAM RESULTS TAB: GAMIFIED FEEDBACK
 *
 * Soft Amber/Peach palette highlights achievements and improvements.
 * Keeps the interface stress-free and motivating.
 */
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
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Cheerful Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("student_results_encouragement_card"),
                colors = CardDefaults.cardColors(containerColor = SoftAmberPeachLight),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, SoftAmberPeach)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(SoftAmberPeach),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Grade, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "OFFICIAL EXAM ACHIEVEMENTS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftAmberPeachDark,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "Every effort counts towards your dreams!",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Slate900
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "PUBLISHED TEST MARKS (${myRecords.size})",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AcademyBlueDark,
                letterSpacing = 1.sp
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
                            text = "No exam marks published yet for your admission profile.",
                            color = DarkMossGrayMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        } else {
            items(myRecords) { record ->
                val percentage = ((record.marksObtained.toFloat() / record.totalMarks) * 100).toInt()

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("student_result_${record.id}"),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = BorderStroke(
                        1.dp,
                        if (percentage >= 80) Color(0xFFFDE68A) else Color(0xFFE2E8F0)
                    )
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular Grade Badge with Warm Palette
                            Box(
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (percentage >= 80) SoftAmberPeachLight else Color(0xFFECFDF5)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${record.marksObtained}/${record.totalMarks}",
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (percentage >= 80) SoftAmberPeachDark else Color(0xFF047857),
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
                                    text = "Score: $percentage% • Excellent Demonstration",
                                    fontSize = 12.sp,
                                    color = AcademyBlue,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            // Star Badge if score >= 80
                            if (percentage >= 80) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = SoftAmberPeachLight
                                ) {
                                    Text(
                                        text = "★ High Merit",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = SoftAmberPeachDark,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Encouraging Teacher Remarks
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = PaleSageOffWhite,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Faculty Note: “${record.remarks}”",
                                    fontSize = 12.sp,
                                    color = DarkMossGray,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
