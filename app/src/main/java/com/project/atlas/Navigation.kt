package com.project.atlas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.atlas.viewModels.RuteViewModel
import com.project.atlas.viewModels.UserViewModel
import com.project.atlas.viewModels.VehicleViewModel
import com.project.atlas.views.HomePage
import com.project.atlas.views.LoginPage
import com.project.atlas.views.MapPage
import com.project.atlas.views.RuteCreatorView
import com.project.atlas.views.RuteViewerPage
import com.project.atlas.views.SignUpPage
import com.project.atlas.views.vehicles.listVehicle
import com.project.atlas.views.locations.LocationsListView
import com.project.atlas.views.vehicles.SelectVehicle

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, userViewModel: UserViewModel){
    val navController = rememberNavController()
    val vehicleViewModel: VehicleViewModel = viewModel()
    val ruteViewModel: RuteViewModel = viewModel()
    NavHost(navController = navController, startDestination =  "login", builder = {
        composable("login"){
            LoginPage(modifier, navController, userViewModel)
        }
        composable("home"){
            HomePage(modifier, navController)
        }
        composable("map"){
            MapPage(modifier, navController)
        }
        composable("signup"){
            SignUpPage(modifier, navController, userViewModel)
        }
        composable("locations"){
            LocationsListView(navController)
        }
        composable("vehicles"){
            listVehicle(modifier, navController, vehicleViewModel)
        }
        composable("selectVehicles"){
            SelectVehicle(modifier, navController, vehicleViewModel, ruteViewModel)
        }
        composable("createRute"){
            RuteCreatorView(navController, ruteViewModel)
        }
        composable("viewRute"){
            RuteViewerPage(navController, ruteViewModel)
        }
    })
}