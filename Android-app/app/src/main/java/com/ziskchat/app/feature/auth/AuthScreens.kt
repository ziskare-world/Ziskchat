package com.ziskchat.app.feature.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ziskchat.app.data.mock.UserUiModel
import com.ziskchat.app.ui.components.AppAvatar
import com.ziskchat.app.ui.components.PrimaryTextField
import com.ziskchat.app.ui.components.ZiskChatLogo
import com.ziskchat.app.ui.theme.ZiskChatAppTheme
import com.ziskchat.app.ui.theme.ZiskChatTheme

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    onSignup: () -> Unit,
    onForgotPassword: () -> Unit
) {
    AuthScaffold(title = "Welcome back", subtitle = "Stay close to your people, teams, and ideas.") {
        var contact by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var showPassword by remember { mutableStateOf(false) }

        PrimaryTextField(
            value = contact,
            onValueChange = { contact = it },
            label = "Email or phone",
            trailingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(
                        imageVector = if (showPassword) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = null
                    )
                }
            }
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onForgotPassword) {
                Text("Forgot Password?")
            }
        }
        Button(
            onClick = onLogin,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Login")
        }
        OutlinedButton(
            onClick = onSignup,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Create Account")
        }
        Text(
            text = "Demo-only UI flow. Use any values to continue.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun SignupScreen(
    onSignup: () -> Unit,
    onBack: () -> Unit
) {
    AuthScaffold(
        title = "Create account",
        subtitle = "Set up your profile and start chatting in minutes.",
        onBack = onBack
    ) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AppAvatar(UserUiModel("signup", "You", "@you", "", "YU", true), modifier = Modifier.size(72.dp))
                TextButton(onClick = {}) {
                    Icon(Icons.Default.Image, contentDescription = null)
                    Text(" Upload profile image")
                }
            }
        }
        PrimaryTextField(value = name, onValueChange = { name = it }, label = "Full name")
        Spacer(modifier = Modifier.height(10.dp))
        PrimaryTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            trailingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        PrimaryTextField(
            value = phone,
            onValueChange = { phone = it },
            label = "Phone number",
            trailingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
        )
        Spacer(modifier = Modifier.height(10.dp))
        PasswordField(value = password, onValueChange = { password = it }, label = "Password")
        Spacer(modifier = Modifier.height(10.dp))
        PasswordField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirm password")
        Spacer(modifier = Modifier.height(14.dp))
        Button(
            onClick = onSignup,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Sign up")
        }
    }
}

@Composable
fun ForgotPasswordScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    AuthScaffold(
        title = "Recover access",
        subtitle = "We'll send a one-time code to your email or phone.",
        onBack = onBack
    ) {
        var contact by remember { mutableStateOf("") }
        PrimaryTextField(value = contact, onValueChange = { contact = it }, label = "Email or phone")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Send OTP")
        }
    }
}

@Composable
fun OtpVerificationScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    AuthScaffold(
        title = "Verify code",
        subtitle = "Enter the 6-digit code we just sent.",
        onBack = onBack
    ) {
        var otp by remember { mutableStateOf("") }
        PrimaryTextField(value = otp, onValueChange = { otp = it.take(6) }, label = "OTP code")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Verify")
        }
        TextButton(onClick = {}) {
            Text("Resend code")
        }
    }
}

@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit,
    onDone: () -> Unit
) {
    AuthScaffold(
        title = "Set a new password",
        subtitle = "Choose a strong password you'll remember.",
        onBack = onBack
    ) {
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }
        PasswordField(value = password, onValueChange = { password = it }, label = "New password")
        Spacer(modifier = Modifier.height(12.dp))
        PasswordField(value = confirmPassword, onValueChange = { confirmPassword = it }, label = "Confirm password")
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onDone,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(20.dp)
        ) {
            Text("Reset password")
        }
    }
}

@Composable
private fun PasswordField(value: String, onValueChange: (String) -> Unit, label: String) {
    var visible by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        visualTransformation = if (visible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            IconButton(onClick = { visible = !visible }) {
                Icon(
                    imageVector = if (visible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                )
            }
        }
    )
}

@Composable
private fun AuthScaffold(
    title: String,
    subtitle: String,
    onBack: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            ZiskChatTheme.extendedColors.appBackground,
                            MaterialTheme.colorScheme.background
                        )
                    )
                )
                .padding(padding)
        ) {
            if (onBack != null) {
                IconButton(onClick = onBack, modifier = Modifier.padding(start = 12.dp, top = 12.dp)) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Spacer(modifier = Modifier.height(if (onBack != null) 24.dp else 8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ZiskChatLogo(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(RoundedCornerShape(24.dp))
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = title, style = MaterialTheme.typography.headlineMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.97f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                        content = content
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun LoginPreview() {
    ZiskChatAppTheme {
        LoginScreen({}, {}, {})
    }
}
