package com.testprojects.portfolio.repositories

import com.testprojects.portfolio.entities.Recipe
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface RecipeRepository : JpaRepository<Recipe, Long> {
    fun findFirst2000ByIdGreaterThanOrderByIdAsc(id: Long): List<Recipe>

    fun findFirst70ByOrderByIdDesc(): List<Recipe>

    @Query("select r from Recipe r where r.id in :ids order by r.id")
    fun findByIds(ids: List<Long?>): List<Recipe>
}
