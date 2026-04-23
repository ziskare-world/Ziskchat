package com.ziskchat.app.data.mock

import androidx.compose.runtime.mutableStateListOf

interface ChatRepository {
    fun currentUser(): UserUiModel
    fun users(): List<UserUiModel>
    fun chats(): List<ChatThreadUiModel>
    fun chatById(id: String): ChatThreadUiModel?
}

interface StatusRepository {
    fun myStatus(): StatusUiModel
    fun statuses(): List<StatusUiModel>
    fun statusById(id: String): StatusUiModel?
}

interface CommunityRepository {
    fun communities(): List<CommunityUiModel>
    fun communityById(id: String): CommunityUiModel?
}

interface CallRepository {
    fun calls(): List<CallLogUiModel>
    fun callById(id: String): CallLogUiModel?
}

interface SettingsRepository {
    fun profile(): UserUiModel
    fun items(): List<SettingsItemUiModel>
}

interface ProjectRepository {
    fun projects(): List<ProjectUiModel>
    fun projectById(id: String): ProjectUiModel?
    fun addProject(project: ProjectUiModel): String
}

object MockChatRepository : ChatRepository {
    private val me = UserUiModel("me", "You", "@you", "Building calm, connected conversations.", "ME", true)
    private val users = listOf(
        UserUiModel("u1", "Aarav Mehta", "@aarav", "On my way to the design sync.", "AM", true),
        UserUiModel("u2", "Sara Kim", "@sara", "Leaving space for good work.", "SK", false),
        UserUiModel("u3", "Noah Rivera", "@noah", "Coffee, code, community.", "NR", true),
        UserUiModel("u4", "Mina Patel", "@mina", "Capturing moments that matter.", "MP", false),
        UserUiModel("u5", "Team Orbit", "@orbit", "Moving faster together.", "TO", true),
        UserUiModel("g1", "Design Circle", "@design_circle", "Critiques, resources, and weekly UI experiments.", "DC", true),
        UserUiModel("g2", "Product Ops", "@product_ops", "Launch planning, blockers, and release reminders.", "PO", true)
    )

