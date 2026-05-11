package com.example.criteriolocal.data.mapper

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

internal fun UserEntity.asDomain(): User = User(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    registeredOn = registeredOn,
    status = status,
)

internal fun User.asEntity(): UserEntity = UserEntity(
    id = id,
    name = name,
    email = email,
    passwordHash = passwordHash,
    registeredOn = registeredOn,
    status = status,
)

internal fun CategoryEntity.asDomain(): Category = Category(
    id = id,
    name = name,
    description = description,
)

internal fun Category.asEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    description = description,
)

internal fun BusinessEntity.asDomain(): Business = Business(
    id = id,
    googlePlaceId = googlePlaceId,
    name = name,
    description = description,
    address = address,
    phone = phone,
    latitude = latitude,
    longitude = longitude,
    categoryId = categoryId,
    status = status,
    photoUrl = photoUrl,
)

internal fun Business.asEntity(): BusinessEntity = BusinessEntity(
    id = id,
    googlePlaceId = googlePlaceId,
    name = name,
    description = description,
    address = address,
    phone = phone,
    latitude = latitude,
    longitude = longitude,
    categoryId = categoryId,
    status = status,
    photoUrl = photoUrl,
)

internal fun QualityEntity.asDomain(): Quality = Quality(
    id = id,
    name = name,
    description = description,
    applicableCategoryId = applicableCategoryId,
    status = status,
)

internal fun Quality.asEntity(): QualityEntity = QualityEntity(
    id = id,
    name = name,
    description = description,
    applicableCategoryId = applicableCategoryId,
    status = status,
)

internal fun RatingEntity.asDomain(): Rating = Rating(
    id = id,
    userId = userId,
    businessId = businessId,
    ratedOn = ratedOn,
    reportedPrice = reportedPrice,
    priceReportedOn = priceReportedOn,
    serviceScore = serviceScore,
    attentionScore = attentionScore,
    satisfactionScore = satisfactionScore,
    waitTime = waitTime,
    wouldRecommend = wouldRecommend,
    usageFrequency = usageFrequency,
    availability = availability,
    serviceMode = serviceMode,
)

internal fun Rating.asEntity(): RatingEntity = RatingEntity(
    id = id,
    userId = userId,
    businessId = businessId,
    ratedOn = ratedOn,
    reportedPrice = reportedPrice,
    priceReportedOn = priceReportedOn,
    serviceScore = serviceScore,
    attentionScore = attentionScore,
    satisfactionScore = satisfactionScore,
    waitTime = waitTime,
    wouldRecommend = wouldRecommend,
    usageFrequency = usageFrequency,
    availability = availability,
    serviceMode = serviceMode,
)

internal fun RatingQualityEntity.asDomain(): RatingQuality = RatingQuality(
    id = id,
    ratingId = ratingId,
    qualityId = qualityId,
)

internal fun RatingQuality.asEntity(): RatingQualityEntity = RatingQualityEntity(
    id = id,
    ratingId = ratingId,
    qualityId = qualityId,
)

internal fun EvidenceEntity.asDomain(): Evidence = Evidence(
    id = id,
    ratingId = ratingId,
    filePath = filePath,
    fileType = fileType,
    uploadedOn = uploadedOn,
)

internal fun Evidence.asEntity(): EvidenceEntity = EvidenceEntity(
    id = id,
    ratingId = ratingId,
    filePath = filePath,
    fileType = fileType,
    uploadedOn = uploadedOn,
)

internal fun BusinessWithCategoryEntity.asDomain(): BusinessWithCategory = BusinessWithCategory(
    business = business.asDomain(),
    category = category.asDomain(),
)

internal fun UserWithRatingsEntity.asDomain(): UserWithRatings = UserWithRatings(
    user = user.asDomain(),
    ratings = ratings.map { it.asDomain() },
)

internal fun RatingWithDetailsEntity.asDomain(): RatingDetails = RatingDetails(
    rating = rating.asDomain(),
    user = user.asDomain(),
    business = business.asDomain(),
    qualities = qualities.map { it.asDomain() },
    evidences = evidences.map { it.asDomain() },
)
