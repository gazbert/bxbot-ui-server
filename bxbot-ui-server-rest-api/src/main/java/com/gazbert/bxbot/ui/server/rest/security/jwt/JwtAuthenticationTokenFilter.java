package com.gazbert.bxbot.ui.server.rest.security.jwt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * JWT Authentication filter validates the token passed in from the client.
 * <p>
 * Code originated from the excellent JWT and Spring Boot example by Stephan Zerhusen:
 * https://github.com/szerhusenBC/jwt-spring-security-demo
 */
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    private static final Logger LOG = LogManager.getLogger();

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Value("${jwt.header}")
    private String tokenHeader;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Extract token after Bearer prefix if present
        String authToken = request.getHeader(this.tokenHeader);
        if (authToken != null && authToken.startsWith("Bearer ")) {
            authToken = authToken.substring(7);
        }

        final String username = jwtTokenUtil.getUsernameFromToken(authToken);
        LOG.info(() -> "About to authenticate User: " + username);

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // It is not compulsory to load the User details from the database. You could also store the information
            // in the token and read it from it. It's up to you ;)
            final UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // For simple validation it is completely sufficient to just check the token integrity.
            // You don't have to call the database for an additional User lookup/check for every request. We do here.
            // Again it's up to you ;)
            if (jwtTokenUtil.validateToken(authToken, userDetails)) {
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
                LOG.info(() -> "Authenticated User: " + username + " has been set in SecurityContext");
            }
        }
        chain.doFilter(request, response);
    }
}