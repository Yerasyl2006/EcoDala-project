package com.ecodala.core.data.dummy

import com.ecodala.core.domain.model.Achievement
import com.ecodala.core.domain.model.Biotoilet
import com.ecodala.core.domain.model.BiotoiletReview
import com.ecodala.core.domain.model.BiotoiletStatus
import com.ecodala.core.domain.model.BiotoiletType
import com.ecodala.core.domain.model.Challenge
import com.ecodala.core.domain.model.ChallengeStatus
import com.ecodala.core.domain.model.ChallengeType
import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.model.EcoReport
import com.ecodala.core.domain.model.EcoReportComment
import com.ecodala.core.domain.model.EcoReportSeverity
import com.ecodala.core.domain.model.EcoReportStatus
import com.ecodala.core.domain.model.LeaderboardEntry
import com.ecodala.core.domain.model.PointsEvent
import com.ecodala.core.domain.model.PointsSource
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.ScannerResult
import com.ecodala.core.domain.model.TreeGrowthEvent
import com.ecodala.core.domain.model.VirtualTree
import com.ecodala.core.domain.model.WasteSubmission
import com.ecodala.core.domain.model.WasteType
import com.ecodala.core.domain.model.WaterStation
import com.ecodala.core.domain.model.WaterStationReview
import com.ecodala.core.domain.model.WaterStationStatus
import com.ecodala.core.domain.model.WaterStationType

object DummyEcoData {
    val currentUser = EcoUser(
        id = "user-1",
        fullName = "EcoWarrior",
        email = "hello@ecodala.com",
        ecoPoints = 845,
        globalRank = 12,
        level = 4,
        joinedAt = "Feb 2023"
    )

    val recyclingPoints = listOf(
        RecyclingPoint(
            id = "point-1",
            name = "Green Recycling Center",
            address = "Abay Ave 150, Almaty",
            phone = "+7 (727) 234-56-78",
            openingHours = "08:00 AM - 07:00 PM",
            latitude = 43.238949,
            longitude = 76.889709,
            rating = 4.8,
            distanceMeters = 450,
            acceptedWasteTypes = listOf(
                WasteType.Plastic,
                WasteType.Paper,
                WasteType.Glass,
                WasteType.Batteries,
                WasteType.Electronics
            ),
            rewardPoints = 15
        ),
        RecyclingPoint(
            id = "point-2",
            name = "Taza Qala Eco Point",
            address = "Dostyk Ave 85, Almaty",
            phone = "+7 (701) 118-22-44",
            openingHours = "09:00 AM - 08:00 PM",
            latitude = 43.250861,
            longitude = 76.955188,
            rating = 4.7,
            distanceMeters = 950,
            acceptedWasteTypes = listOf(WasteType.Glass, WasteType.Plastic, WasteType.Metal),
            rewardPoints = 18
        ),
        RecyclingPoint(
            id = "point-3",
            name = "Mega Recycling Box",
            address = "Rozybakiev St 247A, Almaty",
            phone = "+7 (775) 402-91-20",
            openingHours = "10:00 AM - 10:00 PM",
            latitude = 43.202719,
            longitude = 76.892913,
            rating = 4.6,
            distanceMeters = 1700,
            acceptedWasteTypes = listOf(WasteType.Paper, WasteType.Plastic, WasteType.Electronics),
            rewardPoints = 20
        ),
        RecyclingPoint(
            id = "point-4",
            name = "Battery Safe Drop",
            address = "Satpayev St 90, Almaty",
            phone = "+7 (747) 610-30-30",
            openingHours = "08:30 AM - 06:30 PM",
            latitude = 43.235055,
            longitude = 76.917819,
            rating = 4.9,
            distanceMeters = 780,
            acceptedWasteTypes = listOf(WasteType.Batteries, WasteType.Electronics, WasteType.Metal),
            rewardPoints = 25
        ),
        RecyclingPoint(
            id = "point-5",
            name = "Campus Paper Hub",
            address = "Timiryazev St 42, Almaty",
            phone = "+7 (777) 510-64-12",
            openingHours = "08:00 AM - 05:00 PM",
            latitude = 43.225095,
            longitude = 76.909302,
            rating = 4.5,
            distanceMeters = 1200,
            acceptedWasteTypes = listOf(WasteType.Paper, WasteType.Organic, WasteType.Plastic),
            rewardPoints = 12
        )
    )

