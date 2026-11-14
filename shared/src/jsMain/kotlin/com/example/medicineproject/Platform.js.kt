package com.example.medicineproject

import web.navigator.navigator

actual class Platform actual constructor() {
    private val userAgent = navigator.userAgent
    private val browserList = listOf("Chrome", "Firefox", "Safari", "Edge")

    actual val name: String = userAgent.findAnyOf(browserList, ignoreCase = true)
        ?.let { (startIndex) -> userAgent.substring(startIndex).substringBefore(" ") }
        ?: "Unknown"
}

actual fun getPlatform(): Platform = Platform()
