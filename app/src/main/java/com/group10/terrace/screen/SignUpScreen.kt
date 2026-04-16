package com.group10.terrace.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.group10.terrace.R
import com.group10.terrace.ui.components.CustomTextField
import com.group10.terrace.ui.theme.Green600
import com.group10.terrace.ui.theme.Neutral50
import com.group10.terrace.ui.theme.PrimaryGradient
import com.group10.terrace.ui.theme.Typography
import com.group10.terrace.viewmodel.AuthViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onNavigateToLogin: () -> Unit,
    onSignUpSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var landSize by remember { mutableStateOf("") } // Tambahan untuk PRD

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = PrimaryGradient)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f) // Agak lebih tinggi karena formnya banyak
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
                Spacer(modifier = Modifier.height(16.dp))

                CustomTextField(
                    value = landSize,
                    onValueChange = { landSize = it },
                    hint = "Luas Lahan (m2)",
                    iconRes = R.drawable.pohon,
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = {
                        viewModel.registerUser(email, password, name, landSize)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(4.dp, RoundedCornerShape(100.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Green600)
                ) {
                    Text("Sign Up", style = Typography.labelLarge, color = Neutral50)
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