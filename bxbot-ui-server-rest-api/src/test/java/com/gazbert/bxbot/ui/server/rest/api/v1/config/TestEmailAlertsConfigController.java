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

package com.gazbert.bxbot.ui.server.rest.api.v1.config;

import com.gazbert.bxbot.ui.server.domain.emailalerts.EmailAlertsConfig;
import com.gazbert.bxbot.ui.server.domain.emailalerts.SmtpConfig;
import com.gazbert.bxbot.ui.server.services.EmailAlertsConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the Email Alerts config controller behaviour.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestEmailAlertsConfigController extends AbstractConfigControllerTest {

    private static final String EMAIL_ALERTS_CONFIG_ENDPOINT_URI = "/api/v1/config/email-alerts/";

    private static final String BOT_ID_PARAM = "botId";
    private static final String BOT_ID = "bitstamp-bot-1";
    private static final String UNKNOWN_BOT_ID = "unknown-bot-id";

    private static final boolean ENABLED = true;
    private static final String HOST = "smtp.host.deathstar.com";
    private static final int TLS_PORT = 573;
    private static final String ACCOUNT_USERNAME = "boba@google.com";
    private static final String ACCOUNT_PASSWORD = "bounty";
    private static final String FROM_ADDRESS = "boba.fett@Mandalore.com";
    private static final String TO_ADDRESS = "darth.vader@deathstar.com";

    @MockBean
    EmailAlertsConfigService emailAlertsConfigService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void whenGetEmailAlertsConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(emailAlertsConfigService.getEmailAlertsConfig(BOT_ID)).willReturn((someEmailAlertsConfig()));
        mockMvc.perform(get(EMAIL_ALERTS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.enabled").value(ENABLED))
                .andExpect(jsonPath("$.data.smtpConfig.host").value(HOST))
                .andExpect(jsonPath("$.data.smtpConfig.tlsPort").value(TLS_PORT))
                .andExpect(jsonPath("$.data.smtpConfig.accountUsername").value(ACCOUNT_USERNAME))
                .andExpect(jsonPath("$.data.smtpConfig.accountPassword").value(ACCOUNT_PASSWORD))
                .andExpect(jsonPath("$.data.smtpConfig.fromAddress").value(FROM_ADDRESS))
                .andExpect(jsonPath("$.data.smtpConfig.toAddress").value(TO_ADDRESS));

        verify(emailAlertsConfigService, times(1)).getEmailAlertsConfig(BOT_ID);
    }

    @Test
    public void whenGetEmailAlertsConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(emailAlertsConfigService.getEmailAlertsConfig(UNKNOWN_BOT_ID)).willReturn(null); // none found!

        mockMvc.perform(get(EMAIL_ALERTS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(emailAlertsConfigService, times(1)).getEmailAlertsConfig(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetEmailAlertsConfigCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get(EMAIL_ALERTS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + BOT_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetEmailAlertsConfigCalledWithoutBotIdThenExpectBadRequestResponse() throws Exception {
        mockMvc.perform(get(EMAIL_ALERTS_CONFIG_ENDPOINT_URI)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenUpdateEmailAlertsConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        final EmailAlertsConfig updatedConfig = someEmailAlertsConfig();
        given(emailAlertsConfigService.updateEmailAlertsConfig(eq(BOT_ID), any())).willReturn(updatedConfig);

        mockMvc.perform(put(EMAIL_ALERTS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(updatedConfig)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.enabled").value(ENABLED))
                .andExpect(jsonPath("$.data.smtpConfig.host").value(HOST))
                .andExpect(jsonPath("$.data.smtpConfig.tlsPort").value(TLS_PORT))
                .andExpect(jsonPath("$.data.smtpConfig.accountUsername").value(ACCOUNT_USERNAME))
                .andExpect(jsonPath("$.data.smtpConfig.accountPassword").value(ACCOUNT_PASSWORD))
                .andExpect(jsonPath("$.data.smtpConfig.fromAddress").value(FROM_ADDRESS))
                .andExpect(jsonPath("$.data.smtpConfig.toAddress").value(TO_ADDRESS));

        verify(emailAlertsConfigService, times(1)).updateEmailAlertsConfig(eq(BOT_ID), any());
    }

    @Test
    public void whenUpdateEmailAlertsConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        final EmailAlertsConfig updatedConfig = someEmailAlertsConfig();
        given(emailAlertsConfigService.updateEmailAlertsConfig(UNKNOWN_BOT_ID, updatedConfig)).willReturn(null);

        mockMvc.perform(put(EMAIL_ALERTS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(updatedConfig)))
                .andExpect(status().isNotFound());

        verify(emailAlertsConfigService, times(1)).updateEmailAlertsConfig(eq(UNKNOWN_BOT_ID), any());
    }

    @Test
    public void whenUpdateEmailAlertsConfigCalledForKnownBotIdAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(put(EMAIL_ALERTS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someEmailAlertsConfig())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateEmailAlertsConfigCalledAndUserIsNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(put(EMAIL_ALERTS_CONFIG_ENDPOINT_URI + "/?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someEmailAlertsConfig())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenUpdateEmailAlertsConfigCalledWithMissingBotIdThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(put(EMAIL_ALERTS_CONFIG_ENDPOINT_URI)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someEmailAlertsConfig())))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    private static EmailAlertsConfig someEmailAlertsConfig() {
        final EmailAlertsConfig emailAlertsConfig = new EmailAlertsConfig();
        final SmtpConfig smtpConfig = new SmtpConfig(HOST, TLS_PORT, ACCOUNT_USERNAME, ACCOUNT_PASSWORD, FROM_ADDRESS, TO_ADDRESS);
        emailAlertsConfig.setSmtpConfig(smtpConfig);
        emailAlertsConfig.setEnabled(ENABLED);
        return emailAlertsConfig;
    }
}

