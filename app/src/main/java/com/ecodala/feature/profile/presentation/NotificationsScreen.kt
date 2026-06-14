package com.ecodala.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.TipsAndUpdates
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun NotificationsRoute(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: NotificationsViewModel = viewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    NotificationsScreen(
        notifications = notifications,
        onBackClick = onBackClick,
        onMarkAllReadClick = viewModel::markAllAsRead,
        onNotificationClick = viewModel::toggleRead,
        modifier = modifier
    )
}

@Composable
fun NotificationsScreen(
    notifications: List<EcoNotification>,
    onBackClick: () -> Unit,
    onMarkAllReadClick: () -> Unit,
    onNotificationClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val unreadCount = notifications.count { !it.isRead }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            NotificationsTopBar(onBackClick = onBackClick)

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(top = 16.dp, bottom = 24.dp)
            ) {
                NotificationSummaryCard(
                    unreadCount = unreadCount,
                    totalCount = notifications.size,
                    onMarkAllReadClick = onMarkAllReadClick
                )

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = LocalEcoStrings.current.recentNotifications,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                notifications.forEach { notification ->
                    NotificationCard(
                        notification = notification,
                        onClick = { onNotificationClick(notification.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun NotificationsTopBar(onBackClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.align(Alignment.CenterStart)
        ) {
            Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = EcoGreen)
        }

        Text(
            text = LocalEcoStrings.current.notifications,
            color = EcoGreen,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Icon(
            imageVector = Icons.Filled.Notifications,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 18.dp)
                .size(22.dp)
        )
    }
}

@Composable
private fun NotificationSummaryCard(
    unreadCount: Int,
    totalCount: Int,
    onMarkAllReadClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EcoGreen),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = LocalEcoStrings.current.unread(unreadCount),
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = LocalEcoStrings.current.totalUpdates(totalCount),
                    color = Color.White.copy(alpha = 0.76f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = LocalEcoStrings.current.markAllRead,
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color.White.copy(alpha = 0.18f))
                    .clickable(onClick = onMarkAllReadClick)
                    .padding(horizontal = 14.dp, vertical = 9.dp),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun NotificationCard(
    notification: EcoNotification,
    onClick: () -> Unit
) {
    val visual = notification.type.visual()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (notification.isRead) 0.72f else 1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                EcoGreen.copy(alpha = 0.14f)
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(visual.background, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = visual.icon,
                    contentDescription = null,
                    tint = visual.tint,
                    modifier = Modifier.size(23.dp)
                )
            }

            Spacer(modifier = Modifier.size(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = notification.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (!notification.isRead) {
                        Spacer(modifier = Modifier.size(8.dp))
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFE53935), CircleShape)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = notification.message,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = notification.time,
                    color = EcoGreen,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }

            if (notification.isRead) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint = EcoGreen,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

private data class NotificationVisual(
    val icon: ImageVector,
    val background: Color,
    val tint: Color
)

@Composable
private fun NotificationType.visual(): NotificationVisual {
    return when (this) {
        NotificationType.Achievement -> NotificationVisual(Icons.Filled.EmojiEvents, Color(0xFFFFC83D).copy(alpha = 0.20f), Color(0xFFE3A008))
        NotificationType.Challenge -> NotificationVisual(Icons.Filled.TaskAlt, EcoGreen.copy(alpha = 0.16f), EcoGreen)
        NotificationType.Recycling -> NotificationVisual(Icons.Filled.Recycling, Color(0xFF4F8DF7).copy(alpha = 0.18f), Color(0xFF6EA2FF))
        NotificationType.System -> NotificationVisual(Icons.Filled.TipsAndUpdates, MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun NotificationsScreenPreview() {
    EcoDalaTheme {
        NotificationsScreen(
            notifications = NotificationsViewModel().notifications.value,
            onBackClick = {},
            onMarkAllReadClick = {},
            onNotificationClick = {}
        )
    }
}
