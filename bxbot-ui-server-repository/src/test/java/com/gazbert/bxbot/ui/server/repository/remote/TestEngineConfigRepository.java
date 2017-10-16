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
import com.gazbert.bxbot.ui.server.domain.engine.EngineConfig;
import com.gazbert.bxbot.ui.server.repository.remote.impl.EngineConfigRepositoryRestClient;
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

import java.math.BigDecimal;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests the behaviour of the Engine config repository.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@RestClientTest(EngineConfigRepositoryRestClient.class)
@SpringBootTest(classes = EngineConfigRepositoryRestClient.class)
public class TestEngineConfigRepository {

    private static final String REST_ENDPOINT_BASE_URL = "https://localhost.one/api";
    private static final String REST_ENDPOINT_PATH = "/config/engine";

    private static final String BOT_NAME = "GDAX";
    private static final String BOT_STATUS = "Running";
    private static final String BOT_ID = "gdax-bot-1";
    private static final String BOT_BASE_URL = REST_ENDPOINT_BASE_URL;
    private static final String BOT_USERNAME = "bxbot-ui-server-admin";
    private static final String BOT_PASSWORD = "aintGonnaTellYa!";

    private static final String ENGINE_EMERGENCY_STOP_CURRENCY = "BTC";
    private static final BigDecimal ENGINE_EMERGENCY_STOP_BALANCE = new BigDecimal("0.9232320");
    private static final int ENGINE_TRADE_CYCLE_INTERVAL = 60;

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EngineConfigRepositoryRestClient restClient;

    private BotConfig botConfig;
    private EngineConfig someEngineConfig;


    @Before
    public void setUp() throws Exception {
        botConfig = new BotConfig(BOT_ID, BOT_NAME, BOT_STATUS, BOT_BASE_URL, BOT_USERNAME, BOT_PASSWORD);
        someEngineConfig = someEngineConfig();
    }

    @Test
    public void whenGetCalledThenExpectEngineConfigToBeReturned() throws Exception {

        final String engineConfigInJson = objectMapper.writeValueAsString(someEngineConfig);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(engineConfigInJson, MediaType.APPLICATION_JSON));

        final EngineConfig engineConfig = restClient.get(botConfig);
        assertThat(engineConfig.getBotId()).isEqualTo(BOT_ID);
        assertThat(engineConfig.getBotName()).isEqualTo(BOT_NAME);
        assertThat(engineConfig.getTradeCycleInterval()).isEqualTo(ENGINE_TRADE_CYCLE_INTERVAL);
        assertThat(engineConfig.getEmergencyStopCurrency()).isEqualTo(ENGINE_EMERGENCY_STOP_CURRENCY);
        assertThat(engineConfig.getEmergencyStopBalance()).isEqualTo(ENGINE_EMERGENCY_STOP_BALANCE);

        mockServer.verify();
    }

    @Test
    public void whenGetCalledAndRemoteCallFailsThenExpectNullEngineConfigToBeReturned() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        final EngineConfig engineConfig = restClient.get(botConfig);
        assertThat(engineConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledThenExpectRepositoryToSaveItAndReturnSavedEngineConfig() throws Exception {

        final String engineConfigInJson = objectMapper.writeValueAsString(someEngineConfig);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(engineConfigInJson, MediaType.APPLICATION_JSON));

        final EngineConfig engineConfig = restClient.save(botConfig, someEngineConfig);
        assertThat(engineConfig.getBotId()).isEqualTo(BOT_ID);
        assertThat(engineConfig.getBotName()).isEqualTo(BOT_NAME);
        assertThat(engineConfig.getTradeCycleInterval()).isEqualTo(ENGINE_TRADE_CYCLE_INTERVAL);
        assertThat(engineConfig.getEmergencyStopCurrency()).isEqualTo(ENGINE_EMERGENCY_STOP_CURRENCY);
        assertThat(engineConfig.getEmergencyStopBalance()).isEqualTo(ENGINE_EMERGENCY_STOP_BALANCE);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledAndRemoteCallFailsThenExpectNullEngineConfigToBeReturned() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withServerError());

        final EngineConfig engineConfig = restClient.save(botConfig, someEngineConfig);
        assertThat(engineConfig).isEqualTo(null);

        mockServer.verify();
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    private static EngineConfig someEngineConfig() {
        final EngineConfig engineConfig = new EngineConfig();
        engineConfig.setBotId(BOT_ID);
        engineConfig.setBotName(BOT_NAME);
        engineConfig.setEmergencyStopCurrency(ENGINE_EMERGENCY_STOP_CURRENCY);
        engineConfig.setEmergencyStopBalance(ENGINE_EMERGENCY_STOP_BALANCE);
        engineConfig.setTradeCycleInterval(ENGINE_TRADE_CYCLE_INTERVAL);
        return engineConfig;
    }
}
