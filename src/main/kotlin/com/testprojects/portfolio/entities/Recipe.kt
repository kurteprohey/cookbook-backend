package com.testprojects.portfolio.entities

import com.fasterxml.jackson.annotation.JsonBackReference
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonManagedReference
import org.springframework.data.annotation.CreatedDate
import java.time.Instant
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Embeddable
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.OneToOne
import javax.persistence.Table
import javax.persistence.Transient

@Entity
@Table(name = "recipes")
data class Recipe (
    @Id
    @Column(name = "recipe_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @CreatedDate
    var created: Instant? = Instant.now(),

    @Column(name = "name")
    val name: String,

    @Column(name = "image_url")
    var imageUrl: String,

    @ElementCollection
    @CollectionTable(name = "ingredients", joinColumns = [JoinColumn(name = "recipe_id")])
    @Column(name = "ingredients")
    val ingredients: MutableSet<Ingredient> = mutableSetOf(),

    @Column(name = "time_to_cook")
    val timeToCook: Long,

    @Column(name = "complexity")
    val complexity: Long,

    @Column(name = "description")
    val description: String,

    // JsonManagedReference gets serialized normally
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonManagedReference
    var createdByUser: User? = null,

    @OneToOne
    @JoinColumn(name = "category_id")
    var category: Category? = null
) {
    @Transient
    @JsonIgnore
    val ingredientsCount: Number = ingredients.size
}

data class RecipeDTO (
    val id: Long? = null,
    var created: Instant? = null,
    val name: String,
    var imageUrl: String,
    val ingredients: MutableSet<Ingredient> = mutableSetOf(),
    val timeToCook: Long,
    val complexity: Long,
    val description: String,
    val ownerEmail: String?,
    val category: Category?
)

@Embeddable
data class Ingredient (
    val name: String,
    val unit: UnitOfMeasurement?,
    val amount: Float?
)

enum class UnitOfMeasurement {
    CUP, TABLE_SPOON, TEE_SPOON, GRAM, KILOGRAM, LITER, MILLILITER, ITEM, GLASS
}
