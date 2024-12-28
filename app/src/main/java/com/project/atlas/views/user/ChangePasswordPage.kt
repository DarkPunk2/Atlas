package com.project.atlas.views.user

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.project.atlas.R
import com.project.atlas.models.ChangeState
import com.project.atlas.viewModels.UserViewModel
import com.project.atlas.ui.theme.AtlasGreen

@Composable
fun ChangePasswordPage(modifier: Modifier = Modifier,navController: NavController, userViewModel: UserViewModel) {
    var oldPassword by remember {
        mutableStateOf("")
    }
    var newPassword by remember {
        mutableStateOf("")
    }
    var confPassword by remember {
        mutableStateOf("")
    }

    val newPasswordFocusRequester = remember { FocusRequester() }
    val confPasswordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val changeState = userViewModel.changeState.observeAsState()
    val context = LocalContext.current

    LaunchedEffect(changeState.value) {
        when(changeState.value){
            is ChangeState.Changed -> navController.navigate("home")
            is ChangeState.Error -> Toast.makeText(context,
                (changeState.value as ChangeState.Error).message, Toast.LENGTH_SHORT).show()
            else -> Unit
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
            .clickable(onClick = { focusManager.clearFocus() }),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.atlas_t),
            contentDescription = "Logo",
            modifier = Modifier.size(200.dp)
        )
        Spacer(modifier = Modifier.height(20.dp))
        Text(
            text = "Change password",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedTextField(
            value = oldPassword,
            onValueChange = { newValue ->
                oldPassword = newValue
            },
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Old password") },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions( onNext = { newPasswordFocusRequester.requestFocus() } )
        )
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedTextField(
            value = newPassword,
            onValueChange = { newValue ->
                newPassword = newValue
            },
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("New password") },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
                .focusRequester(newPasswordFocusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions( onNext = { confPasswordFocusRequester.requestFocus() } )
        )
        Spacer(modifier = Modifier.height(15.dp))
        OutlinedTextField(
            value = confPassword,
            onValueChange = { newValue ->
                confPassword = newValue
            },
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Lock, contentDescription = null)
            },
            visualTransformation = PasswordVisualTransformation(),
            label = { Text("Repeat password") },
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth()
                .focusRequester(confPasswordFocusRequester),
            keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions( onNext = { focusManager.clearFocus() } )
        )
        Spacer(modifier = Modifier.height(15.dp))
        Button(
            onClick = {
                userViewModel.changePassword(oldPassword,newPassword,confPassword)
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors( containerColor = AtlasGreen )
        ) {
            Text(
                text = "Change",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }
    }
}

