package com.project.atlas.views.locations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandIn
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.atlas.components.CustomBottomSheet
import com.project.atlas.models.Location
import com.project.atlas.ui.theme.AtlasDarker
import com.project.atlas.ui.theme.AtlasGreen
import com.project.atlas.ui.theme.AtlasRed
import com.project.atlas.ui.theme.SnowWhite
import com.project.atlas.viewModels.LocationsViewModel
import com.project.atlas.viewModels.RouteViewModel

@Composable
fun ActionLocationView(
    onDismiss: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
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
                onEdit = {
                    onEdit()
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
                    text = location.toponym,
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(8.dp)
                )
                Text(
                    text = "( ${String.format("%.7f", location.lat)} , ${String.format("%.7f", location.lon)} )",
                    style = MaterialTheme.typography.labelMedium,
                )
                HorizontalDivider(
                    modifier = Modifier
                        .padding(8.dp)
                )

                if (!(routeViewModel.showStartSelect.value!! || routeViewModel.showEndSelect.value!!)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        FilledTonalButton(
                            onClick = {
                                showEditCard.value = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AtlasDarker,
                                contentColor = SnowWhite
                            ),
                        ) {
                            Text("Edit")
                        }
                        FilledTonalButton(
                            onClick = {
                                viewModel.removeLocation(
                                    location
                                )
                                onDelete()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AtlasRed,
                                contentColor = SnowWhite
                            ),
                        ) {
                            Text("Remove location")
                        }
                    }
                }

                if (routeViewModel.showStartSelect.value!!) {
                    FilledTonalButton(
                        onClick = {
                            routeViewModel.addStart(location)
                            routeViewModel.seeSelectStart(false)
                            onDismiss()
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AtlasGreen,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Select location")
                    }
                }
                if (routeViewModel.showEndSelect.value!!) {
                    FilledTonalButton(
                        onClick = {
                            routeViewModel.addEnd(location)
                            routeViewModel.seeSelectEnd(false)
                            navController.popBackStack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AtlasGreen,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    ) {
                        Text("Select location")
                    }
                }
                Spacer(modifier = Modifier.size(200.dp))
            }
        }
    }
}
