package com.testprojects.portfolio.repositories

import com.testprojects.portfolio.entities.Recipe
import com.testprojects.portfolio.entities.User
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;

interface UserRepository : PagingAndSortingRepository<User?, Long?> {
    fun findByEmail(email: String?): User?
    fun findByEmailContains(email: String?, pageable: Pageable?): Page<User?>?
    fun findAllByEmail(email: String?, pageable: Pageable?): Page<User?>?
    fun findAllByEmailContainsAndEmail(
        email: String?,
        auth: String?,
        pageable: Pageable?
    ): Page<User?>?

    @Query("select r from Recipe r where r.createdByUser.email = :username")
    fun getUserRecipes(username: String): MutableList<Recipe>

    fun existsByEmail(email: String?): Boolean?
}