    val biotoilets = listOf(
        Biotoilet(
            id = "bio-1",
            name = "Central Park Biotoilet",
            photoLabel = "Park entrance facility",
            address = "Gogol St 1, Central Park, Almaty",
            latitude = 43.260871,
            longitude = 76.971219,
            distanceMeters = 520,
            openingHours = "08:00 AM - 10:00 PM",
            status = BiotoiletStatus.Open,
            type = BiotoiletType.Free,
            isAccessible = true,
            isFamilyFriendly = true,
            cleanlinessRating = 4.6,
            reviews = listOf(
                BiotoiletReview("Aruzhan", 5, "Clean, easy to find and has accessible entrance.", "Today"),
                BiotoiletReview("Daniel", 4, "Good for tourists, paper was available.", "Yesterday")
            )
        ),
        Biotoilet(
            id = "bio-2",
            name = "Abay Square Public Toilet",
            photoLabel = "Square service block",
            address = "Abay Ave 56, Almaty",
            latitude = 43.242734,
            longitude = 76.944886,
            distanceMeters = 780,
            openingHours = "07:00 AM - 11:00 PM",
            status = BiotoiletStatus.Open,
            type = BiotoiletType.Paid,
            isAccessible = true,
            isFamilyFriendly = false,
            cleanlinessRating = 4.2,
            reviews = listOf(
                BiotoiletReview("Miras", 4, "Paid but maintained well.", "2 days ago"),
                BiotoiletReview("Lina", 4, "Wheelchair ramp is available.", "Last week")
            )
        ),
        Biotoilet(
            id = "bio-3",
            name = "Botanical Garden Biotoilet",
            photoLabel = "Garden eco cabin",
            address = "Timiryazev St 36D, Almaty",
            latitude = 43.224728,
            longitude = 76.912821,
            distanceMeters = 1260,
            openingHours = "09:00 AM - 08:00 PM",
            status = BiotoiletStatus.Unknown,
            type = BiotoiletType.Paid,
            isAccessible = false,
            isFamilyFriendly = true,
            cleanlinessRating = 3.8,
            reviews = listOf(
                BiotoiletReview("EcoVisitor", 3, "Usually open, but status changes often.", "3 days ago")
            )
        ),
        Biotoilet(
            id = "bio-4",
            name = "Bus Station Sanitary Point",
            photoLabel = "Transport hub toilet",
            address = "Sayran Bus Station, Almaty",
            latitude = 43.233103,
            longitude = 76.874068,
            distanceMeters = 1900,
            openingHours = "24 hours",
            status = BiotoiletStatus.Maintenance,
            type = BiotoiletType.Free,
            isAccessible = false,
            isFamilyFriendly = false,
            cleanlinessRating = 2.9,
            reviews = listOf(
                BiotoiletReview("Visitor", 2, "Reported no water this morning.", "Today")
            )
        )
    )

    val waterStations = listOf(
        WaterStation(
            id = "water-1",
            name = "Central Park Drinking Fountain",
            photoLabel = "Blue fountain near main alley",
            address = "Central Park, Gogol St 1, Almaty",
            latitude = 43.260112,
            longitude = 76.969814,
            distanceMeters = 480,
            workingHours = "08:00 AM - 10:00 PM",
            waterType = WaterStationType.FreeDrinkingWater,
            status = WaterStationStatus.Available,
            rating = 4.7,
            reviews = listOf(
                WaterStationReview("Aruzhan", 5, "Cold water, easy to refill bottle.", "Today"),
                WaterStationReview("Tourist", 4, "Clean and visible from the path.", "Yesterday")
            )
        ),
        WaterStation(
            id = "water-2",
            name = "KazNU Refill Station",
            photoLabel = "Campus refill point",
            address = "Al-Farabi Ave 71, Almaty",
            latitude = 43.226091,
            longitude = 76.922241,
            distanceMeters = 920,
            workingHours = "08:00 AM - 08:00 PM",
            waterType = WaterStationType.RefillStation,
            status = WaterStationStatus.Available,
            rating = 4.8,
            reviews = listOf(
                WaterStationReview("Student", 5, "Best refill station on campus.", "2 days ago")
            )
        ),
        WaterStation(
            id = "water-3",
            name = "Mega Filtered Water Point",
            photoLabel = "Filtered water near entrance",
            address = "Rozybakiev St 247A, Almaty",
            latitude = 43.202980,
            longitude = 76.892674,
            distanceMeters = 1650,
            workingHours = "10:00 AM - 10:00 PM",
            waterType = WaterStationType.FilteredWater,
            status = WaterStationStatus.Unknown,
            rating = 4.1,
            reviews = listOf(
                WaterStationReview("Miras", 4, "Usually works, but sometimes closed for cleaning.", "Last week")
            )
        ),
        WaterStation(
            id = "water-4",
            name = "Sayran Water Vending Machine",
            photoLabel = "Water vending machine",
            address = "Sayran Bus Station, Almaty",
            latitude = 43.232792,
            longitude = 76.873681,
            distanceMeters = 1980,
            workingHours = "24 hours",
            waterType = WaterStationType.BottledWaterVendingMachine,
            status = WaterStationStatus.Maintenance,
            rating = 3.2,
            reviews = listOf(
                WaterStationReview("Visitor", 2, "Machine was not accepting payment.", "Today")
            )
        )
    )

