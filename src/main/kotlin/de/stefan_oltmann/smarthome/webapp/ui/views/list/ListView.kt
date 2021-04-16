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
package de.stefan_oltmann.smarthome.webapp.ui.views.list

import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dialog.Dialog
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import de.stefan_oltmann.smarthome.webapp.backend.model.Device
import de.stefan_oltmann.smarthome.webapp.backend.model.DevicePowerState
import de.stefan_oltmann.smarthome.webapp.backend.service.DeviceGroupService
import de.stefan_oltmann.smarthome.webapp.backend.service.DeviceService
import de.stefan_oltmann.smarthome.webapp.ui.MainLayout
import de.stefan_oltmann.smarthome.webapp.ui.views.list.DeviceForm.SaveEvent
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

@Component
@Scope("prototype")
@Route(value = "", layout = MainLayout::class)
@PageTitle("Devices")
class ListView(
    private val deviceService: DeviceService,
    private val deviceGroupService: DeviceGroupService
) : VerticalLayout() {

    private val form: DeviceForm
    private val dialog = Dialog()
    private val flexLayout: FlexLayout
    private val deviceStateChangeCallback: DeviceStateChangeCallback

    private val deviceBoxPerDeviceMap = mutableMapOf<String, DeviceBoxLayout>()

    init {

        deviceStateChangeCallback = object : DeviceStateChangeCallback {

            override fun onDevicePowerStateChanged(device: Device, powerState: DevicePowerState) {
                deviceService.setDevicePowerState(device, powerState)
                println("$device -> $powerState")
            }

            override fun onDevicePercentageChanged(device: Device, percentage: Int) {
                deviceService.setDevicePercentage(device, percentage)
                println("$device -> $percentage%")
            }

            override fun onDeviceTargetTemperatureChanged(device: Device, targetTemperature: Int) {
                deviceService.setDeviceTargetTemperature(device, targetTemperature)
                println("$device -> $targetTemperature°C")
            }
        }

        addClassName("list-view")
        setSizeFull()

        form = DeviceForm(deviceGroupService.findAll())
        form.addListener(SaveEvent::class.java) { event: SaveEvent -> saveDevice(event) }
        form.addListener(DeviceForm.DeleteEvent::class.java) { event -> deleteDevice(event) }
        form.addListener(DeviceForm.CloseEvent::class.java) { closeEditor() }

        dialog.add(form)
        dialog.width = "360px"

        flexLayout = FlexLayout()
        flexLayout.flexWrap = FlexLayout.FlexWrap.WRAP

        add(createToolBar(), flexLayout)

        updateList()
        closeEditor()
    }

    private fun deleteDevice(event: DeviceForm.DeleteEvent) {

        deviceService.delete(event.device!!)

        updateList()
        closeEditor()
    }

    private fun saveDevice(event: SaveEvent) {

        deviceService.save(event.device!!)

        updateList()
        closeEditor()
    }

    private fun createToolBar(): HorizontalLayout {

        val tabs = Tabs()

        for (deviceGroup in deviceGroupService.findAll())
            tabs.add(Tab(deviceGroup.name))

        val refreshButton = Button()

        refreshButton.icon = Icon(VaadinIcon.REFRESH)

        refreshButton.addClickListener {

            deviceService.syncDeviceList()
            updateList()
        }

        return HorizontalLayout(tabs, refreshButton)
    }

    private fun editDevice(device: Device?) {

        if (device == null) {
            closeEditor()
            return
        }

        form.setDevice(device)
        dialog.open()
        addClassName("editing")
    }

    private fun closeEditor() {

        form.setDevice(null)

        dialog.close()

        removeClassName("editing")
    }

    private fun createRightClickMenu(
        deviceBox: DeviceBoxLayout,
        device: Device
    ) {

        val contextMenu = ContextMenu()

        contextMenu.target = deviceBox

        contextMenu.addItem("Edit") { editDevice(device) }
    }

    private fun updateList() {

        deviceBoxPerDeviceMap.clear()
        flexLayout.removeAll()

        val deviceList = deviceService.findAll()

        for (device in deviceList) {

            val deviceBox = DeviceBoxLayout(device, deviceStateChangeCallback)

            deviceBoxPerDeviceMap[device.id!!] = deviceBox

            createRightClickMenu(deviceBox, device)

            flexLayout.add(deviceBox)
        }

        updateDeviceStates()
    }

    private fun updateDeviceStates() {

        val deviceStates = deviceService.findAllDeviceStates()

        for (deviceState in deviceStates) {

            val deviceBoxLayout = deviceBoxPerDeviceMap[deviceState.deviceId] ?: continue

            deviceState.powerState?.let { powerState -> deviceBoxLayout.updatePowerState(powerState) }

            deviceState.percentage?.let { percentage -> deviceBoxLayout.updatePercentage(percentage) }

            deviceState.targetTemperature?.let { temperature -> deviceBoxLayout.updateTargetTemperature(temperature) }
        }
    }
}
