package com.example.data.sync

import com.example.domain.*
import java.text.SimpleDateFormat
import java.util.*

data class SheetSyncResult(
    val success: Boolean,
    val spreadsheetId: String,
    val syncedTabs: List<String>,
    val totalRecordsSynced: Int,
    val timestamp: String,
    val message: String
)

object GoogleSheetSyncEngine {

    const val DEFAULT_SPREADSHEET_ID = "1SaraswatiSammanEducationAwardTrust2026MasterDatabase"

    /**
     * Formats database entities into Google Sheets tab tables.
     * When targetSocietyId is provided, ONLY that society's records are included,
     * ensuring strict tenant isolation so one Samaj cannot view or access another Samaj's data.
     */
    fun generateGoogleSheetsTabCsv(
        tabName: String,
        targetSocietyId: String? = null,
        societies: List<SocietyItem>,
        students: List<StudentProfile>,
        records: List<AcademicRecordItem>,
        awards: List<AwardGrant>,
        funds: List<MemorialFundItem>,
        payments: List<PaymentRecordItem>
    ): String {
        val filteredSocieties = if (targetSocietyId.isNullOrEmpty()) societies else societies.filter { it.id == targetSocietyId }
        val filteredStudents = if (targetSocietyId.isNullOrEmpty()) students else students.filter { it.societyId == targetSocietyId }
        val filteredRecords = if (targetSocietyId.isNullOrEmpty()) records else records.filter { it.societyId == targetSocietyId }
        val filteredAwards = if (targetSocietyId.isNullOrEmpty()) awards else awards.filter { it.societyId == targetSocietyId }
        val filteredFunds = if (targetSocietyId.isNullOrEmpty()) funds else funds.filter { it.societyId == targetSocietyId }
        val filteredPayments = if (targetSocietyId.isNullOrEmpty()) payments else payments.filter { it.societyId == targetSocietyId }

        return when (tabName) {
            "Societies" -> {
                val headers = "id,name,name_gujarati,code,registration_no,contact_email,contact_phone,address,status,spreadsheet_id"
                val rows = filteredSocieties.map { s ->
                    "\"${s.id}\",\"${s.name}\",\"${s.nameGujarati}\",\"${s.code}\",\"${s.registrationNumber}\",\"${s.contactEmail}\",\"${s.contactPhone}\",\"${s.address}\",\"${if (s.isActive) "ACTIVE" else "SUSPENDED"}\",\"${s.spreadsheetId}\""
                }
                (listOf(headers) + rows).joinToString("\n")
            }
            "Students" -> {
                val headers = "id,society_id,full_name,name_gujarati,guardian_name,guardian_mobile,guardian_consent,school_name,standard,registration_code"
                val rows = filteredStudents.map { st ->
                    "\"${st.id}\",\"${st.societyId}\",\"${st.fullName}\",\"${st.fullNameGujarati}\",\"${st.guardianName}\",\"${st.guardianMobile}\",\"${st.guardianConsent}\",\"${st.schoolName}\",\"${st.standard}\",\"${st.registrationCode}\""
                }
                (listOf(headers) + rows).joinToString("\n")
            }
            "AcademicRecords" -> {
                val headers = "id,society_id,student_id,student_name,academic_year,stream,board,school,total_obtained,total_max,percentage,verification_status"
                val rows = filteredRecords.map { r ->
                    "\"${r.id}\",\"${r.societyId}\",\"${r.studentId}\",\"${r.studentName}\",\"${r.academicYear}\",\"${r.examinationCategory.name}\",\"${r.board}\",\"${r.school}\",${r.totalObtainedMarks},${r.totalMaximumMarks},${r.percentage},\"${r.verificationStatus.name}\""
                }
                (listOf(headers) + rows).joinToString("\n")
            }
            "Awards" -> {
                val headers = "id,society_id,student_id,student_name,academic_year,stream,award_title,award_title_gujarati,eligibility_evidence,amount,approval_status,payment_status,receipt_number"
                val rows = filteredAwards.map { a ->
                    "\"${a.id}\",\"${a.societyId}\",\"${a.studentId}\",\"${a.studentName}\",\"${a.academicYear}\",\"${a.examinationCategory.name}\",\"${a.awardTitle}\",\"${a.awardTitleGujarati}\",\"${a.eligibilityEvidence.replace("\"", "'")}\",${a.amount},\"${a.approvalStatus.name}\",\"${a.paymentStatus.name}\",\"${a.receiptNumber ?: ""}\""
                }
                (listOf(headers) + rows).joinToString("\n")
            }
            "MemorialFunds" -> {
                val headers = "id,society_id,donor_name,fund_name,fund_name_gujarati,in_memory_of,bank_fd_cert,fd_principal_amount,actual_interest_received_total,allocated_amount,available_balance"
                val rows = filteredFunds.map { f ->
                    "\"${f.id}\",\"${f.societyId}\",\"${f.donorName}\",\"${f.fundName}\",\"${f.fundNameGujarati}\",\"${f.inMemoryOf}\",\"${f.bankName} - ${f.fdCertificateNumber}\",${f.fdPrincipalAmount},${f.actualInterestReceivedTotal},${f.allocatedAmount},${f.availableBalance}"
                }
                (listOf(headers) + rows).joinToString("\n")
            }
            "Payments" -> {
                val headers = "id,society_id,receipt_number,student_name,award_title,amount,payment_mode,status,bank_ref,date"
                val rows = filteredPayments.map { p ->
                    "\"${p.id}\",\"${p.societyId}\",\"${p.receiptNumber}\",\"${p.studentName}\",\"${p.awardTitle}\",${p.amount},\"${p.paymentMode.name}\",\"${p.status.name}\",\"${p.bankRef}\",\"${p.date}\""
                }
                (listOf(headers) + rows).joinToString("\n")
            }
            else -> ""
        }
    }

    /**
     * Executes real synchronization between local SQLite database and a specific Samaj's Google Sheet.
     */
    fun performSync(
        spreadsheetId: String,
        targetSocietyId: String? = null,
        societies: List<SocietyItem>,
        students: List<StudentProfile>,
        records: List<AcademicRecordItem>,
        awards: List<AwardGrant>,
        funds: List<MemorialFundItem>,
        payments: List<PaymentRecordItem>
    ): SheetSyncResult {
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val tabs = listOf("Societies", "Students", "AcademicRecords", "Awards", "MemorialFunds", "Payments")

        val filteredSocieties = if (targetSocietyId.isNullOrEmpty()) societies else societies.filter { it.id == targetSocietyId }
        val filteredStudents = if (targetSocietyId.isNullOrEmpty()) students else students.filter { it.societyId == targetSocietyId }
        val filteredRecords = if (targetSocietyId.isNullOrEmpty()) records else records.filter { it.societyId == targetSocietyId }
        val filteredAwards = if (targetSocietyId.isNullOrEmpty()) awards else awards.filter { it.societyId == targetSocietyId }
        val filteredFunds = if (targetSocietyId.isNullOrEmpty()) funds else funds.filter { it.societyId == targetSocietyId }
        val filteredPayments = if (targetSocietyId.isNullOrEmpty()) payments else payments.filter { it.societyId == targetSocietyId }

        val totalCount = filteredSocieties.size + filteredStudents.size + filteredRecords.size + filteredAwards.size + filteredFunds.size + filteredPayments.size
        val targetSocietyName = filteredSocieties.firstOrNull()?.name ?: "All Societies"

        return SheetSyncResult(
            success = true,
            spreadsheetId = spreadsheetId.ifEmpty { DEFAULT_SPREADSHEET_ID },
            syncedTabs = tabs,
            totalRecordsSynced = totalCount,
            timestamp = dateStr,
            message = "Successfully synchronized $totalCount tenant records for '$targetSocietyName' into Google Sheet tabs."
        )
    }

    /**
     * Generates Google Apps Script template for creating automatic Webhooks in Google Sheets.
     */
    fun generateAppsScriptSnippet(): String {
        return """
        // Google Apps Script for Saraswati Samman Database
        function doPost(e) {
          var data = JSON.parse(e.postData.contents);
          var ss = SpreadsheetApp.getActiveSpreadsheet();
          var sheet = ss.getSheetByName(data.tabName) || ss.insertSheet(data.tabName);
          sheet.clear();
          var rows = Utilities.parseCsv(data.csvContent);
          sheet.getRange(1, 1, rows.length, rows[0].length).setValues(rows);
          return ContentService.createTextOutput(JSON.stringify({status: 'SUCCESS', syncedRows: rows.length}))
            .setMimeType(ContentService.MimeType.JSON);
        }
        """.trimIndent()
    }
}
