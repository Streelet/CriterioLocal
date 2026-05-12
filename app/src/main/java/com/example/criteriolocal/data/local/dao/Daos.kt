package com.example.criteriolocal.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.criteriolocal.data.local.entity.BusinessEntity
import com.example.criteriolocal.data.local.entity.CategoryEntity
import com.example.criteriolocal.data.local.entity.EvidenceEntity
import com.example.criteriolocal.data.local.entity.QualityEntity
import com.example.criteriolocal.data.local.entity.RatingEntity
import com.example.criteriolocal.data.local.entity.RatingQualityEntity
import com.example.criteriolocal.data.local.entity.UserEntity
import com.example.criteriolocal.data.local.relation.BusinessWithCategoryEntity
import com.example.criteriolocal.data.local.relation.RatingWithDetailsEntity
import com.example.criteriolocal.data.local.relation.UserWithRatingsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(users: List<UserEntity>)

    @Query("SELECT * FROM users ORDER BY name ASC")
    fun observeUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUser(userId: Long): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    @Transaction
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun observeUserWithRatings(userId: Long): Flow<UserWithRatingsEntity?>

    @Query("SELECT COUNT(*) FROM users")
    suspend fun count(): Int
}

@Dao
interface CategoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Query("SELECT * FROM categories ORDER BY name ASC")
    fun observeCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :categoryId LIMIT 1")
    suspend fun getById(categoryId: Long): CategoryEntity?

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun count(): Int
}

@Dao
interface BusinessDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(business: BusinessEntity): Long

    @Update
    suspend fun update(business: BusinessEntity)

    @Transaction
    suspend fun upsertPreservingRelations(business: BusinessEntity): Long {
        val insertedId = insert(business)
        if (insertedId != -1L) {
            return insertedId
        }

        update(business)
        return business.id
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(businesses: List<BusinessEntity>)

    @Transaction
    @Query("SELECT * FROM businesses ORDER BY name ASC")
    fun observeBusinesses(): Flow<List<BusinessWithCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM businesses WHERE category_id = :categoryId ORDER BY name ASC")
    fun observeBusinessesByCategory(categoryId: Long): Flow<List<BusinessWithCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM businesses WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchBusinesses(query: String): Flow<List<BusinessWithCategoryEntity>>

    @Transaction
    @Query("SELECT * FROM businesses WHERE id = :businessId LIMIT 1")
    fun observeBusiness(businessId: Long): Flow<BusinessWithCategoryEntity?>

    @Query("SELECT * FROM businesses WHERE google_place_id = :googlePlaceId LIMIT 1")
    suspend fun getByGooglePlaceId(googlePlaceId: String): BusinessEntity?

    @Query("SELECT COUNT(*) FROM businesses")
    suspend fun count(): Int
}

@Dao
interface QualityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(qualities: List<QualityEntity>)

    @Query("SELECT * FROM qualities ORDER BY name ASC")
    fun observeQualities(): Flow<List<QualityEntity>>

    @Query("""
        SELECT * FROM qualities
        WHERE applicable_category_id IS NULL OR applicable_category_id = :categoryId
        ORDER BY name ASC
    """)
    fun observeQualitiesByCategory(categoryId: Long): Flow<List<QualityEntity>>

    @Query("SELECT COUNT(*) FROM qualities")
    suspend fun count(): Int
}

@Dao
interface RatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rating: RatingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(ratings: List<RatingEntity>)

    @Transaction
    @Query("SELECT * FROM ratings ORDER BY rated_on DESC")
    fun observeAllRatings(): Flow<List<RatingWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM ratings WHERE user_id = :userId ORDER BY rated_on DESC")
    fun observeRatingsByUser(userId: Long): Flow<List<RatingWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM ratings WHERE business_id = :businessId ORDER BY rated_on DESC")
    fun observeRatingsByBusiness(businessId: Long): Flow<List<RatingWithDetailsEntity>>

    @Transaction
    @Query("SELECT * FROM ratings WHERE id = :ratingId LIMIT 1")
    fun observeRating(ratingId: Long): Flow<RatingWithDetailsEntity?>

    @Query("SELECT COUNT(*) FROM ratings")
    suspend fun count(): Int
}

@Dao
interface RatingQualityDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<RatingQualityEntity>)

    @Query("SELECT COUNT(*) FROM rating_qualities")
    suspend fun count(): Int
}

@Dao
interface EvidenceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<EvidenceEntity>)

    @Query("SELECT COUNT(*) FROM evidences")
    suspend fun count(): Int
}
