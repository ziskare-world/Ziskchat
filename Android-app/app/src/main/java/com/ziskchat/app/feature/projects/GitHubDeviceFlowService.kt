package com.ziskchat.app.feature.projects
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.UnknownHostException
import java.net.URL

private const val GITHUB_CLIENT_ID = "Ov23liKakxnuvqUxkFwh"
private const val GITHUB_USER_AGENT = "ZiskChat-Android"
private val GITHUB_DEVICE_CODE_URLS = listOf(
    "https://github.com/login/device/code",
    "https://www.github.com/login/device/code"
)
private val GITHUB_ACCESS_TOKEN_URLS = listOf(
    "https://github.com/login/oauth/access_token",
    "https://www.github.com/login/oauth/access_token"
)

data class GitHubDeviceCode(
    val deviceCode: String,
    val userCode: String,
    val verificationUri: String,
    val expiresIn: Int,
    val interval: Int
)

data class GitHubTokenResult(
    val accessToken: String,
    val scope: String,
    val tokenType: String
)

data class GitHubRepoSummary(
    val fullName: String,
    val ownerLogin: String,
    val defaultBranch: String
)

data class GitHubRepoDetails(
    val fullName: String,
    val ownerLogin: String,
    val defaultBranch: String,
    val htmlUrl: String,
    val zipballUrl: String,
    val description: String
)

object GitHubDeviceFlowService {

    fun requestDeviceCode(scope: String = "repo read:user"): Result<GitHubDeviceCode> = runCatching {
        retryNetwork {
            val response = postFormWithFallback(
                urls = GITHUB_DEVICE_CODE_URLS,
                params = mapOf(
                    "client_id" to GITHUB_CLIENT_ID,
                    "scope" to scope
                )
            )
            GitHubDeviceCode(
                deviceCode = response.getString("device_code"),
                userCode = response.getString("user_code"),
                verificationUri = response.getString("verification_uri"),
                expiresIn = response.getInt("expires_in"),
                interval = response.getInt("interval")
            )
        }
    }

    fun pollAccessToken(deviceCode: String): Result<GitHubTokenResult> = runCatching {
        retryNetwork {
            val response = postFormWithFallback(
                urls = GITHUB_ACCESS_TOKEN_URLS,
                params = mapOf(
                    "client_id" to GITHUB_CLIENT_ID,
                    "device_code" to deviceCode,
                    "grant_type" to "urn:ietf:params:oauth:grant-type:device_code"
                )
            )

            if (response.has("error")) {
                error(response.getString("error"))
            }

            GitHubTokenResult(
                accessToken = response.getString("access_token"),
                scope = response.optString("scope"),
                tokenType = response.optString("token_type")
            )
        }
    }

    fun fetchCurrentUser(accessToken: String): Result<String> = runCatching {
        retryNetwork {
            val response = getJson(
                url = "https://api.github.com/user",
                accessToken = accessToken
            )
            response.getString("login")
        }
    }

    fun fetchRepository(accessToken: String, owner: String, repo: String): Result<GitHubRepoSummary> = runCatching {
        retryNetwork {
            val response = getJson(
                url = "https://api.github.com/repos/$owner/$repo",
                accessToken = accessToken
            )
            GitHubRepoSummary(
                fullName = response.getString("full_name"),
                ownerLogin = response.getJSONObject("owner").getString("login"),
                defaultBranch = response.getString("default_branch")
            )
        }
    }

    fun fetchRepositoryDetails(accessToken: String, owner: String, repo: String): Result<GitHubRepoDetails> = runCatching {
        retryNetwork {
            val response = getJson(
                url = "https://api.github.com/repos/$owner/$repo",
                accessToken = accessToken
            )
            GitHubRepoDetails(
                fullName = response.getString("full_name"),
                ownerLogin = response.getJSONObject("owner").getString("login"),
                defaultBranch = response.getString("default_branch"),
                htmlUrl = response.getString("html_url"),
                zipballUrl = response.optString("zipball_url"),
                description = response.optString("description")
            )
        }
    }