    private val chats = listOf(
        ChatThreadUiModel(
            id = "c1",
            participant = users[0],
            lastMessage = "I shared the updated onboarding mock. Want a quick look?",
            timestamp = "09:41",
            unreadCount = 2,
            isTyping = true,
            messages = listOf(
                MessageUiModel("m1", "u1", "Morning! I tightened the hero spacing.", "09:15", false, DeliveryState.SEEN),
                MessageUiModel("m2", "me", "Looks great already. Did you also revisit dark mode?", "09:18", true, DeliveryState.SEEN),
                MessageUiModel("m3", "u1", "Yes, and the new token scale feels much calmer.", "09:20", false, DeliveryState.SEEN, listOf("fire")),
                MessageUiModel("m4", "me", "Perfect. Send it over and I'll wire the previews.", "09:22", true, DeliveryState.DELIVERED),
                MessageUiModel("m5", "u1", "I shared the updated onboarding mock. Want a quick look?", "09:41", false, DeliveryState.SENT),
                MessageUiModel("m5a", "me", "Yes, open the first flow and let's compare the spacing.", "09:43", true, DeliveryState.SEEN),
                MessageUiModel("m5b", "u1", "The hero title now sits 8dp higher and the CTA group is aligned.", "09:45", false, DeliveryState.DELIVERED),
                MessageUiModel("m5c", "me", "Nice. Did you keep the status ring colors from the last pass?", "09:47", true, DeliveryState.SEEN),
                MessageUiModel("m5d", "u1", "Yes, and I also softened the top bar spacing for chat detail.", "09:49", false, DeliveryState.DELIVERED),
                MessageUiModel("m5e", "u1", "Scroll a bit more in this thread when you test the keyboard, it should feel like WhatsApp now.", "09:52", false, DeliveryState.SENT)
            )
        ),
        ChatThreadUiModel(
            id = "c2",
            participant = users[1],
            lastMessage = "Let's keep the motion restrained and make the CTA clearer.",
            timestamp = "Yesterday",
            unreadCount = 0,
            isTyping = false,
            messages = listOf(
                MessageUiModel("m6", "u2", "The login form feels balanced now.", "Yesterday", false, DeliveryState.SEEN),
                MessageUiModel("m7", "me", "Agreed. I'll soften the elevation and keep the CTA bold.", "Yesterday", true, DeliveryState.SEEN),
                MessageUiModel("m8", "u2", "Let's keep the motion restrained and make the CTA clearer.", "Yesterday", false, DeliveryState.DELIVERED)
            )
        ),
        ChatThreadUiModel(
            id = "c3",
            participant = users[2],
            lastMessage = "Voice note placeholder is in, we can swap the backend later.",
            timestamp = "Yesterday",
            unreadCount = 1,
            isTyping = false,
            messages = listOf(
                MessageUiModel("m9", "me", "Did you wire the attachment rail?", "Yesterday", true, DeliveryState.SEEN),
                MessageUiModel("m10", "u3", "Voice note placeholder is in, we can swap the backend later.", "Yesterday", false, DeliveryState.DELIVERED, listOf("ok"))
            )
        ),
        ChatThreadUiModel(
            id = "c4",
            participant = users[3],
            lastMessage = "Status ring colors feel polished now.",
            timestamp = "Mon",
            unreadCount = 0,
            isTyping = false,
            messages = listOf(
                MessageUiModel("m11", "u4", "Status ring colors feel polished now.", "Mon", false, DeliveryState.DELIVERED)
            )
        ),
        ChatThreadUiModel(
            id = "c5",
            participant = users[4],
            lastMessage = "Let's capture the release notes in the group after lunch.",
            timestamp = "Sun",
            unreadCount = 0,
            isTyping = false,
            messages = listOf(
                MessageUiModel("m12", "u5", "Let's capture the release notes in the group after lunch.", "Sun", false, DeliveryState.DELIVERED)
            )
        ),
        ChatThreadUiModel(
            id = "c6",
            participant = users[5],
            lastMessage = "Shared the accessibility pass for review.",
            timestamp = "09:08",
            unreadCount = 4,
            isTyping = false,
            messages = listOf(
                MessageUiModel("m13", "u2", "Shared the accessibility pass for review.", "08:15", false, reactions = listOf("clap")),
                MessageUiModel("m14", "me", "I'll review it after standup.", "08:20", true, DeliveryState.SEEN),
                MessageUiModel("m15", "u4", "Also dropped a new color study in files.", "08:44", false),
                MessageUiModel("m16", "u2", "Pinned the design checklist for everyone.", "09:08", false, DeliveryState.DELIVERED)
            )
        ),
        ChatThreadUiModel(
            id = "c7",
            participant = users[6],
            lastMessage = "Tomorrow's rollout checklist is ready.",
            timestamp = "Yesterday",
            unreadCount = 0,
            isTyping = false,
            messages = listOf(
                MessageUiModel("m17", "u3", "Tomorrow's rollout checklist is ready.", "Yesterday", false),
                MessageUiModel("m18", "me", "Perfect. Let's pin it in the channel.", "Yesterday", true, DeliveryState.DELIVERED),
                MessageUiModel("m19", "u1", "I'll add the final release notes after lunch.", "Yesterday", false, DeliveryState.DELIVERED)
            )
        )
    )

    override fun currentUser() = me
    override fun users() = users
    override fun chats() = chats
    override fun chatById(id: String) = chats.firstOrNull { it.id == id }
}

object MockStatusRepository : StatusRepository {
    private val myStatus = StatusUiModel(
        id = "s0",
        user = MockChatRepository.currentUser(),
        caption = "Tap to add a new update",
        timeAgo = "Today",
        viewed = false,
        type = StatusType.PHOTO
    )

    private val items = listOf(
        StatusUiModel("s1", MockChatRepository.users()[0], "Launching the refreshed flow in an hour.", "15m ago", false, StatusType.VIDEO),
        StatusUiModel("s2", MockChatRepository.users()[2], "Prototype night. Espresso count: 3.", "1h ago", false, StatusType.TEXT),
        StatusUiModel("s3", MockChatRepository.users()[3], "Moodboard for the community page.", "3h ago", true, StatusType.PHOTO)
    )

    override fun myStatus() = myStatus
    override fun statuses() = items
    override fun statusById(id: String) = (items + myStatus).firstOrNull { it.id == id }
}

