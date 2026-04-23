package com.ziskchat.app.navigation

sealed class ZiskRoute(val route: String) {
    data object Splash : ZiskRoute("splash")
    data object Login : ZiskRoute("login")
    data object Signup : ZiskRoute("signup")
    data object ForgotPassword : ZiskRoute("forgot_password")
    data object OtpVerification : ZiskRoute("otp_verification")
    data object ResetPassword : ZiskRoute("reset_password")
    data object Main : ZiskRoute("main")
    data object Chats : ZiskRoute("chats")
    data object Status : ZiskRoute("status")
    data object Communities : ZiskRoute("communities")
    data object Projects : ZiskRoute("projects")
    data object Calls : ZiskRoute("calls")
    data object NewChat : ZiskRoute("new_chat")
    data object ChatDetail : ZiskRoute("chat/{chatId}") {
        fun create(chatId: String) = "chat/$chatId"
    }
    data object StatusViewer : ZiskRoute("status/{statusId}") {
        fun create(statusId: String) = "status/$statusId"
    }
    data object GroupDetail : ZiskRoute("group/{groupId}") {
        fun create(groupId: String) = "group/$groupId"
    }
    data object CreateGroup : ZiskRoute("create_group")
    data object ProjectDetail : ZiskRoute("project/{projectId}") {
        fun create(projectId: String) = "project/$projectId"
    }
    data object ProjectCreate : ZiskRoute("project_create")
    data object ProjectsGitHubConnect : ZiskRoute("projects_github_connect")
    data object ProjectDocuments : ZiskRoute("project/{projectId}/documents") {
        fun create(projectId: String) = "project/$projectId/documents"
    }
    data object ProjectMembers : ZiskRoute("project/{projectId}/members") {
        fun create(projectId: String) = "project/$projectId/members"
    }
    data object ProjectTimeline : ZiskRoute("project/{projectId}/timeline") {
        fun create(projectId: String) = "project/$projectId/timeline"
    }
    data object ProjectSettings : ZiskRoute("project/{projectId}/settings") {
        fun create(projectId: String) = "project/$projectId/settings"
    }
    data object ProjectSettingsMembers : ZiskRoute("project/{projectId}/settings/members") {
        fun create(projectId: String) = "project/$projectId/settings/members"
    }
    data object ProjectSettingsRepository : ZiskRoute("project/{projectId}/settings/repository") {
        fun create(projectId: String) = "project/$projectId/settings/repository"
    }
    data object ProjectSettingsMore : ZiskRoute("project/{projectId}/settings/more") {
        fun create(projectId: String) = "project/$projectId/settings/more"
    }
    data object ProjectGitHubConnect : ZiskRoute("project/{projectId}/github") {
        fun create(projectId: String) = "project/$projectId/github"
    }
    data object CallMock : ZiskRoute("call/{callId}") {
        fun create(callId: String) = "call/$callId"
    }
    data object Settings : ZiskRoute("settings")
    data object EditProfile : ZiskRoute("edit_profile")
}
