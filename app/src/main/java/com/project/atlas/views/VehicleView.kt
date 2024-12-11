package com.project.atlas.views

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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.atlas.interfaces.EnergyType
import com.project.atlas.interfaces.Petrol95
import com.project.atlas.viewModels.VehicleViewModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.models.VehicleType
import com.project.atlas.R
import com.project.atlas.ui.theme.AtlasGreen
import kotlinx.coroutines.delay


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun listVehicle(
    modifier: Modifier,
    navController: NavController,
    vehicleViewModel: VehicleViewModel
) {
    val vehicleList by vehicleViewModel.vehicleList.observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }
    var showDetails by remember { mutableStateOf<VehicleModel?>(null) }
    var showAddForm by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        vehicleViewModel.refreshVehicles()
    }
    isLoading = false

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
                            tint = MaterialTheme.colorScheme.onSurface // Mismo color que el texto por defecto
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
                    .padding(paddingValues) // Aplica el padding respectivo
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
                            VehicleItem(vehicle = vehicle) {
                                showDetails = vehicle
                            }
                        }
                    }
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
                showDetails = null
            },
            onUpdate = { oldAlias,updatedVehicle ->
                vehicleViewModel.update(oldAlias,updatedVehicle)
                showDetails = null
            }
        )
    }

    if (showAddForm) {
        AddVehicleDialog(
            vehicleList= vehicleList,
            onDismiss = { showAddForm = false },
            onConfirm = { alias, type, energyType, consumption ->
                val newVehicle = VehicleModel(alias, type, energyType, consumption.toDouble())
                vehicleViewModel.add(newVehicle)
                showAddForm = false
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
        "Coche", "Moto" -> listOf(Petrol95(), Petrol98(), Diesel(), Electricity())
        "Patinete" -> listOf(Electricity())
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
        title = { Text(text = "Añadir Vehículo") },
        text = {
            Column {
                // Alias
                TextField(
                    value = alias,
                    onValueChange = {
                        alias = it
                        aliasError = it.isBlank() || vehicleList.any { it.alias == alias }
                    },
                    label = { Text("Alias") },
                    isError = aliasError
                )
                if (aliasError) {
                    Text(
                        text = if (alias!!.isBlank()) "El alias no puede estar vacío" else "El alias ya existe",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Tipo de Vehículo
                DropdownSelector(
                    label = "Tipo",
                    items = listOf(VehicleType.Car, VehicleType.Bike, VehicleType.Scooter) /*VehicleType.entries*/,
                    selectedItem = selectedType,
                    onItemSelected = { selectedType = it }
                )

                // Tipo de Energía
                DropdownSelector(
                    label = "Tipo de Energía",
                    items = energyOptions,
                    selectedItem = selectedEnergyType,
                    onItemSelected = {
                        selectedEnergyType = it
                        energyError = it == null
                    }
                )
                if (energyError) {
                    Text("Seleccione un tipo de energía válido", color = Color.Red, style = MaterialTheme.typography.bodySmall)
                }

                // Consumo
                TextField(
                    value = consumption,
                    onValueChange = {
                        consumption = it
                        consumptionError = it.toDoubleOrNull()?.let { it <= 0 } ?: true
                    },
                    label = { Text("Consumo ${selectedEnergyType?.magnitude ?: "no seleccionado"}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = consumptionError
                )
                if (consumptionError) {
                    Text("El consumo debe ser mayor que 0", color = Color.Red, style = MaterialTheme.typography.bodySmall)
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
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cancelar")
            }
        }
    )
}

@Composable
fun EditVehicleDialog(
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
        "Coche", "Moto" -> listOf(Petrol95(), Petrol98(), Diesel(), Electricity())
        "Patinete" -> listOf(Electricity())
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
        title = { Text("Editar Vehículo") },
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
                        text = if (alias!!.isBlank()) "El alias no puede estar vacío" else "El alias ya existe",
                        color = Color.Red,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                // Tipo de Vehículo
                DropdownSelector(
                    label = "Tipo",
                    items = listOf(VehicleType.Car, VehicleType.Bike, VehicleType.Scooter),
                    selectedItem = selectedType,
                    onItemSelected = { selectedType = it }
                )

                // Tipo de Energía
                DropdownSelector(
                    label = "Tipo de Energía",
                    items = energyOptions,
                    selectedItem = selectedEnergyType,
                    onItemSelected = {
                        selectedEnergyType = it
                        energyError = it == null
                    }
                )
                if (energyError) {
                    Text(
                        text = "Seleccione un tipo de energía válido",
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
                    label = { Text("Consumo ${selectedEnergyType?.magnitude ?: "no seleccionado"}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = consumptionError
                )
                if (consumptionError) {
                    Text(
                        text = "El consumo debe ser mayor que 0",
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
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            Button(
                onClick = { onDismiss() },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
            ) {
                Text("Cancelar")
            }
        }
    )

    // Dialogo de confirmación para la actualización
    if (showConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmationDialog = false },
            title = { Text("Confirmar Actualización") },
            text = { Text("¿Está seguro de que desea actualizar los datos del vehículo?") },
            confirmButton = {
                Button(
                    onClick = {
                        onConfirm(alias!!, selectedType!!, selectedEnergyType!!, consumption.toDouble())
                        showConfirmationDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Sí")
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
                Text(text = selectedItem?.toString() ?: "Seleccionar $label", color = Color.Black)
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
fun VehicleItem(vehicle: VehicleModel, onClick: () -> Unit) {
    var isFavorite = false
    val launched = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        launched.value = true
    }
    AnimatedVisibility(
        visible = launched.value,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ){
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
                Image(
                    painter = painterResource(
                        id = when (vehicle.type.name) {
                            "Coche" -> R.drawable.car
                            "Moto" -> R.drawable.bike
                            "Bicicleta" -> R.drawable.cycle
                            "Patinete" -> R.drawable.scooter
                            "Andar" -> R.drawable.walk
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
fun VehicleDetailsDialog(
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
                    Text(vehicle.alias?:"S/N", style = MaterialTheme.typography.bodyMedium)
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
                // Botón de actualizar
                Button(
                    onClick = { showEditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AtlasGreen)
                ) {
                    Text("Actualizar", color = Color.Black)
                }

                // Botón de eliminar
                Button(
                    onClick = { showDeleteConfirmation = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            }
        }
    }

    // Confirmación de eliminación
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Confirmar Eliminación") },
            text = { Text("¿Estás seguro de que deseas eliminar este vehículo?") },
            confirmButton = {
                Button(
                    onClick = {
                    onDelete()
                    showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                    Text(text="Eliminar")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteConfirmation = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text(text="Cancelar", color = Color.Black)
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
            onConfirm = { alias, type, energyType, consumption ->
                val updatedVehicle = vehicle.copy(alias = alias, type = type, energyType = energyType, consumption = consumption)
                onUpdate(vehicle.alias!!, updatedVehicle)
                showEditDialog = false
            }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewListVehicle() {
    val vehicleViewModel = VehicleViewModel() // Aquí puedes crear un ViewModel ficticio o usar uno de prueba
    val navController = rememberNavController()
    val vehicleList = listOf(
        VehicleModel("Car1", VehicleType.Car, Petrol95(), 8.5),
        VehicleModel("Bike1", VehicleType.Bike, Petrol98(), 5.0)
    )
    vehicleViewModel.vehicleList.observeAsState(vehicleList)

    // Datos de ejemplo para vehicleList
    vehicleViewModel.vehicleList.observeAsState(emptyList())

    listVehicle(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        vehicleViewModel = vehicleViewModel
    )
}


