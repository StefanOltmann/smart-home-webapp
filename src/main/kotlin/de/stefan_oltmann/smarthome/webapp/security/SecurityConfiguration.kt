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

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.util.matcher.RequestMatcher

@EnableWebSecurity
@Configuration
open class SecurityConfiguration : WebSecurityConfigurerAdapter() {

    /**
     * Require login to access internal pages and configure login form.
     */
    @Throws(Exception::class)
    override fun configure(http: HttpSecurity) {

        // Not using Spring CSRF here to be able to use plain HTML for the login page
        http.csrf().disable()

            // Register our CustomRequestCache, that saves unauthorized access attempts, so
            // the user is redirected after login.
            .requestCache().requestCache(CustomRequestCache()) // Restrict access to our application.
            .and().authorizeRequests()

            // Allow all Vaadin internal requests.
            .requestMatchers(RequestMatcher { request -> SecurityUtils.isFrameworkInternalRequest(request) })
            .permitAll()

            // Allow all requests by logged in users.
            .anyRequest().authenticated()

            // Configure the login page.
            .and().formLogin()
            .loginPage(LOGIN_URL).permitAll()
            .loginProcessingUrl(LOGIN_PROCESSING_URL)
            .failureUrl(LOGIN_FAILURE_URL)

            // Remember Login
            .and().rememberMe()

            // Configure logout
            .and().logout().logoutSuccessUrl(LOGOUT_SUCCESS_URL)
    }

    @Bean
    public override fun userDetailsService(): UserDetailsService {

        val user = User.withUsername("user")
            .password("{noop}password")
            .roles("USER")
            .build()

        return InMemoryUserDetailsManager(user)
    }

    /**
     * Allows access to static resources, bypassing Spring security.
     */
    override fun configure(web: WebSecurity) {

        web.ignoring().antMatchers(

            // Client-side JS
            "/VAADIN/**",

            // the standard favicon URI
            "/favicon.ico",

            // the robots exclusion standard
            "/robots.txt",

            // web application manifest
            "/manifest.webmanifest",
            "/sw.js",
            "/offline.html",

            // icons and images
            "/icons/**",
            "/images/**",
            "/styles/**",

            // (development mode) H2 debugging console
            "/h2-console/**"
        )
    }

    companion object {

        private const val LOGIN_PROCESSING_URL = "/login"
        private const val LOGIN_FAILURE_URL = "/login?error"
        private const val LOGIN_URL = LOGIN_PROCESSING_URL
        private const val LOGOUT_SUCCESS_URL = LOGIN_PROCESSING_URL
    }
}
