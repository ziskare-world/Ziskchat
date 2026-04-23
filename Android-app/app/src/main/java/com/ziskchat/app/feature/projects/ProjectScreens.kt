package com.ziskchat.app.feature.projects

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ziskchat.app.data.mock.MockProjectRepository
import com.ziskchat.app.data.mock.ProjectDocumentUiModel
import com.ziskchat.app.data.mock.ProjectMemberUiModel
import com.ziskchat.app.data.mock.ProjectUiModel
import com.ziskchat.app.ui.components.CompactTopBar
import com.ziskchat.app.ui.theme.ZiskChatAppTheme
import com.ziskchat.app.ui.theme.ZiskChatTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ProjectsUiState(val projects: List<ProjectUiModel>)

enum class ProjectTab(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    OVERVIEW("Overview", Icons.Default.TaskAlt),
    DOCUMENTS("Documents", Icons.Default.Description),
    MEMBERS("Members", Icons.Default.Groups),
    TIMELINE("Timeline", Icons.Default.Timeline)
}

class ProjectsViewModel : ViewModel() {
    val state: ProjectsUiState
        get() = ProjectsUiState(MockProjectRepository.projects())
}

@Composable
fun ProjectsRoute(
    onOpenProject: (String) -> Unit,
    onCreateProject: () -> Unit,
    onOpenGitHub: () -> Unit,
    viewModel: ProjectsViewModel = viewModel()
) {
    ProjectsScreen(
        state = viewModel.state,
        onOpenProject = onOpenProject,
        onCreateProject = onCreateProject,
        onOpenGitHub = onOpenGitHub
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    state: ProjectsUiState,
    onOpenProject: (String) -> Unit,
    onCreateProject: () -> Unit,
    onOpenGitHub: () -> Unit
) {
    val context = LocalContext.current
    val globalGitHubSession = ProjectGitHubStorage.loadGlobalSession(context)
    val coroutineScope = rememberCoroutineScope()
    val hasGlobalGitHubSession = globalGitHubSession != null
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Projects") },
                actions = {
                    TextButton(onClick = onOpenGitHub) {
                        Text(
                            text = globalGitHubSession?.githubLogin ?: "GitHub Sign in",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onCreateProject) {
                Icon(Icons.Default.Add, contentDescription = "Create project")
            }
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
            if (!hasGlobalGitHubSession) {
                item {
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                "Sign in to GitHub",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                            Text(
                                "Sign in once from the top right, then select a repository inside each project settings page.",
                                color = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                }
            }
            items(state.projects) { project ->
                ProjectCard(
                    project = project,
                    onClick = { onOpenProject(project.id) },
                    showGitHubSigninAlert = false
                )
            }
        }
    }
}

class ProjectWorkspaceViewModel : ViewModel() {
    fun resolve(projectId: String): ProjectUiModel =
        MockProjectRepository.projectById(projectId) ?: MockProjectRepository.projects().first()
}

@Composable
fun ProjectDetailRoute(
    projectId: String,
    onBack: () -> Unit,
    onSelectTab: (ProjectTab) -> Unit,
    onOpenGitHub: (String) -> Unit,
    onOpenSettings: (String) -> Unit,
    viewModel: ProjectWorkspaceViewModel = viewModel()
) {
    val project = rememberHydratedProject(projectId = projectId, viewModel = viewModel)
    ProjectWorkspaceScreen(
        project = project,
        selectedTab = ProjectTab.OVERVIEW,
        onBack = onBack,
        onSelectTab = onSelectTab,
        onOpenGitHub = { onOpenGitHub(projectId) },
        onOpenSettings = { onOpenSettings(projectId) }
    )
}

@Composable
fun ProjectDocumentsRoute(
    projectId: String,
    onBack: () -> Unit,
    onSelectTab: (ProjectTab) -> Unit,
    onOpenGitHub: (String) -> Unit,
    onOpenSettings: (String) -> Unit,
    viewModel: ProjectWorkspaceViewModel = viewModel()
) {
    val project = rememberHydratedProject(projectId = projectId, viewModel = viewModel)
    ProjectWorkspaceScreen(
        project = project,
        selectedTab = ProjectTab.DOCUMENTS,
        onBack = onBack,
        onSelectTab = onSelectTab,
        onOpenGitHub = { onOpenGitHub(projectId) },
        onOpenSettings = { onOpenSettings(projectId) }
    )
}

@Composable
fun ProjectMembersRoute(
    projectId: String,
    onBack: () -> Unit,
    onSelectTab: (ProjectTab) -> Unit,
    onOpenGitHub: (String) -> Unit,
    onOpenSettings: (String) -> Unit,
    viewModel: ProjectWorkspaceViewModel = viewModel()
) {
    val project = rememberHydratedProject(projectId = projectId, viewModel = viewModel)
    ProjectWorkspaceScreen(
        project = project,
        selectedTab = ProjectTab.MEMBERS,
        onBack = onBack,
        onSelectTab = onSelectTab,
        onOpenGitHub = { onOpenGitHub(projectId) },
        onOpenSettings = { onOpenSettings(projectId) }
    )
}

@Composable
fun ProjectTimelineRoute(
    projectId: String,
    onBack: () -> Unit,
    onSelectTab: (ProjectTab) -> Unit,
    onOpenGitHub: (String) -> Unit,
    onOpenSettings: (String) -> Unit,
    viewModel: ProjectWorkspaceViewModel = viewModel()
) {
    val project = rememberHydratedProject(projectId = projectId, viewModel = viewModel)
    ProjectWorkspaceScreen(
        project = project,
        selectedTab = ProjectTab.TIMELINE,
        onBack = onBack,
        onSelectTab = onSelectTab,
        onOpenGitHub = { onOpenGitHub(projectId) },
        onOpenSettings = { onOpenSettings(projectId) }
    )
}

@Composable
private fun rememberHydratedProject(
    projectId: String,
    viewModel: ProjectWorkspaceViewModel
): ProjectUiModel {
    val context = LocalContext.current
    val project = viewModel.resolve(projectId)

    LaunchedEffect(projectId) {
        val storedSession = ProjectGitHubStorage.loadSession(context, projectId) ?: return@LaunchedEffect
        val current = MockProjectRepository.projectById(projectId) ?: return@LaunchedEffect
        MockProjectRepository.updateProject(
            current.copy(
                githubConnected = true,
                githubUserLogin = storedSession.githubLogin,
                githubRepoName = storedSession.repoFullName,
                githubRepoOwner = storedSession.repoOwner,
                githubBranches = storedSession.branches
            )
        )
    }

    return project
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProjectWorkspaceScreen(
    project: ProjectUiModel,
    selectedTab: ProjectTab,
    onBack: () -> Unit,
    onSelectTab: (ProjectTab) -> Unit,
    onOpenGitHub: () -> Unit,
    onOpenSettings: () -> Unit
) {
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text(project.name) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Project settings")
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                ProjectTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = tab == selectedTab,
                        onClick = {
                            onSelectTab(tab)
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.label) },
                        label = { Text(tab.label) }
                    )
                }
            }
        }
    ) { padding ->
        when (selectedTab) {
            ProjectTab.OVERVIEW -> OverviewContent(project = project, padding = padding, onOpenGitHub = onOpenGitHub)
            ProjectTab.DOCUMENTS -> DocumentsContent(project = project, padding = padding)
            ProjectTab.MEMBERS -> MembersContent(project = project, padding = padding)
            ProjectTab.TIMELINE -> TimelineContent(project = project, padding = padding)
        }
    }
}

