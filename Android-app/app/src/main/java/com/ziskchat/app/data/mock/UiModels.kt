package com.ziskchat.app.data.mock

enum class DeliveryState { SENT, DELIVERED, SEEN }

enum class CallType { INCOMING, OUTGOING, MISSED }

enum class MediaCallType { VOICE, VIDEO }

enum class StatusType { PHOTO, VIDEO, TEXT }

data class UserUiModel(
    val id: String,
    val name: String,
    val handle: String,
    val about: String,
    val avatarSeed: String,
    val isOnline: Boolean
)

data class MessageUiModel(
    val id: String,
    val senderId: String,
    val text: String,
    val timestamp: String,
    val isMine: Boolean,
    val deliveryState: DeliveryState = DeliveryState.DELIVERED,
    val reactions: List<String> = emptyList()
)

data class ChatThreadUiModel(
    val id: String,
    val participant: UserUiModel,
    val lastMessage: String,
    val timestamp: String,
    val unreadCount: Int,
    val isTyping: Boolean,
    val messages: List<MessageUiModel>
)

data class StatusUiModel(
    val id: String,
    val user: UserUiModel,
    val caption: String,
    val timeAgo: String,
    val viewed: Boolean,
    val type: StatusType
)

data class CallLogUiModel(
    val id: String,
    val user: UserUiModel,
    val time: String,
    val type: CallType,
    val mediaType: MediaCallType
)

data class CommunityUiModel(
    val id: String,
    val title: String,
    val description: String,
    val memberCount: Int,
    val activity: String,
    val participants: List<UserUiModel>,
    val messages: List<MessageUiModel>
)

data class SettingsItemUiModel(
    val id: String,
    val title: String,
    val subtitle: String,
    val iconName: String
)

data class ProjectDocumentUiModel(
    val id: String,
    val fileName: String,
    val fileType: String,
    val uploadedBy: String,
    val uploadedTime: String,
    val sizeLabel: String,
    val versionLabel: String,
    val isRestricted: Boolean
)

data class ProjectMemberUiModel(
    val id: String,
    val name: String,
    val role: String,
    val branchName: String,
    val avatarSeed: String,
    val progressNote: String
)

data class ProjectMemberUpdateUiModel(
    val id: String,
    val member: UserUiModel,
    val status: String,
    val summary: String,
    val completedWork: List<String>,
    val time: String,
    val documents: List<ProjectDocumentUiModel> = emptyList()
)

data class ProjectUiModel(
    val id: String,
    val name: String,
    val teamName: String,
    val progress: Int,
    val status: String,
    val startDate: String,
    val phaseOneDate: String,
    val phaseTwoDate: String,
    val finalDeadline: String,
    val lastUpdate: String,
    val summary: String,
    val members: List<ProjectMemberUiModel>,
    val completedItems: List<String>,
    val updates: List<ProjectMemberUpdateUiModel>,
    val documents: List<ProjectDocumentUiModel>,
    val githubRepoName: String,
    val githubConnected: Boolean
)
