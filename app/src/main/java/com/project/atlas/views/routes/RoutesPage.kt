package com.project.atlas.views.vehicles

import Diesel
import Electricity
import Petrol98
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.atlas.interfaces.EnergyType
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.R
import com.project.atlas.models.RouteModel
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.viewModels.RouteViewModel
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun listRoute(
    modifier: Modifier,
    navController: NavController,
    routeViewModel: RouteViewModel
) {
    val routeList by routeViewModel.ruteList.observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }
    var showDetails by remember { mutableStateOf<RouteModel?>(null) }
    var showAddForm by remember { mutableStateOf(false) }

    // Variables para SnackBar
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarColor by remember { mutableStateOf(AtlasGreen) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        routeViewModel.getRutes()
        isLoading = false
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
                title = { Text("Route List") },
                actions = {
                    IconButton(onClick = { showAddForm = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Route")
                    }
                }
            )
        },
        content = { paddingValues ->
            val loadingTimeoutMillis = 20000L // 20 segundos
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
                    .padding(paddingValues)
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
                    LazyColumn {
                        items(routeList) { route ->
                            RouteItem(rute = route) {
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
fun AddRouteDialog(vehicleList: List<VehicleModel>,
                     onDismiss: () -> Unit,
                     onConfirm: (alias: String, type: VehicleType, energyType: EnergyType, consumption: String) -> Unit
) {
    var alias by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<VehicleType?>(null) }
    var selectedEnergyType by remember { mutableStateOf<EnergyType?>(null) }
    var consumption by remember { mutableStateOf("") }

    val energyOptions = when (selectedType?.name) {
        "Car", "Bike" -> listOf(Petrol95(), Petrol98(), Diesel(), Electricity())
        "Scooter" -> listOf(Electricity())
        else -> emptyList()
    }

    var aliasError by remember { mutableStateOf(false) }
    var energyError by remember { mutableStateOf(false) }
    var consumptionError by remember { mutableStateOf(false) }

    // Validar si la energía seleccionada está permitida
    LaunchedEffect(selectedType) {
        if (selectedEnergyType != null && energyOptions.none { it.typeName == selectedEnergyType?.typeName }) {
            selectedEnergyType = null
        }
    }

    val isValid = alias.isNotBlank() &&
            selectedType != null &&
            selectedEnergyType != null &&
            consumption.toDoubleOrNull()?.let { it > 0 } == true

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Add Vehicle") },
        text = {
            Column {
                // Alias
                TextField(
                    value = alias,
                    onValueChange = {
                        alias = it.replace("\n", "")
                        aliasError = it.isBlank() || vehicleList.any { it.alias == alias }
                    },
                    label = { Text("Alias") },
                    isError = aliasError,
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Done))
                if (aliasError) {
                    Text(
                        text = if (alias!!.isBlank()) "Alias cannot be blank" else "Alias already exists",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Tipo de Vehículo
                DropdownSelector(
                    label = "Type",
                    items = listOf(VehicleType.Car, VehicleType.Bike, VehicleType.Scooter) /*VehicleType.entries*/,
                    selectedItem = selectedType,
                    onItemSelected = { selectedType = it }
                )

                // Tipo de Energía
                DropdownSelector(
                    label = "Energy type",
                    items = energyOptions,
                    selectedItem = selectedEnergyType,
                    onItemSelected = {
                        selectedEnergyType = it
                        energyError = it == null
                    }
                )
                if (energyError) {
                    Text("Select a valid energy type", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                // Consumo
                TextField(
                    value = consumption,
                    onValueChange = {
                        consumption = it.replace(",", ".")
                        consumptionError = it.toDoubleOrNull()?.let { it <= 0 } ?: true
                    },
                    label = { Text("Consumption ${selectedEnergyType?.magnitude ?: "not selected"}") },
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done),
                    isError = consumptionError
                )
                if (consumptionError) {
                    Text("Consumption must be greater than 0", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onConfirm(alias, selectedType!!, selectedEnergyType!!, consumption)
                    }
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isValid) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                Text("Add", color = Color.Black)
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun EditRouteDialog(
    vehicle: VehicleModel,
    vehicleList: List<VehicleModel>,
    onDismiss: () -> Unit,
    onConfirm: (alias: String, type: VehicleType, energyType: EnergyType, consumption: Double) -> Unit
) {
    var alias by remember { mutableStateOf(vehicle.alias) }
    var selectedType by remember { mutableStateOf(vehicle.type) }
    var selectedEnergyType by remember { mutableStateOf(vehicle.energyType) }
    var consumption by remember { mutableStateOf(vehicle.consumption.toString()) }

    val energyOptions = when (selectedType?.name) {
        "Car", "Bike" -> listOf(Petrol95(), Petrol98(), Diesel(), Electricity())
        "Scooter" -> listOf(Electricity())
        else -> emptyList()
    }

    var aliasError by remember { mutableStateOf(false) }
    var energyError by remember { mutableStateOf(false) }
    var consumptionError by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }

    // Validar si la energía seleccionada está permitida
    LaunchedEffect(selectedType) {
        if (selectedEnergyType != null && energyOptions.none { it.typeName == selectedEnergyType?.typeName }) {
            selectedEnergyType = null
        }
    }

    val isValid = alias!!.isNotBlank() &&
            selectedType != null &&
            selectedEnergyType != null &&
            consumption.toDoubleOrNull()?.let { it > 0 } == true &&
            (alias == vehicle.alias || vehicleList.none { it.alias == alias })
            && !vehicle.equals(VehicleModel(alias, selectedType,selectedEnergyType, consumption.toDouble()))

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Edit Vehicle") },
        text = {
            Column {
                // Alias
                TextField(
                    value = alias!!,
                    onValueChange = {
                        alias = it
                        aliasError = it.isBlank() || (it != vehicle.alias && vehicleList.any { vehicle -> vehicle.alias == it })
                    },
                    label = { Text("Alias") },
                    isError = aliasError
                )
                if (aliasError) {
                    Text(
                        text = if (alias!!.isBlank()) "Alias cannot be blank" else "Alias already exists",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Tipo de Vehículo
                DropdownSelector(
                    label = "Type",
                    items = listOf(VehicleType.Car, VehicleType.Bike, VehicleType.Scooter),
                    selectedItem = selectedType,
                    onItemSelected = { selectedType = it }
                )

                // Tipo de Energía
                DropdownSelector(
                    label =  "Energy type",
                    items = energyOptions,
                    selectedItem = selectedEnergyType,
                    onItemSelected = {
                        selectedEnergyType = it
                        energyError = it == null
                    }
                )
                if (energyError) {
                    Text(
                        text ="Seleccione un tipo de energía válido",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Consumo
                TextField(
                    value = consumption,
                    onValueChange = {
                        consumption = it
                        consumptionError = it.toDoubleOrNull()?.let { it <= 0 } ?: true
                    },
                    label = { Text("Consumption ${selectedEnergyType?.magnitude ?: "not selected"}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = consumptionError
                )
                if (consumptionError) {
                    Text(
                        text = "Consumption must be greater than 0",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        showConfirmationDialog = true
                    }
                },
                enabled = isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isValid) MaterialTheme.colorScheme.primary else Color.Gray
                )
            ) {
                Text("Save changes")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cancel")
            }
        }
    )

    // Dialogo de confirmación para la actualización
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirm Update") },
            text = { Text("Are you sure you want to update the vehicle details?") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(alias!!, selectedType!!, selectedEnergyType!!, consumption.toDouble())
                        showConfirmationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Yes", color = Color.Black)
                }
            },
            dismissButton = {
                Button(
                    onClick = { showConfirmationDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("No")
                }
            }
        )
    }
}


@Composable
fun RouteItem(rute: RouteModel, onClick: () -> Unit) {
    var isFavorite by remember { mutableStateOf(false) }
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
                containerColor = Color.White
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono representativo del vehículo asociado a la ruta
                Image(
                    painter = painterResource(
                        id = when (rute.vehicle.type.toString()) {
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

                Column(modifier = Modifier.weight(2f)) {
                    // Información de la ruta (origen y destino)
                    Text(
                        text = "From: ${rute.start.alias}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )
                    Text(
                        text = "To: ${rute.end.alias}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Tipo de ruta y distancia/duración
                    Text(
                        text = "Type: ${rute.routeType.getPreference()}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AtlasGreen
                    )
                    val formattedDistance = if (rute.distance >= 1000) {
                        String.format("%.1f km", rute.distance / 1000.0)
                    } else {
                        "${rute.distance.toInt()} m"
                    }

                    val formattedDuration = if (rute.duration >= 3600) {
                        String.format("%.1f h", rute.duration / 3600.0) // Convertir segundos a horas
                    } else {
                        String.format("%d min", (rute.duration / 60).toInt()) // Convertir segundos a minutos
                    }


                    Text(
                        text = "Distance: $formattedDistance | Duration: $formattedDuration",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }

                // Botón de favorito
                IconButton(
                    onClick = { isFavorite = !isFavorite }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) AtlasGreen else Color.Black,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsDialog(
    vehicle: VehicleModel,
    vehicleList: List<VehicleModel>,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (String, VehicleModel) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = bottomSheetState,
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título
            Text(
                text = "Vehicle Details",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Información del vehículo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Alias", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(vehicle.alias ?: "S/N", style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Type", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(vehicle.type.name, style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Energy", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(vehicle.energyType?.typeName ?: "N/A", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Consumo
            Text(
                text = "Consumption",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${vehicle.consumption} ${vehicle.energyType?.magnitude ?: ""}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(24.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Mostrar botones solo si el tipo de vehículo no es Walk ni Cycle
                if (vehicle.type != VehicleType.Walk && vehicle.type != VehicleType.Cycle) {
                    // Botón de actualizar
                    Button(
                        onClick = { showEditDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AtlasGreen)
                    ) {
                        Text("Update", color = Color.Black)
                    }

                    // Botón de eliminar
                    Button(
                        onClick = { showDeleteConfirmation = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete", color = Color.White)
                    }
                } else {

                    Spacer(modifier = Modifier.width(120.dp))
                }
            }
        }
    }

    // Confirmación de eliminación
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirm Deletion") },
            text = { Text("Are you sure you want to delete this vehicle?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(text = "Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "Cancel", color = Color.Black)
                }
            }
        )
    }

    // Dialogo de edición de vehículo
    if (showEditDialog) {
        EditRouteDialog(
            vehicle = vehicle,
            vehicleList = vehicleList,
            onDismiss = { showEditDialog = false },
            onConfirm = { alias, type, energyType, consumption ->
                val updatedVehicle = vehicle.copy(alias = alias, type = type, energyType = energyType, consumption = consumption)
                onUpdate(vehicle.alias!!, updatedVehicle)
                showEditDialog = false
            }
        )
    }
}

