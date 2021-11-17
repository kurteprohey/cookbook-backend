package com.testprojects.portfolio.controllers

import com.testprojects.portfolio.entities.Recipe
import com.testprojects.portfolio.entities.User
import com.testprojects.portfolio.entities.UserCreateUTO
import com.testprojects.portfolio.entities.UserDTO
import com.testprojects.portfolio.mappers.RecipeMapper
import com.testprojects.portfolio.mappers.UserMapper
import com.testprojects.portfolio.services.UserService
import org.springframework.web.bind.annotation.*

@RestController
class SignInController(
    val userService: UserService,
    val userMapper: UserMapper,
    val recipeMapper: RecipeMapper
) {
    @PostMapping("/api/user/signin")
    fun signin(@RequestBody user: UserCreateUTO) = userService.signin(user)

    @GetMapping("/api/user/profile")
    fun getCurrentUser() =
        userService.getProfile()
            .let { userMapper.userToUserDTO(it) }
            .apply {
                recipes = userService.getUserRecipes()
                    .map { recipeMapper.recipeToRecipeDTO(it) }
                    .toMutableList()
            }

    @GetMapping("/api/user/recipes")
    fun getUserRecipes() = userService.getUserRecipes()

    @PutMapping("/api/user/favorites")
    fun addToFavorites(@RequestBody recipe: Recipe): UserDTO =
            userService.addToFavorites(recipe)
                .let { userMapper.userToUserDTO(it) }

    @DeleteMapping("/api/user/favorites/{id}")
    fun removeFromFavorites(@PathVariable id: Long): UserDTO =
            userService.removeFromFavorites(id)
                .let { userMapper.userToUserDTO(it) }
}
