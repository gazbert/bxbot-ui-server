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

package com.gazbert.bxbot.ui.server.rest.security.service;

import com.gazbert.bxbot.ui.server.rest.security.jwt.JwtUser;
import com.gazbert.bxbot.ui.server.rest.security.model.Role;
import com.gazbert.bxbot.ui.server.rest.security.model.RoleName;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import com.gazbert.bxbot.ui.server.rest.security.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Tests the behaviour of the JWT User Details Service is as expected.
 * <p>
 * Normally, I'd mock out the JwtUserFactory, but Mockito does not allow final util classes to be mocked - I could
 * use PowerMock, but it's simple enough to just mock the User and use the real JwtUserFactory.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestJwtUserDetailsService {

    private static final Long ADMIN_ROLE_ID = new Long("213443242342");
    private static final Long USER_ROLE_ID = new Long("21344565442342");

    private static final String KNOWN_USERNAME = "known-username";
    private static final String UNKNOWN_USERNAME = "unknown-username";

    private static final Long USER_ID = new Long("2323267789789");
    private static final String USERNAME = "hansolo";
    private static final String PASSWORD = "password";
    private static final String FIRSTNAME = "Han";
    private static final String LASTNAME = "Solo";
    private static final String EMAIL = "han@falcon";
    private static final boolean USER_ENABLED = true;
    private static final Date LAST_PASSWORD_RESET_DATE = new Date();

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private User user;

    @Test
    public void whenLoadByUsernameCalledWithKnownUsernameThenExpectUserDetailsToBeReturned() throws Exception {

        when(userRepository.findByUsername(KNOWN_USERNAME)).thenReturn(user);
        when(user.getId()).thenReturn(USER_ID);
        when(user.getUsername()).thenReturn(USERNAME);
        when(user.getPassword()).thenReturn(PASSWORD);
        when(user.getFirstname()).thenReturn(FIRSTNAME);
        when(user.getLastname()).thenReturn(LASTNAME);
        when(user.getEmail()).thenReturn(EMAIL);
        when(user.getEnabled()).thenReturn(USER_ENABLED);
        when(user.getRoles()).thenReturn(createRoles());
        when(user.getLastPasswordResetDate()).thenReturn(LAST_PASSWORD_RESET_DATE);

        final JwtUserDetailsService jwtUserDetailsService = new JwtUserDetailsService(userRepository);
        final JwtUser userDetails = (JwtUser) jwtUserDetailsService.loadUserByUsername(KNOWN_USERNAME);

        assertEquals(userDetails.getId(), USER_ID);
        assertEquals(userDetails.getUsername(), USERNAME);
        assertEquals(userDetails.getPassword(), PASSWORD);
        assertEquals(userDetails.getFirstname(), FIRSTNAME);
        assertEquals(userDetails.getLastname(), LASTNAME);
        assertEquals(userDetails.getEmail(), EMAIL);
        assertEquals(userDetails.isEnabled(), USER_ENABLED);
        assertEquals(userDetails.getLastPasswordResetDate(), LAST_PASSWORD_RESET_DATE.getTime());

        assertTrue(userDetails.getRoles().contains(RoleName.ROLE_ADMIN.name()));
        assertTrue(userDetails.getRoles().contains(RoleName.ROLE_USER.name()));

        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_ADMIN.name())));
        assertTrue(userDetails.getAuthorities().contains(new SimpleGrantedAuthority(RoleName.ROLE_USER.name())));
    }

    @Test(expected = UsernameNotFoundException.class)
    public void whenLoadByUsernameCalledWithUnknownUsernameThenExpectUsernameNotFoundException() throws Exception {

        when(userRepository.findByUsername(UNKNOWN_USERNAME)).thenReturn(null);

        final JwtUserDetailsService jwtUserDetailsService = new JwtUserDetailsService(userRepository);
        jwtUserDetailsService.loadUserByUsername(KNOWN_USERNAME);
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    private List<Role> createRoles() {

        final List<User> users = Collections.singletonList(user);

        final Role role1 = new Role();
        role1.setId(ADMIN_ROLE_ID);
        role1.setName(RoleName.ROLE_ADMIN);
        role1.setUsers(users);

        final Role role2 = new Role();
        role2.setId(USER_ROLE_ID);
        role2.setName(RoleName.ROLE_USER);
        role2.setUsers(users);

        final List<Role> roles = new ArrayList<>();
        roles.add(role1);
        roles.add(role2);
        return roles;
    }
}
