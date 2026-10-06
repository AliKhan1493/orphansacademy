package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.Student
import com.example.model.UserAccount
import com.example.model.UserRole
import com.example.ui.AuthScreen
import com.example.ui.RoleGate
import com.example.ui.getRoleColor
import com.example.ui.theme.DarkMossGray
import com.example.ui.theme.DarkMossGrayMuted
import com.example.ui.theme.DeepForestTeal
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.PastelSeafoamLight
import com.example.viewmodel.AdminViewModel
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.ManagerViewModel
import com.example.viewmodel.StudentViewModel
import com.example.viewmodel.TeacherViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val appContainer = (application as AcademyApplication).container

        setContent {
            MyApplicationTheme {
                val authViewModel: AuthViewModel = viewModel(factory = appContainer.viewModelFactory)
                val adminViewModel: AdminViewModel = viewModel(factory = appContainer.viewModelFactory)
                val managerViewModel: ManagerViewModel = viewModel(factory = appContainer.viewModelFactory)
                val teacherViewModel: TeacherViewModel = viewModel(factory = appContainer.viewModelFactory)
                val studentViewModel: StudentViewModel = viewModel(factory = appContainer.viewModelFactory)

                // Auth state collection
                val authUiState by authViewModel.uiState.collectAsState()
                val currentUser = authUiState.currentUser

                // Quick role switcher dialog state
                var showQuickRoleSwitcher by remember { mutableStateOf(false) }

                // Auto sync when coming back online
                LaunchedEffect(authUiState.isOnline) {
                    if (authUiState.isOnline) {
                        appContainer.triggerSync()
                    }
                }

                if (currentUser == null) {
                    AuthScreen(
                        isOnline = authUiState.isOnline,
                        isSimulatedOffline = authUiState.isSimulatedOffline,
                        onToggleAirplaneSimulation = { authViewModel.toggleAirplaneSimulation() },
                        onSignIn = { email, password -> authViewModel.signIn(email, password) },
                        onSignUp = { email, password, displayName, role, assignedLocation, studentAdmissionNo ->
                            authViewModel.signUp(
                                email = email,
                                pass = password,
                                name = displayName,
                                role = role,
                                location = assignedLocation,
                                admissionNo = studentAdmissionNo
                            )
                        },
                        isLoading = authUiState.isLoading,
                        errorMessage = authUiState.errorMessage,
                        infoMessage = authUiState.infoMessage
                    )
                } else {
                    // Observe active domain collections based on currentUser role
                    val students by when (currentUser.role) {
                        UserRole.ADMIN -> adminViewModel.students.collectAsState()
                        UserRole.MANAGER -> managerViewModel.students.collectAsState()
                        UserRole.TEACHER -> teacherViewModel.students.collectAsState()
                        UserRole.STUDENT -> studentViewModel.allStudents.collectAsState()
                    }

                    val teachers by when (currentUser.role) {
                        UserRole.ADMIN -> adminViewModel.teachers.collectAsState()
                        UserRole.MANAGER -> managerViewModel.teachers.collectAsState()
                        else -> adminViewModel.teachers.collectAsState()
                    }

                    val syllabusTopics by when (currentUser.role) {
                        UserRole.TEACHER -> teacherViewModel.syllabusTopics.collectAsState()
                        UserRole.STUDENT -> studentViewModel.syllabusTopics.collectAsState()
                        else -> teacherViewModel.syllabusTopics.collectAsState()
                    }

                    val tests by when (currentUser.role) {
                        UserRole.ADMIN -> adminViewModel.tests.collectAsState()
                        UserRole.TEACHER -> teacherViewModel.tests.collectAsState()
                        UserRole.STUDENT -> studentViewModel.tests.collectAsState()
                        else -> adminViewModel.tests.collectAsState()
                    }

                    val testRecords by when (currentUser.role) {
                        UserRole.MANAGER -> managerViewModel.records.collectAsState()
                        UserRole.TEACHER -> teacherViewModel.records.collectAsState()
                        UserRole.STUDENT -> studentViewModel.allRecords.collectAsState()
                        else -> teacherViewModel.records.collectAsState()
                    }

                    val features by when (currentUser.role) {
                        UserRole.ADMIN -> adminViewModel.features.collectAsState()
                        UserRole.MANAGER -> managerViewModel.features.collectAsState()
                        else -> adminViewModel.features.collectAsState()
                    }

                    val sponsors by when (currentUser.role) {
                        UserRole.ADMIN -> adminViewModel.sponsors.collectAsState()
                        UserRole.MANAGER -> managerViewModel.sponsors.collectAsState()
                        else -> adminViewModel.sponsors.collectAsState()
                    }

                    val donations by when (currentUser.role) {
                        UserRole.ADMIN -> adminViewModel.donations.collectAsState()
                        UserRole.MANAGER -> managerViewModel.donations.collectAsState()
                        else -> adminViewModel.donations.collectAsState()
                    }

                    val attendance by when (currentUser.role) {
                        UserRole.ADMIN -> adminViewModel.attendance.collectAsState()
                        UserRole.MANAGER -> managerViewModel.attendance.collectAsState()
                        else -> adminViewModel.attendance.collectAsState()
                    }

                    val userAccounts by adminViewModel.userAccounts.collectAsState()
                    val pendingSyncCount by adminViewModel.pendingSyncCount.collectAsState()

                    val selectedDonorReport by when (currentUser.role) {
                        UserRole.ADMIN -> adminViewModel.selectedDonorReport.collectAsState()
                        UserRole.MANAGER -> managerViewModel.selectedDonorReport.collectAsState()
                        else -> remember { mutableStateOf(null) }
                    }

                    // Role-specific attendance tracking
                    val teacherAttendance by teacherViewModel.todayAttendance.collectAsState()
                    val isTeacherCheckingLocation by teacherViewModel.isCheckingLocation.collectAsState()

                    val studentAttendance by studentViewModel.todayAttendance.collectAsState()
                    val isStudentCheckingLocation by studentViewModel.isCheckingLocation.collectAsState()

                    LaunchedEffect(currentUser.uid) {
                        if (currentUser.role == UserRole.TEACHER) {
                            teacherViewModel.loadTodayAttendance(currentUser.uid)
                        } else if (currentUser.role == UserRole.STUDENT) {
                            studentViewModel.loadTodayAttendance(currentUser.studentAdmissionNo ?: "OA-2026-042")
                        }
                    }

                    RoleGate(
                        currentUser = currentUser,
                        isOnline = authUiState.isOnline,
                        isSimulatedOffline = authUiState.isSimulatedOffline,
                        onToggleAirplaneSimulation = { authViewModel.toggleAirplaneSimulation() },
                        onSignOut = {
                            authViewModel.signOut()
                            Toast.makeText(applicationContext, "Signed Out", Toast.LENGTH_SHORT).show()
                        },
                        onQuickSwitchClick = { showQuickRoleSwitcher = true },
                        students = students,
                        teachers = teachers,
                        syllabusTopics = syllabusTopics,
                        tests = tests,
                        records = testRecords,
                        features = features,
                        sponsors = sponsors,
                        donations = donations,
                        attendance = attendance,
                        userAccounts = userAccounts,
                        pendingSyncCount = pendingSyncCount,
                        selectedDonorReport = selectedDonorReport,
                        onDismissDonorReport = {
                            adminViewModel.clearDonorReport()
                            managerViewModel.clearDonorReport()
                        },
                        onAddStudent = { student ->
                            if (currentUser.role == UserRole.ADMIN) adminViewModel.addStudent(student)
                            else managerViewModel.addStudent(student)
                        },
                        onDeleteStudent = { student ->
                            if (currentUser.role == UserRole.ADMIN) adminViewModel.deleteStudent(student)
                            else managerViewModel.deleteStudent(student)
                        },
                        onAddTeacher = { teacher, password ->
                            adminViewModel.addTeacher(teacher, password, authUiState.isOnline)
                            Toast.makeText(applicationContext, "Faculty appointed & login created", Toast.LENGTH_SHORT).show()
                        },
                        onDeleteTeacher = { teacher -> adminViewModel.deleteTeacher(teacher) },
                        onAddSyllabusTopic = { topic -> teacherViewModel.addSyllabusTopic(topic) },
                        onToggleTopicCompleted = { topic -> teacherViewModel.toggleTopicCompleted(topic) },
                        onAddTest = { test ->
                            if (currentUser.role == UserRole.ADMIN) adminViewModel.addTest(test)
                            else teacherViewModel.addTest(test)
                        },
                        onDeleteTest = { test ->
                            if (currentUser.role == UserRole.ADMIN) adminViewModel.deleteTest(test)
                            else teacherViewModel.deleteTest(test)
                        },
                        onAddTestRecord = { record -> teacherViewModel.addTestRecord(record) },
                        onAddFeature = { feature ->
                            if (currentUser.role == UserRole.ADMIN) adminViewModel.addFeature(feature)
                            else managerViewModel.addFeature(feature)
                        },
                        onDeleteFeature = { feature ->
                            if (currentUser.role == UserRole.ADMIN) adminViewModel.deleteFeature(feature)
                            else managerViewModel.deleteFeature(feature)
                        },
                        onAddSponsor = { sponsor ->
                            adminViewModel.addSponsor(sponsor)
                            Toast.makeText(applicationContext, "Sponsor registered", Toast.LENGTH_SHORT).show()
                        },
                        onDeleteSponsor = { sponsor -> adminViewModel.deleteSponsor(sponsor) },
                        onRecordDonation = { donation ->
                            adminViewModel.recordDonation(donation)
                            Toast.makeText(applicationContext, "Donation logged: ${donation.receiptNumber}", Toast.LENGTH_SHORT).show()
                        },
                        onGenerateDonorReport = { sponsorId ->
                            if (currentUser.role == UserRole.ADMIN) adminViewModel.generateDonorReport(sponsorId)
                            else managerViewModel.generateDonorReport(sponsorId)
                        },
                        onUpdateUserRole = { targetUid, newRole ->
                            adminViewModel.updateUserRole(targetUid, newRole)
                            Toast.makeText(applicationContext, "Role Updated to ${newRole.name}", Toast.LENGTH_SHORT).show()
                        },
                        onCreateUser = { name, email, pass, role, loc, admission ->
                            adminViewModel.createUser(name, email, pass, role, loc, admission, authUiState.isOnline) { res ->
                                when (res) {
                                    is com.example.data.AuthResult.Success -> Toast.makeText(applicationContext, "Created user ${role.name}: $email", Toast.LENGTH_SHORT).show()
                                    is com.example.data.AuthResult.Error -> Toast.makeText(applicationContext, "Error: ${res.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        onResetUserPassword = { targetUid, newPass ->
                            adminViewModel.resetUserPassword(targetUid, newPass) { ok ->
                                Toast.makeText(applicationContext, if (ok) "Password updated" else "Failed to update", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDeleteUserAccount = { targetUid ->
                            adminViewModel.deleteUserAccount(targetUid) { ok ->
                                if (ok) Toast.makeText(applicationContext, "User account deleted", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onForceSync = {
                            adminViewModel.forceSync { count ->
                                Toast.makeText(
                                    applicationContext,
                                    if (count > 0) "WorkManager triggered: Synced $count record(s)" else "All records up to date",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        todayAttendance = if (currentUser.role == UserRole.TEACHER) teacherAttendance else studentAttendance,
                        isCheckingLocation = if (currentUser.role == UserRole.TEACHER) isTeacherCheckingLocation else isStudentCheckingLocation,
                        onTeacherCheckIn = {
                            teacherViewModel.checkInLiveLocation(currentUser.uid, currentUser.displayName) { msg ->
                                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onTeacherCheckOut = {
                            teacherViewModel.checkOutLiveLocation(currentUser.uid) { msg ->
                                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onStudentCheckIn = {
                            val admNo = currentUser.studentAdmissionNo ?: "OA-2026-042"
                            studentViewModel.checkInLiveLocation(admNo, currentUser.displayName) { msg ->
                                Toast.makeText(applicationContext, msg, Toast.LENGTH_SHORT).show()
                            }
                        },
                        onCaptureStudentPhoto = { bitmap ->
                            val currentStudent = students.firstOrNull {
                                it.admissionNo.equals(currentUser.studentAdmissionNo, ignoreCase = true)
                            } ?: Student(
                                id = currentUser.uid,
                                admissionNo = currentUser.studentAdmissionNo ?: "OA-2026-042",
                                fullName = currentUser.displayName,
                                gradeLevel = "Grade 10 - STEM",
                                assignedLocation = currentUser.assignedLocation ?: "Campus North Wing - Room 101",
                                guardianName = "Mrs. Fatima Tariq",
                                guardianContact = "+1 555-234-5678"
                            )
                            studentViewModel.savePhotoPermanently(bitmap, currentStudent) { savedPath ->
                                Toast.makeText(applicationContext, "Photo saved permanently to device storage", Toast.LENGTH_SHORT).show()
                            }
                        }
                    )
                }

                // Quick Role Switcher Dialog (utility preview)
                if (showQuickRoleSwitcher) {
                    val userAccountsList by adminViewModel.userAccounts.collectAsState()
                    QuickRoleSwitcherDialog(
                        currentUser = currentUser,
                        userAccounts = userAccountsList,
                        onSelectAccount = { selectedAccount ->
                            authViewModel.switchUserQuick(selectedAccount)
                            showQuickRoleSwitcher = false
                            Toast.makeText(
                                applicationContext,
                                "Switched to ${selectedAccount.role.name} (${selectedAccount.displayName})",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onDismiss = { showQuickRoleSwitcher = false }
                    )
                }
            }
        }
    }
}

@Composable
fun QuickRoleSwitcherDialog(
    currentUser: UserAccount?,
    userAccounts: List<UserAccount>,
    onSelectAccount: (UserAccount) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Switch Active User / Role",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Select any verified tier account to immediately switch active permissions and NavigationBar routing:",
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                )

                Spacer(modifier = Modifier.height(4.dp))

                userAccounts.forEach { account ->
                    val isCurrent = currentUser?.uid == account.uid
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectAccount(account) }
                            .testTag("switch_to_account_${account.uid}"),
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) PastelSeafoamLight else Color.White
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCurrent) DeepForestTeal else Color(0xFFE2E8F0)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(getRoleColor(account.role)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = account.role.name.take(1),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = account.displayName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = DarkMossGray
                                )
                                Text(
                                    text = "${account.role.name} • ${account.email}",
                                    fontSize = 11.sp,
                                    color = DarkMossGrayMuted
                                )
                            }
                            if (isCurrent) {
                                Surface(
                                    color = DeepForestTeal,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
