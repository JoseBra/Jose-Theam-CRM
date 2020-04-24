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
import shop.utils.CustomerNotFoundException
import shop.utils.IdGenerator
import shop.utils.UserNotFoundException
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

    @Test
    fun `it should update an existing customer`() {
        val expectedUpdatedCustomer = expectedCustomer.copy(
                name = "updatedName",
                surname = "updatedSurname",
                lastUpdatedBy = creatingUser
        )

        every { customerRepository.findById(expectedCustomer.customerId) } returns Optional.of(expectedCustomer)
        every { customerRepository.save(expectedUpdatedCustomer) } returns expectedUpdatedCustomer
        every { userRepository.findByUsername(creatingUser.username) } returns creatingUser

        val updatedCustomer = service.updateCustomer(
                expectedCustomer.customerId,
                expectedUpdatedCustomer.name,
                expectedUpdatedCustomer.surname,
                creatingUser.username)

        verify { customerRepository.save(expectedUpdatedCustomer) }
        verify { customerRepository.findById(expectedCustomer.customerId) }
        verify { userRepository.findByUsername(creatingUser.username) }

        assertEquals(expectedUpdatedCustomer, (updatedCustomer as Either.Right).b)
    }

    @Test
    fun `it should return an error when updating a Customer that does not exist`() {
        every { customerRepository.findById(expectedCustomer.customerId) } returns Optional.empty()
        every { userRepository.findByUsername(creatingUser.username) } returns creatingUser

        val updateCustomerAttempt = service.updateCustomer(
                expectedCustomer.customerId,
                "anything",
                "anything",
                creatingUser.username)

        verify { customerRepository.findById(expectedCustomer.customerId) }
        verify { userRepository.findByUsername(creatingUser.username) }

        updateCustomerAttempt.shouldBeLeft()
        assert((updateCustomerAttempt as Either.Left).a is CustomerNotFoundException)
    }

    @Test
    fun `it should return an error when updating with a User that does not exist`() {
        every { customerRepository.findById(expectedCustomer.customerId) } returns Optional.empty()
        every { userRepository.findByUsername(creatingUser.username) } returns null

        val updateCustomerAttempt = service.updateCustomer(
                expectedCustomer.customerId,
                "anything",
                "anything",
                creatingUser.username)

        verify { userRepository.findByUsername(creatingUser.username) }

        updateCustomerAttempt.shouldBeLeft()
        assert((updateCustomerAttempt as Either.Left).a is UserNotFoundException)

    }

    @Test
    fun `it should delete a Customer when it exists`() {
        every { customerRepository.findById(expectedCustomer.customerId) } returns Optional.of(expectedCustomer)
        every { customerRepository.delete(expectedCustomer) } returns Unit

        val updatedCustomer = service.deleteCustomer(expectedCustomer.customerId)

        verify { customerRepository.delete(expectedCustomer) }
        verify { customerRepository.findById(expectedCustomer.customerId) }

        assertEquals(expectedCustomer, (updatedCustomer as Either.Right).b)
    }

    @Test
    fun `it should return an error when deleting a Customer that does not exist`() {
        every { customerRepository.findById(expectedCustomer.customerId) } returns Optional.empty()

        val deleteCustomerAttempt = service.deleteCustomer(expectedCustomer.customerId)

        verify { customerRepository.findById(expectedCustomer.customerId) }

        deleteCustomerAttempt.shouldBeLeft()
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