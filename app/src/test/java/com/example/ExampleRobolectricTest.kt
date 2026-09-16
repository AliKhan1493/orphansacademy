package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.UserRole
import com.example.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Orphan's Academy", appName)
  }

  @Test
  fun `verify password hashing and verification`() {
    val password = "Pakistan@14931493"
    val hash = SecurityUtils.hashPassword(password)
    assertTrue(SecurityUtils.verifyPassword(password, hash))
  }

  @Test
  fun `verify default admin credentials`() {
    val adminEmail = "admin@orphan.com.pk"
    val adminPassword = "Pakistan@14931493"
    val hash = SecurityUtils.hashPassword(adminPassword)
    assertEquals("admin@orphan.com.pk", adminEmail)
    assertTrue(SecurityUtils.verifyPassword("Pakistan@14931493", hash))
  }

  @Test
  fun `verify RBAC permissions structure`() {
    val admin = UserRole.ADMIN
    val manager = UserRole.MANAGER
    val teacher = UserRole.TEACHER
    val student = UserRole.STUDENT

    assertTrue(admin.canManageTeachers())
    assertTrue(admin.canAccessAdminSettings())
    assertTrue(manager.canManageStudents())
    assertTrue(!manager.canManageTeachers())
    assertTrue(teacher.canGenerateA4Pdf())
    assertTrue(!student.canGenerateA4Pdf())
  }
}
