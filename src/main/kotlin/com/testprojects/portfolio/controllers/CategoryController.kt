package com.testprojects.portfolio.controllers

import com.testprojects.portfolio.services.CategoryService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/categories")
class CategoryController (val categoryService: CategoryService) {
    @GetMapping("")
    fun getAllCategories() = categoryService.getAllCategories()
}
