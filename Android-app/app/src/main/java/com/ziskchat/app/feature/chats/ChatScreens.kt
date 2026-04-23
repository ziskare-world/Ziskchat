package com.ziskchat.app.feature.chats

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ziskchat.app.data.mock.ChatThreadUiModel
import com.ziskchat.app.data.mock.MockChatRepository
import com.ziskchat.app.data.mock.UserUiModel
import com.ziskchat.app.ui.components.AddFab
import com.ziskchat.app.ui.components.AppAvatar
import com.ziskchat.app.ui.components.ChatListItem
import com.ziskchat.app.ui.components.CompactTopBar
import com.ziskchat.app.ui.components.MessageBubble
import com.ziskchat.app.ui.components.PrimaryTextField
import com.ziskchat.app.ui.components.TypingIndicatorText
import com.ziskchat.app.ui.components.UserCard
import com.ziskchat.app.ui.theme.ZiskChatAppTheme
import com.ziskchat.app.ui.theme.ZiskChatTheme

data class ChatsUiState(
    val currentUser: UserUiModel,
    val chats: List<ChatThreadUiModel>
)

class ChatsViewModel : ViewModel() {
    val uiState = ChatsUiState(
        currentUser = MockChatRepository.currentUser(),
        chats = MockChatRepository.chats()
    )
}

