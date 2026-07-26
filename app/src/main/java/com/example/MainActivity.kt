package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.UserProgressEntity
import com.example.ui.components.HeaderAppBar
import com.example.ui.screens.CoursesScreen
import com.example.ui.screens.CtfScreen
import com.example.ui.screens.LabsScreen
import com.example.ui.screens.MentorAiScreen
import com.example.ui.screens.ProfileNotesScreen
import com.example.ui.theme.CyberGreen
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.HackGuardTheme
import com.example.viewmodel.CyberViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: CyberViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            HackGuardTheme {
                val selectedTab by viewModel.selectedTab.collectAsState()
                val userProgress by viewModel.userProgressState.collectAsState()
                val newlyUnlockedBadge by viewModel.newlyUnlockedBadge.collectAsState()
                val progress = userProgress ?: UserProgressEntity()

                newlyUnlockedBadge?.let { badge ->
                    com.example.ui.components.BadgeUnlockDialog(
                        badge = badge,
                        onDismiss = { viewModel.dismissBadgeDialog() }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        HeaderAppBar(
                            progress = progress,
                            selectedTab = selectedTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    },
                    bottomBar = {
                        CyberBottomBar(
                            selectedTab = selectedTab,
                            onTabSelected = { viewModel.selectTab(it) }
                        )
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (selectedTab) {
                            0 -> CoursesScreen(viewModel = viewModel)
                            1 -> LabsScreen(viewModel = viewModel)
                            2 -> CtfScreen(viewModel = viewModel)
                            3 -> MentorAiScreen(viewModel = viewModel)
                            4 -> ProfileNotesScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CyberBottomBar(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    NavigationBar(
        modifier = Modifier
            .background(CyberSurface)
            .border(width = 1.dp, color = CyberSurfaceBorder)
            .windowInsetsPadding(WindowInsets.navigationBars)
            .testTag("cyber_bottom_bar"),
        containerColor = CyberSurface,
        contentColor = Color.White
    ) {
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) },
            label = { Text("Cours", fontSize = 11.sp) },
            icon = { Icon(Icons.Default.School, contentDescription = "Cours") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CyberGreen,
                indicatorColor = CyberGreen,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("tab_courses")
        )

        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) },
            label = { Text("Labs", fontSize = 11.sp) },
            icon = { Icon(Icons.Default.Terminal, contentDescription = "Labs") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CyberGreen,
                indicatorColor = CyberGreen,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("tab_labs")
        )

        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) },
            label = { Text("CTF", fontSize = 11.sp) },
            icon = { Icon(Icons.Default.Flag, contentDescription = "CTF") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CyberGreen,
                indicatorColor = CyberGreen,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("tab_ctf")
        )

        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) },
            label = { Text("Mentor IA", fontSize = 11.sp) },
            icon = { Icon(Icons.Default.SmartToy, contentDescription = "Mentor IA") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CyberGreen,
                indicatorColor = CyberGreen,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("tab_mentor")
        )

        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = { onTabSelected(4) },
            label = { Text("Profil", fontSize = 11.sp) },
            icon = { Icon(Icons.Default.Person, contentDescription = "Profil") },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.Black,
                selectedTextColor = CyberGreen,
                indicatorColor = CyberGreen,
                unselectedIconColor = Color.Gray,
                unselectedTextColor = Color.Gray
            ),
            modifier = Modifier.testTag("tab_profile")
        )
    }
}
