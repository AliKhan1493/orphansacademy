package com.example.data

import android.content.Context
import com.example.model.UserAccount
import com.example.model.UserRole
import com.example.util.SecurityUtils
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: UserAccount, val isOfflineAuth: Boolean, val message: String) : AuthResult()
    data class Error(val message: String) : AuthResult()
}

class AuthRepository(
    private val context: Context,
    private val userAccountDao: UserAccountDao
) {
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    private val firebaseAuth: FirebaseAuth? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseAuth.getInstance()
        } catch (_: Exception) {
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
            FirebaseFirestore.getInstance()
        } catch (_: Exception) {
            null
        }
    }

    suspend fun initializeDefaultAccountsIfEmpty() = withContext(Dispatchers.IO) {
        // Clean out legacy demo accounts
        userAccountDao.removeOldDemoAccounts()

        // Ensure primary system administrator exists
        val adminEmail = "admin@orphan.com.pk"
        val existingAdmin = userAccountDao.getUserByEmail(adminEmail)
        if (existingAdmin == null) {
            val defaultAdmin = UserAccount(
                uid = "admin_orphan_001",
                email = adminEmail,
                passwordHash = SecurityUtils.hashPassword("Pakistan@14931493"),
                displayName = "Academy Administrator",
                role = UserRole.ADMIN,
                assignedLocation = "Main Campus - Central Administration",
                studentAdmissionNo = null,
                isPendingCloudSync = false,
                lastLoginTimestamp = System.currentTimeMillis()
            )
            userAccountDao.insertUser(defaultAdmin)
        }
    }

    suspend fun signIn(
        email: String,
        password: String,
        isOnline: Boolean
    ): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()

        // If Airplane / Offline mode is selected:
        if (!isOnline) {
            return@withContext performOfflineSignIn(cleanEmail, password)
        }

        // Online flow: Attempt Firebase Auth
        val auth = firebaseAuth
        val db = firestore
        if (auth != null && db != null) {
            try {
                val authResult = auth.signInWithEmailAndPassword(cleanEmail, password).await()
                val firebaseUser = authResult.user
                    ?: return@withContext performOfflineSignIn(cleanEmail, password)

                val uid = firebaseUser.uid
                var userRole = UserRole.STUDENT
                var displayName = firebaseUser.displayName ?: cleanEmail.substringBefore("@")
                var assignedLocation: String? = null
                var studentAdmissionNo: String? = null

                try {
                    val doc = db.collection("users").document(uid).get().await()
                    if (doc.exists()) {
                        val roleStr = doc.getString("role")
                        userRole = UserRole.fromString(roleStr)
                        doc.getString("displayName")?.let { displayName = it }
                        assignedLocation = doc.getString("assignedLocation")
                        studentAdmissionNo = doc.getString("studentAdmissionNo")
                    }
                } catch (_: Exception) {
                    // Firestore read failed, fallback to locally cached role if available
                    val local = userAccountDao.getUserByEmail(cleanEmail)
                    if (local != null) {
                        userRole = local.role
                        assignedLocation = local.assignedLocation
                        studentAdmissionNo = local.studentAdmissionNo
                    }
                }

                // Cache verified account locally in Room with hashed password for offline mode
                val cachedAccount = UserAccount(
                    uid = uid,
                    email = cleanEmail,
                    passwordHash = SecurityUtils.hashPassword(password),
                    displayName = displayName,
                    role = userRole,
                    assignedLocation = assignedLocation,
                    studentAdmissionNo = studentAdmissionNo,
                    isPendingCloudSync = false,
                    lastLoginTimestamp = System.currentTimeMillis()
                )
                userAccountDao.insertUser(cachedAccount)
                _currentUser.value = cachedAccount

                return@withContext AuthResult.Success(
                    user = cachedAccount,
                    isOfflineAuth = false,
                    message = "Online Authentication Verified via Firebase"
                )
            } catch (e: Exception) {
                // Online attempt encountered network/Firebase error -> Try seamless fallback to Room offline cache
                val localUser = userAccountDao.getUserByEmail(cleanEmail)
                if (localUser != null && SecurityUtils.verifyPassword(password, localUser.passwordHash)) {
                    val updated = localUser.copy(lastLoginTimestamp = System.currentTimeMillis())
                    userAccountDao.updateUser(updated)
                    _currentUser.value = updated
                    return@withContext AuthResult.Success(
                        user = updated,
                        isOfflineAuth = true,
                        message = "Welcome, ${updated.displayName}"
                    )
                }
                return@withContext AuthResult.Error(e.localizedMessage ?: "Sign-in failed")
            }
        } else {
            // Firebase SDK not configured, perform offline Room auth
            return@withContext performOfflineSignIn(cleanEmail, password)
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
        assignedLocation: String?,
        studentAdmissionNo: String?,
        isOnline: Boolean
    ): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val passwordHash = SecurityUtils.hashPassword(password)

        if (!isOnline) {
            // 100% Offline Sign Up: Save user directly into Room marked with isPendingCloudSync = true
            val localUid = "local_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
            val offlineAccount = UserAccount(
                uid = localUid,
                email = cleanEmail,
                passwordHash = passwordHash,
                displayName = displayName.ifBlank { cleanEmail.substringBefore("@") },
                role = role,
                assignedLocation = assignedLocation,
                studentAdmissionNo = studentAdmissionNo,
                isPendingCloudSync = true,
                lastLoginTimestamp = System.currentTimeMillis()
            )
            userAccountDao.insertUser(offlineAccount)
            _currentUser.value = offlineAccount
            return@withContext AuthResult.Success(
                user = offlineAccount,
                isOfflineAuth = true,
                message = "Offline Account Created (Pending Cloud Sync)"
            )
        }

        // Online Sign Up Flow
        val auth = firebaseAuth
        val db = firestore
        if (auth != null && db != null) {
            try {
                val authResult = auth.createUserWithEmailAndPassword(cleanEmail, password).await()
                val uid = authResult.user?.uid ?: UUID.randomUUID().toString()

                // Save to Firestore
                val userMap = hashMapOf(
                    "uid" to uid,
                    "email" to cleanEmail,
                    "displayName" to displayName,
                    "role" to role.name,
                    "assignedLocation" to (assignedLocation ?: ""),
                    "studentAdmissionNo" to (studentAdmissionNo ?: ""),
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection("users").document(uid).set(userMap).await()

                // Cache to Room
                val account = UserAccount(
                    uid = uid,
                    email = cleanEmail,
                    passwordHash = passwordHash,
                    displayName = displayName,
                    role = role,
                    assignedLocation = assignedLocation,
                    studentAdmissionNo = studentAdmissionNo,
                    isPendingCloudSync = false,
                    lastLoginTimestamp = System.currentTimeMillis()
                )
                userAccountDao.insertUser(account)
                _currentUser.value = account

                return@withContext AuthResult.Success(
                    user = account,
                    isOfflineAuth = false,
                    message = "Account Registered Online & Synced with Cloud"
                )
            } catch (e: Exception) {
                // Online signup threw error -> fallback to creating local offline account
                val localUid = "local_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
                val fallbackAccount = UserAccount(
                    uid = localUid,
                    email = cleanEmail,
                    passwordHash = passwordHash,
                    displayName = displayName,
                    role = role,
                    assignedLocation = assignedLocation,
                    studentAdmissionNo = studentAdmissionNo,
                    isPendingCloudSync = true,
                    lastLoginTimestamp = System.currentTimeMillis()
                )
                userAccountDao.insertUser(fallbackAccount)
                _currentUser.value = fallbackAccount
                return@withContext AuthResult.Success(
                    user = fallbackAccount,
                    isOfflineAuth = true,
                    message = "Account created successfully"
                )
            }
        } else {
            // Firebase not initialized, create locally
            val localUid = "local_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
            val offlineAccount = UserAccount(
                uid = localUid,
                email = cleanEmail,
                passwordHash = passwordHash,
                displayName = displayName,
                role = role,
                assignedLocation = assignedLocation,
                studentAdmissionNo = studentAdmissionNo,
                isPendingCloudSync = true,
                lastLoginTimestamp = System.currentTimeMillis()
            )
            userAccountDao.insertUser(offlineAccount)
            _currentUser.value = offlineAccount
            return@withContext AuthResult.Success(
                user = offlineAccount,
                isOfflineAuth = true,
                message = "Account created successfully"
            )
        }
    }

    private suspend fun performOfflineSignIn(email: String, password: String): AuthResult {
        val user = userAccountDao.getUserByEmail(email)
            ?: return AuthResult.Error("No account found for '$email'.")

        if (SecurityUtils.verifyPassword(password, user.passwordHash)) {
            val updated = user.copy(lastLoginTimestamp = System.currentTimeMillis())
            userAccountDao.updateUser(updated)
            _currentUser.value = updated
            return AuthResult.Success(
                user = updated,
                isOfflineAuth = true,
                message = "Welcome, ${user.displayName}"
            )
        } else {
            return AuthResult.Error("Incorrect password.")
        }
    }

    suspend fun createUserByAdmin(
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
        assignedLocation: String? = null,
        studentAdmissionNo: String? = null,
        isOnline: Boolean = true
    ): AuthResult = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        val existing = userAccountDao.getUserByEmail(cleanEmail)
        if (existing != null) {
            return@withContext AuthResult.Error("An account with email '$cleanEmail' already exists.")
        }

        val uid = "user_${System.currentTimeMillis()}_${UUID.randomUUID().toString().take(6)}"
        val newAccount = UserAccount(
            uid = uid,
            email = cleanEmail,
            passwordHash = SecurityUtils.hashPassword(password),
            displayName = displayName.ifBlank { cleanEmail.substringBefore("@") },
            role = role,
            assignedLocation = assignedLocation,
            studentAdmissionNo = studentAdmissionNo,
            isPendingCloudSync = !isOnline,
            lastLoginTimestamp = System.currentTimeMillis()
        )
        userAccountDao.insertUser(newAccount)

        if (isOnline && firestore != null) {
            try {
                val data = hashMapOf(
                    "uid" to uid,
                    "email" to cleanEmail,
                    "displayName" to newAccount.displayName,
                    "role" to role.name,
                    "assignedLocation" to (assignedLocation ?: ""),
                    "studentAdmissionNo" to (studentAdmissionNo ?: ""),
                    "createdAt" to System.currentTimeMillis()
                )
                firestore?.collection("users")?.document(uid)?.set(data)?.await()
            } catch (_: Exception) {}
        }

        return@withContext AuthResult.Success(
            user = newAccount,
            isOfflineAuth = !isOnline,
            message = "${role.name} account created for $cleanEmail"
        )
    }

    suspend fun resetUserPassword(targetUid: String, newPass: String): Boolean = withContext(Dispatchers.IO) {
        val user = userAccountDao.getUserByUid(targetUid) ?: return@withContext false
        val updated = user.copy(
            passwordHash = SecurityUtils.hashPassword(newPass),
            isPendingCloudSync = true
        )
        userAccountDao.updateUser(updated)
        true
    }

    suspend fun deleteUserAccount(targetUid: String): Boolean = withContext(Dispatchers.IO) {
        userAccountDao.deleteUserByUid(targetUid)
        try {
            firestore?.collection("users")?.document(targetUid)?.delete()
        } catch (_: Exception) {}
        true
    }

    suspend fun syncPendingAccounts(): Int = withContext(Dispatchers.IO) {
        val pending = userAccountDao.getPendingSyncUsers()
        if (pending.isEmpty()) return@withContext 0

        val db = firestore ?: return@withContext 0
        var syncedCount = 0

        for (user in pending) {
            try {
                val data = hashMapOf(
                    "uid" to user.uid,
                    "email" to user.email,
                    "displayName" to user.displayName,
                    "role" to user.role.name,
                    "assignedLocation" to (user.assignedLocation ?: ""),
                    "studentAdmissionNo" to (user.studentAdmissionNo ?: ""),
                    "syncedAt" to System.currentTimeMillis()
                )
                db.collection("users").document(user.uid).set(data).await()
                userAccountDao.updateUser(user.copy(isPendingCloudSync = false))
                syncedCount++
            } catch (_: Exception) {
                // Ignore sync individual failures
            }
        }
        syncedCount
    }

    suspend fun updateRoleByAdmin(targetUid: String, newRole: UserRole) = withContext(Dispatchers.IO) {
        val target = userAccountDao.getUserByUid(targetUid) ?: return@withContext
        val updated = target.copy(role = newRole, isPendingCloudSync = true)
        userAccountDao.updateUser(updated)

        // Try pushing to Firestore if available
        try {
            firestore?.collection("users")?.document(targetUid)?.update("role", newRole.name)
        } catch (_: Exception) {}
    }

    fun switchUserQuick(account: UserAccount) {
        _currentUser.value = account
    }

    fun signOut() {
        try {
            firebaseAuth?.signOut()
        } catch (_: Exception) {}
        _currentUser.value = null
    }
}
