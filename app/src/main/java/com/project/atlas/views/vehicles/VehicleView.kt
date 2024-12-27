package com.project.atlas.views.vehicles

import Diesel
import Electricity
import Petrol98
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.atlas.interfaces.EnergyType
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.viewModels.VehicleViewModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.R
import com.project.atlas.ui.theme.AtlasGold
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.views.NavigationMenu
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun listVehicle(
    modifier: Modifier,
    navController: NavController,
    vehicleViewModel: VehicleViewModel
) {
    val vehicleList by vehicleViewModel.vehicleList.observeAsState(emptyList())
    val defaultVehicle by vehicleViewModel.defaultVehicle.observeAsState(null)
    var isLoading by remember { mutableStateOf(true) }
    var showDetails by remember { mutableStateOf<VehicleModel?>(null) }
    var showAddForm by remember { mutableStateOf(false) }

    //variables SnackBar
    var showSnackbar by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf("") }
    var snackbarColor by remember { mutableStateOf(AtlasGreen) }
    val snackbarHostState = remember { SnackbarHostState() }


    LaunchedEffect(Unit) {
        vehicleViewModel.refreshVehicles()
        vehicleViewModel.refreshDefaultVehicle()
    }
    isLoading = false

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
                title = { Text("Vehicle List") },
                actions = {
                    IconButton(onClick = { showAddForm = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Add Vehicle")
                    }
                }
            )
        },
        content = { paddingValues ->
            val loadingTimeoutMillis = 20000L // 20 segundos
            var showLoading by remember { mutableStateOf(true) }

            LaunchedEffect(key1 = vehicleList) {
                if (vehicleList.isEmpty()) {
                    delay(loadingTimeoutMillis)
                    showLoading = false
                } else {
                    showLoading = false
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize() // Ocupa todo el espacio disponible en la pantalla
                    .padding(
                        start = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        top = paddingValues.calculateTopPadding(),
                        end = paddingValues.calculateStartPadding(LayoutDirection.Ltr),
                        bottom = 0.dp
                    )
            ) {
                if (showLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(), // Asegura que este Box ocupe todo el espacio
                        contentAlignment = Alignment.Center // Centra todo el contenido dentro del Box
                    ) {
                        CircularProgressIndicator(
                            color = AtlasGreen,
                            modifier = Modifier.size(200.dp) // Tamaño del indicador
                        )
                        Text(
                            text = "Loading vehicles",
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier.align(Alignment.Center) // Centra el texto
                        )
                    }
                } else if (vehicleList.isEmpty()) {
                    Text(
                        text = "No vehicles available",
                        modifier = Modifier.align(Alignment.Center), // Centra el texto
                        textAlign = TextAlign.Center
                    )
                } else {
                    LazyColumn {
                        items(vehicleList) { vehicle ->
                            VehicleItem(
                                vehicle = vehicle,
                                onClick = {showDetails = vehicle},
                                onFavourite = {
                                    vehicleViewModel.update(vehicle.alias!!, vehicle)
                                    snackbarMessage = if(vehicle.favourite) "Vehicle ${vehicle.alias} is now set as a favourite" else "Vehicle ${vehicle.alias} is now unset as a favourite"
                                    snackbarColor = AtlasGreen
                                    showSnackbar = true
                                              },
                                default = defaultVehicle?.alias?:"" == vehicle.alias,
                                modifier = Modifier.animateContentSize()
                            )
                        }
                    }
                }
                Box(modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                ) {
                    NavigationMenu(navController, 2)
                }
            }
        }
    )

    showDetails?.let { vehicle ->
        VehicleDetailsDialog(
            vehicle = vehicle,
            vehicleList = vehicleList,
            onDismiss = { showDetails = null },
            onDelete = {
                vehicleViewModel.delete(vehicle)
                snackbarMessage = "Vehicle ${vehicle.alias} deleted sucessfully"
                snackbarColor = Color.Red
                showDetails = null
                showSnackbar = true
            },
            onUpdate = { oldAlias,updatedVehicle ->
                var diff = !oldAlias.equals(updatedVehicle.alias)
                vehicleViewModel.update(oldAlias,updatedVehicle)
                snackbarMessage = if(diff) "Vehicle ${updatedVehicle.alias} (previously ${oldAlias}) updated sucessfully" else "Vehicle ${updatedVehicle.alias} updated sucessfully"
                snackbarColor = AtlasGold
                showDetails = null
                showSnackbar = true
            },
            default = defaultVehicle?.alias?:"" == vehicle.alias,
            onDefaultAdd = {
                vehicleViewModel.setDefaultVehicle(vehicle)
                var message: String = if(defaultVehicle != null)
                            "Vehicle ${vehicle.alias} now set as your default vehicle (previously set ${defaultVehicle?.alias})"
                            else "Vehicle ${vehicle.alias} now set as your default vehicle"
                snackbarMessage = message
                snackbarColor = AtlasGold
                showDetails = null
                showSnackbar = true
                           },
            onDefaultDelete = {
                vehicleViewModel.deleteDefaultVehicle()
                snackbarMessage = "Vehicle ${vehicle.alias} is now unset as your default vehicle"
                snackbarColor = AtlasGold
                showDetails = null
                showSnackbar = true
                              },
            onFavourite = {
                vehicleViewModel.update(vehicle.alias!!, vehicle)
                snackbarMessage = if(vehicle.favourite) "Vehicle ${vehicle.alias} is now set as a favourite" else "Vehicle ${vehicle.alias} is now unset as a favourite"
                snackbarColor = AtlasGreen
                showDetails = null
                showSnackbar = true

            }
        )
    }

    if (showAddForm) {
        AddVehicleDialog(
            vehicleList= vehicleList,
            onDismiss = { showAddForm = false },
            onConfirm = { alias, type, energyType, consumption ->
                val legalAlias = alias.trim()
                val newVehicle = VehicleModel(legalAlias, type, energyType, consumption.toDouble())
                vehicleViewModel.add(newVehicle)
                snackbarMessage = "Vehicle ${legalAlias} added successfully"
                snackbarColor = AtlasGreen
                showAddForm = false
                showSnackbar = true
            }
        )
    }
}


