package shop.service

import arrow.core.Either
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import shop.model.Role
import shop.model.User
import shop.model.UserID
import shop.repository.UserRepository
import shop.utils.IdGenerator
import shop.utils.UserAlreadyExists

@Service
class UserService(
        @Autowired
        val userRepository: UserRepository,
        @Autowired
        val idGenerator: IdGenerator,
        @Autowired
        val passwordEncoder: PasswordEncoder
) {
    fun createUser(username: String, password: String, roles: List<Role>): Either<UserAlreadyExists, User> {
        return if (userRepository.findByUsername(username) != null) {
            Either.left(UserAlreadyExists("Username already in use."))
        } else {
            val userToCreate = User(
                    UserID(idGenerator.generate()),
                    username,
                    passwordEncoder.encode(password),
                    roles
            )

            Either.right(userRepository.save(userToCreate))
        }
    }

    fun listAllActiveUsers(): List<User> {
        return userRepository.findByIsActiveTrue().toList()
    }
}