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

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tests the Bot process service behaves as expected.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
public class TestBotProcessService {

    private static final String UNKNOWN_BOT_ID = "unknown-or-new-bot-id";

    private static final String BOT_ID = "bitstamp-bot-1";
    private static final String BOT_NAME = "Bitstamp Bot";
    private static final String BOT_BASE_URL = "https://hostname.one/api";
    private static final String BOT_USERNAME = "admin";
    private static final String BOT_PASSWORD = "password";

    private static final String BOT_DISPLAY_NAME = "Bitstamp";
    private static final String BOT_STATUS = "running";

    private BotConfig knownBotConfig;
    private BotStatus botStatus;

    @MockBean
    BotProcessRepository botProcessRepository;

    @MockBean
    BotConfigRepository botConfigRepository;


    @Before
    public void setup() throws Exception {
        knownBotConfig = new BotConfig(BOT_ID, BOT_NAME, BOT_BASE_URL, BOT_USERNAME, BOT_PASSWORD);
        botStatus = someBotStatus();
    }

    @Test
    public void whenGetStatusCalledWithKnownBotIdThenReturnBotStatus() throws Exception {

        given(botConfigRepository.findById(BOT_ID)).willReturn(knownBotConfig);
        given(botProcessRepository.getBotStatus(knownBotConfig)).willReturn(botStatus);

        final BotProcessService botProcessService =
                new BotProcessServiceImpl(botProcessRepository, botConfigRepository);

        final BotStatus status = botProcessService.getBotStatus(BOT_ID);
        assertThat(status.equals(this.botStatus));

        verify(botConfigRepository, times(1)).findById(BOT_ID);
        verify(botProcessRepository, times(1)).getBotStatus(knownBotConfig);
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

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private static BotStatus someBotStatus() {
        final BotStatus botStatus = new BotStatus();
        botStatus.setId(BOT_ID);
        botStatus.setDisplayName(BOT_DISPLAY_NAME);
        botStatus.setStatus(BOT_STATUS);
        return botStatus;
    }
}
