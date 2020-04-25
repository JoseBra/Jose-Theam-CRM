package shop.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import shop.model.User
import shop.model.UserID

@Repository
interface UserRepository : CrudRepository<User, UserID> {
    fun findByUsername(username: String): User?
    fun findByIsActiveTrue(): MutableList<User>
    fun findByUsernameAndIsActiveTrue(username: String): User?
}
