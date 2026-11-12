package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.*

@Entity(tableName = "societies")
data class SocietyEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nameGujarati: String,
    val code: String,
    val registrationNumber: String,
    val contactEmail: String,
    val contactPhone: String,
    val address: String,
    val isActive: Boolean = true,
    val spreadsheetId: String = ""
)

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey val id: String,
    val societyId: String,
    val fullName: String,
    val fullNameGujarati: String,
    val guardianName: String,
    val guardianMobile: String,
    val guardianConsent: Boolean = true,
    val schoolName: String,
    val standard: String,
    val registrationCode: String
)

@Entity(tableName = "academic_records")
data class AcademicRecordEntity(
    @PrimaryKey val id: String,
    val societyId: String,
    val studentId: String,
    val studentName: String,
    val academicYear: String,
    val examinationCategory: String, // ExamStream name
    val board: String = "GSEB",
    val school: String,
    val stream: String = "General",
    val subjectsJson: String, // serialized subjects
    val totalMaximumMarks: Int,
    val totalObtainedMarks: Int,
    val percentage: Double,
    val result: String,
    val verificationStatus: String = "PENDING",
    val verificationRemarks: String = "",
    val marksheetDocumentUri: String? = null
)

@Entity(tableName = "award_categories")
data class AwardCategoryEntity(
    @PrimaryKey val id: String,
    val societyId: String,
    val code: String,
    val name: String,
    val nameGujarati: String,
    val description: String = ""
)

@Entity(tableName = "award_rules")
data class AwardRuleEntity(
    @PrimaryKey val id: String,
    val societyId: String,
    val categoryId: String,
    val academicYear: String,
    val examinationCategory: String,
    val title: String,
    val titleGujarati: String,
    val rankTarget: Int? = null,
    val subjectTarget: String? = null,
    val minimumPercentageThreshold: Double? = null,
    val cashAmount: Int,
    val tiePolicy: String = "DUPLICATE_PRIZE",
    val requiresCommitteeApproval: Boolean = true,
    val dedicatedMemorialFundId: String? = null
)

@Entity(tableName = "awards")
data class AwardEntity(
    @PrimaryKey val id: String,
    val societyId: String,
    val studentId: String,
    val studentName: String,
    val academicYear: String,
    val examinationCategory: String,
    val categoryId: String,
    val ruleId: String,
    val awardTitle: String,
    val awardTitleGujarati: String,
    val eligibilityEvidence: String,
    val amount: Int,
    val approvalStatus: String = "PENDING_COMMITTEE",
    val paymentStatus: String = "PENDING",
    val paymentMode: String? = null,
    val transactionRef: String? = null,
    val receiptNumber: String? = null,
    val committeeNotes: String? = null,
    val donorName: String? = null,
    val memorialFundName: String? = null
)

@Entity(tableName = "donors")
data class DonorEntity(
    @PrimaryKey val id: String,
    val societyId: String,
    val name: String,
    val nameGujarati: String,
    val contactNumber: String,
    val email: String,
    val totalContributed: Long = 0L
)

@Entity(tableName = "memorial_funds")
data class MemorialFundEntity(
    @PrimaryKey val id: String,
    val societyId: String,
    val donorId: String,
    val donorName: String,
    val fundName: String,
    val fundNameGujarati: String,
    val inMemoryOf: String,
    val fdCertificateNumber: String,
    val bankName: String,
    val fdPrincipalAmount: Long, // Strictly separated from interest
    val fdInterestRatePercent: Double = 7.25,
    val actualInterestReceivedTotal: Long, // Only bank-supported actual interest
    val allocatedAmount: Long = 0L,
    val availableBalance: Long,
    val purposeRestriction: String = "Merit Awards for SSC & HSC Students"
)

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey val id: String,
    val societyId: String,
    val fundType: String,
    val transactionType: String,
    val amount: Long,
    val referenceNumber: String,
    val narrative: String,
    val isReversed: Boolean = false,
    val date: String
)

@Entity(tableName = "payments")
data class PaymentEntity(
    @PrimaryKey val id: String,
    val societyId: String,
    val awardId: String,
    val studentName: String,
    val awardTitle: String,
    val amount: Int,
    val paymentMode: String,
    val status: String,
    val receiptNumber: String,
    val bankRef: String,
    val date: String
)

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey val id: String,
    val societyId: String,
    val action: String,
    val performedBy: String,
    val details: String,
    val timestamp: String
)

@Entity(tableName = "sheet_sync_config")
data class SheetSyncConfigEntity(
    @PrimaryKey val id: String = "default_sheet_sync",
    val spreadsheetId: String,
    val sheetName: String = "Saraswati_Samman_Database",
    val lastSyncTimestamp: String = "Never",
    val lastSyncStatus: String = "Ready",
    val totalRecordsSynced: Int = 0
)
