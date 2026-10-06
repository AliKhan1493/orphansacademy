package com.example.viewmodel

import android.content.Context
import android.graphics.Bitmap
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
import com.example.util.MediaManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class StudentViewModel(
    private val context: Context,
    private val academyRepository: AcademyRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    val syllabusTopics: StateFlow<List<SyllabusTopic>> = academyRepository.allSyllabusTopics.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val tests: StateFlow<List<ExamTest>> = academyRepository.allTests.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allRecords: StateFlow<List<TestRecord>> = academyRepository.allTestRecords.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    val allStudents: StateFlow<List<Student>> = academyRepository.allStudents.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList()
    )

    private val _todayAttendance = MutableStateFlow<AttendanceRecord?>(null)
    val todayAttendance: StateFlow<AttendanceRecord?> = _todayAttendance.asStateFlow()

    private val _isSavingPhoto = MutableStateFlow(false)
    val isSavingPhoto: StateFlow<Boolean> = _isSavingPhoto.asStateFlow()

    private val _isCheckingLocation = MutableStateFlow(false)
    val isCheckingLocation: StateFlow<Boolean> = _isCheckingLocation.asStateFlow()

    fun loadTodayAttendance(admissionNo: String) {
        viewModelScope.launch {
            _todayAttendance.value = academyRepository.getTodayAttendanceForPerson(admissionNo)
        }
    }

    /**
     * Compresses and permanently saves captured photo into private internal filesDir.
     * Updates the Student entity photoUri in the local Room Database so the image never breaks.
     */
    fun savePhotoPermanently(bitmap: Bitmap, student: Student, onSaved: (String) -> Unit) {
        _isSavingPhoto.value = true
        viewModelScope.launch {
            val permanentPath = MediaManager.saveStudentPhotoPermanently(
                context = context,
                bitmap = bitmap,
                admissionNo = student.admissionNo,
                existingPath = student.photoUri
            )

            if (permanentPath != null) {
                val updatedStudent = student.copy(photoUri = permanentPath)
                academyRepository.insertStudent(updatedStudent)
                _isSavingPhoto.value = false
                onSaved(permanentPath)
            } else {
                _isSavingPhoto.value = false
            }
        }
    }

    fun checkInLiveLocation(admissionNo: String, studentName: String, onDone: (String) -> Unit) {
        _isCheckingLocation.value = true
        viewModelScope.launch {
            val geo = LocationHelper.getLiveLocation(context)
            val record = academyRepository.recordAttendanceCheckIn(
                personId = admissionNo,
                personName = studentName,
                role = UserRole.STUDENT,
                latitude = geo.latitude,
                longitude = geo.longitude,
                locationName = geo.campusZone
            )
            _todayAttendance.value = record
            _isCheckingLocation.value = false
            SyncScheduler.triggerImmediateSync(context)
            onDone("Attendance logged at ${geo.campusZone} (${record.checkInTime})")
        }
    }
}
