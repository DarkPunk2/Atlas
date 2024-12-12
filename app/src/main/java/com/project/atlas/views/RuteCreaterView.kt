package com.project.atlas.views


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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.atlas.models.RuteType
import com.project.atlas.models.VehicleModel
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.viewModels.RuteViewModel
import com.project.atlas.views.vehicles.DropdownSelector


@Composable
fun RuteCreatorView(navController: NavController, ruteViewModel: RuteViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<RuteType?>(null) }

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
            text = "Start location",
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
            text = "End location",
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
            items = RuteType.entries.toList(),
            selectedItem = selectedType,
            onItemSelected = { selectedType = it }
        )

        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = {

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

