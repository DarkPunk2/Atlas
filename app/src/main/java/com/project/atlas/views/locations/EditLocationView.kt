package com.project.atlas.views.locations

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.atlas.models.Location
import com.project.atlas.viewModels.LocationsViewModel

@Composable
fun EditLocationView(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    viewModel: LocationsViewModel,
    location: Location
) {
    val lats = remember { mutableStateOf(location.lat.toString()) }
    val lons = remember { mutableStateOf(location.lon.toString()) }
    val alias = remember { mutableStateOf(location.alias) }
    val focusRequester = FocusRequester()

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(modifier = Modifier.padding(8.dp)) {
        Row {
            OutlinedTextField(
                value = lats.value,
                onValueChange = { lats.value = it },
                label = { Text("Lat") },
                placeholder = { Text("0.0") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
            Spacer(modifier = Modifier.width(10.dp))
            OutlinedTextField(
                value = lons.value,
                onValueChange = { lons.value = it },
                label = { Text("Lon") },
                placeholder = { Text("0.0") },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            )
        }

        OutlinedTextField(
            value = alias.value,
            onValueChange = { alias.value = it },
            label = { Text("Alias") },
            placeholder = { Text("Enter new Alias") },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    viewModel.updateLocation(
                        location,
                        lats.value.toDouble(),
                        lons.value.toDouble(),
                        alias.value
                    )
                    onEdit()
                    onDismiss()
                }) {
                Text("Save location")
            }
        }
    }
}