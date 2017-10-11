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

package com.gazbert.bxbot.ui.server.services;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.strategy.StrategyConfig;
import com.gazbert.bxbot.ui.server.repository.local.BotConfigRepository;
import com.gazbert.bxbot.ui.server.repository.remote.StrategyConfigRepository;
import com.gazbert.bxbot.ui.server.services.impl.StrategyConfigServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests the Strategy configuration service behaves as expected.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
public class TestStrategyConfigService {

    private static final String UNKNOWN_BOT_ID = "unknown-or-new-bot-id";

    private static final String BOT_1_ID = "bitstamp-bot-1";
    private static final String BOT_1_NAME = "Bitstamp Bot";
    private static final String BOT_1_STATUS = "Running";
    private static final String BOT_1_BASE_URL = "https://hostname.one/api";
    private static final String BOT_1_USERNAME = "admin";
    private static final String BOT_1_PASSWORD = "password";

    private static final String STRAT_1_ID = "macd-long-position";
    private static final String STRAT_1_NAME = "MACD Strat Algo";
    private static final String STRAT_1_DESCRIPTION = "Uses MACD as indicator and takes long position in base currency.";
    private static final String STRAT_1_CLASSNAME = "com.gazbert.nova.algos.MacdLongBase";

    private static final String STRAT_2_ID = "long-scalper";
    private static final String STRAT_2_LABEL = "Long Position Scalper Algo";
    private static final String STRAT_2_DESCRIPTION = "Scalps and goes long...";
    private static final String STRAT_2_CLASSNAME = "com.gazbert.nova.algos.LongScalper";

    private static final String BUY_PRICE_CONFIG_ITEM_KEY = "buy-price";
    private static final String BUY_PRICE_CONFIG_ITEM_VALUE = "671.15";
    private static final String AMOUNT_TO_BUY_CONFIG_ITEM_KEY = "buy-amount";
    private static final String AMOUNT_TO_BUY_CONFIG_ITEM_VALUE = "0.5";

    private BotConfig knownBotConfig;

    @MockBean
    StrategyConfigRepository strategyConfigRepository;

    @MockBean
    BotConfigRepository botConfigRepository;


    @Before
    public void setup() throws Exception {
        knownBotConfig = new BotConfig(BOT_1_ID, BOT_1_NAME, BOT_1_STATUS, BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);
    }

    @Test
    public void whenGetAllStrategyConfigCalledWithKnownBotIdThenReturnAllBotConfig() throws Exception {

        final List<StrategyConfig> allTheStrategiesConfig = buildAllTheStrategiesConfig();

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(knownBotConfig);
        given(strategyConfigRepository.findAll(knownBotConfig)).willReturn(allTheStrategiesConfig);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        final List<StrategyConfig> strategyConfigs = strategyConfigService.getAllStrategyConfig(BOT_1_ID);
        assertThat(strategyConfigs.equals(allTheStrategiesConfig));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(strategyConfigRepository, times(1)).findAll(knownBotConfig);
    }

    @Test
    public void whenGetAllStrategyConfigCalledWithUnknownBotIdThenReturnEmptyBotConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        final List<StrategyConfig> strategyConfigs = strategyConfigService.getAllStrategyConfig(UNKNOWN_BOT_ID);
        assertThat(strategyConfigs.equals(new ArrayList<>()));

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private static List<StrategyConfig> buildAllTheStrategiesConfig() {

        final StrategyConfig strategyConfig1 = new StrategyConfig(STRAT_1_ID, STRAT_1_NAME, STRAT_1_DESCRIPTION,
                STRAT_1_CLASSNAME, someConfigItems());
        final StrategyConfig strategyConfig2 = new StrategyConfig(STRAT_2_ID, STRAT_2_LABEL, STRAT_2_DESCRIPTION,
                STRAT_2_CLASSNAME, someConfigItems());

        final List<StrategyConfig> allStrategies = new ArrayList<>();
        allStrategies.add(strategyConfig1);
        allStrategies.add(strategyConfig2);
        return allStrategies;
    }

    private static StrategyConfig someStrategyConfig() {
        return new StrategyConfig(STRAT_1_ID, STRAT_1_NAME, STRAT_1_DESCRIPTION, STRAT_1_CLASSNAME, someConfigItems());
    }

    private static Map<String, String> someConfigItems() {
        final Map<String, String> configItems = new HashMap<>();
        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return configItems;
    }
}
