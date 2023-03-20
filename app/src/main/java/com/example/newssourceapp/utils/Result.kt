package com.example.newssourceapp.utils

/**
 * A generic Result class to abstract the responses received from the API and to improve the error
 * handling strategies.
 * This Result class can have two types:
 * - Success - meaning that some outcome is deemed successful and the corresponding [data] can be
 * passed through
 * - Error - meaning that something erroneous has happened and we want to include the error [message]
 * for better error handling
 */
sealed class Result<T>(val data: T?, val message: String?) {
    class Success<T>(data: T) : Result<T>(data, null)
    class Error<T>(message: String) : Result<T>(null, message)
}
