package com.group10.terrace.screen.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.ui.components.CustomTextField
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.AuthState
import com.group10.terrace.viewmodel.AuthViewModel

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            viewModel.resetAuthState()
            onSignUpSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = PrimaryGradient)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .align(Alignment.BottomCenter)
                .clip(RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp))
                .background(Color(0xFFF3E5E5))
                .padding(32.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Sign Up",
                    style = Typography.displayLarge.copy(fontSize = 36.sp),
                    color = Green600,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = TextAlign.Start
                )

                CustomTextField(
                    value = name,
                    onValueChange = { name = it },
                    hint = "Nama",
                    iconRes = R.drawable.ic_person
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = email,
                    onValueChange = { email = it },
                    hint = "Email",
                    iconRes = R.drawable.ic_gmail1
                )
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = password,
                    onValueChange = { password = it },
                    hint = "Password",
                    iconRes = R.drawable.ic_lock,
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = { viewModel.registerUser(email, password, name) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(4.dp, RoundedCornerShape(100.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Green600),
                    enabled = authState !is AuthState.Loading
                ) {
                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(
                            color = Neutral50,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        Text("Sign Up", style = Typography.labelLarge, color = Neutral50)
                    }
                }

                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (authState as AuthState.Error).message,
                        style = Typography.labelMedium,
                        color = Red500
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "Back to login",
                    style = Typography.labelLarge.copy(fontSize = 13.sp),
                    color = Green600,
                    modifier = Modifier.clickable { onNavigateToLogin() }
                )
            }
        }
    }
}