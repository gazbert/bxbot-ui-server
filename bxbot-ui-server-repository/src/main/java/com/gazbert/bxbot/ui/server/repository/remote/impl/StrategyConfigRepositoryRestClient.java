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

package com.gazbert.bxbot.ui.server.repository.remote.impl;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.strategy.StrategyConfig;
import com.gazbert.bxbot.ui.server.repository.remote.StrategyConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * Implementation of the remote Strategy config repository.
 *
 * @author gazbert
 */
@Repository("strategyConfigRepository")
@Transactional
public class StrategyConfigRepositoryRestClient implements StrategyConfigRepository {

    private static final Logger LOG = LogManager.getLogger();
    private static final String REST_ENDPOINT_PATH = "/config/strategies";

    private RestTemplate restTemplate;


    public StrategyConfigRepositoryRestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public List<StrategyConfig> findAll(BotConfig botConfig) {

        LOG.info(() -> "Fetching all StrategyConfig for botId: " + botConfig.getId());

        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                botConfig.getUsername(), botConfig.getPassword()));

        @SuppressWarnings("unchecked")
        final List<StrategyConfig> allTheStrategyConfig = restTemplate.getForObject(
                botConfig.getBaseUrl() + REST_ENDPOINT_PATH, List.class);

        LOG.info(() -> "Response received from remote Bot: " + allTheStrategyConfig);
        return allTheStrategyConfig;
    }

    @Override
    public StrategyConfig findById(String id) {
        throw new UnsupportedOperationException("findById() not implemented");
    }

    @Override
    public StrategyConfig save(StrategyConfig config) {
        throw new UnsupportedOperationException("save() not implemented");
    }

    @Override
    public StrategyConfig delete(String id) {
        throw new UnsupportedOperationException("delete() not implemented");
    }
}