package com.example.medicineproject.domain.errors

sealed class LoadException : Exception() {
    class NoConnection : LoadException()
    class Timeout : LoadException()
    class NotFound : LoadException()
    class Request(val statusCode: Int) : LoadException()
    class Server(val statusCode: Int? = null) : LoadException()
    class BadResponse : LoadException()
}
