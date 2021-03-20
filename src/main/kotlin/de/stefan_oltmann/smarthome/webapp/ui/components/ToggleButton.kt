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
package de.stefan_oltmann.smarthome.webapp.ui.components

import com.vaadin.flow.component.AbstractSinglePropertyField
import com.vaadin.flow.component.Tag
import com.vaadin.flow.component.dependency.JsModule
import com.vaadin.flow.component.dependency.NpmPackage

@Tag("paper-toggle-button")
@NpmPackage(value = "@polymer/paper-toggle-button", version = "3.0.1")
@JsModule("@polymer/paper-toggle-button/paper-toggle-button.js")
class ToggleButton : AbstractSinglePropertyField<ToggleButton, Boolean>(
        "checked", false, false
)