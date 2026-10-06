package com.example.medicineproject

actual class Platform actual constructor() {
    actual val name: String = "Desktop (Java ${System.getProperty("java.version")})"
}

actual fun getPlatform(): Platform = Platform()
