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
import com.gazbert.bxbot.ui.server.domain.exchange.AuthenticationConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.ExchangeConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.NetworkConfig;
import com.gazbert.bxbot.ui.server.repository.remote.ExchangeConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the remote Exchange config repository.
 *
 * @author gazbert
 */
@Repository("exchangeConfigRepository")
@Transactional
public class ExchangeConfigRepositoryRestClient implements ExchangeConfigRepository {

    private static final Logger LOG = LogManager.getLogger();

    private RestTemplateBuilder restTemplateBuilder;

    public ExchangeConfigRepositoryRestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
    }

    @Override
    public ExchangeConfig get(BotConfig botConfig) {
        return getRemoteExchangeAdapterConfig(botConfig);
    }

    @Override
    public ExchangeConfig save(ExchangeConfig config, BotConfig botConfig) {
        throw new UnsupportedOperationException("save() not implemented");
    }

    /*
     * Stub for now.
    */
    private ExchangeConfig getExchangeConfig() {

        final Map<String, String> authItems = new HashMap<>();
        authItems.put("key", "my-api-key");
        authItems.put("secret", "my-secret");

        final AuthenticationConfig authenticationConfig = new AuthenticationConfig();
        authenticationConfig.setItems(authItems);

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

    /*
     * Query remote bot.
     */
    private ExchangeConfig getRemoteExchangeAdapterConfig(BotConfig botConfig) {

        final RestTemplate restTemplate = restTemplateBuilder.basicAuthorization(
                botConfig.getUsername(), botConfig.getPassword()).build();

        final ExchangeConfig config = restTemplate.getForObject(botConfig.getBaseUrl() + "/config/exchange", ExchangeConfig.class);

        LOG.info(() -> "Response received from remote Bot: " + config);
        return config;
    }
}