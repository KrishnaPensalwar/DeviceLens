package com.example.devicelens.presentation.usage

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.devicelens.core.util.PermissionHelper
import com.example.devicelens.presentation.components.LensBodyText
import com.example.devicelens.presentation.components.LensHeroText
import com.example.devicelens.ui.theme.LensBackground
import com.example.devicelens.ui.theme.LensTextPrimary

@Composable
fun UsageAccessScreen() {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(LensBackground)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LensHeroText(text = "Usage Access needed")
        Spacer(modifier = Modifier.height(12.dp))
        LensBodyText(
            text = "To show how long apps were used, Android requires Usage Access. This stays on your phone and is never sent anywhere."
        )
        Spacer(modifier = Modifier.height(20.dp))
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                context.startActivity(
                    PermissionHelper.usageAccessSettingsIntent().addFlags(
                        Intent.FLAG_ACTIVITY_NEW_TASK
                    )
                )
            }
        ) {
            Text("Open Usage Access settings", color = LensTextPrimary)
        }
    }
}
