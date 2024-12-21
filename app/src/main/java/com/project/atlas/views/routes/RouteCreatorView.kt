package com.project.atlas.views.routes


import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.atlas.R
import com.project.atlas.models.RouteType
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.viewModels.RouteViewModel
import com.project.atlas.views.vehicles.DropdownSelector


@Composable
fun RouteCreatorView(navController: NavController, routeViewModel: RouteViewModel) {
    var selectedType by remember { mutableStateOf<RouteType?>(null) }
    val ruteState by routeViewModel.routeState.observeAsState()
    val navigateToRuteView by routeViewModel.navigateToRuteView.observeAsState()

    LaunchedEffect(ruteState) {
        if (navigateToRuteView == true){
            routeViewModel.seeAdd(true)
            navController.navigate("viewRute")
        }
    }

    BackHandler {
        routeViewModel.resetValues()
        navController.navigate("routes")
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
                routeViewModel.seeSelectStart(true)
                navController.navigate("locations")
            }, modifier = Modifier.fillMaxWidth()
        ) { Text(
            text = routeViewModel.start.value?.alias ?:"Select start location",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        }
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedButton(
            onClick = {
                routeViewModel.seeSelectEnd(true)
                navController.navigate("locations")
            }, modifier = Modifier.fillMaxWidth()
        ) { Text(
            text = routeViewModel.end.value?.alias ?:"Select end location",
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
            text = routeViewModel.vehicleState.value?.alias ?: "Select vehicle",
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
                routeViewModel.createRute(start = routeViewModel.start.value,
                    end = routeViewModel.end.value,
                    vehicle = routeViewModel.vehicleState.value,
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