object MockCommunityRepository : CommunityRepository {
    private val communities = listOf(
        CommunityUiModel(
            id = "g1",
            title = "Design Circle",
            description = "Critiques, resources, and weekly UI experiments.",
            memberCount = 24,
            activity = "12 new messages",
            participants = MockChatRepository.users().take(4),
            messages = listOf(
                MessageUiModel("g1m1", "u2", "Shared the accessibility pass for review.", "08:15", false, reactions = listOf("clap")),
                MessageUiModel("g1m2", "me", "I'll review it after standup.", "08:20", true, DeliveryState.SEEN),
                MessageUiModel("g1m3", "u4", "Also dropped a new color study in files.", "08:44", false)
            )
        ),
        CommunityUiModel(
            id = "g2",
            title = "Product Ops",
            description = "Launch planning, blockers, and release reminders.",
            memberCount = 18,
            activity = "2 calls scheduled",
            participants = MockChatRepository.users(),
            messages = listOf(
                MessageUiModel("g2m1", "u3", "Tomorrow's rollout checklist is ready.", "Yesterday", false),
                MessageUiModel("g2m2", "me", "Perfect. Let's pin it in the channel.", "Yesterday", true, DeliveryState.DELIVERED)
            )
        )
    )

    override fun communities() = communities
    override fun communityById(id: String) = communities.firstOrNull { it.id == id }
}

object MockCallRepository : CallRepository {
    private val calls = listOf(
        CallLogUiModel("call1", MockChatRepository.users()[0], "Today, 10:20", CallType.OUTGOING, MediaCallType.VIDEO),
        CallLogUiModel("call2", MockChatRepository.users()[2], "Today, 08:05", CallType.MISSED, MediaCallType.VOICE),
        CallLogUiModel("call3", MockChatRepository.users()[1], "Yesterday, 21:10", CallType.INCOMING, MediaCallType.VIDEO),
        CallLogUiModel("call4", MockChatRepository.users()[3], "Mon, 18:44", CallType.OUTGOING, MediaCallType.VOICE)
    )

    override fun calls() = calls
    override fun callById(id: String) = calls.firstOrNull { it.id == id }
}

object MockSettingsRepository : SettingsRepository {
    private val items = listOf(
        SettingsItemUiModel("account", "Account", "Security notifications, email, password", "person"),
        SettingsItemUiModel("privacy", "Privacy", "Last seen, profile, blocked contacts", "lock"),
        SettingsItemUiModel("notifications", "Notifications", "Messages, groups, calls", "notifications"),
        SettingsItemUiModel("appearance", "Appearance", "Theme, wallpaper, app icon", "palette"),
        SettingsItemUiModel("storage", "Storage & data", "Network usage and media quality", "database"),
        SettingsItemUiModel("help", "Help", "FAQ, contact, privacy policy", "help")
    )

    override fun profile() = MockChatRepository.currentUser()
    override fun items() = items
}

object MockProjectRepository : ProjectRepository {
    private val users = MockChatRepository.users()
    private var nextProjectId = 3

    private val sprintDocs = listOf(
        ProjectDocumentUiModel(
            id = "doc1",
            fileName = "Sprint-plan-v4.pdf",
            fileType = "PDF",
            uploadedBy = "Aarav Mehta",
            uploadedTime = "Today, 10:15",
            sizeLabel = "1.8 MB",
            versionLabel = "v4",
            isRestricted = true
        ),
        ProjectDocumentUiModel(
            id = "doc2",
            fileName = "Client-feedback.docx",
            fileType = "DOCX",
            uploadedBy = "Sara Kim",
            uploadedTime = "Yesterday, 18:40",
            sizeLabel = "420 KB",
            versionLabel = "v2",
            isRestricted = true
        )
    )

    private val launchMembers = listOf(
        ProjectMemberUiModel("pm1", "Aarav Mehta", "Product Designer", "feature/launch-ux", "AM", "Finished the final visual polish."),
        ProjectMemberUiModel("pm2", "Sara Kim", "Content Strategist", "feature/release-copy", "SK", "Updated FAQ and launch messaging."),
        ProjectMemberUiModel("pm3", "Noah Rivera", "Android Developer", "feature/project-dashboard", "NR", "Preparing delivery handoff screens."),
        ProjectMemberUiModel("pm4", "You", "Project Admin", "main", "ME", "Reviewing the final checklist and progress.")
    )

