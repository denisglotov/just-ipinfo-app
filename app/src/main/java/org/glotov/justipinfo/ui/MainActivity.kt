package org.glotov.justipinfo.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.glotov.justipinfo.BuildConfig
import org.glotov.justipinfo.data.AppRepository
import org.glotov.justipinfo.data.IpService
import org.glotov.justipinfo.data.Logger
import org.glotov.justipinfo.ui.theme.JustIpInfoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Manual Dependency Injection
        val logger = Logger(applicationContext)
        val ipService = IpService()
        val repository = AppRepository(ipService, logger, applicationContext)
        val viewModelFactory = MainViewModelFactory(repository)

        setContent {
            val viewModel: MainViewModel = viewModel(factory = viewModelFactory)
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()
            val baseUrl by viewModel.baseUrl.collectAsState()

            JustIpInfoTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    MainScreen(
                        viewModel = viewModel,
                        isDarkTheme = isDarkTheme,
                        onThemeToggle = { viewModel.toggleDarkTheme(it) },
                        baseUrl = baseUrl,
                        onBaseUrlChange = { viewModel.updateBaseUrl(it) },
                    )
                }
            }
        }
    }
}

@Composable
fun MainScreen(
    viewModel: MainViewModel,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    baseUrl: String,
    onBaseUrlChange: (String) -> Unit,
) {
    val logs by viewModel.logs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val scrollState = rememberScrollState()
    var showDialog by remember { mutableStateOf(false) }
    val uriHandler = LocalUriHandler.current

    // Scroll to bottom when logs change
    LaunchedEffect(logs) { scrollState.animateScrollTo(scrollState.maxValue) }

    if (showDialog) {
        val presets =
            listOf(
                "https://api.ipify.org",
                "https://checkip.amazonaws.com",
                "https://icanhazip.com",
                "https://ident.me",
                "https://ifconfig.co/json",
                "https://ifconfig.me",
                "https://ipinfo.io/json",
            )
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Settings") },
            text = {
                Column {
                    Text("Version: ${BuildConfig.VERSION_NAME}", style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Service URL:", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = baseUrl,
                        onValueChange = onBaseUrlChange,
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Column {
                        presets.forEach { url ->
                            TextButton(
                                onClick = { onBaseUrlChange(url) },
                                contentPadding = PaddingValues(0.dp),
                                modifier = Modifier.height(32.dp),
                            ) {
                                Text(
                                    text = url,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.primary,
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    TextButton(
                        onClick = {
                            uriHandler.openUri(
                                "https://github.com/denisglotov/just-ipinfo-app",
                            )
                        },
                        contentPadding = PaddingValues(0.dp),
                    ) { Text("Source Code") }
                }
            },
            confirmButton = {
                IconButton(onClick = { showDialog = false }) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Confirm",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(16.dp),
    ) {
        // Top Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Button(
                    onClick = { viewModel.onRequestClicked() },
                    enabled = !isLoading,
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary,
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text("Request")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { viewModel.onClearClicked() },
                    enabled = !isLoading,
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                        ),
                ) { Text("Clear") }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(
                    checked = isDarkTheme,
                    onCheckedChange = { onThemeToggle(it) },
                    modifier = Modifier.scale(0.8f),
                    colors =
                        SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                            uncheckedThumbColor = MaterialTheme.colorScheme.secondary,
                            uncheckedTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                        ),
                )

                IconButton(onClick = { showDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Logs:",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Log Display Area
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    .padding(8.dp)
                    .verticalScroll(scrollState),
        ) {
            Text(
                text = logs.ifEmpty { "No logs yet." },
                fontFamily = FontFamily.Monospace,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}
