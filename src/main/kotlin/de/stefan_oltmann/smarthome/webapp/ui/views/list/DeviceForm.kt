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

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentEvent
import com.vaadin.flow.component.ComponentEventListener
import com.vaadin.flow.component.Key
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.button.ButtonVariant
import com.vaadin.flow.component.combobox.ComboBox
import com.vaadin.flow.component.formlayout.FormLayout
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.textfield.TextField
import com.vaadin.flow.data.binder.BeanValidationBinder
import com.vaadin.flow.data.binder.Binder
import com.vaadin.flow.data.binder.ValidationException
import com.vaadin.flow.shared.Registration
import de.stefan_oltmann.smarthome.webapp.backend.model.Device
import de.stefan_oltmann.smarthome.webapp.backend.model.DeviceGroup
import de.stefan_oltmann.smarthome.webapp.backend.model.DeviceType

class DeviceForm(groups: List<DeviceGroup>) : FormLayout() {

    private val id = TextField("ID")
    private val name = TextField("Name")
    private val group = ComboBox<DeviceGroup>("Group")
    private val deviceType = ComboBox<DeviceType>("Type")

    private val save = Button("Save")
    private val delete = Button("Delete")
    private val close = Button("Cancel")

    private val binder: Binder<Device?> = BeanValidationBinder(Device::class.java)

    init {

        addClassName("device-form")

        binder.bindInstanceFields(this)

        deviceType.setItems(*DeviceType.values())

        group.setItems(groups)
        group.setItemLabelGenerator(DeviceGroup::name)

        add(id, name, group, deviceType, createButtonsLayout())
    }

    private var device: Device? = null

    fun setDevice(device: Device?) {

        this.device = device

        binder.readBean(device)
    }

    private fun createButtonsLayout(): Component {

        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY)
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR)
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY)

        save.addClickShortcut(Key.ENTER)
        close.addClickShortcut(Key.ESCAPE)

        save.addClickListener { validateAndSave() }
        delete.addClickListener { fireEvent(DeleteEvent(this, device!!)) }
        close.addClickListener { fireEvent(CloseEvent(this)) }

        binder.addStatusChangeListener { save.isEnabled = binder.isValid }

        return HorizontalLayout(save, delete, close)
    }

    private fun validateAndSave() {

        try {

            binder.writeBean(device)
            fireEvent(SaveEvent(this, device!!))

        } catch (e: ValidationException) {
            e.printStackTrace()
        }
    }

    /*
     * Events
     */
    abstract class DeviceFormEvent protected constructor(source: DeviceForm, val device: Device?) :
        ComponentEvent<DeviceForm>(source, false)

    class SaveEvent internal constructor(source: DeviceForm, device: Device) : DeviceFormEvent(source, device)
    class DeleteEvent internal constructor(source: DeviceForm, device: Device) : DeviceFormEvent(source, device)
    class CloseEvent internal constructor(source: DeviceForm) : DeviceFormEvent(source, null)

    public override fun <T : ComponentEvent<*>?> addListener(
        eventType: Class<T>, listener: ComponentEventListener<T>
    ): Registration {

        return eventBus.addListener(eventType, listener)
    }
}
