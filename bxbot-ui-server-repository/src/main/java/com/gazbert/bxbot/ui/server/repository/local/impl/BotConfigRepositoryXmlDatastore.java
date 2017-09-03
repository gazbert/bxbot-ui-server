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

package com.gazbert.bxbot.ui.server.repository.local.impl;

import com.gazbert.bxbot.ui.server.datastore.ConfigurationManager;
import com.gazbert.bxbot.ui.server.datastore.FileLocations;
import com.gazbert.bxbot.ui.server.datastore.bots.generated.BotType;
import com.gazbert.bxbot.ui.server.datastore.bots.generated.BotsType;
import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.repository.local.BotConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementation of the Bot config repository.
 *
 * @author gazbert
 */
@Repository("botConfigRepository")
@Transactional
public class BotConfigRepositoryXmlDatastore implements BotConfigRepository {

    private static final Logger LOG = LogManager.getLogger();

    @Override
    public List<BotConfig> findAll() {

        final BotsType internalBotsConfig = ConfigurationManager.loadConfig(BotsType.class,
                FileLocations.BOTS_CONFIG_XML_FILENAME, FileLocations.BOTS_CONFIG_XSD_FILENAME);
        return adaptAllInternalToAllExternalConfig(internalBotsConfig);
    }

    @Override
    public BotConfig findById(String id) {

        LOG.info(() -> "Fetching Bot config for id: " + id);

        final BotsType internalBotsConfig = ConfigurationManager.loadConfig(BotsType.class,
                FileLocations.BOTS_CONFIG_XML_FILENAME, FileLocations.BOTS_CONFIG_XSD_FILENAME);

        return adaptInternalToExternalConfig(
                internalBotsConfig.getBots()
                        .stream()
                        .filter((item) -> item.getId().equals(id))
                        .distinct()
                        .collect(Collectors.toList()));
    }

    @Override
    public BotConfig save(BotConfig config) {

        if (config.getId() == null || config.getId().isEmpty()) {

            LOG.info(() -> "About to create Bot config: " + config);

            final BotsType internalBotsConfig = ConfigurationManager.loadConfig(BotsType.class,
                    FileLocations.BOTS_CONFIG_XML_FILENAME, FileLocations.BOTS_CONFIG_XSD_FILENAME);

            final List<BotType> botTypes = internalBotsConfig.getBots()
                    .stream()
                    .filter((item) -> item.getId().equals(config.getId()))
                    .distinct()
                    .collect(Collectors.toList());

            if (botTypes.isEmpty()) {

                final BotConfig newBotConfig = new BotConfig(config);
                newBotConfig.setId(generateUuid());

                internalBotsConfig.getBots().add(adaptExternalToInternalConfig(newBotConfig));
                ConfigurationManager.saveConfig(BotsType.class, internalBotsConfig,
                        FileLocations.BOTS_CONFIG_XML_FILENAME);

                final BotsType updatedInternalBotsConfig = ConfigurationManager.loadConfig(
                        BotsType.class, FileLocations.BOTS_CONFIG_XML_FILENAME, FileLocations.BOTS_CONFIG_XSD_FILENAME);

                return adaptInternalToExternalConfig(
                        updatedInternalBotsConfig.getBots()
                        .stream()
                        .filter((item) -> item.getId().equals(newBotConfig.getId()))
                        .distinct()
                        .collect(Collectors.toList()));
            } else {
                throw new IllegalStateException("Trying to create new BotConfig but null/empty id already exists. " +
                        "BotConfig: " + config + " Existing BotConfigs: "
                        + adaptAllInternalToAllExternalConfig(internalBotsConfig));
            }

        } else {

            LOG.info(() -> "About to update Bot Config: " + config);

            final BotsType internalBotsConfig = ConfigurationManager.loadConfig(BotsType.class,
                    FileLocations.BOTS_CONFIG_XML_FILENAME, FileLocations.BOTS_CONFIG_XSD_FILENAME);

            final List<BotType> botTypes = internalBotsConfig.getBots()
                    .stream()
                    .filter((item) -> item.getId().equals(config.getId()))
                    .distinct()
                    .collect(Collectors.toList());

            if (!botTypes.isEmpty()) {

                internalBotsConfig.getBots().remove(botTypes.get(0)); // will only be 1 unique bot
                internalBotsConfig.getBots().add(adaptExternalToInternalConfig(config));
                ConfigurationManager.saveConfig(BotsType.class, internalBotsConfig,
                        FileLocations.BOTS_CONFIG_XML_FILENAME);

                final BotsType updatedInternalBotsConfig = ConfigurationManager.loadConfig(
                        BotsType.class, FileLocations.BOTS_CONFIG_XML_FILENAME, FileLocations.BOTS_CONFIG_XSD_FILENAME);

                return adaptInternalToExternalConfig(
                        updatedInternalBotsConfig.getBots()
                                .stream()
                                .filter((item) -> item.getId().equals(config.getId()))
                                .distinct()
                                .collect(Collectors.toList()));
            } else {
                LOG.warn("Trying to update BotConfig but id does not exist BotConfig: " + config +
                        " Existing BotConfigs: " + adaptAllInternalToAllExternalConfig(internalBotsConfig));
                return new BotConfig();
            }
        }
    }

