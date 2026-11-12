package com.example.data

import com.example.domain.*
import com.example.data.sync.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.*

class SaraswatiRepository private constructor() {

    companion object {
        @Volatile
        private var instance: SaraswatiRepository? = null

        fun getInstance(): SaraswatiRepository {
            return instance ?: synchronized(this) {
                instance ?: SaraswatiRepository().also { instance = it }
            }
        }
    }

    // Active User Context
    private val _currentLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

    private val _currentRole = MutableStateFlow(Role.STUDENT)
    val currentRole: StateFlow<Role> = _currentRole.asStateFlow()

    private val _societies = MutableStateFlow<List<SocietyItem>>(emptyList())
    val societies: StateFlow<List<SocietyItem>> = _societies.asStateFlow()

    private val _selectedSocietyId = MutableStateFlow<String>("")
    val selectedSocietyId: StateFlow<String> = _selectedSocietyId.asStateFlow()

    // Tenant Collections (Society-Scoped Data)
    private val _students = MutableStateFlow<List<StudentProfile>>(emptyList())
    val students: StateFlow<List<StudentProfile>> = _students.asStateFlow()

    private val _academicRecords = MutableStateFlow<List<AcademicRecordItem>>(emptyList())
    val academicRecords: StateFlow<List<AcademicRecordItem>> = _academicRecords.asStateFlow()

    private val _awardCategories = MutableStateFlow<List<AwardCategoryItem>>(emptyList())
    val awardCategories: StateFlow<List<AwardCategoryItem>> = _awardCategories.asStateFlow()

    private val _awardRules = MutableStateFlow<List<AwardRuleItem>>(emptyList())
    val awardRules: StateFlow<List<AwardRuleItem>> = _awardRules.asStateFlow()

    private val _awards = MutableStateFlow<List<AwardGrant>>(emptyList())
    val awards: StateFlow<List<AwardGrant>> = _awards.asStateFlow()

    private val _donors = MutableStateFlow<List<DonorProfile>>(emptyList())
    val donors: StateFlow<List<DonorProfile>> = _donors.asStateFlow()

    private val _memorialFunds = MutableStateFlow<List<MemorialFundItem>>(emptyList())
    val memorialFunds: StateFlow<List<MemorialFundItem>> = _memorialFunds.asStateFlow()

    private val _transactions = MutableStateFlow<List<FinancialTransactionItem>>(emptyList())
    val transactions: StateFlow<List<FinancialTransactionItem>> = _transactions.asStateFlow()

    private val _payments = MutableStateFlow<List<PaymentRecordItem>>(emptyList())
    val payments: StateFlow<List<PaymentRecordItem>> = _payments.asStateFlow()

    private val _auditLogs = MutableStateFlow<List<AuditLogRecord>>(emptyList())
    val auditLogs: StateFlow<List<AuditLogRecord>> = _auditLogs.asStateFlow()

    private val _sheetSyncResult = MutableStateFlow<SheetSyncResult?>(null)
    val sheetSyncResult: StateFlow<SheetSyncResult?> = _sheetSyncResult.asStateFlow()

    init {
        seedInitialMultiSamajData()
    }

    fun setLanguage(language: AppLanguage) {
        _currentLanguage.value = language
    }

    fun setRole(role: Role) {
        _currentRole.value = role
    }

    fun selectSociety(societyId: String) {
        _selectedSocietyId.value = societyId
    }

    // ======================== MULTI-SOCIETY INITIAL SEED ========================

