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

    private val sessionManager = com.example.util.EncryptedSessionManager(context)

    private val _academyGeofence = MutableStateFlow(sessionManager.getAcademyGeofence())
    val academyGeofence: StateFlow<com.example.model.AcademyGeofence> = _academyGeofence.asStateFlow()

    private val _liveCoordinates = MutableStateFlow<com.example.util.GeolocationResult?>(null)
    val liveCoordinates: StateFlow<com.example.util.GeolocationResult?> = _liveCoordinates.asStateFlow()

    fun refreshAcademyGeofence() {
        _academyGeofence.value = sessionManager.getAcademyGeofence()
    }

    fun loadTodayAttendance(teacherId: String) {
        viewModelScope.launch {
            _todayAttendance.value = academyRepository.getTodayAttendanceForPerson(teacherId)
            _academyGeofence.value = sessionManager.getAcademyGeofence()
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

    fun saveClassroomAttendance(records: List<AttendanceRecord>, onComplete: (String) -> Unit) {
        viewModelScope.launch {
            academyRepository.saveClassroomRosterAttendance(records)
            SyncScheduler.triggerImmediateSync(context)
            onComplete("Class attendance for ${records.size} students saved successfully.")
        }
    }

    fun checkInLiveLocation(teacherId: String, teacherName: String, onDone: (String, Boolean) -> Unit) {
        _isCheckingLocation.value = true
        viewModelScope.launch {
            val geo = LocationHelper.getLiveLocation(context)
            _liveCoordinates.value = geo
            val geofence = sessionManager.getAcademyGeofence()
            _academyGeofence.value = geofence

            val distance = LocationHelper.calculateDistanceMeters(
                startLat = geo.latitude,
                startLon = geo.longitude,
                endLat = geofence.latitude,
                endLon = geofence.longitude
            )
            val isWithin = distance <= geofence.radiusMeters
            val status = if (isWithin) "PRESENT" else "OUT_OF_COVERAGE"
            val locationName = if (isWithin) {
                "${geofence.campusName} (Verified Inside Perimeter)"
            } else {
                "OUT OF COVERAGE (${distance.toInt()}m from Academy)"
            }

            val baseRecord = academyRepository.recordAttendanceCheckIn(
                personId = teacherId,
                personName = teacherName,
                role = UserRole.TEACHER,
                latitude = geo.latitude,
                longitude = geo.longitude,
                locationName = locationName
            )

            val fullRecord = baseRecord.copy(
                status = status,
                isWithinGeofence = isWithin,
                checkInDistanceMeters = distance,
                assignedRadiusMeters = geofence.radiusMeters
            )
            academyRepository.saveClassroomRosterAttendance(listOf(fullRecord))

            _todayAttendance.value = fullRecord
            _isCheckingLocation.value = false
            SyncScheduler.triggerImmediateSync(context)

            val msg = if (isWithin) {
                "Verified In Coverage at ${geofence.campusName} (${distance.toInt()}m from center, Limit: ${geofence.radiusMeters.toInt()}m)"
            } else {
                "OUT OF COVERAGE: You are ${distance.toInt()}m away from Academy (Coverage perimeter is ${geofence.radiusMeters.toInt()}m)"
            }
            onDone(msg, isWithin)
        }
    }

    fun checkOutLiveLocation(teacherId: String, onDone: (String, Boolean) -> Unit) {
        _isCheckingLocation.value = true
        viewModelScope.launch {
            val geo = LocationHelper.getLiveLocation(context)
            _liveCoordinates.value = geo
            val geofence = sessionManager.getAcademyGeofence()

            val distance = LocationHelper.calculateDistanceMeters(
                startLat = geo.latitude,
                startLon = geo.longitude,
                endLat = geofence.latitude,
                endLon = geofence.longitude
            )
            val isWithin = distance <= geofence.radiusMeters
            val locationName = if (isWithin) {
                "${geofence.campusName} (Verified Departure)"
            } else {
                "OUT OF COVERAGE Check-Out (${distance.toInt()}m from Academy)"
            }

            val updated = academyRepository.recordAttendanceCheckOut(
                personId = teacherId,
                latitude = geo.latitude,
                longitude = geo.longitude,
                locationName = locationName
            )

            val finalRecord = updated?.copy(
                checkOutDistanceMeters = distance,
                checkOutLocationName = locationName
            )
            if (finalRecord != null) {
                academyRepository.saveClassroomRosterAttendance(listOf(finalRecord))
            }

            _todayAttendance.value = finalRecord ?: updated
            _isCheckingLocation.value = false
            SyncScheduler.triggerImmediateSync(context)

            val msg = if (isWithin) {
                "Check-out verified within coverage perimeter (${distance.toInt()}m)"
            } else {
                "Check-out logged OUT OF COVERAGE (${distance.toInt()}m from Academy)"
            }
            onDone(msg, isWithin)
        }
    }
}
