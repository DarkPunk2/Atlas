package com.project.atlas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.atlas.viewModels.FuelPriceViewModel
import com.project.atlas.viewModels.MapViewModel
import com.project.atlas.viewModels.RouteViewModel
import com.project.atlas.viewModels.UserViewModel
import com.project.atlas.viewModels.VehicleViewModel
import com.project.atlas.views.EnergyTypeTest
import com.project.atlas.views.HomePage
import com.project.atlas.views.user.LoginPage
import com.project.atlas.views.user.SignUpPage
import com.project.atlas.views.vehicles.listVehicle
import com.project.atlas.views.locations.LocationsListView
import com.project.atlas.views.vehicles.SelectVehicle
import com.project.atlas.views.routes.ListRoute
import com.project.atlas.views.routes.RouteCreatorView
import com.project.atlas.views.routes.RouteViewerPage
import com.project.atlas.views.user.ChangePasswordPage
import com.project.atlas.views.user.RecoverPasswordPage

@Composable
fun MyAppNavigation(modifier: Modifier = Modifier, userViewModel: UserViewModel, mapViewModel: MapViewModel){
    val navController = rememberNavController()
    val vehicleViewModel: VehicleViewModel = viewModel()
    val fuelPriceViewModel: FuelPriceViewModel = viewModel()
    val routeViewModel: RouteViewModel = viewModel()

    NavHost(navController = navController, startDestination =  "login", builder = {
        composable("login"){
            LoginPage(modifier, navController, userViewModel)
        }
        composable("home"){
            HomePage(modifier, navController, userViewModel, routeViewModel, mapViewModel)
        }
        composable("signup"){
            SignUpPage(modifier, navController, userViewModel)
        }
        composable("locations"){
            LocationsListView(navController, routeViewModel)
        }
        composable("vehicles"){
            listVehicle(modifier, navController, vehicleViewModel)
        }
        composable("routes"){
            ListRoute(modifier, navController, routeViewModel)
        }
        composable("selectVehicles"){
            SelectVehicle(modifier, navController, vehicleViewModel, routeViewModel)
        }
        composable("createRute"){
            RouteCreatorView(navController, routeViewModel)
        }
        composable("viewRute"){
            RouteViewerPage(navController, routeViewModel)
        }
        composable("fuelTest"){
            EnergyTypeTest(
                viewModel = fuelPriceViewModel
            )
        }
        composable("recover"){
            RecoverPasswordPage(modifier, navController, userViewModel)
        }
        composable("changePassword"){
            ChangePasswordPage(modifier, navController, userViewModel)
        }
    })
}


