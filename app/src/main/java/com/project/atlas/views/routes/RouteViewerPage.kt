package com.project.atlas.views.routes

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.maps.android.PolyUtil
import com.project.atlas.ui.theme.AtlasDarker
import com.project.atlas.viewModels.RouteViewModel
import com.utsman.osmandcompose.CameraProperty
import com.utsman.osmandcompose.CameraState
import com.utsman.osmandcompose.DefaultMapProperties
import com.utsman.osmandcompose.Marker
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.Polyline
import com.utsman.osmandcompose.ZoomButtonVisibility
import com.utsman.osmandcompose.rememberMarkerState

import org.osmdroid.util.GeoPoint
import kotlin.math.log2


@Composable
fun RouteViewerPage(navController: NavController, routeViewModel: RouteViewModel) {
    val bbox = routeViewModel.routeState.value!!.bbox
    val centerLat = (bbox[1] + bbox[3]) / 2
    val centerLon = (bbox[0] + bbox[2]) / 2
    val latDiff = bbox[3] - bbox[1]
    val lonDiff = bbox[2] - bbox[0]
    val zoom = 10.0 - log2((latDiff + lonDiff) / 2)

    var cameraState by remember {
        mutableStateOf(
            CameraState(
                CameraProperty(
                    geoPoint = GeoPoint(centerLat, centerLon),
                    zoom = zoom
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

    val geoPoint = remember {
        PolyUtil.decode(routeViewModel.routeState.value!!.rute).map {
            GeoPoint(it.latitude, it.longitude)
        }
    }

    val start = routeViewModel.routeState.value!!.start
    val starMarker = rememberMarkerState(
        geoPoint = GeoPoint(start.lat, start.lon)
    )

    val end = routeViewModel.routeState.value!!.end
    val endMarker = rememberMarkerState(
        geoPoint = GeoPoint(end.lat, end.lon)
    )

    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    val showCard = remember { mutableStateOf(true) }

    SideEffect {
        mapProperties = mapProperties
            .copy(isEnableRotationGesture = true)
            .copy(zoomButtonVisibility = ZoomButtonVisibility.NEVER)
    }

    BackHandler {
        if (showCard.value) {
            showCard.value = false
        } else {
            routeViewModel.resetValues()
            navController.navigate("createRute")
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            properties = mapProperties
        ) {
            Marker(
                state = starMarker,

                )
            Marker(
                state = endMarker
            )
            Polyline(
                geoPoints = geoPoint,
                color = AtlasDarker
            )
        }
        AnimatedVisibility(
            visible = showCard.value,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            RouteDetailsCard(
                rute = routeViewModel.routeState.value!!,
                activeAdd = true,
                onDismiss = { showCard.value = false },
                onAdd = { rute -> routeViewModel.addRoute(rute) }
            )
        }
        if (!showCard.value) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(48.dp)
            )
            {
                Card(modifier = Modifier
                    .clickable { showCard.value = true }
                    .padding(8.dp),
                    elevation = CardDefaults.cardElevation())
                { Text(text = "Show Details", modifier = Modifier.padding(8.dp)) }
            }
        }
    }
}




