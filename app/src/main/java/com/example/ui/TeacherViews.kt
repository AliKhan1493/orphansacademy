package com.example.ui

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.EventBusy
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.LocationOff
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AcademyGeofence
import com.example.model.AttendanceRecord
import com.example.model.ExamTest
import com.example.model.Student
import com.example.model.SyllabusTopic
import com.example.model.TestRecord
import com.example.model.UserAccount
import com.example.model.UserRole
import com.example.ui.theme.AcademyBlue
import com.example.ui.theme.AcademyBlueDark
import com.example.ui.theme.DarkMossGray
import com.example.ui.theme.DarkMossGrayMuted
import com.example.ui.theme.DeepForestTeal
import com.example.ui.theme.PaleSageOffWhite
import com.example.ui.theme.PastelSeafoam
import com.example.ui.theme.PastelSeafoamLight
import com.example.ui.theme.RoleTeacherColor
import com.example.ui.theme.Slate900
import com.example.ui.theme.SoftAmberPeach
import com.example.util.LocationHelper
import com.example.util.PdfGenerator
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

/**
 * TEACHER DASHBOARD: ZERO-FRICTION UX
 *
 * 1. One-Tap Attendance:
 *    - Defaults ALL students to "Present".
 *    - Interaction only needed for "Absent" or "Leave".
 *    - Large 48dp toggle buttons (Green = Present, Red = Absent, Amber = Leave).
 * 2. Offline Confidence:
 *    - Visual badge: Amber "Saving Locally" cloud icon when offline.
 *    - Animates to Green "Synced" checkmark when online.
 *    - Never blocks data entry.
 * 3. Fat-Finger Friendly:
 *    - Minimum 48dp touch targets on list rows, toggles, grade entries, and syllabus checkboxes.
 * 4. Real Geofenced Check-in & Check-out:
 *    - Displays real live GPS latitude & longitude.
 *    - Verifies against Admin-assigned Academy Latitude, Longitude, and Coverage Perimeter (meters).
 *    - Outside radius is classified as "OUT OF COVERAGE" with warning badge & distance.
 */