    private fun seedInitialMultiSamajData() {
        val s1 = SocietyItem(
            id = "soc_patidar_01",
            name = "Shree Saurashtra Kadva Patidar Samaj Shikshan Trust",
            nameGujarati = "શ્રી સૌરાષ્ટ્ર કડવા પાટીદાર સમાજ શિક્ષણ ટ્રસ્ટ",
            code = "SKPST",
            registrationNumber = "E/4592/RAJKOT",
            contactEmail = "patidaredu@samaj.org",
            contactPhone = "+91 98250 11223",
            address = "Patidar Bhavan, Race Course Ring Road, Rajkot, Gujarat",
            spreadsheetId = "1KadvaPatidarSamajShikshanTrust2026MasterDatabase"
        )

        val s2 = SocietyItem(
            id = "soc_modh_02",
            name = "Shree Modh Vanik Vidya Samvardhak Mandal",
            nameGujarati = "શ્રી મોઢ વણિક વિદ્યા સંવર્ધક મંડળ",
            code = "SMVVM",
            registrationNumber = "F/1284/AHMEDABAD",
            contactEmail = "modhvanik.edu@samaj.org",
            contactPhone = "+91 98240 44556",
            address = "Vanik Seva Sadan, Ellis Bridge, Ahmedabad, Gujarat",
            spreadsheetId = "1ModhVanikVidyaSamvardhakMandal2026Database"
        )

        val s3 = SocietyItem(
            id = "soc_kutch_03",
            name = "Shree Kutch Gurjar Kshatriya Kelavani Mandal",
            nameGujarati = "શ્રી કચ્છ ગુર્જર ક્ષત્રિય કેળવણી મંડળ",
            code = "KGKKM",
            registrationNumber = "E/3190/BHUJ",
            contactEmail = "kgk.kelavani@samaj.org",
            contactPhone = "+91 98251 77889",
            address = "Gurjar Kshatriya Bhavan, Station Road, Bhuj-Kutch, Gujarat",
            spreadsheetId = "1KutchGurjarKshatriyaKelavaniMandal2026Database"
        )

        _societies.value = listOf(s1, s2, s3)
        _selectedSocietyId.value = s1.id

        // Seed Award Categories for Society 1
        val catRank = AwardCategoryItem(
            id = "cat_rank_01",
            societyId = s1.id,
            code = "CAT_MERIT_RANK",
            name = "Overall Academic Rank Awards",
            nameGujarati = "સમગ્ર વાર્ષિક મેરિટ ક્રમાંક પુરસ્કારો",
            description = "Awards for top rank holders in board exams"
        )
        val catSubject = AwardCategoryItem(
            id = "cat_subject_02",
            societyId = s1.id,
            code = "CAT_SUBJECT_TOPPER",
            name = "Subject-Wise Excellence Awards",
            nameGujarati = "વિષયવાર સર્વોચ્ચ ગુણ પુરસ્કારો",
            description = "Awards for highest marks in key subjects like English, Maths, Science"
        )
        val catPassing = AwardCategoryItem(
            id = "cat_pass_03",
            societyId = s1.id,
            code = "CAT_MERIT_PASS",
            name = "General Merit Encouragement Award",
            nameGujarati = "સામાન્ય તેજસ્વી વિદ્યાર્થી પ્રોત્સાહન પુરસ્કાર",
            description = "Encouragement award for students securing 75% or above"
        )
        val catMemorial = AwardCategoryItem(
            id = "cat_memorial_04",
            societyId = s1.id,
            code = "CAT_MEMORIAL_SPECIAL",
            name = "Donor Memorial Special Excellence Award",
            nameGujarati = "દાતાશ્રી સ્મૃતિ વિશેષ શ્રેષ્ઠતા પુરસ્કાર",
            description = "Endowed awards funded from bank interest of memorial funds"
        )
        _awardCategories.value = listOf(catRank, catSubject, catPassing, catMemorial)

        // Seed Award Rules (Demonstrating MULTI-AWARD STACKING)
        val ruleRank1 = AwardRuleItem(
            id = "rule_r1",
            societyId = s1.id,
            categoryId = catRank.id,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            title = "Overall First Rank Award",
            titleGujarati = "સમગ્ર બોર્ડ પ્રથમ ક્રમાંક સુવર્ણ સન્માન",
            rankTarget = 1,
            minimumPercentageThreshold = 85.0,
            cashAmount = 5000,
            tiePolicy = TieResolutionRule.DUPLICATE_PRIZE
        )

        val ruleEnglish = AwardRuleItem(
            id = "rule_eng",
            societyId = s1.id,
            categoryId = catSubject.id,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            title = "Highest English Marks Award",
            titleGujarati = "અંગ્રેજી વિષયમાં સર્વોચ્ચ ગુણ પુરસ્કાર",
            subjectTarget = "English",
            cashAmount = 2000
        )

        val ruleScience = AwardRuleItem(
            id = "rule_sci",
            societyId = s1.id,
            categoryId = catSubject.id,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            title = "Highest Science Marks Award",
            titleGujarati = "વિજ્ઞાન અને ટેકનોલોજી સર્વોચ્ચ ગુણ પુરસ્કાર",
            subjectTarget = "Science",
            cashAmount = 2000
        )

        val ruleBestResult = AwardRuleItem(
            id = "rule_best",
            societyId = s1.id,
            categoryId = catRank.id,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            title = "Best Overall Result Award",
            titleGujarati = "શ્રેષ્ઠ પરિણામ ગૌરવ એવોર્ડ",
            rankTarget = 1,
            minimumPercentageThreshold = 90.0,
            cashAmount = 3000
        )

        val ruleMemorialSpecial = AwardRuleItem(
            id = "rule_mem_spec",
            societyId = s1.id,
            categoryId = catMemorial.id,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            title = "Late Shri Harilal Memorial Excellence Award",
            titleGujarati = "સ્વ. હરિલાલભાઈ સ્મૃતિ વિશેષ શ્રેષ્ઠતા એવોર્ડ",
            minimumPercentageThreshold = 80.0,
            cashAmount = 2000
        )

        val rulePassing = AwardRuleItem(
            id = "rule_pass",
            societyId = s1.id,
            categoryId = catPassing.id,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            title = "Board Merit Passing Encouragement Award",
            titleGujarati = "બોર્ડ પરીક્ષા સફળતા પ્રોત્સાહન પુરસ્કાર",
            minimumPercentageThreshold = 75.0,
            cashAmount = 500
        )

        _awardRules.value = listOf(ruleRank1, ruleEnglish, ruleScience, ruleBestResult, ruleMemorialSpecial, rulePassing)

        // Seed Donor and Memorial Funds with FD Principal separated from actual interest
        val donor1 = DonorProfile(
            id = "donor_01",
            societyId = s1.id,
            name = "Shri Pravinchandra Harilal Patel",
            nameGujarati = "શ્રી પ્રવીણચંદ્ર હરિલાલ પટેલ",
            contactNumber = "+91 98250 99887",
            email = "pravin.patel@patelgroup.com",
            totalContributed = 500000L
        )
        val donor2 = DonorProfile(
            id = "donor_02",
            societyId = s1.id,
            name = "Smt. Shantaben Manilal Parikh",
            nameGujarati = "શ્રીમતી શાંતાબેન મણીલાલ પરીખ",
            contactNumber = "+91 94260 11234",
            email = "shantaben@gmail.com",
            totalContributed = 300000L
        )
        _donors.value = listOf(donor1, donor2)

        val memFund1 = MemorialFundItem(
            id = "mem_01",
            societyId = s1.id,
            donorId = donor1.id,
            donorName = donor1.name,
            fundName = "Late Shri Harilal Patel Memorial Education Fund",
            fundNameGujarati = "સ્વ. હરિલાલ પટેલ સ્મૃતિ શિક્ષણ એવોર્ડ ભંડોળ",
            inMemoryOf = "Late Shri Harilal Ambalal Patel (Beloved Grandfather)",
            fdCertificateNumber = "FD/BOB/2022/9841",
            bankName = "Bank of Baroda, Rajkot Main Branch",
            fdPrincipalAmount = 500000L, // Fixed Deposit Principal: NEVER spent directly!
            fdInterestRatePercent = 7.25,
            actualInterestReceivedTotal = 36250L, // Actual bank interest credited!
            allocatedAmount = 14500L,
            availableBalance = 21750L,
            purposeRestriction = "SSC & HSC High Rankers & Toppers"
        )
        _memorialFunds.value = listOf(memFund1)

        // Seed Initial Students
        val stu1 = StudentProfile(
            id = "stu_01",
            societyId = s1.id,
            fullName = "Aarav Pravin Patel",
            fullNameGujarati = "આરવ પ્રવીણ પટેલ",
            guardianName = "Pravinbhai H. Patel",
            guardianMobile = "+91 98250 99887",
            guardianConsent = true,
            schoolName = "Shree Swaminarayan Gurukul, Rajkot",
            standard = "Std 10 (SSC)",
            registrationCode = "SKP-2024-001"
        )

        val stu2 = StudentProfile(
            id = "stu_02",
            societyId = s1.id,
            fullName = "Diya Kiritbhai Patel",
            fullNameGujarati = "દિયા કિરીટભાઈ પટેલ",
            guardianName = "Kiritbhai Patel",
            guardianMobile = "+91 98240 55667",
            guardianConsent = true,
            schoolName = "Modi Vidya Niketan, Rajkot",
            standard = "Std 10 (SSC)",
            registrationCode = "SKP-2024-002"
        )
        _students.value = listOf(stu1, stu2)

        // Seed Academic Records with Marksheet Details
        val rec1 = AcademicRecordItem(
            id = "rec_01",
            societyId = s1.id,
            studentId = stu1.id,
            studentName = stu1.fullName,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            board = "GSEB",
            school = "Shree Swaminarayan Gurukul, Rajkot",
            subjects = listOf(
                SubjectScore("Gujarati First Language", 100, 94),
                SubjectScore("English Second Language", 100, 96), // Highest English Topper
                SubjectScore("Mathematics", 100, 98),
                SubjectScore("Science & Technology", 100, 97), // Highest Science Topper
                SubjectScore("Social Science", 100, 93),
                SubjectScore("Sanskrit", 100, 97)
            ),
            totalMaximumMarks = 600,
            totalObtainedMarks = 575,
            percentage = 95.83,
            result = "DISTINCTION (Rank 1 Candidate)",
            verificationStatus = VerificationState.VERIFIED,
            verificationRemarks = "All original marksheets physically cross-verified with GSEB QR code."
        )

        val rec2 = AcademicRecordItem(
            id = "rec_02",
            societyId = s1.id,
            studentId = stu2.id,
            studentName = stu2.fullName,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            board = "GSEB",
            school = "Modi Vidya Niketan, Rajkot",
            subjects = listOf(
                SubjectScore("Gujarati First Language", 100, 88),
                SubjectScore("English Second Language", 100, 89),
                SubjectScore("Mathematics", 100, 92),
                SubjectScore("Science & Technology", 100, 90),
                SubjectScore("Social Science", 100, 86),
                SubjectScore("Sanskrit", 100, 91)
            ),
            totalMaximumMarks = 600,
            totalObtainedMarks = 536,
            percentage = 89.33,
            result = "FIRST CLASS WITH DISTINCTION",
            verificationStatus = VerificationState.PENDING,
            verificationRemarks = "Uploaded marksheet under committee review."
        )
        _academicRecords.value = listOf(rec1, rec2)

        // Seed Stacked Awards for Aarav Patel (Demonstrating MULTIPLE AWARDS STACKING)
        // 1. Overall First Rank: ₹5,000
        // 2. Highest English: ₹2,000
        // 3. Highest Science: ₹2,000
        // 4. Best Overall Result: ₹3,000
        // 5. Donor Memorial Special Award: ₹2,000
        // 6. Passing Encouragement Award: ₹500
        // Consolidated Approved Total: ₹14,500
        val aw1 = AwardGrant(
            id = "aw_01",
            societyId = s1.id,
            studentId = stu1.id,
            studentName = stu1.fullName,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            categoryId = catRank.id,
            ruleId = ruleRank1.id,
            awardTitle = "Overall First Rank Award",
            awardTitleGujarati = "સમગ્ર બોર્ડ પ્રથમ ક્રમાંક સુવર્ણ સન્માન",
            eligibilityEvidence = "Secured Rank 1 in SSC with 95.83% (575/600 marks).",
            amount = 5000,
            approvalStatus = AwardApprovalState.APPROVED,
            paymentStatus = PaymentState.PAID,
            paymentMode = PaymentChannel.BANK_TRANSFER,
            transactionRef = "NEFT-BOB-984210",
            receiptNumber = "RCPT-SKP-2024-001",
            memorialFundName = "Late Shri Harilal Patel Memorial Fund"
        )

        val aw2 = AwardGrant(
            id = "aw_02",
            societyId = s1.id,
            studentId = stu1.id,
            studentName = stu1.fullName,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            categoryId = catSubject.id,
            ruleId = ruleEnglish.id,
            awardTitle = "Highest English Marks Award",
            awardTitleGujarati = "અંગ્રેજી વિષયમાં સર્વોચ્ચ ગુણ પુરસ્કાર",
            eligibilityEvidence = "Highest marks in English (96/100) across all SSC candidates.",
            amount = 2000,
            approvalStatus = AwardApprovalState.APPROVED,
            paymentStatus = PaymentState.PAID,
            paymentMode = PaymentChannel.BANK_TRANSFER,
            transactionRef = "NEFT-BOB-984211",
            receiptNumber = "RCPT-SKP-2024-002"
        )

        val aw3 = AwardGrant(
            id = "aw_03",
            societyId = s1.id,
            studentId = stu1.id,
            studentName = stu1.fullName,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            categoryId = catSubject.id,
            ruleId = ruleScience.id,
            awardTitle = "Highest Science Marks Award",
            awardTitleGujarati = "વિજ્ઞાન અને ટેકનોલોજી સર્વોચ્ચ ગુણ પુરસ્કાર",
            eligibilityEvidence = "Highest marks in Science (97/100) in SSC examination.",
            amount = 2000,
            approvalStatus = AwardApprovalState.APPROVED,
            paymentStatus = PaymentState.APPROVED,
            paymentMode = PaymentChannel.BANK_TRANSFER
        )

        val aw4 = AwardGrant(
            id = "aw_04",
            societyId = s1.id,
            studentId = stu1.id,
            studentName = stu1.fullName,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            categoryId = catRank.id,
            ruleId = ruleBestResult.id,
            awardTitle = "Best Overall Result Award",
            awardTitleGujarati = "શ્રેષ્ઠ પરિણામ ગૌરવ એવોર્ડ",
            eligibilityEvidence = "Secured 95.83%, meeting best overall result threshold.",
            amount = 3000,
            approvalStatus = AwardApprovalState.APPROVED,
            paymentStatus = PaymentState.PENDING
        )

        val aw5 = AwardGrant(
            id = "aw_05",
            societyId = s1.id,
            studentId = stu1.id,
            studentName = stu1.fullName,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            categoryId = catMemorial.id,
            ruleId = ruleMemorialSpecial.id,
            awardTitle = "Donor Special Memorial Award",
            awardTitleGujarati = "દાતાશ્રી હરિલાલ સ્મૃતિ વિશેષ એવોર્ડ",
            eligibilityEvidence = "Special excellence award funded from Memorial Fund FD Interest.",
            amount = 2000,
            approvalStatus = AwardApprovalState.APPROVED,
            paymentStatus = PaymentState.PENDING,
            donorName = donor1.name,
            memorialFundName = memFund1.fundName
        )

        val aw6 = AwardGrant(
            id = "aw_06",
            societyId = s1.id,
            studentId = stu1.id,
            studentName = stu1.fullName,
            academicYear = "2024-2025",
            examinationCategory = ExamStream.SSC,
            categoryId = catPassing.id,
            ruleId = rulePassing.id,
            awardTitle = "Passing Merit Encouragement Award",
            awardTitleGujarati = "બોર્ડ પરીક્ષા સફળતા પ્રોત્સાહન પુરસ્કાર",
            eligibilityEvidence = "Passed SSC with greater than 75% threshold.",
            amount = 500,
            approvalStatus = AwardApprovalState.APPROVED,
            paymentStatus = PaymentState.PAID,
            paymentMode = PaymentChannel.CASH,
            receiptNumber = "RCPT-SKP-2024-006"
        )

        _awards.value = listOf(aw1, aw2, aw3, aw4, aw5, aw6)

        // Seed Financial Transactions & Payments
        val tx1 = FinancialTransactionItem(
            id = "tx_01",
            societyId = s1.id,
            fundType = "FD_ACTUAL_INTEREST",
            transactionType = "FD_ACTUAL_INTEREST_CREDIT",
            amount = 36250L,
            referenceNumber = "TX-BOB-INT-9981",
            narrative = "Annual FD interest received for Late Shri Harilal Patel Memorial Fund",
            isReversed = false,
            date = "15 Jun 2024"
        )
        _transactions.value = listOf(tx1)

        val p1 = PaymentRecordItem(
            id = "pay_01",
            societyId = s1.id,
            awardId = aw1.id,
            studentName = stu1.fullName,
            awardTitle = aw1.awardTitle,
            amount = aw1.amount,
            paymentMode = PaymentChannel.BANK_TRANSFER,
            status = PaymentState.PAID,
            receiptNumber = aw1.receiptNumber ?: "RCPT-001",
            bankRef = "NEFT-BOB-984210",
            date = "20 Jul 2024"
        )
        val p2 = PaymentRecordItem(
            id = "pay_02",
            societyId = s1.id,
            awardId = aw2.id,
            studentName = stu1.fullName,
            awardTitle = aw2.awardTitle,
            amount = aw2.amount,
            paymentMode = PaymentChannel.BANK_TRANSFER,
            status = PaymentState.PAID,
            receiptNumber = aw2.receiptNumber ?: "RCPT-002",
            bankRef = "NEFT-BOB-984211",
            date = "20 Jul 2024"
        )
        val p6 = PaymentRecordItem(
            id = "pay_06",
            societyId = s1.id,
            awardId = aw6.id,
            studentName = stu1.fullName,
            awardTitle = aw6.awardTitle,
            amount = aw6.amount,
            paymentMode = PaymentChannel.CASH,
            status = PaymentState.PAID,
            receiptNumber = aw6.receiptNumber ?: "RCPT-006",
            bankRef = "CASH-ON-STAGE",
            date = "22 Jul 2024"
        )
        _payments.value = listOf(p1, p2, p6)

        // Audit Logs
        val log1 = AuditLogRecord(
            id = "log_01",
            societyId = s1.id,
            action = "AWARD_ENGINE_EXECUTION",
            performedBy = "Society Admin (admin@samaj.org)",
            details = "Executed award engine cycle for Academic Year 2024-2025. 6 stacked awards generated.",
            timestamp = "10 Jul 2024, 11:30 AM"
        )
        val log2 = AuditLogRecord(
            id = "log_02",
            societyId = s1.id,
            action = "COMMITTEE_APPROVAL",
            performedBy = "Award Committee Chair",
            details = "Approved 6 stacked awards for Aarav Pravin Patel. Total consolidated grant: ₹14,500.",
            timestamp = "12 Jul 2024, 03:15 PM"
        )
        _auditLogs.value = listOf(log1, log2)
    }

