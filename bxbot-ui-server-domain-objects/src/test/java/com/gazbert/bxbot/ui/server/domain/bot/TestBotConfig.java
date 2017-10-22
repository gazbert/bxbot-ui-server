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

package com.gazbert.bxbot.ui.server.domain.bot;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests BotConfig domain object behaves as expected.
 *
 * @author gazbert
 */
public class TestBotConfig {

    private static final String BOT_ID = "gdax-bot-1";
    private static final String BOT_NAME = "GDAX";
    private static final String BOT_BASE_URL = "https://localhost/api";
    private static final String BOT_USERNAME = "bxbot-ui-server-admin";
    private static final String BOT_PASSWORD = "aintGonnaTellYa!";

    @Test
    public void testInitialisationWorksAsExpected() {

        final BotConfig botConfig = new BotConfig();
        assertEquals(null, botConfig.getId());
        assertEquals(null, botConfig.getDisplayName());
        assertEquals(null, botConfig.getBaseUrl());
        assertEquals(null, botConfig.getUsername());
        assertEquals(null, botConfig.getPassword());
    }

    @Test
    public void testSettersWorkAsExpected() {

        final BotConfig botConfig = new BotConfig();

        botConfig.setId(BOT_ID);
        assertEquals(BOT_ID, botConfig.getId());

        botConfig.setDisplayName(BOT_NAME);
        assertEquals(BOT_NAME, botConfig.getDisplayName());

        botConfig.setBaseUrl(BOT_BASE_URL);
        assertEquals(BOT_BASE_URL, botConfig.getBaseUrl());

        botConfig.setUsername(BOT_USERNAME);
        assertEquals(BOT_USERNAME, botConfig.getUsername());

        botConfig.setPassword(BOT_PASSWORD);
        assertEquals(BOT_PASSWORD, botConfig.getPassword());
    }
}
