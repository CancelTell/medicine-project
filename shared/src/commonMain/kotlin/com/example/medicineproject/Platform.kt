package com.example.medicineproject

expect class Platform() {
    val name: String
}

expect fun getPlatform(): Platform
