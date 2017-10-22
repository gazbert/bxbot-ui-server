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

package com.gazbert.bxbot.ui.server.services.config.impl;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.market.MarketConfig;
import com.gazbert.bxbot.ui.server.repository.local.BotConfigRepository;
import com.gazbert.bxbot.ui.server.repository.remote.config.MarketConfigRepository;
import com.gazbert.bxbot.ui.server.services.config.MarketConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Market config service.
 *
 * @author gazbert
 */
@Service("marketConfigService")
@Transactional
@ComponentScan(basePackages = {"com.gazbert.bxbot.repository"})
public class MarketConfigServiceImpl implements MarketConfigService {

    private static final Logger LOG = LogManager.getLogger();

    private final MarketConfigRepository marketConfigRepository;
    private final BotConfigRepository botConfigRepository;

    @Autowired
    public MarketConfigServiceImpl(MarketConfigRepository marketConfigRepository,
                                   BotConfigRepository botConfigRepository) {

        this.marketConfigRepository = marketConfigRepository;
        this.botConfigRepository = botConfigRepository;
    }

    @Override
    public List<MarketConfig> getAllMarketConfig(String botId) {

        LOG.info(() -> "About to fetch all Markets for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return new ArrayList<>();
        } else {
            return marketConfigRepository.findAll(botConfig);
        }
    }

    @Override
    public MarketConfig getMarketConfig(String botId, String marketId) {

        LOG.info(() -> "Fetching Market config for marketId: " + marketId + " for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return null;
        } else {
            return marketConfigRepository.findById(botConfig, marketId);
        }
    }

    @Override
    public MarketConfig updateMarketConfig(String botId, MarketConfig marketConfig) {

        LOG.info(() -> "About to update Market config: " + marketConfig + " for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return null;
        } else {
            return marketConfigRepository.save(botConfig, marketConfig);
        }
    }

    @Override
    public MarketConfig createMarketConfig(String botId, MarketConfig marketConfig) {

        LOG.info(() -> "About to create Market config: " + marketConfig + " for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return null;
        } else {
            return marketConfigRepository.save(botConfig, marketConfig);
        }
    }

    @Override
    public boolean deleteMarketConfig(String botId, String marketId) {

        LOG.info(() -> "About to delete Market config for marketId: " + marketId + " for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return false;
        } else {
            return marketConfigRepository.delete(botConfig, marketId);
        }
    }
}