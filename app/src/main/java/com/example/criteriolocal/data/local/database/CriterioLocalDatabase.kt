package com.example.criteriolocal.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.criteriolocal.data.local.converter.EnumConverters
import com.example.criteriolocal.data.local.dao.BusinessDao
import com.example.criteriolocal.data.local.dao.CategoryDao
import com.example.criteriolocal.data.local.dao.EvidenceDao
import com.example.criteriolocal.data.local.dao.QualityDao
import com.example.criteriolocal.data.local.dao.RatingDao
import com.example.criteriolocal.data.local.dao.RatingQualityDao
import com.example.criteriolocal.data.local.dao.UserDao
import com.example.criteriolocal.data.local.entity.BusinessEntity
import com.example.criteriolocal.data.local.entity.CategoryEntity
import com.example.criteriolocal.data.local.entity.EvidenceEntity
import com.example.criteriolocal.data.local.entity.QualityEntity
import com.example.criteriolocal.data.local.entity.RatingEntity
import com.example.criteriolocal.data.local.entity.RatingQualityEntity
import com.example.criteriolocal.data.local.entity.UserEntity

@Database(
    entities = [
        UserEntity::class,
        CategoryEntity::class,
        BusinessEntity::class,
        QualityEntity::class,
        RatingEntity::class,
        RatingQualityEntity::class,
        EvidenceEntity::class,
    ],
    version = 4,
    exportSchema = false,
)
@TypeConverters(EnumConverters::class)
abstract class CriterioLocalDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun businessDao(): BusinessDao
    abstract fun qualityDao(): QualityDao
    abstract fun ratingDao(): RatingDao
    abstract fun ratingQualityDao(): RatingQualityDao
    abstract fun evidenceDao(): EvidenceDao

    companion object {
        fun build(context: Context): CriterioLocalDatabase {
            return Room.databaseBuilder(
                context,
                CriterioLocalDatabase::class.java,
                "criterio_local.db",
            ).fallbackToDestructiveMigration().build()
        }
    }
}