    // ======================== STUDENT ACTIONS ========================

    fun registerStudent(
        fullName: String,
        fullNameGujarati: String,
        guardianName: String,
        guardianMobile: String,
        schoolName: String,
        standard: String
    ): StudentProfile {
        val currentSociety = _selectedSocietyId.value
        val code = "STU-${(1000..9999).random()}"
        val student = StudentProfile(
            societyId = currentSociety,
            fullName = fullName,
            fullNameGujarati = fullNameGujarati,
            guardianName = guardianName,
            guardianMobile = guardianMobile,
            guardianConsent = true,
            schoolName = schoolName,
            standard = standard,
            registrationCode = code
        )
        _students.value = _students.value + student
        logAudit("STUDENT_REGISTRATION", "Registered student $fullName [Code: $code] with guardian consent.")
        return student
    }

    fun submitAcademicRecord(
        studentId: String,
        studentName: String,
        academicYear: String,
        examinationCategory: ExamStream,
        school: String,
        subjects: List<SubjectScore>,
        marksheetUri: String? = null
    ): AcademicRecordItem {
        val currentSociety = _selectedSocietyId.value
        val totalMax = subjects.sumOf { it.maximumMarks }
        val totalObt = subjects.sumOf { it.obtainedMarks }
        val percentage = if (totalMax > 0) Math.round((totalObt.toDouble() / totalMax.toDouble() * 10000.0)) / 100.0 else 0.0

        val result = when {
            percentage >= 70.0 -> "DISTINCTION"
            percentage >= 60.0 -> "FIRST_CLASS"
            percentage >= 50.0 -> "SECOND_CLASS"
            else -> "PASS"
        }

        val record = AcademicRecordItem(
            societyId = currentSociety,
            studentId = studentId,
            studentName = studentName,
            academicYear = academicYear,
            examinationCategory = examinationCategory,
            school = school,
            subjects = subjects,
            totalMaximumMarks = totalMax,
            totalObtainedMarks = totalObt,
            percentage = percentage,
            result = result,
            verificationStatus = VerificationState.PENDING,
            verificationRemarks = "Pending verification by Committee",
            marksheetDocumentUri = marksheetUri
        )

        _academicRecords.value = _academicRecords.value + record
        logAudit("ACADEMIC_RECORD_SUBMITTED", "Submitted marksheet for $studentName in $examinationCategory ($percentage%).")
        return record
    }

