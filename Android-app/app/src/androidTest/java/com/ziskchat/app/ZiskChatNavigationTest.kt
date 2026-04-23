package com.ziskchat.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ZiskChatNavigationTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun splash_auth_and_main_navigation_work() {
        composeRule.mainClock.autoAdvance = false

        composeRule.onNodeWithText("ZiskChat").assertIsDisplayed()

        composeRule.mainClock.advanceTimeBy(1800)
        composeRule.waitForIdle()

        composeRule.onNodeWithText("Welcome back").assertIsDisplayed()
        composeRule.onNodeWithText("Create Account").performClick()
        composeRule.onNodeWithText("Create account").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Forgot Password?").performClick()
        composeRule.onNodeWithText("Recover access").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Login").performClick()
        composeRule.onNodeWithText("Recent conversations").assertIsDisplayed()

        composeRule.onNodeWithText("Status").performClick()
        composeRule.onNodeWithText("Recent updates").assertIsDisplayed()

        composeRule.onNodeWithText("Communities").performClick()
        composeRule.onNodeWithText("Create new group").assertIsDisplayed()

        composeRule.onNodeWithText("Calls").performClick()
        composeRule.onNodeWithText("Aarav Mehta").assertIsDisplayed()

        composeRule.onNodeWithText("Chats").performClick()
        composeRule.onNodeWithContentDescription("Add").performClick()
        composeRule.onNodeWithText("Start a new chat").assertIsDisplayed()
    }
}
