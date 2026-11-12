package com.example.domain

import java.util.UUID

enum class Role(val displayName: String) {
    STUDENT("Student"),
    SOCIETY_ADMIN("Society Admin"),
    COMMITTEE_MEMBER("Committee Member"),
    TREASURER("Treasurer"),
    DONOR("Donor"),
    SUPER_ADMIN("Super Admin")
}

enum class ExamStream(val titleEn: String, val titleGu: String) {
    SSC("SSC (Std 10)", "એસ.એસ.સી (ધોરણ ૧૦)"),
    HSC_SCIENCE("HSC Science (Std 12)", "એચ.એસ.સી વિજ્ઞાન પ્રવાહ"),
    HSC_COMMERCE("HSC Commerce (Std 12)", "એચ.એસ.સી વાણિજ્ય પ્રવાહ"),
    HSC_ARTS("HSC Arts (Std 12)", "એચ.એસ.સી વિનયન પ્રવાહ"),
    OTHER("Higher Degree / Other", "ઉચ્ચ અભ્યાસ / અન્ય")
}

enum class VerificationState {
    PENDING,
    UNDER_REVIEW,
    VERIFIED,
    REJECTED
}

enum class AwardApprovalState {
    DRAFT,
    PENDING_COMMITTEE,
    APPROVED,
    REJECTED,
    CANCELLED
}

enum class PaymentState {
    PENDING,
    APPROVED,
    PAID,
    FAILED,
    REVERSED
}

enum class PaymentChannel {
    BANK_TRANSFER,
    CHEQUE,
    CASH
}

enum class TieResolutionRule {
    SPLIT_AMOUNT,
    DUPLICATE_PRIZE,
    SENIORITY_BY_AGE,
    COMMITTEE_DECISION
}

data class SocietyItem(
    val id: String,
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

data class StudentProfile(
    val id: String = UUID.randomUUID().toString(),
    val societyId: String,
    val fullName: String,
    val fullNameGujarati: String = "",
    val guardianName: String,
    val guardianMobile: String,
    val guardianConsent: Boolean = true,
    val schoolName: String,
    val standard: String,
    val registrationCode: String
)

data class SubjectScore(
    val subjectName: String,
    val maximumMarks: Int = 100,
    val obtainedMarks: Int = 0
)

data class AcademicRecordItem(
    val id: String = UUID.randomUUID().toString(),
    val societyId: String,
    val studentId: String,
    val studentName: String,
    val academicYear: String,
    val examinationCategory: ExamStream,
    val board: String = "GSEB",
    val school: String,
    val stream: String = "General",
    val subjects: List<SubjectScore>,
    val totalMaximumMarks: Int,
    val totalObtainedMarks: Int,
    val percentage: Double,
    val result: String,
    val verificationStatus: VerificationState = VerificationState.PENDING,
    val verificationRemarks: String = "",
    val marksheetDocumentUri: String? = null
)

data class AwardCategoryItem(
    val id: String = UUID.randomUUID().toString(),
    val societyId: String,
    val code: String,
    val name: String,
    val nameGujarati: String,
    val description: String = ""
)

data class AwardRuleItem(
    val id: String = UUID.randomUUID().toString(),
    val societyId: String,
    val categoryId: String,
    val academicYear: String,
    val examinationCategory: ExamStream,
    val title: String,
    val titleGujarati: String,
    val rankTarget: Int? = null,
    val subjectTarget: String? = null,
    val minimumPercentageThreshold: Double? = null,
    val cashAmount: Int,
    val tiePolicy: TieResolutionRule = TieResolutionRule.DUPLICATE_PRIZE,
    val requiresCommitteeApproval: Boolean = true,
    val dedicatedMemorialFundId: String? = null
)

data class AwardGrant(
    val id: String = UUID.randomUUID().toString(),
    val societyId: String,
    val studentId: String,
    val studentName: String,
    val academicYear: String,
    val examinationCategory: ExamStream,
    val categoryId: String,
    val ruleId: String,
    val awardTitle: String,
    val awardTitleGujarati: String,
    val eligibilityEvidence: String,
    val amount: Int,
    val approvalStatus: AwardApprovalState = AwardApprovalState.PENDING_COMMITTEE,
    val paymentStatus: PaymentState = PaymentState.PENDING,
    val paymentMode: PaymentChannel? = null,
    val transactionRef: String? = null,
    val receiptNumber: String? = null,
    val committeeNotes: String? = null,
    val donorName: String? = null,
    val memorialFundName: String? = null
)

data class DonorProfile(
    val id: String = UUID.randomUUID().toString(),
    val societyId: String,
    val name: String,
    val nameGujarati: String,
    val contactNumber: String,
    val email: String,
    val totalContributed: Long = 0L
)

data class MemorialFundItem(
    val id: String = UUID.randomUUID().toString(),
    val societyId: String,
    val donorId: String,
    val donorName: String,
    val fundName: String,
    val fundNameGujarati: String,
    val inMemoryOf: String,
    val fdCertificateNumber: String,
    val bankName: String,
    val fdPrincipalAmount: Long, // Kept strictly separate from interest!
    val fdInterestRatePercent: Double = 7.25,
    val actualInterestReceivedTotal: Long, // Only bank-supported actual interest counts!
    val allocatedAmount: Long = 0L,
    val availableBalance: Long,
    val purposeRestriction: String = "Merit Awards for SSC & HSC Students"
)

data class FinancialTransactionItem(
    val id: String = UUID.randomUUID().toString(),
    val societyId: String,
    val fundType: String,
    val transactionType: String,
    val amount: Long,
    val referenceNumber: String,
    val narrative: String,
    val isReversed: Boolean = false,
    val date: String
)

data class PaymentRecordItem(
    val id: String = UUID.randomUUID().toString(),
    val societyId: String,
    val awardId: String,
    val studentName: String,
    val awardTitle: String,
    val amount: Int,
    val paymentMode: PaymentChannel,
    val status: PaymentState,
    val receiptNumber: String,
    val bankRef: String,
    val date: String
)

data class AuditLogRecord(
    val id: String = UUID.randomUUID().toString(),
    val societyId: String,
    val action: String,
    val performedBy: String,
    val details: String,
    val timestamp: String
)
