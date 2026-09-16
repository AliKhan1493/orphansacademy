package com.example.data

import com.example.model.CustomFeature
import com.example.model.ExamTest
import com.example.model.Student
import com.example.model.SyllabusTopic
import com.example.model.Teacher
import com.example.model.TestRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.util.UUID

class AcademyRepository(
    private val studentDao: StudentDao,
    private val teacherDao: TeacherDao,
    private val syllabusDao: SyllabusDao,
    private val testDao: TestDao,
    private val testRecordDao: TestRecordDao,
    private val featureDao: FeatureDao
) {
    val allStudents: Flow<List<Student>> = studentDao.getAllStudents()
    val allTeachers: Flow<List<Teacher>> = teacherDao.getAllTeachers()
    val allSyllabusTopics: Flow<List<SyllabusTopic>> = syllabusDao.getAllTopics()
    val allTests: Flow<List<ExamTest>> = testDao.getAllTests()
    val allTestRecords: Flow<List<TestRecord>> = testRecordDao.getAllRecords()
    val allFeatures: Flow<List<CustomFeature>> = featureDao.getAllFeatures()

    fun getRecordsForStudent(admissionNo: String): Flow<List<TestRecord>> =
        testRecordDao.getRecordsByStudent(admissionNo)

    fun getStudentsForLocation(location: String): Flow<List<Student>> =
        studentDao.getStudentsByLocation(location)

    suspend fun getStudentByAdmissionNo(admissionNo: String): Student? =
        studentDao.getStudentByAdmissionNo(admissionNo)

    suspend fun insertStudent(student: Student) = withContext(Dispatchers.IO) {
        studentDao.insertStudent(student)
    }

    suspend fun deleteStudent(student: Student) = withContext(Dispatchers.IO) {
        studentDao.deleteStudent(student)
    }

    suspend fun insertTeacher(teacher: Teacher) = withContext(Dispatchers.IO) {
        teacherDao.insertTeacher(teacher)
    }

    suspend fun deleteTeacher(teacher: Teacher) = withContext(Dispatchers.IO) {
        teacherDao.deleteTeacher(teacher)
    }

    suspend fun insertTopic(topic: SyllabusTopic) = withContext(Dispatchers.IO) {
        syllabusDao.insertTopic(topic)
    }

    suspend fun updateTopic(topic: SyllabusTopic) = withContext(Dispatchers.IO) {
        syllabusDao.updateTopic(topic)
    }

    suspend fun deleteTopic(topic: SyllabusTopic) = withContext(Dispatchers.IO) {
        syllabusDao.deleteTopic(topic)
    }

    suspend fun insertTest(test: ExamTest) = withContext(Dispatchers.IO) {
        testDao.insertTest(test)
    }

    suspend fun deleteTest(test: ExamTest) = withContext(Dispatchers.IO) {
        testDao.deleteTest(test)
    }

    suspend fun insertTestRecord(record: TestRecord) = withContext(Dispatchers.IO) {
        testRecordDao.insertRecord(record)
    }

    suspend fun insertFeature(feature: CustomFeature) = withContext(Dispatchers.IO) {
        featureDao.insertFeature(feature)
    }

    suspend fun deleteFeature(feature: CustomFeature) = withContext(Dispatchers.IO) {
        featureDao.deleteFeature(feature)
    }

    suspend fun seedInitialAcademyDataIfEmpty() = withContext(Dispatchers.IO) {
        if (studentDao.countStudents() == 0) {
            val initialStudents = listOf(
                Student(
                    id = "std_001",
                    admissionNo = "OA-2026-042",
                    fullName = "Amina Tariq",
                    gradeLevel = "Grade 10 - STEM",
                    assignedLocation = "Campus North Wing - Room 101",
                    guardianName = "Mrs. Fatima Tariq",
                    guardianContact = "+1 (555) 234-5678",
                    bloodGroup = "O+",
                    dob = "2009-08-12",
                    emergencyContact = "+1 (555) 901-2345"
                ),
                Student(
                    id = "std_002",
                    admissionNo = "OA-2026-043",
                    fullName = "Bilal Farooq",
                    gradeLevel = "Grade 9 - Humanities",
                    assignedLocation = "Campus Science Hall 204",
                    guardianName = "Mr. Zahid Farooq",
                    guardianContact = "+1 (555) 345-6789",
                    bloodGroup = "A+",
                    dob = "2010-03-22",
                    emergencyContact = "+1 (555) 901-2346"
                ),
                Student(
                    id = "std_003",
                    admissionNo = "OA-2026-044",
                    fullName = "Clara Mensah",
                    gradeLevel = "Grade 10 - STEM",
                    assignedLocation = "Campus Science Hall 204",
                    guardianName = "Dr. Samuel Mensah",
                    guardianContact = "+1 (555) 456-7890",
                    bloodGroup = "B+",
                    dob = "2009-11-05",
                    emergencyContact = "+1 (555) 901-2347"
                ),
                Student(
                    id = "std_004",
                    admissionNo = "OA-2026-045",
                    fullName = "Danyal Hassan",
                    gradeLevel = "Grade 8 - General",
                    assignedLocation = "East Wing - Lab 3",
                    guardianName = "Sister Mary (Academy Shelter)",
                    guardianContact = "+1 (555) 567-8901",
                    bloodGroup = "AB+",
                    dob = "2011-06-19",
                    emergencyContact = "+1 (555) 901-2348"
                )
            )
            studentDao.insertStudents(initialStudents)
        }

        // Ensure legacy demo teachers are removed; teachers and managers are managed and created by admin
        teacherDao.removeOldDemoTeachers()

        // Seed initial syllabus
        val initialSyllabus = listOf(
            SyllabusTopic(
                id = "syl_001",
                subject = "Physics",
                title = "Newtonian Mechanics & Force Equilibrium",
                gradeLevel = "Grade 10 - STEM",
                term = "Term 1 (Midterm)",
                learningObjectives = "Analyze 3D vector forces, apply Newton's second law in non-inertial frames, and resolve friction coefficients.",
                assignedTeacherId = "tch_001",
                isCompleted = true
            ),
            SyllabusTopic(
                id = "syl_002",
                subject = "Mathematics",
                title = "Calculus I: Differential Equations & Rates",
                gradeLevel = "Grade 10 - STEM",
                term = "Term 1 (Midterm)",
                learningObjectives = "Derive rate of change for geometric models; solve first-order separable differential equations.",
                assignedTeacherId = "tch_001",
                isCompleted = false
            ),
            SyllabusTopic(
                id = "syl_003",
                subject = "Biology",
                title = "Cellular Respiration & Krebs Cycle",
                gradeLevel = "Grade 10 - STEM",
                term = "Term 2",
                learningObjectives = "Diagram ATP synthase mechanisms, electron transport chains, and chemiosmotic gradients.",
                assignedTeacherId = "tch_002",
                isCompleted = false
            )
        )
        syllabusDao.insertTopics(initialSyllabus)

        // Seed initial tests
        val initialTests = listOf(
            ExamTest(
                id = "test_001",
                title = "Physics Mid-Term Examination 2026",
                subject = "Physics",
                gradeLevel = "Grade 10 - STEM",
                totalMarks = 50,
                durationMinutes = 90,
                examDate = "2026-10-15",
                instructions = "Attempt all 4 sections. Show all free-body diagrams clearly. Non-programmable calculators allowed.",
                questionsJson = "State and prove Newton's Third Law of Motion with an Atwood machine diagram.\nA 5.0 kg crate is pulled across an inclined plane at 30 degrees with friction coefficient 0.25. Calculate acceleration.\nExplain the difference between conservative and non-conservative forces with mathematical proof.\nDerive the formula for relativistic kinetic energy at speeds approaching 0.8c.",
                createdByTeacherId = "tch_001"
            )
        )
        for (test in initialTests) {
            testDao.insertTest(test)
        }

        // Seed test records
        val initialRecords = listOf(
            TestRecord(
                id = "rec_001",
                testId = "test_001",
                testTitle = "Physics Mid-Term Examination 2026",
                studentAdmissionNo = "OA-2026-042",
                studentName = "Amina Tariq",
                marksObtained = 47,
                totalMarks = 50,
                remarks = "Outstanding mathematical derivations. Excellent free-body analysis."
            ),
            TestRecord(
                id = "rec_002",
                testId = "test_001",
                testTitle = "Physics Mid-Term Examination 2026",
                studentAdmissionNo = "OA-2026-044",
                studentName = "Clara Mensah",
                marksObtained = 44,
                totalMarks = 50,
                remarks = "Strong theoretical comprehension. Minor arithmetic check needed in Atwood diagram."
            )
        )
        testRecordDao.insertRecords(initialRecords)

        // Seed features registry
        val initialFeatures = listOf(
            CustomFeature(
                id = "feat_001",
                name = "Monthly Food Rations & Nutrition Ledger",
                category = "Rations",
                description = "Daily calorie intake monitoring, bulk grain logistics, and dietary health flags for all residential orphans.",
                status = "Active Supply",
                updatedBy = "manager@academy.org"
            ),
            CustomFeature(
                id = "feat_002",
                name = "Uniform & Winter Apparel Inventory",
                category = "Inventory",
                description = "Automated stock tracking for school blazers, footwear, and cold-weather thermal sets per admission ID.",
                status = "Fully Stocked",
                updatedBy = "admin@academy.org"
            ),
            CustomFeature(
                id = "feat_003",
                name = "Emergency Medical & Vaccination Clinic",
                category = "Health",
                description = "Annual pediatric checkups, immunization certificates, and allergy warnings for all campus houses.",
                status = "Inspection Passed",
                updatedBy = "manager@academy.org"
            )
        )
        featureDao.insertFeatures(initialFeatures)
    }
}