@Composable
private fun OverviewContent(
    project: ProjectUiModel,
    padding: PaddingValues,
    onOpenGitHub: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZiskChatTheme.extendedColors.appBackground)
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { ProjectHeroCard(project = project) }
        item { ProjectGitHubOverviewSection(project = project, onOpenGitHub = onOpenGitHub) }
        if (false) item {
            SectionHeading("Ongoing changes")
            project.updates.forEach { update ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                    shape = MaterialTheme.shapes.large,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(update.member.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(update.summary, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${update.status} • ${update.time}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun DocumentsContent(project: ProjectUiModel, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZiskChatTheme.extendedColors.appBackground)
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(project.documents) { doc ->
            ProjectDocumentCard(document = doc)
        }
    }
}

@Composable
private fun MembersContent(project: ProjectUiModel, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZiskChatTheme.extendedColors.appBackground)
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            if (project.githubConnected) {
                BranchesCard(project.githubRepoOwner, project.githubRepoName, project.githubBranches)
            } else {
                GithubInfoBanner()
            }
        }
        items(project.members) { member ->
            MemberCard(member = member)
        }
    }
}

@Composable
private fun TimelineContent(project: ProjectUiModel, padding: PaddingValues) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ZiskChatTheme.extendedColors.appBackground)
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { ProjectHeroCard(project = project) }
        item { TimelineCard(title = "Project start", date = project.startDate, color = Color(0xFF1B7F6B)) }
        item { TimelineCard(title = "Phase 1", date = project.phaseOneDate, color = Color(0xFF3B82F6)) }
        item { TimelineCard(title = "Phase 2", date = project.phaseTwoDate, color = Color(0xFFF59E0B)) }
        item { TimelineCard(title = "Final deadline", date = project.finalDeadline, color = Color(0xFFEF4444)) }
    }
}

