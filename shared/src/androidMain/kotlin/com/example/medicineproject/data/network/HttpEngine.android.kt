package com.example.medicineproject.data.network

import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.cio.CIO

internal actual fun createHttpEngine(): HttpClientEngine = CIO.create()
