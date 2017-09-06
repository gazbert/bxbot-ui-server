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
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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
    StrategyConfigRepositoryRestClient restClient;

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

        final List<StrategyConfig> allTheStrats = restClient.findAll(botConfig);

        assertThat(allTheStrats.size()).isEqualTo(2);
        assertThat(allTheStrats.contains(strategyConfig_1));
        assertThat(allTheStrats.contains(strategyConfig_2));

        mockServer.verify();
    }


//    @Test
//    public void whenFindAllStrategiesCalledThenExpectServiceToReturnThemAll() throws Exception {
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfig());
//
//        PowerMock.replayAll();
//
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryRestClient();
//        final List<StrategyConfig> strategyConfigItems = strategyConfigRepository.findAllStrategies();
//
//        assertThat(strategyConfigItems.size()).isEqualTo(2);
//
//        assertThat(strategyConfigItems.get(0).getId()).isEqualTo(STRAT_ID_1);
//        assertThat(strategyConfigItems.get(0).getLabel()).isEqualTo(STRAT_LABEL_1);
//        assertThat(strategyConfigItems.get(0).getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
//        assertThat(strategyConfigItems.get(0).getExchangeAdapter()).isEqualTo(STRAT_CLASSNAME_1);
//        assertThat(strategyConfigItems.get(0).getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
//        assertThat(strategyConfigItems.get(0).getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
//        assertThat(strategyConfigItems.get(0).getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
//        assertThat(strategyConfigItems.get(0).getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));
//
//        assertThat(strategyConfigItems.get(1).getId()).isEqualTo(STRAT_ID_2);
//        assertThat(strategyConfigItems.get(1).getLabel()).isEqualTo(STRAT_LABEL_2);
//        assertThat(strategyConfigItems.get(1).getDescription()).isEqualTo(STRAT_DESCRIPTION_2);
//        assertThat(strategyConfigItems.get(1).getExchangeAdapter()).isEqualTo(STRAT_CLASSNAME_2);
//        assertThat(strategyConfigItems.get(1).getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
//        assertThat(strategyConfigItems.get(1).getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
//        assertThat(strategyConfigItems.get(1).getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
//        assertThat(strategyConfigItems.get(1).getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));
//
//        PowerMock.verifyAll();
//    }

//    @Test
//    public void whenFindByIdCalledWithRecognizedIdThenReturnMatchingStrategy() throws Exception {
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfig());
//
//        PowerMock.replayAll();
//
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryRestClient();
//        final StrategyConfig strategyConfig = strategyConfigRepository.findById(STRAT_ID_1);
//
//        assertThat(strategyConfig.getId()).isEqualTo(STRAT_ID_1);
//        assertThat(strategyConfig.getLabel()).isEqualTo(STRAT_LABEL_1);
//        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
//        assertThat(strategyConfig.getExchangeAdapter()).isEqualTo(STRAT_CLASSNAME_1);
//        assertThat(strategyConfig.getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
//        assertThat(strategyConfig.getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
//        assertThat(strategyConfig.getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
//        assertThat(strategyConfig.getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));
//
//        PowerMock.verifyAll();
//    }

//    @Test
//    public void whenFindByIdCalledWithUnrecognizedIdThenReturnEmptyStrategy() throws Exception {
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfig());
//
//        PowerMock.replayAll();
//
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryRestClient();
//        final StrategyConfig strategyConfig = strategyConfigRepository.findById("unknown-id");
//
//        assertThat(strategyConfig.getId()).isEqualTo(null);
//        assertThat(strategyConfig.getLabel()).isEqualTo(null);
//        assertThat(strategyConfig.getDescription()).isEqualTo(null);
//        assertThat(strategyConfig.getExchangeAdapter()).isEqualTo(null);
//        assertThat(strategyConfig.getConfigItems().isEmpty());
//
//        PowerMock.verifyAll();
//    }

//    @Test
//    public void whenUpdateStrategyCalledWithKnownIdThenExpectServiceToReturnUpdatedStrategy() throws Exception {
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfig());
//
//        ConfigurationManager.saveConfig(
//                eq(TradingStrategiesType.class),
//                anyObject(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME));
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfig());
//
//        PowerMock.replayAll();
//
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryRestClient();
//        final StrategyConfig strategyConfig = strategyConfigRepository.save(someExternalStrategyConfig());
//
//        assertThat(strategyConfig.getId()).isEqualTo(STRAT_ID_1);
//        assertThat(strategyConfig.getLabel()).isEqualTo(STRAT_LABEL_1);
//        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
//        assertThat(strategyConfig.getExchangeAdapter()).isEqualTo(STRAT_CLASSNAME_1);
//        assertThat(strategyConfig.getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
//        assertThat(strategyConfig.getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
//        assertThat(strategyConfig.getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
//        assertThat(strategyConfig.getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));
//
//        PowerMock.verifyAll();
//    }

//    @Test
//    public void whenUpdateStrategyConfigCalledWithUnrecognizedIdThenReturnEmptyStrategy() throws Exception {
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfig());
//
//        PowerMock.replayAll();
//
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryRestClient();
//        final StrategyConfig strategyConfig = strategyConfigRepository.save(someExternalStrategyConfigWithUnknownId());
//
//        assertThat(strategyConfig.getId()).isEqualTo(null);
//        assertThat(strategyConfig.getLabel()).isEqualTo(null);
//        assertThat(strategyConfig.getDescription()).isEqualTo(null);
//        assertThat(strategyConfig.getExchangeAdapter()).isEqualTo(null);
//        assertThat(strategyConfig.getConfigItems().isEmpty());
//
//        PowerMock.verifyAll();
//    }

