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
import shop.utils.UserNotFoundException

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

    fun markAsInactive(userId: UserID): Either<UserNotFoundException, User> {
        val foundUser = userRepository.findById(userId)

        return if (foundUser.isPresent) {
            val inactiveUser = userRepository.save(foundUser.get().copy(isActive = false))
            Either.right(inactiveUser)
        } else {
            Either.left(UserNotFoundException("User with id ${userId.id} not found."))
        }
    }

    fun updateUser(
            userId: UserID, newUsername: String,
            newPassword: String, newRoles: List<Role>,
            newIsActive: Boolean): Either<UserNotFoundException, User> {

        val foundUser = userRepository.findById(userId)

        return if (foundUser.isPresent) {
            val updatedUser = userRepository.save(foundUser.get().copy(
                    username = newUsername,
                    password = newPassword,
                    roles = newRoles,
                    isActive = newIsActive))
            Either.right(updatedUser)
        } else {
            Either.left(UserNotFoundException("User with id ${userId.id} not found."))
        }
    }
}