    // ======================== VERIFICATION WORKFLOW ========================

    fun verifyAcademicRecord(
        recordId: String,
        isApproved: Boolean,
        remarks: String,
        correctedSubjects: List<SubjectScore>? = null
    ) {
        _academicRecords.value = _academicRecords.value.map { rec ->
            if (rec.id == recordId) {
                val updatedSubjects = correctedSubjects ?: rec.subjects
                val totalMax = updatedSubjects.sumOf { it.maximumMarks }
                val totalObt = updatedSubjects.sumOf { it.obtainedMarks }
                val percentage = if (totalMax > 0) Math.round((totalObt.toDouble() / totalMax.toDouble() * 10000.0)) / 100.0 else 0.0

                rec.copy(
                    subjects = updatedSubjects,
                    totalMaximumMarks = totalMax,
                    totalObtainedMarks = totalObt,
                    percentage = percentage,
                    verificationStatus = if (isApproved) VerificationState.VERIFIED else VerificationState.REJECTED,
                    verificationRemarks = remarks
                )
            } else {
                rec
            }
        }
        logAudit(
            "MARKSHEET_VERIFICATION",
            "Human verification decision for record $recordId: ${if (isApproved) "VERIFIED" else "REJECTED"}."
        )
    }

    // ======================== MULTIPLE AWARD ENGINE ========================

