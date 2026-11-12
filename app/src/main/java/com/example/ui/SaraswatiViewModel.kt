package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.SaraswatiRepository
import com.example.data.sync.SheetSyncResult
import com.example.domain.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class UiMessage(val message: String, val isError: Boolean = false)

class SaraswatiViewModel(
    private val repository: SaraswatiRepository = SaraswatiRepository.getInstance()
) : ViewModel() {

    val currentLanguage: StateFlow<AppLanguage> = repository.currentLanguage
    val currentRole: StateFlow<Role> = repository.currentRole
    val societies: StateFlow<List<SocietyItem>> = repository.societies
    val selectedSocietyId: StateFlow<String> = repository.selectedSocietyId

    // Scoped Data by Society Tenant
    val activeSociety: StateFlow<SocietyItem?> = combine(societies, selectedSocietyId) { socs, selId ->
        socs.find { it.id == selId } ?: socs.firstOrNull()
    }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val students: StateFlow<List<StudentProfile>> = combine(repository.students, selectedSocietyId) { list, sId ->
        list.filter { it.societyId == sId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val academicRecords: StateFlow<List<AcademicRecordItem>> = combine(repository.academicRecords, selectedSocietyId) { list, sId ->
        list.filter { it.societyId == sId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val awardCategories: StateFlow<List<AwardCategoryItem>> = combine(repository.awardCategories, selectedSocietyId) { list, sId ->
        list.filter { it.societyId == sId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val awardRules: StateFlow<List<AwardRuleItem>> = combine(repository.awardRules, selectedSocietyId) { list, sId ->
        list.filter { it.societyId == sId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val awards: StateFlow<List<AwardGrant>> = combine(repository.awards, selectedSocietyId) { list, sId ->
        list.filter { it.societyId == sId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val donors: StateFlow<List<DonorProfile>> = combine(repository.donors, selectedSocietyId) { list, sId ->
        list.filter { it.societyId == sId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val memorialFunds: StateFlow<List<MemorialFundItem>> = combine(repository.memorialFunds, selectedSocietyId) { list, sId ->
        list.filter { it.societyId == sId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val transactions: StateFlow<List<FinancialTransactionItem>> = combine(repository.transactions, selectedSocietyId) { list, sId ->
        list.filter { it.societyId == sId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val payments: StateFlow<List<PaymentRecordItem>> = combine(repository.payments, selectedSocietyId) { list, sId ->
        list.filter { it.societyId == sId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val auditLogs: StateFlow<List<AuditLogRecord>> = combine(repository.auditLogs, selectedSocietyId) { list, sId ->
        list.filter { it.societyId == sId }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val sheetSyncResult: StateFlow<SheetSyncResult?> = repository.sheetSyncResult

    // Feedback message channel
    private val _uiMessage = MutableSharedFlow<UiMessage>()
    val uiMessage: SharedFlow<UiMessage> = _uiMessage.asSharedFlow()

    fun toggleLanguage() {
        val next = if (currentLanguage.value == AppLanguage.ENGLISH) AppLanguage.GUJARATI else AppLanguage.ENGLISH
        repository.setLanguage(next)
    }

    fun setLanguage(language: AppLanguage) {
        repository.setLanguage(language)
    }

    fun setRole(role: Role) {
        repository.setRole(role)
        emitMessage("Active role changed to ${role.displayName}")
    }

    fun selectSociety(societyId: String) {
        repository.selectSociety(societyId)
        val soc = societies.value.find { it.id == societyId }
        emitMessage("Switched to ${soc?.name ?: societyId}")
    }

    fun registerStudent(
        fullName: String,
        fullNameGujarati: String,
        guardianName: String,
        guardianMobile: String,
        schoolName: String,
        standard: String
    ) {
        viewModelScope.launch {
            if (fullName.isBlank() || guardianName.isBlank() || guardianMobile.isBlank()) {
                emitMessage("All fields including guardian details are required", isError = true)
                return@launch
            }
            val student = repository.registerStudent(
                fullName = fullName,
                fullNameGujarati = fullNameGujarati,
                guardianName = guardianName,
                guardianMobile = guardianMobile,
                schoolName = schoolName,
                standard = standard
            )
            emitMessage("Student ${student.fullName} registered successfully! Registration Code: ${student.registrationCode}")
        }
    }

    fun submitAcademicRecord(
        studentId: String,
        studentName: String,
        academicYear: String,
        examStream: ExamStream,
        school: String,
        subjects: List<SubjectScore>
    ) {
        viewModelScope.launch {
            if (subjects.isEmpty()) {
                emitMessage("Please add at least one subject with marks", isError = true)
                return@launch
            }
            val record = repository.submitAcademicRecord(
                studentId = studentId,
                studentName = studentName,
                academicYear = academicYear,
                examinationCategory = examStream,
                school = school,
                subjects = subjects
            )
            emitMessage("Academic Record for $studentName submitted! Percentage: ${record.percentage}%")
        }
    }

    fun verifyRecord(recordId: String, isApproved: Boolean, remarks: String) {
        viewModelScope.launch {
            repository.verifyAcademicRecord(recordId, isApproved, remarks)
            emitMessage("Marksheet review submitted: ${if (isApproved) "VERIFIED" else "REJECTED"}")
        }
    }

    fun runAwardEngine(academicYear: String = "2024-2025") {
        viewModelScope.launch {
            val count = repository.runAwardEngine(academicYear)
            emitMessage("Award Engine executed for $academicYear! Generated $count award allocations.")
        }
    }

    fun approveOrRejectAward(awardId: String, isApproved: Boolean, notes: String) {
        viewModelScope.launch {
            repository.updateAwardApproval(awardId, isApproved, notes)
            emitMessage("Award decision updated: ${if (isApproved) "APPROVED" else "REJECTED"}")
        }
    }

    fun recordFdInterest(memorialFundId: String, amount: Long, bankRef: String, narrative: String) {
        viewModelScope.launch {
            val success = repository.recordActualFdInterest(memorialFundId, amount, bankRef, narrative)
            if (success) {
                emitMessage("Actual FD interest ₹$amount credited successfully!")
            } else {
                emitMessage("Failed to record interest. Check amount.", isError = true)
            }
        }
    }

    fun disburseAwardPayment(awardId: String, paymentMode: PaymentChannel, bankRef: String, notes: String) {
        viewModelScope.launch {
            val payment = repository.disburseAwardPayment(awardId, paymentMode, bankRef, notes)
            if (payment != null) {
                emitMessage("Payment of ₹${payment.amount} disbursed! Receipt: ${payment.receiptNumber}")
            } else {
                emitMessage("Payment blocked: Award already paid or unapproved.", isError = true)
            }
        }
    }

    fun reverseTransaction(txId: String, reason: String) {
        viewModelScope.launch {
            val success = repository.reverseTransaction(txId, reason)
            if (success) {
                emitMessage("Transaction reversed successfully with audit entry.")
            } else {
                emitMessage("Cannot reverse transaction.", isError = true)
            }
        }
    }

    fun getConsolidatedApprovedTotal(studentId: String, academicYear: String = "2024-2025"): Int {
        return repository.getConsolidatedApprovedAmount(studentId, academicYear)
    }

    fun exportAwardRegisterCsv(academicYear: String = "2024-2025"): String {
        return repository.generateAnnualAwardRegisterCsv(academicYear, currentLanguage.value)
    }

    fun exportFdInterestCsv(): String {
        return repository.generateFdInterestReportCsv(currentLanguage.value)
    }

    fun updateSocietySpreadsheetId(societyId: String, newSpreadsheetId: String) {
        repository.updateSocietySpreadsheetId(societyId, newSpreadsheetId)
        emitMessage("Updated Google Spreadsheet ID for society!")
    }

    fun syncWithGoogleSheets(spreadsheetId: String = "") {
        viewModelScope.launch {
            val currentSoc = activeSociety.value
            val targetId = spreadsheetId.ifEmpty { currentSoc?.spreadsheetId ?: "" }
            val res = repository.syncWithGoogleSheets(targetId, currentSoc?.id)
            val name = currentSoc?.name ?: "Active Samaj"
            emitMessage("Synced ${res.totalRecordsSynced} records for $name to Google Sheet (${res.syncedTabs.joinToString()})")
        }
    }

    fun getGoogleSheetCsvForTab(tabName: String): String {
        return repository.getGoogleSheetCsvForTab(tabName, activeSociety.value?.id)
    }

    fun getAppsScriptTemplate(): String {
        return repository.getAppsScriptWebhookCode()
    }

    private fun emitMessage(msg: String, isError: Boolean = false) {
        viewModelScope.launch {
            _uiMessage.emit(UiMessage(msg, isError))
        }
    }
}
