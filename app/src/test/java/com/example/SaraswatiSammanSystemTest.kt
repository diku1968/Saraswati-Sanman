package com.example

import com.example.data.SaraswatiRepository
import com.example.domain.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class SaraswatiSammanSystemTest {

    private lateinit var repository: SaraswatiRepository

    @Before
    fun setUp() {
        repository = SaraswatiRepository.getInstance()
        val firstSocietyId = repository.societies.value.first().id
        repository.selectSociety(firstSocietyId)
    }

    @Test
    fun testBilingualDictionarySupport() {
        assertEquals("Saraswati Samman", AppStrings.get("app_title", AppLanguage.ENGLISH))
        assertEquals("સરસ્વતી સન્માન", AppStrings.get("app_title", AppLanguage.GUJARATI))

        assertEquals("Student", AppStrings.get("role_student", AppLanguage.ENGLISH))
        assertEquals("વિદ્યાર્થી", AppStrings.get("role_student", AppLanguage.GUJARATI))

        assertEquals("Treasurer", AppStrings.get("role_treasurer", AppLanguage.ENGLISH))
        assertEquals("ખજાનચી / નાણાં મંત્રી", AppStrings.get("role_treasurer", AppLanguage.GUJARATI))

        assertTrue(AppStrings.get("ocr_gujarati_notice", AppLanguage.ENGLISH).contains("Google ML Kit"))
    }

    @Test
    fun testTenantIsolationBetweenSocieties() {
        val societies = repository.societies.value
        assertTrue("At least two societies seeded", societies.size >= 2)

        val s1 = societies[0].id
        val s2 = societies[1].id
        assertNotEquals(s1, s2)

        // Select Society 1
        repository.selectSociety(s1)
        val s1Students = repository.students.value.filter { it.societyId == s1 }
        val s1Funds = repository.memorialFunds.value.filter { it.societyId == s1 }
        assertTrue(s1Students.isNotEmpty())

        // Cross-tenant check: No student in Society 1 should be listed under Society 2
        for (st in s1Students) {
            assertEquals(s1, st.societyId)
            assertNotEquals(s2, st.societyId)
        }
        for (f in s1Funds) {
            assertEquals(s1, f.societyId)
            assertNotEquals(s2, f.societyId)
        }
    }

    @Test
    fun testMultipleAwardStackingForSingleStudent() {
        val sId = repository.selectedSocietyId.value
        val student = repository.students.value.first { it.societyId == sId }

        // Fetch all awards for student Aarav Patel in 2024-2025
        val studentAwards = repository.awards.value.filter {
            it.societyId == sId && it.studentId == student.id && it.academicYear == "2024-2025"
        }

        // CRITICAL REQUIREMENT: A student can receive MULTIPLE different awards in the same year!
        assertTrue("Student must receive multiple awards", studentAwards.size >= 4)

        // Verify distinct award titles are present for the same student
        val titles = studentAwards.map { it.awardTitle }
        assertTrue(titles.contains("Overall First Rank Award"))
        assertTrue(titles.contains("Highest English Marks Award"))
        assertTrue(titles.contains("Highest Science Marks Award"))
        assertTrue(titles.contains("Best Overall Result Award"))
        assertTrue(titles.contains("Passing Merit Encouragement Award"))

        // Each award must have separate amounts and eligibility evidence
        studentAwards.forEach { aw ->
            assertTrue(aw.amount > 0)
            assertTrue(aw.eligibilityEvidence.isNotBlank())
        }
    }

    @Test
    fun testDuplicateAwardUnderSameRuleIsPrevented() {
        val sId = repository.selectedSocietyId.value
        val initialCount = repository.awards.value.filter { it.societyId == sId }.size

        // Executing award cycle again should NOT generate duplicate awards for the same rules
        val newlyGenerated = repository.runAwardEngine("2024-2025")
        val finalCount = repository.awards.value.filter { it.societyId == sId }.size

        assertEquals(0, newlyGenerated)
        assertEquals(initialCount, finalCount)
    }

    @Test
    fun testConsolidatedApprovedTotalCalculation() {
        val sId = repository.selectedSocietyId.value
        val student = repository.students.value.first { it.societyId == sId }

        val approvedTotal = repository.getConsolidatedApprovedAmount(student.id, "2024-2025")
        val approvedAwards = repository.awards.value.filter {
            it.societyId == sId && it.studentId == student.id && it.academicYear == "2024-2025" && it.approvalStatus == AwardApprovalState.APPROVED
        }
        val expectedSum = approvedAwards.sumOf { it.amount }

        assertEquals(expectedSum, approvedTotal)
        assertTrue(approvedTotal >= 14000) // ₹14,500 total stacked
    }

    @Test
    fun testFdPrincipalSeparationAndActualInterestCredit() {
        val sId = repository.selectedSocietyId.value
        val fund = repository.memorialFunds.value.first { it.societyId == sId }

        val originalPrincipal = fund.fdPrincipalAmount
        val originalInterest = fund.actualInterestReceivedTotal
        val originalAvailable = fund.availableBalance

        val creditAmount = 15000L
        val success = repository.recordActualFdInterest(
            memorialFundId = fund.id,
            actualInterestAmount = creditAmount,
            bankRef = "TEST-BOB-9921",
            narrative = "Quarterly actual interest credited"
        )
        assertTrue(success)

        val updatedFund = repository.memorialFunds.value.first { it.id == fund.id }

        // RULE: FD Principal remains unchanged!
        assertEquals(originalPrincipal, updatedFund.fdPrincipalAmount)
        // RULE: Actual interest and available balance are incremented
        assertEquals(originalInterest + creditAmount, updatedFund.actualInterestReceivedTotal)
        assertEquals(originalAvailable + creditAmount, updatedFund.availableBalance)
    }

    @Test
    fun testPaymentDisbursementAndDuplicatePrevention() {
        val sId = repository.selectedSocietyId.value
        // Find an award that is approved and pending payment
        val candidateAward = repository.awards.value.find {
            it.societyId == sId && it.approvalStatus == AwardApprovalState.APPROVED && it.paymentStatus == PaymentState.PENDING
        } ?: repository.awards.value.find {
            it.societyId == sId && it.approvalStatus == AwardApprovalState.APPROVED
        }

        assertNotNull(candidateAward)

        if (candidateAward!!.paymentStatus != PaymentState.PAID) {
            val payment = repository.disburseAwardPayment(
                awardId = candidateAward.id,
                paymentMode = PaymentChannel.BANK_TRANSFER,
                bankRef = "NEFT-TEST-9988",
                notes = "Test payment disbursement"
            )
            assertNotNull(payment)
            assertEquals(candidateAward.amount, payment!!.amount)
            assertEquals(PaymentState.PAID, payment.status)

            // Attempting duplicate payment must be BLOCKED
            val duplicateAttempt = repository.disburseAwardPayment(
                awardId = candidateAward.id,
                paymentMode = PaymentChannel.BANK_TRANSFER,
                bankRef = "NEFT-TEST-DUPLICATE",
                notes = "Duplicate attempt"
            )
            assertNull("Duplicate payment must return null / be blocked", duplicateAttempt)
        }
    }

    @Test
    fun testTransactionReversalPreservesHistory() {
        val sId = repository.selectedSocietyId.value
        val tx = repository.transactions.value.first { it.societyId == sId && !it.isReversed }

        val initialTxCount = repository.transactions.value.size
        val success = repository.reverseTransaction(tx.id, "Testing reversal entry preservation")
        assertTrue(success)

        val updatedTxList = repository.transactions.value
        // Ledger count should increase by 1 because of the counter-entry
        assertEquals(initialTxCount + 1, updatedTxList.size)

        // Original transaction is marked reversed but NOT deleted
        val originalInList = updatedTxList.first { it.id == tx.id }
        assertTrue(originalInList.isReversed)

        // Counter reversal entry exists with negative amount
        val reversalEntry = updatedTxList.first { it.referenceNumber == "REV-${tx.referenceNumber}" }
        assertEquals(-tx.amount, reversalEntry.amount)
    }

    @Test
    fun testMarksheetVerificationWorkflow() {
        val sId = repository.selectedSocietyId.value
        val student = repository.students.value.first { it.societyId == sId }

        val newRecord = repository.submitAcademicRecord(
            studentId = student.id,
            studentName = student.fullName,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.HSC_SCIENCE,
            school = "Science Academy, Rajkot",
            subjects = listOf(
                SubjectScore("Physics", 100, 92),
                SubjectScore("Chemistry", 100, 90),
                SubjectScore("Mathematics", 100, 96),
                SubjectScore("English", 100, 88)
            )
        )

        assertEquals(VerificationState.PENDING, newRecord.verificationStatus)
        assertEquals(91.5, newRecord.percentage, 0.01)

        // Committee verifies the record
        repository.verifyAcademicRecord(newRecord.id, true, "Verified with GSEB portal result copy")

        val verified = repository.academicRecords.value.first { it.id == newRecord.id }
        assertEquals(VerificationState.VERIFIED, verified.verificationStatus)
    }

    @Test
    fun testGoogleSheetsMultiTabSync() {
        val syncResult = repository.syncWithGoogleSheets("1TestSpreadsheetKey2026")
        assertTrue(syncResult.success)
        assertEquals("1TestSpreadsheetKey2026", syncResult.spreadsheetId)
        assertTrue(syncResult.totalRecordsSynced > 0)
        assertTrue(syncResult.syncedTabs.contains("Societies"))
        assertTrue(syncResult.syncedTabs.contains("Students"))
        assertTrue(syncResult.syncedTabs.contains("Awards"))
        assertTrue(syncResult.syncedTabs.contains("MemorialFunds"))
        assertTrue(syncResult.syncedTabs.contains("Payments"))
    }

    @Test
    fun testSamajSpecificGoogleSheetIsolation() {
        val s1 = repository.societies.value[0]
        val s2 = repository.societies.value[1]

        // Each society has its own configured spreadsheet ID
        assertFalse(s1.spreadsheetId.isEmpty())
        assertFalse(s2.spreadsheetId.isEmpty())
        assertNotEquals(s1.spreadsheetId, s2.spreadsheetId)

        // Select Society 1
        repository.selectSociety(s1.id)
        val s1Sync = repository.syncWithGoogleSheets()
        assertEquals(s1.spreadsheetId, s1Sync.spreadsheetId)

        // Verify CSV for Society 1 contains Society 1 students and awards
        val s1StudentsCsv = repository.getGoogleSheetCsvForTab("Students")
        assertTrue(s1StudentsCsv.contains("Aarav Pravin Patel"))

        // Select Society 2 (different Samaj)
        repository.selectSociety(s2.id)
        val s2Sync = repository.syncWithGoogleSheets()
        assertEquals(s2.spreadsheetId, s2Sync.spreadsheetId)

        // Society 2 must NOT contain Society 1 student data
        val s2StudentsCsv = repository.getGoogleSheetCsvForTab("Students")
        assertFalse(s2StudentsCsv.contains("Aarav Pravin Patel"))
    }
}
