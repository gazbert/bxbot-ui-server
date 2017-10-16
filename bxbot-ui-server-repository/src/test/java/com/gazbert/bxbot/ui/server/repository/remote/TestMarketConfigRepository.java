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
import com.gazbert.bxbot.ui.server.domain.market.MarketConfig;
import com.gazbert.bxbot.ui.server.repository.remote.impl.MarketConfigRepositoryRestClient;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Tests Market configuration repository behaves as expected.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@RestClientTest(MarketConfigRepositoryRestClient.class)
@SpringBootTest(classes = MarketConfigRepositoryRestClient.class)
public class TestMarketConfigRepository {

    private static final String REST_ENDPOINT_BASE_URL = "https://localhost.one/api";
    private static final String REST_ENDPOINT_PATH = "/config/markets";

    private static final String BOT_ID = "gdax-bot-1";
    private static final String BOT_NAME = "GDAX";
    private static final String BOT_STATUS = "Running";
    private static final String BOT_BASE_URL = REST_ENDPOINT_BASE_URL;
    private static final String BOT_USERNAME = "bxbot-ui-server-admin";
    private static final String BOT_PASSWORD = "aintGonnaTellYa!";

    private static final String UNKNOWN_MARKET_ID = "unknown-id";

    private static final String MARKET_1_ID = "btc_usd";
    private static final String MARKET_1_NAME = "BTC/USD";
    private static final String MARKET_1_BASE_CURRENCY = "BTC";
    private static final String MARKET_1_COUNTER_CURRENCY = "USD";
    private static final boolean MARKET_1_ENABLED = true;
    private static final String MARKET_1_STRATEGY_ID = "scalper-strategy";

    private static final String MARKET_2_ID = "btc_gbp";
    private static final String MARKET_2_NAME = "BTC/GBP";
    private static final String MARKET_2_BASE_CURRENCY = "BTC";
    private static final String MARKET_2_COUNTER_CURRENCY = "GBP";
    private static final boolean MARKET_2_ENABLED = false;
    private static final String MARKET_2_STRATEGY_ID = "macd-strategy";

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MarketConfigRepositoryRestClient restClient;

    private BotConfig botConfig;
    private MarketConfig marketConfig_1;
    private MarketConfig marketConfig_2;


    @Before
    public void setUp() throws Exception {

        botConfig = new BotConfig(BOT_ID, BOT_NAME, BOT_STATUS, BOT_BASE_URL, BOT_USERNAME, BOT_PASSWORD);

        marketConfig_1 = new MarketConfig(MARKET_1_ID, MARKET_1_NAME, MARKET_1_BASE_CURRENCY,
                MARKET_1_COUNTER_CURRENCY, MARKET_1_ENABLED, MARKET_1_STRATEGY_ID);

        marketConfig_2 = new MarketConfig(MARKET_2_ID, MARKET_2_NAME, MARKET_2_BASE_CURRENCY,
                MARKET_2_COUNTER_CURRENCY, MARKET_2_ENABLED, MARKET_2_STRATEGY_ID);
    }

    @Test
    public void whenFindAllCalledThenExpectAllMarketConfigToBeReturned() throws Exception {

        final String allTheMarketsConfigInJson = objectMapper.writeValueAsString(allTheMarketConfig());

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(allTheMarketsConfigInJson, MediaType.APPLICATION_JSON));

        final List<MarketConfig> allTheMarketConfig = restClient.findAll(botConfig);

        assertThat(allTheMarketConfig.size()).isEqualTo(2);
        assertThat(allTheMarketConfig.contains(marketConfig_1));
        assertThat(allTheMarketConfig.contains(marketConfig_2));

        mockServer.verify();
    }

    @Test
    public void whenFindAllCalledAndRemoteCallFailsThenExpectNoMarketConfigToBeReturned() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        final List<MarketConfig> allTheMarketConfig = restClient.findAll(botConfig);
        assertThat(allTheMarketConfig).isEqualTo(new ArrayList<>());

        mockServer.verify();
    }

    @Test
    public void whenFindByIdCalledWithKnownIdThenExpectMatchingMarketConfigToBeReturned() throws Exception {

        final String theMarketConfigInJson = objectMapper.writeValueAsString(marketConfig_1);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + '/' + MARKET_1_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(theMarketConfigInJson, MediaType.APPLICATION_JSON));

        final MarketConfig marketConfig = restClient.findById(botConfig, MARKET_1_ID);
        assertThat(marketConfig).isEqualTo(marketConfig_1);

        mockServer.verify();
    }

    @Test
    public void whenFindByIdCalledWithUnknownIdThenReturnNullMarket() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + '/' + MARKET_1_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        final MarketConfig marketConfig = restClient.findById(botConfig, MARKET_1_ID);
        assertThat(marketConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenFindByIdCalledAndRemoteCallFailsThenReturnNullMarket() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + '/' + MARKET_1_ID))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        final MarketConfig marketConfig = restClient.findById(botConfig, MARKET_1_ID);
        assertThat(marketConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledWithKnownIdThenExpectSavedMarketToBeReturned() throws Exception {

        final String theMarketConfigInJson = objectMapper.writeValueAsString(marketConfig_1);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(theMarketConfigInJson, MediaType.APPLICATION_JSON));

        final MarketConfig strategyConfig = restClient.save(botConfig, marketConfig_1);
        assertThat(strategyConfig).isEqualTo(marketConfig_1);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledWithUnknownIdThenReturnNullMarket() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        final MarketConfig marketConfig = restClient.save(botConfig, unrecognizedMarketConfig());
        assertThat(marketConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledAndRemoteCallFailsThenReturnNullMarket() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withServerError());

        final MarketConfig marketConfig = restClient.save(botConfig, unrecognizedMarketConfig());
        assertThat(marketConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenDeleteCalledWithKnownIdThenExpectSuccessResponse() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + "/" + MARKET_1_ID))
                .andRespond(withNoContent());

        final boolean result = restClient.delete(botConfig, MARKET_1_ID);
        assertThat(result).isEqualTo(true);

        mockServer.verify();
    }

    @Test
    public void whenDeleteCalledWithUnknownIdThenExpectFailureResponse() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + "/" + UNKNOWN_MARKET_ID))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        final boolean result = restClient.delete(botConfig, UNKNOWN_MARKET_ID);
        assertThat(result).isEqualTo(false);

        mockServer.verify();
    }

    @Test
    public void whenDeleteCalledAndRemoteCallFailsThenExpectFailureResponse() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + "/" + UNKNOWN_MARKET_ID))
                .andRespond(withServerError());

        final boolean result = restClient.delete(botConfig, UNKNOWN_MARKET_ID);
        assertThat(result).isEqualTo(false);

        mockServer.verify();
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private List<MarketConfig> allTheMarketConfig() {
        final List<MarketConfig> allMarkets = new ArrayList<>();
        allMarkets.add(marketConfig_1);
        allMarkets.add(marketConfig_2);
        return allMarkets;
    }

    private static MarketConfig unrecognizedMarketConfig() {
        return new MarketConfig(UNKNOWN_MARKET_ID, MARKET_1_NAME, MARKET_1_BASE_CURRENCY,
                MARKET_1_COUNTER_CURRENCY, MARKET_1_ENABLED, MARKET_1_STRATEGY_ID);
    }
}
