package shop.service

import arrow.core.Either
import io.kotest.assertions.arrow.either.shouldBeLeft
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import shop.model.*
import shop.repository.CustomerRepository
import shop.repository.UserRepository
import shop.utils.IdGenerator
import java.util.*

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    lateinit var customerRepository: CustomerRepository

    @MockK
    lateinit var userRepository: UserRepository

    @MockK
    lateinit var idGenerator: IdGenerator

    @InjectMocks
    lateinit var service: CustomerService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        service = CustomerService(customerRepository, userRepository, idGenerator)
    }

    @Test
    fun `it should create a new customer`() {
        every { customerRepository.save(expectedCustomer) } returns expectedCustomer
        every { userRepository.findByUsername(creatingUser.username) } returns creatingUser

        every { idGenerator.generate() } returns "testId"

        val createdCustomer = service.createCustomer(expectedCustomer.name, expectedCustomer.surname, creatingUser.username)

        verify { customerRepository.save(expectedCustomer) }
        verify { userRepository.findByUsername(creatingUser.username) }

        assertEquals(expectedCustomer, (createdCustomer as Either.Right).b)
    }

    @Test
    fun `it should trigger an error when trying to create a customer with a user that does not exist`() {
        every { userRepository.findByUsername(creatingUser.username) } returns null

        every { idGenerator.generate() } returns "testId"

        val createCustomerAttempt = service.createCustomer(expectedCustomer.name, expectedCustomer.surname, creatingUser.username)

        verify { userRepository.findByUsername(creatingUser.username) }

        createCustomerAttempt.shouldBeLeft()
    }

    @Test
    fun `it should list all customers`() {
        every { customerRepository.findAll() } returns listOf(expectedCustomer, expectedCustomer)

        assertEquals(listOf(expectedCustomer, expectedCustomer), service.listAllCustomers())
    }

    @Test
    fun `it should retrieve details for an existing customer`() {
        every { customerRepository.findById(expectedCustomer.customerId) } returns Optional.of(expectedCustomer)

        assertEquals(expectedCustomer, (service.retrieveDetails(expectedCustomer.customerId) as Either.Right).b)
    }

    @Test
    fun `it should return an error when customer is not found`() {
        every { customerRepository.findById(any()) } returns Optional.empty()

        val findCustomerAttempt = service.retrieveDetails(expectedCustomer.customerId)

        findCustomerAttempt.shouldBeLeft()
    }

    val creatingUser = User(UserID("anyId"),
            "anyCreatingUsername",
            "", listOf(Role.ROLE_USER))
    private val expectedCustomer = Customer(
            CustomerID("testId"),
            "name",
            "surname",
            creatingUser
    )
}