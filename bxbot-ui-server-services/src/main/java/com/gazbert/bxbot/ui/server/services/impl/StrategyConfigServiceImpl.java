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

package com.gazbert.bxbot.ui.server.services.impl;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.strategy.StrategyConfig;
import com.gazbert.bxbot.ui.server.repository.local.BotConfigRepository;
import com.gazbert.bxbot.ui.server.repository.remote.StrategyConfigRepository;
import com.gazbert.bxbot.ui.server.services.StrategyConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Strategy config service.
 *
 * @author gazbert
 */
@Service("strategyConfigService")
@Transactional
@ComponentScan(basePackages = {"com.gazbert.bxbot.repository"})
public class StrategyConfigServiceImpl implements StrategyConfigService {

    private static final Logger LOG = LogManager.getLogger();

    private final StrategyConfigRepository strategyConfigRepository;
    private final BotConfigRepository botConfigRepository;

    @Autowired
    public StrategyConfigServiceImpl(StrategyConfigRepository strategyConfigRepository,
                                     BotConfigRepository botConfigRepository) {

        this.strategyConfigRepository = strategyConfigRepository;
        this.botConfigRepository = botConfigRepository;
    }

    @Override
    public List<StrategyConfig> getAllStrategyConfig(String botId) {

        LOG.info(() -> "About to fetch all Strategies for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return new ArrayList<>();
        } else {
            return strategyConfigRepository.findAll(botConfig);
        }
    }

    @Override
    public StrategyConfig getStrategyConfig(String botId, String strategyId) {

        LOG.info(() -> "Fetching Strategy config for strategyId: " + strategyId + " for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return null;
        } else {
            return strategyConfigRepository.findById(botConfig, strategyId);
        }
    }

    @Override
    public StrategyConfig updateStrategyConfig(String botId, StrategyConfig strategyConfig) {

        LOG.info(() -> "About to update Strategy config: " + strategyConfig + " for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return null;
        } else {
            return strategyConfigRepository.save(botConfig, strategyConfig);
        }
    }

    @Override
    public StrategyConfig createStrategyConfig(String botId, StrategyConfig strategyConfig) {

        LOG.info(() -> "About to create Strategy config: " + strategyConfig + " for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return null;
        } else {
            return strategyConfigRepository.save(botConfig, strategyConfig);
        }
    }

    @Override
    public boolean deleteStrategyConfig(String botId, String strategyId) {

        LOG.info(() -> "About to delete Strategy config for strategyId: " + strategyId + " for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return false;
        } else {
            return strategyConfigRepository.delete(botConfig, strategyId);
        }
    }
}