/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Stephan Zerhusen
 * Copyright (c) 2017 Gareth Jon Lynch
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.gazbert.bxbot.ui.server.rest.security.filter;

import com.gazbert.bxbot.ui.server.rest.security.jwt.JwtUtils;
import io.jsonwebtoken.Claims;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * The JWT Authentication filter extracts the token from the Authorization header and it validates it. If no JWT is
 * present, the next filter in the Spring Security filter chain is invoked.
 * <p>
 * Code originated from the excellent JWT and Spring Boot example by Stephan Zerhusen:
 * https://github.com/szerhusenBC/jwt-spring-security-demo
 *
 * @author gazbert
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger LOG = LogManager.getLogger();
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

    private JwtUtils jwtUtils;
    private UserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Extract token after Bearer prefix if present
        String authorizationHeader = request.getHeader(AUTHORIZATION_HEADER);
        if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
            authorizationHeader = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
        }

        // Might be null if client does not have a token yet
        if (authorizationHeader != null) {

            final Claims claims = jwtUtils.validateTokenAndGetClaims(authorizationHeader);
            final String username = jwtUtils.getUsernameFromTokenClaims(claims);
            LOG.info(() -> "Username in JWT: " + username);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {

                // It is not compulsory to load the User details from the database.
                // We can just use the information in the token claims - this saves a repo lookup.
                //
                // final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                // if (userDetails != null && !(userDetails.getUsername().equals(username))) {
                //    final String errorMsg = "Username is token not found in User repository! Token username: " + username;
                //    throw new JwtAuthenticationException(errorMsg);
                // }

                LOG.info(() -> "JWT is valid");

                // final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                //        userDetails, null, userDetails.getAuthorities());
                final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        username, null, jwtUtils.getRolesFromTokenClaims(claims));

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                LOG.info(() -> "Authenticated User: " + username + " has been set in Spring SecurityContext.");
            }
        }

        chain.doFilter(request, response);
    }

    @Autowired
    public void setJwtUtils(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
}