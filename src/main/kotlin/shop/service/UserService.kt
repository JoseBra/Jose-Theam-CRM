package shop.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import shop.model.Role
import shop.model.User
import shop.model.UserID
import shop.repository.UserRepository
import shop.utils.IdGenerator

@Service
class UserService(
        @Autowired
        val userRepository: UserRepository,
        @Autowired
        val idGenerator: IdGenerator,
        @Autowired
        val passwordEncoder: PasswordEncoder
) {
    fun createUser(username: String, password: String, roles: List<Role>): User {
        val userToCreate = User(
                UserID(idGenerator.generate()),
                username,
                passwordEncoder.encode(password),
                roles
        )

        return userRepository.save(userToCreate)
    }
}