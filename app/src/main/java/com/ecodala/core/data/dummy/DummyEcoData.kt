package com.ecodala.core.data.dummy

import com.ecodala.core.domain.model.Achievement
import com.ecodala.core.domain.model.Challenge
import com.ecodala.core.domain.model.ChallengeStatus
import com.ecodala.core.domain.model.ChallengeType
import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.domain.model.LeaderboardEntry
import com.ecodala.core.domain.model.RecyclingPoint
import com.ecodala.core.domain.model.ScannerResult
import com.ecodala.core.domain.model.TreeGrowthEvent
import com.ecodala.core.domain.model.VirtualTree
import com.ecodala.core.domain.model.WasteSubmission
import com.ecodala.core.domain.model.WasteType

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
            acceptedWasteTypes = listOf(WasteType.Plastic, WasteType.Glass, WasteType.Metal),
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
}