    fun fetchUserRepositories(accessToken: String): Result<List<GitHubRepoSummary>> = runCatching {
        retryNetwork {
            val response = getJsonArray(
                url = "https://api.github.com/user/repos?sort=updated&per_page=100",
                accessToken = accessToken
            )
            buildList {
                for (index in 0 until response.length()) {
                    val repo = response.getJSONObject(index)
                    add(
                        GitHubRepoSummary(
                            fullName = repo.getString("full_name"),
                            ownerLogin = repo.getJSONObject("owner").getString("login"),
                            defaultBranch = repo.optString("default_branch", "main")
                        )
                    )
                }
            }
        }
    }

    fun fetchBranches(accessToken: String, owner: String, repo: String): Result<List<String>> = runCatching {
        retryNetwork {
            val response = getJsonArray(
                url = "https://api.github.com/repos/$owner/$repo/branches",
                accessToken = accessToken
            )
            buildList {
                for (index in 0 until response.length()) {
                    add(response.getJSONObject(index).getString("name"))
                }
            }
        }
    }

    private inline fun <T> retryNetwork(block: () -> T): T {
        var lastError: IOException? = null
        repeat(4) { attempt ->
            try {
                return block()
            } catch (error: IOException) {
                lastError = error
                if (attempt == 3) {
                    throw if (error is UnknownHostException) {
                        UnknownHostException("Unable to reach GitHub right now. Please retry once and make sure the phone internet is active.")
                    } else {
                        error
                    }
                }
                Thread.sleep((attempt + 1) * 1500L)
            }
        }
        throw (lastError ?: IOException("GitHub request failed"))
    }

    private fun postFormWithFallback(urls: List<String>, params: Map<String, String>): JSONObject {
        var lastUnknownHost: UnknownHostException? = null
        var lastOtherError: Throwable? = null

        urls.forEach { url ->
            try {
                return postForm(url = url, params = params)
            } catch (error: UnknownHostException) {
                lastUnknownHost = error
            } catch (error: Throwable) {
                lastOtherError = error
            }
        }

        lastUnknownHost?.let { throw UnknownHostException("Unable to resolve GitHub. Check the phone network or DNS and try again.") }
        throw (lastOtherError ?: IllegalStateException("GitHub request failed"))
    }

    private fun postForm(url: String, params: Map<String, String>): JSONObject {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            doOutput = true
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/json")
            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
            setRequestProperty("User-Agent", GITHUB_USER_AGENT)
        }

        val formBody = params.entries.joinToString("&") { "${it.key}=${it.value.encodeUrl()}" }
        OutputStreamWriter(connection.outputStream).use { writer ->
            writer.write(formBody)
            writer.flush()
        }

        val raw = readResponse(connection)
        return JSONObject(raw)
    }

    private fun getJson(url: String, accessToken: String): JSONObject {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("Authorization", "Bearer $accessToken")
            setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            setRequestProperty("User-Agent", GITHUB_USER_AGENT)
        }
        val raw = readResponse(connection)
        return JSONObject(raw)
    }

    private fun getJsonArray(url: String, accessToken: String): JSONArray {
        val connection = (URL(url).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15_000
            readTimeout = 15_000
            setRequestProperty("Accept", "application/vnd.github+json")
            setRequestProperty("Authorization", "Bearer $accessToken")
            setRequestProperty("X-GitHub-Api-Version", "2022-11-28")
            setRequestProperty("User-Agent", GITHUB_USER_AGENT)
        }
        val raw = readResponse(connection)
        return JSONArray(raw)
    }

    private fun readResponse(connection: HttpURLConnection): String {
        val stream = if (connection.responseCode in 200..299) connection.inputStream else connection.errorStream
        val body = BufferedReader(InputStreamReader(stream)).use { reader -> reader.readText() }
        if (connection.responseCode !in 200..299) {
            error(body.ifBlank { "GitHub request failed with ${connection.responseCode}" })
        }
        return body
    }

    private fun String.encodeUrl(): String = java.net.URLEncoder.encode(this, "UTF-8")
}
