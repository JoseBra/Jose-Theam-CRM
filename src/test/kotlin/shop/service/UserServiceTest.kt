package shop.service

import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.InjectMocks
import org.springframework.security.crypto.password.PasswordEncoder
import shop.model.Role
import shop.model.User
import shop.model.UserID
import shop.repository.UserRepository
import shop.utils.IdGenerator
import java.util.*

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
        every { userRepository.findByUsername(expectedUser.username) } returns null

        every { userRepository.save(expectedUser) } returns expectedUser

        every { idGenerator.generate() } returns expectedUser.userId.id
        every { passwordEncoder.encode(any()) } returns expectedUser.password

        val createdCustomer = service.createUser(expectedUser.username, expectedUser.password, expectedUser.roles)

        verify { userRepository.save(expectedUser) }
        verify { passwordEncoder.encode(expectedUser.password) }
        verify { idGenerator.generate() }

        assertEquals(expectedUser, (createdCustomer as Either.Right).b)
    }

    @Test
    fun `it should not create a new user if username is already in use`() {
        every { userRepository.findByUsername(expectedUser.username) } returns expectedUser

        service
                .createUser(expectedUser.username, expectedUser.password, expectedUser.roles)
                .shouldBeLeft()
    }

    @Test
    fun `it should list all active users`() {
        every { userRepository.findByIsActiveTrue() } returns mutableListOf(expectedUser, expectedUser)

        assertEquals(listOf(expectedUser, expectedUser), service.listAllActiveUsers())
    }

    @Test
    fun `it should allow user updates when it exists`() {
        every { userRepository.findById(expectedUser.userId) } returns Optional.of(expectedUser)

        val expectedUpdatedUser = expectedUser.copy(
                username = "newUsername",
                roles = listOf(Role.ROLE_ADMIN, Role.ROLE_USER)
        )

        every { userRepository.save(expectedUpdatedUser) } returns expectedUpdatedUser

        val updatedUser = service
                .updateUser(
                        expectedUpdatedUser.userId,
                        expectedUpdatedUser.username,
                        expectedUpdatedUser.password,
                        expectedUpdatedUser.roles,
                        expectedUpdatedUser.isActive)

        assertEquals(expectedUpdatedUser, (updatedUser as Either.Right).b)
    }

    @Test
    fun `it should return an error when user does not exists`() {
        every { userRepository.findById(expectedUser.userId) } returns Optional.empty()

        service
                .updateUser(expectedUser.userId, "", "", emptyList(), true)
                .shouldBeLeft()
    }

    private val expectedUser = User(
            UserID("anyId"),
            "username",
            "password",
            listOf(Role.ROLE_ADMIN, Role.ROLE_USER)
    )

}