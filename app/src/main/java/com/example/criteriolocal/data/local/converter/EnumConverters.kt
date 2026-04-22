package com.example.criteriolocal.data.local.converter

import androidx.room.TypeConverter
import com.example.criteriolocal.domain.model.AvailabilityOption
import com.example.criteriolocal.domain.model.BusinessStatus
import com.example.criteriolocal.domain.model.CatalogStatus
import com.example.criteriolocal.domain.model.EvidenceType
import com.example.criteriolocal.domain.model.ServiceModeOption
import com.example.criteriolocal.domain.model.UsageFrequencyOption
import com.example.criteriolocal.domain.model.UserStatus
import com.example.criteriolocal.domain.model.WaitTimeOption

class EnumConverters {
    @TypeConverter
    fun fromUserStatus(value: UserStatus): String = value.name

    @TypeConverter
    fun toUserStatus(value: String): UserStatus = UserStatus.valueOf(value)

    @TypeConverter
    fun fromBusinessStatus(value: BusinessStatus): String = value.name

    @TypeConverter
    fun toBusinessStatus(value: String): BusinessStatus = BusinessStatus.valueOf(value)

    @TypeConverter
    fun fromCatalogStatus(value: CatalogStatus): String = value.name

    @TypeConverter
    fun toCatalogStatus(value: String): CatalogStatus = CatalogStatus.valueOf(value)

    @TypeConverter
    fun fromWaitTimeOption(value: WaitTimeOption): String = value.name

    @TypeConverter
    fun toWaitTimeOption(value: String): WaitTimeOption = WaitTimeOption.valueOf(value)

    @TypeConverter
    fun fromUsageFrequencyOption(value: UsageFrequencyOption): String = value.name

    @TypeConverter
    fun toUsageFrequencyOption(value: String): UsageFrequencyOption = UsageFrequencyOption.valueOf(value)

    @TypeConverter
    fun fromAvailabilityOption(value: AvailabilityOption): String = value.name

    @TypeConverter
    fun toAvailabilityOption(value: String): AvailabilityOption = AvailabilityOption.valueOf(value)

    @TypeConverter
    fun fromServiceModeOption(value: ServiceModeOption): String = value.name

    @TypeConverter
    fun toServiceModeOption(value: String): ServiceModeOption = ServiceModeOption.valueOf(value)

    @TypeConverter
    fun fromEvidenceType(value: EvidenceType): String = value.name

    @TypeConverter
    fun toEvidenceType(value: String): EvidenceType = EvidenceType.valueOf(value)
}
