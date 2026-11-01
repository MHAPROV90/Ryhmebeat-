package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.Surface
import com.example.ui.MainAppScreen
import com.example.ui.RhymeBeatViewModel
import com.example.ui.theme.RhymeBeatTheme
import com.example.ui.theme.StudioDarkBg

class MainActivity : ComponentActivity() {

    private val viewModel: RhymeBeatViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // STEP 1: Configure App Check with launch intent before any UI or Firebase operations
        configureAppCheck(this, intent)

        // STEP 2: Edge-to-Edge full-bleed display
        enableEdgeToEdge()

        // STEP 3: Compose UI initialization
        setContent {
            RhymeBeatTheme {
                Surface(color = StudioDarkBg) {
                    MainAppScreen(viewModel = viewModel)
                }
            }
        }
    }
}
