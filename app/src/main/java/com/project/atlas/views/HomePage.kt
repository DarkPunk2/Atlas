package com.project.atlas.views

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.atlas.R
import com.project.atlas.models.AuthState
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.ui.theme.BackgroundBlack
import com.project.atlas.viewModels.MapViewModel
import com.project.atlas.viewModels.UserViewModel
import kotlinx.coroutines.launch


@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel,
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val authState = userViewModel.authState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login")
            is AuthState.Error -> Toast.makeText(context,
                (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    BackHandler {

    }

    Box(modifier = Modifier.fillMaxSize()) {
        OsmdroidMapView(MapViewModel())
        Image(
            painter = painterResource(id = R.drawable.atlas_lettering_black),
            contentDescription = "letterning",
            modifier = Modifier
                .padding(start = 32.dp)
                .absolutePadding(2.dp, 1.dp, 3.dp, 3.dp)
                .size(100.dp),
            colorFilter = ColorFilter.tint(BackgroundBlack)

        )
        ExtendedFloatingActionButton(
            onClick = { navController.navigate("routes") },
            icon = { Icon(
                Icons.Filled.LocationOn,
                "My Routes",
                tint = BackgroundBlack // Cambiar el color del icono a blanco
            )},
                text = {
                Text(text = "My Routes",
                     color = BackgroundBlack
                    )},
            containerColor = AtlasGreen,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 180.dp, end = 16.dp)
        )
        IconButton(
            onClick = {
                scope.launch {
                    drawerState.open()
                }
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 32.dp, end = 16.dp)
        ) {
            Icon(
                Icons.Filled.Settings,
                contentDescription = "Menu",
                tint = BackgroundBlack
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            NavigationMenu(navController, 0)
        }

        if (drawerState.isOpen) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.5f))
                .clickable {
                    scope.launch { drawerState.close() }
                }
            )

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .align(Alignment.CenterEnd)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(56.dp))
                TextButton(onClick = {
                    userViewModel.goChangePage()
                    navController.navigate("changePassword")
                }) {
                    Text("Change Password")
                }
                Spacer(modifier = Modifier.weight(1f))
                HorizontalDivider()

                TextButton(
                    onClick = {
                        userViewModel.logout()
                    }
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.ExitToApp,
                        contentDescription = "LogoutIcon",
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log out")
                }


                TextButton(onClick = {
                    userViewModel.delete()
                }
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = "DeleteIcon",
                        tint = Color.Red
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Delete account", color = Color.Red)
                }
                Spacer(modifier = Modifier.height(56.dp))
            }
        }
    }
}

