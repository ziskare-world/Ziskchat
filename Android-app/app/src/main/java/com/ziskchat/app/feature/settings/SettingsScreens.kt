package com.ziskchat.app.feature.settings

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
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ziskchat.app.data.mock.MockSettingsRepository
import com.ziskchat.app.data.mock.SettingsItemUiModel
import com.ziskchat.app.data.mock.UserUiModel
import com.ziskchat.app.ui.components.AppAvatar
import com.ziskchat.app.ui.components.CompactTopBar
import com.ziskchat.app.ui.components.PrimaryTextField
import com.ziskchat.app.ui.components.SettingsRow
import com.ziskchat.app.ui.components.UserCard
import com.ziskchat.app.ui.theme.ZiskChatAppTheme
import com.ziskchat.app.ui.theme.ZiskChatTheme

data class SettingsUiState(
    val user: UserUiModel,
    val items: List<SettingsItemUiModel>
)

class SettingsViewModel : ViewModel() {
    val state = SettingsUiState(
        user = MockSettingsRepository.profile(),
        items = MockSettingsRepository.items()
    )
}

@Composable
fun SettingsRoute(
    onBack: () -> Unit,
    onEditProfile: () -> Unit,
    viewModel: SettingsViewModel = viewModel()
) {
    SettingsScreen(state = viewModel.state, onBack = onBack, onEditProfile = onEditProfile)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsUiState,
    onBack: () -> Unit,
    onEditProfile: () -> Unit
) {
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                UserCard(
                    user = state.user,
                    subtitle = state.user.about,
                    onClick = onEditProfile,
                    trailing = { Text("Edit", color = MaterialTheme.colorScheme.primary) }
                )
                Spacer(modifier = Modifier.height(14.dp))
            }
            items(state.items) { item ->
                SettingsRow(item = item, onClick = {})
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Default.Logout, contentDescription = null)
                    Text("  Logout")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileRoute(onBack: () -> Unit) {
    var name by remember { mutableStateOf(MockSettingsRepository.profile().name) }
    var about by remember { mutableStateOf(MockSettingsRepository.profile().about) }
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Edit profile") },
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
            AppAvatar(MockSettingsRepository.profile(), modifier = Modifier.size(86.dp), highlight = true)
            PrimaryTextField(value = name, onValueChange = { name = it }, label = "Name")
            PrimaryTextField(value = about, onValueChange = { about = it }, label = "About")
            Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Default.Save, contentDescription = null)
                Text("  Save profile")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun SettingsPreview() {
    ZiskChatAppTheme {
        SettingsScreen(
            state = SettingsUiState(MockSettingsRepository.profile(), MockSettingsRepository.items()),
            onBack = {},
            onEditProfile = {}
        )
    }
}
