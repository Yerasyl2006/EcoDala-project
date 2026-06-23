package com.ecodala.feature.profile.presentation

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Recycling
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.SupportAgent
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material.icons.filled.Yard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ecodala.core.data.dummy.DummyEcoData
import com.ecodala.core.domain.model.EcoUser
import com.ecodala.core.localization.LocalEcoStrings
import com.ecodala.core.ui.components.EcoErrorState
import com.ecodala.core.ui.components.EcoInlineLoading
import com.ecodala.core.ui.theme.EcoDalaTheme
import com.ecodala.core.ui.theme.EcoGreen

@Composable
fun ProfileRoute(
    onBackClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onRecyclingHistoryClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSupportClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel = viewModel()
) {
    val user by viewModel.user.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    ProfileScreen(
        user = user,
        uiState = uiState,
        onBackClick = onBackClick,
        onAchievementsClick = onAchievementsClick,
        onRecyclingHistoryClick = onRecyclingHistoryClick,
        onNotificationsClick = onNotificationsClick,
        onSettingsClick = onSettingsClick,
        onSupportClick = onSupportClick,
        onLogoutClick = { viewModel.logout(onLogoutClick) },
        onEditProfileClick = viewModel::startProfileEdit,
        onCancelProfileEdit = viewModel::cancelProfileEdit,
        onSaveProfileClick = viewModel::saveProfile,
        onFullNameChange = viewModel::onEditFullNameChange,
        onEmailChange = viewModel::onEditEmailChange,
        onRetryClick = viewModel::retry,
        modifier = modifier
    )
}

