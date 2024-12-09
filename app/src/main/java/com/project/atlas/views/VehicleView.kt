package com.project.atlas.views

import Calories
import Diesel
import Electricity
import Petrol98
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.atlas.Interfaces.EnergyType
import com.project.atlas.Interfaces.Petrol95
import com.project.atlas.Models.AuthState
import com.project.atlas.ViewModels.VehicleViewModel
import com.project.atlas.Models.VehicleModel
import com.project.atlas.Models.VehicleType
import com.project.atlas.R
import com.project.atlas.ui.theme.AtlasGreen
import org.osmdroid.views.overlay.gridlines.LatLonGridlineOverlay.backgroundColor
import org.w3c.dom.Text


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
                title = { Text("Lista de Vehículos") },
                actions = {
                    IconButton(onClick = { showAddForm = true }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir Vehículo")
                    }
                }
            )
        },
        content = { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                if (isLoading) {
                    CircularProgressIndicator(color = AtlasGreen)
                } else if (vehicleList.isEmpty()) {
                    Text(
                        text = "No hay vehículos disponibles",
                        modifier = Modifier.align(Alignment.Center),
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

    // Mostrar ventana de detalles
    showDetails?.let { vehicle ->
        VehicleDetailsDialog(
            vehicle = vehicle,
            onDismiss = { showDetails = null },
            onDelete = {
                vehicleViewModel.delete(vehicle)
                showDetails = null
            },
            onUpdate = { oldAlias,updatedVehicle ->
                try {
                    vehicleViewModel.update(oldAlias,updatedVehicle)
                }catch (e: Exception){
                    println(e.message)
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
                showDetails = null
            }
        )
    }

    // Mostrar formulario para añadir vehículo
    if (showAddForm) {
        AddVehicleDialog(
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
fun AddVehicleDialog(
    onDismiss: () -> Unit,
    onConfirm: (alias: String, type: VehicleType, energyType: EnergyType, consumption: String) -> Unit
) {
    var alias by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf<VehicleType?>(null) }
    var selectedEnergyType by remember { mutableStateOf<EnergyType?>(null) }
    var consumption by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Añadir Vehículo") },
        text = {
            Column {
                TextField(
                    value = alias,
                    onValueChange = { alias = it },
                    label = { Text("Alias") }
                )
                DropdownSelector(
                    label = "Tipo",
                    items = VehicleType.entries,
                    selectedItem = selectedType,
                    onItemSelected = { selectedType = it }
                )
                DropdownSelector(
                    label = "Tipo de Energía",
                    items =  listOf(Petrol95(), Petrol98(), Diesel(), Electricity(), Calories()),
                    selectedItem = selectedEnergyType,
                    onItemSelected = { selectedEnergyType = it }
                )
                TextField(
                    value = consumption,
                    onValueChange = { consumption = it },
                    label = { Text("Consumo ${selectedEnergyType?.magnitude ?: "no seleccionado"}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedType != null && selectedEnergyType != null) {
                        onConfirm(alias, selectedType!!, selectedEnergyType!!, consumption)
                    }
                }
            ) {
                Text("Añadir")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
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
                Text(text = selectedItem?.toString() ?: "Seleccionar $label")
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
            border = BorderStroke(2.dp, Color(0xFF00FF66)), // Borde verde
            shape = RoundedCornerShape(16.dp), // Esquinas redondeadas
            colors = CardDefaults.cardColors(
                containerColor = Color.White // Fondo blanco
            )
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Imagen del vehículo
                Image(
                    painter = painterResource(
                        id = when (vehicle.type.name) {
                            "Coche" -> R.drawable.car // Reemplaza con el recurso correcto
                            "Moto" -> R.drawable.bike
                            "Bicicleta" -> R.drawable.cycle
                            "Patinete" -> R.drawable.scooter
                            "Andar" -> R.drawable.walk
                            else -> android.R.drawable.stat_notify_sdcard_usb
                        }
                    ),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp) // Tamaño de la imagen
                )

                Spacer(modifier = Modifier.width(16.dp))

                // Texto del vehículo
                Text(
                    text = vehicle.alias!!,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(2f),
                    color = Color.Black
                )

                // Icono de favorito
                IconButton(
                    onClick = { isFavorite = !isFavorite }
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isFavorite) AtlasGreen else Color.Black,
                        modifier = Modifier.size(32.dp) // Tamaño del icono
                    )
                }
            }
    }
}
}

@Composable
fun VehicleDetailsDialog(
    vehicle: VehicleModel,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onUpdate: (String,VehicleModel) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text(text = "Detalles del Vehículo") },
        text = {
            Column {
                Text("Alias: ${vehicle.alias}")
                Text("Type: ${vehicle.type}")
                Text("Energy: ${vehicle.energyType?.typeName}")
                Text("Consumption: ${vehicle.consumption} ${vehicle.energyType?.magnitude}")
            }
        },
        confirmButton = {
            Button(onClick = {
                showEditDialog = true
            }) {
                Text("Actualizar")
            }
        },
        dismissButton = {
            Button(onClick = { onDelete() }) {
                Text("Eliminar")
            }
        }
    )

    if (showEditDialog) {
        EditVehicleDialog(
            vehicle = vehicle,
            onDismiss = { showEditDialog = false },
            onConfirm = { alias, type, energyType, consumption ->
                val updatedVehicle = vehicle.copy(alias = alias, type = type, energyType = energyType, consumption = consumption)
                onUpdate(vehicle.alias!!,updatedVehicle)
                showEditDialog = false
            }
        )
    }
}
@Composable
fun EditVehicleDialog(
    vehicle: VehicleModel,
    onDismiss: () -> Unit,
    onConfirm: (alias: String, type: VehicleType, energyType: EnergyType, consumption: Double) -> Unit
) {
    var alias by remember { mutableStateOf(vehicle.alias) }
    var selectedType by remember { mutableStateOf(vehicle.type) }
    var selectedEnergyType by remember { mutableStateOf(vehicle.energyType) }
    var consumption by remember { mutableStateOf(vehicle.consumption.toString()) }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Editar Vehículo") },
        text = {
            Column {
                TextField(
                    value = alias!!,
                    onValueChange = { alias = it },
                    label = { Text("Alias") }
                )

                // Dropdown selector para seleccionar el tipo
                DropdownSelector(
                    label = "Tipo",
                    items = VehicleType.entries,
                    selectedItem = selectedType,
                    onItemSelected = { selectedType = it }
                )

                // Dropdown selector para seleccionar el tipo de energía
                DropdownSelector(
                    label = "Tipo de Energía",
                    items =  listOf(Petrol95(), Petrol98(), Diesel(), Electricity(), Calories()),
                    selectedItem = selectedEnergyType,
                    onItemSelected = { selectedEnergyType = it }
                )

                // Campo para editar el consumo
                TextField(
                    value = consumption,
                    onValueChange = { consumption = it },
                    label = { Text("Consumo ${selectedEnergyType?.magnitude ?: "no seleccionado"}") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (selectedType != null && selectedEnergyType != null && alias!!.isNotBlank()) {
                        onConfirm(alias!!, selectedType, selectedEnergyType!!, consumption.toDouble())
                    }
                }
            ) {
                Text("Guardar Cambios")
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    )
}

