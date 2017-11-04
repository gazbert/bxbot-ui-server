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

package com.gazbert.bxbot.ui.server.services.runtime.impl;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.bot.BotStatus;
import com.gazbert.bxbot.ui.server.repository.local.BotConfigRepository;
import com.gazbert.bxbot.ui.server.repository.remote.runtime.BotStatusRepository;
import com.gazbert.bxbot.ui.server.services.runtime.BotStatusService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the Bot process service.
 *
 * @author gazbert
 */
@Service("botStatusService")
@Transactional
@ComponentScan(basePackages = {"com.gazbert.bxbot.ui.server.repository"})
public class BotStatusServiceImpl implements BotStatusService {

    private static final Logger LOG = LogManager.getLogger();

    private final BotStatusRepository botProcessRepository;
    private final BotConfigRepository botConfigRepository;

    @Autowired
    public BotStatusServiceImpl(BotStatusRepository botProcessRepository,
                                BotConfigRepository botConfigRepository) {

        this.botProcessRepository = botProcessRepository;
        this.botConfigRepository = botConfigRepository;
    }

    @Override
    public BotStatus getBotStatus(String botId) {

        LOG.info(() -> "About to fetch BotStatus for botId: " + botId);

        final BotConfig botConfig = botConfigRepository.findById(botId);
        if (botConfig == null) {
            LOG.warn("Failed to find BotConfig for botId: " + botId);
            return null;
        } else {

            BotStatus botStatus = botProcessRepository.getBotStatus(botConfig);
            if (botStatus == null) {
                botStatus = new BotStatus();
                botStatus.setId(botId);
                botStatus.setStatus("stopped"); // TODO use enum at some point...
            }
            return botStatus;
        }
    }

    @Override
    public List<BotStatus> getAllBotStatus() {

        LOG.info(() -> "About to fetch BotStatus for all bots...");

        final List<BotStatus> allBotStatus = new ArrayList<>();

        final List<BotConfig> allBotConfigs = botConfigRepository.findAll();
        for (final BotConfig botConfig : allBotConfigs) {

            BotStatus botStatus = botProcessRepository.getBotStatus(botConfig);
            if (botStatus == null) {
                botStatus = new BotStatus();
                botStatus.setId(botConfig.getId());
                botStatus.setStatus("stopped"); // TODO use enum at some point...
            }
            allBotStatus.add(botStatus);
        }
        return allBotStatus;
    }
}