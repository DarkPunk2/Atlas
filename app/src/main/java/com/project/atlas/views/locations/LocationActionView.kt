package com.project.atlas.views.locations

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import com.project.atlas.models.Location
import com.project.atlas.viewModels.LocationsViewModel

@Composable
fun LocationActionView(
    onBack: () -> Unit,
    lvm: LocationsViewModel,
    location: Location
) {
    Column(
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            colors = CardDefaults
                .cardColors(containerColor = MaterialTheme.colorScheme.inverseSurface),
            modifier = Modifier.shadow(4.dp)
        )
        {
            MaterialTheme(darkColorScheme()) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceBright,
                ) {
                    Column(modifier = Modifier.padding(8.dp, 16.dp, 8.dp, 200.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    onBack()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Go Back"
                                )
                            }
                            Text(
                                text = location.alias,
                                style = MaterialTheme.typography.headlineSmall
                            )
                        }
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                text = "(" + location.lat + ", " + location.lon + ")",
                                style = MaterialTheme.typography.labelLarge
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                FilledTonalButton(
                                    onClick = {
                                        lvm.removeLocation(
                                            location
                                        )
                                        Log.d("locations", "Removed")
                                        onBack()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.error
                                    )
                                ) {
                                    Text("Remove location")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}