package com.ecodala.core.localization

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import com.ecodala.core.domain.model.BiotoiletIssueType
import com.ecodala.core.domain.model.BiotoiletStatus
import com.ecodala.core.domain.model.BiotoiletType
import com.ecodala.core.domain.model.ChallengeStatus
import com.ecodala.core.domain.model.ChallengeType
import com.ecodala.core.domain.model.EcoReportIssueType
import com.ecodala.core.domain.model.EcoReportSeverity
import com.ecodala.core.domain.model.EcoReportStatus
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.domain.model.WaterStationIssueType
import com.ecodala.core.domain.model.WaterStationStatus
import com.ecodala.core.domain.model.WaterStationType

data class EcoStrings(
    val languageTag: String,
    val home: String,
    val map: String,
    val submit: String,
    val leads: String,
    val profile: String,
    val login: String,
    val splashTagline: String,
    val splashExperience: String,
    val registerNow: String,
    val createAccount: String,
    val forgotPassword: String,
    val welcomeBack: String,
    val signInSubtitle: String,
    val emailAddress: String,
    val password: String,
    val continueWithGoogle: String,
    val or: String,
    val noAccountPrefix: String,
    val fullName: String,
    val joinCommunity: String,
    val confirmPassword: String,
    val fairlyStrongPassword: String,
    val termsPrefix: String,
    val terms: String,
    val termsAnd: String,
    val privacyPolicy: String,
    val orSignUpWith: String,
    val alreadyHaveAccount: String,
    val checkYourEmail: String,
    val forgotPasswordSubtitle: String,
    val resetInstructions: (String) -> String,
    val rememberedPassword: String,
    val sendResetLink: String,
    val resetLinkSent: String,
    val invalidEmail: String,
    val hello: (String) -> String,
    val quickActions: String,
    val currentEcoRating: String,
    val points: (Int) -> String,
    val pointsLabel: String,
    val level: (Int) -> String,
    val globalRank: (Int) -> String,
    val virtualTreeTitle: String,
    val progressToLevel: (Int, Int) -> String,
    val recentAchievements: String,
    val searchLocation: String,
    val openUntil: String,
    val distanceAway: (Double, Int) -> String,
    val acceptedItems: String,
    val reward: String,
    val acceptedWasteTypes: String,
    val open: String,
    val buildRoute: String,
    val call: String,
    val share: String,
    val sustainableFact: String,
    val submitWaste: String,
    val wasteType: String,
    val quantity: String,
    val photoConfirmation: String,
    val takePhotoUpload: String,
    val commentOptional: String,
    val addNote: String,
    val submitWithPoints: (Int) -> String,
    val myEcoTree: String,
    val progressToNextLevel: (Int) -> String,
    val xpStatus: (Int, Int) -> String,
    val xpToGo: (Int) -> String,
    val nextGoal: String,
    val collectXpGoal: (Int, Int) -> String,
    val growthHistory: String,
    val challenges: String,
    val daily: String,
    val weekly: String,
    val special: String,
    val dailyChallenges: String,
    val weeklyChallenges: String,
    val left: (Int) -> String,
    val active: String,
    val completed: String,
    val locked: String,
    val ecoSpecialTitle: String,
    val ecoSpecialSubtitle: String,
    val leaderboard: String,
    val students: String,
    val faculties: String,
    val topFaculty: String,
    val thisWeek: (Int) -> String,
    val total: String,
    val members: String,
    val topContributor: (Int, String) -> String,
    val memberSince: (String) -> String,
    val kgWaste: String,
    val trees: String,
    val sustainabilityScore: String,
    val topContributorText: String,
    val nextMilestone: String,
    val myAchievements: String,
    val recyclingHistory: String,
    val notifications: String,
    val settings: String,
    val support: String,
    val logout: String,
    val all: String,
    val achievements: String,
    val ecoProgress: String,
    val achievementsUnlocked: (Int, Int) -> String,
    val completePercent: (Int) -> String,
    val recentSubmissions: String,
    val yourImpact: String,
    val impactSubtitle: String,
    val recentNotifications: String,
    val unread: (Int) -> String,
    val totalUpdates: (Int) -> String,
    val markAllRead: String,
    val supportHeroTitle: String,
    val supportHeroSubtitle: String,
    val contactUs: String,
    val sendRequest: String,
    val describeIssue: String,
    val requestSaved: String,
    val faq: String,
    val settingsPreferences: String,
    val settingsPushNotifications: String,
    val settingsPushNotificationsSubtitle: String,
    val settingsLocation: String,
    val settingsLocationSubtitle: String,
    val settingsAiTips: String,
    val settingsAiTipsSubtitle: String,
    val settingsDisplayUnits: String,
    val settingsDarkMode: String,
    val settingsDarkModeSubtitle: String,
    val settingsLanguage: String,
    val settingsLanguageSubtitle: String,
    val settingsPreferredUnit: String,
    val settingsPreferredUnitSubtitle: String,
    val settingsPrivacy: String,
    val settingsPublicProfile: String,
    val settingsPublicProfileSubtitle: String,
    val settingsDataSecurity: String,
    val settingsDataSecuritySubtitle: String,
    val settingsInfo: String,
    val wasteTypeName: (WasteType) -> String,
    val challengeTypeName: (ChallengeType) -> String,
    val challengeStatusName: (ChallengeStatus) -> String
)

