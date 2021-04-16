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

import com.vaadin.flow.server.ServletHelper
import com.vaadin.flow.shared.ApplicationConstants
import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.util.stream.Stream
import javax.servlet.http.HttpServletRequest

/**
 * SecurityUtils takes care of all such static operations that have to do with
 * security and querying rights from different beans of the UI.
 */
object SecurityUtils {

    /**
     * Tests if some user is authenticated. As Spring Security always will create an [AnonymousAuthenticationToken]
     * we have to ignore those tokens explicitly.
     */
    val isUserLoggedIn: Boolean
        get() {

            val authentication = SecurityContextHolder.getContext().authentication

            return (authentication != null
                    && authentication !is AnonymousAuthenticationToken
                    && authentication.isAuthenticated)
        }

    /**
     * Tests if the request is an internal framework request. The test consists of
     * checking if the request parameter is present and if its value is consistent
     * with any of the request types know.
     *
     * @param request [HttpServletRequest]
     * @return true if is an internal framework request. False otherwise.
     */
    @JvmStatic
    fun isFrameworkInternalRequest(request: HttpServletRequest): Boolean {

        val parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER) ?: return false

        return Stream.of(*ServletHelper.RequestType.values())
            .anyMatch { r: ServletHelper.RequestType -> r.identifier == parameterValue }
    }
}
