package com.group10.terrace.screen.onboarding

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.*
import com.group10.terrace.viewmodel.MarketplaceViewModel

@Composable
fun PersonalizationScreen(
    onFinishPersonalization: (landSize: Double, location: String, experience: String) -> Unit,
    marketplaceViewModel: MarketplaceViewModel  // tambah parameter ini
) {
    var currentStep by remember { mutableIntStateOf(0) }

    var landSize by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var experience by remember { mutableStateOf("Pemula (Newbie)") }

    Crossfade(targetState = currentStep, label = "Personalization Step") { step ->
        when (step) {
            0 -> IntroScreen(
                onNext = { currentStep = 1 },
                marketplaceViewModel = marketplaceViewModel  // pass ke IntroScreen
            )
            1 -> StepOneScreen(
                landSize = landSize,
                onLandSizeChange = { landSize = it },
                location = location,
                onLocationChange = { location = it },
                onNext = { currentStep = 2 }
            )
            2 -> StepTwoScreen(
                selectedExperience = experience,
                onExperienceChange = { experience = it },
                onSubmit = {
                    val finalLandSize = landSize.toDoubleOrNull() ?: 0.0
                    onFinishPersonalization(finalLandSize, location, experience)
                }
            )
        }
    }
}