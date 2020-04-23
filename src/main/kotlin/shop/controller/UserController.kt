package shop.controller

import arrow.core.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import shop.model.Role
import shop.model.User
import shop.service.UserService

@RestController
class UserController {

    @Autowired
    private lateinit var userService: UserService

    @PostMapping(
            "/users",
            consumes = ["application/json"],
            produces = ["application/json"])
    @PreAuthorize("hasRole('ADMIN')")
    fun createUser(
            @RequestBody request: CreateUserRequest
    ): ResponseEntity<UserResponse> {
        val createUserAttempt = userService.createUser(request.username, request.password, request.roles)

        when (createUserAttempt) {
            is Either.Right ->
                return ResponseEntity
                        .status(HttpStatus.CREATED)
                        .body(UserResponse.fromUser(createUserAttempt.b))
            is Either.Left ->
                throw createUserAttempt.a
        }
    }

}

data class CreateUserRequest(
        val username: String,
        val password: String,
        val roles: ArrayList<Role>
)

data class UserResponse(
        val username: String,
        val userId: String,
        val roles: List<Role>
) {
    companion object {
        fun fromUser(user: User) =
                UserResponse(user.username, user.userId.id, user.roles)
    }
}