@Composable
fun AddVehicleDialog(vehicleList: List<VehicleModel>,
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
fun EditVehicleDialog(
    vehicle: VehicleModel,
    vehicleList: List<VehicleModel>,
    onDismiss: () -> Unit,
    onConfirm: (alias: String, type: VehicleType, energyType: EnergyType, consumption: Double, favourite:Boolean) -> Unit
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
                        onConfirm(alias!!, selectedType!!, selectedEnergyType!!, consumption.toDouble(), vehicle.favourite)
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
fun <T> DropdownSelector(
    label: String,
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Box(modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.TopStart)) {
            OutlinedButton(
                onClick = { expanded = true }
                ) {
                Text(text = selectedItem?.toString() ?: "Select $label", color = Color.Black)
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(text = item.toString()) },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun VehicleItem(
    vehicle: VehicleModel,
    onClick: () -> Unit,
    onFavourite: () -> Unit,
    default: Boolean,
    modifier: Modifier = Modifier) {
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
            modifier = modifier
                .then(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                        .clickable { onClick() }
                ),
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
                Image(
                    painter = painterResource(
                        id = when (vehicle.type.name) {
                            "Car" -> R.drawable.car
                            "Bike" -> R.drawable.bike
                            "Cycle" -> R.drawable.cycle
                            "Scooter" -> R.drawable.scooter
                            "Walk" -> R.drawable.walk
                            else -> android.R.drawable.stat_notify_sdcard_usb
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Text(
                    text = vehicle.alias!!,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(2f),
                    color = Color.Black
                )

                if (default) {
                    IconButton(
                        onClick = {}
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Default mark",
                            tint = AtlasGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                if (vehicle.type.name != VehicleType.Walk.name && vehicle.type.name != VehicleType.Cycle.name) {
                    IconButton(
                        onClick = {
                            vehicle.toggleFavourite()
                            onFavourite()
                        }
                    ) {
                        Icon(
                            imageVector = if (vehicle.favourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = animateColorAsState(if (vehicle.favourite) AtlasGreen else Color.Black).value,
                            modifier = Modifier.size(32.dp).animateContentSize()
                        )
                    }
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleDetailsDialog(
    vehicle: VehicleModel,
    vehicleList: List<VehicleModel>,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (String, VehicleModel) -> Unit,
    onDefaultAdd: () -> Unit,
    onDefaultDelete: () -> Unit,
    onFavourite: () -> Unit,
    default : Boolean
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showSetDefaultConfirmation by remember { mutableStateOf(false) }

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
            // Header con acciones de estrella y corazón
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { showSetDefaultConfirmation = true }) {
                    Icon(
                        imageVector = if (default) Icons.Filled.Star else Icons.Outlined.StarOutline,
                        contentDescription = "Set as Default",
                        tint = AtlasGold,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Vehicle Details",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f), // Agregamos peso para centrar el texto
                    textAlign = TextAlign.Center // Aseguramos alineación centrada
                )

                if (vehicle.type.name != VehicleType.Walk.name && vehicle.type.name != VehicleType.Cycle.name) {
                    IconButton(
                        onClick = {
                            vehicle.toggleFavourite()
                            onFavourite()
                        }
                    ) {
                        Icon(
                            imageVector = if (vehicle.favourite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = null,
                            tint = if (vehicle.favourite) AtlasGreen else Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp)) // Reserva espacio para el ícono del corazón
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Información del vehículo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Alias",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        vehicle.alias ?: "S/N",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center // Aseguramos el centrado del texto
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Type",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        vehicle.type.name,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Energy",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        vehicle.energyType?.typeName ?: "N/A",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Consumo
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Consumption",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "${vehicle.consumption} ${vehicle.energyType?.magnitude ?: ""}",
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (vehicle.type != VehicleType.Walk && vehicle.type != VehicleType.Cycle) {
                    Button(
                        onClick = { showEditDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AtlasGreen)
                    ) {
                        Text("Update", color = Color.Black)
                    }

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

    // Confirmación de establecer como predeterminado
    if (showSetDefaultConfirmation) {
        AlertDialog(
            onDismissRequest = { showSetDefaultConfirmation = false },
            title = {Text(if(!default) "Set as Default" else "Unset as Default")},
            text = {Text(
                    if(!default) "Are you sure you want to set this vehicle as default?"
                    else "Are you sure you want to unset this vehicle as your default?") },
            confirmButton = {
                Button(
                    onClick = {
                        if(!default) onDefaultAdd() else onDefaultDelete()
                        showSetDefaultConfirmation = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AtlasGold)
                ) {
                    Text(text = "Confirm")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showSetDefaultConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text = "Cancel", color = Color.Black)
                }
            }
        )
    }

    // Dialogo de edición de vehículo
    if (showEditDialog) {
        EditVehicleDialog(
            vehicle = vehicle,
            vehicleList = vehicleList,
            onDismiss = { showEditDialog = false },
            onConfirm = { alias, type, energyType, consumption, favourite ->
                val updatedVehicle = vehicle.copy(alias = alias, type = type, energyType = energyType, consumption = consumption)
                updatedVehicle.favourite = vehicle.favourite
                onUpdate(vehicle.alias!!, updatedVehicle)
                showEditDialog = false
            }
        )
    }
}

