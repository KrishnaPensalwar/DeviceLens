package com.example.devicelens

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.navigation.compose.rememberNavController
import com.example.devicelens.presentation.components.LensTopBar
import com.example.devicelens.presentation.navigation.DeviceLensNavHost
import com.example.devicelens.ui.theme.DeviceLensTheme
import com.example.devicelens.ui.theme.LensBackground
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DeviceLensTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = LensBackground,

                ) {
                    DeviceLensApp(modifier = Modifier.padding(it))
                }
            }
        }
    }
}
@Composable
private fun DeviceLensApp(modifier: Modifier) {

    val navController = rememberNavController()

    DeviceLensNavHost(
        navController = navController,
        modifier
    )
}