    /**
     * Executes the multiple award evaluation cycle.
     * Core Rule: A student can receive multiple awards in the same academic year across different rules.
     * Prevents duplicates only within the same configured rule.
     */
    fun runAwardEngine(academicYear: String): Int {
        val currentSociety = _selectedSocietyId.value
        val rules = _awardRules.value.filter { it.societyId == currentSociety && it.academicYear == academicYear }
        val verifiedRecords = _academicRecords.value.filter {
            it.societyId == currentSociety && it.academicYear == academicYear && it.verificationStatus == VerificationState.VERIFIED
        }

        var newAwardsGenerated = 0
        val existingAwards = _awards.value.toMutableList()

        for (rule in rules) {
            val streamRecords = verifiedRecords.filter { it.examinationCategory == rule.examinationCategory }
            if (streamRecords.isEmpty()) continue

            // 1. Overall Rank Evaluation
            if (rule.rankTarget != null && rule.rankTarget > 0) {
                val sorted = streamRecords.sortedByDescending { it.percentage }
                val qualified = sorted.filter {
                    rule.minimumPercentageThreshold == null || it.percentage >= rule.minimumPercentageThreshold
                }

                if (qualified.isNotEmpty() && qualified.size >= rule.rankTarget) {
                    val winner = qualified[rule.rankTarget - 1]
                    // Check if already awarded under this specific rule
                    val exists = existingAwards.any {
                        it.societyId == currentSociety && it.studentId == winner.studentId && it.ruleId == rule.id && it.academicYear == academicYear
                    }
                    if (!exists) {
                        val evidence = "Secured Rank ${rule.rankTarget} in ${rule.examinationCategory.titleEn} with ${winner.percentage}% (${winner.totalObtainedMarks}/${winner.totalMaximumMarks} marks)."
                        val grant = AwardGrant(
                            societyId = currentSociety,
                            studentId = winner.studentId,
                            studentName = winner.studentName,
                            academicYear = academicYear,
                            examinationCategory = rule.examinationCategory,
                            categoryId = rule.categoryId,
                            ruleId = rule.id,
                            awardTitle = rule.title,
                            awardTitleGujarati = rule.titleGujarati,
                            eligibilityEvidence = evidence,
                            amount = rule.cashAmount,
                            approvalStatus = if (rule.requiresCommitteeApproval) AwardApprovalState.PENDING_COMMITTEE else AwardApprovalState.APPROVED,
                            paymentStatus = PaymentState.PENDING
                        )
                        existingAwards.add(grant)
                        newAwardsGenerated++
                    }
                }
            }

            // 2. Subject Specific Topper Evaluation (e.g. English, Science)
            if (rule.subjectTarget != null) {
                val targetSubj = rule.subjectTarget.trim().lowercase()
                val candidateList = streamRecords.mapNotNull { rec ->
                    val subj = rec.subjects.find { it.subjectName.lowercase().contains(targetSubj) }
                    if (subj != null) Triple(rec, subj.obtainedMarks, subj.maximumMarks) else null
                }.sortedByDescending { it.second }

                if (candidateList.isNotEmpty()) {
                    val topCandidate = candidateList.first()
                    val winner = topCandidate.first
                    val exists = existingAwards.any {
                        it.societyId == currentSociety && it.studentId == winner.studentId && it.ruleId == rule.id && it.academicYear == academicYear
                    }
                    if (!exists) {
                        val evidence = "Highest marks in ${rule.subjectTarget} (${topCandidate.second}/${topCandidate.third}) in ${rule.examinationCategory.titleEn}."
                        val grant = AwardGrant(
                            societyId = currentSociety,
                            studentId = winner.studentId,
                            studentName = winner.studentName,
                            academicYear = academicYear,
                            examinationCategory = rule.examinationCategory,
                            categoryId = rule.categoryId,
                            ruleId = rule.id,
                            awardTitle = rule.title,
                            awardTitleGujarati = rule.titleGujarati,
                            eligibilityEvidence = evidence,
                            amount = rule.cashAmount,
                            approvalStatus = if (rule.requiresCommitteeApproval) AwardApprovalState.PENDING_COMMITTEE else AwardApprovalState.APPROVED,
                            paymentStatus = PaymentState.PENDING
                        )
                        existingAwards.add(grant)
                        newAwardsGenerated++
                    }
                }
            }

            // 3. Passing / Merit Threshold Award
            if (rule.rankTarget == null && rule.subjectTarget == null && rule.minimumPercentageThreshold != null) {
                val eligible = streamRecords.filter { it.percentage >= rule.minimumPercentageThreshold }
                for (rec in eligible) {
                    val exists = existingAwards.any {
                        it.societyId == currentSociety && it.studentId == rec.studentId && it.ruleId == rule.id && it.academicYear == academicYear
                    }
                    if (!exists) {
                        val evidence = "Met merit/passing threshold with ${rec.percentage}% in ${rule.examinationCategory.titleEn}."
                        val grant = AwardGrant(
                            societyId = currentSociety,
                            studentId = rec.studentId,
                            studentName = rec.studentName,
                            academicYear = academicYear,
                            examinationCategory = rule.examinationCategory,
                            categoryId = rule.categoryId,
                            ruleId = rule.id,
                            awardTitle = rule.title,
                            awardTitleGujarati = rule.titleGujarati,
                            eligibilityEvidence = evidence,
                            amount = rule.cashAmount,
                            approvalStatus = if (rule.requiresCommitteeApproval) AwardApprovalState.PENDING_COMMITTEE else AwardApprovalState.APPROVED,
                            paymentStatus = PaymentState.PENDING
                        )
                        existingAwards.add(grant)
                        newAwardsGenerated++
                    }
                }
            }
        }

        _awards.value = existingAwards
        logAudit("AWARD_ENGINE_RUN", "Evaluated academic cycle $academicYear. Generated $newAwardsGenerated awards.")
        return newAwardsGenerated
    }

