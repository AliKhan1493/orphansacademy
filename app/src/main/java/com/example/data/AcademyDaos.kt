package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CustomFeature
import com.example.model.ExamTest
import com.example.model.Student
import com.example.model.SyllabusTopic
import com.example.model.Teacher
import com.example.model.TestRecord
import com.example.model.UserAccount
import com.example.model.UserRole
import kotlinx.coroutines.flow.Flow

@Dao
interface UserAccountDao {
    @Query("SELECT * FROM user_accounts WHERE LOWER(email) = LOWER(:email) LIMIT 1")
    suspend fun getUserByEmail(email: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE uid = :uid LIMIT 1")
    suspend fun getUserByUid(uid: String): UserAccount?

    @Query("SELECT * FROM user_accounts ORDER BY lastLoginTimestamp DESC")
    fun getAllUsers(): Flow<List<UserAccount>>

    @Query("SELECT * FROM user_accounts WHERE role = :role")
    fun getUsersByRole(role: UserRole): Flow<List<UserAccount>>

    @Query("SELECT * FROM user_accounts WHERE isPendingCloudSync = 1")
    suspend fun getPendingSyncUsers(): List<UserAccount>

    @Query("SELECT COUNT(*) FROM user_accounts WHERE isPendingCloudSync = 1")
    fun getPendingSyncCount(): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserAccount>)

    @Update
    suspend fun updateUser(user: UserAccount)

    @Delete
    suspend fun deleteUser(user: UserAccount)

    @Query("DELETE FROM user_accounts WHERE uid = :uid")
    suspend fun deleteUserByUid(uid: String)

    @Query("DELETE FROM user_accounts WHERE LOWER(email) IN ('admin@academy.org', 'manager@academy.org', 'teacher@academy.org', 'student@academy.org')")
    suspend fun removeOldDemoAccounts()

    @Query("SELECT COUNT(*) FROM user_accounts")
    suspend fun countUsers(): Int
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students ORDER BY fullName ASC")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE assignedLocation = :location ORDER BY fullName ASC")
    fun getStudentsByLocation(location: String): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE admissionNo = :admissionNo LIMIT 1")
    suspend fun getStudentByAdmissionNo(admissionNo: String): Student?

    @Query("SELECT * FROM students WHERE admissionNo = :admissionNo LIMIT 1")
    fun observeStudentByAdmissionNo(admissionNo: String): Flow<Student?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<Student>)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT COUNT(*) FROM students")
    suspend fun countStudents(): Int
}

@Dao
interface TeacherDao {
    @Query("SELECT * FROM teachers ORDER BY fullName ASC")
    fun getAllTeachers(): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE id = :id LIMIT 1")
    suspend fun getTeacherById(id: String): Teacher?

    @Query("SELECT * FROM teachers WHERE email = :email LIMIT 1")
    suspend fun getTeacherByEmail(email: String): Teacher?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: Teacher)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeachers(teachers: List<Teacher>)

    @Delete
    suspend fun deleteTeacher(teacher: Teacher)

    @Query("DELETE FROM teachers WHERE LOWER(email) IN ('teacher@academy.org', 'elena.r@academy.org', 'tariq.m@academy.org')")
    suspend fun removeOldDemoTeachers()

    @Query("SELECT COUNT(*) FROM teachers")
    suspend fun countTeachers(): Int
}

@Dao
interface SyllabusDao {
    @Query("SELECT * FROM syllabus_topics ORDER BY subject, gradeLevel ASC")
    fun getAllTopics(): Flow<List<SyllabusTopic>>

    @Query("SELECT * FROM syllabus_topics WHERE assignedTeacherId = :teacherId")
    fun getTopicsByTeacher(teacherId: String): Flow<List<SyllabusTopic>>

    @Query("SELECT * FROM syllabus_topics WHERE gradeLevel = :gradeLevel")
    fun getTopicsByGrade(gradeLevel: String): Flow<List<SyllabusTopic>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: SyllabusTopic)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<SyllabusTopic>)

    @Update
    suspend fun updateTopic(topic: SyllabusTopic)

    @Delete
    suspend fun deleteTopic(topic: SyllabusTopic)
}

@Dao
interface TestDao {
    @Query("SELECT * FROM exam_tests ORDER BY examDate DESC")
    fun getAllTests(): Flow<List<ExamTest>>

    @Query("SELECT * FROM exam_tests WHERE createdByTeacherId = :teacherId")
    fun getTestsByTeacher(teacherId: String): Flow<List<ExamTest>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: ExamTest)

    @Delete
    suspend fun deleteTest(test: ExamTest)
}

@Dao
interface TestRecordDao {
    @Query("SELECT * FROM test_records ORDER BY gradedAt DESC")
    fun getAllRecords(): Flow<List<TestRecord>>

    @Query("SELECT * FROM test_records WHERE studentAdmissionNo = :admissionNo ORDER BY gradedAt DESC")
    fun getRecordsByStudent(admissionNo: String): Flow<List<TestRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: TestRecord)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<TestRecord>)

    @Update
    suspend fun updateRecord(record: TestRecord)

    @Delete
    suspend fun deleteRecord(record: TestRecord)
}

@Dao
interface FeatureDao {
    @Query("SELECT * FROM custom_features ORDER BY lastUpdated DESC")
    fun getAllFeatures(): Flow<List<CustomFeature>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeature(feature: CustomFeature)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeatures(features: List<CustomFeature>)

    @Delete
    suspend fun deleteFeature(feature: CustomFeature)
}
