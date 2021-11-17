package com.testprojects.portfolio.controllers

import com.testprojects.portfolio.entities.Recipe
import com.testprojects.portfolio.entities.RecipeDTO
import com.testprojects.portfolio.mappers.RecipeMapper
import com.testprojects.portfolio.services.RecipeService
import org.springframework.http.MediaType
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@Transactional
@RequestMapping("/api/recipes")
class RecipeController(
    val recipeService: RecipeService,
    val recipeMapper: RecipeMapper
) {

    @GetMapping("")
    fun getRecipes() =
        recipeService.getAllRecipes()
            .map { recipeMapper.recipeToRecipeDTO(it) }

    @PostMapping("")
    @PreAuthorize("isAuthenticated()")
    fun addRecipe(@RequestBody recipeDTO: RecipeDTO) =
        recipeMapper.recipeDTOToRecipe(recipeDTO)
            .let { recipeService.addRecipe(it) }
            .let { recipeMapper.recipeToRecipeDTO(it) }

    @PutMapping("/{id}")
    fun updateRecipe(@RequestBody recipeDTO: RecipeDTO, @PathVariable id: Long) =
        recipeDTO
            .let { recipeMapper.recipeDTOToRecipe(it) }
            .let { recipeService.updateRecipe(it, id) }
            .let { recipeMapper.recipeToRecipeDTO(it) }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun deleteRecipe(@PathVariable id: Long) = recipeService.deleteRecipe(id)

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    fun getRecipe(@PathVariable id: Long) =
        recipeService.getRecipe(id)
            .let { recipeMapper.recipeToRecipeDTO(it) }

    @RequestMapping(
        value = ["/{id}/image"],
        method = [RequestMethod.PUT],
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    @PreAuthorize("isAuthenticated()")
    fun updateRecipeImage(@RequestParam image: MultipartFile, @PathVariable id: Long) =
        recipeService.updateRecipeImage(image, id)

    @PostMapping("/search")
    fun searchRecipe(@RequestBody ingredientsSearchDTO: RecipeSearchDTO) =
        recipeService.search(ingredientsSearchDTO)
            .map { recipeMapper.recipeToRecipeDTO(it) }

    @GetMapping("/unit-of-measurements")
    fun getUnits() = recipeService.getUnits()

    @GetMapping("/wake-up")
    fun wakeUp() = recipeService.search(RecipeSearchDTO(category = "main_dishes", ingredients = emptyList()))
}

data class RecipeSearchDTO (
    val ingredients: List<String>,
    val category: String = "",
    val page: Long = 1,
    val size: Long = 5
)
