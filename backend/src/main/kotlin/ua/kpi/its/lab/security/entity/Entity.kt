package ua.kpi.its.lab.security.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "vehicles")
class Vehicle(
    @Column
    var brand: String,

    @Column
    var model: String,

    @Column
    var manufacturer: String,

    @Column
    var manufactureDate: Date,

    @Column
    var maxSpeed: Double,

    @Column
    var price: BigDecimal,

    @Column
    var isABS: Boolean,

    @OneToOne(cascade = [CascadeType.ALL])
    @JoinColumn(name = "battery_id", referencedColumnName = "id")
    var battery: Battery
) : Comparable<Vehicle> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1

    override fun compareTo(other: Vehicle): Int {
        val equal = this.model == other.model && this.manufactureDate.time == other.manufactureDate.time
        return if (equal) 0 else 1
    }

    override fun toString(): String {
        return "Vehicle(model=$model, manufactureDate=$manufactureDate, battery=$battery)"
    }
}

@Entity
@Table(name = "batteries")
class Battery(
    @Column
    var model: String,

    @Column
    var manufacturer: String,

    @Column
    var type: String,

    @Column
    var capacity: Int,

    @Column
    var manufactureDate: Date,

    @Column
    var chargeTime: Double,

    @Column
    var isFastCharge: Boolean,

    @OneToOne(mappedBy = "battery")
    var vehicle: Vehicle? = null,
): Comparable<Battery> {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = -1

    override fun compareTo(other: Battery): Int {
        val equal = this.model == other.model && this.capacity == other.capacity
        return if (equal) 0 else 1
    }

    override fun toString(): String {
        return "Battery(model=$model, capacity=$capacity)"
    }
}