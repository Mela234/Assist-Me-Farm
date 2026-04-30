package com.cropdoc.app

import android.Manifest
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cropdoc.app.ui.screens.CameraScreen
import com.cropdoc.app.ui.screens.HistoryScreen
import com.cropdoc.app.ui.screens.HomeScreen
import com.cropdoc.app.ui.screens.SensorScreen
import com.cropdoc.app.ui.theme.CropDocTheme
import com.cropdoc.app.viewmodel.CropDocViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Locale

// DataStore for language preference
val Context.dataStore by preferencesDataStore(name = "settings")
val LANGUAGE_KEY = stringPreferencesKey("selected_language")

object Routes {
    const val HOME    = "home"
    const val CAMERA  = "camera"
    const val SENSOR  = "sensor"
    const val HISTORY = "history"
}

data class AppLanguage(
    val code: String,
    val displayName: String
)

val SUPPORTED_LANGUAGES = listOf(
    AppLanguage("en", "English"),
    AppLanguage("sn", "Shona"),
    AppLanguage("am", "Amharic / አማርኛ")
)

class MainActivity : ComponentActivity() {

    private val viewModel: CropDocViewModel by viewModels()

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyStoredLanguage()
        enableEdgeToEdge()
        requestAppPermissions()

        setContent {
            CropDocTheme {
                CropDocApp(viewModel)
            }
        }
    }

    private fun applyStoredLanguage() {
        val savedCode = runBlocking {
            dataStore.data.map { prefs ->
                prefs[LANGUAGE_KEY] ?: "en"
            }.first()
        }
        applyLocale(this, savedCode)
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

    override fun attachBaseContext(newBase: Context) {
        val savedCode = runBlocking {
            newBase.dataStore.data.map { prefs ->
                prefs[LANGUAGE_KEY] ?: "en"
            }.first()
        }
        val locale = Locale(savedCode)
        Locale.setDefault(locale)
        val config = newBase.resources.configuration
        config.setLocale(locale)
        super.attachBaseContext(newBase.createConfigurationContext(config))
    }
}

fun applyLocale(context: Context, languageCode: String) {
    val locale = Locale(languageCode)
    Locale.setDefault(locale)
    val config = context.resources.configuration
    config.setLocale(locale)
    @Suppress("DEPRECATION")
    context.resources.updateConfiguration(config, context.resources.displayMetrics)
}

@Composable
fun CropDocApp(viewModel: CropDocViewModel) {
    val navController = rememberNavController()
    val context = LocalContext.current
    var showLanguagePicker by remember { mutableStateOf(false) }

    val currentLanguageCode by remember {
        context.dataStore.data.map { prefs ->
            prefs[LANGUAGE_KEY] ?: "en"
        }
    }.collectAsState(initial = "en")

    NavHost(navController = navController, startDestination = Routes.HOME) {

        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onNavigateToCamera   = { navController.navigate(Routes.CAMERA) },
                onNavigateToSensor   = { navController.navigate(Routes.SENSOR) },
                onNavigateToHistory  = { navController.navigate(Routes.HISTORY) },
                onOpenLanguagePicker = { showLanguagePicker = true }
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

    if (showLanguagePicker) {
        LanguagePickerDialog(
            currentCode = currentLanguageCode,
            onDismiss = { showLanguagePicker = false },
            onLanguageSelected = { code ->
                viewModel.setLanguage(code)
                showLanguagePicker = false
                (context as? MainActivity)?.recreate()
            }
        )
    }
}

@Composable
fun LanguagePickerDialog(
    currentCode: String,
    onDismiss: () -> Unit,
    onLanguageSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                Icons.Default.Language,
                contentDescription = null,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text("Select Language / Sarudza Mutauro / ቋንቋ ይምረጡ")
        },
        text = {
            LazyColumn {
                items(SUPPORTED_LANGUAGES) { lang ->
                    val isSelected = lang.code == currentCode
                    TextButton(
                        onClick = { onLanguageSelected(lang.code) },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = if (isSelected) "✓  ${lang.displayName}"
                            else "   ${lang.displayName}",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}