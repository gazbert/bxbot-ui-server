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
import com.gazbert.bxbot.ui.server.domain.exchange.ExchangeConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.NetworkConfig;
import com.gazbert.bxbot.ui.server.repository.remote.ExchangeConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * A REST client implementation of the remote Exchange config repository.
 *
 * @author gazbert
 */
@Repository("exchangeConfigRepository")
@Transactional
public class ExchangeConfigRepositoryRestClient implements ExchangeConfigRepository {

    private static final Logger LOG = LogManager.getLogger();
    private static final String REST_ENDPOINT_PATH = "/config/exchange";

    private RestTemplate restTemplate;


    public ExchangeConfigRepositoryRestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public ExchangeConfig get(BotConfig botConfig) {

        LOG.info(() -> "Fetching ExchangeConfig...");

        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                botConfig.getUsername(), botConfig.getPassword()));

        final ExchangeConfig config = restTemplate.getForObject(
                botConfig.getBaseUrl() + REST_ENDPOINT_PATH, ExchangeConfig.class);

        LOG.info(() -> "Response received from remote Bot: " + config);
        return config;
    }

    @Override
    public ExchangeConfig save(BotConfig botConfig, ExchangeConfig exchangeConfig) {

        LOG.info(() -> "About to save ExchangeConfig: " + exchangeConfig);

        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                botConfig.getUsername(), botConfig.getPassword()));

        final HttpEntity<ExchangeConfig> requestUpdate = new HttpEntity<>(exchangeConfig);
        final ResponseEntity<ExchangeConfig> savedConfig  = restTemplate.exchange(
                botConfig.getBaseUrl() + REST_ENDPOINT_PATH, HttpMethod.PUT, requestUpdate, ExchangeConfig.class);

        LOG.info(() -> "Response received from remote Bot: " + savedConfig);
        return savedConfig.getBody();
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    /*
     * Tmp stub for testing.
     */
    private ExchangeConfig getExchangeConfig() {

        final NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setConnectionTimeout(30);
        networkConfig.setNonFatalErrorCodes(Arrays.asList(522, 524, 525));
        networkConfig.setNonFatalErrorMessages(Arrays.asList("Connection reset", "Connection closed by peer",
                "Remote host closed connection during handshake"));

        final ExchangeConfig exchangeConfig = new ExchangeConfig();
        exchangeConfig.setExchangeName("Bitstamp");
        exchangeConfig.setExchangeAdapter("com.gazbert.bxbot.exchanges.BitstampExchangeAdapter");
        exchangeConfig.setNetworkConfig(networkConfig);

        return exchangeConfig;
    }
}