val LocalEcoStrings = staticCompositionLocalOf { englishEcoStrings }

@Composable
fun EcoLocalization(
    languageTag: String,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalEcoStrings provides ecoStringsFor(languageTag),
        content = content
    )
}

fun ecoStringsFor(languageTag: String): EcoStrings {
    return when (languageTag) {
        "ru" -> russianEcoStrings
        "kk" -> kazakhEcoStrings
        else -> englishEcoStrings
    }
}

private fun EcoStrings.localized(en: String, ru: String, kk: String): String {
    return when (languageTag) {
        "ru" -> ru
        "kk" -> kk
        else -> en
    }
}

val EcoStrings.biotoilets: String
    get() = localized("Biotoilets", "Биотуалеты", "Биотуалеттер")

val EcoStrings.waterStations: String
    get() = localized("Water Stations", "Питьевая вода", "Су станциялары")

val EcoStrings.ecoReports: String
    get() = localized("EcoReports", "Эко-жалобы", "Экошағымдар")

val EcoStrings.freeOnly: String
    get() = localized("Free only", "Только бесплатно", "Тек тегін")

val EcoStrings.free: String
    get() = localized("Free", "Бесплатно", "Тегін")

val EcoStrings.paid: String
    get() = localized("Paid", "Платно", "Ақылы")

val EcoStrings.accessible: String
    get() = localized("Accessible", "Доступно", "Қолжетімді")

val EcoStrings.limited: String
    get() = localized("Limited", "Ограничено", "Шектеулі")

val EcoStrings.family: String
    get() = localized("Family", "Семейный", "Отбасылық")

val EcoStrings.standard: String
    get() = localized("Standard", "Обычный", "Қалыпты")

val EcoStrings.familyFriendly: String
    get() = localized("Family-friendly", "Для семьи", "Отбасына ыңғайлы")

val EcoStrings.communityVerified: String
    get() = localized("Community verified", "Проверено сообществом", "Қауымдастық растаған")

val EcoStrings.highlyRated: String
    get() = localized("Highly rated", "Высокий рейтинг", "Жоғары бағаланған")

val EcoStrings.openNow: String
    get() = localized("Open now", "Открыто сейчас", "Қазір ашық")

val EcoStrings.nearest: String
    get() = localized("Nearest", "Ближайшие", "Ең жақын")

val EcoStrings.refill: String
    get() = localized("Refill", "Пополнение", "Толтыру")

val EcoStrings.highSeverity: String
    get() = localized("High severity", "Высокая важность", "Жоғары маңыз")

val EcoStrings.distance: String
    get() = localized("Distance", "Расстояние", "Қашықтық")

val EcoStrings.rating: String
    get() = localized("Rating", "Рейтинг", "Баға")

val EcoStrings.cleanliness: String
    get() = localized("Cleanliness", "Чистота", "Тазалық")

val EcoStrings.type: String
    get() = localized("Type", "Тип", "Түрі")

val EcoStrings.facilities: String
    get() = localized("Facilities", "Удобства", "Мүмкіндіктер")

val EcoStrings.hours: String
    get() = localized("Hours", "Часы работы", "Жұмыс уақыты")

val EcoStrings.workingHours: String
    get() = localized("Working hours", "Время работы", "Жұмыс уақыты")

val EcoStrings.gps: String
    get() = localized("GPS", "GPS", "GPS")

val EcoStrings.buildRouteWithMaps: String
    get() = localized("Build route with Google Maps", "Построить маршрут в Google Maps", "Google Maps арқылы маршрут құру")

val EcoStrings.rateAndReport: String
    get() = localized("Rate and report", "Оценить и сообщить", "Бағалау және хабарлау")

val EcoStrings.waterQualityAndReports: String
    get() = localized("Water quality and reports", "Качество воды и жалобы", "Су сапасы және хабарламалар")

val EcoStrings.uploadUpdatedPhoto: String
    get() = localized("Upload updated photo", "Загрузить новое фото", "Жаңа фото жүктеу")

val EcoStrings.verifyStationInformation: String
    get() = localized("Verify station information", "Проверить информацию станции", "Станция ақпаратын растау")

val EcoStrings.submitReport: String
    get() = localized("Submit report", "Отправить жалобу", "Хабарлама жіберу")

val EcoStrings.reportSavedWithPoints: String
    get() = localized("Report saved +15 EcoPoints", "Жалоба сохранена +15 EcoPoints", "Хабарлама сақталды +15 EcoPoints")

val EcoStrings.savedWithPoints: String
    get() = localized("Saved +15 EcoPoints", "Сохранено +15 EcoPoints", "Сақталды +15 EcoPoints")

val EcoStrings.reviews: String
    get() = localized("Reviews", "Отзывы", "Пікірлер")

val EcoStrings.yes: String
    get() = localized("Yes", "Да", "Иә")

