package com.example.devicelens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Contrast
import androidx.compose.material.icons.outlined.Memory
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.MenuOpen
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.devicelens.presentation.components.clickableWithoutRipple
import com.example.devicelens.presentation.navigation.DeviceLensNavHost
import com.example.devicelens.presentation.navigation.LensSidebar
import com.example.devicelens.presentation.navigation.Routes
import com.example.devicelens.ui.theme.DeviceLensTheme
import com.example.devicelens.ui.theme.LensBackground
import com.example.devicelens.ui.theme.LensOnAccent
import com.example.devicelens.ui.theme.LensPurple
import com.example.devicelens.ui.theme.LensSurface
import com.example.devicelens.ui.theme.LensTextPrimary
import com.example.devicelens.ui.theme.toggleLensTheme
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
                    containerColor = LensBackground
                ) { padding ->
                    DeviceLensApp(Modifier.padding(padding))
                }
            }
        }
    }
}

@Composable
private fun DeviceLensApp(modifier: Modifier) {
    val navController = rememberNavController()
    var sidebarExpanded by rememberSaveable { mutableStateOf(false) }
    val backStack by navController.currentBackStackEntryAsState()
    val route = backStack?.destination?.route

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(LensBackground)
    ) {
        AppHeader(
            expanded = sidebarExpanded,
            onMenuClick = { sidebarExpanded = !sidebarExpanded }
        )
        Box(modifier = Modifier.weight(1f)) {
            DeviceLensNavHost(
                navController = navController,
                modifier = Modifier.fillMaxSize()
            )
            if (sidebarExpanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.28f))
                        .clickableWithoutRipple { sidebarExpanded = false }
                )
                LensSidebar(
                    selectedRoute = route,
                    onSelect = { destination ->
                        navController.navigate(destination.route) {
                            popUpTo(Routes.Dashboard.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                        sidebarExpanded = false
                    },
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(start = 12.dp, top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun AppHeader(
    expanded: Boolean,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (expanded) Icons.Outlined.MenuOpen else Icons.Outlined.Menu,
            contentDescription = "Menu",
            tint = LensTextPrimary,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(LensSurface)
                .clickableWithoutRipple(onMenuClick)
                .padding(9.dp)
        )
        Text(
            text = "DeviceLens",
            color = LensTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp)
        )
        Icon(
            imageVector = Icons.Outlined.Contrast,
            contentDescription = "Toggle theme",
            tint = LensTextPrimary,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(LensSurface)
                .clickableWithoutRipple { toggleLensTheme() }
                .padding(9.dp)
        )
    }
}
