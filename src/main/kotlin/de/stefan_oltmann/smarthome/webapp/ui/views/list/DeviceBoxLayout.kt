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
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import de.stefan_oltmann.smarthome.webapp.backend.model.Device
import de.stefan_oltmann.smarthome.webapp.backend.model.DevicePowerState
import de.stefan_oltmann.smarthome.webapp.backend.model.DeviceType
import de.stefan_oltmann.smarthome.webapp.ui.components.ToggleButton
import kotlin.math.roundToInt

class DeviceBoxLayout(device: Device, callback: DeviceStateChangeCallback) : VerticalLayout() {

    private val statusTextSpan = Span("-")

    private var currentPercentage = 0

    private var currentTargetTemperature = 0

    private val toggleButton = ToggleButton()

    private var listenerSilent = false

    init {

        className = "device-box"

        width = BOX_SIZE_PX.toString() + "px"
        height = BOX_SIZE_PX.toString() + "px"

        val vaadinIcon: VaadinIcon = when (device.type) {
            DeviceType.LIGHT_SWITCH -> VaadinIcon.LIGHTBULB
            DeviceType.DIMMER -> VaadinIcon.CONTROLLER
            DeviceType.ROLLER_SHUTTER -> VaadinIcon.LINES
            DeviceType.HEATING -> VaadinIcon.FIRE
            else -> VaadinIcon.BUG
        }

        val deviceIcon = Icon(vaadinIcon)
        deviceIcon.setSize(ICON_SIZE_PX.toString() + "px")
        deviceIcon.color = "#3D3D3D"

        val deviceNameSpan = Span()
        deviceNameSpan.className = "device-name-span"
        deviceNameSpan.setWidthFull()
        deviceNameSpan.text = device.name

        val deviceOptionsBox = HorizontalLayout()
        deviceOptionsBox.setWidthFull()
        deviceOptionsBox.className = "device-options-box"

        val gap = Div()
        gap.className = "fill-remaining-space"
        deviceOptionsBox.add(statusTextSpan, gap)

        if (device.type === DeviceType.LIGHT_SWITCH) {

            toggleButton.addValueChangeListener { event ->

                if (listenerSilent)
                    return@addValueChangeListener

                val powerState = if (event.value) DevicePowerState.ON else DevicePowerState.OFF

                callback.onDevicePowerStateChanged(device, powerState)

                statusTextSpan.text = powerState.name
            }

            val div = Div()
            div.className = "power-state-toggle"
            div.add(toggleButton)

            deviceOptionsBox.add(div)

        } else {

            val minusButton = Button()
            minusButton.className = "percentage-button"
            minusButton.icon = Icon(VaadinIcon.MINUS_CIRCLE_O)

            val plusButton = Button()
            plusButton.className = "percentage-button"
            plusButton.icon = Icon(VaadinIcon.PLUS_CIRCLE_O)

            if (device.type == DeviceType.HEATING) {

                minusButton.addClickListener {

                    currentTargetTemperature = 10.coerceAtLeast(currentTargetTemperature - TEMPERATURE_STEP)

                    callback.onDeviceTargetTemperatureChanged(device, currentTargetTemperature)

                    statusTextSpan.text = "$currentTargetTemperature°C"
                }

                plusButton.addClickListener {

                    currentTargetTemperature = 30.coerceAtMost(currentTargetTemperature + TEMPERATURE_STEP)

                    callback.onDeviceTargetTemperatureChanged(device, currentTargetTemperature)

                    statusTextSpan.text = "$currentTargetTemperature°C"
                }

            } else {

                minusButton.addClickListener {

                    currentPercentage = 0.coerceAtLeast(currentPercentage - PERCENT_STEP)

                    callback.onDevicePercentageChanged(device, currentPercentage)

                    statusTextSpan.text = "$currentPercentage%"
                }

                plusButton.addClickListener {

                    currentPercentage = 100.coerceAtMost(currentPercentage + PERCENT_STEP)

                    callback.onDevicePercentageChanged(device, currentPercentage)

                    statusTextSpan.text = "$currentPercentage%"
                }
            }

            deviceOptionsBox.add(minusButton, plusButton)
        }

        add(deviceIcon)
        add(deviceNameSpan)
        add(deviceOptionsBox)
    }

    fun updatePowerState(powerState: DevicePowerState) {

        try {

            listenerSilent = true

            toggleButton.value = powerState == DevicePowerState.ON

            statusTextSpan.text = powerState.name

        } finally {
            listenerSilent = false
        }
    }

    fun updatePercentage(percentage: Int) {

        currentPercentage = percentage

        statusTextSpan.text = "$percentage%"
    }

    fun updateTargetTemperature(targetTemperature: Double) {

        currentTargetTemperature = targetTemperature.roundToInt()

        statusTextSpan.text = "$currentTargetTemperature °C"
    }

    companion object {

        private const val BOX_SIZE_PX = 160
        private const val ICON_SIZE_PX = 48

        private const val TEMPERATURE_STEP = 1
        private const val PERCENT_STEP = 10
    }
}
