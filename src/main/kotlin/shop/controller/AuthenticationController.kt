package shop.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import shop.config.security.JwtValidationException
import shop.service.AuthenticationService


@RestController
class AuthenticationController {
    @Autowired
    lateinit var authenticationService: AuthenticationService

    @PostMapping("/authentication/login")
    fun login(
            @RequestBody loginRequest: LoginRequest
    ): ResponseEntity<String> {
        return try {
            val generatedToken = authenticationService.login(loginRequest.username, loginRequest.password)
            ResponseEntity.status(HttpStatus.OK).body(
                    jacksonObjectMapper().createObjectNode().put("token", generatedToken).toString()
            )
        } catch (ex: JwtValidationException) {
            ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(jacksonObjectMapper().createObjectNode().put("error", ex.message).toString())
        }
    }

}

data class LoginRequest(
        val username: String,
        val password: String
)
