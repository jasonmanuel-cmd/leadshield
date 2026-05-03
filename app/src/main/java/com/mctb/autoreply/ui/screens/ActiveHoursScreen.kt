package com.mctb.autoreply.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.mctb.autoreply.R
import com.mctb.autoreply.data.AppPreferences
import com.mctb.autoreply.ui.components.AnimatedMeshBackground
import com.mctb.autoreply.ui.components.GlassCard
import com.mctb.autoreply.ui.theme.NeonCyan
import com.mctb.autoreply.ui.theme.SpaceBlack
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveHoursScreen(navController: NavController) {
    val context = LocalContext.current
    val prefs = remember { AppPreferences(context) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val startHour by prefs.startHour.collectAsState(initial = AppPreferences.DEFAULT_START_HOUR)
    val startMinute by prefs.startMinute.collectAsState(initial = AppPreferences.DEFAULT_START_MINUTE)
    val endHour by prefs.endHour.collectAsState(initial = AppPreferences.DEFAULT_END_HOUR)
    val endMinute by prefs.endMinute.collectAsState(initial = AppPreferences.DEFAULT_END_MINUTE)
    val isAlwaysOn by prefs.isAlwaysOn.collectAsState(initial = false)

    var localAlwaysOn by remember { mutableStateOf(isAlwaysOn) }
    var localStartHour by remember { mutableStateOf(startHour) }
    var localStartMinute by remember { mutableStateOf(startMinute) }
    var localEndHour by remember { mutableStateOf(endHour) }
    var localEndMinute by remember { mutableStateOf(endMinute) }

    LaunchedEffect(isAlwaysOn) { localAlwaysOn = isAlwaysOn }
    LaunchedEffect(startHour) { localStartHour = startHour }
    LaunchedEffect(startMinute) { localStartMinute = startMinute }
    LaunchedEffect(endHour) { localEndHour = endHour }
    LaunchedEffect(endMinute) { localEndMinute = endMinute }

    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.active_hours_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().background(SpaceBlack)) {
            AnimatedMeshBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Always On switch
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.always_active),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = NeonCyan
                            )
                            Text(
                                text = stringResource(R.string.always_active_desc),
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                        Switch(
                            checked = localAlwaysOn,
                            onCheckedChange = { localAlwaysOn = it }
                        )
                    }
                }

                if (!localAlwaysOn) {
                    Text(
                        text = stringResource(R.string.active_hours_desc),
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )

                    // Start time
                    Column {
                        Text(
                            text = stringResource(R.string.start_time),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showStartTimePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(NeonCyan.copy(alpha = 0.5f)))
                        ) {
                            Text(
                                text = stringResource(R.string.time_format, localStartHour, localStartMinute),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                    }

                    // End time
                    Column {
                        Text(
                            text = stringResource(R.string.end_time),
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = NeonCyan
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedButton(
                            onClick = { showEndTimePicker = true },
                            modifier = Modifier.fillMaxWidth(),
                            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(brush = androidx.compose.ui.graphics.SolidColor(NeonCyan.copy(alpha = 0.5f)))
                        ) {
                            Text(
                                text = stringResource(R.string.time_format, localEndHour, localEndMinute),
                                style = MaterialTheme.typography.titleLarge,
                                color = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = {
                        scope.launch {
                            prefs.setAlwaysOn(localAlwaysOn)
                            if (!localAlwaysOn) {
                                prefs.setActiveHours(localStartHour, localStartMinute, localEndHour, localEndMinute)
                            }
                            snackbarHostState.showSnackbar(context.getString(R.string.hours_saved))
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonCyan)
                ) {
                    Text(stringResource(R.string.save), fontWeight = FontWeight.Bold, color = SpaceBlack)
                }
            }
        }
    }

    if (showStartTimePicker) {
        TimePickerDialog(
            onDismiss = { showStartTimePicker = false },
            onConfirm = { hour, minute -> localStartHour = hour; localStartMinute = minute; showStartTimePicker = false },
            initialHour = localStartHour,
            initialMinute = localStartMinute
        )
    }

    if (showEndTimePicker) {
        TimePickerDialog(
            onDismiss = { showEndTimePicker = false },
            onConfirm = { hour, minute -> localEndHour = hour; localEndMinute = minute; showEndTimePicker = false },
            initialHour = localEndHour,
            initialMinute = localEndMinute
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(onDismiss: () -> Unit, onConfirm: (Int, Int) -> Unit, initialHour: Int, initialMinute: Int) {
    val timePickerState = rememberTimePickerState(initialHour = initialHour, initialMinute = initialMinute, is24Hour = true)
    Dialog(onDismissRequest = onDismiss) {
        GlassCard(cornerRadius = 24.dp) {
            Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = stringResource(R.string.select_time), style = MaterialTheme.typography.titleMedium, color = NeonCyan)
                Spacer(modifier = Modifier.height(16.dp))
                TimePicker(state = timePickerState)
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
                    TextButton(onClick = onDismiss) { Text(stringResource(R.string.cancel), color = Color.White.copy(alpha = 0.7f)) }
                    TextButton(onClick = { onConfirm(timePickerState.hour, timePickerState.minute) }) { Text(stringResource(R.string.ok), color = NeonCyan, fontWeight = FontWeight.Bold) }
                }
            }
        }
    }
}
