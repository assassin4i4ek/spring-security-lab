package ua.kpi.its.lab.security.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.*
import ua.kpi.its.lab.security.dto.BatteryRequest
import ua.kpi.its.lab.security.dto.VehicleRequest
import ua.kpi.its.lab.security.dto.VehicleResponse

@Composable
fun VehicleScreen(
    token: String,
    scope: CoroutineScope,
    client: HttpClient,
    snackbarHostState: SnackbarHostState
) {
    var vehicles by remember { mutableStateOf<List<VehicleResponse>>(listOf()) }
    var loading by remember { mutableStateOf(false) }
    var openDialog by remember { mutableStateOf(false) }
    var selectedVehicle by remember { mutableStateOf<VehicleResponse?>(null) }

    LaunchedEffect(token) {
        loading = true
        delay(1000)
        vehicles = withContext(Dispatchers.IO) {
            try {
                val response = client.get("http://localhost:8080/vehicles") {
                    bearerAuth(token)
                }
                loading = false
                response.body()
            }
            catch (e: Exception) {
                val msg = e.toString()
                snackbarHostState.showSnackbar(msg, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                vehicles
            }
        }
    }

    if (loading) {
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        )
    }
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    selectedVehicle = null
                    openDialog = true
                },
                content = {
                    Icon(Icons.Filled.Add, "add vehicle")
                }
            )
        }
    ) {
        if (vehicles.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                Text("No vehicles to show", modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
            }
        }
        else {
            LazyColumn(
                modifier = Modifier.background(MaterialTheme.colorScheme.surfaceVariant).fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(vehicles) { vehicle ->
                    VehicleItem(
                        vehicle = vehicle,
                        onEdit = {
                            selectedVehicle = vehicle
                            openDialog = true
                        },
                        onRemove = {
                            scope.launch {
                                withContext(Dispatchers.IO) {
                                    try {
                                        val response = client.delete("http://localhost:8080/vehicles/${vehicle.id}") {
                                            bearerAuth(token)
                                        }
                                        require(response.status.isSuccess())
                                    }
                                    catch(e: Exception) {
                                        val msg = e.toString()
                                        snackbarHostState.showSnackbar(msg, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                                    }
                                }

                                loading = true

                                vehicles = withContext(Dispatchers.IO) {
                                    try {
                                        val response = client.get("http://localhost:8080/vehicles") {
                                            bearerAuth(token)
                                        }
                                        loading = false
                                        response.body()
                                    }
                                    catch (e: Exception) {
                                        val msg = e.toString()
                                        snackbarHostState.showSnackbar(msg, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                                        vehicles
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        if (openDialog) {
            VehicleDialog(
                vehicle = selectedVehicle,
                token = token,
                scope = scope,
                client = client,
                onDismiss = {
                    openDialog = false
                },
                onError = {
                    scope.launch {
                        snackbarHostState.showSnackbar(it, withDismissAction = true, duration = SnackbarDuration.Indefinite)
                    }
                },
                onConfirm = {
                    openDialog = false
                    loading = true
                    scope.launch {
                        vehicles = withContext(Dispatchers.IO) {
                            try {
                                val response = client.get("http://localhost:8080/vehicles") {
                                    bearerAuth(token)
                                }
                                loading = false
                                response.body()
                            }
                            catch (e: Exception) {
                                loading = false
                                vehicles
                            }
                        }
                    }
                }
            )
        }
    }
}


@Composable
fun VehicleDialog(
    vehicle: VehicleResponse?,
    token: String,
    scope: CoroutineScope,
    client: HttpClient,
    onDismiss: () -> Unit,
    onError: (String) -> Unit,
    onConfirm: () -> Unit,
) {
    val battery = vehicle?.battery

    var brand by remember { mutableStateOf(vehicle?.brand ?: "") }
    var model by remember { mutableStateOf(vehicle?.model ?: "") }
    var manufacturer by remember { mutableStateOf(vehicle?.manufacturer ?: "") }
    var manufactureDate by remember { mutableStateOf(vehicle?.manufactureDate ?: "") }
    var maxSpeed by remember { mutableStateOf(vehicle?.maxSpeed?.toString() ?: "") }
    var price by remember { mutableStateOf(vehicle?.price ?: "") }
    var isAbs by remember { mutableStateOf(vehicle?.isABS ?: false) }
    var batteryModel by remember { mutableStateOf(battery?.model ?: "") }
    var batteryManufacturer by remember { mutableStateOf(battery?.manufacturer ?: "") }
    var batteryType by remember { mutableStateOf(battery?.type ?: "") }
    var batteryCapacity by remember { mutableStateOf(battery?.capacity?.toString() ?: "") }
    var batteryManufactureDate by remember { mutableStateOf(battery?.manufactureDate ?: "") }
    var batteryChargeTime by remember { mutableStateOf(battery?.chargeTime?.toString() ?: "") }
    var batteryIsFastCharge by remember { mutableStateOf(battery?.isFastCharge ?: false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.padding(16.dp).wrapContentSize()) {
            Column(
                modifier = Modifier.padding(16.dp, 8.dp).width(IntrinsicSize.Max).verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (vehicle == null) {
                    Text("Create vehicle")
                }
                else {
                    Text("Update vehicle")
                }

                HorizontalDivider()
                Text("Vehicle info")
                TextField(brand, { brand = it }, label = { Text("Brand") })
                TextField(model, { model = it }, label = { Text("Model") })
                TextField(manufacturer, { manufacturer = it }, label = { Text("Manufacturer") })
                TextField(manufactureDate, { manufactureDate = it }, label = { Text("Manufacture date") })
                TextField(maxSpeed, { maxSpeed = it }, label = { Text("Max speed") })
                TextField(price, { price = it }, label = { Text("Price") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(isAbs, { isAbs = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("ABS")
                }

                HorizontalDivider()
                Text("Battery info")
                TextField(batteryModel, { batteryModel = it }, label = { Text("Model") })
                TextField(batteryManufacturer, { batteryManufacturer = it }, label = { Text("Manufacturer") })
                TextField(batteryType, { batteryType = it }, label = { Text("Type") })
                TextField(batteryCapacity, { batteryCapacity = it }, label = { Text("Capacity") })
                TextField(batteryManufactureDate, { batteryManufactureDate = it }, label = { Text("Manufacture date") })
                TextField(batteryChargeTime, { batteryChargeTime = it }, label = { Text("Charge time") })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(batteryIsFastCharge, { batteryIsFastCharge = it })
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Fast charge")
                }

                HorizontalDivider()
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.fillMaxWidth(0.1f))
                    Button(
                        modifier = Modifier.weight(1f),
                        onClick = {
                            scope.launch {
                                try {
                                    val request = VehicleRequest(
                                        brand, model, manufacturer, manufactureDate, maxSpeed.toDouble(), price, isAbs,
                                        BatteryRequest(
                                            batteryModel, batteryManufacturer, batteryType, batteryCapacity.toInt(),
                                            batteryManufactureDate, batteryChargeTime.toDouble(), batteryIsFastCharge
                                        )
                                    )
                                    val response = if (vehicle == null) {
                                        client.post("http://localhost:8080/vehicles") {
                                            bearerAuth(token)
                                            setBody(request)
                                            contentType(ContentType.Application.Json)
                                        }
                                    } else {
                                        client.put("http://localhost:8080/vehicles/${vehicle.id}") {
                                            bearerAuth(token)
                                            setBody(request)
                                            contentType(ContentType.Application.Json)
                                        }
                                    }
                                    require(response.status.isSuccess())
                                    onConfirm()
                                }
                                catch (e: Exception) {
                                    val msg = e.toString()
                                    onError(msg)
                                }
                            }
                        }
                    ) {
                        if (vehicle == null) {
                            Text("Create")
                        }
                        else {
                            Text("Update")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun VehicleItem(vehicle: VehicleResponse, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(shape = CardDefaults.elevatedShape, elevation = CardDefaults.elevatedCardElevation()) {
        ListItem(
            overlineContent = {
                Text(vehicle.brand)
            },
            headlineContent = {
                Text(vehicle.model)
            },
            supportingContent = {
                Text("$${vehicle.price}")
            },
            trailingContent = {
                Row(modifier = Modifier.padding(0.dp, 20.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Edit",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(CircleShape).clickable(onClick = onEdit)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clip(CircleShape).clickable(onClick = onRemove)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                }
            }
        )
    }
}