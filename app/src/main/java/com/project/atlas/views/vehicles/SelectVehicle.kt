package com.project.atlas.views.vehicles

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
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
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.atlas.R
import com.project.atlas.models.VehicleModel
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.viewModels.VehicleViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectVehicle(
    modifier: Modifier,
    navController: NavController,
    vehicleViewModel: VehicleViewModel
) {
    val vehicleList by vehicleViewModel.vehicleList.observeAsState(emptyList())
    var isLoading by remember { mutableStateOf(true) }
    var showDetails by remember { mutableStateOf<VehicleModel?>(null) }

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
            title = { Text("Select vehicle") },
            actions = {
                IconButton(onClick = { navController.navigate("vehicles")}) {
                    Icon(Icons.Default.Edit, contentDescription = "Go to Vehicle List")
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
                        VehicleSelectItem(vehicle = vehicle) {
                            showDetails = vehicle
                        }
                    }
                }
            }
        }
    }
    )

    showDetails?.let { vehicle ->
        VehicleSelectDetailsDialog(
            vehicle = vehicle,
            onDismiss = { showDetails = null },
            onSelect = {/**/}
        )
    }

}

@Composable
fun VehicleSelectItem(vehicle: VehicleModel, onClick: () -> Unit) {
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
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VehicleSelectDetailsDialog(
    vehicle: VehicleModel,
    onDismiss: () -> Unit,
    onSelect: (VehicleModel) -> Unit // Función para manejar la selección del vehículo
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

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

            // Botón de seleccionar
            Button(
                onClick = { onSelect(vehicle) },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Select", color = Color.White)
            }
        }
    }
}