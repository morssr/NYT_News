package com.mls.mor.nytnews.utilities

open class ApiResponseException(
    val code: Int,
    override val message: String
) : Exception(message)

class NetworkException(
    override val message: String
) : ApiResponseException(message = message, code = 0)

class UnknownException(
    override val message: String
) : ApiResponseException(message = message, code = 1)

class BadRequestException(
    override val message: String
) : ApiResponseException(message = message, code = 400)

class UnauthorizedException(
    override val message: String
) : ApiResponseException(message = message, code = 401)

class BadResponseException(
    override val message: String
) : ApiResponseException(message = message, code = 402)