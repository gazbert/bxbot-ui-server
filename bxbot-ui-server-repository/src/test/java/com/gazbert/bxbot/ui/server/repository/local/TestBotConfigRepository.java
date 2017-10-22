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

package com.gazbert.bxbot.ui.server.repository.local;

import com.gazbert.bxbot.ui.server.datastore.ConfigurationManager;
import com.gazbert.bxbot.ui.server.datastore.bots.generated.BotType;
import com.gazbert.bxbot.ui.server.datastore.bots.generated.BotsType;
import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.repository.local.impl.BotConfigRepositoryXmlDatastore;
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
import static org.easymock.EasyMock.*;

/**
 * Tests the Bot configuration repository behaves as expected.
 *
 * @author gazbert
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ConfigurationManager.class, BotConfigRepositoryXmlDatastore.class})
public class TestBotConfigRepository {

    // Mocked out methods
    private static final String MOCKED_GENERATE_UUID_METHOD = "generateUuid";

    private static final String UNKNOWN_BOT_ID = "unknown-or-new-bot-id";
    private static final String GENERATED_BOT_ID = "new-bot-id-123";

    private static final String BOT_1_ID = "bitstamp-bot-1";
    private static final String BOT_1_ALIAS = "Bitstamp Bot";
    private static final String BOT_1_BASE_URL = "https://hostname.one/api";
    private static final String BOT_1_USERNAME = "admin";
    private static final String BOT_1_PASSWORD = "password";

    private static final String BOT_2_ID = "gdax-bot-1";
    private static final String BOT_2_ALIAS = "GDAX Bot";
    private static final String BOT_2_BASE_URL = "https://hostname.two/api";
    private static final String BOT_2_USERNAME = "admin";
    private static final String BOT_2_PASSWORD = "password";

    private static final String NEW_BOT_NAME = "Gemini Bot";
    private static final String NEW_BOT_URL = "https://hostname.new/api";
    private static final String NEW_BOT_USERNAME = "admin";
    private static final String NEW_BOT_PASSWORD = "password";


    @Before
    public void setup() throws Exception {
        PowerMock.mockStatic(ConfigurationManager.class);
    }

    @Test
    public void whenFindAllCalledThenExpectRepositoryToReturnAllBotConfigs() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        PowerMock.replayAll();

        final BotConfigRepository botConfigRepository = new BotConfigRepositoryXmlDatastore();
        final List<BotConfig> botConfigItems = botConfigRepository.findAll();

        assertThat(botConfigItems.size()).isEqualTo(2);

        assertThat(botConfigItems.get(0).getId()).isEqualTo(BOT_1_ID);
        assertThat(botConfigItems.get(0).getAlias()).isEqualTo(BOT_1_ALIAS);
        assertThat(botConfigItems.get(0).getBaseUrl()).isEqualTo(BOT_1_BASE_URL);
        assertThat(botConfigItems.get(0).getUsername()).isEqualTo(BOT_1_USERNAME);
        assertThat(botConfigItems.get(0).getPassword()).isEqualTo(BOT_1_PASSWORD);

        assertThat(botConfigItems.get(1).getId()).isEqualTo(BOT_2_ID);
        assertThat(botConfigItems.get(1).getAlias()).isEqualTo(BOT_2_ALIAS);
        assertThat(botConfigItems.get(1).getBaseUrl()).isEqualTo(BOT_2_BASE_URL);
        assertThat(botConfigItems.get(1).getUsername()).isEqualTo(BOT_2_USERNAME);
        assertThat(botConfigItems.get(1).getPassword()).isEqualTo(BOT_2_PASSWORD);

        PowerMock.verifyAll();
    }

    @Test
    public void whenFindByIdCalledWithKnownIdThenReturnMatchingBotConfig() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        PowerMock.replayAll();

        final BotConfigRepository botConfigRepository = new BotConfigRepositoryXmlDatastore();
        final BotConfig botConfig = botConfigRepository.findById(BOT_1_ID);

        assertThat(botConfig.getId()).isEqualTo(BOT_1_ID);
        assertThat(botConfig.getAlias()).isEqualTo(BOT_1_ALIAS);
        assertThat(botConfig.getBaseUrl()).isEqualTo(BOT_1_BASE_URL);
        assertThat(botConfig.getUsername()).isEqualTo(BOT_1_USERNAME);
        assertThat(botConfig.getPassword()).isEqualTo(BOT_1_PASSWORD);

        PowerMock.verifyAll();
    }

    @Test
    public void whenFindByIdCalledWithUnknownIdThenReturnEmptyBotConfig() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        PowerMock.replayAll();

        final BotConfigRepository botConfigRepository = new BotConfigRepositoryXmlDatastore();
        final BotConfig botConfig = botConfigRepository.findById(UNKNOWN_BOT_ID);

        assertThat(botConfig.getId()).isEqualTo(null);
        assertThat(botConfig.getAlias()).isEqualTo(null);
        assertThat(botConfig.getBaseUrl()).isEqualTo(null);
        assertThat(botConfig.getUsername()).isEqualTo(null);
        assertThat(botConfig.getPassword()).isEqualTo(null);

        PowerMock.verifyAll();
    }

    @Test
    public void whenSaveCalledWithKnownIdThenExpectUpdatedBotConfigToBeReturned() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        ConfigurationManager.saveConfig(
                eq(BotsType.class),
                anyObject(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME));

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        PowerMock.replayAll();

        final BotConfigRepository botConfigRepository = new BotConfigRepositoryXmlDatastore();
        final BotConfig botConfig = botConfigRepository.save(someUpdatedExternalBotConfig());

        assertThat(botConfig.getId()).isEqualTo(BOT_1_ID);
        assertThat(botConfig.getAlias()).isEqualTo(BOT_1_ALIAS);
        assertThat(botConfig.getBaseUrl()).isEqualTo(BOT_1_BASE_URL);
        assertThat(botConfig.getUsername()).isEqualTo(BOT_1_USERNAME);
        assertThat(botConfig.getPassword()).isEqualTo(BOT_1_PASSWORD);

        PowerMock.verifyAll();
    }

    @Test
    public void whenSaveCalledWithEmptyIdThenExpectCreatedBotConfigToBeReturned() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        ConfigurationManager.saveConfig(
                eq(BotsType.class),
                anyObject(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME));

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfigPlusNewOne());

        final BotConfigRepository botConfigRepository = PowerMock.createPartialMock(
                BotConfigRepositoryXmlDatastore.class, MOCKED_GENERATE_UUID_METHOD);
        PowerMock.expectPrivate(botConfigRepository, MOCKED_GENERATE_UUID_METHOD).andReturn(GENERATED_BOT_ID);

        PowerMock.replayAll();

        final BotConfig botConfig = botConfigRepository.save(someNewExternalBotConfig());

        assertThat(botConfig.getId()).isNotEmpty(); // uuid has been generated
        assertThat(botConfig.getAlias()).isEqualTo(NEW_BOT_NAME);
        assertThat(botConfig.getBaseUrl()).isEqualTo(NEW_BOT_URL);
        assertThat(botConfig.getUsername()).isEqualTo(NEW_BOT_USERNAME);
        assertThat(botConfig.getPassword()).isEqualTo(NEW_BOT_PASSWORD);

        PowerMock.verifyAll();
    }

    @Test
    public void whenSaveCalledWithUnknownIdThenExpectEmptyBotConfigToBeReturned() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        PowerMock.replayAll();

        final BotConfigRepository botConfigRepository = new BotConfigRepositoryXmlDatastore();
        final BotConfig botConfig = botConfigRepository.save(someUpdatedExternalBotConfigWithUnknownId());

        assertThat(botConfig.getId()).isEqualTo(null);
        assertThat(botConfig.getAlias()).isEqualTo(null);
        assertThat(botConfig.getBaseUrl()).isEqualTo(null);
        assertThat(botConfig.getUsername()).isEqualTo(null);
        assertThat(botConfig.getPassword()).isEqualTo(null);

        PowerMock.verifyAll();
    }

    @Test
    public void whenDeleteCalledWithKnownIdThenReturnDeletedBotConfig() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        ConfigurationManager.saveConfig(
                eq(BotsType.class),
                anyObject(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME));

        PowerMock.replayAll();

        final BotConfigRepository botConfigRepository = new BotConfigRepositoryXmlDatastore();
        final BotConfig botConfig = botConfigRepository.delete(BOT_1_ID);

        assertThat(botConfig.getId()).isEqualTo(BOT_1_ID);
        assertThat(botConfig.getAlias()).isEqualTo(BOT_1_ALIAS);
        assertThat(botConfig.getBaseUrl()).isEqualTo(BOT_1_BASE_URL);
        assertThat(botConfig.getUsername()).isEqualTo(BOT_1_USERNAME);
        assertThat(botConfig.getPassword()).isEqualTo(BOT_1_PASSWORD);

        PowerMock.verifyAll();
    }

    @Test
    public void whenDeleteCalledWithUnknownIdThenReturnEmptyBotConfig() throws Exception {

        expect(ConfigurationManager.loadConfig(
                eq(BotsType.class),
                eq(BOTS_CONFIG_XML_FILENAME),
                eq(BOTS_CONFIG_XSD_FILENAME))).
                andReturn(allTheInternalBotsConfig());

        PowerMock.replayAll();

        final BotConfigRepository botConfigRepository = new BotConfigRepositoryXmlDatastore();
        final BotConfig botConfig = botConfigRepository.delete(UNKNOWN_BOT_ID);

        assertThat(botConfig.getId()).isEqualTo(null);
        assertThat(botConfig.getAlias()).isEqualTo(null);
        assertThat(botConfig.getBaseUrl()).isEqualTo(null);
        assertThat(botConfig.getUsername()).isEqualTo(null);
        assertThat(botConfig.getPassword()).isEqualTo(null);

        PowerMock.verifyAll();
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private static BotsType allTheInternalBotsConfig() {

        final BotType botType1 = new BotType();
        botType1.setId(BOT_1_ID);
        botType1.setAlias(BOT_1_ALIAS);
        botType1.setBaseUrl(BOT_1_BASE_URL);
        botType1.setUsername(BOT_1_USERNAME);
        botType1.setPassword(BOT_1_PASSWORD);

        final BotType botType2 = new BotType();
        botType2.setId(BOT_2_ID);
        botType2.setAlias(BOT_2_ALIAS);
        botType2.setBaseUrl(BOT_2_BASE_URL);
        botType2.setUsername(BOT_2_USERNAME);
        botType2.setPassword(BOT_2_PASSWORD);

        final BotsType botsType = new BotsType();
        botsType.getBots().add(botType1);
        botsType.getBots().add(botType2);
        return botsType;
    }

    private static BotsType allTheInternalBotsConfigPlusNewOne() {

        final BotType newBot = new BotType();
        newBot.setId(GENERATED_BOT_ID);
        newBot.setAlias(NEW_BOT_NAME);
        newBot.setBaseUrl(NEW_BOT_URL);
        newBot.setUsername(NEW_BOT_USERNAME);
        newBot.setPassword(NEW_BOT_PASSWORD);

        final BotsType existingBotsPlusNewOne = allTheInternalBotsConfig();
        existingBotsPlusNewOne.getBots().add(newBot);
        return existingBotsPlusNewOne;
    }

    private static BotConfig someNewExternalBotConfig() {
        return new BotConfig(null, NEW_BOT_NAME, NEW_BOT_URL, NEW_BOT_USERNAME, NEW_BOT_PASSWORD);
    }

    private static BotConfig someUpdatedExternalBotConfig() {
        return new BotConfig(BOT_1_ID, BOT_1_ALIAS + "_UPDATED", BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);
    }

    private static BotConfig someUpdatedExternalBotConfigWithUnknownId() {
        return new BotConfig(UNKNOWN_BOT_ID, BOT_1_ALIAS, BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);
    }
}
