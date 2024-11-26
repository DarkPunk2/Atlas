package com.project.atlas

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.project.atlas.ViewModels.UserViewModel
import com.project.atlas.views.HomePage
import com.project.atlas.views.LoginPage

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

    })
}