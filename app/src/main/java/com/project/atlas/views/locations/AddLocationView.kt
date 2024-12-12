package com.project.atlas.views.locations

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.project.atlas.viewModels.LocationsViewModel

@Composable
fun AddLocationView(
    onBack: () -> Unit,
    lvm: LocationsViewModel,
    lat: Double,
    lon: Double
) {
    var lats = remember { mutableStateOf(lat.toString()) }
    var lons = remember { mutableStateOf(lon.toString()) }
    var alias = remember { mutableStateOf("") }
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            colors = CardDefaults
                .cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
            modifier = Modifier.shadow(4.dp)
        )
        {
            MaterialTheme(darkColorScheme()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceBright,
                ) {
                    Column(modifier = Modifier.padding(8.dp, 16.dp, 8.dp, 320.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    onBack()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go Back"
                                )
                            }
                            Text(
                                text = "New location",
                                style = MaterialTheme.typography.headlineSmall
                            )
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
                                        .weight(1f)
                                        .focusRequester(focusRequester),
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
                                placeholder = { Text("Enter Alias") },
                                singleLine = true,
                                shape = RoundedCornerShape(20.dp),
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii),
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    onClick = {
                                        lvm.addLocation(
                                            lats.value.toDouble(),
                                            lons.value.toDouble(),
                                            alias.value
                                        )
                                        lats = mutableStateOf("")
                                        lons = mutableStateOf("")
                                        alias = mutableStateOf("")
                                        focusManager.clearFocus()
                                        Log.d("locations", "Añadido")
                                        onBack()
                                    }) {
                                    Text("Save location")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}