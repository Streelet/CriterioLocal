package com.example.criteriolocal.ui.navigation

object Routes {
    const val Login = "login"
    const val Register = "register"
    const val Home = "home"
    const val Profile = "profile"
    const val BusinessDetail = "business_detail"
    const val BusinessIdArg = "businessId"
    const val BusinessDetailPattern = "$BusinessDetail/{$BusinessIdArg}"

    fun businessDetail(businessId: Long): String = "$BusinessDetail/$businessId"
}
