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

import com.gazbert.bxbot.ui.server.rest.security.model.Role;
import com.gazbert.bxbot.ui.server.rest.security.model.RoleName;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import org.assertj.core.util.DateUtil;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Tests the JWT utils.
 * <p>
 * Code originated from the excellent JWT and Spring Boot example by Stephan Zerhusen:
 * https://github.com/szerhusenBC/jwt-spring-security-demo
 *
 * @author gazbert
 */
public class TestJwtUtils {

    private static final String SECRET_KEY = "mkultra";
    private static final long EXPIRATION_PERIOD = 3600L;
    private static final long ALLOWED_CLOCK_SKEW_IN_SECS = 5 * 60 + 1000; // 5 mins

    private static final Long USER_ROLE_ID = new Long("21344565442342");

    private static final Long USER_ID = new Long("2323267789789");
    private static final String USERNAME = "hansolo";
    private static final String PASSWORD = "password";
    private static final String FIRSTNAME = "Han";
    private static final String LASTNAME = "Solo";
    private static final String EMAIL = "han@falcon";
    private static final boolean USER_ENABLED = true;
    private static final Date LAST_PASSWORD_RESET_DATE = new Date();

    @InjectMocks
    private JwtTokenUtils jwtTokenUtils;


    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        ReflectionTestUtils.setField(jwtTokenUtils, "expiration", EXPIRATION_PERIOD);
        ReflectionTestUtils.setField(jwtTokenUtils, "secret", SECRET_KEY);
        ReflectionTestUtils.setField(jwtTokenUtils, "allowedClockSkewInSecs", ALLOWED_CLOCK_SKEW_IN_SECS);
    }

    @Test
    public void testUsernameCanBeExtractedFromToken() throws Exception {
        final String token = createToken();
        assertThat(jwtTokenUtils.getUsernameFromToken(token)).isEqualTo(USERNAME);
    }

    @Test
    public void testAudienceCanBeExtractedFromToken() throws Exception {
        final String token = createToken();
        assertThat(jwtTokenUtils.getAudienceFromToken(token)).isEqualTo(JwtTokenUtils.AUDIENCE_BXBOT_UI);
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
        assertThat(roles.get(0).getAuthority()).isEqualTo(RoleName.ROLE_USER.name());
    }

    @Test
    public void testLastPasswordResetDateCanBeExtractedFromToken() throws Exception {
        final String token = createToken();
        assertThat(jwtTokenUtils.getLastPasswordResetDateFromToken(token)).isCloseTo(LAST_PASSWORD_RESET_DATE, 1000);
    }

    @Test
    public void whenValidateTokenCalledWithNonExpiredTokenThenExpectSuccess() throws Exception {
        final String token = createToken();
        assertThat(jwtTokenUtils.validateToken(token)).isTrue();
    }

    @Test
    public void whenValidateTokenCalledWithExpiredTokenThenExpectFailure() throws Exception {
        ReflectionTestUtils.setField(jwtTokenUtils, "allowedClockSkewInSecs", 0L);
        ReflectionTestUtils.setField(jwtTokenUtils, "expiration", 0L); // will expire fast!
        final String token = createToken();
        assertThat(jwtTokenUtils.validateToken(token)).isFalse();
    }

    @Test
    public void whenValidateTokenCalledWithCreatedDateEarlierThanLastPasswordResetDateThenExpectFailure() throws Exception {
        final String token = createTokenWithInvalidCreationDate();
        assertThat(jwtTokenUtils.validateToken(token)).isFalse();
    }

    // ------------------------------------------------------------------------
    // Util methods
    // ------------------------------------------------------------------------

    private String createToken() {
        return jwtTokenUtils.generateToken(createUserDetails(LAST_PASSWORD_RESET_DATE));
    }

    private String createTokenWithInvalidCreationDate() {
        return jwtTokenUtils.generateToken(createUserDetails(DateUtil.tomorrow()));
    }

    private JwtUser createUserDetails(Date lastPasswordResetDate) {

        final User user = new User();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setFirstname(FIRSTNAME);
        user.setLastname(LASTNAME);
        user.setEmail(EMAIL);
        user.setEnabled(USER_ENABLED);
        user.setLastPasswordResetDate(lastPasswordResetDate);

        final List<Role> roles = createRoles(user);
        user.setRoles(roles);
        assertEquals(roles, user.getRoles());

        return JwtUserFactory.create(user);
    }

    private List<Role> createRoles(User user) {

        final List<User> users = Collections.singletonList(user);

        final Role role1 = new Role();
        role1.setId(USER_ROLE_ID);
        role1.setName(RoleName.ROLE_USER);
        role1.setUsers(users);

        final List<Role> roles = new ArrayList<>();
        roles.add(role1);
        return roles;
    }
}

