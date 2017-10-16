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

package com.gazbert.bxbot.ui.server.repository.remote;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.emailalerts.EmailAlertsConfig;
import com.gazbert.bxbot.ui.server.domain.emailalerts.SmtpConfig;
import com.gazbert.bxbot.ui.server.repository.remote.impl.EmailAlertsConfigRepositoryRestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests the behaviour of the Email Alerts config repository.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@RestClientTest(EmailAlertsConfigRepositoryRestClient.class)
@SpringBootTest(classes = EmailAlertsConfigRepositoryRestClient.class)
public class TestEmailAlertsConfigRepository {

    private static final String REST_ENDPOINT_BASE_URL = "https://localhost.one/api";
    private static final String REST_ENDPOINT_PATH = "/config/email-alerts";

    private static final String BOT_NAME = "GDAX";
    private static final String BOT_STATUS = "Running";
    private static final String BOT_ID = "gdax-bot-1";
    private static final String BOT_BASE_URL = REST_ENDPOINT_BASE_URL;
    private static final String BOT_USERNAME = "bxbot-ui-server-admin";
    private static final String BOT_PASSWORD = "aintGonnaTellYa!";

    private static final boolean ENABLED = true;
    private static final String HOST = "smtp.host.deathstar.com";
    private static final int TLS_PORT = 573;
    private static final String ACCOUNT_USERNAME = "boba@google.com";
    private static final String ACCOUNT_PASSWORD = "bounty";
    private static final String FROM_ADDRESS = "boba.fett@Mandalore.com";
    private static final String TO_ADDRESS = "darth.vader@deathstar.com";

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EmailAlertsConfigRepositoryRestClient restClient;

    private BotConfig botConfig;
    private EmailAlertsConfig someEmailAlertsConfig;


    @Before
    public void setUp() throws Exception {
        botConfig = new BotConfig(BOT_ID, BOT_NAME, BOT_STATUS, BOT_BASE_URL, BOT_USERNAME, BOT_PASSWORD);
        someEmailAlertsConfig = someEmailAlertsConfig();
    }

    @Test
    public void whenGetCalledThenExpectEmailAlertsConfigToBeReturned() throws Exception {

        final String emailAlertsConfigInJson = objectMapper.writeValueAsString(someEmailAlertsConfig);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(emailAlertsConfigInJson, MediaType.APPLICATION_JSON));

        final EmailAlertsConfig emailAlertsConfig = restClient.get(botConfig);
        assertThat(emailAlertsConfig.isEnabled()).isEqualTo(ENABLED);
        assertThat(emailAlertsConfig.getSmtpConfig().getAccountUsername()).isEqualTo(ACCOUNT_USERNAME);
        assertThat(emailAlertsConfig.getSmtpConfig().getAccountPassword()).isEqualTo(ACCOUNT_PASSWORD);
        assertThat(emailAlertsConfig.getSmtpConfig().getHost()).isEqualTo(HOST);
        assertThat(emailAlertsConfig.getSmtpConfig().getTlsPort()).isEqualTo(TLS_PORT);
        assertThat(emailAlertsConfig.getSmtpConfig().getFromAddress()).isEqualTo(FROM_ADDRESS);
        assertThat(emailAlertsConfig.getSmtpConfig().getToAddress()).isEqualTo(TO_ADDRESS);

        mockServer.verify();
    }

    @Test
    public void whenGetCalledAndRemoteCallFailsThenExpectNullEmailAlertsConfigToBeReturned() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        final EmailAlertsConfig emailAlertsConfig = restClient.get(botConfig);
        assertThat(emailAlertsConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledThenExpectRepositoryToSaveItAndReturnSavedEmailAlertsConfig() throws Exception {

        final String emailAlertsConfigInJson = objectMapper.writeValueAsString(someEmailAlertsConfig);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(emailAlertsConfigInJson, MediaType.APPLICATION_JSON));

        final EmailAlertsConfig emailAlertsConfig = restClient.save(botConfig, someEmailAlertsConfig);
        assertThat(emailAlertsConfig.isEnabled()).isEqualTo(ENABLED);
        assertThat(emailAlertsConfig.getSmtpConfig().getAccountUsername()).isEqualTo(ACCOUNT_USERNAME);
        assertThat(emailAlertsConfig.getSmtpConfig().getAccountPassword()).isEqualTo(ACCOUNT_PASSWORD);
        assertThat(emailAlertsConfig.getSmtpConfig().getHost()).isEqualTo(HOST);
        assertThat(emailAlertsConfig.getSmtpConfig().getTlsPort()).isEqualTo(TLS_PORT);
        assertThat(emailAlertsConfig.getSmtpConfig().getFromAddress()).isEqualTo(FROM_ADDRESS);
        assertThat(emailAlertsConfig.getSmtpConfig().getToAddress()).isEqualTo(TO_ADDRESS);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledAndRemoteCallFailsThenExpectNullEmailAlertsConfigToBeReturned() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withServerError());

        final EmailAlertsConfig emailAlertsConfig = restClient.save(botConfig, someEmailAlertsConfig);
        assertThat(emailAlertsConfig).isEqualTo(null);

        mockServer.verify();
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
