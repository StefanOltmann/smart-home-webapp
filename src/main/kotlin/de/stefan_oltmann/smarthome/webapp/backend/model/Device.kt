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
package de.stefan_oltmann.smarthome.webapp.backend.model

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.validation.constraints.NotEmpty
import javax.validation.constraints.NotNull

@Entity
class Device : Cloneable {

    @Id
    var id: String? = null
        private set

    @NotNull
    @NotEmpty
    var name: String = ""

    @ManyToOne
    @JoinColumn(name = "group_id")
    var group: DeviceGroup? = null

    @Enumerated(EnumType.STRING)
    @NotNull
    var type: DeviceType? = null

    private constructor() {
        /* Default for JPA */
    }

    constructor(id: String?) {
        this.id = id
    }

    override fun toString() = name

    override fun equals(other: Any?): Boolean {

        if (this === other)
            return true

        if (javaClass != other?.javaClass)
            return false

        other as Device

        if (id != other.id)
            return false

        return true
    }

    override fun hashCode() = id?.hashCode() ?: 0

}
