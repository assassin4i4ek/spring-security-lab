package ua.kpi.its.lab.security.svc

import ua.kpi.its.lab.security.dto.VehicleRequest
import ua.kpi.its.lab.security.dto.VehicleResponse

interface VehicleService {
    /**
     * Creates a new Vehicle record.
     *
     * @param vehicle: The VehicleRequest instance to be inserted
     * @return: The recently created VehicleResponse instance
     */
    fun create(vehicle: VehicleRequest): VehicleResponse

    /**
     * Reads all created Vehicle records.
     *
     * @return: List of created VehicleResponse records
     */
    fun read(): List<VehicleResponse>

    /**
     * Reads a Vehicle record by its id.
     * The order is determined by the order of creation.
     *
     * @param id: The id of VehicleRequest record
     * @return: The VehicleResponse instance at index
     */
    fun readById(id: Long): VehicleResponse

    /**
     * Updates a VehicleRequest record data.
     *
     * @param id: The id of the Vehicle instance to be updated
     * @param vehicle: The VehicleRequest with new Vehicle values
     * @return: The updated VehicleResponse record
     */
    fun updateById(id: Long, vehicle: VehicleRequest): VehicleResponse

    /**
     * Deletes a VehicleRequest record by its index.
     * The order is determined by the order of creation.
     *
     * @param id: The id of Vehicle record to delete
     * @return: The deleted VehicleResponse instance at index
     */
    fun deleteById(id: Long): VehicleResponse
}