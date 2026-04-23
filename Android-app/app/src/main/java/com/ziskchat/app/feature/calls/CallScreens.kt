package com.ziskchat.app.feature.calls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ziskchat.app.data.mock.CallLogUiModel
import com.ziskchat.app.data.mock.MediaCallType
import com.ziskchat.app.data.mock.MockCallRepository
import com.ziskchat.app.ui.components.AppAvatar
import com.ziskchat.app.ui.components.CallLogItem
import com.ziskchat.app.ui.components.CompactTopBar
import com.ziskchat.app.ui.theme.ZiskChatAppTheme
import com.ziskchat.app.ui.theme.ZiskChatTheme

data class CallsUiState(val calls: List<CallLogUiModel>)

class CallsViewModel : ViewModel() {
    val state = CallsUiState(MockCallRepository.calls())
}

@Composable
fun CallsRoute(
    onOpenCall: (String) -> Unit,
    viewModel: CallsViewModel = viewModel()
) {
    CallsScreen(state = viewModel.state, onOpenCall = onOpenCall)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallsScreen(
    state: CallsUiState,
    onOpenCall: (String) -> Unit
) {
    Scaffold(
        topBar = { CompactTopBar(title = { Text("Calls") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = {}) {
                Icon(Icons.Default.Call, contentDescription = null)
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
            items(state.calls) { call ->
                CallLogItem(call = call, onClick = { onOpenCall(call.id) })
            }
        }
    }
}

class CallMockViewModel : ViewModel() {
    fun resolve(callId: String): CallLogUiModel = MockCallRepository.callById(callId) ?: MockCallRepository.calls().first()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CallMockScreen(
    callId: String,
    onBack: () -> Unit,
    viewModel: CallMockViewModel = viewModel()
) {
    val call = viewModel.resolve(callId)
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Call preview") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(padding)
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppAvatar(call.user, modifier = Modifier.size(112.dp), highlight = true)
                Spacer(modifier = Modifier.height(20.dp))
                Text(call.user.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(call.time, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    if (call.mediaType == MediaCallType.VIDEO) "UI-only video call screen" else "UI-only voice call screen",
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = {}) {
                    Icon(if (call.mediaType == MediaCallType.VIDEO) Icons.Default.Videocam else Icons.Default.Call, contentDescription = null)
                    Text("  Connect")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun CallsPreview() {
    ZiskChatAppTheme {
        CallsScreen(
            state = CallsUiState(MockCallRepository.calls()),
            onOpenCall = {}
        )
    }
}
