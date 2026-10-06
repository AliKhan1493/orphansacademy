package com.example.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AuthRepository
import com.example.data.AuthResult
import com.example.model.UserAccount
import com.example.model.UserRole
import com.example.util.EncryptedSessionManager
import com.example.util.NetworkMonitor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AuthUiState(
    val currentUser: UserAccount? = null,
    val isOnline: Boolean = true,
    val isSimulatedOffline: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val infoMessage: String? = null
)

class AuthViewModel(
    private val context: Context,
    private val authRepository: AuthRepository,
    private val networkMonitor: NetworkMonitor,
    private val sessionManager: EncryptedSessionManager
) : ViewModel() {

    private val _isSimulatedOffline = MutableStateFlow(false)
    val isSimulatedOffline: StateFlow<Boolean> = _isSimulatedOffline.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage: StateFlow<String?> = _infoMessage.asStateFlow()

    val realIsOnline: StateFlow<Boolean> = networkMonitor.isOnline.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    val currentUser: StateFlow<UserAccount?> = authRepository.currentUser

    val effectiveIsOnline: StateFlow<Boolean> = combine(
        realIsOnline,
        _isSimulatedOffline
    ) { real, simulated ->
        real && !simulated
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val uiState: StateFlow<AuthUiState> = combine(
        currentUser,
        effectiveIsOnline,
        _isLoading,
        _errorMessage,
        _infoMessage
    ) { user, isOnline, loading, err, info ->
        AuthUiState(
            currentUser = user,
            isOnline = isOnline,
            isSimulatedOffline = _isSimulatedOffline.value,
            isLoading = loading,
            errorMessage = err,
            infoMessage = info
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), AuthUiState())

    init {
        viewModelScope.launch {
            authRepository.initializeDefaultAccountsIfEmpty()
        }
    }

    fun toggleAirplaneSimulation() {
        _isSimulatedOffline.value = !_isSimulatedOffline.value
    }

    fun signIn(email: String, pass: String) {
        _isLoading.value = true
        _errorMessage.value = null
        _infoMessage.value = null

        viewModelScope.launch {
            val online = effectiveIsOnline.value
            val result = authRepository.signIn(email, pass, online)
            _isLoading.value = false
            when (result) {
                is AuthResult.Success -> {
                    _infoMessage.value = result.message
                    sessionManager.saveSession(
                        uid = result.user.uid,
                        role = result.user.role.name,
                        token = "enc_token_${result.user.uid}_${System.currentTimeMillis()}"
                    )
                }
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    fun signUp(
        email: String,
        pass: String,
        name: String,
        role: UserRole,
        location: String?,
        admissionNo: String?
    ) {
        _isLoading.value = true
        _errorMessage.value = null
        _infoMessage.value = null

        viewModelScope.launch {
            val online = effectiveIsOnline.value
            val result = authRepository.signUp(
                email = email,
                password = pass,
                displayName = name,
                role = role,
                assignedLocation = location,
                studentAdmissionNo = admissionNo,
                isOnline = online
            )
            _isLoading.value = false
            when (result) {
                is AuthResult.Success -> {
                    _infoMessage.value = result.message
                    sessionManager.saveSession(
                        uid = result.user.uid,
                        role = result.user.role.name,
                        token = "enc_token_${result.user.uid}_${System.currentTimeMillis()}"
                    )
                }
                is AuthResult.Error -> {
                    _errorMessage.value = result.message
                }
            }
        }
    }

    fun switchUserQuick(account: UserAccount) {
        authRepository.switchUserQuick(account)
        sessionManager.saveSession(
            uid = account.uid,
            role = account.role.name,
            token = "quick_switch_${account.uid}"
        )
    }

    fun signOut() {
        sessionManager.clearSession()
        authRepository.signOut()
        _errorMessage.value = null
        _infoMessage.value = null
    }

    fun clearMessages() {
        _errorMessage.value = null
        _infoMessage.value = null
    }
}
