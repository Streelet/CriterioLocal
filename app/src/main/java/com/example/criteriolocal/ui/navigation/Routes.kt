package com.example.criteriolocal.ui.navigation

object Routes {
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val Profile = "profile"
    const val BusinessDetail = "business_detail"
    const val BusinessIdArg = "businessId"
    const val BusinessDetailPattern = "$BusinessDetail/{$BusinessIdArg}"
    const val RatingForm = "rating_form"
    const val RatingFormPattern = "$RatingForm/{$BusinessIdArg}"

    fun businessDetail(businessId: Long): String = "$BusinessDetail/$businessId"
    fun ratingForm(businessId: Long): String = "$RatingForm/$businessId"
}