    val ecoReports = listOf(
        EcoReport(
            id = "report-1",
            title = "Illegal dump near river path",
            photoLabel = "Plastic bags and mixed waste",
            address = "Esentai River walking path, Almaty",
            latitude = 43.229912,
            longitude = 76.928381,
            distanceMeters = 640,
            wasteDescription = "Mixed plastic bags, food packaging and bottles left near the river path.",
            status = EcoReportStatus.Verified,
            severity = EcoReportSeverity.High,
            reportedBy = "EcoWarrior",
            reportedAt = "Today, 09:40",
            verificationCount = 12,
            comments = listOf(
                EcoReportComment("Aibek", "Confirmed. Needs municipal cleanup.", "Today"),
                EcoReportComment("Alina", "I uploaded an updated photo.", "Today")
            )
        ),
        EcoReport(
            id = "report-2",
            title = "Overflowing bins behind campus",
            photoLabel = "Overflowing public bins",
            address = "Timiryazev St 42, Almaty",
            latitude = 43.225728,
            longitude = 76.909981,
            distanceMeters = 980,
            wasteDescription = "Bins are full and waste is spreading to the sidewalk.",
            status = EcoReportStatus.Submitted,
            severity = EcoReportSeverity.Medium,
            reportedBy = "Student",
            reportedAt = "Yesterday",
            verificationCount = 3,
            comments = listOf(
                EcoReportComment("Miras", "Saw this yesterday evening too.", "Yesterday")
            )
        ),
        EcoReport(
            id = "report-3",
            title = "Construction waste near bus stop",
            photoLabel = "Construction debris",
            address = "Abay Ave 150, Almaty",
            latitude = 43.239421,
            longitude = 76.888902,
            distanceMeters = 450,
            wasteDescription = "Wood pieces and broken tiles placed near public transport stop.",
            status = EcoReportStatus.InProgress,
            severity = EcoReportSeverity.High,
            reportedBy = "Dana",
            reportedAt = "2 days ago",
            verificationCount = 8,
            comments = listOf(
                EcoReportComment("Admin", "Cleanup request has been forwarded.", "Today")
            )
        ),
        EcoReport(
            id = "report-4",
            title = "Cleaned park corner",
            photoLabel = "Resolved area after cleanup",
            address = "Central Park, Almaty",
            latitude = 43.260440,
            longitude = 76.970623,
            distanceMeters = 530,
            wasteDescription = "Community report was resolved after volunteer cleanup.",
            status = EcoReportStatus.Resolved,
            severity = EcoReportSeverity.Low,
            reportedBy = "Volunteer",
            reportedAt = "Last week",
            verificationCount = 21,
            comments = listOf(
                EcoReportComment("EcoDala", "Resolved and verified by community.", "3 days ago")
            )
        )
    )

    val tree = VirtualTree(
        level = 4,
        progressPercent = 70,
        currentXp = 70,
        nextLevelXp = 100,
        growthHistory = listOf(
            TreeGrowthEvent("24 May", "Tree", "Reached level 4"),
            TreeGrowthEvent("18 May", "Young Plant", "New leaves unlocked"),
            TreeGrowthEvent("15 May", "Seedling", "Started eco journey")
        )
    )

    val challenges = listOf(
        Challenge(
            id = "challenge-1",
            title = "Recycle 5 Plastic Bottles",
            description = "3 of 5 completed",
            progress = 3,
            target = 5,
            rewardPoints = 20,
            status = ChallengeStatus.Active,
            type = ChallengeType.Daily
        ),
        Challenge(
            id = "challenge-2",
            title = "Visit a Recycling Point",
            description = "Task finished",
            progress = 1,
            target = 1,
            rewardPoints = 15,
            status = ChallengeStatus.Completed,
            type = ChallengeType.Daily
        ),
        Challenge(
            id = "challenge-3",
            title = "Visit 3 Recycling Points",
            description = "Locked until Monday",
            progress = 0,
            target = 3,
            rewardPoints = 100,
            status = ChallengeStatus.Locked,
            type = ChallengeType.Weekly
        )
    )

