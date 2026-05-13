package com.landt.unifoodapp.data.models

data class NotificationListResponse(
    val notifications: List<Notification>,
    val unreadCount: Int
)