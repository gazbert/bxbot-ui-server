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
import com.gazbert.bxbot.ui.server.domain.exchange.ExchangeConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.NetworkConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.OptionalConfig;
import com.gazbert.bxbot.ui.server.repository.remote.impl.ExchangeConfigRepositoryRestClient;
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

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * Tests the behaviour of the Exchange config repository.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@RestClientTest(ExchangeConfigRepositoryRestClient.class)
@SpringBootTest(classes = ExchangeConfigRepositoryRestClient.class)
public class TestExchangeConfigRepository {

    private static final String REST_ENDPOINT_BASE_URL = "https://localhost.one/api";
    private static final String REST_ENDPOINT_PATH = "/config/exchange";

    private static final String BOT_ID = "gdax-bot-1";
    private static final String BOT_NAME = "GDAX";
    private static final String BOT_STATUS = "Running";
    private static final String BOT_BASE_URL = REST_ENDPOINT_BASE_URL;
    private static final String BOT_USERNAME = "bxbot-ui-server-admin";
    private static final String BOT_PASSWORD = "aintGonnaTellYa!";

    private static final String EXCHANGE_NAME = "Bitstamp";
    private static final String EXCHANGE_ADAPTER = "com.gazbert.bxbot.exchanges.TestExchangeAdapter";

    private static final Integer CONNECTION_TIMEOUT = 30;
    private static final List<Integer> NON_FATAL_ERROR_CODES = Arrays.asList(502, 503, 504);
    private static final List<String> NON_FATAL_ERROR_MESSAGES = Arrays.asList(
            "Connection refused", "Connection reset", "Remote host closed connection during handshake");

    private static final String BUY_FEE_CONFIG_ITEM_KEY = "buy-fee";
    private static final String BUY_FEE_CONFIG_ITEM_VALUE = "0.20";
    private static final String SELL_FEE_CONFIG_ITEM_KEY = "sell-fee";
    private static final String SELL_FEE_CONFIG_ITEM_VALUE = "0.25";

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ExchangeConfigRepositoryRestClient restClient;

    private BotConfig botConfig;
    private ExchangeConfig someExchangeConfig;


    @Before
    public void setUp() throws Exception {
        botConfig = new BotConfig(BOT_ID, BOT_NAME, BOT_STATUS, BOT_BASE_URL, BOT_USERNAME, BOT_PASSWORD);
        someExchangeConfig = someExchangeConfig();
    }

    @Test
    public void whenGetCalledThenExpectExchangeConfigToBeReturned() throws Exception {

        final String exchangeConfigInJson = objectMapper.writeValueAsString(someExchangeConfig);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(exchangeConfigInJson, MediaType.APPLICATION_JSON));

        final ExchangeConfig exchangeConfig = restClient.get(botConfig);

        assertThat(exchangeConfig.getExchangeName()).isEqualTo(EXCHANGE_NAME);
        assertThat(exchangeConfig.getExchangeAdapter()).isEqualTo(EXCHANGE_ADAPTER);

        assertThat(exchangeConfig.getNetworkConfig().getConnectionTimeout()).isEqualTo(CONNECTION_TIMEOUT);
        assertThat(exchangeConfig.getNetworkConfig().getNonFatalErrorCodes()).isEqualTo(NON_FATAL_ERROR_CODES);
        assertThat(exchangeConfig.getNetworkConfig().getNonFatalErrorMessages()).isEqualTo(NON_FATAL_ERROR_MESSAGES);
        assertThat(exchangeConfig.getOptionalConfig().getItems().get(BUY_FEE_CONFIG_ITEM_KEY)).isEqualTo(BUY_FEE_CONFIG_ITEM_VALUE);

        mockServer.verify();
    }

    @Test
    public void whenGetCalledAndRemoteCallFailsThenExpectNullExchangeConfigToBeReturned() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        final ExchangeConfig exchangeConfig = restClient.get(botConfig);
        assertThat(exchangeConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledThenExpectRepositoryToSaveItAndReturnSavedExchangeConfig() throws Exception {

        final String exchangeConfigInJson = objectMapper.writeValueAsString(someExchangeConfig);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(exchangeConfigInJson, MediaType.APPLICATION_JSON));

        final ExchangeConfig exchangeConfig = restClient.save(botConfig, someExchangeConfig);

        assertThat(exchangeConfig.getExchangeName()).isEqualTo(EXCHANGE_NAME);
        assertThat(exchangeConfig.getExchangeAdapter()).isEqualTo(EXCHANGE_ADAPTER);

        assertThat(exchangeConfig.getNetworkConfig().getConnectionTimeout()).isEqualTo(CONNECTION_TIMEOUT);
        assertThat(exchangeConfig.getNetworkConfig().getNonFatalErrorCodes()).isEqualTo(NON_FATAL_ERROR_CODES);
        assertThat(exchangeConfig.getNetworkConfig().getNonFatalErrorMessages()).isEqualTo(NON_FATAL_ERROR_MESSAGES);
        assertThat(exchangeConfig.getOptionalConfig().getItems().get(BUY_FEE_CONFIG_ITEM_KEY)).isEqualTo(BUY_FEE_CONFIG_ITEM_VALUE);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledAndRemoteCallFailsThenExpectNullExchangeConfigToBeReturned() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withServerError());

        final ExchangeConfig exchangeConfig = restClient.save(botConfig, someExchangeConfig);
        assertThat(exchangeConfig).isEqualTo(null);

        mockServer.verify();
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    private static ExchangeConfig someExchangeConfig() {

        final NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setConnectionTimeout(CONNECTION_TIMEOUT);
        networkConfig.setNonFatalErrorCodes(NON_FATAL_ERROR_CODES);
        networkConfig.setNonFatalErrorMessages(NON_FATAL_ERROR_MESSAGES);

        final OptionalConfig optionalConfig = new OptionalConfig();
        optionalConfig.getItems().put(BUY_FEE_CONFIG_ITEM_KEY, BUY_FEE_CONFIG_ITEM_VALUE);
        optionalConfig.getItems().put(SELL_FEE_CONFIG_ITEM_KEY, SELL_FEE_CONFIG_ITEM_VALUE);

        final ExchangeConfig exchangeConfig = new ExchangeConfig();
        exchangeConfig.setExchangeName(EXCHANGE_NAME);
        exchangeConfig.setExchangeAdapter(EXCHANGE_ADAPTER);
        exchangeConfig.setNetworkConfig(networkConfig);
        exchangeConfig.setOptionalConfig(optionalConfig);

        return exchangeConfig;
    }
}
