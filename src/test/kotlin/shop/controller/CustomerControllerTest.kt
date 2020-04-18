package shop.controller

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import shop.model.Customer
import shop.model.CustomerID
import shop.service.CustomerService

@WebMvcTest(CustomerController::class)
class CustomerControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var customerService: CustomerService

    @BeforeEach
    fun setUp() = MockitoAnnotations.initMocks(this)

    @Test
    fun `it should be able to create a customer with correct parameters`() {
        val testCustomer = Customer(CustomerID("anyId"), "testCustomer", "anySurname")

        whenever(customerService.createCustomer(any(), any())).thenReturn(testCustomer)

        val validCreateCustomerRequestBody = JSONObject()
                .put("name", testCustomer.name)
                .put("surname", testCustomer.surname)

        val request = MockMvcRequestBuilders
                .post("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validCreateCustomerRequestBody.toString())

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(testCustomer.name))
                .andExpect(MockMvcResultMatchers.jsonPath("$.surname").value(testCustomer.surname))
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(testCustomer.customerId!!.id))
    }
}