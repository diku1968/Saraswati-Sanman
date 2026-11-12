package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        SocietyEntity::class,
        StudentEntity::class,
        AcademicRecordEntity::class,
        AwardCategoryEntity::class,
        AwardRuleEntity::class,
        AwardEntity::class,
        DonorEntity::class,
        MemorialFundEntity::class,
        TransactionEntity::class,
        PaymentEntity::class,
        AuditLogEntity::class,
        SheetSyncConfigEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun societyDao(): SocietyDao
    abstract fun studentDao(): StudentDao
    abstract fun academicRecordDao(): AcademicRecordDao
    abstract fun awardDao(): AwardDao
    abstract fun memorialFundDao(): MemorialFundDao
    abstract fun transactionDao(): TransactionDao
    abstract fun paymentDao(): PaymentDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun sheetSyncDao(): SheetSyncDao

    companion object {
        /**
         * SQLite Database Name
         * You can easily change this file name in VS Code / Android Studio to your preferred database name.
         * Default: "saraswati_samman.db"
         */
        const val DATABASE_NAME = "saraswati_samman.db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                ).fallbackToDestructiveMigration(true)
                 .build()
                 .also { INSTANCE = it }
            }
        }
    }
}
