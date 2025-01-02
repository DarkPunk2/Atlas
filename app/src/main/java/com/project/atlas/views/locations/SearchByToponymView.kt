package com.project.atlas.views.locations

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.project.atlas.components.CustomBottomSheet
import com.project.atlas.viewModels.LocationsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchByToponymView(
    onDismiss: () -> Unit,
    onAdd: () -> Unit,
    lvm: LocationsViewModel,
) {
    var toponym = remember { mutableStateOf("") }
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
        OutlinedTextField(
            value = toponym.value,
            onValueChange = { toponym.value = it },
            label = { Text("Toponym") },
            placeholder = { Text("Enter toponym") },
            singleLine = true,
            shape = RoundedCornerShape(20.dp),
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
                    lvm.addLocation(
                        toponym.value
                    )
                    toponym = mutableStateOf("")
                    focusManager.clearFocus()
                    Log.d("locations", "Añadido")
                    onAdd()
                    onDismiss()
                }) {
                Text("Save location")
            }
        }
    }
}