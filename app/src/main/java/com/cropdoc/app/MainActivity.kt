package com.cropdoc.app

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cropdoc.app.ui.screens.CameraScreen
import com.cropdoc.app.ui.screens.HistoryScreen
import com.cropdoc.app.ui.screens.HomeScreen
import com.cropdoc.app.ui.screens.SensorScreen
import com.cropdoc.app.ui.theme.CropDocTheme
import com.cropdoc.app.viewmodel.CropDocViewModel

object Routes {
    const val HOME    = "home"
    const val CAMERA  = "camera"
    const val SENSOR  = "sensor"
    const val HISTORY = "history"
}

class MainActivity : ComponentActivity() {

    private val viewModel: CropDocViewModel by viewModels()

    // Request all required permissions up-front
    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { /* results handled reactively in UI */ }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        requestAppPermissions()

        setContent {
            CropDocTheme {
                CropDocApp(viewModel)
            }
        }
    }

    private fun requestAppPermissions() {
        val permissions = mutableListOf(Manifest.permission.CAMERA)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions += Manifest.permission.BLUETOOTH_SCAN
            permissions += Manifest.permission.BLUETOOTH_CONNECT
        } else {
            permissions += Manifest.permission.BLUETOOTH
            permissions += Manifest.permission.BLUETOOTH_ADMIN
            permissions += Manifest.permission.ACCESS_FINE_LOCATION
        }

        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            permissions += Manifest.permission.WRITE_EXTERNAL_STORAGE
        }

        permissionLauncher.launch(permissions.toTypedArray())
    }
}

@Composable
fun CropDocApp(viewModel: CropDocViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCamera  = { navController.navigate(Routes.CAMERA) },
                onNavigateToSensor  = { navController.navigate(Routes.SENSOR) },
                onNavigateToHistory = { navController.navigate(Routes.HISTORY) }
            )
        }

        composable(Routes.CAMERA) {
            CameraScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.SENSOR) {
            SensorScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HISTORY) {
            HistoryScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