@Composable
fun TeacherClassAttendanceTab(
    students: List<Student>,
    isOnline: Boolean,
    onSaveAttendance: (List<AttendanceRecord>) -> Unit
) {
    val context = LocalContext.current
    val todayFormatted = remember {
        SimpleDateFormat("EEEE, MMM d, yyyy", Locale.getDefault()).format(Date())
    }
    val todayDateKey = remember {
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
    val currentTimeString = remember {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    }

    // Default ALL students to "PRESENT" for Zero-Friction One-Tap Attendance
    val attendanceStatusMap = remember(students) {
        mutableStateMapOf<String, String>().apply {
            students.forEach { student ->
                put(student.admissionNo, "PRESENT")
            }
        }
    }

    var isSaving by remember { mutableStateOf(false) }
    var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

    val presentCount = attendanceStatusMap.values.count { it == "PRESENT" }
    val absentCount = attendanceStatusMap.values.count { it == "ABSENT" }
    val leaveCount = attendanceStatusMap.values.count { it == "LEAVE" }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Roster Control & Offline Confidence Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("attendance_header_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PastelSeafoam)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DAILY CLASSROOM ROSTER",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = DeepForestTeal,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = todayFormatted,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Slate900
                            )
                        }

                        // Offline Confidence Badge: Non-intrusive indicator
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = if (isOnline) Color(0xFFDCFCE7) else Color(0xFFFEF3C7),
                            border = BorderStroke(1.dp, if (isOnline) Color(0xFF86EFAC) else Color(0xFFFDE68A)),
                            modifier = Modifier.testTag("offline_confidence_badge")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isOnline) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                                    contentDescription = null,
                                    tint = if (isOnline) Color(0xFF166534) else Color(0xFFB45309),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (isOnline) "Synced" else "Saving Locally",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOnline) Color(0xFF166534) else Color(0xFFB45309)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Summary Counts Card
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "$presentCount", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF047857))
                                Text(text = "Present", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF065F46))
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFECACA))
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "$absentCount", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFDC2626))
                                Text(text = "Absent", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF991B1B))
                            }
                        }

                        Surface(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFFFFFBEB),
                            border = BorderStroke(1.dp, Color(0xFFFDE68A))
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(text = "$leaveCount", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFFD97706))
                                Text(text = "On Leave", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF92400E))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // One-Tap Save Action
                    Button(
                        onClick = {
                            isSaving = true
                            val records = students.map { student ->
                                val status = attendanceStatusMap[student.admissionNo] ?: "PRESENT"
                                AttendanceRecord(
                                    id = "att_${student.admissionNo}_$todayDateKey",
                                    personId = student.admissionNo,
                                    personName = student.fullName,
                                    userRole = UserRole.STUDENT,
                                    date = todayDateKey,
                                    checkInTime = currentTimeString,
                                    status = status,
                                    checkInLocationName = "Classroom Roster • Roll Call",
                                    isWithinGeofence = true,
                                    syncStatus = if (isOnline) "SYNCED" else "PENDING"
                                )
                            }
                            onSaveAttendance(records)
                            isSaving = false
                            saveSuccessMessage = "Attendance locked for ${records.size} students ($presentCount present)."
                            Toast.makeText(context, "Classroom attendance saved!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp) // Minimum 48dp touch target
                            .testTag("save_class_attendance_button"),
                        enabled = !isSaving && students.isNotEmpty(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RoleTeacherColor,
                            contentColor = Color.White
                        )
                    ) {
                        if (isSaving) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save & Lock Class Attendance",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Feedback Callout
                    AnimatedVisibility(visible = saveSuccessMessage != null) {
                        Surface(
                            color = Color(0xFFECFDF5),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp)
                        ) {
                            Text(
                                text = saveSuccessMessage ?: "",
                                color = Color(0xFF047857),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }

        // Student Roster List with 48dp Toggle Buttons
        item {
            Text(
                text = "STUDENTS ENROLLED (${students.size}) • TAP TO CHANGE STATUS",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = AcademyBlueDark,
                letterSpacing = 1.sp
            )
        }

        items(students) { student ->
            val currentStatus = attendanceStatusMap[student.admissionNo] ?: "PRESENT"

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("roster_student_${student.admissionNo}"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(
                    width = 1.dp,
                    color = when (currentStatus) {
                        "PRESENT" -> Color(0xFFA7F3D0)
                        "ABSENT" -> Color(0xFFFECACA)
                        else -> Color(0xFFFDE68A)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(
                                    when (currentStatus) {
                                        "PRESENT" -> Color(0xFFCCFBF1)
                                        "ABSENT" -> Color(0xFFFEE2E2)
                                        else -> Color(0xFFFEF3C7)
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = student.fullName.take(2).uppercase(),
                                fontWeight = FontWeight.ExtraBold,
                                color = when (currentStatus) {
                                    "PRESENT" -> RoleTeacherColor
                                    "ABSENT" -> Color(0xFFDC2626)
                                    else -> Color(0xFFD97706)
                                },
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
                                text = "${student.admissionNo} • ${student.gradeLevel}",
                                fontSize = 12.sp,
                                color = DarkMossGrayMuted
                            )
                        }

                        // Current Status Tag
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (currentStatus) {
                                "PRESENT" -> Color(0xFFDCFCE7)
                                "ABSENT" -> Color(0xFFFEE2E2)
                                else -> Color(0xFFFEF3C7)
                            }
                        ) {
                            Text(
                                text = currentStatus,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = when (currentStatus) {
                                    "PRESENT" -> Color(0xFF166534)
                                    "ABSENT" -> Color(0xFFB91C1C)
                                    else -> Color(0xFFB45309)
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Distinct Toggle Buttons (Minimum 48dp Touch Target)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // PRESENT BUTTON (Green)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp) // Accessibility min touch target
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    attendanceStatusMap[student.admissionNo] = "PRESENT"
                                    saveSuccessMessage = null
                                }
                                .testTag("toggle_present_${student.admissionNo}"),
                            color = if (currentStatus == "PRESENT") Color(0xFF10B981) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (currentStatus == "PRESENT") Color(0xFF059669) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "✓ Present",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (currentStatus == "PRESENT") Color.White else Color(0xFF64748B)
                                )
                            }
                        }

                        // ABSENT BUTTON (Red)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp) // Accessibility min touch target
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    attendanceStatusMap[student.admissionNo] = "ABSENT"
                                    saveSuccessMessage = null
                                }
                                .testTag("toggle_absent_${student.admissionNo}"),
                            color = if (currentStatus == "ABSENT") Color(0xFFEF4444) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (currentStatus == "ABSENT") Color(0xFFDC2626) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "✕ Absent",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (currentStatus == "ABSENT") Color.White else Color(0xFF64748B)
                                )
                            }
                        }

                        // LEAVE BUTTON (Amber)
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp) // Accessibility min touch target
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    attendanceStatusMap[student.admissionNo] = "LEAVE"
                                    saveSuccessMessage = null
                                }
                                .testTag("toggle_leave_${student.admissionNo}"),
                            color = if (currentStatus == "LEAVE") Color(0xFFF59E0B) else Color(0xFFF1F5F9),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(
                                1.dp,
                                if (currentStatus == "LEAVE") Color(0xFFD97706) else Color(0xFFE2E8F0)
                            )
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "◷ Leave",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (currentStatus == "LEAVE") Color.White else Color(0xFF64748B)
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

