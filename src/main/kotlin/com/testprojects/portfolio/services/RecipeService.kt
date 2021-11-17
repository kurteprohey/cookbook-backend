package com.testprojects.portfolio.services

import com.testprojects.portfolio.controllers.RecipeSearchDTO
import com.testprojects.portfolio.entities.Recipe
import com.testprojects.portfolio.entities.Role
import com.testprojects.portfolio.entities.UnitOfMeasurement
import com.testprojects.portfolio.errors.ForbiddenException
import com.testprojects.portfolio.errors.NotFoundException
import com.testprojects.portfolio.repositories.RecipeRepository
import com.testprojects.portfolio.repositories.UserRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class RecipeService(
    val recipeRepository: RecipeRepository,
    val userRepository: UserRepository,
    val elasticSearchService: ElasticSearchService,
    val securityService: SecurityService,
    val imageUploadService: ImageUploadService
) {
    private fun getRecipeById(id: Long) = recipeRepository.findById(id).orElse(null) ?:
        throw NotFoundException("recipe id: $id not found")

    private fun canUpdate(recipe: Recipe) {
        if (securityService.getLoggedInUsername() != recipe.createdByUser?.email) {
            throw ForbiddenException("Not allowed to modify this recipe")
        }
    }

    fun addRecipe(recipe: Recipe): Recipe {
        val user = userRepository.findByEmail(securityService.getLoggedInUsername())
        return user?.let {
            recipe.createdByUser = it
            recipeRepository.saveAndFlush(recipe)
                .also { elasticSearchService.save(it) }
        } ?: throw NotFoundException("User not found")
    }

    fun updateRecipe(recipe: Recipe, id: Long): Recipe {
        return getRecipeById(id)
            .also { canUpdate(it) }
            .let {
                // only these fields can be updated upon existing recipe
                val recipeToUpdate = it.copy(
                    name = recipe.name,
                    description = recipe.description,
                    complexity = recipe.complexity,
                    timeToCook = recipe.timeToCook,
                    ingredients = recipe.ingredients,
                    category = recipe.category
                )
                recipeRepository.saveAndFlush(recipeToUpdate)
            }
            .also { elasticSearchService.save(it) }
    }

    fun updateRecipeImage(image: MultipartFile, id: Long): Recipe {
        return getRecipeById(id)
            .also { canUpdate(it) }
            .let {
                val recipeToUpdate = it.copy(imageUrl = imageUploadService.uploadImage(image))
                recipeRepository.saveAndFlush(recipeToUpdate)
            }
            .also { elasticSearchService.save(it) }
    }

    fun deleteRecipe(id: Long) {
        getRecipeById(id)
            .also {
                if (!securityService.userHasRole(Role.ADMIN) && securityService.getLoggedInUsername() != it.createdByUser?.email) {
                    throw ForbiddenException("Not allowed to delete this recipe")
                }
            }
            .let { recipeRepository.delete(it) }
            .also { elasticSearchService.delete(id) }
    }

    fun getAllRecipes() = recipeRepository.findFirst70ByOrderByIdDesc()

    fun getRecipe(id: Long) = getRecipeById(id)

    fun search(recipeSearchDTO: RecipeSearchDTO): List<Recipe> =
        elasticSearchService.searchRecipes(recipeSearchDTO)
//            .let { recipeRepository.findByIds(it.map { it.id }) }

    fun getUnits(): List<String> =
        UnitOfMeasurement.values().map { it.toString() }

}
