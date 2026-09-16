package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.Student
import com.example.ui.StudentIdCardView
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun student_id_card_screenshot() {
    val sampleStudent = Student(
      id = "std_test_01",
      admissionNo = "OA-2026-042",
      fullName = "Amina Tariq",
      gradeLevel = "Grade 10 - STEM",
      assignedLocation = "Campus North Wing - Room 101",
      guardianName = "Mrs. Fatima Tariq",
      guardianContact = "+1 555-234-5678",
      bloodGroup = "O+",
      dob = "2009-08-12",
      emergencyContact = "+1 555-901-2345"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        StudentIdCardView(student = sampleStudent)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/student_id_card.png")
  }
}