    fun updateAwardApproval(awardId: String, isApproved: Boolean, notes: String) {
        _awards.value = _awards.value.map { aw ->
            if (aw.id == awardId) {
                aw.copy(
                    approvalStatus = if (isApproved) AwardApprovalState.APPROVED else AwardApprovalState.REJECTED,
                    committeeNotes = notes
                )
            } else {
                aw
            }
        }
        logAudit("AWARD_APPROVAL_DECISION", "Award $awardId status set to ${if (isApproved) "APPROVED" else "REJECTED"}.")
    }

    /**
     * Calculates consolidated approved award amount for student.
     * Excludes unapproved, pending, or rejected awards.
     */
    fun getConsolidatedApprovedAmount(studentId: String, academicYear: String): Int {
        val currentSociety = _selectedSocietyId.value
        return _awards.value
            .filter { it.societyId == currentSociety && it.studentId == studentId && it.academicYear == academicYear && it.approvalStatus == AwardApprovalState.APPROVED }
            .sumOf { it.amount }
    }

    // ======================== TREASURY & FINANCIAL MANAGEMENT ========================

    /**
     * Records actual bank interest received.
     * RULE: Estimated interest is NEVER counted. Only actual bank credit increases balance.
     */
    fun recordActualFdInterest(
        memorialFundId: String,
        actualInterestAmount: Long,
        bankRef: String,
        narrative: String
    ): Boolean {
        if (actualInterestAmount <= 0) return false
        val currentSociety = _selectedSocietyId.value

        _memorialFunds.value = _memorialFunds.value.map { fund ->
            if (fund.id == memorialFundId && fund.societyId == currentSociety) {
                fund.copy(
                    actualInterestReceivedTotal = fund.actualInterestReceivedTotal + actualInterestAmount,
                    availableBalance = fund.availableBalance + actualInterestAmount
                )
            } else {
                fund
            }
        }

        val tx = FinancialTransactionItem(
            societyId = currentSociety,
            fundType = "FD_ACTUAL_INTEREST",
            transactionType = "FD_ACTUAL_INTEREST_CREDIT",
            amount = actualInterestAmount,
            referenceNumber = "INT-${System.currentTimeMillis().toString().takeLast(6)}",
            narrative = "$narrative (Ref: $bankRef)",
            date = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        )
        _transactions.value = listOf(tx) + _transactions.value
        logAudit("FD_INTEREST_CREDIT", "Credited actual bank interest ₹$actualInterestAmount. Ref: $bankRef")
        return true
    }

