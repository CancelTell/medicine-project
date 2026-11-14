package com.example.medicineproject

actual class Platform actual constructor() {
    actual val name: String = "Web with Kotlin/Wasm"
}

actual fun getPlatform(): Platform = Platform()
