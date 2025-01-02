package com.project.atlas.views

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.outlined.DirectionsCarFilled
import androidx.compose.material.icons.outlined.Home
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
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.ui.graphics.Color
import com.project.atlas.ui.theme.AtlasGreen

@Composable
fun NavigationMenu(navController: NavController, indexSelected: Int) {
    var selectedItem by remember { mutableIntStateOf(indexSelected) }
    val items = listOf("Home", "Locations", "Vehicles", "Routes")
    val navigateitems = listOf("home", "locations", "vehicles", "routes")
    val selectedIcons = listOf(
        Icons.Filled.Home,
        Icons.Filled.LocationOn,
        Icons.Sharp.DirectionsCarFilled,
        Icons.Filled.Map
    )
    val unselectedIcons =
        listOf(
            Icons.Outlined.Home,
            Icons.Outlined.Place,
            Icons.Outlined.DirectionsCarFilled,
            Icons.Outlined.Map
        )

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
                colors = NavigationBarItemColors(
                    selectedIconColor = Color.Black,
                    selectedTextColor = MaterialTheme.colorScheme.onBackground,
                    selectedIndicatorColor = AtlasGreen,
                    unselectedIconColor = MaterialTheme.colorScheme.onBackground,
                    unselectedTextColor = MaterialTheme.colorScheme.onBackground,
                    disabledIconColor = MaterialTheme.colorScheme.onBackground,
                    disabledTextColor = MaterialTheme.colorScheme.onBackground
                ),
                selected = selectedItem == index,
                onClick = {
                    selectedItem = index
                    navController.navigate(navigateitems[index])
                }
            )
        }
    }
}