val EcoStrings.no: String
    get() = localized("No", "Нет", "Жоқ")

val EcoStrings.verifications: String
    get() = localized("Verifications", "Подтверждения", "Растаулар")

val EcoStrings.severity: String
    get() = localized("Severity", "Важность", "Маңыздылық")

val EcoStrings.reportedBy: (String, String) -> String
    get() = { name, date ->
        when (languageTag) {
            "ru" -> "Сообщил $name - $date"
            "kk" -> "$name хабарлады - $date"
            else -> "Reported by $name - $date"
        }
    }

val EcoStrings.communityActions: String
    get() = localized("Community actions", "Действия сообщества", "Қауымдастық әрекеттері")

val EcoStrings.verifyCondition: String
    get() = localized("Verify condition +10 EcoPoints", "Подтвердить состояние +10 EcoPoints", "Жағдайын растау +10 EcoPoints")

val EcoStrings.verifiedWithPoints: String
    get() = localized("Verified +10 EcoPoints", "Подтверждено +10 EcoPoints", "Расталды +10 EcoPoints")

val EcoStrings.submitReportUpdate: String
    get() = localized("Submit a report update", "Обновить жалобу", "Шағым жаңарту")

val EcoStrings.updateSavedWithPoints: String
    get() = localized("Update saved +15 EcoPoints", "Обновление сохранено +15 EcoPoints", "Жаңарту сақталды +15 EcoPoints")

val EcoStrings.sendUpdateToAdministration: String
    get() = localized("Send update to administration", "Отправить администрации", "Әкімшілікке жіберу")

val EcoStrings.comments: String
    get() = localized("Comments", "Комментарии", "Пікірлер")

val EcoStrings.access: String
    get() = localized("Access", "Доступ", "Қолжетімділік")

val EcoStrings.openDetails: String
    get() = localized("Open details", "Открыть детали", "Толық ақпарат")

val EcoStrings.openReport: String
    get() = localized("Open report", "Открыть жалобу", "Шағымды ашу")

fun EcoStrings.biotoiletStatusName(status: BiotoiletStatus): String {
    return when (status) {
        BiotoiletStatus.Open -> localized("Open", "Открыто", "Ашық")
        BiotoiletStatus.Unknown -> localized("Unknown", "Неизвестно", "Белгісіз")
        BiotoiletStatus.Closed -> localized("Closed", "Закрыто", "Жабық")
        BiotoiletStatus.Maintenance -> localized("Maintenance", "На ремонте", "Жөндеуде")
    }
}

fun EcoStrings.biotoiletTypeName(type: BiotoiletType): String {
    return when (type) {
        BiotoiletType.Free -> free
        BiotoiletType.Paid -> paid
    }
}

fun EcoStrings.biotoiletIssueName(issue: BiotoiletIssueType): String {
    return when (issue) {
        BiotoiletIssueType.ClosedToilet -> localized("Closed toilet", "Туалет закрыт", "Туалет жабық")
        BiotoiletIssueType.DirtyToilet -> localized("Dirty toilet", "Грязный туалет", "Туалет лас")
        BiotoiletIssueType.DamagedToilet -> localized("Damaged", "Поврежден", "Бұзылған")
        BiotoiletIssueType.NoWaterAvailable -> localized("No water", "Нет воды", "Су жоқ")
    }
}

fun EcoStrings.waterStationTypeName(type: WaterStationType): String {
    return when (type) {
        WaterStationType.FreeDrinkingWater -> localized("Free water", "Бесплатная вода", "Тегін су")
        WaterStationType.RefillStation -> localized("Refill", "Пополнение", "Толтыру")
        WaterStationType.FilteredWater -> localized("Filtered", "Фильтрованная", "Сүзілген")
        WaterStationType.WaterDispenser -> localized("Dispenser", "Диспенсер", "Диспенсер")
        WaterStationType.BottledWaterVendingMachine -> localized("Vending", "Автомат", "Автомат")
    }
}

fun EcoStrings.waterStationStatusName(status: WaterStationStatus): String {
    return when (status) {
        WaterStationStatus.Available -> localized("Available", "Доступно", "Қолжетімді")
        WaterStationStatus.Unknown -> localized("Unknown", "Неизвестно", "Белгісіз")
        WaterStationStatus.TemporarilyUnavailable -> localized("Unavailable", "Недоступно", "Қолжетімсіз")
        WaterStationStatus.Maintenance -> localized("Maintenance", "На ремонте", "Жөндеуде")
    }
}

fun EcoStrings.waterStationIssueName(issue: WaterStationIssueType): String {
    return when (issue) {
        WaterStationIssueType.NotWorking -> localized("Not working", "Не работает", "Жұмыс істемейді")
        WaterStationIssueType.PoorWaterQuality -> localized("Poor quality", "Плохое качество", "Сапасы нашар")
        WaterStationIssueType.NoWater -> localized("No water", "Нет воды", "Су жоқ")
        WaterStationIssueType.WrongHours -> localized("Wrong hours", "Неверное время", "Уақыты дұрыс емес")
    }
}

