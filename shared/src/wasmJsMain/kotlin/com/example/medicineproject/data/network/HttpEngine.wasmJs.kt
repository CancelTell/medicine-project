package com.example.medicineproject.data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.js.Js

internal actual fun createHttpEngine(): HttpClientEngine = Js.create()
