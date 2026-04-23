package com.ziskchat.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ziskchat.app.navigation.ZiskChatNavHost
import com.ziskchat.app.ui.theme.ZiskChatAppTheme
import com.ziskchat.app.ui.theme.ZiskChatTheme

@Composable
fun ZiskChatApp() {
    ZiskChatAppTheme {
        Surface(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(ZiskChatTheme.extendedColors.appBackground)
            ) {
                ZiskChatNavHost()
            }
        }
    }
}
