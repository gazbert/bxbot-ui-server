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

package com.gazbert.bxbot.ui.server.services.config;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.strategy.StrategyConfig;
import com.gazbert.bxbot.ui.server.repository.local.BotConfigRepository;
import com.gazbert.bxbot.ui.server.repository.remote.config.StrategyConfigRepository;
import com.gazbert.bxbot.ui.server.services.config.StrategyConfigService;
import com.gazbert.bxbot.ui.server.services.config.impl.StrategyConfigServiceImpl;
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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
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
    private StrategyConfig strategyConfig_1;
    private StrategyConfig strategyConfig_2;

    @MockBean
    StrategyConfigRepository strategyConfigRepository;

    @MockBean
    BotConfigRepository botConfigRepository;


    @Before
    public void setup() throws Exception {
        knownBotConfig = new BotConfig(BOT_1_ID, BOT_1_NAME, BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);

        strategyConfig_1 = new StrategyConfig(STRAT_1_ID, STRAT_1_NAME, STRAT_1_DESCRIPTION,
                STRAT_1_CLASSNAME, someConfigItems());
        strategyConfig_2 = new StrategyConfig(STRAT_2_ID, STRAT_2_LABEL, STRAT_2_DESCRIPTION,
                STRAT_2_CLASSNAME, someConfigItems());
    }

    @Test
    public void whenGetAllStrategyConfigCalledWithKnownBotIdThenReturnAllStrategyConfigForTheBot() throws Exception {

        final List<StrategyConfig> allTheStrategiesConfig = allTheStrategiesConfig();

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
    public void whenGetAllStrategyConfigCalledWithUnknownBotIdThenReturnEmptyStrategyConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        final List<StrategyConfig> strategyConfigs = strategyConfigService.getAllStrategyConfig(UNKNOWN_BOT_ID);
        assertThat(strategyConfigs.equals(new ArrayList<>()));

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetStrategyConfigCalledWithKnownBotIdThenReturnStrategyConfig() throws Exception {

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(knownBotConfig);
        given(strategyConfigRepository.findById(knownBotConfig, STRAT_1_ID)).willReturn(strategyConfig_1);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        final StrategyConfig fetchedConfig = strategyConfigService.getStrategyConfig(BOT_1_ID, STRAT_1_ID);
        assertThat(fetchedConfig.equals(strategyConfig_1));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(strategyConfigRepository, times(1)).findById(knownBotConfig, STRAT_1_ID);
    }

    @Test
    public void whenGetStrategyConfigCalledWithUnknownBotIdThenReturnNullStrategyConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        final StrategyConfig fetchedConfig = strategyConfigService.getStrategyConfig(UNKNOWN_BOT_ID, STRAT_1_ID);
        assertThat(fetchedConfig == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenUpdateStrategyConfigCalledWithKnownBotIdThenReturnUpdatedStrategyConfig() throws Exception {

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(knownBotConfig);
        given(strategyConfigRepository.save(knownBotConfig, strategyConfig_1)).willReturn(strategyConfig_1);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        final StrategyConfig updatedConfig = strategyConfigService.updateStrategyConfig(BOT_1_ID, strategyConfig_1);
        assertThat(updatedConfig.equals(strategyConfig_1));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(strategyConfigRepository, times(1)).save(knownBotConfig, strategyConfig_1);
    }

    @Test
    public void whenUpdateStrategyConfigCalledWithUnknownBotIdThenReturnNullStrategyConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        final StrategyConfig updatedConfig = strategyConfigService.updateStrategyConfig(UNKNOWN_BOT_ID, strategyConfig_1);
        assertThat(updatedConfig == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenCreateStrategyConfigCalledWithKnownBotIdThenReturnCreatedStrategyConfig() throws Exception {

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(knownBotConfig);
        given(strategyConfigRepository.save(knownBotConfig, strategyConfig_1)).willReturn(strategyConfig_1);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        final StrategyConfig createdConfig = strategyConfigService.createStrategyConfig(BOT_1_ID, strategyConfig_1);
        assertThat(createdConfig.equals(strategyConfig_1));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(strategyConfigRepository, times(1)).save(knownBotConfig, strategyConfig_1);
    }

    @Test
    public void whenCreateStrategyConfigCalledWithUnknownBotIdThenReturnNullStrategyConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        final StrategyConfig createdConfig = strategyConfigService.createStrategyConfig(UNKNOWN_BOT_ID, strategyConfig_1);
        assertThat(createdConfig == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenDeleteStrategyConfigCalledWithKnownBotIdThenReturnSuccess() throws Exception {

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(knownBotConfig);
        given(strategyConfigRepository.delete(knownBotConfig, STRAT_1_ID)).willReturn(true);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        assertTrue(strategyConfigService.deleteStrategyConfig(BOT_1_ID, STRAT_1_ID));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(strategyConfigRepository, times(1)).delete(knownBotConfig, STRAT_1_ID);
    }

    @Test
    public void whenDeleteStrategyConfigCalledWithUnknownBotIdThenReturnFalse() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final StrategyConfigService strategyConfigService =
                new StrategyConfigServiceImpl(strategyConfigRepository, botConfigRepository);

        assertFalse(strategyConfigService.deleteStrategyConfig(UNKNOWN_BOT_ID, STRAT_1_ID));

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private List<StrategyConfig> allTheStrategiesConfig() {
        final List<StrategyConfig> allStrategies = new ArrayList<>();
        allStrategies.add(strategyConfig_1);
        allStrategies.add(strategyConfig_2);
        return allStrategies;
    }

    private static Map<String, String> someConfigItems() {
        final Map<String, String> configItems = new HashMap<>();
        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return configItems;
    }
}