    @Override
    public BotConfig delete(String id) {

        LOG.info(() -> "Deleting Bot config for id: " + id);

        final BotsType internalBotsConfig = ConfigurationManager.loadConfig(BotsType.class,
                FileLocations.BOTS_CONFIG_XML_FILENAME, FileLocations.BOTS_CONFIG_XSD_FILENAME);

        final List<BotType> botTypes = internalBotsConfig.getBots()
                .stream()
                .filter((item) -> item.getId().equals(id))
                .distinct()
                .collect(Collectors.toList());

        if (!botTypes.isEmpty()) {

            final BotType botToRemove = botTypes.get(0); // will only be 1 unique strat
            internalBotsConfig.getBots().remove(botToRemove);
            ConfigurationManager.saveConfig(BotsType.class, internalBotsConfig,
                    FileLocations.BOTS_CONFIG_XML_FILENAME);

            return adaptInternalToExternalConfig(Collections.singletonList(botToRemove));
        } else {
            LOG.warn("Trying to delete BotConfig but id does not exist. BotConfig id: " + id + " Existing BotConfigs: "
                    + adaptAllInternalToAllExternalConfig(internalBotsConfig));
            return new BotConfig();
        }
    }

    // ------------------------------------------------------------------------------------------------
    // Adapter methods
    // ------------------------------------------------------------------------------------------------

    private static List<BotConfig> adaptAllInternalToAllExternalConfig(BotsType internalBotsConfig) {

        final List<BotConfig> botConfigItems = new ArrayList<>();

        final List<BotType> internalBotConfig = internalBotsConfig.getBots();
        internalBotConfig.forEach((item) -> {
            final BotConfig botConfig = new BotConfig();
            botConfig.setId(item.getId());
            botConfig.setName(item.getName());
            botConfig.setStatus(item.getStatus());
            botConfig.setBaseUrl(item.getBaseUrl());
            botConfig.setUsername(item.getUsername());
            botConfig.setPassword(item.getPassword());
            botConfigItems.add(botConfig);
        });
        return botConfigItems;
    }

    private static BotConfig adaptInternalToExternalConfig(List<BotType> internalBotsConfigItems) {

        final BotConfig botConfig = new BotConfig();

        if (!internalBotsConfigItems.isEmpty()) {

            // Should only ever be 1 unique Bot id
            final BotType internalBotConfig = internalBotsConfigItems.get(0);
            botConfig.setId(internalBotConfig.getId());
            botConfig.setName(internalBotConfig.getName());
            botConfig.setStatus(internalBotConfig.getStatus());
            botConfig.setBaseUrl(internalBotConfig.getBaseUrl());
            botConfig.setUsername(internalBotConfig.getUsername());
            botConfig.setPassword(internalBotConfig.getPassword());
        }
        return botConfig;
    }

    private static BotType adaptExternalToInternalConfig(BotConfig externalBotConfig) {

        final BotType botType = new BotType();
        botType.setId(externalBotConfig.getId());
        botType.setName(externalBotConfig.getName());
        botType.setStatus(externalBotConfig.getStatus());
        botType.setBaseUrl(externalBotConfig.getBaseUrl());
        botType.setUsername(externalBotConfig.getUsername());
        botType.setPassword(externalBotConfig.getPassword());
        return botType;
    }

    // ------------------------------------------------------------------------------------------------
    // Util methods
    // ------------------------------------------------------------------------------------------------

    private String generateUuid() {
        return UUID.randomUUID().toString();
    }
}
