package com.ecodala.feature.profile.presentation

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

data class EcoNotification(
    val id: String,
    val title: String,
    val message: String,
    val time: String,
    val type: NotificationType,
    val isRead: Boolean
)

enum class NotificationType {
    Achievement,
    Challenge,
    Recycling,
    System
}

class NotificationsViewModel : ViewModel() {
    private val _notifications = MutableStateFlow(
        listOf(
            EcoNotification(
                id = "notification-1",
                title = "Achievement unlocked",
                message = "You earned First Recycling for your latest submission.",
                time = "Today, 10:30",
                type = NotificationType.Achievement,
                isRead = false
            ),
            EcoNotification(
                id = "notification-2",
                title = "Daily challenge reminder",
                message = "Recycle 2 more plastic bottles to finish today's task.",
                time = "Today, 08:15",
                type = NotificationType.Challenge,
                isRead = false
            ),
            EcoNotification(
                id = "notification-3",
                title = "Submission approved",
                message = "Your paper recycling submission was approved. +30 pts added.",
                time = "Yesterday",
                type = NotificationType.Recycling,
                isRead = true
            ),
            EcoNotification(
                id = "notification-4",
                title = "New recycling point nearby",
                message = "Green Recycling Center now accepts electronics.",
                time = "2 days ago",
                type = NotificationType.System,
                isRead = true
            )
        )
    )
    val notifications: StateFlow<List<EcoNotification>> = _notifications

    fun markAllAsRead() {
        _notifications.update { items -> items.map { it.copy(isRead = true) } }
    }

    fun toggleRead(notificationId: String) {
        _notifications.update { items ->
            items.map { if (it.id == notificationId) it.copy(isRead = !it.isRead) else it }
        }
    }
}
