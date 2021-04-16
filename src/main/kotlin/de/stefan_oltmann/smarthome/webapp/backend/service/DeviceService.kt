/*
 * Stefans Smart Home Project
 * Copyright (C) 2021 Stefan Oltmann
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package de.stefan_oltmann.smarthome.webapp.backend.service

import de.stefan_oltmann.smarthome.webapp.backend.data.DeviceGroupRepository
import de.stefan_oltmann.smarthome.webapp.backend.data.DeviceRepository
import de.stefan_oltmann.smarthome.webapp.backend.model.*
import de.stefan_oltmann.smarthome.webapp.backend.network.RestApi
import de.stefan_oltmann.smarthome.webapp.backend.network.RestApiClientFactory
import org.springframework.stereotype.Service
import java.io.File
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors
import java.util.stream.Stream
import javax.annotation.PostConstruct

@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
    private val deviceGroupRepository: DeviceGroupRepository
) {

    companion object {

        private val logger = Logger.getLogger(DeviceService::class.java.name)

        // TODO FIXME Temporary solution for loading settings
        private val baseUrl = File("server_url.txt").readText()
        private val authCode = File("auth_code.txt").readText()
    }

    private val restApi: RestApi = RestApiClientFactory.createRestApiClient(baseUrl, authCode)

    fun findAll(): List<Device> = deviceRepository.findAll()

    fun count() = deviceRepository.count()

    fun delete(device: Device) = deviceRepository.delete(device)

    fun save(device: Device): Device = deviceRepository.save(device)

    /**
     * Refresh device list from remote server
     */
    fun syncDeviceList() {

        val devicesResponse = restApi.findAllDevices().execute()

        if (!devicesResponse.isSuccessful) {
            logger.log(Level.SEVERE, "Request returned with HTTP ${devicesResponse.code()}")
            return
        }

        val devices: List<Device> = devicesResponse.body()!!

        deviceRepository.deleteAll()
        deviceRepository.saveAll(devices)

        logger.log(Level.INFO, "Refreshing devices from remote successful.")
    }

    fun findAllDeviceStates(): List<DeviceState> {

        val deviceStatesResponse = restApi.findAllDeviceStates().execute()

        if (!deviceStatesResponse.isSuccessful) {
            logger.log(Level.SEVERE, "Request returned with HTTP ${deviceStatesResponse.code()}")
            return emptyList()
        }

        val deviceStates: List<DeviceState> = deviceStatesResponse.body()!!

        logger.log(Level.INFO, "Refreshing device states from remote successful.")

        return deviceStates
    }

    fun setDevicePowerState(device: Device, powerState: DevicePowerState) {

        val deviceId = device.id

        if (deviceId != null)
            restApi.setDevicePowerState(deviceId, powerState).execute()
        else
            logger.log(Level.SEVERE, "Device has no ID: $device")
    }

    fun setDevicePercentage(device: Device, percentage: Int) {

        val deviceId = device.id

        if (deviceId != null)
            restApi.setDevicePercentage(deviceId, percentage).execute()
        else
            logger.log(Level.SEVERE, "Device has no ID: $device")
    }


    fun setDeviceTargetTemperature(device: Device, targetTemperature: Int) {

        val deviceId = device.id

        if (deviceId != null)
            restApi.setDeviceTargetTemperature(deviceId, targetTemperature).execute()
        else
            logger.log(Level.SEVERE, "Device has no ID: $device")
    }

    @PostConstruct
    fun populateTestData() {

        if (deviceGroupRepository.count() == 0L) {
            deviceGroupRepository.saveAll(
                Stream.of("Kitchen", "Living Room", "Bedroom")
                    .map { name: String? -> DeviceGroup(name!!) }
                    .collect(Collectors.toList()))
        }

        if (deviceRepository.count() == 0L) {
            val r = Random(0)
            val deviceGroups = deviceGroupRepository.findAll()
            deviceRepository.saveAll(
                Stream.of(
                    "Switch 1", "Switch 2", "Switch 3", "Switch 4",
                    "Dimmer 1", "Dimmer 2", "Dimmer 3", "Dimmer 4", "Dimmer 5",
                    "Roller shutter 1", "Roller shutter 2"
                )
                    .map { name: String ->
                        val id = name.toLowerCase().replace(" ".toRegex(), "_")
                        val device = Device(id)
                        device.name = name
                        device.group = deviceGroups[r.nextInt(deviceGroups.size)]
                        device.type = DeviceType.values()[r.nextInt(DeviceType.values().size)]
                        device
                    }.collect(Collectors.toList())
            )
        }
    }
}
