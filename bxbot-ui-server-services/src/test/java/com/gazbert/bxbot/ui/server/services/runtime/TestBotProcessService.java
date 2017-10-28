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

package com.gazbert.bxbot.ui.server.services.runtime;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.bot.BotStatus;
import com.gazbert.bxbot.ui.server.repository.local.BotConfigRepository;
import com.gazbert.bxbot.ui.server.repository.remote.runtime.BotProcessRepository;
import com.gazbert.bxbot.ui.server.services.runtime.impl.BotProcessServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests the Bot process service behaves as expected.
 * <p>
 * TODO - test when bot is down and check 'stopped' status is returned (need enum)
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
public class TestBotProcessService {

    private static final String UNKNOWN_BOT_ID = "unknown-or-new-bot-id";

    private static final String BOT_1_ID = "bitstamp-bot-1";
    private static final String BOT_1_NAME = "Bitstamp Bot";
    private static final String BOT_1_BASE_URL = "https://hostname.one/api";
    private static final String BOT_1_USERNAME = "admin";
    private static final String BOT_1_PASSWORD = "password";

    private static final String BOT_1_DISPLAY_NAME = "Bitstamp";
    private static final String BOT_1_STATUS = "running";

    private static final String BOT_2_ID = "gdax-bot-1";
    private static final String BOT_2_NAME = "GDAX Bot";
    private static final String BOT_2_BASE_URL = "https://hostname.one/api";
    private static final String BOT_2_USERNAME = "admin";
    private static final String BOT_2_PASSWORD = "password";

    private static final String BOT_2_DISPLAY_NAME = "GDAX";
    private static final String BOT_2_STATUS = "running";

    private BotConfig bot1Config;
    private BotStatus bot1Status;

    private BotConfig bot2Config;
    private BotStatus bot2Status;

    @MockBean
    BotProcessRepository botProcessRepository;

    @MockBean
    BotConfigRepository botConfigRepository;


    @Before
    public void setup() throws Exception {
        bot1Config = new BotConfig(BOT_1_ID, BOT_1_NAME, BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);
        bot1Status = new BotStatus(BOT_1_ID, BOT_1_DISPLAY_NAME, BOT_1_STATUS);
        bot2Config = new BotConfig(BOT_2_ID, BOT_2_NAME, BOT_2_BASE_URL, BOT_2_USERNAME, BOT_2_PASSWORD);
        bot2Status = new BotStatus(BOT_2_ID, BOT_2_DISPLAY_NAME, BOT_2_STATUS);
    }

    @Test
    public void whenGetStatusCalledWithKnownBotIdThenReturnBotStatus() throws Exception {

        given(botConfigRepository.findById(BOT_1_ID)).willReturn(bot1Config);
        given(botProcessRepository.getBotStatus(bot1Config)).willReturn(bot1Status);

        final BotProcessService botProcessService =
                new BotProcessServiceImpl(botProcessRepository, botConfigRepository);

        final BotStatus status = botProcessService.getBotStatus(BOT_1_ID);
        assertThat(status.equals(this.bot1Status));

        verify(botConfigRepository, times(1)).findById(BOT_1_ID);
        verify(botProcessRepository, times(1)).getBotStatus(bot1Config);
    }

    @Test
    public void whenGetStatusCalledWithUnknownBotIdThenReturnNullBotStatus() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final BotProcessService botProcessService =
                new BotProcessServiceImpl(botProcessRepository, botConfigRepository);

        final BotStatus status = botProcessService.getBotStatus(UNKNOWN_BOT_ID);
        assertThat(status == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetAllStatusCalledWThenReturnAllBotStatus() throws Exception {

        final List<BotConfig> allBotConfig = new ArrayList<>();
        allBotConfig.add(bot1Config);
        allBotConfig.add(bot2Config);

        given(botConfigRepository.findAll()).willReturn(allBotConfig);
        given(botProcessRepository.getBotStatus(bot1Config)).willReturn(bot1Status);
        given(botProcessRepository.getBotStatus(bot2Config)).willReturn(bot2Status);

        final BotProcessService botProcessService =
                new BotProcessServiceImpl(botProcessRepository, botConfigRepository);

        final List<BotStatus> allBotStatus = botProcessService.getAllBotStatus();
        assertThat(allBotStatus.size() == 2);
        assertThat(allBotStatus.contains(bot1Status));
        assertThat(allBotStatus.contains(bot2Status));

        verify(botConfigRepository, times(1)).findAll();
        verify(botProcessRepository, times(1)).getBotStatus(bot1Config);
        verify(botProcessRepository, times(1)).getBotStatus(bot2Config);
    }

    @Test
    public void whenGetAllStatusCalledAndNoBotsFoundThenExpectEmptyBotStatusList() throws Exception {

        final List<BotConfig> allBotConfig = new ArrayList<>();
        given(botConfigRepository.findAll()).willReturn(allBotConfig);

        final BotProcessService botProcessService =
                new BotProcessServiceImpl(botProcessRepository, botConfigRepository);

        final List<BotStatus> allBotStatus = botProcessService.getAllBotStatus();
        assertThat(allBotStatus.isEmpty());

        verify(botConfigRepository, times(1)).findAll();
    }
}
