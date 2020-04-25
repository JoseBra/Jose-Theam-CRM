package shop.controller

import arrow.core.Either
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.whenever
import org.json.JSONArray
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
import shop.model.Role
import shop.model.User
import shop.model.UserID
import shop.service.UserService
import shop.utils.UserAlreadyExists

@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    @MockBean
    lateinit var userService: UserService

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `it should be able to create a user with correct parameters`() {
        whenever(userService.createUser(any(), any(), any())).thenReturn(Either.right(testUser))

        val request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validCreateUserRequestBody.toString())

        mockMvc.perform(request)
                .andExpect(status().isCreated)
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.username").value(testUser.username))
                .andExpect(jsonPath("$.roles").isArray)
                .andExpect(jsonPath("$.roles[0]").value(testUser.roles.first().toString()))
                .andExpect(jsonPath("$.userId").value(testUser.userId.id))
                .andExpect(jsonPath("$.password").doesNotExist())
    }

    @Test
    @WithMockUser(roles = ["USER"])
    fun `it should answer with 403 error when user is not admin`() {
        val request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validCreateUserRequestBody.toString())

        mockMvc.perform(request)
                .andExpect(status().isForbidden)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `it should return Conflict error code and message when username already in use`() {
        val expectedMessage = "Username alreay in use."
        whenever(userService.createUser(any(), any(), any()))
                .thenReturn(Either.left(UserAlreadyExists(expectedMessage)))

        val request = MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validCreateUserRequestBody.toString())

        mockMvc.perform(request)
                .andExpect(status().isConflict)
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `it should be able to list all active users`() {
        whenever(userService.listAllActiveUsers()).thenReturn(listOf(testUser))

        val request = MockMvcRequestBuilders
                .get("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.items").isArray)
                .andExpect(jsonPath("$.items[0].userId").value(testUser.userId.id))
    }

    @Test
    @WithMockUser(roles = ["ADMIN"])
    fun `it should mark as inactive a user when deleting it`() {
        whenever(userService.markAsInactive(testUser.userId))
                .thenReturn(Either.right(testUser.copy(isActive = false)))

        val request = MockMvcRequestBuilders
                .delete("/users/${testUser.userId.id}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)

        mockMvc.perform(request)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.userId").value(testUser.userId.id))
                .andExpect(jsonPath("$.isActive").value(false))
    }

    private final val testUser = User(UserID("testUser"), "testUsername", "testPassword", listOf(Role.ROLE_USER))
    val validCreateUserRequestBody: JSONObject = JSONObject()
            .put("username", testUser.username)
            .put("password", testUser.password)
            .put("roles", JSONArray().apply {
                testUser.roles.forEach { role -> this.put(role) }
            })
}