package org.dymka.justipinfo.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Dns
import androidx.compose.material.icons.outlined.Http
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import org.dymka.justipinfo.BuildConfig
import org.dymka.justipinfo.R
import org.dymka.justipinfo.data.AppRepository

data class PresetEndpoint(
    val name: String,
    val url: String,
    val format: String,
)

val PRESET_ENDPOINTS =
    listOf(
        PresetEndpoint(name = "ipinfo.io", url = "https://ipinfo.io/json", format = "JSON"),
        PresetEndpoint(name = "icanhazip", url = "https://icanhazip.com", format = "Plain"),
        PresetEndpoint(name = "api.ipify.org", url = "https://api.ipify.org", format = "Plain"),
        PresetEndpoint(name = "AWS checkip", url = "https://checkip.amazonaws.com", format = "Plain"),
        PresetEndpoint(name = "ident.me", url = "https://ident.me", format = "Plain"),
        PresetEndpoint(name = "ifconfig.co", url = "https://ifconfig.co/json", format = "JSON"),
    )

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SettingsDialog(
    baseUrl: String,
    onBaseUrlChange: (String) -> Unit,
    onResetBaseUrl: () -> Unit,
    isDarkTheme: Boolean,
    onThemeToggle: (Boolean) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusManager = LocalFocusManager.current
    val isInvalidUrl =
        remember(baseUrl) {
            baseUrl.isNotBlank() &&
                !baseUrl.startsWith("http://", ignoreCase = true) &&
                !baseUrl.startsWith("https://", ignoreCase = true)
        }
    val isDefaultUrl = remember(baseUrl) { baseUrl.trim() == AppRepository.DEFAULT_URL }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        icon = {
            Icon(
                imageVector = Icons.Outlined.Settings,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp),
            )
        },
        title = {
            Text(
                text = stringResource(R.string.settings_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
            ) {
                // Section 1: Service Endpoint
                SectionHeader(
                    icon = Icons.Outlined.Dns,
                    title = stringResource(R.string.service_endpoint_label),
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = baseUrl,
                    onValueChange = onBaseUrlChange,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    isError = isInvalidUrl,
                    placeholder = {
                        Text(
                            text = stringResource(R.string.custom_url_hint),
                            style = MaterialTheme.typography.bodySmall,
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Http,
                            contentDescription = null,
                            tint =
                                if (isInvalidUrl) {
                                    MaterialTheme.colorScheme.error
                                } else {
                                    MaterialTheme.colorScheme.primary
                                },
                        )
                    },
                    trailingIcon = {
                        if (baseUrl.isNotEmpty()) {
                            IconButton(onClick = { onBaseUrlChange("") }) {
                                Icon(
                                    imageVector = Icons.Outlined.Clear,
                                    contentDescription = stringResource(R.string.clear_button),
                                )
                            }
                        }
                    },
                    supportingText =
                        if (isInvalidUrl) {
                            {
                                Text(
                                    text = stringResource(R.string.invalid_url_warning),
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            }
                        } else {
                            null
                        },
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions =
                        KeyboardOptions(
                            keyboardType = KeyboardType.Uri,
                            imeAction = ImeAction.Done,
                        ),
                    keyboardActions =
                        KeyboardActions(
                            onDone = { focusManager.clearFocus() },
                        ),
                    textStyle = MaterialTheme.typography.bodyMedium,
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = stringResource(R.string.presets_label),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    PRESET_ENDPOINTS.forEach { preset ->
                        val isSelected = baseUrl.trim() == preset.url
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                onBaseUrlChange(preset.url)
                                focusManager.clearFocus()
                            },
                            label = {
                                Text(
                                    text = preset.name,
                                    style = MaterialTheme.typography.labelSmall,
                                )
                            },
                            leadingIcon =
                                if (isSelected) {
                                    {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                        )
                                    }
                                } else {
                                    null
                                },
                            shape = RoundedCornerShape(8.dp),
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                )

                // Section 2: Appearance
                SectionHeader(
                    icon = Icons.Outlined.DarkMode,
                    title = stringResource(R.string.appearance_label),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.dark_theme_title),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Text(
                                text = stringResource(R.string.dark_theme_subtitle),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        Switch(
                            checked = isDarkTheme,
                            onCheckedChange = onThemeToggle,
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 16.dp),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f),
                )

                // Section 3: About
                SectionHeader(
                    icon = Icons.Outlined.Info,
                    title = stringResource(R.string.about_title),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        ),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Public,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp),
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = stringResource(R.string.app_name),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer,
                            ) {
                                Text(
                                    text = "v${BuildConfig.VERSION_NAME}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = stringResource(R.string.app_description),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        },
        dismissButton = {
            if (!isDefaultUrl) {
                TextButton(
                    onClick = onResetBaseUrl,
                ) {
                    Text(stringResource(R.string.reset_to_default))
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
            ) {
                Text(stringResource(R.string.done_button))
            }
        },
    )
}

@Composable
private fun SectionHeader(
    icon: ImageVector,
    title: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun SettingsDialogPreview() {
    org.dymka.justipinfo.ui.theme.JustIpInfoTheme(darkTheme = false) {
        SettingsDialog(
            baseUrl = "https://ipinfo.io/json",
            onBaseUrlChange = {},
            onResetBaseUrl = {},
            isDarkTheme = false,
            onThemeToggle = {},
            onDismiss = {},
        )
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
private fun SettingsDialogDarkPreview() {
    org.dymka.justipinfo.ui.theme.JustIpInfoTheme(darkTheme = true) {
        SettingsDialog(
            baseUrl = "https://api.ipify.org",
            onBaseUrlChange = {},
            onResetBaseUrl = {},
            isDarkTheme = true,
            onThemeToggle = {},
            onDismiss = {},
        )
    }
}
