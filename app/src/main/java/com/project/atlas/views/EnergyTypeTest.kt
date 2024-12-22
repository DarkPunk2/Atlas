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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.project.atlas.viewModels.FuelPriceViewModel

// Retrofit API interface






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnergyTypeTest(
    viewModel: FuelPriceViewModel = androidx.lifecycle.viewmodel.compose.viewModel() // Inyecta el ViewModel
) {
    var selectedFuel by remember { mutableStateOf("Gasolina 95") }
    val fuelOptions = listOf("Gasolina 95", "Gasolina 98", "Diesel")
    var latitudInput by remember { mutableStateOf("") }
    var longitudInput by remember { mutableStateOf("") }

    val priceInfo by viewModel.priceInfo.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    val nearestPrice by viewModel.nearestPrice.collectAsState()
    val municipioId by viewModel.municipioId.collectAsState()

    var inputError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Selecciona el tipo de combustible",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Dropdown menu for fuel type selection
        var expanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            TextField(
                value = selectedFuel,
                onValueChange = {},
                readOnly = true,
                label = { Text("Tipo de combustible") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                fuelOptions.forEach { fuelOption ->
                    DropdownMenuItem(
                        text = { Text(fuelOption) },
                        onClick = {
                            selectedFuel = fuelOption
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.padding(16.dp))

        // Input for latitude and longitude
        TextField(
            value = latitudInput,
            onValueChange = { latitudInput = it },
            label = { Text("Ingrese la latitud") },
            isError = inputError,
            modifier = Modifier.fillMaxWidth()
        )

        TextField(
            value = longitudInput,
            onValueChange = { longitudInput = it },
            label = { Text("Ingrese la longitud") },
            isError = inputError,
            modifier = Modifier.fillMaxWidth()
        )

        if (inputError) {
            Text(
                text = "Por favor, ingrese coordenadas válidas",
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.padding(16.dp))

        // Button to fetch nearest fuel price
        Button(onClick = {
            val latitud = latitudInput.toDoubleOrNull()
            val longitud = longitudInput.toDoubleOrNull()

            if (latitud != null && longitud != null) {
                inputError = false
                val idProducto = when (selectedFuel) {
                    "Gasolina 95" -> 1
                    "Gasolina 98" -> 3
                    "Diesel" -> 4
                    else -> 0
                }
                viewModel.fetchFuelData(latitud, longitud, Diesel())

            } else {
                inputError = true

            }
        }) {
            Text(text = "Buscar gasolinera más cercana")
        }

        Spacer(modifier = Modifier.padding(16.dp))

        // Display results or errors
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color.Red,
                style = MaterialTheme.typography.bodyMedium
            )
        } else if (nearestPrice != null) {
            Text(
                text = "Estación más cercana: ${nearestPrice!!.Rótulo}, Precio: ${nearestPrice!!.PrecioProducto}",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}




@Preview
@Composable
fun EnergyTypeTestPreview() {
    EnergyTypeTest()
}
