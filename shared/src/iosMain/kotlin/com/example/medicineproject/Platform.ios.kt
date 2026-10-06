package com.example.medicineproject

actual class Platform actual constructor() {
    actual val name: String = "iOS"
}

actual fun getPlatform(): Platform = Platform()