@Composable
fun ProjectCreateRoute(
    onBack: () -> Unit,
    onProjectCreated: (String) -> Unit
) {
    ProjectCreateScreen(onBack = onBack, onProjectCreated = onProjectCreated)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectCreateScreen(
    onBack: () -> Unit,
    onProjectCreated: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("Planning") }
    var startDate by remember { mutableStateOf("May 01") }
    var phaseOneDate by remember { mutableStateOf("May 08") }
    var phaseTwoDate by remember { mutableStateOf("May 18") }
    var finalDeadline by remember { mutableStateOf("May 30") }
    var memberName by remember { mutableStateOf("Aarav Mehta") }
    var memberRole by remember { mutableStateOf("Project Lead") }
    var memberBranch by remember { mutableStateOf("feature/project-admin") }

    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Create project") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Project name") }) }
            item {
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    label = { Text("Project details") }
                )
            }
            item { OutlinedTextField(value = status, onValueChange = { status = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Status") }) }
            item { OutlinedTextField(value = startDate, onValueChange = { startDate = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Project start date") }) }
            item { OutlinedTextField(value = phaseOneDate, onValueChange = { phaseOneDate = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Phase 1 date") }) }
            item { OutlinedTextField(value = phaseTwoDate, onValueChange = { phaseTwoDate = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Phase 2 date") }) }
            item { OutlinedTextField(value = finalDeadline, onValueChange = { finalDeadline = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Final deadline") }) }
            item {
                Card(shape = MaterialTheme.shapes.large) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Initial team member", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(value = memberName, onValueChange = { memberName = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Member name") })
                        OutlinedTextField(value = memberRole, onValueChange = { memberRole = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Role") })
                        OutlinedTextField(value = memberBranch, onValueChange = { memberBranch = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Branch") })
                    }
                }
            }
            item {
                Button(
                    onClick = {
                        val newProject = ProjectUiModel(
                            id = "",
                            name = name.ifBlank { "New Project" },
                            teamName = "Admin Workspace",
                            progress = 18,
                            status = status,
                            startDate = startDate,
                            phaseOneDate = phaseOneDate,
                            phaseTwoDate = phaseTwoDate,
                            finalDeadline = finalDeadline,
                            lastUpdate = "Today, 12:00",
                            summary = summary.ifBlank { "Admin-created project workspace with tracked milestones and team details." },
                            members = listOf(
                                ProjectMemberUiModel(
                                    id = "pm-new",
                                    name = memberName,
                                    role = memberRole,
                                    branchName = memberBranch,
                                    avatarSeed = memberName.take(2).uppercase(),
                                    progressNote = "Initial member added by admin.",
                                    phoneNumber = ""
                                )
                            ),
                            completedItems = listOf("Project shell created", "Team member added"),
                            updates = emptyList(),
                            documents = emptyList(),
                            githubRepoName = "",
                            githubConnected = false,
                            githubUserLogin = "",
                            githubRepoOwner = "",
                            githubBranches = emptyList()
                        )
                        val newId = MockProjectRepository.addProject(newProject)
                        onProjectCreated(newId)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create project")
                }
            }
        }
    }
}

@Composable
fun ProjectSettingsRoute(
    projectId: String,
    onBack: () -> Unit,
    onOpenMembers: () -> Unit,
    onOpenRepository: () -> Unit,
    onOpenMore: () -> Unit,
    viewModel: ProjectWorkspaceViewModel = viewModel()
) {
    val project = rememberHydratedProject(projectId = projectId, viewModel = viewModel)
    ProjectSettingsScreen(
        project = project,
        onBack = onBack,
        onOpenMembers = onOpenMembers,
        onOpenRepository = onOpenRepository,
        onOpenMore = onOpenMore
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSettingsScreen(
    project: ProjectUiModel,
    onBack: () -> Unit,
    onOpenMembers: () -> Unit,
    onOpenRepository: () -> Unit,
    onOpenMore: () -> Unit
) {
    Scaffold(
        topBar = {
            CompactTopBar(title = { Text("Project settings") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ZiskChatTheme.extendedColors.appBackground)
                .padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ProjectSettingsOptionCard(
                    title = "Manage members",
                    subtitle = "${project.members.size} members, roles, and phone numbers",
                    onClick = onOpenMembers
                )
            }
            item {
                ProjectSettingsOptionCard(
                    title = "Repository",
                    subtitle = if (project.githubRepoName.isNotBlank()) project.githubRepoName else "Select a repository for this project",
                    onClick = onOpenRepository
                )
            }
            item {
                ProjectSettingsOptionCard(
                    title = "More",
                    subtitle = "GitHub session and extra project options",
                    onClick = onOpenMore
                )
            }
        }
    }
}

@Composable
fun ProjectSettingsMembersRoute(
    projectId: String,
    onBack: () -> Unit,
    viewModel: ProjectWorkspaceViewModel = viewModel()
) {
    val project = rememberHydratedProject(projectId = projectId, viewModel = viewModel)
    ProjectSettingsMembersScreen(project = project, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSettingsMembersScreen(
    project: ProjectUiModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var memberName by remember(project.id) { mutableStateOf("") }
    var memberPhone by remember(project.id) { mutableStateOf("") }
    var selectedRole by remember(project.id) { mutableStateOf("Developer") }
    var statusMessage by remember(project.id) { mutableStateOf("") }
    val roleOptions = listOf("Developer", "Designer", "Tester", "Manager", "Admin")

    Scaffold(
        topBar = {
            CompactTopBar(title = { Text("Manage members") })
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ZiskChatTheme.extendedColors.appBackground)
                .padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(shape = MaterialTheme.shapes.large) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Add project member", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        OutlinedTextField(
                            value = memberName,
                            onValueChange = { memberName = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Member name") }
                        )
                        OutlinedTextField(
                            value = memberPhone,
                            onValueChange = { memberPhone = it },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Phone number") }
                        )
                        Text("Select role", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                        roleOptions.forEach { role ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedRole = role },
                                shape = MaterialTheme.shapes.large,
                                color = if (selectedRole == role) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(selected = selectedRole == role, onClick = { selectedRole = role })
                                    Text(role, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                        Button(
                            onClick = {
                                if (memberName.isBlank() || memberPhone.isBlank()) {
                                    statusMessage = "Enter member name and phone number."
                                    return@Button
                                }
                                MockProjectRepository.updateProject(
                                    project.copy(
                                        members = project.members + ProjectMemberUiModel(
                                            id = "pm-${project.members.size + 1}-${memberName.lowercase().replace(" ", "-")}",
                                            name = memberName,
                                            role = selectedRole,
                                            branchName = "",
                                            avatarSeed = memberName.take(2).uppercase(),
                                            progressNote = "Added from project settings.",
                                            phoneNumber = memberPhone
                                        )
                                    )
                                )
                                statusMessage = "Member added to this project."
                                memberName = ""
                                memberPhone = ""
                                selectedRole = "Developer"
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save member")
                        }
                    }
                }
            }
            item {
                Card(shape = MaterialTheme.shapes.large) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Current members", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        project.members.forEach { member ->
                            Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceVariant) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text(member.name, fontWeight = FontWeight.SemiBold)
                                    Text(member.role, color = MaterialTheme.colorScheme.primary)
                                    if (member.phoneNumber.isNotBlank()) {
                                        Text(member.phoneNumber, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (statusMessage.isNotBlank()) {
                item {
                    StatusMessageCard(
                        title = "Project settings",
                        message = statusMessage,
                        color = Color(0xFF1B7F6B)
                    )
                }
            }
        }
    }
}

@Composable
fun ProjectSettingsRepositoryRoute(
    projectId: String,
    onBack: () -> Unit,
    viewModel: ProjectWorkspaceViewModel = viewModel()
) {
    val project = rememberHydratedProject(projectId = projectId, viewModel = viewModel)
    ProjectSettingsRepositoryScreen(project = project, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSettingsRepositoryScreen(
    project: ProjectUiModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val globalGitHubSession = ProjectGitHubStorage.loadGlobalSession(context)
    val coroutineScope = rememberCoroutineScope()
    var statusMessage by remember(project.id) { mutableStateOf("") }
    var availableRepositories by remember(project.id) { mutableStateOf<List<GitHubRepoSummary>>(emptyList()) }
    var selectedRepository by remember(project.id) { mutableStateOf(project.githubRepoName) }
    var isLoadingRepositories by remember(project.id) { mutableStateOf(false) }

    LaunchedEffect(project.id, globalGitHubSession?.accessToken) {
        if (globalGitHubSession?.accessToken.isNullOrBlank()) return@LaunchedEffect
        isLoadingRepositories = true
        val repositoriesResult = withContext(Dispatchers.IO) {
            GitHubDeviceFlowService.fetchUserRepositories(globalGitHubSession!!.accessToken)
        }
        repositoriesResult.onSuccess { repositories ->
            availableRepositories = repositories
            selectedRepository = selectedRepository.takeIf { current ->
                repositories.any { it.fullName == current }
            } ?: project.githubRepoName.takeIf { current ->
                repositories.any { it.fullName == current }
            } ?: repositories.firstOrNull()?.fullName.orEmpty()
        }.onFailure {
            statusMessage = it.message ?: "Unable to load repositories."
        }
        isLoadingRepositories = false
    }

    Scaffold(
        topBar = { CompactTopBar(title = { Text("Repository") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ZiskChatTheme.extendedColors.appBackground)
                .padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(shape = MaterialTheme.shapes.large) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text("Select repository", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            if (globalGitHubSession != null) {
                                "Signed in as ${globalGitHubSession.githubLogin}. Choose a repository for this project."
                            } else {
                                "Sign in to GitHub from the Projects page first."
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (isLoadingRepositories) {
                            GithubPollingCard(
                                title = "Loading repositories",
                                message = "Fetching repositories for ${globalGitHubSession?.githubLogin ?: "your GitHub account"}."
                            )
                        }
                        availableRepositories.take(20).forEach { repository ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedRepository = repository.fullName },
                                shape = MaterialTheme.shapes.large,
                                color = if (selectedRepository == repository.fullName) {
                                    MaterialTheme.colorScheme.primaryContainer
                                } else {
                                    MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    RadioButton(
                                        selected = selectedRepository == repository.fullName,
                                        onClick = { selectedRepository = repository.fullName }
                                    )
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(repository.fullName, fontWeight = FontWeight.SemiBold)
                                        Text(
                                            "Default branch: ${repository.defaultBranch}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        Button(
                            onClick = {
                                if (globalGitHubSession == null) {
                                    statusMessage = "Sign in to GitHub from the Projects page first."
                                    return@Button
                                }
                                if (selectedRepository.isBlank()) {
                                    statusMessage = "Select a repository for this project."
                                    return@Button
                                }
                                val owner = selectedRepository.substringBefore('/')
                                val repoName = selectedRepository.substringAfter('/', "")
                                if (owner.isBlank() || repoName.isBlank()) {
                                    statusMessage = "The selected repository is invalid."
                                    return@Button
                                }
                                coroutineScope.launch {
                                    runCatching {
                                        val branches = withContext(Dispatchers.IO) {
                                            GitHubDeviceFlowService.fetchBranches(globalGitHubSession.accessToken, owner, repoName).getOrThrow()
                                        }
                                        val repoDetails = withContext(Dispatchers.IO) {
                                            GitHubDeviceFlowService.fetchRepositoryDetails(globalGitHubSession.accessToken, owner, repoName).getOrThrow()
                                        }
                                        ProjectGitHubStorage.saveSession(
                                            context = context,
                                            projectId = project.id,
                                            session = StoredProjectGitHubSession(
                                                accessToken = globalGitHubSession.accessToken,
                                                githubLogin = globalGitHubSession.githubLogin,
                                                repoFullName = repoDetails.fullName,
                                                repoOwner = repoDetails.ownerLogin,
                                                branches = branches
                                            )
                                        )
                                        MockProjectRepository.updateProject(
                                            project.copy(
                                                githubConnected = true,
                                                githubUserLogin = globalGitHubSession.githubLogin,
                                                githubRepoOwner = repoDetails.ownerLogin,
                                                githubRepoName = repoDetails.fullName,
                                                githubBranches = branches,
                                                githubRepoUrl = repoDetails.htmlUrl,
                                                githubZipUrl = repoDetails.zipballUrl,
                                                githubDescription = repoDetails.description
                                            )
                                        )
                                        statusMessage = "Repository linked to this project."
                                    }.onFailure {
                                        statusMessage = it.message ?: "Unable to link repository."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Save repository")
                        }
                    }
                }
            }
            if (statusMessage.isNotBlank()) {
                item {
                    StatusMessageCard("Repository", statusMessage, Color(0xFF1B7F6B))
                }
            }
        }
    }
}

@Composable
fun ProjectSettingsMoreRoute(
    projectId: String,
    onBack: () -> Unit,
    viewModel: ProjectWorkspaceViewModel = viewModel()
) {
    val project = rememberHydratedProject(projectId = projectId, viewModel = viewModel)
    ProjectSettingsMoreScreen(project = project, onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectSettingsMoreScreen(
    project: ProjectUiModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var statusMessage by remember(project.id) { mutableStateOf("") }

    Scaffold(
        topBar = { CompactTopBar(title = { Text("More") }) }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(ZiskChatTheme.extendedColors.appBackground)
                .padding(padding),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(shape = MaterialTheme.shapes.large) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("GitHub session", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(
                            if (project.githubConnected) {
                                "Connected as ${project.githubUserLogin.ifBlank { "GitHub user" }} to ${project.githubRepoName}"
                            } else {
                                "No repository linked to this project yet."
                            },
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = {
                                ProjectGitHubStorage.clearGlobalSession(context)
                                ProjectGitHubStorage.clearSession(context, project.id)
                                MockProjectRepository.updateProject(
                                    project.copy(
                                        githubConnected = false,
                                        githubUserLogin = "",
                                        githubRepoName = "",
                                        githubRepoOwner = "",
                                        githubBranches = emptyList()
                                    )
                                )
                                statusMessage = "Saved GitHub login removed."
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                        ) {
                            Text("Clear saved GitHub login")
                        }
                    }
                }
            }
            item {
                Card(shape = MaterialTheme.shapes.large) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("More options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text("More project admin options will appear here.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (statusMessage.isNotBlank()) {
                item {
                    StatusMessageCard("More", statusMessage, Color(0xFF1B7F6B))
                }
            }
        }
    }
}

@Composable
private fun ProjectSettingsOptionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

data class GitHubConnectUiState(
    val authMode: GitHubAuthMode = GitHubAuthMode.DEVICE_FLOW,
    val userCode: String = "",
    val verificationUri: String = "",
    val deviceCode: String = "",
    val intervalSeconds: Int = 5,
    val isRequestingCode: Boolean = false,
    val isPolling: Boolean = false,
    val isLoadingRepositories: Boolean = false,
    val isValidatingToken: Boolean = false,
    val accessToken: String = "",
    val personalAccessToken: String = "",
    val githubLogin: String = "",
    val availableRepositories: List<GitHubRepoSummary> = emptyList(),
    val selectedRepository: String = "",
    val errorMessage: String = "",
    val successMessage: String = ""
)

enum class GitHubAuthMode {
    DEVICE_FLOW,
    PERSONAL_TOKEN
}

@Composable
fun ProjectsGitHubConnectRoute(onBack: () -> Unit) {
    ProjectGitHubConnectScreen(
        project = ProjectUiModel(
            id = "global_github",
            name = "GitHub account",
            teamName = "",
            progress = 0,
            status = "",
            startDate = "",
            phaseOneDate = "",
            phaseTwoDate = "",
            finalDeadline = "",
            lastUpdate = "",
            summary = "",
            members = emptyList(),
            completedItems = emptyList(),
            updates = emptyList(),
            documents = emptyList(),
            githubRepoName = "",
            githubConnected = false
        ),
        onBack = onBack,
        onLinked = onBack,
        showRepositoryPicker = false
    )
}

@Composable
fun ProjectGitHubConnectRoute(
    projectId: String,
    onBack: () -> Unit,
    onLinked: () -> Unit,
    viewModel: ProjectWorkspaceViewModel = viewModel()
) {
    val project = rememberHydratedProject(projectId = projectId, viewModel = viewModel)
    ProjectGitHubConnectScreen(
        project = project,
        onBack = onBack,
        onLinked = onLinked,
        showRepositoryPicker = true
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectGitHubConnectScreen(
    project: ProjectUiModel,
    onBack: () -> Unit,
    onLinked: () -> Unit,
    showRepositoryPicker: Boolean
) {
    val context = LocalContext.current
    val globalSession = remember { ProjectGitHubStorage.loadGlobalSession(context) }
    val storedSession = remember(project.id) {
        if (showRepositoryPicker) ProjectGitHubStorage.loadSession(context, project.id) else null
    }
    var uiState by remember(project.id) {
        mutableStateOf(
            GitHubConnectUiState(
                accessToken = globalSession?.accessToken ?: storedSession?.accessToken.orEmpty(),
                personalAccessToken = globalSession?.accessToken ?: storedSession?.accessToken.orEmpty(),
                githubLogin = globalSession?.githubLogin ?: storedSession?.githubLogin.orEmpty(),
                selectedRepository = storedSession?.repoFullName ?: project.githubRepoName,
                successMessage = if (!(globalSession?.githubLogin ?: storedSession?.githubLogin).isNullOrBlank()) {
                    "GitHub signed in as ${globalSession?.githubLogin ?: storedSession?.githubLogin}"
                } else {
                    ""
                },
                isLoadingRepositories = showRepositoryPicker && (globalSession?.accessToken?.isNotBlank() == true || storedSession?.accessToken?.isNotBlank() == true)
            )
        )
    }

    LaunchedEffect(uiState.isPolling, uiState.deviceCode) {
        if (uiState.isPolling && uiState.deviceCode.isNotBlank()) {
            while (uiState.isPolling && uiState.accessToken.isBlank()) {
                delay(uiState.intervalSeconds * 1000L)
                val tokenResult = withContext(Dispatchers.IO) {
                    GitHubDeviceFlowService.pollAccessToken(uiState.deviceCode)
                }
                tokenResult.onSuccess { token ->
                    val userResult = withContext(Dispatchers.IO) {
                        GitHubDeviceFlowService.fetchCurrentUser(token.accessToken)
                    }
                    userResult.onSuccess { login ->
                        ProjectGitHubStorage.saveGlobalSession(
                            context,
                            StoredGlobalGitHubSession(
                                accessToken = token.accessToken,
                                githubLogin = login
                            )
                        )
                        uiState = uiState.copy(
                            accessToken = token.accessToken,
                            isPolling = false,
                            githubLogin = login,
                            isLoadingRepositories = showRepositoryPicker,
                            successMessage = "GitHub signed in as $login"
                        )
                        if (!showRepositoryPicker) onLinked()
                    }.onFailure {
                        uiState = uiState.copy(isPolling = false, errorMessage = it.message ?: "Unable to load GitHub user")
                    }
                }.onFailure { error ->
                    val message = error.message.orEmpty()
                    if (!message.contains("authorization_pending") && !message.contains("slow_down")) {
                        uiState = uiState.copy(isPolling = false, errorMessage = if (message.isBlank()) "GitHub sign-in failed" else message)
                    }
                }
            }
        }
    }

    LaunchedEffect(uiState.accessToken, uiState.isLoadingRepositories) {
        if (showRepositoryPicker && uiState.accessToken.isNotBlank() && uiState.isLoadingRepositories) {
            val repositoriesResult = withContext(Dispatchers.IO) {
                GitHubDeviceFlowService.fetchUserRepositories(uiState.accessToken)
            }
            repositoriesResult.onSuccess { repositories ->
                uiState = uiState.copy(
                    isLoadingRepositories = false,
                    availableRepositories = repositories,
                    selectedRepository = uiState.selectedRepository.takeIf { current ->
                        repositories.any { it.fullName == current }
                    } ?: repositories.firstOrNull()?.fullName.orEmpty(),
                    errorMessage = if (repositories.isEmpty()) "No repositories were found for this GitHub account." else ""
                )
            }.onFailure {
                uiState = uiState.copy(
                    isLoadingRepositories = false,
                    errorMessage = it.message ?: "Unable to load repositories"
                )
            }
        }
    }

    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Link GitHub") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(shape = MaterialTheme.shapes.large) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Choose sign-in method", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Button(
                                onClick = {
                                    uiState = uiState.copy(
                                        authMode = GitHubAuthMode.DEVICE_FLOW,
                                        errorMessage = "",
                                        successMessage = ""
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (uiState.authMode == GitHubAuthMode.DEVICE_FLOW) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    contentColor = Color.White
                                )
                            ) {
                                Text("GitHub sign-in")
                            }
                            Button(
                                onClick = {
                                    uiState = uiState.copy(
                                        authMode = GitHubAuthMode.PERSONAL_TOKEN,
                                        errorMessage = "",
                                        successMessage = ""
                                    )
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (uiState.authMode == GitHubAuthMode.PERSONAL_TOKEN) {
                                        MaterialTheme.colorScheme.primary
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    },
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Use token")
                            }
                        }
                    }
                }
            }
            item {
                if (uiState.authMode == GitHubAuthMode.DEVICE_FLOW) {
                    Button(
                        onClick = {
                            uiState = uiState.copy(isRequestingCode = true, errorMessage = "", successMessage = "")
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Start GitHub sign-in")
                    }
                } else {
                    Card(shape = MaterialTheme.shapes.large) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Use a GitHub personal access token", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Open GitHub in your browser, create a token with repo and read:user access, then paste it here.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Button(
                                onClick = {
                                    runCatching {
                                        val intent = Intent(
                                            Intent.ACTION_VIEW,
                                            Uri.parse("https://github.com/settings/tokens/new?description=ZiskChat&scopes=repo,read:user")
                                        )
                                        context.startActivity(intent)
                                    }.onFailure {
                                        uiState = uiState.copy(errorMessage = it.message ?: "Unable to open GitHub token page")
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open GitHub token page")
                            }
                            OutlinedTextField(
                                value = uiState.personalAccessToken,
                                onValueChange = {
                                    uiState = uiState.copy(personalAccessToken = it, errorMessage = "")
                                },
                                modifier = Modifier.fillMaxWidth(),
                                label = { Text("Personal access token") }
                            )
                            Button(
                                onClick = {
                                    if (uiState.personalAccessToken.isBlank()) {
                                        uiState = uiState.copy(errorMessage = "Paste a GitHub personal access token first.")
                                        return@Button
                                    }
                                    uiState = uiState.copy(
                                        isValidatingToken = true,
                                        errorMessage = "",
                                        successMessage = ""
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Validate token")
                            }
                        }
                    }
                }
            }
            item {
                if (uiState.authMode == GitHubAuthMode.DEVICE_FLOW && uiState.isRequestingCode && uiState.userCode.isBlank()) {
                    LaunchedEffect(Unit) {
                        val result = withContext(Dispatchers.IO) { GitHubDeviceFlowService.requestDeviceCode() }
                        result.onSuccess { code ->
                            uiState = uiState.copy(
                                isRequestingCode = false,
                                userCode = code.userCode,
                                verificationUri = code.verificationUri,
                                deviceCode = code.deviceCode,
                                intervalSeconds = code.interval,
                                isPolling = true
                            )
                        }.onFailure {
                            uiState = uiState.copy(isRequestingCode = false, errorMessage = it.message ?: "Unable to start GitHub sign-in")
                        }
                    }
                }
            }
            item {
                if (uiState.authMode == GitHubAuthMode.PERSONAL_TOKEN && uiState.isValidatingToken) {
                    LaunchedEffect(uiState.personalAccessToken) {
                        val loginResult = withContext(Dispatchers.IO) {
                            GitHubDeviceFlowService.fetchCurrentUser(uiState.personalAccessToken)
                        }
                        loginResult.onSuccess { login ->
                            ProjectGitHubStorage.saveGlobalSession(
                                context,
                                StoredGlobalGitHubSession(
                                    accessToken = uiState.personalAccessToken,
                                    githubLogin = login
                                )
                            )
                            uiState = uiState.copy(
                                accessToken = uiState.personalAccessToken,
                                githubLogin = login,
                                isValidatingToken = false,
                                isLoadingRepositories = showRepositoryPicker,
                                successMessage = "GitHub signed in as $login"
                            )
                            if (!showRepositoryPicker) onLinked()
                        }.onFailure {
                            uiState = uiState.copy(
                                isValidatingToken = false,
                                errorMessage = it.message ?: "Unable to validate GitHub token"
                            )
                        }
                    }
                }
            }
            if (uiState.accessToken.isBlank() && uiState.authMode == GitHubAuthMode.DEVICE_FLOW && uiState.userCode.isNotBlank()) {
                item {
                    Card(shape = MaterialTheme.shapes.large) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text("Step 1", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                            Text("Open GitHub device activation and enter this code:", style = MaterialTheme.typography.bodyLarge)
                            Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceVariant) {
                                Text(
                                    text = uiState.userCode,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 16.dp),
                                    textAlign = TextAlign.Center,
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Button(
                                onClick = {
                                    runCatching {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uiState.verificationUri))
                                        context.startActivity(intent)
                                    }.onFailure {
                                        uiState = uiState.copy(
                                            errorMessage = it.message ?: "Unable to open the GitHub activation page"
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Open GitHub activation page")
                            }
                        }
                    }
                }
            }
            if (uiState.isPolling) {
                item {
                    GithubPollingCard()
                }
            }
            if (uiState.isValidatingToken) {
                item {
                    GithubPollingCard(
                        title = "Validating token",
                        message = "Checking the GitHub token and loading your account."
                    )
                }
            }
            if (uiState.successMessage.isNotBlank()) {
                item {
                    StatusMessageCard(title = "Signed in", message = uiState.successMessage, color = Color(0xFF1B7F6B))
                }
            }
            if (showRepositoryPicker && uiState.isLoadingRepositories) {
                item {
                    GithubPollingCard(
                        title = "Loading repositories",
                        message = "Fetching repositories for ${uiState.githubLogin.ifBlank { "your GitHub account" }}."
                    )
                }
            }
            if (showRepositoryPicker && uiState.availableRepositories.isNotEmpty()) {
                item {
                    Card(shape = MaterialTheme.shapes.large) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text("Select repository", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Text(
                                "Choose the repository you want to link only to this project.",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            uiState.availableRepositories.take(12).forEach { repository ->
                                Surface(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            uiState = uiState.copy(
                                                selectedRepository = repository.fullName,
                                                errorMessage = ""
                                            )
                                        },
                                    shape = MaterialTheme.shapes.large,
                                    color = if (uiState.selectedRepository == repository.fullName) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceVariant
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(horizontal = 12.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        RadioButton(
                                            selected = uiState.selectedRepository == repository.fullName,
                                            onClick = {
                                                uiState = uiState.copy(
                                                    selectedRepository = repository.fullName,
                                                    errorMessage = ""
                                                )
                                            }
                                        )
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(repository.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                            Text(
                                                "Default branch: ${repository.defaultBranch}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (uiState.errorMessage.isNotBlank()) {
                item {
                    StatusMessageCard(title = "GitHub issue", message = uiState.errorMessage, color = Color(0xFFEF4444))
                }
            }
            if (showRepositoryPicker && uiState.accessToken.isNotBlank() && uiState.availableRepositories.isEmpty() && !uiState.isLoadingRepositories) {
                item {
                    Button(
                        onClick = {
                            uiState = uiState.copy(
                                isLoadingRepositories = true,
                                errorMessage = "",
                                successMessage = if (uiState.githubLogin.isNotBlank()) {
                                    "GitHub signed in as ${uiState.githubLogin}"
                                } else {
                                    uiState.successMessage
                                }
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Retry loading repositories")
                    }
                }
            }
            if (showRepositoryPicker) item {
                Button(
                    onClick = {
                        if (uiState.accessToken.isBlank()) {
                            uiState = uiState.copy(errorMessage = "Sign in first to load your GitHub repositories.")
                            return@Button
                        }
                        if (uiState.selectedRepository.isBlank()) {
                            uiState = uiState.copy(errorMessage = "Select a repository to link with this project.")
                            return@Button
                        }
                        val owner = uiState.selectedRepository.substringBefore('/')
                        val repoName = uiState.selectedRepository.substringAfter('/', "")
                        if (owner.isBlank() || repoName.isBlank()) {
                            uiState = uiState.copy(errorMessage = "The selected repository is invalid. Choose another one.")
                            return@Button
                        }

                        uiState = uiState.copy(errorMessage = "", successMessage = "Verifying ${uiState.selectedRepository}...")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Link selected repository")
                }
            }
            item {
                if (uiState.accessToken.isNotBlank() && uiState.successMessage.startsWith("Verifying ")) {
                    LaunchedEffect(uiState.accessToken, uiState.selectedRepository, uiState.successMessage) {
                        val owner = uiState.selectedRepository.substringBefore('/')
                        val repoName = uiState.selectedRepository.substringAfter('/', "")
                        if (owner.isNotBlank() && repoName.isNotBlank()) {
                            val repoResult = withContext(Dispatchers.IO) {
                                GitHubDeviceFlowService.fetchRepositoryDetails(uiState.accessToken, owner, repoName)
                            }
                            repoResult.onSuccess { repo ->
                                val branchesResult = withContext(Dispatchers.IO) {
                                    GitHubDeviceFlowService.fetchBranches(uiState.accessToken, owner, repoName)
                                }
                                branchesResult.onSuccess { branches ->
                                    ProjectGitHubStorage.saveSession(
                                        context = context,
                                        projectId = project.id,
                                        session = StoredProjectGitHubSession(
                                            accessToken = uiState.accessToken,
                                            githubLogin = uiState.githubLogin,
                                            repoFullName = repo.fullName,
                                            repoOwner = repo.ownerLogin,
                                            branches = branches
                                        )
                                    )
                                    MockProjectRepository.updateProject(
                                        project.copy(
                                            githubConnected = true,
                                            githubUserLogin = uiState.githubLogin,
                                            githubRepoOwner = repo.ownerLogin,
                                            githubRepoName = repo.fullName,
                                            githubBranches = branches,
                                            githubRepoUrl = repo.htmlUrl,
                                            githubZipUrl = repo.zipballUrl,
                                            githubDescription = repo.description
                                        )
                                    )
                                    uiState = uiState.copy(
                                        successMessage = "Linked ${repo.fullName} to this project"
                                    )
                                    onLinked()
                                }.onFailure {
                                    uiState = uiState.copy(errorMessage = it.message ?: "Unable to load branches")
                                }
                            }.onFailure {
                                uiState = uiState.copy(errorMessage = it.message ?: "Unable to verify repository")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectCard(
    project: ProjectUiModel,
    onClick: () -> Unit,
    showGitHubSigninAlert: Boolean
) {
    Card(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(project.name, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
                    Text(project.teamName, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                AssistChip(onClick = {}, label = { Text(project.status) })
            }
            LinearProgressIndicator(progress = { project.progress / 100f }, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("${project.progress}% complete", style = MaterialTheme.typography.labelLarge)
                Text("Deadline ${project.finalDeadline}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (project.githubConnected) {
                Text("GitHub linked: ${project.githubRepoName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            } else if (showGitHubSigninAlert) {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = "Sign in to GitHub",
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }
    }
}

@Composable
private fun ProjectHeroCard(project: ProjectUiModel) {
    val progress by animateFloatAsState(targetValue = project.progress / 100f, label = "projectProgress")
    val pulseTransition = rememberInfiniteTransition(label = "projectPulse")
    val pulseAlpha by pulseTransition.animateFloat(
        initialValue = 0.65f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(animation = tween(1100), repeatMode = RepeatMode.Reverse),
        label = "pulseAlpha"
    )

    Card(
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xFF0C4A6E),
                            Color(0xFF14532D),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(project.teamName, color = Color.White.copy(alpha = 0.92f), style = MaterialTheme.typography.labelLarge)
            Text(project.summary, style = MaterialTheme.typography.bodyLarge, color = Color.White)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = Color(0xFF86EFAC),
                trackColor = Color.White.copy(alpha = 0.22f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimelineDot("Start", project.startDate, Color(0xFF6EE7B7), pulseAlpha)
                TimelineDot("P1", project.phaseOneDate, Color(0xFF93C5FD), pulseAlpha)
                TimelineDot("P2", project.phaseTwoDate, Color(0xFFFCD34D), pulseAlpha)
                TimelineDot("Final", project.finalDeadline, Color(0xFFFCA5A5), pulseAlpha)
            }
        }
    }
}

@Composable
private fun ProjectOverviewCard(project: ProjectUiModel) {
    Card(shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                AssistChip(onClick = {}, label = { Text("${project.progress}% complete") })
                AssistChip(onClick = {}, label = { Text(project.status) })
            }
            Text("Start: ${project.startDate}")
            Text("Phase 1: ${project.phaseOneDate}")
            Text("Phase 2: ${project.phaseTwoDate}")
            Text("Final deadline: ${project.finalDeadline}")
        }
    }
}

@Composable
private fun ProjectGitHubOverviewSection(
    project: ProjectUiModel,
    onOpenGitHub: () -> Unit
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var repoUrl by remember(project.id, project.githubRepoUrl) {
        mutableStateOf(project.githubRepoUrl)
    }
    var zipUrl by remember(project.id, project.githubZipUrl) {
        mutableStateOf(project.githubZipUrl)
    }
    var description by remember(project.id, project.githubDescription) {
        mutableStateOf(project.githubDescription)
    }
    var isLoading by remember(project.id, project.githubRepoName) { mutableStateOf(false) }
    var errorMessage by remember(project.id, project.githubRepoName) { mutableStateOf("") }

    LaunchedEffect(project.id, project.githubRepoName, project.githubConnected) {
        val session = ProjectGitHubStorage.loadSession(context, project.id) ?: return@LaunchedEffect
        if (!project.githubConnected || project.githubRepoOwner.isBlank() || project.githubRepoName.isBlank()) {
            return@LaunchedEffect
        }
        isLoading = true
        errorMessage = ""
        val owner = project.githubRepoOwner
        val repoName = project.githubRepoName.substringAfter('/')
        val detailsResult = withContext(Dispatchers.IO) {
            GitHubDeviceFlowService.fetchRepositoryDetails(session.accessToken, owner, repoName)
        }

        detailsResult.onSuccess { details ->
            repoUrl = details.htmlUrl
            zipUrl = details.zipballUrl
            description = details.description
        }.onFailure {
            errorMessage = it.message ?: "Unable to load repository details"
        }
        isLoading = false
    }

    if (!project.githubConnected) {
        GithubStatusCard(project = project, onOpenGitHub = onOpenGitHub)
        return
    }

    Card(shape = MaterialTheme.shapes.extraLarge) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("GitHub project details", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                description.ifBlank { "Connected to ${project.githubRepoName} as ${project.githubUserLogin.ifBlank { "GitHub user" }}." },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceVariant) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(project.githubRepoName, fontWeight = FontWeight.SemiBold)
                        Text(
                            "Linked as ${project.githubUserLogin.ifBlank { "GitHub user" }}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(
                        onClick = {
                            val linkToCopy = repoUrl.ifBlank { "https://github.com/${project.githubRepoName}" }
                            if (linkToCopy.isNotBlank()) {
                                clipboardManager.setText(AnnotatedString(linkToCopy))
                                Toast.makeText(context, "GitHub link copied", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy GitHub link")
                    }
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = {
                        val openUrl = repoUrl.ifBlank { "https://github.com/${project.githubRepoName}" }
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(openUrl)))
                        }.onFailure {
                            Toast.makeText(context, "Unable to open repository", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Open repo", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                Button(
                    onClick = {
                        val defaultBranch = project.githubBranches.firstOrNull().orEmpty().ifBlank { "main" }
                        val downloadUrl = zipUrl.ifBlank {
                            val baseUrl = repoUrl.ifBlank { "https://github.com/${project.githubRepoName}" }
                            "$baseUrl/archive/refs/heads/$defaultBranch.zip"
                        }
                        runCatching {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl)))
                        }.onFailure {
                            Toast.makeText(context, "Unable to download repository", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Download", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            if (isLoading) {
                GithubPollingCard(title = "Loading GitHub details", message = "Fetching repository information.")
            }
            if (errorMessage.isNotBlank()) {
                StatusMessageCard(title = "GitHub issue", message = errorMessage, color = Color(0xFFEF4444))
            }
        }
    }
}

@Composable
private fun TimelineDot(label: String, date: String, color: Color, alpha: Float) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .alpha(alpha)
                .background(color = color, shape = MaterialTheme.shapes.small)
        )
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold, color = Color.White)
        Text(date, style = MaterialTheme.typography.bodySmall, color = Color.White.copy(alpha = 0.84f))
    }
}

@Composable
private fun GithubStatusCard(project: ProjectUiModel, onOpenGitHub: () -> Unit) {
    Card(shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("GitHub workspace", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                if (project.githubConnected) {
                    "Connected as ${project.githubUserLogin.ifBlank { "GitHub user" }} to ${project.githubRepoName}"
                } else {
                    "Sign in from the Projects page, then select a repository for this project."
                },
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun GithubInfoBanner() {
    Card(shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("GitHub branches", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("Connect GitHub on the Overview tab to load real branches for this project.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun BranchesCard(owner: String, fullName: String, branches: List<String>) {
    Card(shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("GitHub branches", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text("$owner • $fullName", color = MaterialTheme.colorScheme.onSurfaceVariant)
            branches.forEach { branch ->
                Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceVariant) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.CallSplit, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(branch)
                    }
                }
            }
        }
    }
}

@Composable
private fun MemberCard(member: ProjectMemberUiModel) {
    Card(shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(member.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(member.role, color = MaterialTheme.colorScheme.primary)
            if (member.phoneNumber.isNotBlank()) {
                Text(member.phoneNumber, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(member.progressNote, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun GithubPollingCard(
    title: String = "Waiting for GitHub authorization",
    message: String = "Finish GitHub sign-in in the browser. This screen keeps checking automatically."
) {
    Card(shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(message, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun StatusMessageCard(title: String, message: String, color: Color) {
    Card(shape = MaterialTheme.shapes.large, colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.12f))) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = color)
            Text(message)
        }
    }
}

@Composable
private fun ProjectDocumentCard(document: ProjectDocumentUiModel) {
    Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.surfaceVariant) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer, MaterialTheme.shapes.medium),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Description, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Text(document.fileName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("${document.fileType} - ${document.versionLabel} - ${document.sizeLabel}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("Uploaded by ${document.uploadedBy} - ${document.uploadedTime}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (document.isRestricted) {
                Surface(shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.primaryContainer) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Text("Restricted", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    }
                }
            }
        }
    }
}

@Composable
private fun TimelineCard(title: String, date: String, color: Color) {
    Card(shape = MaterialTheme.shapes.large) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .background(color = color, shape = MaterialTheme.shapes.small)
            )
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text(date, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun SectionHeading(title: String) {
    Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@Preview(showBackground = true)
@Composable
private fun ProjectsPreview() {
    ZiskChatAppTheme {
        ProjectsScreen(
            state = ProjectsUiState(MockProjectRepository.projects()),
            onOpenProject = {},
            onCreateProject = {},
            onOpenGitHub = {}
        )
    }
}
