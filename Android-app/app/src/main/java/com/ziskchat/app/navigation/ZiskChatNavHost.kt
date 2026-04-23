package com.ziskchat.app.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ziskchat.app.feature.auth.ForgotPasswordScreen
import com.ziskchat.app.feature.auth.LoginScreen
import com.ziskchat.app.feature.auth.OtpVerificationScreen
import com.ziskchat.app.feature.auth.ResetPasswordScreen
import com.ziskchat.app.feature.auth.SignupScreen
import com.ziskchat.app.feature.calls.CallMockScreen
import com.ziskchat.app.feature.calls.CallsRoute
import com.ziskchat.app.feature.chats.ChatDetailRoute
import com.ziskchat.app.feature.chats.ChatsRoute
import com.ziskchat.app.feature.chats.NewChatRoute
import com.ziskchat.app.feature.communities.CreateGroupScreen
import com.ziskchat.app.feature.communities.GroupDetailRoute
import com.ziskchat.app.feature.projects.ProjectDetailRoute
import com.ziskchat.app.feature.projects.ProjectCreateRoute
import com.ziskchat.app.feature.projects.ProjectDocumentsRoute
import com.ziskchat.app.feature.projects.ProjectMembersRoute
import com.ziskchat.app.feature.projects.ProjectsRoute
import com.ziskchat.app.feature.projects.ProjectGitHubConnectRoute
import com.ziskchat.app.feature.projects.ProjectsGitHubConnectRoute
import com.ziskchat.app.feature.projects.ProjectSettingsMembersRoute
import com.ziskchat.app.feature.projects.ProjectSettingsMoreRoute
import com.ziskchat.app.feature.projects.ProjectSettingsRepositoryRoute
import com.ziskchat.app.feature.projects.ProjectSettingsRoute
import com.ziskchat.app.feature.projects.ProjectTab
import com.ziskchat.app.feature.projects.ProjectTimelineRoute
import com.ziskchat.app.feature.settings.EditProfileRoute
import com.ziskchat.app.feature.settings.SettingsRoute
import com.ziskchat.app.feature.status.StatusRoute
import com.ziskchat.app.feature.status.StatusViewerRoute
import com.ziskchat.app.ui.components.ZiskChatLogo
import kotlinx.coroutines.delay

