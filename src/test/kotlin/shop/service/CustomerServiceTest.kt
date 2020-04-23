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
import shop.model.Customer
import shop.model.CustomerID
import shop.repository.CustomerRepository
import shop.utils.IdGenerator
import java.util.*

@ExtendWith(MockKExtension::class)
class CustomerServiceTest {

    @MockK
    lateinit var customerRepository: CustomerRepository

    @MockK
    lateinit var idGenerator: IdGenerator

    @InjectMocks
    lateinit var service: CustomerService

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        service = CustomerService(customerRepository, idGenerator)
    }

    @Test
    fun `it should create a new customer`() {
        every { customerRepository.save(expectedCustomer) } returns expectedCustomer

        every { idGenerator.generate() } returns "testId"

        val createdCustomer = service.createCustomer("name", "surname")

        verify { customerRepository.save(expectedCustomer) }

        assertEquals(expectedCustomer, createdCustomer)
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

    private val expectedCustomer = Customer(CustomerID("testId"), "name", "surname")
}