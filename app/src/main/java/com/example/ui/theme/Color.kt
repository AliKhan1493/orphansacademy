package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// Nordic & Study Palette (User-Requested Specifications):
// 1. Pale Sage / Off-White: #F6F9F6 (Calm, low eye-strain surface)
// 2. Deep Forest Teal: #0F5156 (Primary brand color, headers, stability)
// 3. Soft Amber / Peach: #FB923C (Call-to-action buttons, motivational highlights)
// 4. Pastel Seafoam: #A7F3D0 (Card backgrounds, active selection states)
// 5. Dark Moss Gray: #1F2E2E (Body typography, legible dark text)
// =========================================================================

val PaleSageOffWhite = Color(0xFFF6F9F6)     // Surface background
val DeepForestTeal = Color(0xFF0F5156)       // Primary brand & headers
val SoftAmberPeach = Color(0xFFFB923C)       // CTA buttons & highlights
val PastelSeafoam = Color(0xFFA7F3D0)        // Cards & active selection
val DarkMossGray = Color(0xFF1F2E2E)         // Body typography & text

// Tonal and semantic variants for smooth UI transitions
val DeepForestTealDark = Color(0xFF0A3A3D)
val DeepForestTealLight = Color(0xFF1E6C72)
val SoftAmberPeachDark = Color(0xFFEA580C)
val SoftAmberPeachLight = Color(0xFFFED7AA)
val PastelSeafoamLight = Color(0xFFD1FAE5)
val PastelSeafoamDark = Color(0xFF6EE7B7)
val DarkMossGrayMuted = Color(0xFF4A5E5E)
val DarkMossGraySubtle = Color(0xFF6B8080)
val PaleSageSurfaceVariant = Color(0xFFE8EFE8)

// Backward-compatible semantic bindings mapped to new palette
val CharcoalAsh = DarkMossGray
val CharcoalAshLight = DarkMossGrayMuted
val CharcoalAshMuted = DarkMossGraySubtle
val DustyRose = SoftAmberPeach
val DustyRoseDark = SoftAmberPeachDark
val DustyRoseLight = SoftAmberPeachLight
val SoftMint = PastelSeafoam
val SoftMintLight = PastelSeafoamLight
val SoftMintDark = PastelSeafoamDark
val PowderSky = DeepForestTeal
val PowderSkyDark = DeepForestTealDark
val PowderSkyLight = DeepForestTealLight
val FrostedPearl = PaleSageOffWhite
val FrostedPearlLight = PaleSageOffWhite

val AcademyBlueDark = DeepForestTealDark
val AcademyBlue = DeepForestTeal
val AcademyBlueLight = PastelSeafoam
val AcademyGold = SoftAmberPeach
val AcademyGoldLight = SoftAmberPeachLight

val Slate900 = DarkMossGray
val Slate800 = DarkMossGrayMuted
val Slate700 = DarkMossGraySubtle
val Slate100 = PaleSageOffWhite
val Slate50 = PaleSageOffWhite

// Role Badges aligned with study palette
val RoleAdminColor = SoftAmberPeachDark
val RoleManagerColor = DeepForestTeal
val RoleTeacherColor = Color(0xFF059669)
val RoleStudentColor = DeepForestTealLight
