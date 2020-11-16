package com.reemsib.mosta3ml.utils

class URLs {
    companion object{

        const val ROOT_URL = "http://mst3jl.com/api/"

        const val URL_REGISTER =  ROOT_URL + "register"
        const val URL_LOGIN = ROOT_URL + "login"
        const val URL_LOGOUT = ROOT_URL + "logout"
        const val URL_USER_INFO = ROOT_URL + "getUserInfo"
        const val URL_RESET_PASS = ROOT_URL + "reset-password/create"

        const val URL_SETTING = ROOT_URL + "getSetting"
        const val URL_COUNTRIES = ROOT_URL + "getCountries"
        const val URL_CITIES = ROOT_URL + "getCities"
        const val URL_YEARS = ROOT_URL + "getYears"

        const val URL_MAIN_CATEGORIES = ROOT_URL + "getMainCategories"
        const val URL_SUB_CATEGORIES_TO_CATEGORY = ROOT_URL + "getSubCategoriesToCategory/"

        const val URL_ADVERT_IN_CATEGORY = ROOT_URL + "showAdvertisementInCategory/"
        const val URL_ADVERT_INFO = ROOT_URL + "showAdvertisementInformation/"
        const val URL_CREATE_ADVERT = ROOT_URL + "createAdvertisement"

        const val URL_FREE_ADVERTS = ROOT_URL + "getFreeAdvertisements"
        const val URL_PAID_ADVERTS = ROOT_URL + "getPaidAdvertisements"
        const val URL_MAIN_SUB_CAT = ROOT_URL + "getMainCategoriesWithSubCategories"
        const val URL_MY_ADVERT = ROOT_URL + "getMyAdvertisements"

        const val URL_USER_CHAT = ROOT_URL + "getUserChat"
        const val URL_CHAT_INFO = ROOT_URL + "showChatInfo/"
        const val URL_SEND_MESSAGE = ROOT_URL + "sendMessage"
        const val URL_NEW_CHAT = ROOT_URL + "newChat"

        const val URL_ADD_COMMENT = ROOT_URL + "createReview"
        const val URL_DELETE_REVIEW= ROOT_URL + "deleteReview"
        const val URL_EDIT_REVIEW= ROOT_URL + "editReview/"


        const val URL_ADD_FAVORITE = ROOT_URL + "addFavorite"
        const val URL_GET_MY_FAVORITE = ROOT_URL + "getMyFavorite"

        const val URL_DELETE_FAVORITE = ROOT_URL + "deleteMyFavorite"



    }
}