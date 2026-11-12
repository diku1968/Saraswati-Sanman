package com.example.domain

enum class AppLanguage(val code: String, val displayName: String, val nativeName: String) {
    ENGLISH("en", "English", "English"),
    GUJARATI("gu", "Gujarati", "ગુજરાતી")
}

object AppStrings {
    private val translations: Map<String, Pair<String, String>> = mapOf(
        // App Branding & Navigation
        "app_title" to Pair("Saraswati Samman", "સરસ્વતી સન્માન"),
        "app_subtitle" to Pair("Multi-Samaj Education Award Platform", "બહુ-સમાજ શિક્ષણ પુરસ્કાર પ્રણાલી"),
        "select_society" to Pair("Active Samaj / Society", "સક્રિય સમાજ / મંડળ"),
        "switch_role" to Pair("Active Role", "સક્રિય હોદ્દો / ભૂમિકા"),
        "language_toggle" to Pair("Language", "ભાષા"),

        // Roles
        "role_student" to Pair("Student", "વિદ્યાર્થી"),
        "role_society_admin" to Pair("Society Admin", "સમાજ સંચાલક"),
        "role_committee" to Pair("Committee Member", "પુરસ્કાર સમિતિ સભ્ય"),
        "role_treasurer" to Pair("Treasurer", "ખજાનચી / નાણાં મંત્રી"),
        "role_donor" to Pair("Donor", "દાતાશ્રી"),
        "role_super_admin" to Pair("Super Admin", "મુખ્ય વ્યવસ્થાપક"),

        // Navigation Tabs
        "nav_dashboard" to Pair("Dashboard", "ડેશબોર્ડ"),
        "nav_students" to Pair("Students", "વિદ્યાર્થીઓ"),
        "nav_marksheets" to Pair("Marksheets & OCR", "ગુણપત્રક ચકાસણી"),
        "nav_awards" to Pair("Awards Engine", "પુરસ્કાર એન્જિન"),
        "nav_finance" to Pair("Treasury & FDs", "તિજોરી અને FD"),
        "nav_reports" to Pair("Reports", "અહેવાલો"),
        "nav_profile" to Pair("Profile", "પ્રોફાઇલ"),

        // Common Actions
        "action_save" to Pair("Save", "સાચવો"),
        "action_submit" to Pair("Submit Application", "અરજી જમા કરો"),
        "action_approve" to Pair("Approve", "મંજૂર કરો"),
        "action_reject" to Pair("Reject", "નામંજૂર કરો"),
        "action_verify" to Pair("Verify Marksheet", "ગુણપત્રક પ્રમાણિત કરો"),
        "action_upload" to Pair("Upload Marksheet", "માર્કશીટ અપલોડ કરો"),
        "action_run_engine" to Pair("Run Award Engine", "પુરસ્કાર ગણતરી શરૂ કરો"),
        "action_export_csv" to Pair("Export CSV", "CSV ડાઉનલોડ"),
        "action_disburse" to Pair("Disburse Payment", "પુરસ્કાર ચુકવણી કરો"),
        "action_reverse" to Pair("Reverse Entry", "ભૂલ સુધારો / રદબતલ"),
        "action_refresh" to Pair("Refresh", "તાજું કરો"),

        // Exam Categories
        "exam_ssc" to Pair("SSC (Std 10)", "એસ.એસ.સી (ધોરણ ૧૦)"),
        "exam_hsc_science" to Pair("HSC Science (Std 12)", "એચ.એસ.સી વિજ્ઞાન પ્રવાહ"),
        "exam_hsc_commerce" to Pair("HSC Commerce (Std 12)", "એચ.એસ.સી વાણિજ્ય પ્રવાહ"),
        "exam_hsc_arts" to Pair("HSC Arts (Std 12)", "એચ.એસ.સી વિનયન પ્રવાહ"),
        "exam_other" to Pair("Higher / Degree Exam", "ઉચ્ચ અભ્યાસ / ડિગ્રી"),

        // Statuses
        "status_pending" to Pair("Pending Review", "સમીક્ષા બાકી"),
        "status_under_review" to Pair("Under Review", "ચકાસણી હેઠળ"),
        "status_verified" to Pair("Verified & Approved", "પ્રમાણિત અને મંજૂર"),
        "status_rejected" to Pair("Rejected", "અસ્વીકૃત"),
        "status_paid" to Pair("Paid", "ચૂકવાઈ ગયું"),
        "status_disbursed" to Pair("Disbursed", "જમા થયું"),

        // Award & Financial Terms
        "multi_award_eligible" to Pair("Eligible for Multiple Awards", "એકથી વધુ પુરસ્કાર માટે પાત્ર"),
        "consolidated_amount" to Pair("Consolidated Approved Award Amount", "કુલ મંજૂર પુરસ્કાર રકમ"),
        "individual_awards" to Pair("Individual Stacked Awards", "વ્યક્તિગત પુરસ્કારોની યાદી"),
        "award_evidence" to Pair("Eligibility Evidence", "પાત્રતાનો પુરાવો"),
        "fd_principal" to Pair("FD Principal", "FD મુદ્દલ રકમ"),
        "fd_actual_interest" to Pair("Actual Bank Interest Received", "વાસ્તવિક પ્રાપ્ત બેંક વ્યાજ"),
        "fd_interest_rule" to Pair("Only bank-supported interest is counted (no estimates)", "માત્ર બેંકમાં જમા થયેલ વ્યાજ જ ગણાય છે"),
        "memorial_fund" to Pair("Memorial Fund (In Memory of)", "સ્મૃતિ ફંડ (સ્મરણાર્થે)"),
        "society_general_fund" to Pair("Samaj General Fund", "સમાજ સામાન્ય ભંડોળ"),
        "available_balance" to Pair("Available Balance", "ઉપલબ્ધ સિલક"),
        "guardian_consent" to Pair("Guardian Consent & Declaration", "વાલીની સંમતિ અને ઘોષણા"),
        "ocr_gujarati_notice" to Pair(
            "Notice: Google ML Kit does not officially support Gujarati script. Manual marks entry and verification is provided for Gujarati documents.",
            "નોંધ: ગૂગલ ML Kit ગુજરાતી લિપિને સત્તાવાર રીતે ટેકો આપતું નથી. ગુજરાતી માર્કશીટ માટે મેન્યુઅલ ગુણ ચકાસણી ફરજિયાત છે."
        ),
        "guardian_consent_desc" to Pair(
            "I hereby certify that my ward's academic records and documents are authentic for Saraswati Samman awards.",
            "હું પ્રમાણિત કરું છું કે સરસ્વતી સન્માન પુરસ્કાર માટે મારા પાલ્યની વિગતો અને ગુણપત્રક સાચા છે."
        )
    )

    fun get(key: String, language: AppLanguage): String {
        val entry = translations[key] ?: return key
        return if (language == AppLanguage.GUJARATI) entry.second else entry.first
    }
}
