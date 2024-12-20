package com.project.atlas.views.routes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.project.atlas.models.RouteModel
import com.project.atlas.ui.theme.AtlasGreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RouteDetailsCard(
    rute: RouteModel,
    activeAdd: Boolean,
    onDismiss: () -> Unit,
    onAdd: (RouteModel) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showAdd by remember { mutableStateOf(activeAdd) }

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
                text = "${rute.start.alias} to ${rute.end.alias}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Información de la ruta
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Duration", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text( rute.duration.toString(), style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Distance", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(rute.distance.toString(), style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cost", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text("N/A", style = MaterialTheme.typography.bodyMedium)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Mostrar botones solo si el tipo de vehículo no es Walk ni Cycle
                if (showAdd) {
                    // Botón de añadir
                    Button(
                        onClick = { onAdd(rute)
                                  showAdd = false},
                        colors = ButtonDefaults.buttonColors(containerColor = AtlasGreen)
                    ) {
                        Text("Store rute", color = Color.Black)
                    }
                } else {
                    Spacer(modifier = Modifier.width(120.dp))
                }
            }
        }
    }
}