//    @Test
//    public void whenDeleteByIdCalledWithRecognizedIdThenReturnMatchingStrategy() throws Exception {
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfig());
//
//        ConfigurationManager.saveConfig(
//                eq(TradingStrategiesType.class),
//                anyObject(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME));
//
//        PowerMock.replayAll();
//
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryRestClient();
//        final StrategyConfig strategyConfig = strategyConfigRepository.deleteStrategyById(STRAT_ID_1);
//
//        assertThat(strategyConfig.getId()).isEqualTo(STRAT_ID_1);
//        assertThat(strategyConfig.getLabel()).isEqualTo(STRAT_LABEL_1);
//        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
//        assertThat(strategyConfig.getExchangeAdapter()).isEqualTo(STRAT_CLASSNAME_1);
//        assertThat(strategyConfig.getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
//        assertThat(strategyConfig.getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
//        assertThat(strategyConfig.getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
//        assertThat(strategyConfig.getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));
//
//        PowerMock.verifyAll();
//    }

//    @Test
//    public void whenDeleteByIdCalledWithUnrecognizedIdThenReturnEmptyStrategy() throws Exception {
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfig());
//
//        PowerMock.replayAll();
//
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryRestClient();
//        final StrategyConfig strategyConfig = strategyConfigRepository.deleteStrategyById("unknown-id");
//
//        assertThat(strategyConfig.getId()).isEqualTo(null);
//        assertThat(strategyConfig.getLabel()).isEqualTo(null);
//        assertThat(strategyConfig.getDescription()).isEqualTo(null);
//        assertThat(strategyConfig.getExchangeAdapter()).isEqualTo(null);
//        assertThat(strategyConfig.getConfigItems().isEmpty());
//
//        PowerMock.verifyAll();
//    }

//    @Test
//    public void whenCreateStrategyCalledWithUnknownThenExpectServiceToReturnCreatedStrategy() throws Exception {
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfig());
//
//        ConfigurationManager.saveConfig(
//                eq(TradingStrategiesType.class),
//                anyObject(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME));
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfigPlusNewOne());
//
//        PowerMock.replayAll();
//
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryRestClient();
//        final StrategyConfig strategyConfig = strategyConfigRepository.createStrategy(someExternalStrategyConfigWithUnknownId());
//
//        assertThat(strategyConfig.getId()).isEqualTo(UNKNOWN_STRAT_ID);
//        assertThat(strategyConfig.getLabel()).isEqualTo(STRAT_LABEL_1);
//        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
//        assertThat(strategyConfig.getExchangeAdapter()).isEqualTo(STRAT_CLASSNAME_1);
//        assertThat(strategyConfig.getConfigItems().containsKey(BUY_PRICE_CONFIG_ITEM_KEY));
//        assertThat(strategyConfig.getConfigItems().containsValue(BUY_PRICE_CONFIG_ITEM_VALUE));
//        assertThat(strategyConfig.getConfigItems().containsKey(AMOUNT_TO_BUY_CONFIG_ITEM_KEY));
//        assertThat(strategyConfig.getConfigItems().containsValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));
//
//        PowerMock.verifyAll();
//    }

//    @Test
//    public void whenCreateStrategyConfigCalledWithExistingIdThenReturnEmptyStrategy() throws Exception {
//
//        expect(ConfigurationManager.loadConfig(
//                eq(TradingStrategiesType.class),
//                eq(STRATEGIES_CONFIG_XML_FILENAME),
//                eq(STRATEGIES_CONFIG_XSD_FILENAME))).
//                andReturn(allTheInternalStrategiesConfig());
//
//        PowerMock.replayAll();
//
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryRestClient();
//        final StrategyConfig strategyConfig = strategyConfigRepository.createStrategy(someExternalStrategyConfig());
//
//        assertThat(strategyConfig.getId()).isEqualTo(null);
//        assertThat(strategyConfig.getLabel()).isEqualTo(null);
//        assertThat(strategyConfig.getDescription()).isEqualTo(null);
//        assertThat(strategyConfig.getExchangeAdapter()).isEqualTo(null);
//        assertThat(strategyConfig.getConfigItems().isEmpty());
//
//        PowerMock.verifyAll();
//    }

    //    @Test
//    public void testGetMessage_404() {
//        mockServer.expect(requestTo("http://google.com")).andExpect(method(HttpMethod.GET))
//                .andRespond(withStatus(HttpStatus.NOT_FOUND));
//
//        String result = simpleRestService.getMessage();
//
//        mockServer.verify();
//        assertThat(result, allOf(containsString("FAILED"), containsString("404")));


    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private static StrategyConfig oneStrategyConfig() {
        final Map<String, String> configItems = new HashMap<>();
        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return new StrategyConfig(STRAT_ID_1, STRAT_LABEL_1, STRAT_DESCRIPTION_1, STRAT_CLASSNAME_1, configItems);
    }

    private List<StrategyConfig> allTheStrategyConfig() {
        final List<StrategyConfig> allTheStrategyConfig = new ArrayList<>();
        allTheStrategyConfig.add(strategyConfig_1);
        allTheStrategyConfig.add(strategyConfig_2);
        return allTheStrategyConfig;
    }

//    private static StrategyConfig someExternalStrategyConfigWithUnknownId() {
//        final Map<String, String> configItems = new HashMap<>();
//        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
//        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
//        return new StrategyConfig(UNKNOWN_STRAT_ID, STRAT_LABEL_1, STRAT_DESCRIPTION_1, STRAT_CLASSNAME_1, configItems);
//    }
}
