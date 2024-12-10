package com.project.atlas.views

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import com.google.maps.android.PolyUtil
import com.project.atlas.viewModels.RuteViewModel
import com.utsman.osmandcompose.OpenStreetMap
import com.utsman.osmandcompose.Polyline

import com.utsman.osmandcompose.rememberCameraState
import org.osmdroid.util.GeoPoint


@Composable
fun RuteViewerPage(navController: NavController, ruteViewModel: RuteViewModel) {

    val cameraState = rememberCameraState {
        geoPoint = GeoPoint(ruteViewModel.ruteState.value!!.start.lon,
            ruteViewModel.ruteState.value!!.start.lat)
        zoom = 12.0
    }

    val geoPoint = remember {
        PolyUtil.decode(ruteViewModel.ruteState.value!!.rute).map {
            GeoPoint(it.latitude, it.longitude)
        }
    }


    OpenStreetMap(
        modifier = Modifier.fillMaxSize(),
        cameraState = cameraState
    ) {
        // add polyline
        Polyline(geoPoints = geoPoint)
    }
}

