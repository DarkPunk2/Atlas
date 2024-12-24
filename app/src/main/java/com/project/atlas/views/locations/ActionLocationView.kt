package com.project.atlas.views.locations

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.atlas.components.CustomBottomSheet
import com.project.atlas.models.Location
import com.project.atlas.ui.theme.AtlasDarker
import com.project.atlas.viewModels.LocationsViewModel
import com.project.atlas.viewModels.RouteViewModel

@Composable
fun ActionLocationView(
    onDismiss: () -> Unit,
    viewModel: LocationsViewModel,
    location: Location,
    routeViewModel: RouteViewModel,
    navController: NavController
) {

    val showEditCard = remember { mutableStateOf(false) }

    val onBack: () -> Unit = if (showEditCard.value) {
        {
            showEditCard.value = false
        }
    } else {
        {
            onDismiss()
        }
    }

    CustomBottomSheet(
        title = "Location",
        onBack = onBack,
        onDismiss = onDismiss
    ) {
        AnimatedVisibility(
            visible = showEditCard.value,
            enter = expandIn(
                expandFrom = Alignment.Center
            ),
            exit = shrinkOut(
                shrinkTowards = Alignment.Center
            )
        ) {
            EditLocationView(
                onDismiss = {
                    onDismiss()
                },
                viewModel,
                location
            )
        }

        AnimatedVisibility(
            visible = !showEditCard.value,
            enter = expandIn(
                expandFrom = Alignment.Center
            ),
            exit = shrinkOut(
                shrinkTowards = Alignment.Center
            )
        ) {
            Column() {
                Text(
                    text = location.alias,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = "(" + location.lat + ", " + location.lon + ")",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(8.dp)
                )

                Row() {
                    FilledTonalButton(
                        onClick = {
                            showEditCard.value = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                    ) {
                        Text("Edit")
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    FilledTonalButton(
                        onClick = {
                            viewModel.removeLocation(
                                location
                            )
                            Log.d("locations", "Removed")
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Remove location")
                    }
                    if (routeViewModel.showStartSelect.value!!) {
                        FilledTonalButton(
                            onClick = {
                                routeViewModel.addStart(location)
                                routeViewModel.seeSelectStart(false)
                                onDismiss()
                                navController.navigate("createRute")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AtlasDarker
                            )
                        ) {
                            Text("Select location")
                        }
                    }
                    if (routeViewModel.showEndSelect.value!!) {
                        FilledTonalButton(
                            onClick = {
                                routeViewModel.addEnd(location)
                                routeViewModel.seeSelectEnd(false)
                                navController.navigate("createRute")
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AtlasDarker
                            )
                        ) {
                            Text("Select location")
                        }
                    }
                }

                Spacer(modifier = Modifier.size(200.dp))
            }
        }
    }

}
