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
        val testCustomer = Customer(
                CustomerID("anyId"),
                "testCustomer",
                "anySurname",
                creatingUser
        )

        whenever(customerService.createCustomer(testCustomer.name, testCustomer.surname, creatingUser.username))
                .thenReturn(Either.right(testCustomer))

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
                .andExpect(jsonPath("$.lastUpdatedBy").isEmpty)
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
    @WithMockUser(roles = ["USER"], username = "testUser")
    fun `it should attach picture to customer at the moment of creating when providing a valid picture id`() {
        val expectedPicture = Picture(PictureID("any"), "")
        val testCustomer = Customer(
                CustomerID("anyId"),
                "testCustomer",
                "anySurname",
                creatingUser,
                picture = expectedPicture
        )

        whenever(customerService.createCustomer(
                testCustomer.name, testCustomer.surname,
                creatingUser.username, expectedPicture.pictureId
        )).thenReturn(Either.right(testCustomer))

        val validCreateCustomerRequestBody = JSONObject()
                .put("name", testCustomer.name)
                .put("surname", testCustomer.surname)
                .put("pictureId", testCustomer.picture?.pictureId?.id)

        val request = MockMvcRequestBuilders
                .post("/customers")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validCreateCustomerRequestBody.toString())

        val expectedCustomerPictureUri = "/customers/${testCustomer.customerId.id}/picture"

        mockMvc.perform(request)
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pictureUri").value(expectedCustomerPictureUri))
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

    @Test
    @WithMockUser(roles = ["USER"], username = "testUser")
    fun `it should update a Customer on a put request and hold a reference to the updating user`() {
        val updatingUser = User(UserID("anyUserId"), "testUser", "password", listOf(Role.ROLE_USER))

        val expectedCustomer = Customer(CustomerID("anyId"), "testCustomer", "anySurname",
                updatingUser, updatingUser)

        whenever(customerService.updateCustomer(
                expectedCustomer.customerId, expectedCustomer.name,
                expectedCustomer.surname, updatingUser.username)
        )
                .thenReturn(Either.right(expectedCustomer))

        val validUpdateRequestBody = JSONObject()
                .put("name", expectedCustomer.name)
                .put("surname", expectedCustomer.surname)

        val request = MockMvcRequestBuilders
                .put("/customers/${expectedCustomer.customerId.id}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUpdateRequestBody.toString())

        mockMvc.perform(request)
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value(expectedCustomer.name))
                .andExpect(jsonPath("$.surname").value(expectedCustomer.surname))
                .andExpect(jsonPath("$.customerId").value(expectedCustomer.customerId.id))
                .andExpect(jsonPath("$.createdBy.username").value(expectedCustomer.createdBy.username))
                .andExpect(jsonPath("$.createdBy.userId").value(expectedCustomer.createdBy.userId.id))
                .andExpect(jsonPath("$.lastUpdatedBy.userId").value(expectedCustomer.lastUpdatedBy!!.userId.id))
    }

    @Test
    @WithMockUser(roles = ["USER"], username = "testUser")
    fun `it should attach a picture to a customer on a put request if picture id is specified`() {
        val expectedPicture = Picture(PictureID("anyPictureId"), "")

        val expectedCustomer = Customer(CustomerID("anyId"), "testCustomer", "anySurname",
                creatingUser, creatingUser, expectedPicture)

        whenever(customerService.updateCustomer(
                expectedCustomer.customerId, expectedCustomer.name,
                expectedCustomer.surname, creatingUser.username,
                expectedPicture.pictureId)
        )
                .thenReturn(Either.right(expectedCustomer))

        val validUpdateRequestBody = JSONObject()
                .put("name", expectedCustomer.name)
                .put("surname", expectedCustomer.surname)
                .put("pictureId", expectedPicture.pictureId.id)

        val request = MockMvcRequestBuilders
                .put("/customers/${expectedCustomer.customerId.id}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUpdateRequestBody.toString())

        mockMvc.perform(request)
                .andExpect(status().isOk)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.pictureUri")
                        .value("/customers/${expectedCustomer.customerId.id}/picture"))
    }

    @Test
    @WithMockUser(roles = ["USER"], username = "testUser")
    fun `it should return 404 when updating a Customer that does not exist`() {
        whenever(
                customerService.updateCustomer(
                        CustomerID("1234"),
                        "name",
                        "surname",
                        creatingUser.username)
        )
                .thenReturn(Either.left(CustomerNotFoundException("")))

        val validUpdateRequestBody = JSONObject()
                .put("name", "name")
                .put("surname", "surname")

        val request = MockMvcRequestBuilders
                .put("/customers/1234")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validUpdateRequestBody.toString())

        mockMvc.perform(request)
                .andExpect(status().isNotFound)

    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `it should delete a customer when it exists`() {
        val testCustomer = Customer(CustomerID("anyId"), "testCustomer", "anySurname",
                User(UserID(""), "", "", emptyList()))

        whenever(customerService.deleteCustomer(testCustomer.customerId)).thenReturn(Either.right(testCustomer))

        val request = MockMvcRequestBuilders
                .delete("/customers/${testCustomer.customerId.id}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
                .andExpect(status().isNoContent)
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `it should return 404 when deleting a customer that does not exist`() {
        whenever(customerService.deleteCustomer(any())).thenReturn(Either.left(CustomerNotFoundException("")))

        val request = MockMvcRequestBuilders
                .delete("/customers/1234")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
                .andExpect(status().isNotFound)
    }

    val creatingUser = User(UserID("anyUserId"), "testUser", "user", listOf(Role.ROLE_USER))
}