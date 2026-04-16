package com.group10.terrace.ui.screen.auth

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.ui.components.CustomTextField
import com.group10.terrace.ui.theme.*
import com.group10.terrace.viewmodel.AuthViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel,
    onNavigateToSignUp: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = PrimaryGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 80.dp, start = 32.dp, end = 32.dp)
        ) {
            Text(
                text = "Hello!",
                style = Typography.displayLarge.copy(fontSize = 48.sp),
                color = Neutral50
            )
            Text(
                text = "Welcome to Terrace",
                style = Typography.titleLarge.copy(fontWeight = FontWeight.Normal),
                color = Neutral50
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.7f)
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
                    text = "Login",
                    style = Typography.displayLarge.copy(fontSize = 36.sp),
                    color = Green600, // #007A2B
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    textAlign = TextAlign.Start
                )

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

                Text(
                    text = "Forgot Password",
                    style = Typography.labelLarge.copy(fontSize = 13.sp),
                    color = Green600,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 24.dp)
                        .clickable { /* TODO */ },
                    textAlign = TextAlign.End
                )

                Button(
                    onClick = {
                        viewModel.loginUser(email, password)
                         onLoginSuccess()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(4.dp, RoundedCornerShape(100.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Green600)
                ) {
                    Text("Login", style = Typography.labelLarge, color = Neutral50)
                }

                Spacer(modifier = Modifier.weight(1f))


                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = Neutral600)) {
                            append("Don't have an account? ")
                        }
                        withStyle(style = SpanStyle(color = Green600, fontWeight = FontWeight.Bold)) {
                            append("Sign Up")
                        }
                    },
                    style = Typography.labelMedium,
                    modifier = Modifier.clickable { onNavigateToSignUp() }
                )
            }
        }
    }
}

