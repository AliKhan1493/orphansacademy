package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AcademyRepository
import com.example.data.AuthRepository
import com.example.model.AttendanceRecord
import com.example.model.ExamTest
import com.example.model.Student
import com.example.model.SyllabusTopic
import com.example.model.TestRecord
import com.example.model.UserRole
import com.example.sync.SyncScheduler
import com.example.util.LocationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TeacherViewModel(
    private val context: Context,
    private val academyRepository: AcademyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val syllabusTopics: StateFlow<List<SyllabusTopic>> = academyRepository.allSyllabusTopics.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val students: StateFlow<List<Student>> = academyRepository.allStudents.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val tests: StateFlow<List<ExamTest>> = academyRepository.allTests.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val records: StateFlow<List<TestRecord>> = academyRepository.allTestRecords.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _todayAttendance = MutableStateFlow<AttendanceRecord?>(null)
    val todayAttendance: StateFlow<AttendanceRecord?> = _todayAttendance.asStateFlow()

    private val _isCheckingLocation = MutableStateFlow(false)
    val isCheckingLocation: StateFlow<Boolean> = _isCheckingLocation.asStateFlow()

    fun loadTodayAttendance(teacherId: String) {
        viewModelScope.launch {
            _todayAttendance.value = academyRepository.getTodayAttendanceForPerson(teacherId)
        }
    }

    fun addSyllabusTopic(topic: SyllabusTopic) {
        viewModelScope.launch { academyRepository.insertTopic(topic) }
    }

    fun toggleTopicCompleted(topic: SyllabusTopic) {
        viewModelScope.launch {
            academyRepository.updateTopic(topic.copy(isCompleted = !topic.isCompleted))
        }
    }

    fun addTest(test: ExamTest) {
        viewModelScope.launch { academyRepository.insertTest(test) }
    }

    fun deleteTest(test: ExamTest) {
        viewModelScope.launch { academyRepository.deleteTest(test) }
    }

    fun addTestRecord(record: TestRecord) {
        viewModelScope.launch { academyRepository.insertTestRecord(record) }
    }

    fun checkInLiveLocation(teacherId: String, teacherName: String, onDone: (String) -> Unit) {
        _isCheckingLocation.value = true
        viewModelScope.launch {
            val geo = LocationHelper.getLiveLocation(context)
            val record = academyRepository.recordAttendanceCheckIn(
                personId = teacherId,
                personName = teacherName,
                role = UserRole.TEACHER,
                latitude = geo.latitude,
                longitude = geo.longitude,
                locationName = geo.campusZone
            )
            _todayAttendance.value = record
            _isCheckingLocation.value = false
            SyncScheduler.triggerImmediateSync(context)
            onDone("Check-in verified at ${geo.campusZone} (${record.checkInTime})")
        }
    }

    fun checkOutLiveLocation(teacherId: String, onDone: (String) -> Unit) {
        _isCheckingLocation.value = true
        viewModelScope.launch {
            val geo = LocationHelper.getLiveLocation(context)
            val updated = academyRepository.recordAttendanceCheckOut(
                personId = teacherId,
                latitude = geo.latitude,
                longitude = geo.longitude,
                locationName = geo.campusZone
            )
            _todayAttendance.value = updated
            _isCheckingLocation.value = false
            SyncScheduler.triggerImmediateSync(context)
            onDone("Check-out recorded at ${updated?.checkOutTime ?: "now"}")
        }
    }
}
