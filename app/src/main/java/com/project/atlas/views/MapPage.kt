package com.project.atlas.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.atlas.R
import com.project.atlas.ViewModels.MapViewModel
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.rememberCameraState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView

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
