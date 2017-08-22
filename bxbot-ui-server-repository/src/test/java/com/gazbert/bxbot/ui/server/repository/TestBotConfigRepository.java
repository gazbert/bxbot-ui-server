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

package com.gazbert.bxbot.ui.server.repository;

import com.gazbert.bxbot.ui.server.datastore.ConfigurationManager;
import com.gazbert.bxbot.ui.server.datastore.bots.generated.BotType;
import com.gazbert.bxbot.ui.server.datastore.bots.generated.BotsType;
import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.repository.impl.BotConfigRepositoryXmlImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static com.gazbert.bxbot.ui.server.datastore.FileLocations.BOTS_CONFIG_XML_FILENAME;
import static com.gazbert.bxbot.ui.server.datastore.FileLocations.BOTS_CONFIG_XSD_FILENAME;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;

/**
 * Tests Bot configuration repository behaves as expected.
 *
 * @author gazbert
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationManager.class})
public class TestBotConfigRepository {

    private static final String UNKNOWN_BOT_ID = "unknown-or-new-bot-id";

    private static final String BOT_1_ID = "bitstamp-bot-1";
    private static final String BOT_1_NAME = "Bitstamp Bot";
    private static final String BOT_1_STATUS = "Running";
    private static final String BOT_1_URL = "https://hostname/bxbot-ui-server";
    private static final String BOT_1_USERNAME = "admin";
    private static final String BOT_1_PASSWORD = "password";

    private static final String BOT_2_ID = "gdax-bot-1";
    private static final String BOT_2_NAME = "GDAX Bot";
    private static final String BOT_2_STATUS = "Running";
    private static final String BOT_2_URL = "https://hostname/bxbot-ui-server";
    private static final String BOT_2_USERNAME = "admin";
    private static final String BOT_2_PASSWORD = "password";


    @Before
    public void setup() throws Exception {
        PowerMock.mockStatic(ConfigurationManager.class);
    }

    @Test
    public void whenFindAllBotsCalledThenExpectRepositoryToReturnThemAll() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        PowerMock.replayAll();

        final BotConfigRepository botConfigRepository = new BotConfigRepositoryXmlImpl();
        final List<BotConfig> botConfigItems = botConfigRepository.findAllBots();

        assertThat(botConfigItems.size()).isEqualTo(2);

        assertThat(botConfigItems.get(0).getId()).isEqualTo(BOT_1_ID);
        assertThat(botConfigItems.get(0).getName()).isEqualTo(BOT_1_NAME);
        assertThat(botConfigItems.get(0).getStatus()).isEqualTo(BOT_1_STATUS);
        assertThat(botConfigItems.get(0).getUrl()).isEqualTo(BOT_1_URL);
        assertThat(botConfigItems.get(0).getUsername()).isEqualTo(BOT_1_USERNAME);
        assertThat(botConfigItems.get(0).getPassword()).isEqualTo(BOT_1_PASSWORD);

        assertThat(botConfigItems.get(1).getId()).isEqualTo(BOT_2_ID);
        assertThat(botConfigItems.get(1).getName()).isEqualTo(BOT_2_NAME);
        assertThat(botConfigItems.get(1).getStatus()).isEqualTo(BOT_2_STATUS);
        assertThat(botConfigItems.get(1).getUrl()).isEqualTo(BOT_2_URL);
        assertThat(botConfigItems.get(1).getUsername()).isEqualTo(BOT_2_USERNAME);
        assertThat(botConfigItems.get(1).getPassword()).isEqualTo(BOT_2_PASSWORD);

        PowerMock.verifyAll();
    }

    @Test
    public void whenFindByIdCalledWithRecognizedIdThenReturnMatchingBot() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        PowerMock.replayAll();

        final BotConfigRepository botConfigRepository = new BotConfigRepositoryXmlImpl();
        final BotConfig botConfig = botConfigRepository.findById(BOT_1_ID);

        assertThat(botConfig.getId()).isEqualTo(BOT_1_ID);
        assertThat(botConfig.getName()).isEqualTo(BOT_1_NAME);
        assertThat(botConfig.getStatus()).isEqualTo(BOT_1_STATUS);
        assertThat(botConfig.getUrl()).isEqualTo(BOT_1_URL);
        assertThat(botConfig.getUsername()).isEqualTo(BOT_1_USERNAME);
        assertThat(botConfig.getPassword()).isEqualTo(BOT_1_PASSWORD);

        PowerMock.verifyAll();
    }

    @Test
    public void whenFindByIdCalledWithUnrecognizedIdThenReturnEmptyBot() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        PowerMock.replayAll();

        final BotConfigRepository botConfigRepository = new BotConfigRepositoryXmlImpl();
        final BotConfig botConfig = botConfigRepository.findById(UNKNOWN_BOT_ID);

        assertThat(botConfig.getId()).isEqualTo(null);
        assertThat(botConfig.getName()).isEqualTo(null);
        assertThat(botConfig.getUrl()).isEqualTo(null);
        assertThat(botConfig.getUsername()).isEqualTo(null);
        assertThat(botConfig.getPassword()).isEqualTo(null);

        PowerMock.verifyAll();
    }

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
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
//        final StrategyConfig strategyConfig = strategyConfigRepository.updateStrategy(someExternalStrategyConfig());
//
//        assertThat(strategyConfig.getId()).isEqualTo(STRAT_ID_1);
//        assertThat(strategyConfig.getLabel()).isEqualTo(STRAT_LABEL_1);
//        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
//        assertThat(strategyConfig.getClassName()).isEqualTo(STRAT_CLASSNAME_1);
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
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
//        final StrategyConfig strategyConfig = strategyConfigRepository.updateStrategy(someExternalStrategyConfigWithUnknownId());
//
//        assertThat(strategyConfig.getId()).isEqualTo(null);
//        assertThat(strategyConfig.getLabel()).isEqualTo(null);
//        assertThat(strategyConfig.getDescription()).isEqualTo(null);
//        assertThat(strategyConfig.getClassName()).isEqualTo(null);
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
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
//        final StrategyConfig strategyConfig = strategyConfigRepository.deleteStrategyById(STRAT_ID_1);
//
//        assertThat(strategyConfig.getId()).isEqualTo(STRAT_ID_1);
//        assertThat(strategyConfig.getLabel()).isEqualTo(STRAT_LABEL_1);
//        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
//        assertThat(strategyConfig.getClassName()).isEqualTo(STRAT_CLASSNAME_1);
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
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
//        final StrategyConfig strategyConfig = strategyConfigRepository.deleteStrategyById("unknown-id");
//
//        assertThat(strategyConfig.getId()).isEqualTo(null);
//        assertThat(strategyConfig.getLabel()).isEqualTo(null);
//        assertThat(strategyConfig.getDescription()).isEqualTo(null);
//        assertThat(strategyConfig.getClassName()).isEqualTo(null);
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
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
//        final StrategyConfig strategyConfig = strategyConfigRepository.createStrategy(someExternalStrategyConfigWithUnknownId());
//
//        assertThat(strategyConfig.getId()).isEqualTo(UNKNOWN_STRAT_ID);
//        assertThat(strategyConfig.getLabel()).isEqualTo(STRAT_LABEL_1);
//        assertThat(strategyConfig.getDescription()).isEqualTo(STRAT_DESCRIPTION_1);
//        assertThat(strategyConfig.getClassName()).isEqualTo(STRAT_CLASSNAME_1);
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
//        final StrategyConfigRepository strategyConfigRepository = new StrategyConfigRepositoryXmlImpl();
//        final StrategyConfig strategyConfig = strategyConfigRepository.createStrategy(someExternalStrategyConfig());
//
//        assertThat(strategyConfig.getId()).isEqualTo(null);
//        assertThat(strategyConfig.getLabel()).isEqualTo(null);
//        assertThat(strategyConfig.getDescription()).isEqualTo(null);
//        assertThat(strategyConfig.getClassName()).isEqualTo(null);
//        assertThat(strategyConfig.getConfigItems().isEmpty());
//
//        PowerMock.verifyAll();
//    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private static BotsType allTheInternalBotsConfig() {

        final BotType botType1 = new BotType();
        botType1.setId(BOT_1_ID);
        botType1.setName(BOT_1_NAME);
        botType1.setStatus(BOT_1_STATUS);
        botType1.setUrl(BOT_1_URL);
        botType1.setUsername(BOT_1_USERNAME);
        botType1.setPassword(BOT_1_PASSWORD);

        final BotType botType2 = new BotType();
        botType2.setId(BOT_2_ID);
        botType2.setName(BOT_2_NAME);
        botType2.setStatus(BOT_2_STATUS);
        botType2.setUrl(BOT_2_URL);
        botType2.setUsername(BOT_2_USERNAME);
        botType2.setPassword(BOT_2_PASSWORD);

        final BotsType botsType = new BotsType();
        botsType.getBots().add(botType1);
        botsType.getBots().add(botType2);
        return botsType;
    }

//    private static TradingStrategiesType allTheInternalStrategiesConfigPlusNewOne() {
//
//        final ConfigItemType buyPriceConfigItem = new ConfigItemType();
//        buyPriceConfigItem.setName(BUY_PRICE_CONFIG_ITEM_KEY);
//        buyPriceConfigItem.setValue(BUY_PRICE_CONFIG_ITEM_VALUE);
//
//        final ConfigItemType amountToBuyConfigItem = new ConfigItemType();
//        amountToBuyConfigItem.setName(AMOUNT_TO_BUY_CONFIG_ITEM_KEY);
//        amountToBuyConfigItem.setValue(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
//
//        final ConfigurationType configurationType = new ConfigurationType();
//        configurationType.getConfigItem().add(buyPriceConfigItem);
//        configurationType.getConfigItem().add(amountToBuyConfigItem);
//
//        final StrategyType newStrat = new StrategyType();
//        newStrat.setId(UNKNOWN_STRAT_ID);
//        newStrat.setLabel(STRAT_LABEL_1);
//        newStrat.setDescription(STRAT_DESCRIPTION_1);
//        newStrat.setClassName(STRAT_CLASSNAME_1);
//        newStrat.setConfiguration(configurationType);
//
//        final TradingStrategiesType existingStatsPlusNewOne = allTheInternalStrategiesConfig();
//        existingStatsPlusNewOne.getStrategies().add(newStrat);
//        return existingStatsPlusNewOne;
//    }

//    private static StrategyConfig someExternalStrategyConfig() {
//        final Map<String, String> configItems = new HashMap<>();
//        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
//        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
//        return new StrategyConfig(STRAT_ID_1, STRAT_LABEL_1, STRAT_DESCRIPTION_1, STRAT_CLASSNAME_1, configItems);
//    }

//    private static StrategyConfig someExternalStrategyConfigWithUnknownId() {
//        final Map<String, String> configItems = new HashMap<>();
//        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
//        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
//        return new StrategyConfig(UNKNOWN_STRAT_ID, STRAT_LABEL_1, STRAT_DESCRIPTION_1, STRAT_CLASSNAME_1, configItems);
//    }
}
