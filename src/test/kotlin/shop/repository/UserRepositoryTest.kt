package shop.shop.repository

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import shop.model.Role
import shop.model.User
import shop.model.UserID
import shop.repository.UserRepository

@ExtendWith(SpringExtension::class)
@SpringBootTest
class UserRepositoryTest {
    @Autowired
    lateinit var repository: UserRepository

    @BeforeEach
    fun setUp() {
        repository.saveAll(listOf(activeUser, inactiveUser))
    }

    @Test
    fun `it should only list active users`() {
        val foundActiveUsers = repository.findByIsActiveTrue().toList()

        assert(foundActiveUsers.contains(activeUser))
        assert(!foundActiveUsers.contains(inactiveUser))
    }

    private val activeUser = User(UserID("activeUserId"), "activeUser", "password", listOf(Role.ROLE_USER))
    private val inactiveUser = User(
            UserID("inactiveUserId"), "inactiveUser",
            "password", listOf(Role.ROLE_USER),
            isActive = false
    )
}