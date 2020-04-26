package shop.utils

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.UNAUTHORIZED, reason = "Expired or invalid JWT token.")
class JwtExpiredOrInvalidToken(override val message: String) : JwtException(message)

@ResponseStatus(HttpStatus.UNAUTHORIZED, reason = "Invalid username/password provided.")
class FailedLoginException(message: String) : Exception(message)

@ResponseStatus(HttpStatus.NOT_FOUND, reason = "Customer with specified ID was not found.")
class CustomerNotFoundException(message: String) : Exception(message)

@ResponseStatus(HttpStatus.CONFLICT, reason = "Username already in use.")
class UserAlreadyExists(message: String) : Exception(message)

@ResponseStatus(HttpStatus.UNAUTHORIZED, reason = "Requesting user does not exist in the system.")
class UserNotFoundException(message: String) : Exception(message)

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Input encoded picture could not be validated as Base64 content.")
class InvalidBase64Picture(message: String) : Exception(message)

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Specified picture ID does not exist.")
class PictureNotFoundException(message: String) : Exception(message)

@ResponseStatus(HttpStatus.NOT_FOUND, reason = "This customer has no picture attached.")
class CustomerHasNoPictureException(message: String) : Exception(message)