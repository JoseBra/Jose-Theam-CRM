package shop.controller

import arrow.core.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import shop.model.Role
import shop.model.User
import shop.model.UserID
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

    @GetMapping(
            "/users",
            produces = ["application/json"])
    @PreAuthorize("hasRole('ADMIN')")
    fun listActiveUsers(): ResponseEntity<ListUsersResponse> {
        val allUsersResponses = userService.listAllActiveUsers().map { UserResponse.fromUser(it) }

        return ResponseEntity
                .ok()
                .body(ListUsersResponse(allUsersResponses))
    }

    @DeleteMapping(
            "/users/{id}",
            produces = ["application/json"])
    @PreAuthorize("hasRole('ADMIN')")
    fun deleteUser(
            @PathVariable id: String
    ): ResponseEntity<UserResponse> {
        val inactiveUserAttempt = userService.markAsInactive(UserID(id))

        when (inactiveUserAttempt) {
            is Either.Right ->
                return ResponseEntity
                        .ok()
                        .body(UserResponse.fromUser(inactiveUserAttempt.b))
            is Either.Left ->
                throw inactiveUserAttempt.a
        }
    }

    @PutMapping(
            "/users/{id}",
            consumes = ["application/json"],
            produces = ["application/json"])
    @PreAuthorize("hasRole('ADMIN')")
    fun updateUser(
            @PathVariable id: String,
            @RequestBody request: UpdateUserRequest
    ): ResponseEntity<UserResponse> {

        val updateUserAttempt = userService.updateUser(
                UserID(id),
                request.username,
                request.password,
                request.roles,
                request.isActive
        )

        when (updateUserAttempt) {
            is Either.Right ->
                return ResponseEntity
                        .status(HttpStatus.OK)
                        .body(UserResponse.fromUser(updateUserAttempt.b))

            is Either.Left ->
                throw updateUserAttempt.a
        }
    }
}

data class CreateUserRequest(
        val username: String,
        val password: String,
        val roles: ArrayList<Role>
)

data class UpdateUserRequest(
        val username: String,
        val password: String,
        val roles: ArrayList<Role>,
        val isActive: Boolean = true
)

data class UserResponse(
        val username: String,
        val userId: String,
        val roles: List<Role>,
        val isActive: Boolean
) {
    companion object {
        fun fromUser(user: User) =
                UserResponse(user.username, user.userId.id, user.roles, user.isActive)
    }
}

data class ListUsersResponse(
        val items: List<UserResponse>
)


