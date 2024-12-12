package com.project.atlas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.google.firebase.FirebaseApp
import com.project.atlas.viewModels.UserViewModel
import com.project.atlas.ui.theme.AtlasTheme
import com.project.atlas.viewModels.RuteViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        enableEdgeToEdge()
        val userViewModel: UserViewModel by viewModels()
        setContent {
            AtlasTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MyAppNavigation(modifier = Modifier.padding(innerPadding), userViewModel = userViewModel)
                }
            }
        }
    }
}
