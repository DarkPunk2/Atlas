package com.project.atlas.views.routes

import android.graphics.drawable.Drawable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.project.atlas.R
import com.project.atlas.viewModels.LocationsViewModel
import com.project.atlas.viewModels.MapViewModel
import com.project.atlas.viewModels.RouteViewModel
import com.utsman.osmandcompose.CameraProperty
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberMarkerState
import org.osmdroid.util.GeoPoint

@Composable
fun MapSelection(routeViewModel: RouteViewModel,
                 mapViewModel: MapViewModel,
                 onDismiss: () -> Unit) {
    val markerPosition by mapViewModel.markerPosition.observeAsState()
    val context = LocalContext.current

    val markerState = rememberMarkerState(
        geoPoint = markerPosition!!
    )
    val markerIcon: Drawable? by remember {
        mutableStateOf(ContextCompat.getDrawable(context, R.drawable.start_icon))
    }

    LaunchedEffect(markerPosition) {
        markerState.geoPoint = markerPosition as GeoPoint
    }

    var cameraState by remember {
        mutableStateOf(
            CameraState(
                CameraProperty(
                    geoPoint = mapViewModel.markerPosition.value!!,
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
    val lat = remember { mutableStateOf(0.0) }
    val lon = remember { mutableStateOf(0.0) }

    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState,
        properties = mapProperties,
        onMapLongClick = { point ->
            if (routeViewModel.showStartSelect.value == true) {
                routeViewModel.addStartByCoord(lat = point.latitude, lon = point.longitude)
            }else{
                routeViewModel.addEndByCoord(lat = point.latitude, lon = point.longitude)
            }
            onDismiss()
        },
    ) {
        Marker(
            state = markerState,
            visible = mapViewModel.showMarker.value!!,
            icon = markerIcon
        )
    }
}