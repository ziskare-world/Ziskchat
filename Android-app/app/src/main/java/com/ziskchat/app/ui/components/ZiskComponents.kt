package com.ziskchat.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ziskchat.app.data.mock.CallLogUiModel
import com.ziskchat.app.data.mock.CallType
import com.ziskchat.app.data.mock.ChatThreadUiModel
import com.ziskchat.app.data.mock.CommunityUiModel
import com.ziskchat.app.data.mock.DeliveryState
import com.ziskchat.app.data.mock.MediaCallType
import com.ziskchat.app.data.mock.MessageUiModel
import com.ziskchat.app.data.mock.SettingsItemUiModel
import com.ziskchat.app.data.mock.StatusUiModel
import com.ziskchat.app.data.mock.UserUiModel
import com.ziskchat.app.ui.theme.ZiskChatTheme
import kotlinx.coroutines.delay

@Composable
fun ZiskChatLogo(modifier: Modifier = Modifier) {
    val primary = Color(0xFF071B1A)
    val secondary = Color(0xFF103B55)
    val accent = Color(0xFF4ED7C6)
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(30.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        primary,
                        secondary
                    ),
                    start = Offset.Zero,
                    end = Offset(400f, 400f)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxWidth(0.72f).aspectRatio(1f)) {
            val stroke = size.minDimension * 0.13f
            val inset = size.minDimension * 0.18f
            drawLine(
                color = accent,
                start = Offset(inset, inset),
                end = Offset(size.width - inset, inset),
                strokeWidth = stroke
            )
            drawLine(
                color = Color.White,
                start = Offset(size.width - inset * 0.95f, inset),
                end = Offset(inset, size.height - inset),
                strokeWidth = stroke
            )
            drawLine(
                color = accent,
                start = Offset(inset, size.height - inset),
                end = Offset(size.width - inset, size.height - inset),
                strokeWidth = stroke
            )
            drawCircle(
                color = accent.copy(alpha = 0.18f),
                radius = size.minDimension / 2.4f,
                center = Offset(size.width * 0.72f, size.height * 0.28f)
            )
        }
    }
}

@Composable
fun AppAvatar(user: UserUiModel, modifier: Modifier = Modifier, highlight: Boolean = false) {
    val borderColor = if (highlight) MaterialTheme.colorScheme.primary else Color.Transparent
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(
                Brush.linearGradient(
                    listOf(
                        MaterialTheme.colorScheme.secondaryContainer,
                        MaterialTheme.colorScheme.primaryContainer
                    )
                )
            )
            .border(2.dp, borderColor, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = user.avatarSeed,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun PrimaryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        singleLine = true,
        trailingIcon = trailingIcon
    )
}

