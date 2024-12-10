package com.project.atlas.views


import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import com.project.atlas.R
import com.project.atlas.apisRequest.RuteType
import com.project.atlas.ui.theme.AtlasGreen


@Composable
fun RuteCreatorView() {
    var expanded by remember { mutableStateOf(false) }

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

            }, modifier = Modifier.fillMaxWidth()
        ) { Text(
            text = "Vehicle",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(vertical = 5.dp)
        )
        }
        Spacer(modifier = Modifier.height(15.dp))

        RuteTypeMenu()

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

@Preview(showBackground = true)
@Composable
fun RutePreview() {
    RuteCreatorView()
}

var selectedDistance by mutableStateOf(RuteType.RECOMMENDED.name)
@Composable
fun RuteTypeMenu() {
    val ruteType = listOf(RuteType.FASTER, RuteType.SHORTER, RuteType.RECOMMENDED)
    var isExpanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val icon = if (isExpanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowDown
    }

    Column {
        OutlinedTextField(
            value = selectedDistance,
            onValueChange = { selectedDistance = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textFieldSize = coordinates.size.toSize()
                },
            label = { Text(text = "Distance") },
            trailingIcon = {
                Icon(icon, "", Modifier.clickable { isExpanded = !isExpanded })
            }, shape = RoundedCornerShape(20.dp)
        )

        DropdownMenu(
            expanded = isExpanded,
            onDismissRequest = { isExpanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            ruteType.forEach { label ->
                DropdownMenuItem(text = { Text(label.name) },onClick = {
                    selectedDistance = label.name
                    isExpanded = false
                })
            }
        }
    }
}
