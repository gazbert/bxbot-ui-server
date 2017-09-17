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

package com.gazbert.bxbot.ui.server.rest.security.jwt;

import com.gazbert.bxbot.ui.server.rest.security.jwt.stubs.DeviceStub;
import com.gazbert.bxbot.ui.server.rest.security.jwt.stubs.UserDetailsStub;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Java6Assertions.assertThat;

/**
 * Tests the JWT utils.
 * <p>
 * Code originated from the excellent JWT and Spring Boot example by Stephan Zerhusen:
 * https://github.com/szerhusenBC/jwt-spring-security-demo
 *
 * @author gazbert
 */
public class TestJwtUtils {

    private static final String USERNAME = "boba.fett";
    private static final String SECRET_KEY = "mkultra";
    private static final long EXPIRATION_PERIOD = 3600L;

    @InjectMocks
    private JwtTokenUtils jwtTokenUtils;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(jwtTokenUtils, "expiration", EXPIRATION_PERIOD);
        ReflectionTestUtils.setField(jwtTokenUtils, "secret", SECRET_KEY);
    }

    @Test
    public void testUsernameCanBeExtractedFromToken() throws Exception {
        final String token = createToken();
        assertThat(jwtTokenUtils.getUsernameFromToken(token)).isEqualTo(USERNAME);
    }

    // ------------------------------------------------------------------------
    // Util methods
    // ------------------------------------------------------------------------

    private String createToken() {
        final DeviceStub device = new DeviceStub();
        device.setNormal(true);
        return jwtTokenUtils.generateToken(new UserDetailsStub(USERNAME), device);
    }
}

