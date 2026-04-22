package com.example.criteriolocal.data.local.relation

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.criteriolocal.data.local.entity.BusinessEntity
import com.example.criteriolocal.data.local.entity.CategoryEntity
import com.example.criteriolocal.data.local.entity.EvidenceEntity
import com.example.criteriolocal.data.local.entity.QualityEntity
import com.example.criteriolocal.data.local.entity.RatingEntity
import com.example.criteriolocal.data.local.entity.RatingQualityEntity
import com.example.criteriolocal.data.local.entity.UserEntity

data class BusinessWithCategoryEntity(
    @Embedded
    val business: BusinessEntity,
    @Relation(
        parentColumn = "category_id",
        entityColumn = "id",
    )
    val category: CategoryEntity,
)

data class UserWithRatingsEntity(
    @Embedded
    val user: UserEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "user_id",
    )
    val ratings: List<RatingEntity>,
)

data class RatingWithDetailsEntity(
    @Embedded
    val rating: RatingEntity,
    @Relation(
        parentColumn = "user_id",
        entityColumn = "id",
    )
    val user: UserEntity,
    @Relation(
        parentColumn = "business_id",
        entityColumn = "id",
    )
    val business: BusinessEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = RatingQualityEntity::class,
            parentColumn = "rating_id",
            entityColumn = "quality_id",
        ),
    )
    val qualities: List<QualityEntity>,
    @Relation(
        parentColumn = "id",
        entityColumn = "rating_id",
    )
    val evidences: List<EvidenceEntity>,
)
