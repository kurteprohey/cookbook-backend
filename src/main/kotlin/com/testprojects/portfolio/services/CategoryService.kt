package com.testprojects.portfolio.services

import com.testprojects.portfolio.entities.Category
import com.testprojects.portfolio.repositories.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CategoryService(val categoryRepository: CategoryRepository) {
    fun getAllCategories(): List<Category> = categoryRepository.findAll()
}
