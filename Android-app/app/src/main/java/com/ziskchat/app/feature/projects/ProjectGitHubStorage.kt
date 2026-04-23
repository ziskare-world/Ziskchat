package com.ziskchat.app.feature.projects

import android.content.Context

data class StoredProjectGitHubSession(
    val accessToken: String,
    val githubLogin: String,
    val repoFullName: String,
    val repoOwner: String,
    val branches: List<String>
)

data class StoredGlobalGitHubSession(
    val accessToken: String,
    val githubLogin: String
)

object ProjectGitHubStorage {
    private const val PREFS_NAME = "project_github_sessions"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun loadSession(context: Context, projectId: String): StoredProjectGitHubSession? {
        val prefix = "${projectId}_"
        val sharedPreferences = prefs(context)
        val token = sharedPreferences.getString("${prefix}token", "").orEmpty()
        if (token.isBlank()) return null

        return StoredProjectGitHubSession(
            accessToken = token,
            githubLogin = sharedPreferences.getString("${prefix}login", "").orEmpty(),
            repoFullName = sharedPreferences.getString("${prefix}repo_full_name", "").orEmpty(),
            repoOwner = sharedPreferences.getString("${prefix}repo_owner", "").orEmpty(),
            branches = sharedPreferences.getString("${prefix}branches", "").orEmpty()
                .split("|")
                .filter { it.isNotBlank() }
        )
    }

    fun saveSession(
        context: Context,
        projectId: String,
        session: StoredProjectGitHubSession
    ) {
        val prefix = "${projectId}_"
        prefs(context).edit()
            .putString("${prefix}token", session.accessToken)
            .putString("${prefix}login", session.githubLogin)
            .putString("${prefix}repo_full_name", session.repoFullName)
            .putString("${prefix}repo_owner", session.repoOwner)
            .putString("${prefix}branches", session.branches.joinToString("|"))
            .apply()
    }

    fun clearSession(context: Context, projectId: String) {
        val prefix = "${projectId}_"
        prefs(context).edit()
            .remove("${prefix}token")
            .remove("${prefix}login")
            .remove("${prefix}repo_full_name")
            .remove("${prefix}repo_owner")
            .remove("${prefix}branches")
            .apply()
    }

    fun loadGlobalSession(context: Context): StoredGlobalGitHubSession? {
        val sharedPreferences = prefs(context)
        val token = sharedPreferences.getString("global_token", "").orEmpty()
        if (token.isBlank()) return null

        return StoredGlobalGitHubSession(
            accessToken = token,
            githubLogin = sharedPreferences.getString("global_login", "").orEmpty()
        )
    }

    fun saveGlobalSession(context: Context, session: StoredGlobalGitHubSession) {
        prefs(context).edit()
            .putString("global_token", session.accessToken)
            .putString("global_login", session.githubLogin)
            .apply()
    }

    fun clearGlobalSession(context: Context) {
        prefs(context).edit()
            .remove("global_token")
            .remove("global_login")
            .apply()
    }
}
