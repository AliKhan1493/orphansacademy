package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AcademyRepository
import com.example.data.AppDatabase
import com.example.data.AuthRepository
import com.example.data.AuthResult
import com.example.model.AttendanceRecord
import com.example.model.CustomFeature
import com.example.model.DonationRecord
import com.example.model.ExamTest
import com.example.model.MonthlyDonorReport
import com.example.model.Sponsor
import com.example.model.Student
import com.example.model.Teacher
import com.example.model.UserAccount
import com.example.model.UserRole
import com.example.sync.SyncScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AdminViewModel(
    private val context: Context,
    private val academyRepository: AcademyRepository,
    private val authRepository: AuthRepository,
    private val database: AppDatabase
) : ViewModel() {

    val teachers: StateFlow<List<Teacher>> = academyRepository.allTeachers.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val students: StateFlow<List<Student>> = academyRepository.allStudents.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val tests: StateFlow<List<ExamTest>> = academyRepository.allTests.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val features: StateFlow<List<CustomFeature>> = academyRepository.allFeatures.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val sponsors: StateFlow<List<Sponsor>> = academyRepository.allSponsors.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val donations: StateFlow<List<DonationRecord>> = academyRepository.allDonations.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val attendance: StateFlow<List<AttendanceRecord>> = academyRepository.allAttendance.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val userAccounts: StateFlow<List<UserAccount>> = database.userAccountDao().getAllUsers().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val pendingSyncCount: StateFlow<Int> = database.userAccountDao().getPendingSyncCount().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), 0
    )

    private val _selectedDonorReport = MutableStateFlow<MonthlyDonorReport?>(null)
    val selectedDonorReport: StateFlow<MonthlyDonorReport?> = _selectedDonorReport.asStateFlow()

    private val sessionManager = com.example.util.EncryptedSessionManager(context)

    private val _academyGeofence = MutableStateFlow(sessionManager.getAcademyGeofence())
    val academyGeofence: StateFlow<com.example.model.AcademyGeofence> = _academyGeofence.asStateFlow()

    init {
        viewModelScope.launch {
            academyRepository.seedInitialAcademyDataIfEmpty()
        }
    }

    fun updateAcademyGeofence(latitude: Double, longitude: Double, radiusMeters: Double, campusName: String) {
        sessionManager.saveAcademyGeofence(latitude, longitude, radiusMeters, campusName)
        _academyGeofence.value = sessionManager.getAcademyGeofence()
    }

    fun setGeofenceToCurrentDeviceLocation(radiusMeters: Double = 250.0, campusName: String = "Main Campus", onResult: (com.example.model.AcademyGeofence) -> Unit = {}) {
        viewModelScope.launch {
            val liveGeo = com.example.util.LocationHelper.getLiveLocation(context)
            sessionManager.saveAcademyGeofence(liveGeo.latitude, liveGeo.longitude, radiusMeters, campusName)
            val updated = sessionManager.getAcademyGeofence()
            _academyGeofence.value = updated
            onResult(updated)
        }
    }

    fun bulkAssignStudentsToSponsor(sponsorId: String, studentAdmissionNos: List<String>, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            academyRepository.bulkAssignStudentsToSponsor(sponsorId, studentAdmissionNos)
            onComplete()
        }
    }

    fun addTeacher(teacher: Teacher, password: String, isOnline: Boolean) {
        viewModelScope.launch {
            academyRepository.insertTeacher(teacher)
            authRepository.createUserByAdmin(
                displayName = teacher.fullName,
                email = teacher.email,
                password = password,
                role = UserRole.TEACHER,
                assignedLocation = teacher.assignedLocation,
                studentAdmissionNo = null,
                isOnline = isOnline
            )
        }
    }

    fun deleteTeacher(teacher: Teacher) {
        viewModelScope.launch { academyRepository.deleteTeacher(teacher) }
    }

    fun addStudent(student: Student) {
        viewModelScope.launch { academyRepository.insertStudent(student) }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch { academyRepository.deleteStudent(student) }
    }

    fun addTest(test: ExamTest) {
        viewModelScope.launch { academyRepository.insertTest(test) }
    }

    fun deleteTest(test: ExamTest) {
        viewModelScope.launch { academyRepository.deleteTest(test) }
    }

    fun addFeature(feature: CustomFeature) {
        viewModelScope.launch { academyRepository.insertFeature(feature) }
    }

    fun deleteFeature(feature: CustomFeature) {
        viewModelScope.launch { academyRepository.deleteFeature(feature) }
    }

    fun addSponsor(sponsor: Sponsor) {
        viewModelScope.launch { academyRepository.insertSponsor(sponsor) }
    }

    fun deleteSponsor(sponsor: Sponsor) {
        viewModelScope.launch { academyRepository.deleteSponsor(sponsor) }
    }

    fun recordDonation(donation: DonationRecord) {
        viewModelScope.launch {
            academyRepository.insertDonation(donation)
            SyncScheduler.triggerImmediateSync(context)
        }
    }

    fun generateDonorReport(sponsorId: String, monthYear: String = "October 2026") {
        viewModelScope.launch {
            val report = academyRepository.generateMonthlyDonorReport(sponsorId, monthYear)
            _selectedDonorReport.value = report
        }
    }

    fun clearDonorReport() {
        _selectedDonorReport.value = null
    }

    fun updateUserRole(targetUid: String, newRole: UserRole) {
        viewModelScope.launch { authRepository.updateRoleByAdmin(targetUid, newRole) }
    }

    fun createUser(
        name: String,
        email: String,
        pass: String,
        role: UserRole,
        loc: String?,
        admission: String?,
        isOnline: Boolean,
        onResult: (AuthResult) -> Unit
    ) {
        viewModelScope.launch {
            val result = authRepository.createUserByAdmin(
                displayName = name,
                email = email,
                password = pass,
                role = role,
                assignedLocation = loc,
                studentAdmissionNo = admission,
                isOnline = isOnline
            )
            onResult(result)
        }
    }

    fun resetUserPassword(targetUid: String, newPass: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = authRepository.resetUserPassword(targetUid, newPass)
            onComplete(ok)
        }
    }

    fun deleteUserAccount(targetUid: String, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            val ok = authRepository.deleteUserAccount(targetUid)
            onComplete(ok)
        }
    }

    fun forceSync(onComplete: (Int) -> Unit) {
        viewModelScope.launch {
            val count = authRepository.syncPendingAccounts()
            SyncScheduler.triggerImmediateSync(context)
            onComplete(count)
        }
    }
}
