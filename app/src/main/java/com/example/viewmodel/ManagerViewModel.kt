package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AcademyRepository
import com.example.data.AppDatabase
import com.example.model.AttendanceRecord
import com.example.model.CustomFeature
import com.example.model.DonationRecord
import com.example.model.MonthlyDonorReport
import com.example.model.Sponsor
import com.example.model.Student
import com.example.model.Teacher
import com.example.model.TestRecord
import com.example.model.UserRole
import com.example.sync.SyncScheduler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ManagerViewModel(
    private val context: Context,
    private val academyRepository: AcademyRepository,
    private val database: AppDatabase
) : ViewModel() {

    val teachers: StateFlow<List<Teacher>> = academyRepository.allTeachers.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val students: StateFlow<List<Student>> = academyRepository.allStudents.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val records: StateFlow<List<TestRecord>> = academyRepository.allTestRecords.stateIn(
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

    private val _selectedDonorReport = MutableStateFlow<MonthlyDonorReport?>(null)
    val selectedDonorReport: StateFlow<MonthlyDonorReport?> = _selectedDonorReport.asStateFlow()

    fun addStudent(student: Student) {
        viewModelScope.launch { academyRepository.insertStudent(student) }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch { academyRepository.deleteStudent(student) }
    }

    fun addFeature(feature: CustomFeature) {
        viewModelScope.launch { academyRepository.insertFeature(feature) }
    }

    fun deleteFeature(feature: CustomFeature) {
        viewModelScope.launch { academyRepository.deleteFeature(feature) }
    }

    fun recordAttendanceCheckIn(
        personId: String,
        personName: String,
        role: UserRole,
        latitude: Double?,
        longitude: Double?,
        locationName: String
    ) {
        viewModelScope.launch {
            academyRepository.recordAttendanceCheckIn(
                personId = personId,
                personName = personName,
                role = role,
                latitude = latitude,
                longitude = longitude,
                locationName = locationName
            )
            SyncScheduler.triggerImmediateSync(context)
        }
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
}
