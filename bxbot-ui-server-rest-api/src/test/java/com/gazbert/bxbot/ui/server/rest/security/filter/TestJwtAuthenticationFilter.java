/*
 * The MIT License (MIT)
 *
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

import com.gazbert.bxbot.ui.server.rest.security.jwt.JwtTokenUtils;
import io.jsonwebtoken.Claims;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

/**
 * Tests the behaviour of the JWT Authentication filter is as expected.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestJwtAuthenticationFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USERNAME = "bobafett";

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private HttpServletResponse response;

    @MockBean
    private FilterChain filterChain;

    @MockBean
    private JwtTokenUtils jwtTokenUtils;

    @MockBean
    private Claims claims;

    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @Before
    public void setup() throws Exception {
        jwtAuthenticationFilter = new JwtAuthenticationFilter();
        jwtAuthenticationFilter.setJwtTokenUtils(jwtTokenUtils);
    }

    @Test
    public void whenFilterCalledWithoutAuthorizationHeaderThenCallNextFilterInChain() throws Exception {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader(AUTHORIZATION_HEADER);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void whenFilterCalledWithBearerTokenWithMissingUsernameThenCallNextFilterInChain() throws Exception {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + "dummy-token");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader(AUTHORIZATION_HEADER);
        verify(jwtTokenUtils, times(1)).getUsernameFromTokenClaims(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }


    @Test
    public void whenFilterCalledWithTokenWithMissingUsernameThenCallNextFilterInChain() throws Exception {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("dummy-token");
        when(jwtTokenUtils.getUsernameFromTokenClaims((any()))).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader(AUTHORIZATION_HEADER);
        verify(jwtTokenUtils, times(1)).getUsernameFromTokenClaims(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void whenFilterCalledWithInvalidTokenThenCallNextFilterInChain() throws Exception {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("dummy-token");
        when(jwtTokenUtils.getUsernameFromTokenClaims((any()))).thenReturn(USERNAME);
        when(jwtTokenUtils.validateTokenAndGetClaims((any()))).thenReturn(claims);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader(AUTHORIZATION_HEADER);
        verify(jwtTokenUtils, times(1)).getUsernameFromTokenClaims(any());
        verify(jwtTokenUtils, times(1)).validateTokenAndGetClaims(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void whenFilterCalledWithValidTokenThenExpectSuccessfulAuthenticationAndCallNextFilterInChain() throws Exception {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("dummy-token");
        when(jwtTokenUtils.getUsernameFromTokenClaims((any()))).thenReturn(USERNAME);
        when(jwtTokenUtils.validateTokenAndGetClaims((any()))).thenReturn(claims);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader(AUTHORIZATION_HEADER);
        verify(jwtTokenUtils, times(1)).getUsernameFromTokenClaims(any());
        verify(jwtTokenUtils, times(1)).validateTokenAndGetClaims(any());
        verify(jwtTokenUtils, times(1)).getRolesFromTokenClaims(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}
