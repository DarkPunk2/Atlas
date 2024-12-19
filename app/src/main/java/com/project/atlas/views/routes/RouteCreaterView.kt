package com.project.atlas.views.routes


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.atlas.models.Location
import com.project.atlas.models.RouteType
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.viewModels.RouteViewModel
import com.project.atlas.views.vehicles.DropdownSelector


@Composable
fun RouteCreatorView(navController: NavController, ruteViewModel: RouteViewModel) {
    var selectedType by remember { mutableStateOf<RouteType?>(null) }
    val ruteState by ruteViewModel.routeState.observeAsState()
    ruteViewModel.addStart(Location(39.992573, -0.064749,"Castellon"))
    ruteViewModel.addEnd(Location(39.479126, -0.342623,"Valencia"))

    LaunchedEffect(ruteState) {
        ruteState?.let {
            navController.navigate("viewRute")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Create Rute",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedButton(
            onClick = {

            }, modifier = Modifier.fillMaxWidth()
        ) { Text(
            text = ruteViewModel.start.value?.alias ?:"Select start location",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {

            }, modifier = Modifier.fillMaxWidth()
        ) { Text(
            text = ruteViewModel.end.value?.alias ?:"Select end location",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {
                navController.navigate("selectVehicles")
            }, modifier = Modifier.fillMaxWidth()
        ) { Text(
            text = ruteViewModel.vehicleState.value?.alias ?: "Select vehicle",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        }
        Spacer(modifier = Modifier.height(15.dp))

        DropdownSelector(
            label = "Type",
            items = RouteType.entries.toList(),
            selectedItem = selectedType,
            onItemSelected = { selectedType = it }
        )

        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = {
                ruteViewModel.createRute(start = ruteViewModel.start.value,
                    end = ruteViewModel.end.value,
                    vehicle = ruteViewModel.vehicleState.value,
                    routeType = selectedType
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors( containerColor = AtlasGreen )
        ) {
            Text(
                text = "Create",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }
    }
}