fun EcoStrings.ecoReportStatusName(status: EcoReportStatus): String {
    return when (status) {
        EcoReportStatus.Submitted -> localized("Submitted", "Отправлено", "Жіберілді")
        EcoReportStatus.Verified -> localized("Verified", "Подтверждено", "Расталды")
        EcoReportStatus.InProgress -> localized("In progress", "В работе", "Орындалуда")
        EcoReportStatus.Resolved -> localized("Resolved", "Решено", "Шешілді")
        EcoReportStatus.Rejected -> localized("Rejected", "Отклонено", "Қабылданбады")
    }
}

fun EcoStrings.ecoReportSeverityName(severity: EcoReportSeverity): String {
    return when (severity) {
        EcoReportSeverity.Low -> localized("Low", "Низкая", "Төмен")
        EcoReportSeverity.Medium -> localized("Medium", "Средняя", "Орташа")
        EcoReportSeverity.High -> localized("High", "Высокая", "Жоғары")
    }
}

fun EcoStrings.ecoReportIssueName(issue: EcoReportIssueType): String {
    return when (issue) {
        EcoReportIssueType.IllegalDump -> localized("Illegal dump", "Незаконная свалка", "Заңсыз қоқыс")
        EcoReportIssueType.ConstructionWaste -> localized("Construction waste", "Строительный мусор", "Құрылыс қалдығы")
        EcoReportIssueType.HazardousWaste -> localized("Hazardous waste", "Опасные отходы", "Қауіпті қалдық")
        EcoReportIssueType.OverflowingBins -> localized("Overflowing bins", "Переполненные контейнеры", "Толып кеткен жәшіктер")
    }
}

private val englishEcoStrings = EcoStrings(
    languageTag = "en",
    home = "Home",
    map = "Map",
    submit = "Submit",
    leads = "Leads",
    profile = "Profile",
    login = "Login",
    splashTagline = "Recycle • Track • Grow",
    splashExperience = "ECO-CONSCIOUS DIGITAL EXPERIENCE",
    registerNow = "Register Now",
    createAccount = "Create Account",
    forgotPassword = "Forgot Password?",
    welcomeBack = "Login",
    signInSubtitle = "Sign in to continue your eco journey",
    emailAddress = "Email address",
    password = "Password",
    continueWithGoogle = "Continue with Google",
    or = "or",
    noAccountPrefix = "Don't have an account? ",
    fullName = "Full Name",
    joinCommunity = "Join the eco community",
    confirmPassword = "Confirm Password",
    fairlyStrongPassword = "Fairly strong password",
    termsPrefix = "I agree to the ",
    terms = "Terms of Service",
    termsAnd = " and ",
    privacyPolicy = "Privacy Policy.",
    orSignUpWith = "OR SIGN UP WITH",
    alreadyHaveAccount = "Already have an account? ",
    checkYourEmail = "Check Your Email",
    forgotPasswordSubtitle = "Enter your email and we will send you a secure reset link.",
    resetInstructions = { "We sent password reset instructions to $it." },
    rememberedPassword = "Remembered your password? ",
    sendResetLink = "Send Reset Link",
    resetLinkSent = "Reset link sent",
    invalidEmail = "Enter a valid email address",
    hello = { "Hello, $it!" },
    quickActions = "QUICK ACTIONS",
    currentEcoRating = "CURRENT ECO RATING",
    points = { "$it pts" },
    pointsLabel = "Points",
    level = { "Level $it" },
    globalRank = { "Global Rank: #$it" },
    virtualTreeTitle = "Your Virtual Tree",
    progressToLevel = { progress, level -> "$progress% to Level $level" },
    recentAchievements = "Recent Achievements",
    searchLocation = "Search location...",
    openUntil = "Open until 8:00 PM",
    distanceAway = { rating, meters -> "$rating (124) • $meters m away" },
    acceptedItems = "Accepted Items",
    reward = "Reward",
    acceptedWasteTypes = "Accepted Waste Types",
    open = "OPEN",
    buildRoute = "Build Route",
    call = "Call",
    share = "Share",
    sustainableFact = "Sustainable Fact",
    submitWaste = "Submit Waste",
    wasteType = "Waste Type",
    quantity = "Quantity",
    photoConfirmation = "Photo Confirmation",
    takePhotoUpload = "Take Photo or Upload",
    commentOptional = "Comment (optional)",
    addNote = "Add a note...",
    submitWithPoints = { "Submit +$it pts" },
    myEcoTree = "My Eco Tree",
    progressToNextLevel = { "Progress to Level $it" },
    xpStatus = { current, next -> "$current / $next XP" },
    xpToGo = { "$it XP to go" },
    nextGoal = "Next Goal",
    collectXpGoal = { xp, level -> "Collect $xp more XP to reach Level $level" },
    growthHistory = "Growth History",
    challenges = "Challenges",
    daily = "Daily",
    weekly = "Weekly",
    special = "Special",
    dailyChallenges = "Daily Challenges",
    weeklyChallenges = "Weekly Challenges",
    left = { "$it left" },
    active = "Active",
    completed = "Completed",
    locked = "Locked",
    ecoSpecialTitle = "Eco-Special: Park Clean-up",
    ecoSpecialSubtitle = "Join the community this Saturday for double points!",
    leaderboard = "Leaderboard",
    students = "Students",
    faculties = "Faculties",
    topFaculty = "Top Faculty",
    thisWeek = { "+$it% this week" },
    total = "Total",
    members = "Members",
    topContributor = { members, name -> "$members members • Top: $name" },
    memberSince = { "Member since $it" },
    kgWaste = "kg Waste",
    trees = "Trees",
    sustainabilityScore = "Sustainability Score",
    topContributorText = "You're in the top 12%\nof contributors this\nmonth!",
    nextMilestone = "Next Milestone: Level 5",
    myAchievements = "My Achievements",
    recyclingHistory = "Recycling History",
    notifications = "Notifications",
    settings = "Settings",
    support = "Support",
    logout = "Logout",
    all = "All",
    achievements = "Achievements",
    ecoProgress = "Eco Progress",
    achievementsUnlocked = { unlocked, total -> "$unlocked of $total achievements unlocked" },
    completePercent = { "$it% complete" },
    recentSubmissions = "Recent Submissions",
    yourImpact = "Your Impact",
    impactSubtitle = "Track what you recycled and how many points you earned.",
    recentNotifications = "Recent Notifications",
    unread = { "$it unread" },
    totalUpdates = { "$it total updates from EcoDala" },
    markAllRead = "Mark all read",
    supportHeroTitle = "EcoDala Kazakhstan Care",
    supportHeroSubtitle = "We help with points, recycling places and app issues across Kazakhstan.",
    contactUs = "Contact us",
    sendRequest = "Send a request",
    describeIssue = "Describe your issue...",
    requestSaved = "Request saved. Support will contact you soon.",
    faq = "FAQ",
    settingsPreferences = "Preferences",
    settingsPushNotifications = "Push Notifications",
    settingsPushNotificationsSubtitle = "Daily challenges and reward updates",
    settingsLocation = "Location Access",
    settingsLocationSubtitle = "Show nearby recycling points",
    settingsAiTips = "AI Eco Tips",
    settingsAiTipsSubtitle = "Personalized waste sorting suggestions",
    settingsDisplayUnits = "Display & Units",
    settingsDarkMode = "Dark Mode",
    settingsDarkModeSubtitle = "Switch app theme instantly",
    settingsLanguage = "Language",
    settingsLanguageSubtitle = "Choose the app language",
    settingsPreferredUnit = "Preferred Unit",
    settingsPreferredUnitSubtitle = "Used in submissions and history",
    settingsPrivacy = "Privacy",
    settingsPublicProfile = "Public Leaderboard Profile",
    settingsPublicProfileSubtitle = "Show your name in rankings",
    settingsDataSecurity = "Data & Security",
    settingsDataSecuritySubtitle = "Account protection and data export",
    settingsInfo = "Settings are stored locally with DataStore and can be synced with the backend later.",
    wasteTypeName = { it.title },
    challengeTypeName = { it.name },
    challengeStatusName = {
        when (it) {
            ChallengeStatus.Active -> "Active"
            ChallengeStatus.Completed -> "Completed"
            ChallengeStatus.Locked -> "Locked"
        }
    }
)

