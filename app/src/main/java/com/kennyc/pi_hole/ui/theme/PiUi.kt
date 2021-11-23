package com.kennyc.pi_hole.ui.theme

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.kennyc.data.pi_hole.model.PiholeSummary
import com.kennyc.data.pi_hole.model.PiholeSystemStatus
import com.kennyc.pi_hole.MainViewModel
import com.kennyc.pi_hole.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

@Composable
fun PiholeScreen(viewModel: MainViewModel) {
    val summary by viewModel.stats.observeAsState()
    summary?.let {
        var showDialog by remember { mutableStateOf(false) }

        val enableDisableClick = {
            // Do the opposite of what the current state is
            if (it.first.isActive) {
                showDialog = true
            } else {
                viewModel.enablePihole()
            }
        }

        val dialogClick: (Int) -> Unit = { index ->
            showDialog = false
            when (index) {
                // INDEFINITELY
                0 -> viewModel.disablePihole(null)

                //30 SECONDS
                1 -> viewModel.disablePihole(30)

                // 1 MINUTE
                2 -> viewModel.disablePihole(60)

                // 5 MINUTES
                3 -> viewModel.disablePihole(
                    TimeUnit.MINUTES.toSeconds(5).toInt()
                )
            }
        }

        val dismiss = {
            showDialog = false
        }

        BuildUi(it.first, it.second, showDialog, enableDisableClick, dialogClick, dismiss)
    }

    viewModel
        .statusError
        .observeAsState()
        .value
        ?.let {
            Toast.makeText(LocalContext.current, R.string.status_failed, Toast.LENGTH_LONG)
                .show()
        }

    viewModel
        .errorMessage
        .observeAsState()
        .value
        ?.let {
            ErrorScreen(it) { viewModel.refresh() }
        }
}

@Composable
fun BuildUi(
    status: PiholeSystemStatus,
    summary: PiholeSummary,
    showDialog: Boolean,
    enableDisableClick: () -> Unit = {},
    dialogClick: (Int) -> Unit = {},
    dialogDismiss: () -> Unit = {},
) {
    val coroutineScope = rememberCoroutineScope()
    val state = rememberScaffoldState(rememberDrawerState(DrawerValue.Closed))

    Scaffold(scaffoldState = state,
        topBar = { TopBar(state, coroutineScope) },
        drawerContent = { DrawerContent(status, showDialog, enableDisableClick, dialogClick,dialogDismiss) },
        content = {
            Column(Modifier.verticalScroll(ScrollState(0), enabled = true)) {
                StatCard(
                    stringResource(R.string.total_queries, summary.clientCount),
                    summary.dnsQueryCount,
                    PiholeGreen
                )
                StatCard(
                    stringResource(R.string.queries_blocked),
                    summary.adsBlockedCount,
                    PiholeBlue
                )
                StatCard(
                    stringResource(R.string.percent_blocked),
                    "${summary.adsPercentageBlocked}%",
                    PiholeOrange
                )
                StatCard(
                    stringResource(R.string.domains_blocked),
                    summary.blockedDomainsCount,
                    PiholeRed
                )
            }
        })
}

@Composable
private fun StatCard(
    title: String,
    count: String,
    bgColor: Color
) {
    Card(
        Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 16.dp),
        backgroundColor = bgColor
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            DardTitle(title)
            CardCount(count)
        }
    }
}

@Composable
private fun DardTitle(title: String) {
    Text(
        text = title,
        Modifier.padding(start = 8.dp, top = 8.dp),
        style = TextStyle(
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontStyle = FontStyle.Normal
        )
    )
}

@Composable
private fun CardCount(count: String) {
    Text(
        text = count,
        Modifier.padding(start = 8.dp, top = 32.dp, bottom = 16.dp),
        style = TextStyle(
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontStyle = FontStyle.Normal
        )
    )
}

