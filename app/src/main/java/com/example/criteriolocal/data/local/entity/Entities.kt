package com.example.criteriolocal.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.EvidenceType
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.UserStatus
import com.example.criteriolocal.domain.model.WaitTimeOption

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)],
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    @ColumnInfo(name = "registered_on")
    val registeredOn: String,
    val status: UserStatus,
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val name: String,
    val description: String,
)

@Entity(
    tableName = "businesses",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [Index(value = ["category_id"])],
)
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val address: String,
    val phone: String,
    @ColumnInfo(name = "category_id")
    val categoryId: Long,
    val status: BusinessStatus,
)

@Entity(
    tableName = "qualities",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["applicable_category_id"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index(value = ["applicable_category_id"])],
)
data class QualityEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    val name: String,
    val description: String,
    @ColumnInfo(name = "applicable_category_id")
    val applicableCategoryId: Long?,
    val status: CatalogStatus,
)

@Entity(
    tableName = "ratings",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["business_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["business_id"]),
    ],
)
data class RatingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "user_id")
    val userId: Long,
    @ColumnInfo(name = "business_id")
    val businessId: Long,
    @ColumnInfo(name = "rated_on")
    val ratedOn: String,
    @ColumnInfo(name = "reported_price")
    val reportedPrice: Double,
    @ColumnInfo(name = "price_reported_on")
    val priceReportedOn: String,
    @ColumnInfo(name = "service_score")
    val serviceScore: Int,
    @ColumnInfo(name = "attention_score")
    val attentionScore: Int,
    @ColumnInfo(name = "satisfaction_score")
    val satisfactionScore: Int,
    @ColumnInfo(name = "wait_time")
    val waitTime: WaitTimeOption,
    @ColumnInfo(name = "would_recommend")
    val wouldRecommend: Boolean,
    @ColumnInfo(name = "usage_frequency")
    val usageFrequency: UsageFrequencyOption,
    val availability: AvailabilityOption,
    @ColumnInfo(name = "service_mode")
    val serviceMode: ServiceModeOption,
)

@Entity(
    tableName = "rating_qualities",
    foreignKeys = [
        ForeignKey(
            entity = RatingEntity::class,
            parentColumns = ["id"],
            childColumns = ["rating_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = QualityEntity::class,
            parentColumns = ["id"],
            childColumns = ["quality_id"],
            onDelete = ForeignKey.RESTRICT,
        ),
    ],
    indices = [
        Index(value = ["rating_id"]),
        Index(value = ["quality_id"]),
        Index(value = ["rating_id", "quality_id"], unique = true),
    ],
)
data class RatingQualityEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "rating_id")
    val ratingId: Long,
    @ColumnInfo(name = "quality_id")
    val qualityId: Long,
)

@Entity(
    tableName = "evidences",
    foreignKeys = [
        ForeignKey(
            entity = RatingEntity::class,
            parentColumns = ["id"],
            childColumns = ["rating_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["rating_id"])],
)
data class EvidenceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(name = "rating_id")
    val ratingId: Long,
    @ColumnInfo(name = "file_path")
    val filePath: String,
    @ColumnInfo(name = "file_type")
    val fileType: EvidenceType,
    @ColumnInfo(name = "uploaded_on")
    val uploadedOn: String,
)
