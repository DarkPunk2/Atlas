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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.project.atlas.components.CustomBottomSheet
import com.project.atlas.viewModels.LocationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLocationView(
    onDismiss: () -> Unit,
    lvm: LocationsViewModel,
    lat: Double,
    lon: Double
) {
    var alias = remember { mutableStateOf("") }
    val focusRequester = FocusRequester()
    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    CustomBottomSheet(
        onBack = onDismiss,
        onDismiss = onDismiss,
        title = "Add location"
    ) {
        Row {
            Text(
                text = "(" + lat + ", " + lon + ")",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier
                    .padding(8.dp)
            )
        }

        OutlinedTextField(
            value = alias.value,
            onValueChange = { alias.value = it },
            label = { Text("Alias") },
            placeholder = { Text("Enter Alias") },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Ascii)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = {
                    lvm.addLocation(
                        lat,
                        lon,
                        alias.value
                    )
                    alias = mutableStateOf("")
                    focusManager.clearFocus()
                    Log.d("locations", "Añadido")
                    onDismiss()
                }) {
                Text("Save location")
            }
        }
    }
}