package com.vivekk.authservice.config;

import com.vivekk.authservice.exception.UnAuthorizeRequestException;
import com.vivekk.authservice.service.LoginService;
import com.vivekk.authservice.utils.Constants;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private LoginService loginService;

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return EXCLUDED_PATHS.stream()
                .anyMatch(path -> new AntPathMatcher().match(path, request.getServletPath()));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String accessToken = request.getHeader(Constants.Login.ACCESS_TOKEN);

        if (StringUtils.isNotBlank(accessToken)) {
            try {
                Claims claims = loginService.parseAndVerifyToken(accessToken);
                if (claims.getExpiration().before(new Date())) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value(), "Token expired");
                    return;
                }
            } catch (UnAuthorizeRequestException e) {
                response.sendError(HttpStatus.UNAUTHORIZED.value(), "Invalid token");
                return;
            } catch (Exception e) {
                response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Token validation failed");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}