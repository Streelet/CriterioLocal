package com.example.criteriolocal.data.repository

import androidx.room.withTransaction
import com.example.criteriolocal.data.catalog.SeedCatalogData
import com.example.criteriolocal.data.local.dao.BusinessDao
import com.example.criteriolocal.data.local.dao.CategoryDao
import com.example.criteriolocal.data.local.dao.EvidenceDao
import com.example.criteriolocal.data.local.dao.QualityDao
import com.example.criteriolocal.data.local.dao.RatingDao
import com.example.criteriolocal.data.local.dao.RatingQualityDao
import com.example.criteriolocal.data.local.dao.UserDao
import com.example.criteriolocal.data.local.database.CriterioLocalDatabase
import com.example.criteriolocal.data.mapper.asDomain
import com.example.criteriolocal.data.mapper.asEntity
import com.example.criteriolocal.domain.repository.BootstrapRepository
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.CategoryRepository
import com.example.criteriolocal.domain.repository.QualityRepository
import com.example.criteriolocal.domain.repository.RatingRepository
import com.example.criteriolocal.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultBootstrapRepository(
    private val database: CriterioLocalDatabase,
    private val categoryDao: CategoryDao,
    private val qualityDao: QualityDao,
    private val userDao: UserDao,
    private val businessDao: BusinessDao,
    private val ratingDao: RatingDao,
    private val ratingQualityDao: RatingQualityDao,
    private val evidenceDao: EvidenceDao,
) : BootstrapRepository {
    override suspend fun seedBaseData() {
        database.withTransaction {
            val shouldSeedCategories = categoryDao.count() == 0
            val shouldSeedQualities = qualityDao.count() == 0
            val shouldSeedUsers = userDao.count() == 0
            val shouldSeedBusinesses = businessDao.count() == 0
            val shouldSeedDemoRatings = ratingDao.count() == 0 &&
                shouldSeedQualities &&
                shouldSeedUsers &&
                shouldSeedBusinesses

            if (shouldSeedCategories) {
                categoryDao.insertAll(SeedCatalogData.categories.map { it.asEntity() })
            }
            if (shouldSeedQualities) {
                qualityDao.insertAll(SeedCatalogData.qualities.map { it.asEntity() })
            }
            if (shouldSeedUsers) {
                userDao.insertAll(SeedCatalogData.users.map { it.asEntity() })
            }
            if (shouldSeedBusinesses) {
                businessDao.insertAll(SeedCatalogData.businesses.map { it.asEntity() })
            }
            if (shouldSeedDemoRatings) {
                ratingDao.insertAll(SeedCatalogData.ratings.map { it.asEntity() })
                ratingQualityDao.insertAll(SeedCatalogData.ratingQualities.map { it.asEntity() })
                evidenceDao.insertAll(SeedCatalogData.evidences.map { it.asEntity() })
            }
        }
    }

    override suspend fun seedCatalogsOnly() {
        database.withTransaction {
            if (categoryDao.count() == 0) {
                categoryDao.insertAll(SeedCatalogData.categories.map { it.asEntity() })
            }
            if (qualityDao.count() == 0) {
                qualityDao.insertAll(SeedCatalogData.qualities.map { it.asEntity() })
            }
        }
    }
}

class DefaultUserRepository(
    private val userDao: UserDao,
) : UserRepository {
    override fun observeUsers(): Flow<List<com.example.criteriolocal.domain.model.User>> {
        return userDao.observeUsers().map { users -> users.map { it.asDomain() } }
    }

    override fun observeUser(userId: Long): Flow<com.example.criteriolocal.domain.model.User?> {
        return userDao.observeUser(userId).map { it?.asDomain() }
    }

    override fun observeUserWithRatings(userId: Long): Flow<com.example.criteriolocal.domain.model.UserWithRatings?> {
        return userDao.observeUserWithRatings(userId).map { it?.asDomain() }
    }

    override suspend fun getUserByEmail(email: String): com.example.criteriolocal.domain.model.User? {
        return userDao.getUserByEmail(email)?.asDomain()
    }

    override suspend fun saveUser(user: com.example.criteriolocal.domain.model.User): Long {
        return userDao.insert(user.asEntity())
    }

    override suspend fun saveUsers(users: List<com.example.criteriolocal.domain.model.User>) {
        userDao.insertAll(users.map { it.asEntity() })
    }
}

class DefaultCategoryRepository(
    private val categoryDao: CategoryDao,
) : CategoryRepository {
    override fun observeCategories(): Flow<List<com.example.criteriolocal.domain.model.Category>> {
        return categoryDao.observeCategories().map { categories -> categories.map { it.asDomain() } }
    }

    override suspend fun getCategory(categoryId: Long): com.example.criteriolocal.domain.model.Category? {
        return categoryDao.getById(categoryId)?.asDomain()
    }

    override suspend fun saveCategory(category: com.example.criteriolocal.domain.model.Category): Long {
        return categoryDao.insert(category.asEntity())
    }

    override suspend fun saveCategories(categories: List<com.example.criteriolocal.domain.model.Category>) {
        categoryDao.insertAll(categories.map { it.asEntity() })
    }
}

