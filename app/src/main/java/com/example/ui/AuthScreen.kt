package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UserRole
import com.example.ui.theme.DarkMossGray
import com.example.ui.theme.DarkMossGrayMuted
import com.example.ui.theme.DarkMossGraySubtle
import com.example.ui.theme.DeepForestTeal
import com.example.ui.theme.DeepForestTealDark
import com.example.ui.theme.PaleSageOffWhite
import com.example.ui.theme.PastelSeafoam
import com.example.ui.theme.PastelSeafoamDark
import com.example.ui.theme.PastelSeafoamLight
import com.example.ui.theme.SoftAmberPeach
import com.example.ui.theme.SoftAmberPeachDark
import com.example.ui.theme.SoftAmberPeachLight

@Composable
fun AuthScreen(
    isOnline: Boolean,
    isSimulatedOffline: Boolean,
    onToggleAirplaneSimulation: () -> Unit,
    onSignIn: (email: String, password: String) -> Unit,
    onSignUp: (
        email: String,
        password: String,
        displayName: String,
        role: UserRole,
        assignedLocation: String?,
        studentAdmissionNo: String?
    ) -> Unit,
    isLoading: Boolean = false,
    errorMessage: String? = null,
    infoMessage: String? = null
) {
    var isSignUpMode by remember { mutableStateOf(false) }

    var email by remember { mutableStateOf("admin@orphan.com.pk") }
    var password by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var studentAdmissionNo by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }

    val submitAction = {
        if (isSignUpMode) {
            onSignUp(
                email.trim(),
                password,
                displayName.ifBlank { email.substringBefore("@") },
                UserRole.STUDENT,
                "Main Campus",
                studentAdmissionNo.ifBlank { null }
            )
        } else {
            onSignIn(email.trim(), password)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PaleSageOffWhite),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Academy Crest (Deep Forest Teal & Pastel Seafoam)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(DeepForestTeal),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = "Academy Crest",
                    tint = PastelSeafoam,
                    modifier = Modifier.size(38.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Orphan's Academy",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = DeepForestTeal,
                letterSpacing = (-0.5).sp
            )

            Text(
                text = if (isSignUpMode) "Create your student study portal account" else "Study materials, reading & homework portal",
                fontSize = 13.sp,
                color = DarkMossGrayMuted,
                modifier = Modifier.padding(top = 4.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Study-Themed Auth Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .widthIn(max = 440.dp)
                    .border(1.dp, PastelSeafoamDark, RoundedCornerShape(18.dp)),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Segmented Mode Switcher (Pale Sage / Pastel Seafoam Active)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(PaleSageOffWhite)
                            .padding(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isSignUpMode = false }
                                .testTag("auth_mode_signin"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (!isSignUpMode) PastelSeafoam else Color.Transparent,
                            shadowElevation = if (!isSignUpMode) 1.dp else 0.dp
                        ) {
                            Text(
                                text = "Sign In",
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = if (!isSignUpMode) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (!isSignUpMode) DeepForestTeal else DarkMossGrayMuted
                            )
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isSignUpMode = true }
                                .testTag("auth_mode_signup"),
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSignUpMode) PastelSeafoam else Color.Transparent,
                            shadowElevation = if (isSignUpMode) 1.dp else 0.dp
                        ) {
                            Text(
                                text = "Sign Up",
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = TextAlign.Center,
                                fontWeight = if (isSignUpMode) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (isSignUpMode) DeepForestTeal else DarkMossGrayMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Sign Up Specific Inputs
                    if (isSignUpMode) {
                        OutlinedTextField(
                            value = displayName,
                            onValueChange = { displayName = it },
                            label = { Text("Full Name", color = DarkMossGrayMuted) },
                            placeholder = { Text("Enter your full name") },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = DeepForestTeal)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_name_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepForestTeal,
                                unfocusedBorderColor = PastelSeafoamDark,
                                focusedTextColor = DarkMossGray,
                                unfocusedTextColor = DarkMossGray
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = studentAdmissionNo,
                            onValueChange = { studentAdmissionNo = it },
                            label = { Text("Student Admission ID", color = DarkMossGrayMuted) },
                            placeholder = { Text("e.g. OA-2026-042") },
                            leadingIcon = {
                                Icon(Icons.Default.School, contentDescription = null, tint = DeepForestTeal)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("auth_admission_input"),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = DeepForestTeal,
                                unfocusedBorderColor = PastelSeafoamDark,
                                focusedTextColor = DarkMossGray,
                                unfocusedTextColor = DarkMossGray
                            ),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                        )

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address", color = DarkMossGrayMuted) },
                        placeholder = { Text("admin@orphan.com.pk") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = DeepForestTeal)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_email_input"),
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Email,
                            imeAction = ImeAction.Next
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepForestTeal,
                            unfocusedBorderColor = PastelSeafoamDark,
                            focusedTextColor = DarkMossGray,
                            unfocusedTextColor = DarkMossGray
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Password Field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = DarkMossGrayMuted) },
                        placeholder = { Text("Enter password") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = DeepForestTeal)
                        },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (showPassword) "Hide password" else "Show password",
                                    tint = DarkMossGraySubtle
                                )
                            }
                        },
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("auth_password_input"),
                        shape = RoundedCornerShape(10.dp),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { if (email.isNotBlank() && password.isNotBlank()) submitAction() }
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = DeepForestTeal,
                            unfocusedBorderColor = PastelSeafoamDark,
                            focusedTextColor = DarkMossGray,
                            unfocusedTextColor = DarkMossGray
                        ),
                        singleLine = true
                    )

                    // Error Message Callout (Soft Amber / Peach Tint)
                    AnimatedVisibility(
                        visible = errorMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            color = SoftAmberPeachLight,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                        ) {
                            Text(
                                text = errorMessage ?: "",
                                color = SoftAmberPeachDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Success Message Callout (Pastel Seafoam Tint)
                    AnimatedVisibility(
                        visible = infoMessage != null,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Surface(
                            color = PastelSeafoamLight,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 14.dp)
                        ) {
                            Text(
                                text = infoMessage ?: "",
                                color = DeepForestTealDark,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(10.dp),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Primary Submit Button: Soft Amber / Peach (#FB923C) for High-Motivation Call To Action
                    Button(
                        onClick = submitAction,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("auth_submit_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SoftAmberPeach,
                            contentColor = Color.White
                        ),
                        enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (isSignUpMode) "Create Student Account" else "Sign In to Academy",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                letterSpacing = 0.2.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
