package shop.service

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
import shop.utils.IdGenerator

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
        val expectedCustomer = Customer("name", "surname", CustomerID("testId"))

        every { customerRepository.save(expectedCustomer) } returns expectedCustomer

        every { idGenerator.generate() } returns "testId"

        val createdCustomer = service.createCustomer("name", "surname")

        verify { customerRepository.save(expectedCustomer) }

        assertEquals(expectedCustomer, createdCustomer)
    }
}