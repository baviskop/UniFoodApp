package com.landt.unifoodapp.ui.navigation

import com.landt.unifoodapp.data.models.FoodItem
import kotlinx.serialization.Serializable

interface NavRoute
@Serializable
object Login: NavRoute

@Serializable
object SignUp: NavRoute

@Serializable
object AuthScreen: NavRoute

@Serializable
object Home: NavRoute

@Serializable
data class RestaurantDetails(
    val restaurantId: String,
    val restaurantName: String,
    val restaurantImageUrl: String,
): NavRoute
@Serializable
data class FoodDetails(val foodItem: FoodItem): NavRoute
@Serializable
object Cart: NavRoute

@Serializable
object Notification: NavRoute

@Serializable
object AddressList : NavRoute

@Serializable
object AddAddress : NavRoute