package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SocietyDao {
    @Query("SELECT * FROM societies WHERE isActive = 1 ORDER BY name ASC")
    fun getAllSocieties(): Flow<List<SocietyEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSocieties(societies: List<SocietyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSociety(society: SocietyEntity)
}

@Dao
interface StudentDao {
    @Query("SELECT * FROM students WHERE societyId = :societyId ORDER BY fullName ASC")
    fun getStudentsBySociety(societyId: String): Flow<List<StudentEntity>>

    @Query("SELECT * FROM students")
    fun getAllStudents(): Flow<List<StudentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudents(students: List<StudentEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: StudentEntity)
}

@Dao
interface AcademicRecordDao {
    @Query("SELECT * FROM academic_records WHERE societyId = :societyId ORDER BY percentage DESC")
    fun getRecordsBySociety(societyId: String): Flow<List<AcademicRecordEntity>>

    @Query("SELECT * FROM academic_records")
    fun getAllRecords(): Flow<List<AcademicRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: AcademicRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(records: List<AcademicRecordEntity>)

    @Query("UPDATE academic_records SET verificationStatus = :status, verificationRemarks = :remarks WHERE id = :id")
    suspend fun updateVerification(id: String, status: String, remarks: String)
}

@Dao
interface AwardDao {
    @Query("SELECT * FROM awards WHERE societyId = :societyId ORDER BY amount DESC")
    fun getAwardsBySociety(societyId: String): Flow<List<AwardEntity>>

    @Query("SELECT * FROM awards")
    fun getAllAwards(): Flow<List<AwardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAwards(awards: List<AwardEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAward(award: AwardEntity)

    @Query("UPDATE awards SET approvalStatus = :status, committeeNotes = :notes WHERE id = :id")
    suspend fun updateApproval(id: String, status: String, notes: String)

    @Query("UPDATE awards SET paymentStatus = :status, paymentMode = :mode, transactionRef = :ref, receiptNumber = :receipt WHERE id = :id")
    suspend fun updatePayment(id: String, status: String, mode: String, ref: String, receipt: String)
}

@Dao
interface MemorialFundDao {
    @Query("SELECT * FROM memorial_funds WHERE societyId = :societyId ORDER BY fundName ASC")
    fun getFundsBySociety(societyId: String): Flow<List<MemorialFundEntity>>

    @Query("SELECT * FROM memorial_funds")
    fun getAllFunds(): Flow<List<MemorialFundEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFunds(funds: List<MemorialFundEntity>)

    @Query("UPDATE memorial_funds SET actualInterestReceivedTotal = actualInterestReceivedTotal + :interestAmount, availableBalance = availableBalance + :interestAmount WHERE id = :id")
    suspend fun creditActualInterest(id: String, interestAmount: Long)
}

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE societyId = :societyId ORDER BY id DESC")
    fun getTransactionsBySociety(societyId: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: TransactionEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(txList: List<TransactionEntity>)

    @Query("UPDATE transactions SET isReversed = 1 WHERE id = :id")
    suspend fun markReversed(id: String)
}

@Dao
interface PaymentDao {
    @Query("SELECT * FROM payments WHERE societyId = :societyId ORDER BY date DESC")
    fun getPaymentsBySociety(societyId: String): Flow<List<PaymentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayment(payment: PaymentEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPayments(payments: List<PaymentEntity>)
}

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs WHERE societyId = :societyId ORDER BY id DESC")
    fun getLogsBySociety(societyId: String): Flow<List<AuditLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: AuditLogEntity)
}

@Dao
interface SheetSyncDao {
    @Query("SELECT * FROM sheet_sync_config WHERE id = 'default_sheet_sync'")
    fun getSyncConfig(): Flow<SheetSyncConfigEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSyncConfig(config: SheetSyncConfigEntity)
}
