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
package de.stefan_oltmann.smarthome.webapp.ui.views.dashboard

import com.vaadin.flow.component.html.Span
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.Route
import de.stefan_oltmann.smarthome.webapp.backend.service.DeviceService
import de.stefan_oltmann.smarthome.webapp.ui.MainLayout

@PageTitle("Dashboard")
@Route(value = "dashboard", layout = MainLayout::class)
class DashboardView(deviceService: DeviceService) : VerticalLayout() {

    init {

        addClassName("dashboard-view")

        defaultHorizontalComponentAlignment = FlexComponent.Alignment.CENTER

        val stats = Span("${deviceService.count()} devices")
        stats.addClassName("device-stats")

        add(stats)
    }
}
