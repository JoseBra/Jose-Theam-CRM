package shop.controller

import arrow.core.Either
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import shop.model.*
import shop.service.CustomerService
import shop.utils.CustomerNotFoundException

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class CustomerControllerTest {
    @MockBean
    lateinit var customerService: CustomerService

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser(roles = ["USER"], username = "testUser")
    fun `it should be able to create a customer with correct parameters and hold a reference to the user that created it`() {
        val creatingUser = User(UserID("anyUserId"), "testUser", "user", listOf(Role.ROLE_USER))
        val testCustomer = Customer(
                CustomerID("anyId"),
                "testCustomer",
                "anySurname",
                creatingUser
        )

        whenever(customerService.createCustomer(any(), any(), any())).thenReturn(Either.right(testCustomer))

        val validCreateCustomerRequestBody = JSONObject()
                .put("name", testCustomer.name)
                .put("surname", testCustomer.surname)

        val request = MockMvcRequestBuilders
                .post("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validCreateCustomerRequestBody.toString())

        mockMvc.perform(request)
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(testCustomer.name))
                .andExpect(jsonPath("$.surname").value(testCustomer.surname))
                .andExpect(jsonPath("$.customerId").value(testCustomer.customerId.id))
                .andExpect(jsonPath("$.createdBy.username").value(testCustomer.createdBy.username))
                .andExpect(jsonPath("$.createdBy.userId").value(testCustomer.createdBy.userId.id))
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `it should answer with 400 when post body params are incorrect creating a customer`() {
        val invalidCreateCustomerRequest = JSONObject()
                .put("name", "anyName")
                .put("tomato", "wrongThing")

        val request = MockMvcRequestBuilders
                .post("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidCreateCustomerRequest.toString())

        mockMvc.perform(request)
                .andExpect(status().isBadRequest)
    }

    @Test
    fun `it should return a 401 error if user has not been authenticated`() {
        val validCreateCustomerRequestBody = JSONObject()
                .put("name", "anyName")
                .put("surname", "anySurname")

        val request = MockMvcRequestBuilders
                .post("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validCreateCustomerRequestBody.toString())

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `it should list all customers`() {
        val testCustomer = Customer(CustomerID("anyId"), "testCustomer", "anySurname", User(UserID(""), "", "", emptyList()))

        whenever(customerService.listAllCustomers()).thenReturn(listOf(testCustomer))

        val request = MockMvcRequestBuilders
                .get("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.items").isArray)
                .andExpect(jsonPath("$.items[0].customerId").value(testCustomer.customerId.id))
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `it should return empty array object when there are no customers`() {
        whenever(customerService.listAllCustomers()).thenReturn(emptyList())

        val request = MockMvcRequestBuilders
                .get("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.items").isArray)
                .andExpect(jsonPath("$.items").isEmpty)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `it should retrieve details for a customer given its id`() {
        val expectedCustomer = Customer(
                CustomerID("anyId"),
                "testCustomer",
                "anySurname",
                User(UserID("anyID"), "anySurname", "", emptyList())
        )

        whenever(
                customerService.retrieveDetails(expectedCustomer.customerId)
        )
                .thenReturn(Either.right(expectedCustomer))

        val request = MockMvcRequestBuilders
                .get("/customers/${expectedCustomer.customerId.id}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.customerId").value(expectedCustomer.customerId.id))
                .andExpect(jsonPath("$.createdBy.userId").value(expectedCustomer.createdBy.userId.id))
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `it should respond 404 when customer is not found`() {
        whenever(
                customerService.retrieveDetails(any())
        )
                .thenReturn(Either.left(CustomerNotFoundException("")))

        val request = MockMvcRequestBuilders
                .get("/customers/1234")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
                .andExpect(status().isNotFound)
    }
}