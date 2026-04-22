package com.example.criteriolocal.domain.repository

import com.example.criteriolocal.domain.model.Business
import com.example.criteriolocal.domain.model.BusinessWithCategory
import com.example.criteriolocal.domain.model.Category
import com.example.criteriolocal.domain.model.Evidence
import com.example.criteriolocal.domain.model.Quality
import com.example.criteriolocal.domain.model.Rating
import com.example.criteriolocal.domain.model.RatingDetails
import com.example.criteriolocal.domain.model.RatingQuality
import com.example.criteriolocal.domain.model.User
import com.example.criteriolocal.domain.model.UserWithRatings
import kotlinx.coroutines.flow.Flow

interface BootstrapRepository {
    suspend fun seedBaseData()
}

interface UserRepository {
    fun observeUsers(): Flow<List<User>>
    fun observeUser(userId: Long): Flow<User?>
    fun observeUserWithRatings(userId: Long): Flow<UserWithRatings?>
    suspend fun getUserByEmail(email: String): User?
    suspend fun saveUsers(users: List<User>)
}

interface CategoryRepository {
    fun observeCategories(): Flow<List<Category>>
    suspend fun getCategory(categoryId: Long): Category?
    suspend fun saveCategories(categories: List<Category>)
}

interface BusinessRepository {
    fun observeBusinesses(): Flow<List<BusinessWithCategory>>
    fun observeBusinessesByCategory(categoryId: Long): Flow<List<BusinessWithCategory>>
    fun searchBusinesses(query: String): Flow<List<BusinessWithCategory>>
    fun observeBusiness(businessId: Long): Flow<BusinessWithCategory?>
    suspend fun saveBusinesses(businesses: List<Business>)
}

interface QualityRepository {
    fun observeQualities(): Flow<List<Quality>>
    fun observeQualitiesByCategory(categoryId: Long): Flow<List<Quality>>
    suspend fun saveQualities(qualities: List<Quality>)
}

interface RatingRepository {
    fun observeRatingsByUser(userId: Long): Flow<List<RatingDetails>>
    fun observeRatingsByBusiness(businessId: Long): Flow<List<RatingDetails>>
    fun observeRating(ratingId: Long): Flow<RatingDetails?>
    suspend fun saveRating(rating: Rating): Long
    suspend fun saveRatingQualities(items: List<RatingQuality>)
    suspend fun saveEvidence(items: List<Evidence>)
}
