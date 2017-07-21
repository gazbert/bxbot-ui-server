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

import com.gazbert.bxbot.ui.server.domain.exchange.AuthenticationConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.ExchangeAdapterConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.NetworkConfig;
import com.gazbert.bxbot.ui.server.services.ExchangeAdapterConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of the Exchange Adapter config service.
 * <p>
 * It will lookup the Exchange (Bot) id in the registry and route the request to the remote Bot.
 *
 * @author gazbert
 */
@Service("exchangeAdapterConfigService")
@Transactional
public class ExchangeAdapterConfigServiceImpl implements ExchangeAdapterConfigService {

    private static final Logger LOG = LogManager.getLogger();
    private final RestTemplate restTemplate;

    public ExchangeAdapterConfigServiceImpl(RestTemplateBuilder restTemplateBuilder) {

        // TODO - lookup user/pass for Bot from secure storage
        this.restTemplate = restTemplateBuilder.basicAuthorization("admin", "notSafeForProduction").build();
    }

    @Override
    public ExchangeAdapterConfig fetchExchangeAdapterConfigForBot(String id) {
        LOG.info(() -> "Fetching config for Exchange Adapter id: " + id);
        return getRemoteExchangeAdapterConfig(id);
    }

    /*
     * Stub for now.
     */
    private ExchangeAdapterConfig getExchangeAdapterConfig(String botId) {

        final Map<String, String> authItems = new HashMap<>();
        authItems.put("key", "my-api-key");
        authItems.put("secret", "my-secret");

        final AuthenticationConfig authenticationConfig = new AuthenticationConfig();
        authenticationConfig.setItems(authItems);

        final NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setConnectionTimeout(30);
        networkConfig.setNonFatalErrorHttpStatusCodes(Arrays.asList(522, 524, 525));
        networkConfig.setNonFatalErrorMessages(Arrays.asList("Connection reset", "Connection closed by peer",
                "Remote host closed connection during handshake"));

        final ExchangeAdapterConfig exchangeAdapterConfig = new ExchangeAdapterConfig();
        exchangeAdapterConfig.setName("Bitstamp");
        exchangeAdapterConfig.setClassName("com.gazbert.bxbot.exchanges.BitstampExchangeAdapter");
        exchangeAdapterConfig.setNetworkConfig(networkConfig);

        return exchangeAdapterConfig;
    }

    /*
     * Query remote bot.
     */
    private ExchangeAdapterConfig getRemoteExchangeAdapterConfig(String id) {

        // TODO - Lookup up Bot routing info based on id...

        // ...then call it...
        final ExchangeAdapterConfig config =
                restTemplate.getForObject("http://localhost:8081/api/config/exchange", ExchangeAdapterConfig.class);

        LOG.info(() -> "Response received from remote Bot: " + config);

        return config;
    }
}