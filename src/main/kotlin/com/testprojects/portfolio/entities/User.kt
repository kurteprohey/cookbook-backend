package com.testprojects.portfolio.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.JoinTable
import javax.persistence.ManyToMany
import javax.persistence.OneToMany
import javax.persistence.Table

@Entity
@Table(name = "users")
data class User (
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name="email")
    val email: String? = null,

    @Column(name="first_name")
    val firstName: String? = null,

    @Column(name="last_name")
    val lastName: String? = null,

    @Column(name="password")
    @JsonIgnore
    val password: String? = null,

    @Column(name="role")
    @Enumerated(EnumType.STRING)
    val role: Role? = null,

    // JsonBackReference omitted from serialization
    @OneToMany(mappedBy = "createdByUser", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JsonBackReference
    var recipes: MutableList<Recipe> = mutableListOf(),

    @ManyToMany
    @JoinTable(
        name = "recipe_like",
        joinColumns = [JoinColumn(name = "user_id")],
        inverseJoinColumns = [JoinColumn(name = "recipe_id")]
    )
    val likedRecipes: MutableList<Recipe> = mutableListOf()
) {
    override fun toString(): String {
        return "dsadsd"
    }
}

enum class Role {
    USER, ADMIN, USER_MANAGER
}

data class UserCreateUTO (
    val email: String,
    val password: String,
    val firstName: String? = null,
    val lastName: String? = null
)

data class UserDTO (
    val email: String?,
    val firstName: String? = null,
    val lastName: String? = null,
    val likedRecipes: MutableList<RecipeDTO> = mutableListOf(),
    var recipes: MutableList<RecipeDTO> = mutableListOf()
)
