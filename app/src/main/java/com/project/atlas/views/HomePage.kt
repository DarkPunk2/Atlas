package com.project.atlas.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.atlas.R

@Composable
fun HomePage(modifier: Modifier = Modifier,navController: NavController ) {

    Image(
            painter = painterResource(id = R.drawable.atlas_lettering_black),
            contentDescription = "letterning",
            modifier = Modifier.absolutePadding(2.dp, 1.dp,3.dp,3.dp).size(100.dp)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            //painter = painterResource(id = R.drawable.atlaslogo),
            painter = painterResource(id = R.drawable.atlas_t),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp).background(color = Color.Transparent)
        )
        Spacer(modifier = Modifier.height(15.dp))
        Text(
            text = "Home",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Bold
        )
        TextButton(onClick = {
            navController.navigate("map")
        }) {
            Text(text = "Go to mapView Test")
        }
        TextButton(onClick = {
            navController.navigate("vehicles")
        }) {
            Text(text = "Go to vehicles")
        }
        TextButton(onClick = {
            navController.navigate("routes")
        }) {
            Text(text = "Go to routes")
        }
        TextButton(onClick = {
            navController.navigate("locations")
        }) {
            Text(text = "Go to locations")
        }
        TextButton(onClick = {
            navController.navigate("selectVehicles")
        }) {
            Text(text = "Go to selection")
        }
        TextButton(onClick = {
            navController.navigate("createRute")
        }) {
            Text(text = "Go to rute creation")
        }
        TextButton(onClick = {
            navController.navigate("fuelTest")
        }) {
            Text(text = "Test Combustible")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePagePreview() {
    val navController = rememberNavController()
    HomePage(navController = navController)
}
