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
package de.stefan_oltmann.smarthome.webapp.security

import com.vaadin.flow.router.BeforeEnterEvent
import com.vaadin.flow.server.ServiceInitEvent
import com.vaadin.flow.server.UIInitEvent
import com.vaadin.flow.server.VaadinServiceInitListener
import de.stefan_oltmann.smarthome.webapp.ui.views.login.LoginView
import org.springframework.stereotype.Component

@Component
class ConfigureUIServiceInitListener : VaadinServiceInitListener {

    override fun serviceInit(event: ServiceInitEvent) {

        event.source.addUIInitListener { uiEvent: UIInitEvent ->

            uiEvent.ui.addBeforeEnterListener { event: BeforeEnterEvent -> beforeEnter(event) }
        }
    }

    /**
     * Reroutes the user if they're not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private fun beforeEnter(event: BeforeEnterEvent) {

        if (LoginView::class.java != event.navigationTarget && !SecurityUtils.isUserLoggedIn)
            event.rerouteTo(LoginView::class.java)
    }
}