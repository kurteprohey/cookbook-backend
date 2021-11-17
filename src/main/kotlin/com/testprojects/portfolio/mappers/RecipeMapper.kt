package com.testprojects.portfolio.mappers

import com.testprojects.portfolio.entities.Recipe
import com.testprojects.portfolio.entities.RecipeDTO
import com.testprojects.portfolio.entities.User
import org.springframework.stereotype.Component

@Component
class RecipeMapper {
    fun recipeToRecipeDTO(recipe: Recipe) =
        RecipeDTO(
            recipe.id,
            recipe.created,
            recipe.name,
            recipe.imageUrl,
            recipe.ingredients,
            recipe.timeToCook,
            recipe.complexity,
            recipe.description,
            recipe.createdByUser?.email,
            recipe.category
        )

    fun recipeDTOToRecipe(recipeDTO: RecipeDTO) =
        Recipe(
            id = recipeDTO.id,
            name = recipeDTO.name,
            imageUrl = recipeDTO.imageUrl,
            ingredients = recipeDTO.ingredients,
            timeToCook = recipeDTO.timeToCook,
            complexity = recipeDTO.complexity,
            description = recipeDTO.description,
            category = recipeDTO.category
        )

    fun recipeToElasticDTO(recipe: Recipe) =
        Recipe(
            recipe.id,
            recipe.created,
            recipe.name,
            recipe.imageUrl,
            recipe.ingredients,
            recipe.timeToCook,
            recipe.complexity,
            recipe.description,
            User(
                email = recipe.createdByUser?.email
            ),
            recipe.category
        )
}