    val leaderboard = listOf(
        LeaderboardEntry(1, "user-2", "Aibek", 1200),
        LeaderboardEntry(2, "user-3", "Alina", 1150),
        LeaderboardEntry(3, "user-4", "Yerzhan", 1100),
        LeaderboardEntry(4, "user-5", "Samat Bolat", 1050),
        LeaderboardEntry(5, "user-6", "Aizhan K.", 1020),
        LeaderboardEntry(6, "user-7", "Duman Serik", 980),
        LeaderboardEntry(12, "user-1", "You (EcoWarrior)", 845, isCurrentUser = true),
        LeaderboardEntry(13, "user-8", "Kanat A.", 810)
    )

    val achievements = listOf(
        Achievement("achievement-1", "First Recycling", "Completed first recycling task", "Yesterday", "recycling", true),
        Achievement("achievement-2", "100 Points", "Reached 100 eco points", "2 days ago", "star", true),
        Achievement("achievement-3", "Challenge Joined", "Joined your first challenge", "Last week", "group", true),
        Achievement("achievement-4", "Tree Keeper", "Reach virtual tree level 5", null, "tree", false),
        Achievement("achievement-5", "Map Explorer", "Visit 5 recycling points", null, "map", false),
        Achievement("achievement-6", "Waste Scanner", "Scan 10 items with AI scanner", null, "scanner", false)
    )

    val wasteSubmissions = listOf(
        WasteSubmission(
            id = "submission-1",
            userId = currentUser.id,
            wasteType = WasteType.Plastic,
            quantity = 5.0,
            unit = "kg",
            photoUrl = null,
            comment = "Bottles from campus cleanup",
            earnedPoints = 50,
            createdAt = "Today, 10:24"
        ),
        WasteSubmission(
            id = "submission-2",
            userId = currentUser.id,
            wasteType = WasteType.Paper,
            quantity = 3.0,
            unit = "kg",
            photoUrl = null,
            comment = "Old notebooks and printed sheets",
            earnedPoints = 30,
            createdAt = "Yesterday, 16:10"
        ),
        WasteSubmission(
            id = "submission-3",
            userId = currentUser.id,
            wasteType = WasteType.Glass,
            quantity = 8.0,
            unit = "pcs",
            photoUrl = null,
            comment = null,
            earnedPoints = 24,
            createdAt = "24 May"
        ),
        WasteSubmission(
            id = "submission-4",
            userId = currentUser.id,
            wasteType = WasteType.Batteries,
            quantity = 6.0,
            unit = "pcs",
            photoUrl = null,
            comment = "Used AA batteries",
            earnedPoints = 36,
            createdAt = "18 May"
        ),
        WasteSubmission(
            id = "submission-5",
            userId = currentUser.id,
            wasteType = WasteType.Electronics,
            quantity = 2.0,
            unit = "pcs",
            photoUrl = null,
            comment = "Old chargers",
            earnedPoints = 45,
            createdAt = "15 May"
        )
    )

    val scannerResult = ScannerResult(
        wasteType = WasteType.Plastic,
        confidence = 0.94f,
        disposalHint = "Put clean plastic into the nearest plastic collection point."
    )

    val pointsLedger = listOf(
        PointsEvent("points-1", currentUser.id, PointsSource.WasteSubmission, "Plastic waste submitted", 50, "2026-06", "Today, 10:24"),
        PointsEvent("points-2", currentUser.id, PointsSource.WasteSubmission, "Paper waste submitted", 30, "2026-06", "Yesterday, 16:10"),
        PointsEvent("points-3", currentUser.id, PointsSource.ChallengeReward, "Visit a Recycling Point", 15, "2026-06", "Yesterday"),
        PointsEvent("points-4", currentUser.id, PointsSource.AchievementBonus, "First Recycling bonus", 100, "2026-06", "2 days ago"),
        PointsEvent("points-5", currentUser.id, PointsSource.WasteSubmission, "Glass bottles submitted", 24, "2026-05", "24 May"),
        PointsEvent("points-6", currentUser.id, PointsSource.WasteSubmission, "Batteries submitted", 36, "2026-05", "18 May"),
        PointsEvent("points-7", currentUser.id, PointsSource.WasteSubmission, "Electronics submitted", 45, "2026-05", "15 May"),
        PointsEvent("points-8", currentUser.id, PointsSource.ChallengeReward, "Weekly recycling streak", 80, "2026-05", "12 May"),
        PointsEvent("points-9", currentUser.id, PointsSource.AchievementBonus, "100 Points milestone", 465, "2026-04", "April")
    )
}
