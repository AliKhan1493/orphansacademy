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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.data.AcademyRepository
import com.example.data.AppDatabase
import com.example.data.AuthRepository
import com.example.data.AuthResult
import com.example.model.CustomFeature
import com.example.model.ExamTest
import com.example.model.Student
import com.example.model.SyllabusTopic
import com.example.model.Teacher
import com.example.model.TestRecord
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
import com.example.util.NetworkMonitor
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var authRepository: AuthRepository
    private lateinit var academyRepository: AcademyRepository
    private lateinit var networkMonitor: NetworkMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize SQLite Room Database & Repositories
        database = AppDatabase.getInstance(applicationContext)
        authRepository = AuthRepository(applicationContext, database.userAccountDao())
        academyRepository = AcademyRepository(
            database.studentDao(),
            database.teacherDao(),
            database.syllabusDao(),
            database.testDao(),
            database.testRecordDao(),
            database.featureDao()
        )
        networkMonitor = NetworkMonitor(applicationContext)

        // Seed initial demo data for offline/first launch
        lifecycleScope.launch {
            authRepository.initializeDefaultAccountsIfEmpty()
            academyRepository.seedInitialAcademyDataIfEmpty()
        }

        setContent {
            MyApplicationTheme {
                val coroutineScope = rememberCoroutineScope()

                // Network observation
                val realIsOnline by networkMonitor.isOnline.collectAsState(initial = true)
                var isSimulatedOffline by remember { mutableStateOf(false) }
                val effectiveIsOnline = realIsOnline && !isSimulatedOffline

                // Auth state
                val currentUser by authRepository.currentUser.collectAsState()
                var isAuthLoading by remember { mutableStateOf(false) }
                var authErrorMessage by remember { mutableStateOf<String?>(null) }
                var authInfoMessage by remember { mutableStateOf<String?>(null) }

                // Academy collections
                val students by academyRepository.allStudents.collectAsState(initial = emptyList())
                val teachers by academyRepository.allTeachers.collectAsState(initial = emptyList())
                val syllabusTopics by academyRepository.allSyllabusTopics.collectAsState(initial = emptyList())
                val tests by academyRepository.allTests.collectAsState(initial = emptyList())
                val testRecords by academyRepository.allTestRecords.collectAsState(initial = emptyList())
                val features by academyRepository.allFeatures.collectAsState(initial = emptyList())

                val userAccounts by database.userAccountDao().getAllUsers().collectAsState(initial = emptyList())
                val pendingSyncCount by database.userAccountDao().getPendingSyncCount().collectAsState(initial = 0)

                // Quick role switcher dialog state
                var showQuickRoleSwitcher by remember { mutableStateOf(false) }

                // Auto sync when coming back online
                LaunchedEffect(effectiveIsOnline) {
                    if (effectiveIsOnline) {
                        val synced = authRepository.syncPendingAccounts()
                        if (synced > 0) {
                            Toast.makeText(
                                applicationContext,
                                "Reconnected: Synced $synced local account(s) with Cloud",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                if (currentUser == null) {
                    AuthScreen(
                        isOnline = effectiveIsOnline,
                        isSimulatedOffline = isSimulatedOffline,
                        onToggleAirplaneSimulation = {
                            isSimulatedOffline = !isSimulatedOffline
                        },
                        onSignIn = { email, password ->
                            isAuthLoading = true
                            authErrorMessage = null
                            authInfoMessage = null
                            coroutineScope.launch {
                                val result = authRepository.signIn(email, password, effectiveIsOnline)
                                isAuthLoading = false
                                when (result) {
                                    is AuthResult.Success -> {
                                        authInfoMessage = result.message
                                        Toast.makeText(applicationContext, result.message, Toast.LENGTH_LONG).show()
                                    }
                                    is AuthResult.Error -> {
                                        authErrorMessage = result.message
                                    }
                                }
                            }
                        },
                        onSignUp = { email, password, displayName, role, assignedLocation, studentAdmissionNo ->
                            isAuthLoading = true
                            authErrorMessage = null
                            authInfoMessage = null
                            coroutineScope.launch {
                                val result = authRepository.signUp(
                                    email = email,
                                    password = password,
                                    displayName = displayName,
                                    role = role,
                                    assignedLocation = assignedLocation,
                                    studentAdmissionNo = studentAdmissionNo,
                                    isOnline = effectiveIsOnline
                                )
                                isAuthLoading = false
                                when (result) {
                                    is AuthResult.Success -> {
                                        authInfoMessage = result.message
                                        Toast.makeText(applicationContext, result.message, Toast.LENGTH_LONG).show()
                                    }
                                    is AuthResult.Error -> {
                                        authErrorMessage = result.message
                                    }
                                }
                            }
                        },
                        isLoading = isAuthLoading,
                        errorMessage = authErrorMessage,
                        infoMessage = authInfoMessage
                    )
                } else {
                    RoleGate(
                        currentUser = currentUser!!,
                        isOnline = effectiveIsOnline,
                        isSimulatedOffline = isSimulatedOffline,
                        onToggleAirplaneSimulation = {
                            isSimulatedOffline = !isSimulatedOffline
                        },
                        onSignOut = {
                            authRepository.signOut()
                            Toast.makeText(applicationContext, "Signed Out", Toast.LENGTH_SHORT).show()
                        },
                        onQuickSwitchClick = null,
                        students = students,
                        teachers = teachers,
                        syllabusTopics = syllabusTopics,
                        tests = tests,
                        records = testRecords,
                        features = features,
                        userAccounts = userAccounts,
                        pendingSyncCount = pendingSyncCount,
                        onAddStudent = { student ->
                            coroutineScope.launch { academyRepository.insertStudent(student) }
                        },
                        onDeleteStudent = { student ->
                            coroutineScope.launch { academyRepository.deleteStudent(student) }
                        },
                        onAddTeacher = { teacher, password ->
                            coroutineScope.launch {
                                academyRepository.insertTeacher(teacher)
                                authRepository.createUserByAdmin(
                                    displayName = teacher.fullName,
                                    email = teacher.email,
                                    password = password,
                                    role = UserRole.TEACHER,
                                    assignedLocation = teacher.assignedLocation,
                                    studentAdmissionNo = null,
                                    isOnline = effectiveIsOnline
                                )
                                Toast.makeText(applicationContext, "Faculty appointed & login created", Toast.LENGTH_SHORT).show()
                            }
                        },
                        onDeleteTeacher = { teacher ->
                            coroutineScope.launch { academyRepository.deleteTeacher(teacher) }
                        },
                        onAddSyllabusTopic = { topic ->
                            coroutineScope.launch { academyRepository.insertTopic(topic) }
                        },
                        onToggleTopicCompleted = { topic ->
                            coroutineScope.launch {
                                academyRepository.updateTopic(topic.copy(isCompleted = !topic.isCompleted))
                            }
                        },
                        onAddTest = { test ->
                            coroutineScope.launch { academyRepository.insertTest(test) }
                        },
                        onDeleteTest = { test ->
                            coroutineScope.launch { academyRepository.deleteTest(test) }
                        },
                        onAddTestRecord = { record ->
                            coroutineScope.launch { academyRepository.insertTestRecord(record) }
                        },
                        onAddFeature = { feature ->
                            coroutineScope.launch { academyRepository.insertFeature(feature) }
                        },
                        onDeleteFeature = { feature ->
                            coroutineScope.launch { academyRepository.deleteFeature(feature) }
                        },
                        onUpdateUserRole = { targetUid, newRole ->
                            coroutineScope.launch {
                                authRepository.updateRoleByAdmin(targetUid, newRole)
                                Toast.makeText(
                                    applicationContext,
                                    "Role Updated to ${newRole.name}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                        onCreateUser = { name, email, pass, role, loc, admission ->
                            coroutineScope.launch {
                                val result = authRepository.createUserByAdmin(
                                    displayName = name,
                                    email = email,
                                    password = pass,
                                    role = role,
                                    assignedLocation = loc,
                                    studentAdmissionNo = admission,
                                    isOnline = effectiveIsOnline
                                )
                                when (result) {
                                    is AuthResult.Success -> Toast.makeText(applicationContext, "Created user ${role.name}: $email", Toast.LENGTH_SHORT).show()
                                    is AuthResult.Error -> Toast.makeText(applicationContext, "Error: ${result.message}", Toast.LENGTH_LONG).show()
                                }
                            }
                        },
                        onResetUserPassword = { targetUid, newPass ->
                            coroutineScope.launch {
                                val ok = authRepository.resetUserPassword(targetUid, newPass)
                                if (ok) {
                                    Toast.makeText(applicationContext, "Password updated successfully", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(applicationContext, "Failed to update password", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onDeleteUserAccount = { targetUid ->
                            coroutineScope.launch {
                                val ok = authRepository.deleteUserAccount(targetUid)
                                if (ok) {
                                    Toast.makeText(applicationContext, "User account deleted", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        onForceSync = {
                            coroutineScope.launch {
                                val count = authRepository.syncPendingAccounts()
                                Toast.makeText(
                                    applicationContext,
                                    if (count > 0) "Synced $count pending record(s) to Firebase" else "All accounts already synced",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }

                // Quick Role Switcher Dialog (optional admin utility)
                if (showQuickRoleSwitcher) {
                    QuickRoleSwitcherDialog(
                        currentUser = currentUser,
                        userAccounts = userAccounts,
                        onSelectAccount = { selectedAccount ->
                            authRepository.switchUserQuick(selectedAccount)
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
