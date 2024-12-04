package com.project.atlas.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.atlas.viewModels.MapViewModel
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

    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState,
        onMapLongClick = {point -> mapViewModel.addPoint(point)}
    )
}

@Preview(showBackground = true)
@Composable
fun MapPagePreview() {
    val navController = rememberNavController()
    MapPage(navController = navController)
}