@Composable
fun ChatListItem(
    chat: ChatThreadUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            AppAvatar(chat.participant, modifier = Modifier.size(54.dp), highlight = chat.unreadCount > 0)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = chat.participant.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f)
                    )
                    Text(text = chat.timestamp, style = MaterialTheme.typography.bodySmall)
                }
                if (chat.isTyping) {
                    TypingIndicatorText(
                        style = MaterialTheme.typography.bodyMedium,
                        color = ZiskChatTheme.extendedColors.success
                    )
                } else {
                    Text(
                        text = chat.lastMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            if (chat.unreadCount > 0) {
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primary
                ) {
                    Text(
                        text = chat.unreadCount.toString(),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        color = MaterialTheme.colorScheme.onPrimary,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
fun UserCard(
    user: UserUiModel,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        AppAvatar(user, modifier = Modifier.size(52.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = user.name, style = MaterialTheme.typography.titleMedium)
            Text(text = subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        trailing?.invoke()
    }
}

@Composable
fun MessageBubble(
    message: MessageUiModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (message.isMine) ZiskChatTheme.extendedColors.chatOutgoing else ZiskChatTheme.extendedColors.chatIncoming,
            shape = RoundedCornerShape(
                topStart = 24.dp,
                topEnd = 24.dp,
                bottomStart = if (message.isMine) 24.dp else 8.dp,
                bottomEnd = if (message.isMine) 8.dp else 24.dp
            ),
            tonalElevation = 2.dp,
            modifier = Modifier.fillMaxWidth(0.82f)
        ) {
            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = message.text, style = MaterialTheme.typography.bodyLarge)
                AnimatedVisibility(visible = message.reactions.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        message.reactions.forEach { reaction ->
                            AssistChip(
                                onClick = {},
                                label = { Text(reaction) },
                                colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = message.timestamp, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.size(6.dp))
                    DeliveryIcon(state = message.deliveryState, mine = message.isMine)
                }
            }
        }
    }
}

@Composable
fun DeliveryIcon(state: DeliveryState, mine: Boolean) {
    if (!mine) return
    when (state) {
        DeliveryState.SENT -> Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        DeliveryState.DELIVERED -> Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
        DeliveryState.SEEN -> Icon(Icons.Default.DoneAll, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.secondary)
    }
}

@Composable
fun StatusCard(
    status: StatusUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showAddAction: Boolean = !status.viewed
) {
    UserCard(
        user = status.user,
        subtitle = "${status.caption} - ${status.timeAgo}",
        onClick = onClick,
        modifier = modifier,
        trailing = {
            if (showAddAction) {
                Surface(shape = CircleShape, color = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.padding(10.dp), tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    )
}

@Composable
fun CommunityCard(
    community: CommunityUiModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = community.title, style = MaterialTheme.typography.titleLarge)
            Text(text = community.description, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                AssistChip(onClick = {}, label = { Text("${community.memberCount} members") })
                AssistChip(onClick = {}, label = { Text(community.activity) })
            }
        }
    }
}

@Composable
fun CallLogItem(call: CallLogUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val stateColor by animateColorAsState(
        targetValue = when (call.type) {
            CallType.MISSED -> ZiskChatTheme.extendedColors.danger
            CallType.INCOMING -> ZiskChatTheme.extendedColors.success
            CallType.OUTGOING -> MaterialTheme.colorScheme.secondary
        },
        label = "callStateColor"
    )
    UserCard(
        user = call.user,
        subtitle = call.time,
        onClick = onClick,
        modifier = modifier,
        trailing = {
            Column(horizontalAlignment = Alignment.End) {
                Text(text = call.type.name.lowercase().replaceFirstChar(Char::uppercase), color = stateColor, style = MaterialTheme.typography.labelLarge)
                Icon(
                    imageVector = if (call.mediaType == MediaCallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}

@Composable
fun SettingsRow(item: SettingsItemUiModel, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val icon = when (item.iconName) {
        "person" -> Icons.Default.Person
        "lock" -> Icons.Default.Lock
        "notifications" -> Icons.Default.Notifications
        "palette" -> Icons.Default.Palette
        "database" -> Icons.Default.Storage
        else -> Icons.Outlined.HelpOutline
    }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(18.dp)) {
            Icon(icon, contentDescription = null, modifier = Modifier.padding(12.dp), tint = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.title, style = MaterialTheme.typography.titleMedium)
            Text(text = item.subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        modifier = modifier.padding(vertical = 8.dp),
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun AddFab(
    onClick: () -> Unit,
    icon: ImageVector = Icons.Default.Add,
    contentDescription: String = "Add"
) {
    FloatingActionButton(onClick = onClick) {
        Icon(icon, contentDescription = contentDescription)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactTopBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {}
) {
    TopAppBar(
        title = title,
        modifier = modifier
            .statusBarsPadding()
            .padding(top = 2.dp),
        navigationIcon = {
            navigationIcon?.invoke()
        },
        actions = actions,
        expandedHeight = 46.dp,
        windowInsets = WindowInsets(0, 0, 0, 0)
    )
}

@Composable
fun TypingIndicatorText(
    modifier: Modifier = Modifier,
    style: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.bodyMedium,
    color: Color = ZiskChatTheme.extendedColors.success
) {
    var dotCount by remember { mutableIntStateOf(1) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(420)
            dotCount = if (dotCount == 3) 1 else dotCount + 1
        }
    }

    Text(
        text = "typing" + ".".repeat(dotCount),
        modifier = modifier,
        style = style,
        color = color
    )
}
