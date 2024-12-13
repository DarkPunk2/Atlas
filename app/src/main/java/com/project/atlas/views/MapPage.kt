package com.project.atlas.views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.NestedScrollSource.Companion.SideEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.project.atlas.viewModels.LocationsViewModel
import com.project.atlas.viewModels.MapViewModel
import com.project.atlas.views.locations.AddLocationView
import com.utsman.osmandcompose.CameraProperty
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberCameraState
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint

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
    val markerPosition by mapViewModel.markerPosition.observeAsState(GeoPoint(39.992573, -0.064749))

    val markerState = rememberMarkerState(
        geoPoint = markerPosition
    )

    LaunchedEffect(markerPosition) {
        markerState.geoPoint = markerPosition
    }

    var cameraState by remember {
        mutableStateOf(
            CameraState(
                CameraProperty(
                    geoPoint = GeoPoint(39.993100, -0.067035),
                    zoom = 16.0
                )
            )
        )
    }

    LaunchedEffect(cameraState.zoom) {
        val zoom = cameraState.zoom
        val geoPoint = cameraState.geoPoint
        cameraState = CameraState(
            CameraProperty(
                geoPoint = geoPoint,
                zoom = zoom
            )
        )
    }

    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    SideEffect {
        mapProperties = mapProperties
            .copy(isEnableRotationGesture = true)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
    }

    val viewModel: LocationsViewModel = viewModel()
    val showCard = remember { mutableStateOf(false) }
    val lat = remember { mutableStateOf(0.0)}
    val lon = remember { mutableStateOf(0.0)}

    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState,
        properties = mapProperties,
        onMapLongClick = {
            point -> lat.value = point.latitude
            lon.value = point.longitude
            showCard.value = true
            mapViewModel.setMarkerPosition(point)
            mapViewModel.setShowMarker(true)
            cameraState = CameraState(
                CameraProperty(
                    geoPoint = point,
                    zoom = 18.0
                )
            )
        },
    ){
        Marker(
            state = markerState,
            visible = mapViewModel.showMarker.value!!
        )
    }

    AnimatedVisibility(
        visible = showCard.value,
        enter = slideInVertically { it },
        exit = slideOutVertically { it }
    ) {
        AddLocationView(onBack = { showCard.value = false }, viewModel, lat.value, lon.value)
    }
}


