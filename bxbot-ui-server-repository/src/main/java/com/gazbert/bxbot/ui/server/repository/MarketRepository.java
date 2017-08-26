package com.gazbert.bxbot.ui.server.repository;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;

public interface MarketRepository {

    BotConfig getMarketConfig(String id);
}