/**
 * TEACHER REAL LOCATION TAB
 *
 * Requirements:
 * - Checked in and checked out must show real location for teacher (lat & lon).
 * - Location assigned through longitude & latitude by admin, and area (radius) where check-in and checkout is possible.
 * - Without that area it will be counted as "OUT OF COVERAGE".
 */
@Composable
fun TeacherLocationTab(
    currentUser: UserAccount,
    academyGeofence: AcademyGeofence,
    todayAttendance: AttendanceRecord? = null,
    isCheckingLocation: Boolean = false,
    onCheckInLiveLocation: () -> Unit = {},
    onCheckOutLiveLocation: () -> Unit = {}
) {
    val context = LocalContext.current
    var liveLat by remember { mutableStateOf<Double?>(null) }
    var liveLon by remember { mutableStateOf<Double?>(null) }
    var distanceToAcademy by remember { mutableStateOf<Float?>(null) }
    var isInsideCoverage by remember { mutableStateOf<Boolean?>(null) }

    // Fetch live coordinates on load
    LaunchedEffect(Unit) {
        val geo = LocationHelper.getLiveLocation(context)
        liveLat = geo.latitude
        liveLon = geo.longitude
        val dist = LocationHelper.calculateDistanceMeters(
            startLat = geo.latitude,
            startLon = geo.longitude,
            endLat = academyGeofence.latitude,
            endLon = academyGeofence.longitude
        )
        distanceToAcademy = dist
        isInsideCoverage = dist <= academyGeofence.radiusMeters
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Real Location & Admin-Assigned Geofence Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("teacher_geofence_status_card"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, PastelSeafoam)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(PastelSeafoam),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NearMe,
                                    contentDescription = null,
                                    tint = DeepForestTeal,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "GPS CHECK-IN & COVERAGE",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestTeal,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = academyGeofence.campusName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Slate900
                                )
                            }
                        }

                        // Geofence status tag
                        if (isInsideCoverage != null) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isInsideCoverage == true) Color(0xFFDCFCE7) else Color(0xFFFEE2E2),
                                border = BorderStroke(
                                    1.dp,
                                    if (isInsideCoverage == true) Color(0xFF86EFAC) else Color(0xFFFCA5A5)
                                )
                            ) {
                                Text(
                                    text = if (isInsideCoverage == true) "IN COVERAGE" else "OUT OF COVERAGE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isInsideCoverage == true) Color(0xFF166534) else Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Real Coordinates & Perimeter Box
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = PaleSageOffWhite,
                        border = BorderStroke(1.dp, PastelSeafoam)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Live GPS Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "My Real GPS Coordinates:", fontSize = 11.sp, color = DarkMossGrayMuted)
                                Text(
                                    text = if (liveLat != null && liveLon != null) {
                                        String.format(Locale.US, "%.4f° N, %.4f° E", liveLat, liveLon)
                                    } else "Acquiring GPS...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Admin Assigned Academy Location Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Admin Assigned Location:", fontSize = 11.sp, color = DarkMossGrayMuted)
                                Text(
                                    text = String.format(Locale.US, "%.4f° N, %.4f° E", academyGeofence.latitude, academyGeofence.longitude),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestTeal
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Allowed Area / Radius Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Allowed Coverage Area:", fontSize = 11.sp, color = DarkMossGrayMuted)
                                Text(
                                    text = "${academyGeofence.radiusMeters.toInt()} meters radius",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = DeepForestTeal
                                )
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Distance from Academy Center
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Distance to Academy:", fontSize = 11.sp, color = DarkMossGrayMuted)
                                Text(
                                    text = if (distanceToAcademy != null) {
                                        if (distanceToAcademy!! < 1000) "${distanceToAcademy!!.toInt()} m" else String.format(Locale.US, "%.2f km", distanceToAcademy!! / 1000f)
                                    } else "Calculating...",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (isInsideCoverage == true) Color(0xFF047857) else Color(0xFFDC2626)
                                )
                            }
                        }
                    }

                    // OUT OF COVERAGE WARNING CALLOUT
                    if (isInsideCoverage == false) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFFEF2F2),
                            border = BorderStroke(1.dp, Color(0xFFFCA5A5))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocationOff,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "OUT OF COVERAGE: You are ${distanceToAcademy?.toInt() ?: 0}m away from the Academy. Admin allowed radius is ${academyGeofence.radiusMeters.toInt()}m. Any check-in here will count as Out of Coverage.",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF991B1B)
                                )
                            }
                        }
                    } else if (isInsideCoverage == true) {
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 10.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFFECFDF5),
                            border = BorderStroke(1.dp, Color(0xFFA7F3D0))
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color(0xFF047857),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "IN COVERAGE: You are inside the authorized academy zone (${distanceToAcademy?.toInt() ?: 0}m from center).",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF065F46)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Action Buttons (Minimum 48dp Touch Target)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onCheckInLiveLocation,
                            enabled = !isCheckingLocation && todayAttendance == null,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp) // Touch target >= 48dp
                                .testTag("teacher_gps_checkin_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = DeepForestTeal,
                                contentColor = Color.White
                            )
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isCheckingLocation) "Verifying GPS..." else "GPS Check-In",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onCheckOutLiveLocation,
                            enabled = !isCheckingLocation && todayAttendance != null && todayAttendance.checkOutTime == null,
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp) // Touch target >= 48dp
                                .testTag("teacher_gps_checkout_button"),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.HowToReg, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (todayAttendance?.checkOutTime != null) "Checked Out" else "Check-Out",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Today's Attendance Record Summary
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("teacher_today_attendance_summary"),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(14.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "TODAY'S ATTENDANCE RECORD",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AcademyBlueDark,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    if (todayAttendance != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Check-In: ${todayAttendance.checkInTime}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Slate900
                                )
                                Text(
                                    text = "Location: ${todayAttendance.checkInLocationName}",
                                    fontSize = 12.sp,
                                    color = DarkMossGrayMuted
                                )
                                if (todayAttendance.checkInLatitude != null && todayAttendance.checkInLongitude != null) {
                                    Text(
                                        text = String.format(Locale.US, "GPS: %.4f° N, %.4f° E (Dist: %.0fm)", todayAttendance.checkInLatitude, todayAttendance.checkInLongitude, todayAttendance.checkInDistanceMeters),
                                        fontSize = 11.sp,
                                        color = DeepForestTeal
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (todayAttendance.isWithinGeofence && todayAttendance.status != "OUT_OF_COVERAGE") Color(0xFFDCFCE7) else Color(0xFFFEE2E2)
                            ) {
                                Text(
                                    text = if (todayAttendance.isWithinGeofence && todayAttendance.status != "OUT_OF_COVERAGE") "PRESENT" else "OUT OF COVERAGE",
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp,
                                    color = if (todayAttendance.isWithinGeofence && todayAttendance.status != "OUT_OF_COVERAGE") Color(0xFF166534) else Color(0xFFDC2626),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (todayAttendance.checkOutTime != null) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Check-Out: ${todayAttendance.checkOutTime}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF475569)
                                )
                                Text(
                                    text = todayAttendance.checkOutLocationName ?: "Verified Departure",
                                    fontSize = 11.sp,
                                    color = DarkMossGrayMuted
                                )
                            }
                        }
                    } else {
                        Text(
                            text = "No check-in recorded for today yet. Use the GPS Check-In button above.",
                            fontSize = 12.sp,
                            color = DarkMossGrayMuted
                        )
                    }
                }
            }
        }

        // Assigned Classroom Info
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("teacher_assigned_room_card"),
                colors = CardDefaults.cardColors(containerColor = RoleTeacherColor),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MeetingRoom, contentDescription = null, tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "ASSIGNED ACADEMY ROOM",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = currentUser.assignedLocation ?: "Campus Science Hall 204",
                                color = Color.White,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Teacher: ${currentUser.displayName} (${currentUser.email})",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
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

                        Spacer(modifier = Modifier.height(12.dp))

                        // A4 PDF Generation Action (Minimum 48dp Touch Target)
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
                                .height(48.dp) // Accessibility min touch target
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
        TeacherCreateTestDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { test: ExamTest ->
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
                                .size(48.dp)
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
fun TeacherCreateTestDialog(
    onDismiss: () -> Unit,
    onConfirm: (ExamTest) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("Physics") }
    var totalMarks by remember { mutableStateOf("50") }
    var duration by remember { mutableStateOf("60") }
    var instructions by remember { mutableStateOf("Answer all questions. Show working.") }
    var questionsText by remember { mutableStateOf("1. State Newton's second law.\n2. Calculate acceleration given force and mass.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Formulate New Printable Exam", fontWeight = FontWeight.Bold) },
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
                        value = totalMarks,
                        onValueChange = { totalMarks = it },
                        label = { Text("Marks") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = duration,
                        onValueChange = { duration = it },
                        label = { Text("Mins") },
                        modifier = Modifier.weight(1f)
                    )
                }
                OutlinedTextField(
                    value = instructions,
                    onValueChange = { instructions = it },
                    label = { Text("Instructions") },
                    maxLines = 2,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = questionsText,
                    onValueChange = { questionsText = it },
                    label = { Text("Exam Questions") },
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
                        onConfirm(
                            ExamTest(
                                id = "test_${UUID.randomUUID().toString().take(8)}",
                                title = title,
                                subject = subject,
                                gradeLevel = "Grade 10 - STEM",
                                totalMarks = totalMarks.toIntOrNull() ?: 50,
                                durationMinutes = duration.toIntOrNull() ?: 60,
                                examDate = today,
                                instructions = instructions,
                                questionsJson = questionsText,
                                createdByTeacherId = "current"
                            )
                        )
                    }
                },
                enabled = title.isNotBlank(),
                modifier = Modifier.testTag("confirm_create_test_button")
            ) {
                Text("Save Exam")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun LogMarksDialog(
    students: List<Student>,
    tests: List<ExamTest>,
    onDismiss: () -> Unit,
    onConfirm: (TestRecord) -> Unit
) {
    var marks by remember { mutableStateOf("45") }
    var remarks by remember { mutableStateOf("Good understanding of theoretical principles.") }
    var selectedStudentIndex by remember { mutableStateOf(0) }
    var selectedTestIndex by remember { mutableStateOf(0) }

    val currentStudent = students.getOrNull(selectedStudentIndex)
    val currentTest = tests.getOrNull(selectedTestIndex)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log & Grade Student Examination", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
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
