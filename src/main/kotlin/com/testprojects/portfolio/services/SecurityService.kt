package com.testprojects.portfolio.services

import com.testprojects.portfolio.entities.Role
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component

@Component
class SecurityService {
    fun getLoggedInUsername(): String {
        return (SecurityContextHolder.getContext().authentication.principal as User).username
    }

    fun userHasRole (role: Role): Boolean {
        val authentication = SecurityContextHolder.getContext().authentication

        return if (authentication == null || !authentication.isAuthenticated) {
            false
        } else {
            authentication.authorities.filter { it.authority.toUpperCase() == role.toString() }.isNotEmpty()
        }
    }
}