class DefaultBusinessRepository(
    private val businessDao: BusinessDao,
) : BusinessRepository {
    override fun observeBusinesses(): Flow<List<com.example.criteriolocal.domain.model.BusinessWithCategory>> {
        return businessDao.observeBusinesses().map { businesses -> businesses.map { it.asDomain() } }
    }

    override fun observeBusinessesByCategory(categoryId: Long): Flow<List<com.example.criteriolocal.domain.model.BusinessWithCategory>> {
        return businessDao.observeBusinessesByCategory(categoryId)
            .map { businesses -> businesses.map { it.asDomain() } }
    }

    override fun searchBusinesses(query: String): Flow<List<com.example.criteriolocal.domain.model.BusinessWithCategory>> {
        return businessDao.searchBusinesses(query).map { businesses -> businesses.map { it.asDomain() } }
    }

    override fun observeBusiness(businessId: Long): Flow<com.example.criteriolocal.domain.model.BusinessWithCategory?> {
        return businessDao.observeBusiness(businessId).map { it?.asDomain() }
    }

    override suspend fun getBusinessByGooglePlaceId(
        googlePlaceId: String,
    ): com.example.criteriolocal.domain.model.Business? {
        return businessDao.getByGooglePlaceId(googlePlaceId)?.asDomain()
    }

    override suspend fun saveBusiness(business: com.example.criteriolocal.domain.model.Business): Long {
        return businessDao.insert(business.asEntity())
    }

    override suspend fun saveBusinesses(businesses: List<com.example.criteriolocal.domain.model.Business>) {
        businessDao.insertAll(businesses.map { it.asEntity() })
    }
}

class DefaultQualityRepository(
    private val qualityDao: QualityDao,
) : QualityRepository {
    override fun observeQualities(): Flow<List<com.example.criteriolocal.domain.model.Quality>> {
        return qualityDao.observeQualities().map { qualities -> qualities.map { it.asDomain() } }
    }

    override fun observeQualitiesByCategory(categoryId: Long): Flow<List<com.example.criteriolocal.domain.model.Quality>> {
        return qualityDao.observeQualitiesByCategory(categoryId)
            .map { qualities -> qualities.map { it.asDomain() } }
    }

    override suspend fun saveQualities(qualities: List<com.example.criteriolocal.domain.model.Quality>) {
        qualityDao.insertAll(qualities.map { it.asEntity() })
    }
}

class DefaultRatingRepository(
    private val database: CriterioLocalDatabase,
    private val ratingDao: RatingDao,
    private val ratingQualityDao: RatingQualityDao,
    private val evidenceDao: EvidenceDao,
) : RatingRepository {
    override fun observeAllRatings(): Flow<List<com.example.criteriolocal.domain.model.RatingDetails>> {
        return ratingDao.observeAllRatings().map { ratings -> ratings.map { it.asDomain() } }
    }

    override fun observeRatingsByUser(userId: Long): Flow<List<com.example.criteriolocal.domain.model.RatingDetails>> {
        return ratingDao.observeRatingsByUser(userId).map { ratings -> ratings.map { it.asDomain() } }
    }

    override fun observeRatingsByBusiness(businessId: Long): Flow<List<com.example.criteriolocal.domain.model.RatingDetails>> {
        return ratingDao.observeRatingsByBusiness(businessId).map { ratings -> ratings.map { it.asDomain() } }
    }

    override fun observeRating(ratingId: Long): Flow<com.example.criteriolocal.domain.model.RatingDetails?> {
        return ratingDao.observeRating(ratingId).map { it?.asDomain() }
    }

    override suspend fun saveStructuredRating(
        rating: com.example.criteriolocal.domain.model.Rating,
        selectedQualityIds: List<Long>,
        evidences: List<com.example.criteriolocal.domain.model.Evidence>,
    ): Long {
        return database.withTransaction {
            val ratingId = ratingDao.insert(rating.asEntity())
            ratingQualityDao.insertAll(
                selectedQualityIds.distinct().map { qualityId ->
                    com.example.criteriolocal.domain.model.RatingQuality(
                        ratingId = ratingId,
                        qualityId = qualityId,
                    ).asEntity()
                },
            )
            if (evidences.isNotEmpty()) {
                evidenceDao.insertAll(
                    evidences.map { evidence ->
                        evidence.copy(ratingId = ratingId).asEntity()
                    },
                )
            }
            ratingId
        }
    }

    override suspend fun saveRating(rating: com.example.criteriolocal.domain.model.Rating): Long {
        return ratingDao.insert(rating.asEntity())
    }

    override suspend fun saveRatingQualities(items: List<com.example.criteriolocal.domain.model.RatingQuality>) {
        if (items.isNotEmpty()) {
            ratingQualityDao.insertAll(items.map { it.asEntity() })
        }
    }

    override suspend fun saveEvidence(items: List<com.example.criteriolocal.domain.model.Evidence>) {
        if (items.isNotEmpty()) {
            evidenceDao.insertAll(items.map { it.asEntity() })
        }
    }
}
