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
import com.gazbert.bxbot.ui.server.domain.market.MarketConfig;
import com.gazbert.bxbot.ui.server.repository.local.BotConfigRepository;
import com.gazbert.bxbot.ui.server.repository.remote.config.MarketConfigRepository;
import com.gazbert.bxbot.ui.server.services.config.MarketConfigService;
import com.gazbert.bxbot.ui.server.services.config.impl.MarketConfigServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests the Market configuration service behaves as expected.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
public class TestMarketConfigService {

    private static final String UNKNOWN_BOT_ID = "unknown-or-new-bot-id";

    private static final String BOT_1_ID = "bitstamp-bot-1";
    private static final String BOT_1_NAME = "Bitstamp Bot";
    private static final String BOT_1_BASE_URL = "https://hostname.one/api";
    private static final String BOT_1_USERNAME = "admin";
    private static final String BOT_1_PASSWORD = "password";

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

    private BotConfig knownBotConfig;
    private MarketConfig marketConfig_1;
    private MarketConfig marketConfig_2;

    @MockBean
    MarketConfigRepository marketConfigRepository;

    @MockBean
    BotConfigRepository botConfigRepository;


    @Before
    public void setup() throws Exception {
        knownBotConfig = new BotConfig(BOT_1_ID, BOT_1_NAME, BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);

        marketConfig_1 = new MarketConfig(MARKET_1_ID, MARKET_1_NAME, MARKET_1_BASE_CURRENCY,
                MARKET_1_COUNTER_CURRENCY, MARKET_1_ENABLED, MARKET_1_STRATEGY_ID);

        marketConfig_2 = new MarketConfig(MARKET_2_ID, MARKET_2_NAME, MARKET_2_BASE_CURRENCY,
                MARKET_2_COUNTER_CURRENCY, MARKET_2_ENABLED, MARKET_2_STRATEGY_ID);
    }

    @Test
    public void whenGetAllMarketConfigCalledWithKnownBotIdThenReturnAllMarketConfigForTheBot() throws Exception {

        final List<MarketConfig> allTheMarketConfig = allTheMarketConfig();

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(knownBotConfig);
        given(marketConfigRepository.findAll(knownBotConfig)).willReturn(allTheMarketConfig);

        final MarketConfigService marketConfigService =
                new MarketConfigServiceImpl(marketConfigRepository, botConfigRepository);

        final List<MarketConfig> allMarketConfig = marketConfigService.getAllMarketConfig(BOT_1_ID);
        assertThat(allMarketConfig.equals(allTheMarketConfig));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(marketConfigRepository, times(1)).findAll(knownBotConfig);
    }

    @Test
    public void whenGetAllMarketConfigCalledWithUnknownBotIdThenReturnEmptyMarketConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final MarketConfigService marketConfigService =
                new MarketConfigServiceImpl(marketConfigRepository, botConfigRepository);

        final List<MarketConfig> allMarketConfig = marketConfigService.getAllMarketConfig(UNKNOWN_BOT_ID);
        assertThat(allMarketConfig.equals(new ArrayList<>()));

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetMarketConfigCalledWithKnownBotIdThenReturnMarketConfig() throws Exception {

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(knownBotConfig);
        given(marketConfigRepository.findById(knownBotConfig, MARKET_1_ID)).willReturn(marketConfig_1);

        final MarketConfigService marketConfigService =
                new MarketConfigServiceImpl(marketConfigRepository, botConfigRepository);

        final MarketConfig fetchedConfig = marketConfigService.getMarketConfig(BOT_1_ID, MARKET_1_ID);
        assertThat(fetchedConfig.equals(marketConfig_1));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(marketConfigRepository, times(1)).findById(knownBotConfig, MARKET_1_ID);
    }

    @Test
    public void whenGetMarketConfigCalledWithUnknownBotIdThenReturnNullMarketConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final MarketConfigService marketConfigService =
                new MarketConfigServiceImpl(marketConfigRepository, botConfigRepository);

        final MarketConfig fetchedConfig = marketConfigService.getMarketConfig(UNKNOWN_BOT_ID, MARKET_1_ID);
        assertThat(fetchedConfig == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenUpdateMarketConfigCalledWithKnownBotIdThenReturnUpdatedMarketConfig() throws Exception {

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(knownBotConfig);
        given(marketConfigRepository.save(knownBotConfig, marketConfig_1)).willReturn(marketConfig_1);

        final MarketConfigService marketConfigService =
                new MarketConfigServiceImpl(marketConfigRepository, botConfigRepository);

        final MarketConfig updatedConfig = marketConfigService.updateMarketConfig(BOT_1_ID, marketConfig_1);
        assertThat(updatedConfig.equals(marketConfig_1));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(marketConfigRepository, times(1)).save(knownBotConfig, marketConfig_1);
    }

    @Test
    public void whenUpdateMarketConfigCalledWithUnknownBotIdThenReturnNullMarketConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final MarketConfigService marketConfigService =
                new MarketConfigServiceImpl(marketConfigRepository, botConfigRepository);

        final MarketConfig updatedConfig = marketConfigService.updateMarketConfig(UNKNOWN_BOT_ID, marketConfig_1);
        assertThat(updatedConfig == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenCreateMarketConfigCalledWithKnownBotIdThenReturnCreatedMarketConfig() throws Exception {

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(knownBotConfig);
        given(marketConfigRepository.save(knownBotConfig, marketConfig_1)).willReturn(marketConfig_1);

        final MarketConfigService marketConfigService =
                new MarketConfigServiceImpl(marketConfigRepository, botConfigRepository);

        final MarketConfig createdConfig = marketConfigService.createMarketConfig(BOT_1_ID, marketConfig_1);
        assertThat(createdConfig.equals(marketConfig_1));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(marketConfigRepository, times(1)).save(knownBotConfig, marketConfig_1);
    }

    @Test
    public void whenCreateMarketConfigCalledWithUnknownBotIdThenReturnNullMarketConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final MarketConfigService marketConfigService =
                new MarketConfigServiceImpl(marketConfigRepository, botConfigRepository);

        final MarketConfig createdConfig = marketConfigService.createMarketConfig(UNKNOWN_BOT_ID, marketConfig_1);
        assertThat(createdConfig == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenDeleteMarketConfigCalledWithKnownBotIdThenReturnSuccess() throws Exception {

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(knownBotConfig);
        given(marketConfigRepository.delete(knownBotConfig, MARKET_1_ID)).willReturn(true);

        final MarketConfigService marketConfigService =
                new MarketConfigServiceImpl(marketConfigRepository, botConfigRepository);

        assertTrue(marketConfigService.deleteMarketConfig(BOT_1_ID, MARKET_1_ID));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(marketConfigRepository, times(1)).delete(knownBotConfig, MARKET_1_ID);
    }

    @Test
    public void whenDeleteMarketConfigCalledWithUnknownBotIdThenReturnFalse() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final MarketConfigService strategyConfigService =
                new MarketConfigServiceImpl(marketConfigRepository, botConfigRepository);

        assertFalse(strategyConfigService.deleteMarketConfig(UNKNOWN_BOT_ID, MARKET_1_ID));

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
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
}
