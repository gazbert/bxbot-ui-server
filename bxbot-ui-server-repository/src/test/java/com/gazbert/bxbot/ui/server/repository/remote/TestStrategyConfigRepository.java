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
import com.gazbert.bxbot.ui.server.domain.strategy.StrategyConfig;
import com.gazbert.bxbot.ui.server.repository.remote.impl.StrategyConfigRepositoryRestClient;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

/**
 * Tests Strategy configuration repository behaves as expected.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@RestClientTest(StrategyConfigRepositoryRestClient.class)
@SpringBootTest(classes = StrategyConfigRepositoryRestClient.class)
public class TestStrategyConfigRepository {

    private static final String REST_ENDPOINT_BASE_URL = "https://localhost.one/api";
    private static final String REST_ENDPOINT_PATH = "/config/strategies";

    private static final String BOT_ID = "gdax-bot-1";
    private static final String BOT_NAME = "GDAX";
    private static final String BOT_STATUS = "Running";
    private static final String BOT_BASE_URL = REST_ENDPOINT_BASE_URL;
    private static final String BOT_USERNAME = "bxbot-ui-server-admin";
    private static final String BOT_PASSWORD = "aintGonnaTellYa!";

    private static final String UNKNOWN_STRAT_ID = "unknown-or-new-strat-id";

    private static final String STRAT_ID_1 = "macd-long-position";
    private static final String STRAT_LABEL_1 = "MACD Long Position Algo";
    private static final String STRAT_DESCRIPTION_1 = "Uses MACD as indicator and takes long position in base currency.";
    private static final String STRAT_CLASSNAME_1 = "com.gazbert.nova.algos.MacdLongBase";

    private static final String STRAT_ID_2 = "long-scalper";
    private static final String STRAT_LABEL_2 = "Long Position Scalper Algo";
    private static final String STRAT_DESCRIPTION_2 = "Scalps and goes long...";
    private static final String STRAT_CLASSNAME_2 = "com.gazbert.nova.algos.LongScalper";

    private static final String BUY_PRICE_CONFIG_ITEM_KEY = "buy-price";
    private static final String BUY_PRICE_CONFIG_ITEM_VALUE = "671.15";
    private static final String AMOUNT_TO_BUY_CONFIG_ITEM_KEY = "buy-amount";
    private static final String AMOUNT_TO_BUY_CONFIG_ITEM_VALUE = "0.5";

    @Autowired
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StrategyConfigRepositoryRestClient restClient;

    private BotConfig botConfig;
    private StrategyConfig strategyConfig_1;
    private StrategyConfig strategyConfig_2;


    @Before
    public void setUp() throws Exception {

        botConfig = new BotConfig(BOT_ID, BOT_NAME, BOT_STATUS, BOT_BASE_URL, BOT_USERNAME, BOT_PASSWORD);

        final Map<String, String> configItems = new HashMap<>();
        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);

        strategyConfig_1 = new StrategyConfig(STRAT_ID_1, STRAT_LABEL_1, STRAT_DESCRIPTION_1, STRAT_CLASSNAME_1, configItems);
        strategyConfig_2 = new StrategyConfig(STRAT_ID_2, STRAT_LABEL_2, STRAT_DESCRIPTION_2, STRAT_CLASSNAME_2, configItems);
    }

    @Test
    public void whenFindAllCalledThenExpectAllStrategyConfigToBeReturned() throws Exception {

        final String allTheStrategiesConfigInJson = objectMapper.writeValueAsString(allTheStrategyConfig());

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(allTheStrategiesConfigInJson, MediaType.APPLICATION_JSON));

        final List<StrategyConfig> allTheStrategyConfig = restClient.findAll(botConfig);

        assertThat(allTheStrategyConfig.size()).isEqualTo(2);
        assertThat(allTheStrategyConfig.contains(strategyConfig_1));
        assertThat(allTheStrategyConfig.contains(strategyConfig_2));

        mockServer.verify();
    }

    @Test
    public void whenFindAllCalledAndRemoteCallFailsThenExpectNoStrategyConfigToBeReturned() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        final List<StrategyConfig> allTheStrategyConfig = restClient.findAll(botConfig);
        assertThat(allTheStrategyConfig).isEqualTo(new ArrayList<>());

        mockServer.verify();
    }

    @Test
    public void whenFindByIdCalledWithKnownIdThenExpectMatchingStrategyConfigToBeReturned() throws Exception {

        final String theStrategyConfigInJson = objectMapper.writeValueAsString(strategyConfig_1);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + '/' + STRAT_ID_1))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(theStrategyConfigInJson, MediaType.APPLICATION_JSON));

        final StrategyConfig strategyConfig = restClient.findById(botConfig, STRAT_ID_1);
        assertThat(strategyConfig).isEqualTo(strategyConfig_1);

        mockServer.verify();
    }

    @Test
    public void whenFindByIdCalledWithUnknownIdThenReturnNullStrategy() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + '/' + STRAT_ID_1))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        final StrategyConfig strategyConfig = restClient.findById(botConfig, STRAT_ID_1);
        assertThat(strategyConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenFindByIdCalledAndRemoteCallFailsThenReturnNullStrategy() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + '/' + STRAT_ID_1))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withServerError());

        final StrategyConfig strategyConfig = restClient.findById(botConfig, STRAT_ID_1);
        assertThat(strategyConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledWithKnownIdThenExpectSavedStrategyToBeReturned() throws Exception {

        final String theStrategyConfigInJson = objectMapper.writeValueAsString(strategyConfig_1);

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withSuccess(theStrategyConfigInJson, MediaType.APPLICATION_JSON));

        final StrategyConfig strategyConfig = restClient.save(botConfig, strategyConfig_1);
        assertThat(strategyConfig).isEqualTo(strategyConfig_1);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledWithUnknownIdThenReturnNullStrategy() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        final StrategyConfig strategyConfig = restClient.save(botConfig, someStrategyConfigWithUnknownId());
        assertThat(strategyConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenSaveCalledAndRemoteCallFailsThenReturnNullStrategy() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH))
                .andExpect(method(HttpMethod.PUT))
                .andRespond(withServerError());

        final StrategyConfig strategyConfig = restClient.save(botConfig, someStrategyConfigWithUnknownId());
        assertThat(strategyConfig).isEqualTo(null);

        mockServer.verify();
    }

    @Test
    public void whenDeleteCalledWithKnownIdThenExpectSuccessResponse() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + "/" + STRAT_ID_1))
                .andRespond(withNoContent());

        final boolean result = restClient.delete(botConfig, STRAT_ID_1);
        assertThat(result).isEqualTo(true);

        mockServer.verify();
    }

    @Test
    public void whenDeleteCalledWithUnknownIdThenExpectFailureResponse() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + "/" + UNKNOWN_STRAT_ID))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        final boolean result = restClient.delete(botConfig, UNKNOWN_STRAT_ID);
        assertThat(result).isEqualTo(false);

        mockServer.verify();
    }

    @Test
    public void whenDeleteCalledAndRemoteCallFailsThenExpectFailureResponse() throws Exception {

        mockServer.expect(requestTo(REST_ENDPOINT_BASE_URL + REST_ENDPOINT_PATH + "/" + UNKNOWN_STRAT_ID))
                .andRespond(withServerError());

        final boolean result = restClient.delete(botConfig, UNKNOWN_STRAT_ID);
        assertThat(result).isEqualTo(false);

        mockServer.verify();
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private List<StrategyConfig> allTheStrategyConfig() {
        final List<StrategyConfig> allTheStrategyConfig = new ArrayList<>();
        allTheStrategyConfig.add(strategyConfig_1);
        allTheStrategyConfig.add(strategyConfig_2);
        return allTheStrategyConfig;
    }

    private static StrategyConfig someStrategyConfigWithUnknownId() {
        final Map<String, String> configItems = new HashMap<>();
        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return new StrategyConfig(UNKNOWN_STRAT_ID, STRAT_LABEL_1, STRAT_DESCRIPTION_1, STRAT_CLASSNAME_1, configItems);
    }
}
