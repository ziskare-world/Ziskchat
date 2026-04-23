package com.ziskchat.app.feature.status

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ziskchat.app.data.mock.MockStatusRepository
import com.ziskchat.app.data.mock.StatusType
import com.ziskchat.app.data.mock.StatusUiModel
import com.ziskchat.app.ui.components.AppAvatar
import com.ziskchat.app.ui.components.CompactTopBar
import com.ziskchat.app.ui.components.SectionTitle
import com.ziskchat.app.ui.components.StatusCard
import com.ziskchat.app.ui.theme.ZiskChatAppTheme
import com.ziskchat.app.ui.theme.ZiskChatTheme

data class StatusUiState(
    val myStatus: StatusUiModel,
    val statuses: List<StatusUiModel>
)

class StatusViewModel : ViewModel() {
    val uiState = StatusUiState(
        myStatus = MockStatusRepository.myStatus(),
        statuses = MockStatusRepository.statuses()
    )
}

@Composable
fun StatusRoute(
    onOpenStatus: (String) -> Unit,
    viewModel: StatusViewModel = viewModel()
) {
    StatusScreen(
        state = viewModel.uiState,
        onOpenStatus = onOpenStatus
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusScreen(
    state: StatusUiState,
    onOpenStatus: (String) -> Unit
) {
    var showCreateMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Status") },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            Box {
                DropdownMenu(
                    expanded = showCreateMenu,
                    onDismissRequest = { showCreateMenu = false },
                    modifier = Modifier.width(208.dp)
                ) {
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Text")
                                Text(
                                    "Share a quick written status",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.EditNote, contentDescription = null)
                        },
                        onClick = { showCreateMenu = false }
                    )
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text("Photo")
                                Text(
                                    "Add a photo with caption support",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Image, contentDescription = null)
                        },
                        onClick = { showCreateMenu = false }
                    )
                }
                FloatingActionButton(onClick = { showCreateMenu = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Create status")
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ZiskChatTheme.extendedColors.appBackground)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                SectionTitle("Recent status")
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    item {
                        StatusStoryCard(
                            status = state.myStatus,
                            isMine = true,
                            onClick = {}
                        )
                    }
                    items(state.statuses) { item ->
                        StatusStoryCard(
                            status = item,
                            isMine = false,
                            onClick = { onOpenStatus(item.id) }
                        )
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("Recent updates")
            }
            items(state.statuses.filterNot { it.viewed }) { item ->
                StatusCard(
                    status = item,
                    onClick = { onOpenStatus(item.id) },
                    showAddAction = false
                )
            }
            item {
                Spacer(modifier = Modifier.height(12.dp))
                SectionTitle("Viewed updates")
            }
            items(state.statuses.filter { it.viewed }) { item ->
                StatusCard(
                    status = item,
                    onClick = { onOpenStatus(item.id) },
                    showAddAction = false
                )
            }
        }
    }
}

@Composable
private fun StatusStoryCard(
    status: StatusUiModel,
    isMine: Boolean,
    onClick: () -> Unit
) {
    val gradient = when (status.type) {
        StatusType.PHOTO -> listOf(Color(0xFF0E5F7A), Color(0xFF0C2230))
        StatusType.VIDEO -> listOf(Color(0xFF0A7A6C), Color(0xFF072C29))
        StatusType.TEXT -> listOf(Color(0xFF3256A8), Color(0xFF181F3F))
    }
    val typeIcon = when (status.type) {
        StatusType.PHOTO -> Icons.Default.Image
        StatusType.VIDEO -> Icons.Default.Videocam
        StatusType.TEXT -> Icons.Default.EditNote
    }
    val typeLabel = when (status.type) {
        StatusType.PHOTO -> "Photo"
        StatusType.VIDEO -> "Video"
        StatusType.TEXT -> "Text"
    }

    Card(
        onClick = onClick,
        modifier = Modifier.size(width = 132.dp, height = 168.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradient))
                .padding(12.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.14f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = Color.White
                    )
                    Text(typeLabel, color = Color.White, style = MaterialTheme.typography.labelMedium)
                }
            }

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Spacer(modifier = Modifier.height(1.dp))
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    AppAvatar(
                        user = status.user,
                        modifier = Modifier.size(42.dp),
                        highlight = !status.viewed || isMine
                    )
                    Text(
                        text = status.caption,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = if (isMine) "My status" else status.user.name,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = status.timeAgo,
                        color = Color.White.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

class StatusViewerViewModel : ViewModel() {
    fun resolve(statusId: String): StatusUiModel = MockStatusRepository.statusById(statusId) ?: MockStatusRepository.myStatus()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusViewerRoute(
    statusId: String,
    onBack: () -> Unit,
    viewModel: StatusViewerViewModel = viewModel()
) {
    val status = viewModel.resolve(statusId)
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Status update") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AppAvatar(status.user, modifier = Modifier.size(92.dp), highlight = !status.viewed)
            Spacer(modifier = Modifier.height(18.dp))
            Text(status.user.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(status.caption, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(status.timeAgo, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(24.dp))
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.PlayArrow, contentDescription = null)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusPreview() {
    ZiskChatAppTheme {
        StatusScreen(
            state = StatusUiState(MockStatusRepository.myStatus(), MockStatusRepository.statuses()),
            onOpenStatus = {}
        )
    }
}
