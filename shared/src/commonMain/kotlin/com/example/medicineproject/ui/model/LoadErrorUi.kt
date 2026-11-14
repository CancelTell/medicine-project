package com.example.medicineproject.ui.model

import androidx.compose.runtime.Immutable
import com.example.medicineproject.domain.errors.LoadException
import com.example.medicineproject.resources.Res
import com.example.medicineproject.resources.error_bad_response
import com.example.medicineproject.resources.error_no_connection
import com.example.medicineproject.resources.error_not_found
import com.example.medicineproject.resources.error_server
import com.example.medicineproject.resources.error_timeout
import com.example.medicineproject.resources.error_request
import org.jetbrains.compose.resources.StringResource

@Immutable
data class LoadErrorUi(val message: StringResource)

fun LoadException.toUi(): LoadErrorUi = LoadErrorUi(
    when (this) {
        is LoadException.NoConnection -> Res.string.error_no_connection
        is LoadException.Timeout -> Res.string.error_timeout
        is LoadException.NotFound -> Res.string.error_not_found
        is LoadException.Request -> Res.string.error_request
        is LoadException.Server -> Res.string.error_server
        is LoadException.BadResponse -> Res.string.error_bad_response
    },
)
