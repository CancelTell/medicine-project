package com.example.medicineproject.data.network

import io.ktor.client.engine.HttpClientEngine

internal expect fun createHttpEngine(): HttpClientEngine
