package com.project.atlas.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.DirectionsCarFilled
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.sharp.DirectionsCarFilled
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavController
import androidx.navigation.Navigator

@Composable
fun NavigationMenu(navController: NavController, indexSelected: Int){
    var selectedItem by remember { mutableIntStateOf(indexSelected) }
    val items = listOf("Home","Locations", "Vehicles", "Routes")
    val navigateitems = listOf("home","locations","vehicles","routes")
    val selectedIcons = listOf(Icons.Filled.Home,Icons.Filled.LocationOn, Icons.Sharp.DirectionsCarFilled, Icons.Filled.Face)
    val unselectedIcons =
        listOf(Icons.Outlined.Home, Icons.Outlined.Place, Icons.Outlined.DirectionsCarFilled, Icons.Outlined.Person)

    NavigationBar {
        items.forEachIndexed { index, item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (selectedItem == index) selectedIcons[index] else unselectedIcons[index],
                        contentDescription = item
                    )
                },
                label = { Text(item) },
                selected = selectedItem == index,
                onClick = { selectedItem = index
                navController.navigate(navigateitems[index])}
            )
        }
    }
}