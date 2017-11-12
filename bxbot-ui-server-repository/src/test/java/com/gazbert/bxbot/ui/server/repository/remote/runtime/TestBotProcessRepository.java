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

package com.gazbert.bxbot.ui.server.repository.remote.runtime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.bot.BotStatus;
import com.gazbert.bxbot.ui.server.repository.remote.runtime.impl.BotStatusRepositoryRestClient;
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
 * Tests the behaviour of the Bot status repository.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@RestClientTest(BotStatusRepositoryRestClient.class)
@SpringBootTest(classes = BotStatusRepositoryRestClient.class)
public class TestBotProcessRepository {

    private static final String REST_ENDPOINT_BASE_URL = "https://localhost.one/api";
    private static final String STATUS_RESOURCE_PATH = "/runtime/status";

    private static final String BOT_ALIAS = "GDAX";
    private static final String BOT_ID = "gdax-bot-1";
    private static final String BOT_BASE_URL = REST_ENDPOINT_BASE_URL;
    private static final String BOT_USERNAME = "bxbot-ui-server-admin";
    private static final String BOT_PASSWORD = "aintGonnaTellYa!";

    private static final String BOT_NAME = "GDAX";
    private static final String BOT_STATUS = "running";

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BotStatusRepositoryRestClient restClient;

    private BotConfig botConfig;
    private BotStatus botStatus;


    @Before
    public void setUp() throws Exception {
        botConfig = new BotConfig(BOT_ID, BOT_ALIAS, BOT_BASE_URL, BOT_USERNAME, BOT_PASSWORD);
        botStatus = someBotStatus();
    }

    @Test
    public void whenGetStatusCalledThenExpectBotStatusToBeReturned() throws Exception {

        final String engineConfigInJson = objectMapper.writeValueAsString(botStatus);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + STATUS_RESOURCE_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(engineConfigInJson, MediaType.APPLICATION_JSON));

        final BotStatus botStatus = restClient.getBotStatus(botConfig);
        assertThat(botStatus.getId()).isEqualTo(BOT_ID);
        assertThat(botStatus.getName()).isEqualTo(BOT_NAME);
        assertThat(botStatus.getStatus()).isEqualTo(BOT_STATUS);

        mockServer.verify();
    }

    @Test
    public void whenGetStatusCalledAndRemoteCallFailsThenExpectNullBotStatusToBeReturned() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + STATUS_RESOURCE_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        final BotStatus botStatus = restClient.getBotStatus(botConfig);
        assertThat(botStatus).isEqualTo(null);

        mockServer.verify();
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    private static BotStatus someBotStatus() {
        final BotStatus botStatus = new BotStatus();
        botStatus.setId(BOT_ID);
        botStatus.setName(BOT_NAME);
        botStatus.setStatus(BOT_STATUS);
        return botStatus;
    }
}
