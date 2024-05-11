package ua.kpi.its.lab.security.dto

import kotlinx.serialization.Serializable
import java.io.Serial

@Serializable
data class VehicleRequest(
    var brand: String,
    var model: String,
    var manufacturer: String,
    var manufactureDate: String,
    var maxSpeed: Double,
    var price: String,
    var isABS: Boolean,
    var battery: BatteryRequest
)

@Serializable
data class VehicleResponse(
    var id: Long,
    var brand: String,
    var model: String,
    var manufacturer: String,
    var manufactureDate: String,
    var maxSpeed: Double,
    var price: String,
    var isABS: Boolean,
    var battery: BatteryResponse
)

@Serializable
data class BatteryRequest(
    var model: String,
    var manufacturer: String,
    var type: String,
    var capacity: Int,
    var manufactureDate: String,
    var chargeTime: Double,
    var isFastCharge: Boolean
)

@Serializable
data class BatteryResponse(
    var id: Long,
    var model: String,
    var manufacturer: String,
    var type: String,
    var capacity: Int,
    var manufactureDate: String,
    var chargeTime: Double,
    var isFastCharge: Boolean
)
