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
import com.gazbert.bxbot.ui.server.domain.emailalerts.EmailAlertsConfig;
import com.gazbert.bxbot.ui.server.domain.emailalerts.SmtpConfig;
import com.gazbert.bxbot.ui.server.repository.local.BotConfigRepository;
import com.gazbert.bxbot.ui.server.repository.remote.config.EmailAlertsConfigRepository;
import com.gazbert.bxbot.ui.server.services.config.EmailAlertsConfigService;
import com.gazbert.bxbot.ui.server.services.config.impl.EmailAlertsConfigServiceImpl;
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
 * Tests the Email Alerts configuration service behaves as expected.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
public class TestEmailAlertsConfigService {

    private static final String UNKNOWN_BOT_ID = "unknown-or-new-bot-id";

    private static final String BOT_ID = "bitstamp-bot-1";
    private static final String BOT_NAME = "Bitstamp Bot";
    private static final String BOT_BASE_URL = "https://hostname.one/api";
    private static final String BOT_USERNAME = "admin";
    private static final String BOT_PASSWORD = "password";

    private static final boolean ENABLED = true;
    private static final String HOST = "smtp.host.deathstar.com";
    private static final int TLS_PORT = 573;
    private static final String ACCOUNT_USERNAME = "boba@google.com";
    private static final String ACCOUNT_PASSWORD = "bounty";
    private static final String FROM_ADDRESS = "boba.fett@Mandalore.com";
    private static final String TO_ADDRESS = "darth.vader@deathstar.com";

    private BotConfig knownBotConfig;
    private EmailAlertsConfig emailAlertsConfig;

    @MockBean
    EmailAlertsConfigRepository emailAlertsConfigRepository;

    @MockBean
    BotConfigRepository botConfigRepository;


    @Before
    public void setup() throws Exception {
        knownBotConfig = new BotConfig(BOT_ID, BOT_NAME, BOT_BASE_URL, BOT_USERNAME, BOT_PASSWORD);
        emailAlertsConfig = buildEmailAlertsConfig();
    }

    @Test
    public void whenGetEmailAlertsConfigCalledWithKnownBotIdThenReturnEmailAlertsConfig() throws Exception {

        given(botConfigRepository.findById(BOT_ID)).willReturn(knownBotConfig);
        given(emailAlertsConfigRepository.get(knownBotConfig)).willReturn(emailAlertsConfig);

        final EmailAlertsConfigService emailAlertsConfigService =
                new EmailAlertsConfigServiceImpl(emailAlertsConfigRepository, botConfigRepository);

        final EmailAlertsConfig emailAlertsConfig = emailAlertsConfigService.getEmailAlertsConfig(BOT_ID);
        assertThat(emailAlertsConfig.equals(this.emailAlertsConfig));

        verify(botConfigRepository, times(1)).findById(BOT_ID);
        verify(emailAlertsConfigRepository, times(1)).get(knownBotConfig);
    }

    @Test
    public void whenGetEmailAlertsConfigCalledWithUnknownBotIdThenReturnNullEmailAlertsConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final EmailAlertsConfigService emailAlertsConfigService =
                new EmailAlertsConfigServiceImpl(emailAlertsConfigRepository, botConfigRepository);

        final EmailAlertsConfig emailAlertsConfig = emailAlertsConfigService.getEmailAlertsConfig(UNKNOWN_BOT_ID);
        assertThat(emailAlertsConfig == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenUpdateEmailAlertsConfigCalledWithKnownBotIdThenReturnEmailAlertsConfig() throws Exception {

        given(botConfigRepository.findById(BOT_ID)).willReturn(knownBotConfig);
        given(emailAlertsConfigRepository.save(knownBotConfig, emailAlertsConfig)).willReturn(emailAlertsConfig);

        final EmailAlertsConfigService emailAlertsConfigService =
                new EmailAlertsConfigServiceImpl(emailAlertsConfigRepository, botConfigRepository);

        final EmailAlertsConfig updatedConfig = emailAlertsConfigService.updateEmailAlertsConfig(BOT_ID, emailAlertsConfig);
        assertThat(updatedConfig.equals(emailAlertsConfig));

        verify(botConfigRepository, times(1)).findById(BOT_ID);
        verify(emailAlertsConfigRepository, times(1)).save(knownBotConfig, emailAlertsConfig);
    }

    @Test
    public void whenUpdateEmailAlertsConfigCalledWithUnknownBotIdThenReturnNullEmailAlertsConfig() throws Exception {

        given(botConfigRepository.findById(UNKNOWN_BOT_ID)).willReturn(null);

        final EmailAlertsConfigService emailAlertsConfigService =
                new EmailAlertsConfigServiceImpl(emailAlertsConfigRepository, botConfigRepository);

        final EmailAlertsConfig updatedConfig = emailAlertsConfigService.updateEmailAlertsConfig(UNKNOWN_BOT_ID, emailAlertsConfig);
        assertThat(updatedConfig == null);

        verify(botConfigRepository, times(1)).findById(UNKNOWN_BOT_ID);
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private static EmailAlertsConfig buildEmailAlertsConfig() {
        final EmailAlertsConfig emailAlertsConfig = new EmailAlertsConfig();
        final SmtpConfig smtpConfig = new SmtpConfig(HOST, TLS_PORT, ACCOUNT_USERNAME, ACCOUNT_PASSWORD, FROM_ADDRESS, TO_ADDRESS);
        emailAlertsConfig.setSmtpConfig(smtpConfig);
        emailAlertsConfig.setEnabled(ENABLED);
        return emailAlertsConfig;
    }
}
