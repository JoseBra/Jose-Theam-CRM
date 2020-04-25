package shop.controller

import com.nhaarman.mockitokotlin2.whenever
import org.json.JSONObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import shop.model.Role
import shop.model.User
import shop.model.UserID
import shop.repository.UserRepository


@ExtendWith(SpringExtension::class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthenticationControllerTest {

    @MockBean
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setUp() {
    }

    @Test
    fun `it should generate a JWT token for a user that exists`() {
        val testUser = User(
                UserID("anyId"),
                "testUser",
                passwordEncoder.encode("testPassword"),
                listOf(Role.ROLE_USER))

        whenever(userRepository.findByUsernameAndIsActiveTrue(testUser.username))
                .thenReturn(testUser)

        val validLoginRequest: JSONObject = JSONObject()
                .put("username", testUser.username)
                .put("password", "testPassword")

        val request = MockMvcRequestBuilders
                .post("/authentication/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(validLoginRequest.toString())

        mockMvc.perform(request)
                .andExpect(status().isOk)
                .andExpect(jsonPath("$").isNotEmpty)
    }

    @Test
    fun `it should respond with error message when credentials are wrong`() {
        val invalidLoginRequest: JSONObject = JSONObject()
                .put("username", "nonExistingUser")
                .put("password", "testPassword")

        val request = MockMvcRequestBuilders
                .post("/authentication/login")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidLoginRequest.toString())

        mockMvc.perform(request)
                .andExpect(status().isUnauthorized)
    }
}