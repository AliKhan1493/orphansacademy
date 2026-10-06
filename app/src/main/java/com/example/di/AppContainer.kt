package com.example.di

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.data.AcademyRepository
import com.example.data.AppDatabase
import com.example.data.AuthRepository
import com.example.sync.SyncScheduler
import com.example.util.EncryptedSessionManager
import com.example.util.NetworkMonitor
import com.example.viewmodel.AdminViewModel
import com.example.viewmodel.AuthViewModel
import com.example.viewmodel.ManagerViewModel
import com.example.viewmodel.StudentViewModel
import com.example.viewmodel.TeacherViewModel

/**
 * Dependency Injection Container for Clean Architecture in Orphan's Academy.
 * Centralizes lifecycle-scoped and singleton dependencies without manual coupling.
 */
class AppContainer(private val context: Context) {

    val database: AppDatabase by lazy {
        AppDatabase.getInstance(context)
    }

    val encryptedSessionManager: EncryptedSessionManager by lazy {
        EncryptedSessionManager(context)
    }

    val authRepository: AuthRepository by lazy {
        AuthRepository(context, database.userAccountDao())
    }

    val academyRepository: AcademyRepository by lazy {
        AcademyRepository(
            studentDao = database.studentDao(),
            teacherDao = database.teacherDao(),
            syllabusDao = database.syllabusDao(),
            testDao = database.testDao(),
            testRecordDao = database.testRecordDao(),
            featureDao = database.featureDao(),
            sponsorDao = database.sponsorDao(),
            donationDao = database.donationDao(),
            attendanceDao = database.attendanceDao()
        )
    }

    val networkMonitor: NetworkMonitor by lazy {
        NetworkMonitor(context)
    }

    val viewModelFactory: ViewModelProvider.Factory by lazy {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return when {
                    modelClass.isAssignableFrom(AuthViewModel::class.java) -> {
                        AuthViewModel(context, authRepository, networkMonitor, encryptedSessionManager) as T
                    }
                    modelClass.isAssignableFrom(AdminViewModel::class.java) -> {
                        AdminViewModel(context, academyRepository, authRepository, database) as T
                    }
                    modelClass.isAssignableFrom(ManagerViewModel::class.java) -> {
                        ManagerViewModel(context, academyRepository, database) as T
                    }
                    modelClass.isAssignableFrom(TeacherViewModel::class.java) -> {
                        TeacherViewModel(context, academyRepository, authRepository) as T
                    }
                    modelClass.isAssignableFrom(StudentViewModel::class.java) -> {
                        StudentViewModel(context, academyRepository, authRepository) as T
                    }
                    else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
                }
            }
        }
    }

    fun triggerSync() {
        SyncScheduler.triggerImmediateSync(context)
    }

    fun startPeriodicSync() {
        SyncScheduler.schedulePeriodicSync(context)
    }
}