@Composable
fun ZiskChatNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ZiskRoute.Splash.route
    ) {
        composable(ZiskRoute.Splash.route) {
            SplashRoute {
                navController.navigate(ZiskRoute.Login.route) {
                    popUpTo(ZiskRoute.Splash.route) { inclusive = true }
                }
            }
        }
        composable(ZiskRoute.Login.route) {
            LoginScreen(
                onLogin = {
                    navController.navigate(ZiskRoute.Main.route) {
                        popUpTo(ZiskRoute.Login.route) { inclusive = true }
                    }
                },
                onSignup = { navController.navigate(ZiskRoute.Signup.route) },
                onForgotPassword = { navController.navigate(ZiskRoute.ForgotPassword.route) }
            )
        }
        composable(ZiskRoute.Signup.route) {
            SignupScreen(
                onSignup = {
                    navController.navigate(ZiskRoute.Main.route) {
                        popUpTo(ZiskRoute.Login.route) { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }
        composable(ZiskRoute.ForgotPassword.route) {
            ForgotPasswordScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(ZiskRoute.OtpVerification.route) }
            )
        }
        composable(ZiskRoute.OtpVerification.route) {
            OtpVerificationScreen(
                onBack = { navController.popBackStack() },
                onContinue = { navController.navigate(ZiskRoute.ResetPassword.route) }
            )
        }
        composable(ZiskRoute.ResetPassword.route) {
            ResetPasswordScreen(
                onBack = { navController.popBackStack() },
                onDone = {
                    navController.navigate(ZiskRoute.Login.route) {
                        popUpTo(ZiskRoute.Login.route) { inclusive = true }
                    }
                }
            )
        }
        composable(ZiskRoute.Main.route) {
            MainShell(navController)
        }
        composable(ZiskRoute.NewChat.route) {
            NewChatRoute(
                onBack = { navController.popBackStack() },
                onOpenChat = { navController.navigate(ZiskRoute.ChatDetail.create(it)) }
            )
        }
        composable(
            route = ZiskRoute.ChatDetail.route,
            arguments = listOf(navArgument("chatId") { defaultValue = "c1" })
        ) {
            val chatId = it.arguments?.getString("chatId").orEmpty()
            ChatDetailRoute(
                chatId = chatId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(
            route = ZiskRoute.StatusViewer.route,
            arguments = listOf(navArgument("statusId") { defaultValue = "s1" })
        ) {
            val statusId = it.arguments?.getString("statusId").orEmpty()
            StatusViewerRoute(statusId = statusId, onBack = { navController.popBackStack() })
        }
        composable(ZiskRoute.CreateGroup.route) {
            CreateGroupScreen(
                onBack = { navController.popBackStack() },
                onDone = { navController.popBackStack() }
            )
        }
        composable(
            route = ZiskRoute.ProjectDetail.route,
            arguments = listOf(navArgument("projectId") { defaultValue = "p1" })
        ) {
            val projectId = it.arguments?.getString("projectId").orEmpty()
            ProjectDetailRoute(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onSelectTab = { tab ->
                    when (tab) {
                        ProjectTab.OVERVIEW -> navController.navigate(ZiskRoute.ProjectDetail.create(projectId))
                        ProjectTab.DOCUMENTS -> navController.navigate(ZiskRoute.ProjectDocuments.create(projectId))
                        ProjectTab.MEMBERS -> navController.navigate(ZiskRoute.ProjectMembers.create(projectId))
                        ProjectTab.TIMELINE -> navController.navigate(ZiskRoute.ProjectTimeline.create(projectId))
                    }
                },
                onOpenGitHub = { navController.navigate(ZiskRoute.ProjectGitHubConnect.create(it)) },
                onOpenSettings = { navController.navigate(ZiskRoute.ProjectSettings.create(it)) }
            )
        }
        composable(ZiskRoute.ProjectCreate.route) {
            ProjectCreateRoute(
                onBack = { navController.popBackStack() },
                onProjectCreated = {
                    navController.navigate(ZiskRoute.ProjectDetail.create(it)) {
                        popUpTo(ZiskRoute.ProjectCreate.route) { inclusive = true }
                    }
                }
            )
        }
        composable(ZiskRoute.ProjectsGitHubConnect.route) {
            ProjectsGitHubConnectRoute(onBack = { navController.popBackStack() })
        }
        composable(
            route = ZiskRoute.ProjectDocuments.route,
            arguments = listOf(navArgument("projectId") { defaultValue = "p1" })
        ) {
            val projectId = it.arguments?.getString("projectId").orEmpty()
            ProjectDocumentsRoute(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onSelectTab = { tab ->
                    when (tab) {
                        ProjectTab.OVERVIEW -> navController.navigate(ZiskRoute.ProjectDetail.create(projectId))
                        ProjectTab.DOCUMENTS -> navController.navigate(ZiskRoute.ProjectDocuments.create(projectId))
                        ProjectTab.MEMBERS -> navController.navigate(ZiskRoute.ProjectMembers.create(projectId))
                        ProjectTab.TIMELINE -> navController.navigate(ZiskRoute.ProjectTimeline.create(projectId))
                    }
                },
                onOpenGitHub = { navController.navigate(ZiskRoute.ProjectGitHubConnect.create(it)) },
                onOpenSettings = { navController.navigate(ZiskRoute.ProjectSettings.create(it)) }
            )
        }
        composable(
            route = ZiskRoute.ProjectMembers.route,
            arguments = listOf(navArgument("projectId") { defaultValue = "p1" })
        ) {
            val projectId = it.arguments?.getString("projectId").orEmpty()
            ProjectMembersRoute(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onSelectTab = { tab ->
                    when (tab) {
                        ProjectTab.OVERVIEW -> navController.navigate(ZiskRoute.ProjectDetail.create(projectId))
                        ProjectTab.DOCUMENTS -> navController.navigate(ZiskRoute.ProjectDocuments.create(projectId))
                        ProjectTab.MEMBERS -> navController.navigate(ZiskRoute.ProjectMembers.create(projectId))
                        ProjectTab.TIMELINE -> navController.navigate(ZiskRoute.ProjectTimeline.create(projectId))
                    }
                },
                onOpenGitHub = { navController.navigate(ZiskRoute.ProjectGitHubConnect.create(it)) },
                onOpenSettings = { navController.navigate(ZiskRoute.ProjectSettings.create(it)) }
            )
        }
        composable(
            route = ZiskRoute.ProjectTimeline.route,
            arguments = listOf(navArgument("projectId") { defaultValue = "p1" })
        ) {
            val projectId = it.arguments?.getString("projectId").orEmpty()
            ProjectTimelineRoute(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onSelectTab = { tab ->
                    when (tab) {
                        ProjectTab.OVERVIEW -> navController.navigate(ZiskRoute.ProjectDetail.create(projectId))
                        ProjectTab.DOCUMENTS -> navController.navigate(ZiskRoute.ProjectDocuments.create(projectId))
                        ProjectTab.MEMBERS -> navController.navigate(ZiskRoute.ProjectMembers.create(projectId))
                        ProjectTab.TIMELINE -> navController.navigate(ZiskRoute.ProjectTimeline.create(projectId))
                    }
                },
                onOpenGitHub = { navController.navigate(ZiskRoute.ProjectGitHubConnect.create(it)) },
                onOpenSettings = { navController.navigate(ZiskRoute.ProjectSettings.create(it)) }
            )
        }
        composable(
            route = ZiskRoute.ProjectSettings.route,
            arguments = listOf(navArgument("projectId") { defaultValue = "p1" })
        ) {
            val projectId = it.arguments?.getString("projectId").orEmpty()
            ProjectSettingsRoute(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onOpenMembers = { navController.navigate(ZiskRoute.ProjectSettingsMembers.create(projectId)) },
                onOpenRepository = { navController.navigate(ZiskRoute.ProjectSettingsRepository.create(projectId)) },
                onOpenMore = { navController.navigate(ZiskRoute.ProjectSettingsMore.create(projectId)) }
            )
        }
        composable(
            route = ZiskRoute.ProjectSettingsMembers.route,
            arguments = listOf(navArgument("projectId") { defaultValue = "p1" })
        ) {
            val projectId = it.arguments?.getString("projectId").orEmpty()
            ProjectSettingsMembersRoute(projectId = projectId, onBack = { navController.popBackStack() })
        }
        composable(
            route = ZiskRoute.ProjectSettingsRepository.route,
            arguments = listOf(navArgument("projectId") { defaultValue = "p1" })
        ) {
            val projectId = it.arguments?.getString("projectId").orEmpty()
            ProjectSettingsRepositoryRoute(projectId = projectId, onBack = { navController.popBackStack() })
        }
        composable(
            route = ZiskRoute.ProjectSettingsMore.route,
            arguments = listOf(navArgument("projectId") { defaultValue = "p1" })
        ) {
            val projectId = it.arguments?.getString("projectId").orEmpty()
            ProjectSettingsMoreRoute(projectId = projectId, onBack = { navController.popBackStack() })
        }
        composable(
            route = ZiskRoute.ProjectGitHubConnect.route,
            arguments = listOf(navArgument("projectId") { defaultValue = "p1" })
        ) {
            val projectId = it.arguments?.getString("projectId").orEmpty()
            ProjectGitHubConnectRoute(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                onLinked = { navController.popBackStack() }
            )
        }
        composable(
            route = ZiskRoute.GroupDetail.route,
            arguments = listOf(navArgument("groupId") { defaultValue = "g1" })
        ) {
            val groupId = it.arguments?.getString("groupId").orEmpty()
            GroupDetailRoute(groupId = groupId, onBack = { navController.popBackStack() })
        }
        composable(
            route = ZiskRoute.CallMock.route,
            arguments = listOf(navArgument("callId") { defaultValue = "call1" })
        ) {
            val callId = it.arguments?.getString("callId").orEmpty()
            CallMockScreen(callId = callId, onBack = { navController.popBackStack() })
        }
        composable(ZiskRoute.Settings.route) {
            SettingsRoute(
                onBack = { navController.popBackStack() },
                onEditProfile = { navController.navigate(ZiskRoute.EditProfile.route) }
            )
        }
        composable(ZiskRoute.EditProfile.route) {
            EditProfileRoute(onBack = { navController.popBackStack() })
        }
    }
}

private data class BottomItem(val route: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
private fun MainShell(rootNavController: NavHostController) {
    val navController = rememberNavController()
    val items = listOf(
        BottomItem(ZiskRoute.Chats.route, "Chats", Icons.Default.Chat),
        BottomItem(ZiskRoute.Status.route, "Status", Icons.Default.Update),
        BottomItem(ZiskRoute.Projects.route, "Projects", Icons.Default.Description),
        BottomItem(ZiskRoute.Calls.route, "Calls", Icons.Default.Call)
    )

    Scaffold(
        bottomBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            NavigationBar {
                items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.label) },
                        label = {
                            Text(
                                text = item.label,
                                maxLines = 1,
                                overflow = TextOverflow.Clip,
                                textAlign = TextAlign.Center,
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        alwaysShowLabel = true,
                        colors = NavigationBarItemDefaults.colors()
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = ZiskRoute.Chats.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(ZiskRoute.Chats.route) {
                ChatsRoute(
                    onOpenChat = { rootNavController.navigate(ZiskRoute.ChatDetail.create(it)) },
                    onNewChat = { rootNavController.navigate(ZiskRoute.NewChat.route) },
                    onOpenSettings = { rootNavController.navigate(ZiskRoute.Settings.route) },
                    onOpenProjects = { navController.navigate(ZiskRoute.Projects.route) }
                )
            }
            composable(ZiskRoute.Status.route) {
                StatusRoute(
                    onOpenStatus = { rootNavController.navigate(ZiskRoute.StatusViewer.create(it)) }
                )
            }
            composable(ZiskRoute.Projects.route) {
                ProjectsRoute(
                    onOpenProject = { rootNavController.navigate(ZiskRoute.ProjectDetail.create(it)) },
                    onCreateProject = { rootNavController.navigate(ZiskRoute.ProjectCreate.route) },
                    onOpenGitHub = { rootNavController.navigate(ZiskRoute.ProjectsGitHubConnect.route) }
                )
            }
            composable(ZiskRoute.Calls.route) {
                CallsRoute(
                    onOpenCall = { rootNavController.navigate(ZiskRoute.CallMock.create(it)) }
                )
            }
        }
    }
}

@Composable
private fun SplashRoute(onFinished: () -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        visible = true
        delay(1700)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background,
                        MaterialTheme.colorScheme.secondaryContainer
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            ZiskChatLogo(modifier = Modifier.size(112.dp))
            AnimatedVisibility(visible = visible) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ZiskChat", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text("Calm conversations, beautifully organized.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
