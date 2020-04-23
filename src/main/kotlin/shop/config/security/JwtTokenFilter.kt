package shop.config.security

import io.jsonwebtoken.JwtException
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class JwtTokenFilter(
        private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(httpServletRequest: HttpServletRequest, httpServletResponse: HttpServletResponse, filterChain: FilterChain) {
        val token: String? = jwtTokenProvider.resolveToken(httpServletRequest)
        try {
            token?.let {
                if (jwtTokenProvider.validateToken(token)) {
                    val auth: Authentication = jwtTokenProvider.getAuthentication(token)
                    SecurityContextHolder.getContext().authentication = auth
                }
            }
        } catch (ex: JwtException) {
            SecurityContextHolder.clearContext()
            httpServletResponse.sendError(HttpStatus.UNAUTHORIZED.value(), ex.message)
            return
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse)
    }
}