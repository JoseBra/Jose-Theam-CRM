package shop.controller

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
        whenever(userService.createUser(any(), any(), any())).thenReturn(testUser)

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


    private final val testUser = User(UserID("testUser"), "testUsername", "testPassword", listOf(Role.ROLE_USER))
    val validCreateUserRequestBody: JSONObject = JSONObject()
            .put("username", testUser.username)
            .put("password", testUser.password)
            .put("roles", JSONArray().apply {
                testUser.roles.forEach { role -> this.put(role) }
            })

}