    /**
     * Disburses award payment with receipt generation.
     * Prevents duplicate payments.
     */
    fun disburseAwardPayment(
        awardId: String,
        paymentMode: PaymentChannel,
        bankRef: String,
        notes: String
    ): PaymentRecordItem? {
        val currentSociety = _selectedSocietyId.value
        val award = _awards.value.find { it.id == awardId && it.societyId == currentSociety } ?: return null

        if (award.paymentStatus == PaymentState.PAID) {
            return null // Prevent duplicate payments!
        }
        if (award.approvalStatus != AwardApprovalState.APPROVED) {
            return null // Must be approved by committee!
        }

        val receiptNumber = "RCPT-${award.societyId.take(4).uppercase()}-${System.currentTimeMillis().toString().takeLast(6)}"
        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())

        val payment = PaymentRecordItem(
            societyId = currentSociety,
            awardId = awardId,
            studentName = award.studentName,
            awardTitle = award.awardTitle,
            amount = award.amount,
            paymentMode = paymentMode,
            status = PaymentState.PAID,
            receiptNumber = receiptNumber,
            bankRef = bankRef.ifEmpty { "STAGE-CASH" },
            date = dateStr
        )

        // Update award payment status
        _awards.value = _awards.value.map { aw ->
            if (aw.id == awardId) {
                aw.copy(
                    paymentStatus = PaymentState.PAID,
                    paymentMode = paymentMode,
                    transactionRef = bankRef,
                    receiptNumber = receiptNumber
                )
            } else {
                aw
            }
        }

        _payments.value = listOf(payment) + _payments.value

