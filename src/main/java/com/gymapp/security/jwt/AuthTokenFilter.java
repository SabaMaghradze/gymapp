package com.gymapp.security.jwt;

import com.gymapp.security.user.UserDetailsCustomService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


// intercepts every HTTP request to validate the JWT and authenticate the user.

// OncePerRequestFilter - A Spring Security filter that ensures each request is filtered only once
// per request cycle.
public class AuthTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserDetailsCustomService userDetailsCustomService;

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

//    Intercepts every request and processes authentication logic.
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {

            String jwt = parseJwt(request); // extract the JWT from the request header.

            if (jwt != null && jwtUtil.isTokenValid(jwt)) {

                String email = jwtUtil.getUserNameFromToken(jwt);
                UserDetails userDetails = userDetailsCustomService.loadUserByUsername(email);

                //  Creates a Spring Security authentication object.
                //  Stores the authentication object in the SecurityContext, making the user
                //  authenticated for this request.
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication : {}", e.getMessage());
        }
        filterChain.doFilter(request, response); // Passes the request to the next filter
    }

//    Extracts JWT from the Authorization header.
//    If it starts with "Bearer ", removes that prefix and returns the token.
    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}
