package com.project.atlas.views

import Diesel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.atlas.models.RouteModel
import com.project.atlas.viewModels.FuelPriceViewModel
import com.project.atlas.viewModels.RouteViewModel
import kotlinx.coroutines.launch

// Retrofit API interface






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnergyTypeTest(
    viewModel: FuelPriceViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    routeViewModel: RouteViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {

    var selectedFuel by remember { mutableStateOf("Gasolina 95") }
    val fuelOptions = listOf("Gasolina 95", "Gasolina 98", "Diesel")
    var latitudInput by remember { mutableStateOf("") }
    var longitudInput by remember { mutableStateOf("") }
    val routeList by routeViewModel.ruteList.observeAsState(emptyList())

    var firstRute by remember { mutableStateOf<RouteModel?>(null) } // Variable para la ruta en posición 0
    var calculatedPrice by remember { mutableStateOf<Double?>(null) } // Resultado del cálculo
    var calculationError by remember { mutableStateOf<String?>(null) } // Error en el cálculo
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        routeViewModel.getRutes() // Llama a la función que carga las rutas
    }

    LaunchedEffect(routeList) { // Se ejecutará cada vez que cambie `routeList`
        if (routeList.isNotEmpty()) {
            firstRute = routeList[0] // Accede a la primera ruta (o la posición que necesites)
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TextView superior para mostrar los datos de la ruta
        Text(
            text = if (firstRute != null) {
                """
                Datos de la ruta:
                Inicio: ${firstRute!!.start}
                Vehículo: ${firstRute!!.vehicle}
                Destino: ${firstRute!!.end}
                Distancia: ${firstRute!!.distance} km
                """.trimIndent()
            } else {
                "Cargando datos de la ruta..."
            },
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(modifier = Modifier.padding(16.dp))

        // Botón para calcular el precio de la ruta
        Button(onClick = {
            firstRute?.let { route ->
                coroutineScope.launch {
                    try {

                        calculatedPrice = viewModel.calculateRoutePrice(route) // Llama a la función suspendida
                        calculationError = null // Resetea cualquier error previo
                    } catch (e: Exception) {
                        calculationError = "Error al calcular el precio: ${e.message}"
                        calculatedPrice = null
                    }
                }
            } ?: run {
                calculationError = "No se encontró una ruta válida."
            }
        }) {
            Text(text = "Calcular Precio de la Ruta")
        }

        Spacer(modifier = Modifier.padding(16.dp))

        // TextView inferior para mostrar el resultado del cálculo o errores
        if (calculationError != null) {
            Text(
                text = calculationError!!,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (calculatedPrice != null) {
            Text(
                text = "Precio estimado de la ruta: ${"%.2f".format(calculatedPrice)} €",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        } else {
            Text(
                text = "Introduce los datos y calcula el precio de la ruta.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}



