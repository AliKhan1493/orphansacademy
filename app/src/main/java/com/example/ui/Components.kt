package com.example.ui

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Student
import com.example.model.UserAccount
import com.example.model.UserRole
import com.example.ui.theme.DarkMossGray
import com.example.ui.theme.DarkMossGrayMuted
import com.example.ui.theme.DarkMossGraySubtle
import com.example.ui.theme.DeepForestTeal
import com.example.ui.theme.DeepForestTealDark
import com.example.ui.theme.DeepForestTealLight
import com.example.ui.theme.PaleSageOffWhite
import com.example.ui.theme.PastelSeafoam
import com.example.ui.theme.PastelSeafoamDark
import com.example.ui.theme.PastelSeafoamLight
import com.example.ui.theme.RoleAdminColor
import com.example.ui.theme.RoleManagerColor
import com.example.ui.theme.RoleStudentColor
import com.example.ui.theme.RoleTeacherColor
import com.example.ui.theme.SoftAmberPeach
import com.example.ui.theme.SoftAmberPeachDark
import com.example.ui.theme.SoftAmberPeachLight

@Composable
fun getRoleColor(role: UserRole): Color {
    return when (role) {
        UserRole.ADMIN -> RoleAdminColor
        UserRole.MANAGER -> RoleManagerColor
        UserRole.TEACHER -> RoleTeacherColor
        UserRole.STUDENT -> RoleStudentColor
    }
}

@Composable
fun NetworkStatusBanner(
    isOnline: Boolean,
    isSimulatedOffline: Boolean,
    onToggleAirplaneSimulation: () -> Unit,
    pendingSyncCount: Int = 0,
    onSyncNow: () -> Unit = {}
) {
    if (isOnline && pendingSyncCount == 0 && !isSimulatedOffline) return

    Surface(
        color = if (isOnline) PastelSeafoamLight else SoftAmberPeachLight,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = if (isOnline) Icons.Default.CloudSync else Icons.Default.CloudOff,
                    contentDescription = null,
                    tint = if (isOnline) DeepForestTeal else SoftAmberPeachDark,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (!isOnline) {
                        "Offline Study Mode • Local database active"
                    } else {
                        "$pendingSyncCount change(s) syncing to cloud..."
                    },
                    color = DarkMossGray,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            if (pendingSyncCount > 0 && isOnline) {
                IconButton(
                    onClick = onSyncNow,
                    modifier = Modifier.size(26.dp).testTag("sync_pending_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Sync,
                        contentDescription = "Sync now",
                        tint = DeepForestTeal,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AcademyTopAppBar(
    currentUser: UserAccount,
    onSignOut: () -> Unit,
    onQuickSwitchClick: (() -> Unit)? = null
) {
    val roleColor = getRoleColor(currentUser.role)

    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(PastelSeafoam),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = DeepForestTeal,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Orphan's Academy",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = roleColor,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = currentUser.role.name,
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = currentUser.displayName,
                            fontSize = 11.sp,
                            color = PastelSeafoamLight
                        )
                    }
                }
            }
        },
        actions = {
            if (onQuickSwitchClick != null) {
                FilledTonalButton(
                    onClick = onQuickSwitchClick,
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .testTag("quick_switch_role_button"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = PastelSeafoam,
                        contentColor = DeepForestTealDark
                    )
                ) {
                    Text("Switch Role", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
            IconButton(
                onClick = onSignOut,
                modifier = Modifier.testTag("sign_out_button")
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Sign Out",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = DeepForestTeal
        )
    )
}

@Composable
fun StudentIdCardView(
    student: Student,
    onPhotoCaptured: (Bitmap) -> Unit = {}
) {
    var capturedPhoto by remember { mutableStateOf<Bitmap?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            capturedPhoto = bitmap
            onPhotoCaptured(bitmap)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("student_id_card_${student.admissionNo}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, PastelSeafoamDark)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // ID Card Header Banner (Deep Forest Teal + Pastel Seafoam)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DeepForestTeal)
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ORPHAN'S ACADEMY",
                            color = PastelSeafoam,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "STUDENT STUDY ID & ACADEMIC CARD",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Surface(
                        color = SoftAmberPeach,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "ACCREDITED",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Student Details & Photo Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Photo Area
                Box(
                    modifier = Modifier
                        .size(96.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PaleSageOffWhite)
                        .border(1.dp, PastelSeafoamDark, RoundedCornerShape(12.dp))
                        .clickable { cameraLauncher.launch(null) }
                        .testTag("capture_student_photo_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (capturedPhoto != null) {
                        Image(
                            bitmap = capturedPhoto!!.asImageBitmap(),
                            contentDescription = "Student Photo",
                            modifier = Modifier.size(96.dp)
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.CameraAlt,
                                contentDescription = "Take Photo",
                                tint = DeepForestTeal,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Take Photo",
                                fontSize = 10.sp,
                                color = DeepForestTeal,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Info Column (Dark Moss Gray typography)
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = student.fullName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = DarkMossGray
                    )
                    Text(
                        text = "ID: ${student.admissionNo}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = DeepForestTeal,
                        fontFamily = FontFamily.Monospace
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Grade: ${student.gradeLevel}",
                        fontSize = 11.sp,
                        color = DarkMossGrayMuted
                    )
                    Text(
                        text = "Location: ${student.assignedLocation}",
                        fontSize = 11.sp,
                        color = DarkMossGrayMuted
                    )
                    Text(
                        text = "Blood Group: ${student.bloodGroup} • DOB: ${student.dob}",
                        fontSize = 10.sp,
                        color = DarkMossGraySubtle
                    )
                }
            }

            // Card Footer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PaleSageOffWhite)
                    .border(BorderStroke(1.dp, PastelSeafoamLight))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Emergency: ${student.emergencyContact}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = SoftAmberPeachDark
                        )
                        Text(
                            text = "Guardian: ${student.guardianName} (${student.guardianContact})",
                            fontSize = 9.sp,
                            color = DarkMossGraySubtle
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = "Card Security Barcode",
                        tint = DeepForestTeal,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}