@Composable
private fun TopBar(state: ScaffoldState, coroutineScope: CoroutineScope) {
    TopAppBar(title = { Text(stringResource(R.string.app_name)) },
        backgroundColor = Purple500,
        navigationIcon = {
            Icon(Icons.Default.Menu, null, Modifier.clickable {
                coroutineScope.launch { state.drawerState.open() }
            })
        })
}

@Composable
private fun DrawerContent(
    status: PiholeSystemStatus,
    showDialog: Boolean,
    enableDisableClick: () -> Unit = {},
    dialogClick: (Int) -> Unit = {},
    dialogDismiss: () -> Unit = {}
) {
    Column(modifier = Modifier.padding(start = 8.dp)) {
        Image(
            painter = painterResource(R.drawable.pihole_logo),
            contentDescription = null,
            modifier = Modifier
                .size(96.dp)
                .padding(top = 16.dp, bottom = 16.dp)
        )

        // Status
        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(if (status.isActive) PiholeGreen else PiholeRed, CircleShape)
            )
            Text(
                stringResource(if (status.isActive) R.string.status_active else R.string.status_disabled),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Load
        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(PiholeGreen, CircleShape)
            )
            Text(
                stringResource(
                    R.string.system_load,
                    status.load[0],
                    status.load[1],
                    status.load[2]
                ),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Memory
        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val color = when {
                status.memoryUsage >= 70 -> PiholeRed
                status.memoryUsage >= 40 -> PiholeOrange
                else -> PiholeGreen
            }

            Box(
                modifier = Modifier
                    .size(16.dp)
                    .background(color, CircleShape)
            )
            Text(
                text = stringResource(id = R.string.memory_usage, status.memoryUsage),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        // Temperature
        Row(
            modifier = Modifier.padding(start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(R.drawable.ic_temperature), contentDescription = null
            )
            Text(
                text = stringResource(id = R.string.temperature, status.temp),
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        DisableEnable(status.isActive, showDialog, enableDisableClick, dialogClick, dialogDismiss)
    }
}

@Composable
private fun DisableEnable(
    active: Boolean,
    showDialog: Boolean,
    onClick: () -> Unit = {},
    dialogClick: (Int) -> Unit = {},
    dialogDismiss: () -> Unit = {}
) {
    val buttonText = if (active) R.string.disable else R.string.enable
    val drawable = if (active) R.drawable.ic_baseline_stop_24 else
        R.drawable.ic_baseline_play_arrow_24

    TextButton(onClick = onClick) {
        Icon(painterResource(drawable), null, tint = Gray)
        Text(stringResource(buttonText), color = Gray, style = TextStyle(fontSize = 16.sp))
    }

    if (showDialog) {
        ShowDisableDialog(dialogClick, dialogDismiss)
    }
}

@Composable
private fun ShowDisableDialog(
    dialogClick: (Int) -> Unit = {},
    dialogDismiss: () -> Unit = {},
) {
    Dialog(onDismissRequest = dialogDismiss) {
        Card() {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = stringResource(R.string.disable_title),
                    style = Typography.h6,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                stringArrayResource(id = R.array.disable_time).forEachIndexed() { index, item ->
                    TextButton(
                        onClick = { dialogClick.invoke(index) },
                    ) {
                        Text(text = item, color = Gray)
                    }
                }

                Button(
                    onClick = dialogDismiss,
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(top = 16.dp)
                ) {
                    Text(stringResource(R.string.Cancel))
                }
            }
        }
    }
}

@Composable
private fun ErrorScreen(@StringRes errorMessage: Int, refresh: () -> Unit = {}) {
    if (errorMessage != -1) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(text = stringResource(errorMessage), style = Typography.subtitle1)
            Button(onClick = refresh, modifier = Modifier.padding(top = 16.dp)) {
                Text(text = stringResource(id = R.string.retry))
            }
        }
    }
}

@Preview()
@Composable
fun PreviewUi() {
    val status = PiholeSystemStatus(30.00, floatArrayOf(.1f, .2f, .3f), 19.8, true)
    val summary = PiholeSummary("1,234", "9,876", "10,654", 15.54, 30, "Good")
    BuildUi(status, summary, false)
}
