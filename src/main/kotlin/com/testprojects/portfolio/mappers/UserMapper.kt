package com.testprojects.portfolio.mappers

import com.testprojects.portfolio.entities.User
import com.testprojects.portfolio.entities.UserDTO
import org.springframework.stereotype.Component

@Component
class UserMapper(val recipeMapper: RecipeMapper) {
    fun userToUserDTO(user: User) =
        UserDTO(
            user.email,
            user.firstName,
            user.lastName,
            user.likedRecipes.map { recipeMapper.recipeToRecipeDTO(it) }.toMutableList()
        )
}
