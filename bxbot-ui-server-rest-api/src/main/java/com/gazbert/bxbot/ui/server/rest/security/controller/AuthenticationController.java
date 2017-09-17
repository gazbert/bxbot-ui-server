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

package com.gazbert.bxbot.ui.server.rest.security.controller;

import com.gazbert.bxbot.ui.server.rest.security.jwt.JwtAuthenticationRequest;
import com.gazbert.bxbot.ui.server.rest.security.jwt.JwtTokenUtils;
import com.gazbert.bxbot.ui.server.rest.security.jwt.JwtUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * REST Controller for handling Authentication requests.
 * <p>
 * Code originated from the excellent JWT and Spring Boot example by Stephan Zerhusen:
 * https://github.com/szerhusenBC/jwt-spring-security-demo
 *
 * @author gazbert
 */
@RestController
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenUtils jwtTokenUtils;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserDetailsService userDetailsService, JwtTokenUtils jwtTokenUtils) {

        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    /**
     * Clients initially call this with their username/password in order to receive a JWT for use in future requests.
     *
     * @param authenticationRequest the authentication request containing the client's username/password.
     * @param device the device the user is making the request from.
     * @return the JWT if the client was authenticated.
     * @throws AuthenticationException if the the client was not authenticated successfully.
     */
    @RequestMapping(value = "/auth", method = RequestMethod.POST)
    public ResponseEntity<?> getAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
                                                    Device device) throws AuthenticationException {

        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequest.getUsername(),
                        authenticationRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Reload password post-security check, so we can generate the token...
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtils.generateToken(userDetails, device);

        return ResponseEntity.ok(new JwtAuthenticationResponse(token));
    }

    /**
     * Clients should call this in order to refresh a JWT.
     *
     * @param request the request from the client.
     * @return the JWT with an extended expiry time if the client was authenticated, a 400 Bad Request otherwise.
     */
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAuthenticationToken(HttpServletRequest request) {

        final String authorizationHeader = request.getHeader("Authorization");
        final String username = jwtTokenUtils.getUsernameFromToken(authorizationHeader);
        final JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtils.canTokenBeRefreshed(authorizationHeader, new Date(user.getLastPasswordResetDate()))) {
            final String refreshedToken = jwtTokenUtils.refreshToken(authorizationHeader);
            return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
