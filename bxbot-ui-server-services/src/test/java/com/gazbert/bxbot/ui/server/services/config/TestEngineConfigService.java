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
import com.gazbert.bxbot.ui.server.domain.engine.EngineConfig;
import com.gazbert.bxbot.ui.server.repository.local.BotConfigRepository;
import com.gazbert.bxbot.ui.server.repository.remote.config.EngineConfigRepository;
import com.gazbert.bxbot.ui.server.services.config.EngineConfigService;
import com.gazbert.bxbot.ui.server.services.config.impl.EngineConfigServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests the Engine configuration service behaves as expected.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
public class TestEngineConfigService {

    private static final String UNKNOWN_BOT_ID = "unknown-or-new-bot-id";

    private static final String BOT_ID = "bitstamp-bot-1";
    private static final String BOT_NAME = "Bitstamp Bot";
    private static final String BOT_BASE_URL = "https://hostname.one/api";
    private static final String BOT_USERNAME = "admin";
    private static final String BOT_PASSWORD = "password";

    private static final String ENGINE_EMERGENCY_STOP_CURRENCY = "BTC";
    private static final BigDecimal ENGINE_EMERGENCY_STOP_BALANCE = new BigDecimal("0.9232320");
    private static final int ENGINE_TRADE_CYCLE_INTERVAL = 60;

    private BotConfig knownBotConfig;
    private EngineConfig engineConfig;

    @MockBean
    EngineConfigRepository engineConfigRepository;

    @MockBean
    BotConfigRepository botConfigRepository;


    @Before
    public void setup() throws Exception {
        knownBotConfig = new BotConfig(BOT_ID, BOT_NAME, BOT_BASE_URL, BOT_USERNAME, BOT_PASSWORD);
        engineConfig = buildEngineConfig();
    }

    @Test
    public void whenGetEngineConfigCalledWithKnownBotIdThenReturnEngineConfig() throws Exception {

        given(botConfigRepository.findById(BOT_ID)).willReturn(knownBotConfig);
        given(engineConfigRepository.get(knownBotConfig)).willReturn(engineConfig);

        final EngineConfigService engineConfigService =
                new EngineConfigServiceImpl(engineConfigRepository, botConfigRepository);

        final EngineConfig engineConfig = engineConfigService.getEngineConfig(BOT_ID);
        assertThat(engineConfig.equals(this.engineConfig));

        verify(botConfigRepository, times(1)).findById(BOT_ID);
        verify(engineConfigRepository, times(1)).get(knownBotConfig);
    }

    @Test
    public void whenGetEngineConfigCalledWithUnknownBotIdThenReturnNullEngineConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final EngineConfigService engineConfigService =
                new EngineConfigServiceImpl(engineConfigRepository, botConfigRepository);

        final EngineConfig engineConfig = engineConfigService.getEngineConfig(UNKNOWN_BOT_ID);
        assertThat(engineConfig == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenUpdateEngineConfigCalledWithKnownBotIdThenReturnEngineConfig() throws Exception {

        given(botConfigRepository.findById(BOT_ID)).willReturn(knownBotConfig);
        given(engineConfigRepository.save(knownBotConfig, engineConfig)).willReturn(engineConfig);

        final EngineConfigService engineConfigService =
                new EngineConfigServiceImpl(engineConfigRepository, botConfigRepository);

        final EngineConfig updatedConfig = engineConfigService.updateEngineConfig(BOT_ID, engineConfig);
        assertThat(updatedConfig.equals(engineConfig));

        verify(botConfigRepository, times(1)).findById(BOT_ID);
        verify(engineConfigRepository, times(1)).save(knownBotConfig, engineConfig);
    }

    @Test
    public void whenUpdateEngineConfigCalledWithUnknownBotIdThenReturnNullEngineConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final EngineConfigService engineConfigService =
                new EngineConfigServiceImpl(engineConfigRepository, botConfigRepository);

        final EngineConfig updatedConfig = engineConfigService.updateEngineConfig(UNKNOWN_BOT_ID, engineConfig);
        assertThat(updatedConfig == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private static EngineConfig buildEngineConfig() {
        final EngineConfig engineConfig = new EngineConfig();
        engineConfig.setBotId(BOT_ID);
        engineConfig.setBotName(BOT_NAME);
        engineConfig.setEmergencyStopCurrency(ENGINE_EMERGENCY_STOP_CURRENCY);
        engineConfig.setEmergencyStopBalance(ENGINE_EMERGENCY_STOP_BALANCE);
        engineConfig.setTradeCycleInterval(ENGINE_TRADE_CYCLE_INTERVAL);
        return engineConfig;
    }
}
