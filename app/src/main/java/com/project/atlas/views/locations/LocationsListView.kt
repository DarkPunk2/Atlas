package com.project.atlas.views.locations

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.atlas.models.Location
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.ui.theme.AtlasTheme
import com.project.atlas.viewModels.LocationsViewModel
import com.project.atlas.viewModels.RouteViewModel
import com.project.atlas.views.NavigationMenu

@SuppressLint("DefaultLocale")
@Composable
fun LocationCard(
    location: Location,
    onFavourite: () -> Unit,
    onClick: () -> Unit
) {
    AtlasTheme(
        dynamicColor = false,
        isDarkTheme = ThemeViewModel.getInstance(LocalContext.current.applicationContext as Application)
            .isDarkTheme.observeAsState(false).value
    ) {
        val launched = remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            launched.value = true
        }
        AnimatedVisibility(
            visible = launched.value,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onClick() },
                border = BorderStroke(2.dp, AtlasGreen),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(
                        modifier = Modifier.weight(2f)
                    ) {
                        Text(
                            text = location.alias,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "(${String.format("%.4f", location.lat)}, ${String.format("%.4f", location.lon)})",
                            maxLines = 1,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    IconButton(
                        onClick = {
                            onFavourite()
                        }
                    ) {
                        Icon(
                            imageVector = if (location.isFavourite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Go Back",
                            tint = if (location.isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationsListView(
    navController: NavController,
    routeViewModel: RouteViewModel,
) {
    val viewModel: LocationsViewModel = viewModel()
    val locations = remember { mutableStateOf(viewModel.getAllLocations()) }
    val selectedLocation = remember { mutableStateOf<Location?>(null) }
    val showAddLocation = remember { mutableStateOf(false) }
    val showActionCard = remember { mutableStateOf(false) }

    //Variables SnackBar
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarColor by remember { mutableStateOf(AtlasGreen) }
    val snackbarHostState = remember { SnackbarHostState() }

    if (showSnackbar) {
        LaunchedEffect(snackbarHostState, snackbarMessage) {
            snackbarHostState.currentSnackbarData?.dismiss()
            snackbarHostState.showSnackbar(snackbarMessage)
            showSnackbar = false
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = snackbarColor,
                    contentColor = Color.Black,
                    shape = RoundedCornerShape(16.dp),
                    modifier =  Modifier
                        .padding(bottom = 76.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Go Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                title = { Text("Saved Locations") },
                actions = {
                    IconButton(onClick = { showAddLocation.value = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Location")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                NavigationMenu(navController, 1)
            }
        },
        content = { paddingValues ->
            Box {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxHeight()
                            .verticalScroll(rememberScrollState())
                    ) {
                        locations.value.forEach { location ->
                            LocationCard(
                                location,
                                onFavourite = {
                                    viewModel.changeFavourite(location)
                                    snackbarMessage = if(location.isFavourite) "${location.alias} is now set as a favourite" else "Vehicle ${location.alias} is now unset as a favourite"
                                    snackbarColor = AtlasGreen
                                    showSnackbar = true
                                },
                                onClick = {
                                    selectedLocation.value = location
                                    showActionCard.value = true
                                    Log.d("locations", "correcto")
                                }
                            )
                        }
                    }
                }
            }
        },
    )

    if (showAddLocation.value) {
        SearchByToponymView(
            onDismiss = { showAddLocation.value = false },
            onAdd = {
                snackbarMessage = "Location added"
                snackbarColor = AtlasGreen
                showSnackbar = true
            },
            viewModel)
    }

    selectedLocation.value?.let { location ->
        ActionLocationView(
            onDismiss = {
                showActionCard.value = false
                selectedLocation.value = null
            },
            onEdit = {
                snackbarMessage = "Location updated"
                snackbarColor = AtlasGreen
                showSnackbar = true
            },
            onDelete = {
                snackbarMessage = "${location.alias} has been removed"
                snackbarColor = AtlasGreen
                showSnackbar = true
            },
            viewModel,
            location,
            routeViewModel,
            navController
        )
    }
}
