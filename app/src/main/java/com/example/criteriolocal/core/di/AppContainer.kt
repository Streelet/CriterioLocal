package com.example.criteriolocal.core.di

import android.content.Context
import com.example.criteriolocal.data.local.database.CriterioLocalDatabase
import com.example.criteriolocal.data.repository.DefaultBootstrapRepository
import com.example.criteriolocal.data.repository.DefaultBusinessRepository
import com.example.criteriolocal.data.repository.DefaultCategoryRepository
import com.example.criteriolocal.data.repository.DefaultQualityRepository
import com.example.criteriolocal.data.repository.DefaultRatingRepository
import com.example.criteriolocal.data.repository.DefaultUserRepository
import com.example.criteriolocal.domain.repository.BootstrapRepository
import com.example.criteriolocal.domain.repository.BusinessRepository
import com.example.criteriolocal.domain.repository.CategoryRepository
import com.example.criteriolocal.domain.repository.QualityRepository
import com.example.criteriolocal.domain.repository.RatingRepository
import com.example.criteriolocal.domain.repository.UserRepository

interface AppContainer {
    val bootstrapRepository: BootstrapRepository
    val userRepository: UserRepository
    val categoryRepository: CategoryRepository
    val businessRepository: BusinessRepository
    val qualityRepository: QualityRepository
    val ratingRepository: RatingRepository
}

class DefaultAppContainer(context: Context) : AppContainer {
    private val database: CriterioLocalDatabase by lazy {
        CriterioLocalDatabase.build(context.applicationContext)
    }

    override val userRepository: UserRepository by lazy {
        DefaultUserRepository(database.userDao())
    }

    override val categoryRepository: CategoryRepository by lazy {
        DefaultCategoryRepository(database.categoryDao())
    }

    override val businessRepository: BusinessRepository by lazy {
        DefaultBusinessRepository(database.businessDao())
    }

    override val qualityRepository: QualityRepository by lazy {
        DefaultQualityRepository(database.qualityDao())
    }

    override val ratingRepository: RatingRepository by lazy {
        DefaultRatingRepository(
            ratingDao = database.ratingDao(),
            ratingQualityDao = database.ratingQualityDao(),
            evidenceDao = database.evidenceDao(),
        )
    }

    override val bootstrapRepository: BootstrapRepository by lazy {
        DefaultBootstrapRepository(
            database = database,
            categoryDao = database.categoryDao(),
            qualityDao = database.qualityDao(),
            userDao = database.userDao(),
            businessDao = database.businessDao(),
        )
    }
}
