package shop.controller

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.json.JSONObject
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import shop.model.Customer
import shop.model.CustomerID
import shop.service.CustomerService

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {
    @MockBean
    lateinit var customerService: CustomerService

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser
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
                .andExpect(MockMvcResultMatchers.jsonPath("$.customerId").value(testCustomer.customerId.id))
    }

    @Test
    @WithMockUser
    fun `it should answer with 400 when post body params are incorrect`() {
        val invalidCreateCustomerRequest = JSONObject()
                .put("name", "anyName")
                .put("tomato", "wrongThing")

        val request = MockMvcRequestBuilders
                .post("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidCreateCustomerRequest.toString())

        mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isBadRequest)
    }

    @Test
    fun `it should return a 401 error if user has not been authenticated`() {
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
                .andExpect(MockMvcResultMatchers.status().isUnauthorized)
    }
}