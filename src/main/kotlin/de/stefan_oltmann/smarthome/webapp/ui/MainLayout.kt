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
package de.stefan_oltmann.smarthome.webapp.ui

import com.vaadin.flow.component.Component
import com.vaadin.flow.component.ComponentUtil
import com.vaadin.flow.component.applayout.AppLayout
import com.vaadin.flow.component.applayout.DrawerToggle
import com.vaadin.flow.component.button.Button
import com.vaadin.flow.component.contextmenu.ContextMenu
import com.vaadin.flow.component.dependency.CssImport
import com.vaadin.flow.component.html.Div
import com.vaadin.flow.component.html.H1
import com.vaadin.flow.component.icon.Icon
import com.vaadin.flow.component.icon.VaadinIcon
import com.vaadin.flow.component.orderedlayout.FlexComponent
import com.vaadin.flow.component.orderedlayout.HorizontalLayout
import com.vaadin.flow.component.orderedlayout.VerticalLayout
import com.vaadin.flow.component.tabs.Tab
import com.vaadin.flow.component.tabs.Tabs
import com.vaadin.flow.component.tabs.TabsVariant
import com.vaadin.flow.router.PageTitle
import com.vaadin.flow.router.RouterLink
import com.vaadin.flow.server.PWA
import de.stefan_oltmann.smarthome.webapp.ui.views.dashboard.DashboardView
import de.stefan_oltmann.smarthome.webapp.ui.views.list.ListView
import java.util.*

@PWA(
        name = "Stefans Smart Home",
        shortName = "Smart Home",
        offlineResources = ["./styles/offline.css", "./images/offline.png"],
        enableInstallPrompt = true
)
@CssImport("./styles/shared-styles.css")
class MainLayout : AppLayout() {

    companion object {

        private fun createTab(text: String, navigationTarget: Class<out Component>): Tab {

            val tab = Tab()

            tab.add(RouterLink(text, navigationTarget))

            ComponentUtil.setData(tab, Class::class.java, navigationTarget)

            return tab
        }
    }

    private val menu: Tabs
    private val viewTitle: H1 = H1()

    private val currentPageTitle: String
        get() = content.javaClass.getAnnotation(PageTitle::class.java).value

    init {

        primarySection = Section.DRAWER

        addToNavbar(false, createHeaderContent())

        menu = createMenu()

        addToDrawer(createDrawerContent(menu))
    }

    private fun createHeaderContent(): Component {

        val layout = HorizontalLayout()
        layout.setId("header")
        layout.themeList["dark"] = true
        layout.setWidthFull()
        layout.isSpacing = false
        layout.alignItems = FlexComponent.Alignment.CENTER
        layout.add(DrawerToggle())
        layout.add(viewTitle)

        val overflow = Button(Icon(VaadinIcon.ELLIPSIS_DOTS_V))
        overflow.className = "overflow-button"

        //layout.add(new Image("images/user.svg", "Avatar"));
        val gap = Div()
        gap.className = "fill-remaining-space"
        layout.add(gap, overflow)
        val menu = ContextMenu()
        menu.target = overflow
        menu.isOpenOnClick = true
        menu.addItem("Justify", null)

        return layout
    }

    private fun createDrawerContent(menu: Tabs): Component {

        val logoLayout = HorizontalLayout()
        logoLayout.setId("logo")
        logoLayout.alignItems = FlexComponent.Alignment.CENTER
        logoLayout.add(Icon(VaadinIcon.HOME))
        // logoLayout.add(new Image("images/logo.png", "Logo"));
        logoLayout.add(H1("Stefans Smart Home"))

        val layout = VerticalLayout()
        layout.setSizeFull()
        layout.isPadding = false
        layout.isSpacing = false
        layout.themeList["spacing-s"] = true
        layout.alignItems = FlexComponent.Alignment.STRETCH
        layout.add(logoLayout, menu)

        return layout
    }

    private fun createMenu(): Tabs {

        val tabs = Tabs()
        tabs.orientation = Tabs.Orientation.VERTICAL
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL)
        tabs.setId("tabs")
        tabs.add(*createMenuItems())

        return tabs
    }

    private fun createMenuItems(): Array<Component> {
        return arrayOf(
                createTab("Devices", ListView::class.java),
                createTab("Dashboard", DashboardView::class.java)
        )
    }

    override fun afterNavigation() {

        super.afterNavigation()

        getTabForComponent(content).ifPresent { selectedTab: Tab? -> menu.selectedTab = selectedTab }

        viewTitle.text = currentPageTitle
    }

    private fun getTabForComponent(component: Component): Optional<Tab> {
        return menu.children
                .filter { tab -> (ComponentUtil.getData(tab, Class::class.java) == component.javaClass) }
                .findFirst().map { component -> Tab::class.java.cast(component) }
    }
}