    private val portalMembers = listOf(
        ProjectMemberUiModel("pm5", "Noah Rivera", "Frontend Engineer", "feature/progress-timeline", "NR", "Working on the timeline card and graph."),
        ProjectMemberUiModel("pm6", "Mina Patel", "Product Designer", "feature/document-vault", "MP", "Refining cards and restricted-access UI."),
        ProjectMemberUiModel("pm7", "You", "Project Admin", "main", "ME", "Tracking milestones and member roles.")
    )

    private val launchProject = ProjectUiModel(
        id = "p1",
        name = "Launch Readiness",
        teamName = "Team Orbit",
        progress = 74,
        status = "On track",
        startDate = "Apr 12",
        phaseOneDate = "Apr 20",
        phaseTwoDate = "Apr 28",
        finalDeadline = "May 04",
        lastUpdate = "Today, 11:30",
        summary = "Finalize launch tasks, QA notes, approval documents, and stakeholder updates before rollout.",
        members = launchMembers,
        completedItems = listOf(
            "QA checklist reviewed",
            "Final copy approved",
            "Release candidate shared with the client"
        ),
        updates = listOf(
            ProjectMemberUpdateUiModel(
                id = "u1",
                member = users[0],
                status = "Completed design sign-off",
                summary = "Closed the final visual review and exported the updated presentation deck.",
                completedWork = listOf("Hero layout approved", "Dark theme polish done"),
                time = "Today, 11:30",
                documents = listOf(sprintDocs[0])
            ),
            ProjectMemberUpdateUiModel(
                id = "u2",
                member = users[1],
                status = "Client notes merged",
                summary = "Updated the launch messaging and attached the latest client feedback document.",
                completedWork = listOf("CTA copy updated", "FAQ answers revised"),
                time = "Yesterday, 18:40",
                documents = listOf(sprintDocs[1])
            )
        ),
        documents = sprintDocs,
        githubRepoName = "ziskchat/launch-readiness",
        githubConnected = false
    )

    private val portalProject = ProjectUiModel(
        id = "p2",
        name = "Internal Portal Refresh",
        teamName = "Product Ops",
        progress = 48,
        status = "In review",
        startDate = "Apr 05",
        phaseOneDate = "Apr 16",
        phaseTwoDate = "Apr 30",
        finalDeadline = "May 12",
        lastUpdate = "Today, 09:20",
        summary = "Refresh the internal project portal with progress tracking, work logs, and secure team document access.",
        members = portalMembers,
        completedItems = listOf(
            "Team update schema drafted",
            "Mock dashboard states prepared"
        ),
        updates = listOf(
            ProjectMemberUpdateUiModel(
                id = "u3",
                member = users[2],
                status = "Tracking widgets drafted",
                summary = "Built the first mockups for progress breakdown and teammate activity feed.",
                completedWork = listOf("Progress card layout", "Milestone timeline draft"),
                time = "Today, 09:20"
            ),
            ProjectMemberUpdateUiModel(
                id = "u4",
                member = users[3],
                status = "Review pending",
                summary = "Prepared structure for document vault cards and secure access messaging.",
                completedWork = listOf("Document card states", "Restricted access badge"),
                time = "Yesterday, 16:05"
            )
        ),
        documents = listOf(
            ProjectDocumentUiModel(
                id = "doc3",
                fileName = "Portal-structure.pdf",
                fileType = "PDF",
                uploadedBy = "Mina Patel",
                uploadedTime = "Yesterday, 16:05",
                sizeLabel = "2.1 MB",
                versionLabel = "v1",
                isRestricted = true
            )
        ),
        githubRepoName = "ziskchat/internal-portal",
        githubConnected = false
    )

    private val projects = mutableStateListOf(launchProject, portalProject)

    override fun projects() = projects
    override fun projectById(id: String) = projects.firstOrNull { it.id == id }
    override fun addProject(project: ProjectUiModel): String {
        val id = "p${nextProjectId++}"
        projects.add(0, project.copy(id = id))
        return id
    }
}
