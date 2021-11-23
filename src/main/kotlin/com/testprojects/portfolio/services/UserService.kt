package com.testprojects.portfolio.services

import com.testprojects.portfolio.entities.Recipe
import com.testprojects.portfolio.entities.Role
import com.testprojects.portfolio.entities.User
import com.testprojects.portfolio.entities.UserCreateUTO
import com.testprojects.portfolio.repositories.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.lang.RuntimeException

@Service
class UserService(
        val repository: UserRepository,
        val passwordEncoder: PasswordEncoder,
        val securityService: SecurityService
) {
    private fun findCurrentUser() = securityService.getLoggedInUsername()
        .let { repository.findByEmail(it) ?: throw Exception("can not find a user") }

    fun signin(user: UserCreateUTO): User {
        val u = User(
                id = null,
                email = user.email,
                password = passwordEncoder.encode(user.password),
                firstName = user.firstName,
                lastName = user.lastName,
                role = Role.USER
        )
        return repository.save(u)
    }

    fun getProfile(): User = findCurrentUser()

    fun addToFavorites(recipe: Recipe): User = findCurrentUser()
                .also {
                    it.likedRecipes.find { it.id == recipe.id }
                            ?.let { throw RuntimeException("Recipe is already in favorites") }
                }
                .apply { this.likedRecipes.add(recipe) }
                .let { repository.save(it) }

    fun removeFromFavorites(id: Long): User = findCurrentUser()
            .let {
                val foundRecipe = it.likedRecipes.find { it.id == id }
                        ?: throw RuntimeException("Recipe not found in favorites")
                it.likedRecipes.remove(foundRecipe)
                it
            }
            .let { repository.save(it) }

    fun getUserRecipes(): List<Recipe> {
        val user = findCurrentUser()
        return repository.getUserRecipes(user.email!!)
    }
}
