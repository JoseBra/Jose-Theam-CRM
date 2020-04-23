package shop.service

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.stereotype.Service
import shop.config.security.JwtTokenProvider
import shop.repository.UserRepository
import shop.utils.FailedLoginException

@Service
class AuthenticationService {
    @Autowired
    lateinit var userRepository: UserRepository

    @Autowired
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Autowired
    lateinit var authenticationManager: AuthenticationManager

    fun login(username: String, password: String): String {
        try {
            authenticationManager.authenticate(
                    UsernamePasswordAuthenticationToken(username, password)
            )
            return jwtTokenProvider.createToken(username, userRepository.findByUsername(username)!!.roles)
        } catch (e: BadCredentialsException) {
            throw FailedLoginException("Invalid username/password provided.")
        }
    }
}