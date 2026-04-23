package com.ziskchat.app.feature.projects

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CallSplit
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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

data class ProjectsUiState(val projects: List<ProjectUiModel>)

class ProjectsViewModel : ViewModel() {
    val state: ProjectsUiState
        get() = ProjectsUiState(MockProjectRepository.projects())
}

@Composable
fun ProjectsRoute(
    onOpenProject: (String) -> Unit,
    onCreateProject: () -> Unit,
    viewModel: ProjectsViewModel = viewModel()
) {
    ProjectsScreen(
        state = viewModel.state,
        onOpenProject = onOpenProject,
        onCreateProject = onCreateProject
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectsScreen(
    state: ProjectsUiState,
    onOpenProject: (String) -> Unit,
    onCreateProject: () -> Unit
) {
    Scaffold(
        topBar = {
            CompactTopBar(title = { Text("Projects") })
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
            items(state.projects) { project ->
                ProjectCard(
                    project = project,
                    onClick = { onOpenProject(project.id) }
                )
            }
        }
    }
}

class ProjectDetailViewModel : ViewModel() {
    fun resolve(projectId: String): ProjectUiModel =
        MockProjectRepository.projectById(projectId) ?: MockProjectRepository.projects().first()
}

@Composable
fun ProjectDetailRoute(
    projectId: String,
    onBack: () -> Unit,
    onOpenDocuments: (String) -> Unit,
    onOpenMembers: (String) -> Unit,
    onOpenTimeline: (String) -> Unit,
    viewModel: ProjectDetailViewModel = viewModel()
) {
    ProjectDetailScreen(
        project = viewModel.resolve(projectId),
        onBack = onBack,
        onOpenDocuments = { onOpenDocuments(projectId) },
        onOpenMembers = { onOpenMembers(projectId) },
        onOpenTimeline = { onOpenTimeline(projectId) }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    project: ProjectUiModel,
    onBack: () -> Unit,
    onOpenDocuments: () -> Unit,
    onOpenMembers: () -> Unit,
    onOpenTimeline: () -> Unit
) {
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text(project.name) },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item { ProjectHeroCard(project = project) }
            item { ProjectOverviewCard(project = project) }
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickNavCard(
                        title = "Documents",
                        icon = Icons.Default.Description,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenDocuments
                    )
                    QuickNavCard(
                        title = "Members",
                        icon = Icons.Default.Groups,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenMembers
                    )
                    QuickNavCard(
                        title = "Timeline",
                        icon = Icons.Default.Timeline,
                        modifier = Modifier.weight(1f),
                        onClick = onOpenTimeline
                    )
                }
            }
            item { GithubPlaceholderCard(project = project) }
            item {
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
            item {
                OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Project name") })
            }
            item {
                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    label = { Text("Project details") }
                )
            }
            item {
                OutlinedTextField(value = status, onValueChange = { status = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Status") })
            }
            item { OutlinedTextField(value = startDate, onValueChange = { startDate = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Project start date") }) }
            item { OutlinedTextField(value = phaseOneDate, onValueChange = { phaseOneDate = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Phase 1 date") }) }
            item { OutlinedTextField(value = phaseTwoDate, onValueChange = { phaseTwoDate = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Phase 2 date") }) }
            item { OutlinedTextField(value = finalDeadline, onValueChange = { finalDeadline = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Final deadline") }) }
            item {
                Card(shape = MaterialTheme.shapes.large) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                                    progressNote = "Initial branch assigned by admin."
                                )
                            ),
                            completedItems = listOf("Project shell created", "Team member added"),
                            updates = emptyList(),
                            documents = emptyList(),
                            githubRepoName = "Connect GitHub to sync branches",
                            githubConnected = false
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

class ProjectDocumentsViewModel : ViewModel() {
    fun resolve(projectId: String): ProjectUiModel =
        MockProjectRepository.projectById(projectId) ?: MockProjectRepository.projects().first()
}

@Composable
fun ProjectDocumentsRoute(
    projectId: String,
    onBack: () -> Unit,
    viewModel: ProjectDocumentsViewModel = viewModel()
) {
    ProjectDocumentsScreen(project = viewModel.resolve(projectId), onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDocumentsScreen(project: ProjectUiModel, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Project documents") },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { GithubPlaceholderCard(project = project) }
            items(project.documents) { doc ->
                ProjectDocumentCard(document = doc)
            }
        }
    }
}

class ProjectMembersViewModel : ViewModel() {
    fun resolve(projectId: String): ProjectUiModel =
        MockProjectRepository.projectById(projectId) ?: MockProjectRepository.projects().first()
}

@Composable
fun ProjectMembersRoute(
    projectId: String,
    onBack: () -> Unit,
    viewModel: ProjectMembersViewModel = viewModel()
) {
    ProjectMembersScreen(project = viewModel.resolve(projectId), onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectMembersScreen(project: ProjectUiModel, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Members & branches") },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { GithubPlaceholderCard(project = project) }
            items(project.members) { member ->
                Card(shape = MaterialTheme.shapes.large) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(member.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Text(member.role, color = MaterialTheme.colorScheme.primary)
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.medium) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.CallSplit, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(member.branchName)
                            }
                        }
                        Text(member.progressNote, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

class ProjectTimelineViewModel : ViewModel() {
    fun resolve(projectId: String): ProjectUiModel =
        MockProjectRepository.projectById(projectId) ?: MockProjectRepository.projects().first()
}

@Composable
fun ProjectTimelineRoute(
    projectId: String,
    onBack: () -> Unit,
    viewModel: ProjectTimelineViewModel = viewModel()
) {
    ProjectTimelineScreen(project = viewModel.resolve(projectId), onBack = onBack)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectTimelineScreen(project: ProjectUiModel, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            CompactTopBar(
                title = { Text("Project timeline") },
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
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { ProjectHeroCard(project = project) }
            item {
                TimelineCard(title = "Project start", date = project.startDate, color = Color(0xFF1B7F6B))
                TimelineCard(title = "Phase 1", date = project.phaseOneDate, color = Color(0xFF3B82F6))
                TimelineCard(title = "Phase 2", date = project.phaseTwoDate, color = Color(0xFFF59E0B))
                TimelineCard(title = "Final deadline", date = project.finalDeadline, color = Color(0xFFEF4444))
            }
        }
    }
}

@Composable
private fun ProjectCard(
    project: ProjectUiModel,
    onClick: () -> Unit
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
            LinearProgressIndicator(
                progress = { project.progress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("${project.progress}% complete", style = MaterialTheme.typography.labelLarge)
                Text("Deadline ${project.finalDeadline}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun ProjectHeroCard(project: ProjectUiModel) {
    val animatedProgress by animateFloatAsState(targetValue = project.progress / 100f, label = "projectProgress")
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
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer,
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(project.teamName, color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.labelLarge)
            Text(project.summary, style = MaterialTheme.typography.bodyLarge)
            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TimelineDot(label = "Start", date = project.startDate, color = Color(0xFF1B7F6B))
                TimelineDot(label = "P1", date = project.phaseOneDate, color = Color(0xFF3B82F6))
                TimelineDot(label = "P2", date = project.phaseTwoDate, color = Color(0xFFF59E0B))
                TimelineDot(label = "Final", date = project.finalDeadline, color = Color(0xFFEF4444))
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
private fun TimelineDot(label: String, date: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .background(color = color, shape = MaterialTheme.shapes.small)
        )
        Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
        Text(date, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun QuickNavCard(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(onClick = onClick, modifier = modifier, shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(icon, contentDescription = null)
            Text(title, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun GithubPlaceholderCard(project: ProjectUiModel) {
    Card(shape = MaterialTheme.shapes.large) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("GitHub workspace", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(
                if (project.githubConnected) "Connected to ${project.githubRepoName}" else "Connect GitHub to show live branches, commits, and repository access here.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = {}) {
                Text(if (project.githubConnected) "View repository" else "GitHub sign-in")
            }
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
                Text(
                    "${document.fileType} - ${document.versionLabel} - ${document.sizeLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "Uploaded by ${document.uploadedBy} - ${document.uploadedTime}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
    Card(shape = MaterialTheme.shapes.large, modifier = Modifier.padding(bottom = 10.dp)) {
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
            onCreateProject = {}
        )
    }
}