private val russianEcoStrings = englishEcoStrings.copy(
    languageTag = "ru",
    home = "Главная",
    map = "Карта",
    submit = "Сдать",
    leads = "Рейтинг",
    profile = "Профиль",
    login = "Войти",
    splashTagline = "Сдавайте • Отслеживайте • Развивайтесь",
    splashExperience = "ЭКОЛОГИЧНЫЙ ЦИФРОВОЙ ОПЫТ",
    registerNow = "Регистрация",
    createAccount = "Создать аккаунт",
    forgotPassword = "Забыли пароль?",
    welcomeBack = "Вход",
    signInSubtitle = "Войдите, чтобы продолжить эко-путь",
    emailAddress = "Email адрес",
    password = "Пароль",
    continueWithGoogle = "Продолжить с Google",
    or = "или",
    noAccountPrefix = "Нет аккаунта? ",
    fullName = "Полное имя",
    joinCommunity = "Присоединяйтесь к эко-сообществу",
    confirmPassword = "Подтвердите пароль",
    fairlyStrongPassword = "Пароль достаточно надежный",
    termsPrefix = "Я принимаю ",
    terms = "Условия сервиса",
    termsAnd = " и ",
    privacyPolicy = "Политику конфиденциальности.",
    orSignUpWith = "ИЛИ ЗАРЕГИСТРИРУЙТЕСЬ ЧЕРЕЗ",
    alreadyHaveAccount = "Уже есть аккаунт? ",
    checkYourEmail = "Проверьте почту",
    forgotPasswordSubtitle = "Введите email, и мы отправим безопасную ссылку для сброса пароля.",
    resetInstructions = { "Мы отправили инструкцию по сбросу пароля на $it." },
    rememberedPassword = "Вспомнили пароль? ",
    sendResetLink = "Отправить ссылку",
    resetLinkSent = "Ссылка отправлена",
    invalidEmail = "Введите корректный email",
    hello = { "Привет, $it!" },
    quickActions = "БЫСТРЫЕ ДЕЙСТВИЯ",
    currentEcoRating = "ТЕКУЩИЙ ЭКО-РЕЙТИНГ",
    points = { "$it баллов" },
    pointsLabel = "Баллы",
    level = { "Уровень $it" },
    globalRank = { "Общий рейтинг: #$it" },
    virtualTreeTitle = "Ваше виртуальное дерево",
    progressToLevel = { progress, level -> "$progress% до уровня $level" },
    recentAchievements = "Последние достижения",
    searchLocation = "Поиск места...",
    openUntil = "Открыто до 20:00",
    distanceAway = { rating, meters -> "$rating (124) • $meters м от вас" },
    acceptedItems = "Принимают",
    reward = "Награда",
    acceptedWasteTypes = "Принимаемые отходы",
    open = "ОТКРЫТО",
    buildRoute = "Построить маршрут",
    call = "Позвонить",
    share = "Поделиться",
    sustainableFact = "Эко-факт",
    submitWaste = "Сдать отходы",
    wasteType = "Тип отходов",
    quantity = "Количество",
    photoConfirmation = "Подтверждение фото",
    takePhotoUpload = "Сделать фото или загрузить",
    commentOptional = "Комментарий (необязательно)",
    addNote = "Добавьте заметку...",
    submitWithPoints = { "Сдать +$it баллов" },
    myEcoTree = "Мое эко-дерево",
    progressToNextLevel = { "Прогресс до уровня $it" },
    xpStatus = { current, next -> "$current / $next XP" },
    xpToGo = { "осталось $it XP" },
    nextGoal = "Следующая цель",
    collectXpGoal = { xp, level -> "Соберите еще $xp XP до уровня $level" },
    growthHistory = "История роста",
    challenges = "Челленджи",
    daily = "Ежедневные",
    weekly = "Еженедельные",
    special = "Особые",
    dailyChallenges = "Ежедневные челленджи",
    weeklyChallenges = "Еженедельные челленджи",
    left = { "осталось $it" },
    active = "Активно",
    completed = "Выполнено",
    locked = "Закрыто",
    ecoSpecialTitle = "Эко-спец: уборка парка",
    ecoSpecialSubtitle = "Присоединяйтесь в субботу и получите двойные баллы!",
    leaderboard = "Рейтинг",
    students = "Студенты",
    faculties = "Факультеты",
    topFaculty = "Лучший факультет",
    thisWeek = { "+$it% за неделю" },
    total = "Всего",
    members = "Участники",
    topContributor = { members, name -> "$members участников • Топ: $name" },
    memberSince = { "Участник с $it" },
    kgWaste = "кг отходов",
    trees = "Деревья",
    sustainabilityScore = "Индекс устойчивости",
    topContributorText = "Вы в топ 12%\nучастников этого\nмесяца!",
    nextMilestone = "Следующая цель: уровень 5",
    myAchievements = "Мои достижения",
    recyclingHistory = "История переработки",
    notifications = "Уведомления",
    settings = "Настройки",
    support = "Поддержка",
    logout = "Выйти",
    all = "Все",
    achievements = "Достижения",
    ecoProgress = "Эко-прогресс",
    achievementsUnlocked = { unlocked, total -> "$unlocked из $total достижений открыто" },
    completePercent = { "$it% выполнено" },
    recentSubmissions = "Последние сдачи",
    yourImpact = "Ваш вклад",
    impactSubtitle = "Следите, что вы сдали и сколько баллов получили.",
    recentNotifications = "Последние уведомления",
    unread = { "$it непрочитано" },
    totalUpdates = { "Всего обновлений: $it" },
    markAllRead = "Прочитать все",
    supportHeroTitle = "Поддержка EcoDala Казахстан",
    supportHeroSubtitle = "Поможем с баллами, пунктами переработки и вопросами по приложению.",
    contactUs = "Связаться с нами",
    sendRequest = "Отправить запрос",
    describeIssue = "Опишите проблему...",
    requestSaved = "Запрос сохранен. Поддержка скоро свяжется с вами.",
    faq = "FAQ",
    settingsPreferences = "Предпочтения",
    settingsPushNotifications = "Push-уведомления",
    settingsPushNotificationsSubtitle = "Ежедневные челленджи и награды",
    settingsLocation = "Геолокация",
    settingsLocationSubtitle = "Показывать ближайшие пункты переработки",
    settingsAiTips = "AI эко-советы",
    settingsAiTipsSubtitle = "Персональные подсказки по сортировке",
    settingsDisplayUnits = "Экран и единицы",
    settingsDarkMode = "Темная тема",
    settingsDarkModeSubtitle = "Мгновенно меняет тему приложения",
    settingsLanguage = "Язык",
    settingsLanguageSubtitle = "Выберите язык приложения",
    settingsPreferredUnit = "Единица измерения",
    settingsPreferredUnitSubtitle = "Для сдачи отходов и истории",
    settingsPrivacy = "Приватность",
    settingsPublicProfile = "Публичный профиль в рейтинге",
    settingsPublicProfileSubtitle = "Показывать ваше имя в рейтингах",
    settingsDataSecurity = "Данные и безопасность",
    settingsDataSecuritySubtitle = "Защита аккаунта и экспорт данных",
    settingsInfo = "Настройки сохраняются локально через DataStore, позже можно синхронизировать с backend.",
    wasteTypeName = {
        when (it) {
            WasteType.Plastic -> "Пластик"
            WasteType.Paper -> "Бумага"
            WasteType.Glass -> "Стекло"
            WasteType.Batteries -> "Батарейки"
            WasteType.Electronics -> "Электроника"
            WasteType.Organic -> "Органика"
            WasteType.Metal -> "Металл"
        }
    },
    challengeTypeName = {
        when (it) {
            ChallengeType.Daily -> "Ежедневные"
            ChallengeType.Weekly -> "Еженедельные"
            ChallengeType.Special -> "Особые"
        }
    },
    challengeStatusName = {
        when (it) {
            ChallengeStatus.Active -> "Активно"
            ChallengeStatus.Completed -> "Выполнено"
            ChallengeStatus.Locked -> "Закрыто"
        }
    }
)

