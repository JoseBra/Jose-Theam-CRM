package shop.config.security

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import shop.model.Role
import shop.utils.JwtExpiredOrInvalidToken
import java.util.*
import javax.servlet.http.HttpServletRequest


@Component
class JwtTokenProvider(
        @Autowired
        var myUserDetails: CustomUserDetails
) {
    @Value("\${security.jwt.token.secret-key:fakeSecret}")
    lateinit var secretKey: String

    val validityInMilliseconds: Long = 3600000

    fun createToken(username: String, roles: List<Role>): String {
        val claims = Jwts.claims().setSubject(username)
        claims["auth"] = roles
                .map { SimpleGrantedAuthority(it.authority) }

        val now = Date()
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(Date(now.time + validityInMilliseconds))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact()
    }

    fun getAuthentication(token: String): Authentication {
        val userDetails: UserDetails = myUserDetails.loadUserByUsername(getUsername(token))
        return UsernamePasswordAuthenticationToken(userDetails, userDetails.password, userDetails.authorities)
    }

    fun getUsername(token: String?): String {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).body.subject
    }

    fun resolveToken(req: HttpServletRequest): String? {
        val bearerToken = req.getHeader("Authorization")
        return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            bearerToken.substring(7)
        } else null
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
            true
        } catch (e: JwtException) {
            throw JwtExpiredOrInvalidToken("Expired or invalid JWT token")
        } catch (e: IllegalArgumentException) {
            throw JwtExpiredOrInvalidToken("Expired or invalid JWT token")
        }
    }
}