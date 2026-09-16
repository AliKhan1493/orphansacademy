package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.model.CustomFeature
import com.example.model.ExamTest
import com.example.model.Student
import com.example.model.SyllabusTopic
import com.example.model.Teacher
import com.example.model.TestRecord
import com.example.model.UserAccount

@Database(
    entities = [
        UserAccount::class,
        Student::class,
        Teacher::class,
        SyllabusTopic::class,
        ExamTest::class,
        TestRecord::class,
        CustomFeature::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userAccountDao(): UserAccountDao
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun syllabusDao(): SyllabusDao
    abstract fun testDao(): TestDao
    abstract fun testRecordDao(): TestRecordDao
    abstract fun featureDao(): FeatureDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "orphans_academy_db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }

        fun getInstance(context: Context): AppDatabase = getDatabase(context)
    }
}