@Composable
fun ProfileScreen(
    user: EcoUser,
    uiState: ProfileUiState = ProfileUiState(),
    onBackClick: () -> Unit,
    onAchievementsClick: () -> Unit,
    onRecyclingHistoryClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSupportClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onEditProfileClick: () -> Unit = {},
    onCancelProfileEdit: () -> Unit = {},
    onSaveProfileClick: () -> Unit = {},
    onFullNameChange: (String) -> Unit = {},
    onEmailChange: (String) -> Unit = {},
    onRetryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            ProfileTopBar(
                onBackClick = onBackClick,
                onSettingsClick = onSettingsClick
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ProfileHeader(user = user)

                Spacer(modifier = Modifier.height(12.dp))

                if (uiState.profileSaved) {
                    Text(
                        text = LocalEcoStrings.current.profileUpdated,
                        color = EcoGreen,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (uiState.isLoading) {
                    EcoInlineLoading(label = "Refreshing profile...")
                    Spacer(modifier = Modifier.height(12.dp))
                }

                uiState.errorMessage?.let { message ->
                    EcoErrorState(
                        message = "Showing saved profile where available. $message",
                        onRetryClick = onRetryClick
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                if (uiState.isEditingProfile) {
                    EditProfileCard(
                        fullName = uiState.editFullName,
                        email = uiState.editEmail,
                        onFullNameChange = onFullNameChange,
                        onEmailChange = onEmailChange,
                        onSaveClick = onSaveProfileClick,
                        onCancelClick = onCancelProfileEdit
                    )
                } else {
                    EditProfileButton(onClick = onEditProfileClick)
                }

                Spacer(modifier = Modifier.height(18.dp))

                StatsRow(uiState = uiState)

                Spacer(modifier = Modifier.height(18.dp))

                StreakStatusCard(uiState = uiState)

                Spacer(modifier = Modifier.height(18.dp))

                SustainabilityScoreCard(score = 75)

                Spacer(modifier = Modifier.height(16.dp))

                ProfileMenu(
                    onAchievementsClick = onAchievementsClick,
                    onRecyclingHistoryClick = onRecyclingHistoryClick,
                    onNotificationsClick = onNotificationsClick,
                    onSettingsClick = onSettingsClick,
                    onSupportClick = onSupportClick
                )

                Spacer(modifier = Modifier.height(22.dp))

                Button(
                    onClick = onLogoutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFB8F0BF),
                        contentColor = EcoGreen
                    )
                ) {
                    Text(
                        text = LocalEcoStrings.current.logout,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun EditProfileButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(22.dp))
            .background(EcoGreen.copy(alpha = 0.12f))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Filled.Edit,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.size(7.dp))
        Text(
            text = LocalEcoStrings.current.editProfile,
            color = EcoGreen,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun EditProfileCard(
    fullName: String,
    email: String,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val strings = LocalEcoStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = strings.editAccountInfo,
                color = MaterialTheme.colorScheme.onSurface,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            ProfileInput(
                label = strings.fullName,
                value = fullName,
                onValueChange = onFullNameChange
            )
            ProfileInput(
                label = strings.emailAddress,
                value = email,
                onValueChange = onEmailChange
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onSaveClick,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EcoGreen)
                ) {
                    Text(strings.save, fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = onCancelClick,
                    modifier = Modifier.weight(1f).height(46.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Text(strings.cancel, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ProfileInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(horizontal = 14.dp, vertical = 14.dp),
            textStyle = TextStyle(
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun StreakStatusCard(uiState: ProfileUiState) {
    val strings = LocalEcoStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(EcoGreen.copy(alpha = 0.12f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Whatshot,
                    contentDescription = null,
                    tint = EcoGreen,
                    modifier = Modifier.size(24.dp)
                )
            }
            Spacer(modifier = Modifier.size(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = strings.streakSummary(uiState.streakDays, uiState.nextStreakRewardPoints).substringBefore(" - "),
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = strings.streakSummary(uiState.streakDays, uiState.nextStreakRewardPoints).substringAfter(" - ", strings.points(uiState.nextStreakRewardPoints)),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun ProfileTopBar(
    onBackClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = EcoGreen
                    )
                }
                Text(
                    text = "EcoDala",
                    color = EcoGreen,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings",
                    tint = EcoGreen,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

@Composable
private fun ProfileHeader(user: EcoUser) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(contentAlignment = Alignment.BottomCenter) {
            Avatar(userName = user.fullName)
            Text(
                text = LocalEcoStrings.current.level(user.level),
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(EcoGreen)
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = user.fullName,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = LocalEcoStrings.current.memberSince(user.joinedAt),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun Avatar(userName: String) {
    Box(
        modifier = Modifier
            .size(86.dp)
            .clip(CircleShape)
            .background(Color(0xFFE6F3DF)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawProfileAvatar(userName)
        }
    }
}

@Composable
private fun StatsRow(uiState: ProfileUiState) {
    val strings = LocalEcoStrings.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            icon = Icons.Filled.Recycling,
            value = uiState.thisMonthKg.toInt().toString(),
            label = strings.kgWaste,
            modifier = Modifier.weight(1f)
        )
        StatCard(
            icon = Icons.Filled.Spa,
            value = uiState.totalPoints.toString(),
            label = strings.pointsLabel,
            modifier = Modifier.weight(1f),
            highlighted = true
        )
        StatCard(
            icon = Icons.Filled.Yard,
            value = uiState.trees.toString(),
            label = strings.trees,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatCard(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    highlighted: Boolean = false
) {
    Card(
        modifier = modifier.height(86.dp),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (highlighted) EcoGreen.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = EcoGreen,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = value,
                color = EcoGreen,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun SustainabilityScoreCard(score: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = LocalEcoStrings.current.sustainabilityScore,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = LocalEcoStrings.current.topContributorText,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                ScoreRing(score = score)
            }

            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE4E9E3))
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = LocalEcoStrings.current.nextMilestone,
                    color = EcoGreen,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                Icon(
                    imageVector = Icons.Filled.BarChart,
                    contentDescription = null,
                    tint = EcoGreen,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun ScoreRing(score: Int) {
    Box(
        modifier = Modifier.size(86.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = Color(0xFFE3EAE2),
                radius = size.minDimension / 2 - 6.dp.toPx(),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 7.dp.toPx())
            )
            drawArc(
                color = EcoGreen,
                startAngle = -90f,
                sweepAngle = 360f * score / 100f,
                useCenter = false,
                topLeft = Offset(6.dp.toPx(), 6.dp.toPx()),
                size = Size(size.width - 12.dp.toPx(), size.height - 12.dp.toPx()),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = 7.dp.toPx())
            )
        }
        Text(
            text = score.toString(),
            color = EcoGreen,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProfileMenu(
    onAchievementsClick: () -> Unit,
    onRecyclingHistoryClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onSupportClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        val strings = LocalEcoStrings.current
        ProfileMenuItem(Icons.Filled.EmojiEvents, strings.myAchievements, onAchievementsClick)
        ProfileMenuItem(Icons.Filled.History, strings.recyclingHistory, onRecyclingHistoryClick)
        ProfileMenuItem(Icons.Filled.Notifications, strings.notifications, onNotificationsClick, showDot = true)
        ProfileMenuItem(Icons.Filled.Settings, strings.settings, onSettingsClick)
        ProfileMenuItem(Icons.Filled.SupportAgent, strings.support, onSupportClick)
    }
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
    showDot: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = EcoGreen,
            modifier = Modifier.size(19.dp)
        )
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold
        )
        if (showDot) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(Color(0xFFE53935), CircleShape)
            )
            Spacer(modifier = Modifier.size(10.dp))
        }
        Icon(
            imageVector = Icons.Filled.ArrowForward,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(18.dp)
        )
    }
}

private fun DrawScope.drawProfileAvatar(userName: String) {
    drawCircle(
        brush = Brush.verticalGradient(listOf(Color(0xFF1D6731), Color(0xFF0D2D1A))),
        radius = size.minDimension / 2
    )
    drawCircle(
        color = Color(0xFFD19662),
        radius = size.minDimension * 0.16f,
        center = Offset(size.width * 0.50f, size.height * 0.34f)
    )
    drawRoundRect(
        color = Color(0xFF2E7D38),
        topLeft = Offset(size.width * 0.31f, size.height * 0.56f),
        size = Size(size.width * 0.38f, size.height * 0.28f),
        cornerRadius = CornerRadius(size.width * 0.12f)
    )
    drawOval(
        color = Color(0xFF141817),
        topLeft = Offset(size.width * 0.35f, size.height * 0.16f),
        size = Size(size.width * 0.30f, size.height * 0.18f)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.10f),
        radius = size.minDimension * 0.42f,
        center = Offset(size.width * 0.28f, size.height * 0.20f)
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ProfileScreenPreview() {
    EcoDalaTheme {
        ProfileScreen(
            user = DummyEcoData.currentUser,
            onBackClick = {},
            onAchievementsClick = {},
            onRecyclingHistoryClick = {},
            onNotificationsClick = {},
            onSettingsClick = {},
            onSupportClick = {},
            onLogoutClick = {}
        )
    }
}
