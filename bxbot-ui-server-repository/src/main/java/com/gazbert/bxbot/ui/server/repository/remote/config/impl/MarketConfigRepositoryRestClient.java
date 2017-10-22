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

package com.gazbert.bxbot.ui.server.repository.remote.config.impl;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.market.MarketConfig;
import com.gazbert.bxbot.ui.server.repository.remote.config.MarketConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of the remote Market config repository.
 *
 * @author gazbert
 */
@Repository("marketConfigRepository")
public class MarketConfigRepositoryRestClient extends AbstractConfigRepositoryRestClient implements MarketConfigRepository {

    private static final Logger LOG = LogManager.getLogger();
    private static final String MARKET_RESOURCE_PATH = CONFIG_RESOURCE_PATH + "/markets";

    public MarketConfigRepositoryRestClient(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    public List<MarketConfig> findAll(BotConfig botConfig) {

        try {
            restTemplate.getInterceptors().clear();
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                    botConfig.getUsername(), botConfig.getPassword()));

            final String endpointUrl = botConfig.getBaseUrl() + MARKET_RESOURCE_PATH;
            LOG.info(() -> "Fetching all MarketConfig from: " + endpointUrl);

            @SuppressWarnings("unchecked") final List<MarketConfig> allTheMarketConfig = restTemplate.getForObject(endpointUrl, List.class);

            LOG.info(() -> REMOTE_RESPONSE_RECEIVED_LOG_MSG + allTheMarketConfig);
            return allTheMarketConfig;

        } catch (RestClientException e) {
            LOG.error(FAILED_TO_INVOKE_REMOTE_BOT_LOG_MSG + e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    @Override
    public MarketConfig findById(BotConfig botConfig, String marketId) {

        try {
            restTemplate.getInterceptors().clear();
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                    botConfig.getUsername(), botConfig.getPassword()));

            final String endpointUrl = botConfig.getBaseUrl() + MARKET_RESOURCE_PATH + '/' + marketId;
            LOG.info(() -> "Fetching MarketConfig from: " + endpointUrl);

            @SuppressWarnings("unchecked") final MarketConfig marketConfig = restTemplate.getForObject(endpointUrl, MarketConfig.class);

            LOG.info(() -> REMOTE_RESPONSE_RECEIVED_LOG_MSG + marketConfig);
            return marketConfig;

        } catch (RestClientException e) {
            LOG.error(FAILED_TO_INVOKE_REMOTE_BOT_LOG_MSG + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public MarketConfig save(BotConfig botConfig, MarketConfig marketConfig) {

        LOG.info(() -> "Saving MarketConfig: " + marketConfig + " for botId: " + botConfig.getId());

        try {
            restTemplate.getInterceptors().clear();
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                    botConfig.getUsername(), botConfig.getPassword()));

            final String endpointUrl = botConfig.getBaseUrl() + MARKET_RESOURCE_PATH;
            LOG.info(() -> "Sending MarketConfig to: " + endpointUrl);

            final HttpEntity<MarketConfig> requestUpdate = new HttpEntity<>(marketConfig);
            final ResponseEntity<MarketConfig> savedConfig = restTemplate.exchange(
                    endpointUrl, HttpMethod.PUT, requestUpdate, MarketConfig.class);

            LOG.info(() -> REMOTE_RESPONSE_RECEIVED_LOG_MSG + savedConfig.getBody());
            return savedConfig.getBody();

        } catch (RestClientException e) {
            LOG.error(FAILED_TO_INVOKE_REMOTE_BOT_LOG_MSG + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean delete(BotConfig botConfig, String marketId) {

        LOG.info(() -> "Deleting MarketConfig for marketId: " + marketId + " for botId: " + botConfig.getId());

        try {
            restTemplate.getInterceptors().clear();
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                    botConfig.getUsername(), botConfig.getPassword()));

            final String endpointUrl = botConfig.getBaseUrl() + MARKET_RESOURCE_PATH + '/' + marketId;
            LOG.info(() -> "Deleting MarketConfig from: " + endpointUrl);

            restTemplate.delete(endpointUrl);
            return true;

        } catch (RestClientException e) {
            LOG.error(FAILED_TO_INVOKE_REMOTE_BOT_LOG_MSG + e.getMessage(), e);
            return false;
        }
    }
}