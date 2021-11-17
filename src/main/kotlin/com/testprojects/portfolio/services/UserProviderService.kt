package com.testprojects.portfolio.services

import com.testprojects.portfolio.entities.User
import com.testprojects.portfolio.repositories.UserRepository

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.util.Arrays

@Service
class UserProviderService(private val repository: UserRepository) : UserDetailsService {
    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(username: String): UserDetails {
        val user: User =
            repository.findByEmail(username) ?: throw RuntimeException("User not found: $username")
        val authority: GrantedAuthority = SimpleGrantedAuthority("user")
        return org.springframework.security.core.userdetails.User(
            user.email,
            user.password,
            Arrays.asList(authority)
        )
    }
}
