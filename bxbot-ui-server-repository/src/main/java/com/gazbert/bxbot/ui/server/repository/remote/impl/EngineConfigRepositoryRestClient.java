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
import com.gazbert.bxbot.ui.server.domain.engine.EngineConfig;
import com.gazbert.bxbot.ui.server.repository.remote.EngineConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * A REST client implementation of the remote Engine config repository.
 *
 * @author gazbert
 */
@Repository("engineConfigRepository")
@Transactional
public class EngineConfigRepositoryRestClient implements EngineConfigRepository {

    private static final Logger LOG = LogManager.getLogger();
    private static final String REST_ENDPOINT_PATH = "/config/engine";

    private RestTemplate restTemplate;


    public EngineConfigRepositoryRestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public EngineConfig get(BotConfig botConfig) {

        try {
            restTemplate.getInterceptors().clear();
            restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                    botConfig.getUsername(), botConfig.getPassword()));

            final String endpointUrl = botConfig.getBaseUrl() + REST_ENDPOINT_PATH;
            LOG.info(() -> "Fetching EngineConfig from: " + endpointUrl);

            final EngineConfig config = restTemplate.getForObject(endpointUrl, EngineConfig.class);

            LOG.info(() -> "Response received from remote Bot: " + config);
            return config;

        } catch(RestClientException e) {
            LOG.error("Failed to invoke remote bot! Details: " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public EngineConfig save(BotConfig botConfig, EngineConfig engineConfig) {

        LOG.info(() -> "About to save EngineConfig: " + engineConfig);

        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                botConfig.getUsername(), botConfig.getPassword()));

        final String endpointUrl = botConfig.getBaseUrl() + REST_ENDPOINT_PATH;
        LOG.info(() -> "Sending EngineConfig to: " + endpointUrl);

        final HttpEntity<EngineConfig> requestUpdate = new HttpEntity<>(engineConfig);
        final ResponseEntity<EngineConfig> savedConfig  = restTemplate.exchange(
                endpointUrl, HttpMethod.PUT, requestUpdate, EngineConfig.class);

        LOG.info(() -> "Response received from remote Bot: " + savedConfig);
        return savedConfig.getBody();
    }
}