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
    route: RouteModel,
    activeAdd: Boolean,
    activeDelete: Boolean,
    onDismiss: () -> Unit,
    onAdd: (RouteModel) -> Unit,
    onDelete: (RouteModel) -> Unit
) {
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showAdd by remember { mutableStateOf(activeAdd) }
    var showDelete by remember { mutableStateOf(activeDelete) }
    val formattedDistance = if (route.distance >= 1000) {
        String.format("%.1f km", route.distance / 1000.0)
    } else {
        "${route.distance.toInt()} m"
    }
    val formattedDuration = if (route.duration >= 3600) {
        String.format(
            "%.1f h",
            route.duration / 3600.0
        ) // Convertir segundos a horas
    } else {
        String.format(
            "%d min",
            (route.duration / 60).toInt()
        ) // Convertir segundos a minutos
    }

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
                text = "${route.start.alias} to ${route.end.alias}",
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
                    Text( formattedDuration, style = MaterialTheme.typography.bodyMedium)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Distance", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                    Text(formattedDistance, style = MaterialTheme.typography.bodyMedium)
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
                        onClick = { onAdd(route)
                                  showAdd = false},
                        colors = ButtonDefaults.buttonColors(containerColor = AtlasGreen)
                    ) {
                        Text("Store rute", color = Color.Black)
                    }
                } else if (showDelete){
                    Button(
                        onClick = { onDelete(route)
                            showDelete = false},
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("Delete rute", color = Color.Black)
                    }
                }
                else {
                    Spacer(modifier = Modifier.width(120.dp))
                }
            }
        }
    }
}