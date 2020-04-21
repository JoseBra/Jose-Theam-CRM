package shop.service

import org.springframework.stereotype.Service
import shop.model.Role
import shop.model.User

@Service
class UserService {
    fun createUser(username: String, password: String, roles: List<Role>): User {
        TODO("Not yet implemented")
    }
}