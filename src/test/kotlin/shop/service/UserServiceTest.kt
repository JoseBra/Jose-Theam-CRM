package shop.service

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.springframework.security.crypto.password.PasswordEncoder
import shop.model.Role
import shop.model.User
import shop.model.UserID
import shop.repository.UserRepository
import shop.utils.IdGenerator

class UserServiceTest {
    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var idGenerator: IdGenerator

    @MockK
    lateinit var passwordEncoder: PasswordEncoder

    @InjectMocks
    lateinit var service: UserService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        service = UserService(userRepository, idGenerator, passwordEncoder)
    }

    @Test
    fun `it should create a user with the given values encoding its password`() {
        val expectedUser = User(
                UserID("anyId"),
                "username",
                "password",
                listOf(Role.ROLE_ADMIN, Role.ROLE_USER)
        )

        every { userRepository.save(expectedUser) } returns expectedUser

        every { idGenerator.generate() } returns expectedUser.userId.id
        every { passwordEncoder.encode(any()) } returns expectedUser.password

        val createdCustomer = service.createUser(expectedUser.username, expectedUser.password, expectedUser.roles)

        verify { userRepository.save(expectedUser) }
        verify { passwordEncoder.encode(expectedUser.password) }
        verify { idGenerator.generate() }

        Assertions.assertEquals(expectedUser, createdCustomer)
    }
}