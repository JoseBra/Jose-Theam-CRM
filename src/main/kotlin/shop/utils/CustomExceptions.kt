package shop.utils

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.NOT_FOUND, reason = "Customer with specified ID was not found.")
class CustomerNotFoundException(message: String) : Exception(message)