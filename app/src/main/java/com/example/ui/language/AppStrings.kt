package com.example.ui.language

object AppStrings {
    fun appTitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "WiFi Router Manager"
        AppLanguage.BENGALI -> "ওয়াইফাই রাউটার ম্যানেজার"
    }

    fun appSubtitle(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Smart WiFi & Router Control Suite"
        AppLanguage.BENGALI -> "স্মার্ট ওয়াইফাই ও রাউটার কন্ট্রোল"
    }

    fun dashboard(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Dashboard"
        AppLanguage.BENGALI -> "ড্যাশবোর্ড"
    }

    fun routers(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Routers"
        AppLanguage.BENGALI -> "রাউটারসমূহ"
    }

    fun devices(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Devices"
        AppLanguage.BENGALI -> "ডিভাইস"
    }

    fun diagnostics(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Diagnostics"
        AppLanguage.BENGALI -> "ডায়াগনস্টিকস"
    }

    fun adminWeb(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Admin Web"
        AppLanguage.BENGALI -> "অ্যাডমিন ওয়েব"
    }

    fun language(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Language"
        AppLanguage.BENGALI -> "ভাষা"
    }

    fun selectLanguage(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Select Language"
        AppLanguage.BENGALI -> "ভাষা নির্বাচন করুন"
    }

    fun cancel(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Cancel"
        AppLanguage.BENGALI -> "বাতিল"
    }

    fun continueToApp(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Continue to App"
        AppLanguage.BENGALI -> "অ্যাপে প্রবেশ করুন"
    }

    fun joinFacebook(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Join Facebook"
        AppLanguage.BENGALI -> "ফেসবুক গ্রুপে যুক্ত হোন"
    }

    fun developerSupport(lang: AppLanguage): String = when (lang) {
        AppLanguage.ENGLISH -> "Developer Support"
        AppLanguage.BENGALI -> "ডেভেলপার সাপোর্ট"
    }
}
