package com.project.atlas.views.routes

import Calories
import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.atlas.R
import com.project.atlas.models.RouteModel
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.ui.theme.AtlasTheme
import com.project.atlas.viewModels.RouteViewModel
import com.project.atlas.views.NavigationMenu
import kotlinx.coroutines.delay
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListRoute(
    modifier: Modifier,
    navController: NavController,
    routeViewModel: RouteViewModel
) {
    val routeList by routeViewModel.ruteList.observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }
    var showDetails by remember { mutableStateOf<RouteModel?>(null) }

    // Variables para SnackBar
    var showSnackbar by remember { mutableStateOf(false) }
    val snackbarMessage by remember { mutableStateOf("") }
    val snackbarColor by remember { mutableStateOf(AtlasGreen) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        routeViewModel.getRutes()
        isLoading = false
    }

    LaunchedEffect(key1 = routeList) {
        if (routeList.any { it.price == null }) {
            routeViewModel.calculatePricesForRoutesIfNeeded()
        }
    }

    BackHandler {
        navController.navigate("home")
    }

    if (showSnackbar) {
        LaunchedEffect(snackbarHostState) {
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
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        topBar = {
            TopAppBar(
                title = { Text("Route List") },
                actions = {
                    IconButton(onClick = { navController.navigate("createRute") }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Route")
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar {
                NavigationMenu(navController, 3)
            }
        },
        content = { paddingValues ->
            val loadingTimeoutMillis = 5000L  // 5 segundos
            var showLoading by remember { mutableStateOf(true) }

            LaunchedEffect(key1 = routeList) {
                if (routeList.isEmpty()) {
                    delay(loadingTimeoutMillis)
                    showLoading = false
                } else {
                    showLoading = false
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(
                        paddingValues = paddingValues
                    )
            ) {
                if (showLoading) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = AtlasGreen,
                            modifier = Modifier.size(200.dp)
                        )
                        Text(
                            text = "Loading routes",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                } else if (routeList.isEmpty()) {
                    Text(
                        text = "No routes available",
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn(modifier=Modifier.fillMaxHeight()) {
                        items(routeList) { route ->
                            RouteItem(route = route, onClick = {
                                routeViewModel.addRouteState(route)
                                routeViewModel.seeRemove(true)
                                navController.navigate("viewRute")
                            }) {
                                showDetails = route
                            }
                        }
                    }
                }
            }
        }
    )
}

@Composable
fun RouteItem(route: RouteModel, onClick: () -> Unit, function: () -> Unit) {
    AtlasTheme(
        dynamicColor = false,
        isDarkTheme = ThemeViewModel.getInstance(LocalContext.current.applicationContext as Application).isDarkTheme.observeAsState(false).value,
    ) {
        var isFavorite by remember { mutableStateOf(false) }
        val launched = remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            launched.value = true
        }

        AnimatedVisibility(
            visible = launched.value,
            enter = slideInVertically { it / 2 },
            exit = slideOutVertically { it / 2 }
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { onClick() },
                border = BorderStroke(1.dp, AtlasGreen),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Icono representativo del vehículo asociado a la ruta
                    Image(
                        painter = painterResource(
                            id = when (route.vehicle.type.toString()) {
                                "Car" -> R.drawable.car
                                "Bike" -> R.drawable.bike
                                "Cycle" -> R.drawable.cycle
                                "Scooter" -> R.drawable.scooter
                                "Walk" -> R.drawable.walk
                                else -> android.R.drawable.stat_notify_sdcard_usb
                            }
                        ),
                        contentDescription = null,
                        modifier = Modifier.size(48.dp)
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        // Información de la ruta (origen y destino)
                        Text(
                            text = "From: ${route.start.alias}",
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "To: ${route.end.alias}",
                            style = MaterialTheme.typography.titleMedium,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Tipo de ruta y distancia/duración
                        Text(
                            text = "Type: ${route.routeType.getPreference()}",
                            style = MaterialTheme.typography.bodySmall,
                            color = AtlasGreen
                        )
                        val formattedDistance = if (route.distance >= 1000) {
                            String.format(Locale("es", "ES"),"%.1f km", route.distance / 1000.0)
                        } else {
                            "${route.distance.toInt()} m"
                        }

                        val formattedDuration = if (route.duration >= 3600) {
                            String.format(Locale("es", "ES"),"%.1f h", route.duration / 3600.0) // Convertir segundos a horas
                        } else {
                            String.format(Locale("es", "ES"),"%d min", (route.duration / 60).toInt()) // Convertir segundos a minutos
                        }

                        Text(
                            text = "Distance: $formattedDistance | Duration: $formattedDuration",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondary
                        )

                        // Formateo de precio con Locale específico
                        Text(
                            text = "Price: ${route.price?.let {
                                if (route.vehicle.energyType is Calories) {
                                    String.format(Locale("es", "ES"), "%.2f cal", it)
                                } else {
                                    String.format(Locale("es", "ES"), "%.2f €", it)
                                }
                            } ?: "Calculating..."}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }

                    // Botón de favorito
                    IconButton(
                        onClick = { isFavorite = !isFavorite }
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
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


