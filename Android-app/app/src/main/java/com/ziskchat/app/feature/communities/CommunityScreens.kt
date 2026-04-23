package com.ziskchat.app.feature.communities

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ziskchat.app.data.mock.CommunityUiModel
import com.ziskchat.app.data.mock.MockCommunityRepository
import com.ziskchat.app.ui.components.CompactTopBar
import com.ziskchat.app.ui.components.CommunityCard
import com.ziskchat.app.ui.components.MessageBubble
import com.ziskchat.app.ui.theme.ZiskChatAppTheme
import com.ziskchat.app.ui.theme.ZiskChatTheme

data class CommunitiesUiState(val communities: List<CommunityUiModel>)

class CommunitiesViewModel : ViewModel() {
    val state = CommunitiesUiState(MockCommunityRepository.communities())
}

@Composable
fun CommunitiesRoute(
    onOpenGroup: (String) -> Unit,
    onCreateGroup: () -> Unit,
    viewModel: CommunitiesViewModel = viewModel()
) {
    CommunitiesScreen(
        state = viewModel.state,
        onOpenGroup = onOpenGroup,
        onCreateGroup = onCreateGroup
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunitiesScreen(
    state: CommunitiesUiState,
    onOpenGroup: (String) -> Unit,
    onCreateGroup: () -> Unit
) {
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Communities") },
                actions = {
                    IconButton(onClick = onCreateGroup) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ZiskChatTheme.extendedColors.appBackground)
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Button(onClick = onCreateGroup, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Groups, contentDescription = null)
                    Text(" Create new group")
                }
            }
            items(state.communities) { community ->
                CommunityCard(community = community, onClick = { onOpenGroup(community.id) })
            }
        }
    }
}

class GroupDetailViewModel : ViewModel() {
    fun resolve(groupId: String): CommunityUiModel = MockCommunityRepository.communityById(groupId) ?: MockCommunityRepository.communities().first()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroupDetailRoute(
    groupId: String,
    onBack: () -> Unit,
    viewModel: GroupDetailViewModel = viewModel()
) {
    val group = viewModel.resolve(groupId)
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text(group.title) },
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
                .background(ZiskChatTheme.extendedColors.appBackground)
                .padding(padding)
                .padding(16.dp)
        ) {
            Text(group.description, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(group.messages) { message ->
                    MessageBubble(message = message)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Create group") },
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
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("This UI-first screen is ready for future participant selection and backend wiring.", style = MaterialTheme.typography.bodyLarge)
            Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
                Text("Create mock group")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CommunityPreview() {
    ZiskChatAppTheme {
        CommunitiesScreen(
            state = CommunitiesUiState(MockCommunityRepository.communities()),
            onOpenGroup = {},
            onCreateGroup = {}
        )
    }
}
