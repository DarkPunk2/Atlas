package com.project.atlas.views

import android.app.Application
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.navigation.NavController
import com.project.atlas.R
import com.project.atlas.models.AuthState
import com.project.atlas.models.RouteType
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.ui.theme.BackgroundBlack
import com.project.atlas.viewModels.MapViewModel
import com.project.atlas.viewModels.RouteViewModel
import com.project.atlas.viewModels.UserViewModel
import kotlinx.coroutines.launch


@Composable
fun HomePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    userViewModel: UserViewModel,
    routeViewModel: RouteViewModel,
    mapViewModel: MapViewModel
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val authState = userViewModel.authState.observeAsState()
    val context = LocalContext.current
    val themeViewModel = ThemeViewModel.getInstance(context.applicationContext as Application)
    var selectedType by remember { mutableStateOf<RouteType?>(routeViewModel.routeTypeState.value) }
    var showConfirmationDelete by remember { mutableStateOf(false) }

    LaunchedEffect(authState.value) {
        when (authState.value) {
            is AuthState.Unauthenticated -> navController.navigate("login")
            is AuthState.Error -> Toast.makeText(context, (authState.value as AuthState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }

    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(250.dp)
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
                        TextButton(onClick = {
                            themeViewModel.toggleTheme()
                        }) {
                            Text("Toggle Theme")
                        }
                        RouteTypeSelector(
                            items = RouteType.entries.toList(),
                            selectedItem = selectedType,
                            onItemSelected = { selectedType = it
                                routeViewModel.changeDefaultRouteType(it)
                            }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        HorizontalDivider()
                        TextButton(
                            onClick = { userViewModel.logout() }
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ExitToApp,
                                contentDescription = "LogoutIcon"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Log out")
                        }
                        TextButton(onClick = { showConfirmationDelete = true }) {
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
        ) {
            CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                Box(modifier = Modifier.fillMaxSize()) {
                    OsmdroidMapView(mapViewModel)
                    Image(
                        painter = painterResource(id = R.drawable.atlas_lettering_black),
                        contentDescription = "lettering",
                        modifier = Modifier
                            .padding(start = 20.dp)
                            .absolutePadding(2.dp, 6.dp, 3.dp, 3.dp)
                            .size(100.dp),
                        colorFilter = ColorFilter.tint(BackgroundBlack)
                    )
                    ExtendedFloatingActionButton(
                        onClick = { navController.navigate("createRute") },
                        icon = {
                            Icon(
                                Icons.Filled.Add,
                                "Create Route",
                                tint = BackgroundBlack
                            )
                        },
                        text = { Text(text = "Create Route", color = BackgroundBlack) },
                        containerColor = AtlasGreen,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(bottom = 120.dp, end = 16.dp)
                    )
                    IconButton(
                        onClick = {
                            scope.launch { drawerState.open() }
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
                }
            }
        }
    }

    if (showConfirmationDelete) {
        AlertDialog(
            onDismissRequest = { showConfirmationDelete = false },
            title = { Text("Confirm Delete") },
            text = { Text("Are you sure you want to delete your account?") },
            confirmButton = {
                Button(
                    onClick = {
                        userViewModel.delete()
                        showConfirmationDelete = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AtlasGreen)
                ) {
                    Text("Yes", color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDelete = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("No")
                }
            }
        )
    }
}

