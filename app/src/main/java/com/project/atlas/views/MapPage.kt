package com.project.atlas.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.atlas.viewModels.LocationsViewModel
import com.project.atlas.viewModels.MapViewModel
import com.project.atlas.views.locations.AddLocationView
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState

@Composable
fun MapPage(modifier: Modifier = Modifier, navController: NavController) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        OsmdroidMapView(MapViewModel())
    }
}

@Composable
fun OsmdroidMapView(mapViewModel: MapViewModel) {
    val cameraState = rememberCameraState {
        geoPoint = mapViewModel.mapState.value!!.zoomTo
        zoom = mapViewModel.mapState.value!!.zoomValue
    }
    val viewModel: LocationsViewModel = viewModel()
    val showCard = remember { mutableStateOf(false) }
    val lat = remember { mutableStateOf(0.0)}
    val lon = remember { mutableStateOf(0.0)}

    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState,
        onMapLongClick = {
            point -> mapViewModel.addPoint(point)
            lat.value = point.latitude
            lon.value = point.longitude
            showCard.value = true
        }
    )

    AnimatedVisibility(
        visible = showCard.value,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        AddLocationView(onDismiss = { showCard.value = false }, viewModel, lat.value, lon.value)
    }
}

@Preview(showBackground = true)
@Composable
fun MapPagePreview() {
    val navController = rememberNavController()
    MapPage(navController = navController)
}
