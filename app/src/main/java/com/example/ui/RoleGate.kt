package com.example.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import com.example.model.CustomFeature
import com.example.model.ExamTest
import com.example.model.Student
import com.example.model.SyllabusTopic
import com.example.model.Teacher
import com.example.model.TestRecord
import com.example.model.UserAccount
import com.example.model.UserRole
import com.example.ui.theme.DarkMossGray
import com.example.ui.theme.DarkMossGrayMuted
import com.example.ui.theme.DeepForestTeal
import com.example.ui.theme.PaleSageOffWhite
import com.example.ui.theme.PastelSeafoam
import androidx.compose.foundation.background

data class NavTabItem(
    val title: String,
    val icon: ImageVector,
    val testTag: String
)

@Composable
fun RoleGate(
    currentUser: UserAccount,
    isOnline: Boolean,
    isSimulatedOffline: Boolean,
    onToggleAirplaneSimulation: () -> Unit,
    onSignOut: () -> Unit,
    onQuickSwitchClick: (() -> Unit)? = null,
    // Data collections
    students: List<Student>,
    teachers: List<Teacher>,
    syllabusTopics: List<SyllabusTopic>,
    tests: List<ExamTest>,
    records: List<TestRecord>,
    features: List<CustomFeature>,
    userAccounts: List<UserAccount>,
    pendingSyncCount: Int,
    // Action callbacks
    onAddStudent: (Student) -> Unit,
    onDeleteStudent: (Student) -> Unit,
    onAddTeacher: (Teacher, String) -> Unit,
    onDeleteTeacher: (Teacher) -> Unit,
    onAddSyllabusTopic: (SyllabusTopic) -> Unit,
    onToggleTopicCompleted: (SyllabusTopic) -> Unit,
    onAddTest: (ExamTest) -> Unit,
    onDeleteTest: (ExamTest) -> Unit,
    onAddTestRecord: (TestRecord) -> Unit,
    onAddFeature: (CustomFeature) -> Unit,
    onDeleteFeature: (CustomFeature) -> Unit,
    onUpdateUserRole: (targetUid: String, newRole: UserRole) -> Unit,
    onCreateUser: (name: String, email: String, password: String, role: UserRole, location: String?, admissionNo: String?) -> Unit = { _, _, _, _, _, _ -> },
    onResetUserPassword: (targetUid: String, newPassword: String) -> Unit = { _, _ -> },
    onDeleteUserAccount: (targetUid: String) -> Unit = {},
    onForceSync: () -> Unit
) {
    var selectedTabIndex by remember(currentUser.role) { mutableIntStateOf(0) }

    // Dynamic Navigation tabs computed by active role
    val tabs = remember(currentUser.role) {
        when (currentUser.role) {
            UserRole.ADMIN -> listOf(
                NavTabItem("Teachers", Icons.Default.School, "nav_tab_admin_teachers"),
                NavTabItem("Students", Icons.Default.People, "nav_tab_admin_students"),
                NavTabItem("Tests & A4", Icons.Default.PictureAsPdf, "nav_tab_admin_tests"),
                NavTabItem("Features", Icons.Default.Widgets, "nav_tab_admin_features"),
                NavTabItem("Admin Settings", Icons.Default.Security, "nav_tab_admin_settings")
            )
            UserRole.MANAGER -> listOf(
                NavTabItem("Teachers & Loc", Icons.Default.LocationOn, "nav_tab_manager_teachers"),
                NavTabItem("Students & ID", Icons.Default.Badge, "nav_tab_manager_students"),
                NavTabItem("Test Reports", Icons.Default.Assessment, "nav_tab_manager_reports"),
                NavTabItem("Features", Icons.Default.Widgets, "nav_tab_manager_features")
            )
            UserRole.TEACHER -> listOf(
                NavTabItem("My Location", Icons.Default.MeetingRoom, "nav_tab_teacher_location"),
                NavTabItem("My Students", Icons.Default.People, "nav_tab_teacher_students"),
                NavTabItem("Syllabus & Tests", Icons.Default.PictureAsPdf, "nav_tab_teacher_tests"),
                NavTabItem("Test Records", Icons.Default.Grade, "nav_tab_teacher_records")
            )
            UserRole.STUDENT -> listOf(
                NavTabItem("My Digital ID", Icons.Default.Badge, "nav_tab_student_id"),
                NavTabItem("Syllabus", Icons.Default.MenuBook, "nav_tab_student_syllabus"),
                NavTabItem("My Exam Results", Icons.Default.Assessment, "nav_tab_student_results")
            )
        }
    }

    Scaffold(
        topBar = {
            Column {
                NetworkStatusBanner(
                    isOnline = isOnline,
                    isSimulatedOffline = isSimulatedOffline,
                    onToggleAirplaneSimulation = onToggleAirplaneSimulation,
                    pendingSyncCount = pendingSyncCount,
                    onSyncNow = onForceSync
                )
                AcademyTopAppBar(
                    currentUser = currentUser,
                    onSignOut = onSignOut,
                    onQuickSwitchClick = onQuickSwitchClick
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = Color.White,
                modifier = Modifier.testTag("role_navigation_bar")
            ) {
                tabs.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                        label = { Text(text = item.title, fontSize = 10.sp, maxLines = 1) },
                        modifier = Modifier.testTag(item.testTag),
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = DeepForestTeal,
                            selectedTextColor = DeepForestTeal,
                            unselectedIconColor = DarkMossGrayMuted,
                            unselectedTextColor = DarkMossGrayMuted,
                            indicatorColor = PastelSeafoam
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(PaleSageOffWhite)
                .padding(innerPadding)
        ) {
            when (currentUser.role) {
                UserRole.ADMIN -> {
                    when (selectedTabIndex) {
                        0 -> AdminTeachersTab(teachers, onAddTeacher, onDeleteTeacher)
                        1 -> AdminStudentsTab(students, onAddStudent, onDeleteStudent)
                        2 -> AdminTestsTab(tests, onAddTest, onDeleteTest)
                        3 -> AdminFeaturesTab(features, onAddFeature, onDeleteFeature)
                        4 -> AdminSettingsTab(
                            userAccounts = userAccounts,
                            onCreateUser = onCreateUser,
                            onResetPassword = onResetUserPassword,
                            onDeleteUser = onDeleteUserAccount,
                            onUpdateUserRole = onUpdateUserRole,
                            onForceSync = onForceSync,
                            isOnline = isOnline
                        )
                    }
                }
                UserRole.MANAGER -> {
                    when (selectedTabIndex) {
                        0 -> ManagerTeachersViewTab(teachers)
                        1 -> ManagerStudentsAndIdTab(students, onAddStudent, onDeleteStudent)
                        2 -> ManagerTestReportsTab(records)
                        3 -> AdminFeaturesTab(features, onAddFeature, onDeleteFeature, isManagerMode = true)
                    }
                }
                UserRole.TEACHER -> {
                    when (selectedTabIndex) {
                        0 -> TeacherLocationTab(
                            currentUser = currentUser,
                            syllabusTopics = syllabusTopics,
                            onAddSyllabusTopic = onAddSyllabusTopic,
                            onToggleTopicCompleted = onToggleTopicCompleted
                        )
                        1 -> TeacherStudentsRosterTab(students = students)
                        2 -> TeacherTestsAndPdfTab(
                            tests = tests,
                            onAddTest = onAddTest,
                            teacherName = currentUser.displayName
                        )
                        3 -> TeacherTestRecordsTab(
                            records = records,
                            students = students,
                            tests = tests,
                            onAddRecord = onAddTestRecord
                        )
                    }
                }
                UserRole.STUDENT -> {
                    val myStudentProfile = students.firstOrNull {
                        it.admissionNo.equals(currentUser.studentAdmissionNo, ignoreCase = true)
                    }
                    when (selectedTabIndex) {
                        0 -> StudentDigitalIdTab(
                            currentUser = currentUser,
                            studentProfile = myStudentProfile
                        )
                        1 -> StudentSyllabusTab(
                            syllabusTopics = syllabusTopics,
                            tests = tests
                        )
                        2 -> StudentExamResultsTab(
                            studentAdmissionNo = currentUser.studentAdmissionNo ?: "OA-2026-042",
                            allRecords = records
                        )
                    }
                }
            }
        }
    }
}
