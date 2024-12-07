package com.project.atlas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.atlas.viewModels.UserViewModel
import com.project.atlas.viewModels.VehicleViewModel
import com.project.atlas.views.HomePage
import com.project.atlas.views.LoginPage
import com.project.atlas.views.MapPage
import com.project.atlas.views.SignUpPage
import com.project.atlas.views.listVehicle

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, userViewModel: UserViewModel){
    val navController = rememberNavController()

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
        composable("vehicles"){
            val vehicleViewModel: VehicleViewModel = viewModel()
            listVehicle(modifier, navController, VehicleViewModel())
        }
    })
}