private val kazakhEcoStrings = englishEcoStrings.copy(
    languageTag = "kk",
    home = "Басты",
    map = "Карта",
    submit = "Тапсыру",
    leads = "Рейтинг",
    profile = "Профиль",
    login = "Кіру",
    splashTagline = "Тапсыр • Бақыла • Өсір",
    splashExperience = "ЭКО-САНАЛЫ ЦИФРЛЫҚ ТӘЖІРИБЕ",
    registerNow = "Тіркелу",
    createAccount = "Аккаунт ашу",
    forgotPassword = "Құпиясөз ұмытылды ма?",
    welcomeBack = "Кіру",
    signInSubtitle = "Эко сапарыңызды жалғастыру үшін кіріңіз",
    emailAddress = "Email мекенжайы",
    password = "Құпиясөз",
    continueWithGoogle = "Google арқылы жалғастыру",
    or = "немесе",
    noAccountPrefix = "Аккаунтыңыз жоқ па? ",
    fullName = "Толық аты",
    joinCommunity = "Эко қауымдастыққа қосылыңыз",
    confirmPassword = "Құпиясөзді растау",
    fairlyStrongPassword = "Құпиясөз жеткілікті мықты",
    termsPrefix = "Мен ",
    terms = "Қызмет шарттарын",
    termsAnd = " және ",
    privacyPolicy = "Құпиялылық саясатын қабылдаймын.",
    orSignUpWith = "НЕМЕСЕ АРҚЫЛЫ ТІРКЕЛУ",
    alreadyHaveAccount = "Аккаунтыңыз бар ма? ",
    checkYourEmail = "Поштаны тексеріңіз",
    forgotPasswordSubtitle = "Email енгізіңіз, біз құпиясөзді қалпына келтіру сілтемесін жібереміз.",
    resetInstructions = { "Құпиясөзді қалпына келтіру нұсқаулығы $it мекенжайына жіберілді." },
    rememberedPassword = "Құпиясөзді еске түсірдіңіз бе? ",
    sendResetLink = "Сілтеме жіберу",
    resetLinkSent = "Сілтеме жіберілді",
    invalidEmail = "Дұрыс email енгізіңіз",
    hello = { "Сәлем, $it!" },
    quickActions = "ЖЫЛДАМ ӘРЕКЕТТЕР",
    currentEcoRating = "ҚАЗІРГІ ЭКО-РЕЙТИНГ",
    points = { "$it балл" },
    pointsLabel = "Балл",
    level = { "$it-деңгей" },
    globalRank = { "Жалпы рейтинг: #$it" },
    virtualTreeTitle = "Виртуалды ағашыңыз",
    progressToLevel = { progress, level -> "$progress% $level-деңгейге дейін" },
    recentAchievements = "Соңғы жетістіктер",
    searchLocation = "Орын іздеу...",
    openUntil = "20:00 дейін ашық",
    distanceAway = { rating, meters -> "$rating (124) • $meters м қашық" },
    acceptedItems = "Қабылдайды",
    reward = "Сыйақы",
    acceptedWasteTypes = "Қабылданатын қалдықтар",
    open = "АШЫҚ",
    buildRoute = "Маршрут құру",
    call = "Қоңырау",
    share = "Бөлісу",
    sustainableFact = "Эко-факт",
    submitWaste = "Қалдық тапсыру",
    wasteType = "Қалдық түрі",
    quantity = "Саны",
    photoConfirmation = "Фото растау",
    takePhotoUpload = "Фото түсіру немесе жүктеу",
    commentOptional = "Пікір (міндетті емес)",
    addNote = "Ескертпе қосыңыз...",
    submitWithPoints = { "Тапсыру +$it балл" },
    myEcoTree = "Менің эко-ағашым",
    progressToNextLevel = { "$it-деңгейге дейінгі прогресс" },
    xpStatus = { current, next -> "$current / $next XP" },
    xpToGo = { "$it XP қалды" },
    nextGoal = "Келесі мақсат",
    collectXpGoal = { xp, level -> "$level-деңгейге жету үшін тағы $xp XP жинаңыз" },
    growthHistory = "Өсу тарихы",
    challenges = "Челлендждер",
    daily = "Күнделікті",
    weekly = "Апталық",
    special = "Арнайы",
    dailyChallenges = "Күнделікті челлендждер",
    weeklyChallenges = "Апталық челлендждер",
    left = { "$it қалды" },
    active = "Белсенді",
    completed = "Аяқталды",
    locked = "Құлыптаулы",
    ecoSpecialTitle = "Эко-арнайы: парк тазалау",
    ecoSpecialSubtitle = "Сенбі күні қосылып, екі есе балл алыңыз!",
    leaderboard = "Рейтинг",
    students = "Студенттер",
    faculties = "Факультеттер",
    topFaculty = "Үздік факультет",
    thisWeek = { "+$it% осы аптада" },
    total = "Барлығы",
    members = "Қатысушылар",
    topContributor = { members, name -> "$members қатысушы • Үздік: $name" },
    memberSince = { "$it бастап қатысушы" },
    kgWaste = "кг қалдық",
    trees = "Ағаштар",
    sustainabilityScore = "Тұрақтылық ұпайы",
    topContributorText = "Сіз осы айдағы\nқатысушылардың\nтоп 12%-ындасыз!",
    nextMilestone = "Келесі мақсат: 5-деңгей",
    myAchievements = "Менің жетістіктерім",
    recyclingHistory = "Қайта өңдеу тарихы",
    notifications = "Хабарламалар",
    settings = "Баптаулар",
    support = "Қолдау",
    logout = "Шығу",
    all = "Барлығы",
    achievements = "Жетістіктер",
    ecoProgress = "Эко-прогресс",
    achievementsUnlocked = { unlocked, total -> "$total жетістіктің $unlocked ашылды" },
    completePercent = { "$it% аяқталды" },
    recentSubmissions = "Соңғы тапсырулар",
    yourImpact = "Сіздің үлесіңіз",
    impactSubtitle = "Не тапсырғаныңызды және қанша балл алғаныңызды бақылаңыз.",
    recentNotifications = "Соңғы хабарламалар",
    unread = { "$it оқылмаған" },
    totalUpdates = { "Барлық жаңарту: $it" },
    markAllRead = "Барлығын оқу",
    supportHeroTitle = "EcoDala Қазақстан қолдауы",
    supportHeroSubtitle = "Балл, қайта өңдеу пункттері және қолданба сұрақтары бойынша көмектесеміз.",
    contactUs = "Бізбен байланысу",
    sendRequest = "Сұрау жіберу",
    describeIssue = "Мәселені сипаттаңыз...",
    requestSaved = "Сұрау сақталды. Қолдау жақында хабарласады.",
    faq = "Жиі сұрақтар",
    settingsPreferences = "Қалаулар",
    settingsPushNotifications = "Push хабарламалар",
    settingsPushNotificationsSubtitle = "Күнделікті челлендждер мен марапаттар",
    settingsLocation = "Геолокация",
    settingsLocationSubtitle = "Жақын қайта өңдеу пункттерін көрсету",
    settingsAiTips = "AI эко-кеңестер",
    settingsAiTipsSubtitle = "Сұрыптау бойынша жеке ұсыныстар",
    settingsDisplayUnits = "Экран және өлшемдер",
    settingsDarkMode = "Қараңғы режим",
    settingsDarkModeSubtitle = "Қолданба тақырыбын бірден ауыстырады",
    settingsLanguage = "Тіл",
    settingsLanguageSubtitle = "Қолданба тілін таңдаңыз",
    settingsPreferredUnit = "Өлшем бірлігі",
    settingsPreferredUnitSubtitle = "Қалдық тапсыруда және тарихта қолданылады",
    settingsPrivacy = "Құпиялылық",
    settingsPublicProfile = "Рейтингтегі ашық профиль",
    settingsPublicProfileSubtitle = "Рейтингтерде атыңызды көрсету",
    settingsDataSecurity = "Деректер және қауіпсіздік",
    settingsDataSecuritySubtitle = "Аккаунтты қорғау және деректерді экспорттау",
    settingsInfo = "Баптаулар DataStore арқылы локалды сақталады, кейін backend-пен синхрондауға болады.",
    wasteTypeName = {
        when (it) {
            WasteType.Plastic -> "Пластик"
            WasteType.Paper -> "Қағаз"
            WasteType.Glass -> "Шыны"
            WasteType.Batteries -> "Батареялар"
            WasteType.Electronics -> "Электроника"
            WasteType.Organic -> "Органика"
            WasteType.Metal -> "Металл"
        }
    },
    challengeTypeName = {
        when (it) {
            ChallengeType.Daily -> "Күнделікті"
            ChallengeType.Weekly -> "Апталық"
            ChallengeType.Special -> "Арнайы"
        }
    },
    challengeStatusName = {
        when (it) {
            ChallengeStatus.Active -> "Белсенді"
            ChallengeStatus.Completed -> "Аяқталды"
            ChallengeStatus.Locked -> "Құлыптаулы"
        }
    }
)
