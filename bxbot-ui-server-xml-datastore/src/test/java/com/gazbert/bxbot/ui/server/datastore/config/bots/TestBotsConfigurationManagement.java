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

package com.gazbert.bxbot.ui.server.datastore.config.bots;

import com.gazbert.bxbot.ui.server.datastore.ConfigurationManager;
import com.gazbert.bxbot.ui.server.datastore.bots.generated.BotType;
import com.gazbert.bxbot.ui.server.datastore.bots.generated.BotsType;
import org.junit.Test;

import java.nio.file.FileSystems;
import java.nio.file.Files;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertEquals;

/**
 * Tests the Bots configuration is loaded as expected.
 *
 * @author gazbert
 */
public class TestBotsConfigurationManagement {

    /* Production XSD */
    private static final String XML_SCHEMA_FILENAME = "com/gazbert/bxbot/ui/server/datastore/config/bots.xsd";

    /* Test XML config */
    private static final String VALID_XML_CONFIG_FILENAME = "src/test/config/bots/valid-bots.xml";
    private static final String INVALID_XML_CONFIG_FILENAME = "src/test/config/bots/invalid-bots.xml";
    private static final String MISSING_XML_CONFIG_FILENAME = "src/test/config/bots/missing-bots.xml";
    private static final String XML_CONFIG_TO_SAVE_FILENAME = "src/test/config/bots/saved-bots.xml";

    private static final String BOT_1_ID = "bitstamp-bot-1";
    private static final String BOT_1_ALIAS = "Bitstamp Bot";
    private static final String BOT_1_BASE_URL = "https://hostname.one/api";
    private static final String BOT_1_USERNAME = "admin";
    private static final String BOT_1_PASSWORD = "password";

    private static final String BOT_2_ID = "gdax-bot-1";
    private static final String BOT_2_ALIAS = null; // optional
    private static final String BOT_2_BASE_URL = "https://hostname.two/api";
    private static final String BOT_2_USERNAME = "admin";
    private static final String BOT_2_PASSWORD = "password";


    @Test
    public void testLoadingValidXmlConfigFileIsSuccessful() {

        final BotsType botsType = ConfigurationManager.loadConfig(BotsType.class,
                VALID_XML_CONFIG_FILENAME, XML_SCHEMA_FILENAME);

        assertEquals(2, botsType.getBots().size());

        assertEquals(BOT_1_ID, botsType.getBots().get(0).getId());
        assertEquals(BOT_1_ALIAS, botsType.getBots().get(0).getAlias());
        assertEquals(BOT_1_BASE_URL, botsType.getBots().get(0).getBaseUrl());
        assertEquals(BOT_1_USERNAME, botsType.getBots().get(0).getUsername());
        assertEquals(BOT_1_PASSWORD, botsType.getBots().get(0).getPassword());

        assertEquals(BOT_2_ID, botsType.getBots().get(1).getId());
        assertEquals(BOT_2_ALIAS, botsType.getBots().get(1).getAlias());
        assertEquals(BOT_2_BASE_URL, botsType.getBots().get(1).getBaseUrl());
        assertEquals(BOT_2_USERNAME, botsType.getBots().get(1).getUsername());
        assertEquals(BOT_2_PASSWORD, botsType.getBots().get(1).getPassword());
    }

    @Test(expected = IllegalStateException.class)
    public void testLoadingMissingXmlConfigFileThrowsException() {
        ConfigurationManager.loadConfig(BotsType.class,
                MISSING_XML_CONFIG_FILENAME, XML_SCHEMA_FILENAME);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testLoadingInvalidXmlConfigFileThrowsException() {
        ConfigurationManager.loadConfig(BotsType.class,
                INVALID_XML_CONFIG_FILENAME, XML_SCHEMA_FILENAME);
    }

    @Test
    public void testSavingConfigToXmlIsSuccessful() throws Exception {

        final BotType bot1 = new BotType();
        bot1.setId(BOT_1_ID);
        bot1.setAlias(BOT_1_ALIAS);
        bot1.setBaseUrl(BOT_1_BASE_URL);
        bot1.setUsername(BOT_1_USERNAME);
        bot1.setPassword(BOT_1_PASSWORD);

        final BotType bot2 = new BotType();
        bot2.setId(BOT_2_ID);
        bot2.setAlias(BOT_2_ALIAS);
        bot2.setBaseUrl(BOT_2_BASE_URL);
        bot2.setUsername(BOT_2_USERNAME);
        bot2.setPassword(BOT_2_PASSWORD);

        final BotsType botsConfig = new BotsType();
        botsConfig.getBots().add(bot1);
        botsConfig.getBots().add(bot2);

        ConfigurationManager.saveConfig(BotsType.class, botsConfig, XML_CONFIG_TO_SAVE_FILENAME);

        // Read it back in
        final BotsType botsReloaded = ConfigurationManager.loadConfig(BotsType.class,
                XML_CONFIG_TO_SAVE_FILENAME, XML_SCHEMA_FILENAME);

        assertThat(botsReloaded.getBots().get(0).getId()).isEqualTo(BOT_1_ID);
        assertThat(botsReloaded.getBots().get(0).getAlias()).isEqualTo(BOT_1_ALIAS);
        assertThat(botsReloaded.getBots().get(0).getBaseUrl()).isEqualTo(BOT_1_BASE_URL);
        assertThat(botsReloaded.getBots().get(0).getUsername()).isEqualTo(BOT_1_USERNAME);
        assertThat(botsReloaded.getBots().get(0).getPassword()).isEqualTo(BOT_1_PASSWORD);

        assertThat(botsReloaded.getBots().get(1).getId()).isEqualTo(BOT_2_ID);
        assertThat(botsReloaded.getBots().get(1).getAlias()).isEqualTo(BOT_2_ALIAS);
        assertThat(botsReloaded.getBots().get(1).getBaseUrl()).isEqualTo(BOT_2_BASE_URL);
        assertThat(botsReloaded.getBots().get(1).getUsername()).isEqualTo(BOT_2_USERNAME);
        assertThat(botsReloaded.getBots().get(1).getPassword()).isEqualTo(BOT_2_PASSWORD);

        // cleanup
        Files.delete(FileSystems.getDefault().getPath(XML_CONFIG_TO_SAVE_FILENAME));
    }
}
