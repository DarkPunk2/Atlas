package com.project.atlas.views.routes

import android.graphics.drawable.Drawable
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absolutePadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.maps.android.PolyUtil
import com.project.atlas.R
import com.project.atlas.ui.theme.AtlasDarker
import com.project.atlas.ui.theme.BackgroundBlack
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
    val context = LocalContext.current
    val start = routeViewModel.routeState.value!!.start
    val starMarker = rememberMarkerState(
        geoPoint = GeoPoint(start.lat, start.lon)
    )
    val startIcon: Drawable? by remember {
        mutableStateOf(ContextCompat.getDrawable(context, R.drawable.start_icon))
    }

    val end = routeViewModel.routeState.value!!.end
    val endMarker = rememberMarkerState(
        geoPoint = GeoPoint(end.lat, end.lon)
    )
    val endIcon: Drawable? by remember {
        mutableStateOf(ContextCompat.getDrawable(context, R.drawable.start_icon_red))
    }

    var mapProperties by remember {
        mutableStateOf(DefaultMapProperties)
    }

    var cost by remember {
        mutableStateOf(routeViewModel.calculatedPrice)
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
            navController.popBackStack()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        OpenStreetMap(
            modifier = Modifier.fillMaxSize(),
            cameraState = cameraState,
            properties = mapProperties
        ) {
            Polyline(
                geoPoints = geoPoint,
                color = AtlasDarker
            )
            Marker(
                state = starMarker,
                icon = startIcon
            )
            Marker(
                state = endMarker,
                icon = endIcon
            )
        }
        Image(
            painter = painterResource(id = R.drawable.atlas_lettering_black),
            contentDescription = "lettering",
            modifier = Modifier
                .padding(start = 20.dp)
                .absolutePadding(2.dp, 6.dp, 3.dp, 3.dp)
                .size(100.dp),
            colorFilter = ColorFilter.tint(BackgroundBlack)
        )
        AnimatedVisibility(
            visible = showCard.value,
            enter = slideInVertically { it },
            exit = slideOutVertically { it }
        ) {
            RouteDetailsCard(
                route = routeViewModel.routeState.value!!,
                activeAdd = routeViewModel.showAddButton.value!!,
                activeDelete = routeViewModel.showRemoveButton.value!!,
                onDismiss = { showCard.value = false },
                onAdd = { route ->
                    routeViewModel.addRoute(route)
                    routeViewModel.seeAdd(false)
                },
                onDelete = {
                    routeViewModel.deleteRoute()
                    routeViewModel.seeRemove(false)
                    routeViewModel.resetValues()
                    navController.navigate("routes")
                },
                onCalculateCost = {
                    routeViewModel.calculatePrice(routeViewModel.routeState.value!!)
                },
                calculatedCost = cost
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




