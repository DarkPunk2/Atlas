package com.project.atlas.views.locations

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.project.atlas.models.Location
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.ui.theme.AtlasTheme
import com.project.atlas.ui.theme.SubtittleGrey
import com.project.atlas.viewModels.LocationsViewModel
import com.project.atlas.viewModels.RouteViewModel
import com.project.atlas.views.NavigationMenu

@Composable
fun LocationCard(
    alias: String,
    coords: String,
    favorite: Boolean,
    onClick: () -> Unit
) {
    AtlasTheme(dynamicColor = false) {
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
                    .padding(8.dp) // Match VehicleItem padding
                    .clickable { onClick() },
                border = BorderStroke(2.dp, AtlasGreen), // Match VehicleItem border
                shape = RoundedCornerShape(16.dp), // Match VehicleItem shape
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Add a placeholder for a potential icon (if applicable)
                    // Spacer(modifier = Modifier.size(48.dp))

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(
                        modifier = Modifier.weight(2f) // Give more weight to the text column
                    ) {
                        Text(
                            text = alias,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onBackground

                        )
                        Text(
                            text = coords,
                            maxLines = 1,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Normal,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    IconButton(
                        onClick = {
                            /* TODO */
                        }
                    ) {
                        val isFavorite = false
                        Icon(
                            imageVector = if (favorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Go Back",
                            tint = if (isFavorite) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSecondary,
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

    //Cards
    val showAddLocation = remember { mutableStateOf(false) }
    val showActionCard = remember { mutableStateOf(false) }
    val showEditCard = remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.popBackStack()
                        }
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
                                alias = location.alias,
                                coords = "(${location.lat}, ${location.lon})",
                                favorite = false,
                                onClick = {
                                    selectedLocation.value = location
                                    showActionCard.value = true
                                    Log.d("locations", "correcto")
                                }
                            )
                        }
                    }
                }
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    NavigationMenu(navController, 1 )
                }
            }
        },
    )

    if (showAddLocation.value) {
        SearchByToponymView(onDismiss = { showAddLocation.value = false }, viewModel)
    }

    selectedLocation.value?.let { location ->
        ActionLocationView(
            onDismiss = {
                showActionCard.value = false
                selectedLocation.value = null
            },
            viewModel,
            location,
            routeViewModel,
            navController
        )
    }

    /*if (showEditCard.value) {
        selectedLocation.value?.let { location ->
            EditLocationView(
                onBack = {
                    showEditCard.value = false
                    selectedLocation.value = null
                },
                viewModel,
                location
            )
        }
    }*/
}