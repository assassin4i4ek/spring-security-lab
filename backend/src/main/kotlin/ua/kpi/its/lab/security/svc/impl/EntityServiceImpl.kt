package ua.kpi.its.lab.security.svc.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ua.kpi.its.lab.security.dto.BatteryResponse
import ua.kpi.its.lab.security.dto.VehicleRequest
import ua.kpi.its.lab.security.dto.VehicleResponse
import ua.kpi.its.lab.security.entity.Battery
import ua.kpi.its.lab.security.entity.Vehicle
import ua.kpi.its.lab.security.repo.VehicleRepository
import ua.kpi.its.lab.security.svc.VehicleService
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.jvm.optionals.getOrElse

@Service
class VehicleServiceImpl @Autowired constructor(
    private val repository: VehicleRepository
): VehicleService {
    override fun create(vehicle: VehicleRequest): VehicleResponse {
        val battery = vehicle.battery
        val newBattery = Battery(
            model = battery.model,
            manufacturer = battery.manufacturer,
            type = battery.type,
            capacity = battery.capacity,
            manufactureDate = this.stringToDate(battery.manufactureDate),
            chargeTime = battery.chargeTime,
            isFastCharge = battery.isFastCharge
        )
        var newVehicle = Vehicle(
            brand = vehicle.brand,
            model = vehicle.model,
            manufacturer = vehicle.manufacturer,
            manufactureDate = this.stringToDate(vehicle.manufactureDate),
            maxSpeed = vehicle.maxSpeed,
            price = this.stringToPrice(vehicle.price),
            isABS = vehicle.isABS,
            battery = newBattery
        )
        newBattery.vehicle = newVehicle
        newVehicle = this.repository.save(newVehicle)
        val vehicleResponse = this.vehicleEntityToDto(newVehicle)
        return vehicleResponse
    }

    override fun read(): List<VehicleResponse> {
        return this.repository.findAll().map(this::vehicleEntityToDto)
    }

    override fun readById(id: Long): VehicleResponse {
        val vehicle = this.getVehicleById(id)
        val vehicleResponse = this.vehicleEntityToDto(vehicle)
        return vehicleResponse
    }

    override fun updateById(id: Long, vehicle: VehicleRequest): VehicleResponse {
        val oldVehicle = this.getVehicleById(id)
        val battery = vehicle.battery

        oldVehicle.apply {
            brand = vehicle.brand
            model = vehicle.model
            manufacturer = vehicle.manufacturer
            manufactureDate = this@VehicleServiceImpl.stringToDate(vehicle.manufactureDate)
            maxSpeed = vehicle.maxSpeed
            price = this@VehicleServiceImpl.stringToPrice(vehicle.price)
            isABS = vehicle.isABS
        }
        oldVehicle.battery.apply {
            model = battery.model
            manufacturer = battery.manufacturer
            type = battery.type
            capacity = battery.capacity
            manufactureDate = this@VehicleServiceImpl.stringToDate(battery.manufactureDate)
            chargeTime = battery.chargeTime
            isFastCharge = battery.isFastCharge
        }
        val newVehicle = this.repository.save(oldVehicle)
        val vehicleResponse = this.vehicleEntityToDto(newVehicle)
        return vehicleResponse
    }

    override fun deleteById(id: Long): VehicleResponse {
        val vehicle = this.getVehicleById(id)
        this.repository.delete(vehicle)
        val vehicleResponse = vehicleEntityToDto(vehicle)
        return vehicleResponse
    }

    private fun getVehicleById(id: Long): Vehicle {
        return this.repository.findById(id).getOrElse {
            throw IllegalArgumentException("Vehicle not found by id = $id")
        }
    }

    private fun vehicleEntityToDto(vehicle: Vehicle): VehicleResponse {
        return VehicleResponse(
            id = vehicle.id,
            brand = vehicle.brand,
            model = vehicle.model,
            manufacturer = vehicle.manufacturer,
            manufactureDate = this.dateToString(vehicle.manufactureDate),
            maxSpeed = vehicle.maxSpeed,
            price = this.priceToString(vehicle.price),
            isABS = vehicle.isABS,
            battery = this.batteryEntityToDto(vehicle.battery)
        )
    }

    private fun batteryEntityToDto(battery: Battery): BatteryResponse {
        return BatteryResponse(
            id = battery.id,
            model = battery.model,
            manufacturer = battery.manufacturer,
            type = battery.type,
            capacity = battery.capacity,
            manufactureDate = this.dateToString(battery.manufactureDate),
            chargeTime = battery.chargeTime,
            isFastCharge = battery.isFastCharge
        )
    }

    private fun dateToString(date: Date): String {
        val instant = date.toInstant()
        val dateTime = instant.atOffset(ZoneOffset.UTC).toLocalDateTime()
        return dateTime.format(DateTimeFormatter.ISO_DATE_TIME)
    }

    private fun stringToDate(date: String): Date {
        val dateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME)
        val instant = dateTime.toInstant(ZoneOffset.UTC)
        return Date.from(instant)
    }

    private fun priceToString(price: BigDecimal): String = price.toString()

    private fun stringToPrice(price: String): BigDecimal = BigDecimal(price)
}