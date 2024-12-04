package com.project.atlas.views

import Diesel
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.atlas.viewModels.VehicleViewModel
import com.project.atlas.models.VehicleModel
import com.project.atlas.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun listVehicle(
    modifier: Modifier = Modifier,
    navController: NavController,
    vehicleViewModel: VehicleViewModel
) {
    val vehicleList by vehicleViewModel.listViewVehicles().observeAsState(initial = emptyList())
    Image(
            painter = painterResource(id = R.drawable.atlas_t),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize(), // Ocupa toda la pantalla
            contentScale = ContentScale.Crop, // Escala para ocupar todo el espacio
            alpha = 0.0f // Ajusta la transparencia para que no interfiera con el contenido
        )

        Scaffold(
            topBar = {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { navController.navigate("home") }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Volver a Home"
                            )
                        }
                    },
                    title = {
                        Image(
                            painter = painterResource(id = R.drawable.atlas_lettering_black),
                            contentDescription = "Lettering Atlas",
                            modifier = Modifier.height(40.dp).size(100.dp)
                        )
                    },
                    actions = {
                        IconButton(onClick = { /* Implementa el menú desplegable */ }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF00FF66), // Color verde personalizado
                        titleContentColor = Color.White
                    )
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    navController.navigate("addVehicleScreen")
                }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Añadir Vehículo")
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Transparent)
            ) {
                if (vehicleList.isEmpty()) {
                    item {
                        Text(
                            text = "No hay vehículos disponibles",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.Center,
                        )
                    }
                } else {
                    items(vehicleList) { vehicle ->
                        VehicleItem(vehicle = vehicle) {
                            navController.navigate("vehicleDetailScreen/${vehicle.alias}")
                        }
                    }
                }
            }
        }
    }


@Composable
fun VehicleItem(vehicle: VehicleModel, onClick: () -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.DarkGray, // Color de fondo de la tarjeta
            contentColor = Color.White,      // Color del texto
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "${vehicle.alias ?: "N/A"}")
            Text(text = "Type: ${vehicle.type ?: "N/A"}")
            Text(text = "Energy Type: ${vehicle.energyType?.typeName ?: "N/A"}")
            Text(text = "Consumption: ${vehicle.consumption?.toString() ?: "N/A"}")
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun vehicleDetailScreen(
    vehicle: VehicleModel,
    vehicleViewModel: VehicleViewModel
) {
    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { /* Navegar hacia atrás */ }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                title = { Text("Detalle del Vehículo") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        vehicle.let {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                Text(text = "Alias: ${vehicle.alias}")
                Text(text = "Nombre: ${vehicle.type}")
                Text(text = "Modelo: ${vehicle.energyType?.typeName ?: "N/A"}")
                Text(text = "Descripción: ${vehicle.consumption}")
            }
        } ?: Text(
            text = "Vehículo no encontrado",
            modifier = Modifier.fillMaxSize(),
            textAlign = TextAlign.Center
        )
    }
}


@Preview(showBackground = true)
@Composable
fun VehicleViewPreview() {
    val navController = rememberNavController()
    val fakeViewModel = object : VehicleViewModel() {
        override fun listViewVehicles() = MutableLiveData(
            listOf(VehicleModel("PreviewCar", "Coche", Diesel(), 5.0)
            )
        )
    }
    listVehicle(navController = navController, vehicleViewModel = fakeViewModel)
}