@Composable
fun ChatsRoute(
    onOpenChat: (String) -> Unit,
    onNewChat: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenProjects: () -> Unit,
    viewModel: ChatsViewModel = viewModel()
) {
    ChatsScreen(
        state = viewModel.uiState,
        onOpenChat = onOpenChat,
        onNewChat = onNewChat,
        onOpenSettings = onOpenSettings,
        onOpenProjects = onOpenProjects
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatsScreen(
    state: ChatsUiState,
    onOpenChat: (String) -> Unit,
    onNewChat: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenProjects: () -> Unit
) {
    var menuExpanded by remember { mutableStateOf(false) }
    val menuItems = listOf(
        "Linked devices",
        "Mark all as read",
        "New group",
        "Starred messages",
        "Payments",
        "Projects",
        "Settings"
    )
    Scaffold(
        topBar = {
            Surface(color = MaterialTheme.colorScheme.background) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(start = 12.dp, end = 4.dp, top = 0.dp, bottom = 0.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(onClick = onOpenSettings, modifier = Modifier.size(44.dp)) {
                        AppAvatar(user = state.currentUser, modifier = Modifier.size(36.dp))
                    }
                    Text(
                        text = "ZiskChat",
                        modifier = Modifier.weight(1f),
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .width(228.dp)
                        ) {
                            menuItems.forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = {
                                        menuExpanded = false
                                        when (item) {
                                            "Settings" -> onOpenSettings()
                                            "Projects" -> onOpenProjects()
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        floatingActionButton = { AddFab(onClick = onNewChat) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ZiskChatTheme.extendedColors.appBackground)
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(state.chats) { chat ->
                ChatListItem(chat = chat, onClick = { onOpenChat(chat.id) })
            }
        }
    }
}

class NewChatViewModel : ViewModel() {
    val users = MockChatRepository.users()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatRoute(
    onBack: () -> Unit,
    onOpenChat: (String) -> Unit,
    viewModel: NewChatViewModel = viewModel()
) {
    var query by remember { mutableStateOf("") }
    val filtered = viewModel.users.filter {
        query.isBlank() || it.name.contains(query, true) || it.handle.contains(query, true)
    }
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Start a new chat") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            PrimaryTextField(
                value = query,
                onValueChange = { query = it },
                label = "Search people",
                trailingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
            )
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filtered) { user ->
                    UserCard(
                        user = user,
                        subtitle = user.about,
                        onClick = {
                            val existing = MockChatRepository.chats().firstOrNull { it.participant.id == user.id }?.id ?: "c1"
                            onOpenChat(existing)
                        }
                    )
                }
            }
        }
    }
}

class ChatDetailViewModel : ViewModel() {
    fun resolve(chatId: String): ChatThreadUiModel = MockChatRepository.chatById(chatId) ?: MockChatRepository.chats().first()
}

@Composable
fun ChatDetailRoute(
    chatId: String,
    onBack: () -> Unit,
    viewModel: ChatDetailViewModel = viewModel()
) {
    ChatDetailScreen(chat = viewModel.resolve(chatId), onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ChatDetailScreen(
    chat: ChatThreadUiModel,
    onBack: () -> Unit
) {
    var message by remember { mutableStateOf("") }
    var chatMenuExpanded by remember { mutableStateOf(false) }
    val isTypingMessage = message.isNotBlank()
    val imeVisible = WindowInsets.isImeVisible
    val listState = rememberLazyListState()
    var didInitialScroll by remember(chat.id) { mutableStateOf(false) }

    LaunchedEffect(chat.id, chat.messages.size, didInitialScroll) {
        if (!didInitialScroll && chat.messages.isNotEmpty()) {
            listState.scrollToItem(chat.messages.lastIndex)
            didInitialScroll = true
        }
    }

    Scaffold(
        containerColor = ZiskChatTheme.extendedColors.appBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        topBar = {
            CompactTopBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AppAvatar(chat.participant, modifier = Modifier.size(34.dp))
                        Column {
                            Text(chat.participant.name)
                            if (chat.isTyping && !isTypingMessage) {
                                TypingIndicatorText(
                                    style = MaterialTheme.typography.bodySmall,
                                    color = ZiskChatTheme.extendedColors.success
                                )
                            } else {
                                Text(
                                    text = if (chat.participant.isOnline) "Online now" else "Last seen 2h ago",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (chat.participant.isOnline) {
                                        ZiskChatTheme.extendedColors.online
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Call, contentDescription = null) }
                    IconButton(onClick = {}) { Icon(Icons.Default.Videocam, contentDescription = null) }
                    Box {
                        IconButton(onClick = { chatMenuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = chatMenuExpanded,
                            onDismissRequest = { chatMenuExpanded = false },
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface)
                                .width(208.dp)
                        ) {
                            listOf("View contact", "Search", "Media", "Mute notifications").forEach { item ->
                                DropdownMenuItem(
                                    text = { Text(item) },
                                    onClick = { chatMenuExpanded = false }
                                )
                            }
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .consumeWindowInsets(padding)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                reverseLayout = false,
                contentPadding = PaddingValues(top = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chat.messages) { item ->
                    MessageBubble(message = item)
                }
            }
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .imePadding()
                    .padding(
                        start = 8.dp,
                        end = 8.dp,
                        top = 4.dp,
                        bottom = if (imeVisible) 0.dp else 6.dp
                    )
            ) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 2.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Default.EmojiEmotions, contentDescription = "Emoji")
                            }
                            TextField(
                                value = message,
                                onValueChange = { message = it },
                                modifier = Modifier
                                    .weight(1f)
                                    .heightIn(min = 40.dp, max = 88.dp),
                                placeholder = { Text("Type a message") },
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    disabledContainerColor = androidx.compose.ui.graphics.Color.Transparent,
                                    focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                    unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                                    disabledIndicatorColor = androidx.compose.ui.graphics.Color.Transparent
                                ),
                                shape = MaterialTheme.shapes.extraLarge,
                                singleLine = true,
                                maxLines = 4
                            )
                            AnimatedVisibility(visible = !isTypingMessage) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                                        Icon(Icons.Default.AttachFile, contentDescription = "Attach")
                                    }
                                    IconButton(onClick = {}, modifier = Modifier.size(36.dp)) {
                                        Icon(Icons.Default.CameraAlt, contentDescription = "Camera")
                                    }
                                }
                            }
                        }
                    }
                    FloatingActionButton(onClick = {}, modifier = Modifier.size(46.dp)) {
                        Icon(if (isTypingMessage) Icons.Default.Send else Icons.Default.Mic, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatsPreview() {
    ZiskChatAppTheme {
        ChatsScreen(
            state = ChatsUiState(MockChatRepository.currentUser(), MockChatRepository.chats()),
            onOpenChat = {},
            onNewChat = {},
            onOpenSettings = {},
            onOpenProjects = {}
        )
    }
}
