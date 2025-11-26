package com.k.sekiro.weatherapp.presentation.permission_screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun MultiplePermissionRequest(
    onAllPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val permissionsToRequest = remember {
        mutableListOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ).toTypedArray()
    }
    var allPermissionsGranted by remember { mutableStateOf(false) }
    var showPermissionRationaleDialog by remember {
        mutableStateOf(false)
    }
    var currentRationalePermission by remember { mutableStateOf("") }
    var pendingPermissions by remember {
        mutableStateOf(listOf<String>())
    }
    var showGoToSettingsUI by remember { mutableStateOf(false) }

    Log.e("ks", "is all granted top  :$allPermissionsGranted")

    // Function to check current permission status
    fun checkPermissionStatus(): Boolean {
        val notGranted = permissionsToRequest.filter {
            ContextCompat.checkSelfPermission(context, it) != PackageManager.PERMISSION_GRANTED
        }
        return notGranted.isEmpty()
    }

    val multiplePermissionsLauncher =
        rememberLauncherForActivityResult(
            contract =
                ActivityResultContracts.RequestMultiplePermissions()
        ) { permissionsResult ->
            val grantedList = permissionsResult.filterValues {
                it
            }.keys.toList()
            val deniedList = permissionsToRequest.filter {
                it !in grantedList
            }
            if (deniedList.isEmpty()) {
                allPermissionsGranted = true
                showGoToSettingsUI = false
                onAllPermissionsGranted()
            } else {
                pendingPermissions = deniedList
                val firstDeniedPermission = deniedList.firstOrNull()
                if (firstDeniedPermission != null &&
                    !context.shouldShowRationale(firstDeniedPermission)
                ) {
                    // User has denied and asked not to be asked again, show go to settings UI
                    showGoToSettingsUI = true
                } else if (firstDeniedPermission != null) {
                    currentRationalePermission = firstDeniedPermission
                    showPermissionRationaleDialog = true
                    showGoToSettingsUI = false
                }
            }
        }

    LaunchedEffect(lifecycleOwner.lifecycle) {


        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                // Check permissions every time the app comes to foreground
                if (checkPermissionStatus()) {
                    allPermissionsGranted = true
                    showGoToSettingsUI = false
                    onAllPermissionsGranted()
                } else {
                    val notGranted = permissionsToRequest.filter {
                        ContextCompat.checkSelfPermission(
                            context,
                            it
                        ) != PackageManager.PERMISSION_GRANTED
                    }
                    pendingPermissions = notGranted

                    // Only launch permission request if we're not already showing go to settings UI
                    if (!showGoToSettingsUI) {
                        multiplePermissionsLauncher.launch(notGranted.toTypedArray())
                    }
                }

                Log.e("ks", "is all granted1 :$allPermissionsGranted")
            }
        }
    }

    // Additional effect to monitor lifecycle changes and re-check permissions
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    // Re-check permissions when app resumes
                    if (checkPermissionStatus()) {
                        allPermissionsGranted = true
                        showGoToSettingsUI = false
                        onAllPermissionsGranted()
                    }
                }

                else -> {}
            }
        }

        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
        }
    }

    // Show UI only if permissions are not granted and delay is done
    if (showGoToSettingsUI) {
    //if (!allPermissionsGranted && isDelayDone) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
           // if (showGoToSettingsUI) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text("Please go to settings and grant the permissions")
                    Button(onClick = { navigateToAppSettings(context) }) {
                        Text("Go")
                    }
                }
         //   }
        }
        Log.e("ks", "is all granted2 :$allPermissionsGranted")
    }

    if (showPermissionRationaleDialog &&
        currentRationalePermission.isNotEmpty()
    ) {
        PermissionRationaleDialog(
            permission = currentRationalePermission,
            onContinueClick = {
                showPermissionRationaleDialog = false
                multiplePermissionsLauncher.launch(
                    arrayOf(currentRationalePermission)
                )
            },
            onCancelClick = {
                showPermissionRationaleDialog = false
                // Show go to settings UI if user cancels rationale
                showGoToSettingsUI = true
            }
        )
    }
}

@Composable
fun PermissionRationaleDialog(
    permission: String,
    onContinueClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val rationaleText = when (permission) {
        Manifest.permission.ACCESS_FINE_LOCATION -> "This app needs Location access to get current weather."
        Manifest.permission.ACCESS_COARSE_LOCATION -> "This app needs Location access to get current weather."
        else -> "This app requires this permission."
    }
    AlertDialog(
        onDismissRequest = onCancelClick,
        title = { Text("Permission Required") },
        text = { Text(rationaleText) },
        confirmButton = {
            Button(onClick = onContinueClick) {
                Text("Continue")
            }
        },
        dismissButton = {
            Button(onClick = onCancelClick) {
                Text("Cancel")
            }
        }
    )
}

fun Context.shouldShowRationale(permission: String): Boolean {
    return ActivityCompat.shouldShowRequestPermissionRationale(
        this as ComponentActivity,
        permission
    )
}

fun navigateToAppSettings(context: Context) {
    try {
        val intent =
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                val uri = Uri.fromParts("package", context.packageName, null)
                data = uri
            }
        context.startActivity(intent)

    } catch (ex: SecurityException) {
        Log.e("ks", "Ops... ${ex.message}")
    } catch (ex: Exception) {
        Log.e("ks", "Ops... ${ex.message}")

    }
}

@Composable
fun PermissionGate(content: @Composable () -> Unit) {
    var allPermissionsGranted by remember { mutableStateOf(false) }
    MultiplePermissionRequest(
        onAllPermissionsGranted = {
            allPermissionsGranted = true
            Log.e("ks", "onAllPermissionsGranted() has been called")
        }
    )
    if (allPermissionsGranted) {
        content()
    } else {
// While permissions are not granted, the user stays here.
// The MultiplePermissionRequest composable handles the UI for this state.
    }
}
