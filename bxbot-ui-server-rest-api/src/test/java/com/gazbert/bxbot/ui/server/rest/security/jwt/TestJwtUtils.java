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
import org.assertj.core.util.DateUtil;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.within;
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
    private static final String AUTHORITY = "USER";
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

    @Test
    public void testAudienceCanBeExtractedFromToken() throws Exception {
        final String token = createToken();
        assertThat(jwtTokenUtils.getAudienceFromToken(token)).isEqualTo(JwtTokenUtils.AUDIENCE_WEB);
    }

    @Test
    public void testCreatedDateCanBeExtractedFromToken() throws Exception {
        final Date now = DateUtil.now();
        final String token = createToken();
        assertThat(jwtTokenUtils.getCreatedDateFromToken(token)).isCloseTo(now, 1000);
    }

    @Test
    public void testExpirationDateCanBeExtractedFromToken() throws Exception {
        final Date now = DateUtil.now();
        final String token = createToken();
        final Date expirationDate = jwtTokenUtils.getExpirationDateFromToken(token);
        assertThat(DateUtil.timeDifference(expirationDate, now)).isCloseTo(EXPIRATION_PERIOD * 1000, within(1000L));
    }

    @Test
    public void testRolesCanBeExtractedFromToken() throws Exception {
        final String token = createToken();
        final List<GrantedAuthority> roles = jwtTokenUtils.getRolesFromToken(token);
        assertThat(roles.size()).isEqualTo(1);
        assertThat(roles.get(0).getAuthority()).isEqualTo(AUTHORITY);
    }

    @Ignore("FIXME!")
    @Test
    public void testLastPasswordResetDateCanBeExtractedFromToken() throws Exception {

        final Date now = DateUtil.now();
        final String token = createToken();
        jwtTokenUtils.getLastPasswordResetDateFromToken(token);

        final Date lastPasswordResetDate = jwtTokenUtils.getLastPasswordResetDateFromToken(token);
        assertThat(DateUtil.timeDifference(lastPasswordResetDate, now)).isCloseTo(3600000L, within(1000L));
    }

    // ------------------------------------------------------------------------
    // Util methods
    // ------------------------------------------------------------------------

    private String createToken() {
        final DeviceStub device = new DeviceStub();
        device.setNormal(true);
        return jwtTokenUtils.generateToken(new UserDetailsStub(USERNAME, AUTHORITY), device);
    }
}

