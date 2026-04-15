package com.group10.terrace.screen

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.group10.terrace.viewmodel.AuthViewModel

@Composable
fun LoginScreen(authViewModel: AuthViewModel = viewModel()) {
    Button(onClick = {
        authViewModel.testRegister()
    }) {
        Text("Test Register & Save Data")
    }
}