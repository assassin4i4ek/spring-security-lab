package ua.kpi.its.lab.security.repo

import org.springframework.data.jpa.repository.JpaRepository
import ua.kpi.its.lab.security.entity.Battery
import ua.kpi.its.lab.security.entity.Vehicle

interface VehicleRepository : JpaRepository<Vehicle, Long>

interface BatteryRepository : JpaRepository<Battery, Long>