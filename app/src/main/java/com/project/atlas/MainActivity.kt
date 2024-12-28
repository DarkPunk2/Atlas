package com.project.atlas

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import com.project.atlas.services.MapService
import com.project.atlas.viewModels.UserViewModel
import com.project.atlas.ui.theme.AtlasTheme
import com.project.atlas.viewModels.MapViewModel

class MainActivity : ComponentActivity() {
    private lateinit var mapService: MapService
    private lateinit var mapViewModel: MapViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        val userViewModel: UserViewModel by viewModels()

        setContent {
            val permissionLauncher = rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) {
                mapService = MapService(this)
                mapViewModel = MapViewModel(mapService)
                setContent {
                    SetupContent(userViewModel, mapViewModel)
                }
            }
            LaunchedEffect(Unit) {
                permissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
            }
        }
    }

    @Composable
    private fun SetupContent(userViewModel: UserViewModel, mapViewModel: MapViewModel) {
        AtlasTheme(ThemeViewModel.getInstance(application).isDarkTheme.observeAsState(false).value) {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                MyAppNavigation(
                    modifier = Modifier.padding(innerPadding),
                    userViewModel = userViewModel,
                    mapViewModel = mapViewModel
                )
            }
        }
    }
}