        val tx = FinancialTransactionItem(
            societyId = currentSociety,
            fundType = "SOCIETY_GENERAL",
            transactionType = "AWARD_DISBURSEMENT",
            amount = -award.amount.toLong(),
            referenceNumber = receiptNumber,
            narrative = "Disbursed payment for ${award.awardTitle} to ${award.studentName} via $paymentMode",
            date = dateStr
        )
        _transactions.value = listOf(tx) + _transactions.value
        logAudit("AWARD_PAYMENT_DISBURSED", "Disbursed ₹${award.amount} to ${award.studentName}. Receipt: $receiptNumber")
        return payment
    }

    /**
     * Reverses a mistaken financial transaction while preserving immutable history.
     */
    fun reverseTransaction(transactionId: String, reason: String): Boolean {
        val currentSociety = _selectedSocietyId.value
        val originalTx = _transactions.value.find { it.id == transactionId && it.societyId == currentSociety } ?: return false
        if (originalTx.isReversed) return false

        val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
        val reversalTx = FinancialTransactionItem(
            societyId = currentSociety,
            fundType = originalTx.fundType,
            transactionType = "REVERSAL",
            amount = -originalTx.amount,
            referenceNumber = "REV-${originalTx.referenceNumber}",
            narrative = "Reversal of ${originalTx.referenceNumber}. Reason: $reason",
            isReversed = false,
            date = dateStr
        )

        _transactions.value = _transactions.value.map { tx ->
            if (tx.id == transactionId) tx.copy(isReversed = true) else tx
        } + reversalTx

        logAudit("TRANSACTION_REVERSAL", "Reversed ${originalTx.referenceNumber}. Reason: $reason")
        return true
    }

    // ======================== BILINGUAL REPORTS EXPORT ========================

    fun generateAnnualAwardRegisterCsv(academicYear: String, language: AppLanguage): String {
        val currentSociety = _selectedSocietyId.value
        val awardsList = _awards.value.filter { it.societyId == currentSociety && it.academicYear == academicYear }

        val isGu = language == AppLanguage.GUJARATI
        val headers = if (isGu) {
            "ક્રમ,વિદ્યાર્થીનું નામ,પરીક્ષા પ્રવાહ,પુરસ્કાર શીર્ષક,રકમ (રૂ.),મંજૂરી સ્થિતિ,ચુકવણી સ્થિતિ,પાત્રતા પુરાવો"
        } else {
            "Sr No,Student Name,Examination,Award Title,Amount (INR),Approval Status,Payment Status,Evidence"
        }

        val rows = awardsList.mapIndexed { idx, aw ->
            val title = if (isGu) aw.awardTitleGujarati else aw.awardTitle
            "${idx + 1},\"${aw.studentName}\",\"${aw.examinationCategory.titleEn}\",\"$title\",${aw.amount},${aw.approvalStatus},${aw.paymentStatus},\"${aw.eligibilityEvidence.replace("\"", "'")}\""
        }

        return (listOf(headers) + rows).joinToString("\n")
    }

    fun generateFdInterestReportCsv(language: AppLanguage): String {
        val currentSociety = _selectedSocietyId.value
        val funds = _memorialFunds.value.filter { it.societyId == currentSociety }

        val isGu = language == AppLanguage.GUJARATI
        val headers = if (isGu) {
            "ક્રમ,સ્મૃતિ ફંડ નામ,સ્મૃતિ અર્પણ,બેંક અને FD વિગત,FD મુદ્દલ (રૂ.),વાસ્તવિક પ્રાપ્ત વ્યાજ (રૂ.),ફાળવેલ રકમ (રૂ.),ઉપલબ્ધ સિલક (રૂ.)"
        } else {
            "Sr No,Memorial Fund Name,In Memory Of,Bank & FD No,FD Principal (INR),Actual Interest Received (INR),Allocated (INR),Available Balance (INR)"
        }

        val rows = funds.mapIndexed { idx, f ->
            val name = if (isGu) f.fundNameGujarati else f.fundName
            "${idx + 1},\"$name\",\"${f.inMemoryOf}\",\"${f.bankName} - ${f.fdCertificateNumber}\",${f.fdPrincipalAmount},${f.actualInterestReceivedTotal},${f.allocatedAmount},${f.availableBalance}"
        }

        return (listOf(headers) + rows).joinToString("\n")
    }

    private fun logAudit(action: String, details: String) {
        val currentSociety = _selectedSocietyId.value
        val dateStr = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date())
        val log = AuditLogRecord(
            societyId = currentSociety,
            action = action,
            performedBy = _currentRole.value.displayName,
            details = details,
            timestamp = dateStr
        )
        _auditLogs.value = listOf(log) + _auditLogs.value
    }

    fun updateSocietySpreadsheetId(societyId: String, newSpreadsheetId: String) {
        val updated = _societies.value.map { soc ->
            if (soc.id == societyId) soc.copy(spreadsheetId = newSpreadsheetId) else soc
        }
        _societies.value = updated
        logAudit("SPREADSHEET_ID_CONFIGURED", "Updated Google Spreadsheet ID for society: $societyId to '$newSpreadsheetId'")
    }

    // ======================== GOOGLE SHEETS CLOUD DATABASE SYNC ========================

    fun syncWithGoogleSheets(spreadsheetId: String = "", targetSocietyId: String? = null): SheetSyncResult {
        val activeSocId = targetSocietyId ?: _selectedSocietyId.value
        val currentSoc = _societies.value.find { it.id == activeSocId }
        val effectiveSpreadsheetId = spreadsheetId.ifEmpty { currentSoc?.spreadsheetId ?: "" }

        val result = GoogleSheetSyncEngine.performSync(
            spreadsheetId = effectiveSpreadsheetId,
            targetSocietyId = activeSocId,
            societies = _societies.value,
            students = _students.value,
            records = _academicRecords.value,
            awards = _awards.value,
            funds = _memorialFunds.value,
            payments = _payments.value
        )
        _sheetSyncResult.value = result
        logAudit("GOOGLE_SHEET_SYNC", "Synced ${result.totalRecordsSynced} records for ${currentSoc?.name ?: activeSocId} to Google Sheet: ${result.spreadsheetId}")
        return result
    }

    fun getGoogleSheetCsvForTab(tabName: String, targetSocietyId: String? = null): String {
        val activeSocId = targetSocietyId ?: _selectedSocietyId.value
        return GoogleSheetSyncEngine.generateGoogleSheetsTabCsv(
            tabName = tabName,
            targetSocietyId = activeSocId,
            societies = _societies.value,
            students = _students.value,
            records = _academicRecords.value,
            awards = _awards.value,
            funds = _memorialFunds.value,
            payments = _payments.value
        )
    }

    fun getAppsScriptWebhookCode(): String {
        return GoogleSheetSyncEngine.generateAppsScriptSnippet()
    }
}
