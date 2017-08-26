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
import com.gazbert.bxbot.ui.server.datastore.FileLocations;
import com.gazbert.bxbot.ui.server.datastore.bots.generated.BotType;
import com.gazbert.bxbot.ui.server.datastore.bots.generated.BotsType;
import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.repository.BotConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the Bot config repository.
 *
 * @author gazbert
 */
@Repository("botConfigRepository")
@Transactional
public class BotConfigRepositoryXmlImpl implements BotConfigRepository {

    private static final Logger LOG = LogManager.getLogger();

    @Override
    public List<BotConfig> findAllBots() {

        final BotsType internalBotsConfig = ConfigurationManager.loadConfig(BotsType.class,
                FileLocations.BOTS_CONFIG_XML_FILENAME, FileLocations.BOTS_CONFIG_XSD_FILENAME);
        return adaptAllInternalToAllExternalConfig(internalBotsConfig);
    }

    @Override
    public BotConfig findById(String id) {

        LOG.info(() -> "Fetching config for Bot id: " + id);

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
    public BotConfig updateBot(BotConfig config) {

        LOG.info(() -> "About to update: " + config);

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
            // no matching id :-(
            return new BotConfig();
        }
    }

    @Override
    public BotConfig createBot(BotConfig config) {

        throw new UnsupportedOperationException("createBot() not implemented yet!");

//        final TradingStrategiesType internalStrategiesConfig = ConfigurationManager.loadConfig(TradingStrategiesType.class,
//                FileLocations.STRATEGIES_CONFIG_XML_FILENAME, FileLocations.STRATEGIES_CONFIG_XSD_FILENAME);
//
//        final List<StrategyType> strategyTypes = internalStrategiesConfig.getStrategies()
//                .stream()
//                .filter((item) -> item.getId().equals(config.getId()))
//                .distinct()
//                .collect(Collectors.toList());
//
//        if (strategyTypes.isEmpty()) {
//
//            internalStrategiesConfig.getStrategies().add(adaptExternalToInternalConfig(config));
//            ConfigurationManager.saveConfig(TradingStrategiesType.class, internalStrategiesConfig,
//                    FileLocations.STRATEGIES_CONFIG_XML_FILENAME);
//
//            final TradingStrategiesType updatedInternalStrategiesConfig = ConfigurationManager.loadConfig(
//                    TradingStrategiesType.class, FileLocations.STRATEGIES_CONFIG_XML_FILENAME, FileLocations.STRATEGIES_CONFIG_XSD_FILENAME);
//
//            return adaptInternalToExternalConfig(
//                    updatedInternalStrategiesConfig.getStrategies()
//                            .stream()
//                            .filter((item) -> item.getId().equals(config.getId()))
//                            .distinct()
//                            .collect(Collectors.toList()));
//        } else {
//            // already have a matching id :-(
//            return new StrategyConfig();
//        }
    }

    @Override
    public BotConfig deleteBotById(String id) {

        throw new UnsupportedOperationException("deleteBotById() not implemented yet!");

//        LOG.info(() -> "Deleting config for Strategy id: " + id);
//
//        final TradingStrategiesType internalStrategiesConfig = ConfigurationManager.loadConfig(TradingStrategiesType.class,
//                FileLocations.STRATEGIES_CONFIG_XML_FILENAME, FileLocations.STRATEGIES_CONFIG_XSD_FILENAME);
//
//        final List<StrategyType> strategyTypes = internalStrategiesConfig.getStrategies()
//                .stream()
//                .filter((item) -> item.getId().equals(id))
//                .distinct()
//                .collect(Collectors.toList());
//
//        if (!strategyTypes.isEmpty()) {
//
//            final StrategyType strategyToRemove = strategyTypes.get(0); // will only be 1 unique strat
//            internalStrategiesConfig.getStrategies().remove(strategyToRemove);
//            ConfigurationManager.saveConfig(TradingStrategiesType.class, internalStrategiesConfig,
//                    FileLocations.STRATEGIES_CONFIG_XML_FILENAME);
//
//            return adaptInternalToExternalConfig(Collections.singletonList(strategyToRemove));
//        } else {
//            // no matching id :-(
//            return new StrategyConfig();
//        }
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
            botConfig.setUrl(item.getUrl());
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
            botConfig.setUrl(internalBotConfig.getUrl());
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
        botType.setUsername(externalBotConfig.getUsername());
        botType.setPassword(externalBotConfig.getPassword());
        return botType;
    }
}
