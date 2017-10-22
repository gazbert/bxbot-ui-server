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
import com.gazbert.bxbot.ui.server.domain.exchange.ExchangeConfig;
import com.gazbert.bxbot.ui.server.repository.remote.config.ExchangeConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestClientException;

/**
 * A REST client implementation of the remote Exchange config repository.
 *
 * @author gazbert
 */
@Repository("exchangeConfigRepository")
public class ExchangeConfigRepositoryRestClient extends AbstractConfigRepositoryRestClient implements ExchangeConfigRepository {

    private static final Logger LOG = LogManager.getLogger();
    private static final String EXCHANGE_RESOURCE_PATH = CONFIG_RESOURCE_PATH + "/exchange";

    public ExchangeConfigRepositoryRestClient(RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder);
    }

    @Override
    public ExchangeConfig get(BotConfig botConfig) {

        try {
            restTemplate.getInterceptors().clear();
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                    botConfig.getUsername(), botConfig.getPassword()));

            final String endpointUrl = botConfig.getBaseUrl() + EXCHANGE_RESOURCE_PATH;
            LOG.info(() -> "Fetching ExchangeConfig from: " + endpointUrl);

            final ExchangeConfig config = restTemplate.getForObject(endpointUrl, ExchangeConfig.class);

            LOG.info(() -> REMOTE_RESPONSE_RECEIVED_LOG_MSG + config);
            return config;

        } catch (RestClientException e) {
            LOG.error(FAILED_TO_INVOKE_REMOTE_BOT_LOG_MSG + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public ExchangeConfig save(BotConfig botConfig, ExchangeConfig exchangeConfig) {

        try {
            LOG.info(() -> "About to save ExchangeConfig: " + exchangeConfig);

            restTemplate.getInterceptors().clear();
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                    botConfig.getUsername(), botConfig.getPassword()));

            final String endpointUrl = botConfig.getBaseUrl() + EXCHANGE_RESOURCE_PATH;
            LOG.info(() -> "Sending ExchangeConfig to: " + endpointUrl);

            final HttpEntity<ExchangeConfig> requestUpdate = new HttpEntity<>(exchangeConfig);
            final ResponseEntity<ExchangeConfig> savedConfig = restTemplate.exchange(
                    endpointUrl, HttpMethod.PUT, requestUpdate, ExchangeConfig.class);

            LOG.info(() -> REMOTE_RESPONSE_RECEIVED_LOG_MSG + savedConfig);
            return savedConfig.getBody();

        } catch (RestClientException e) {
            LOG.error(FAILED_TO_INVOKE_REMOTE_BOT_LOG_MSG + e.getMessage(), e);
            return null;
        }
    }
}