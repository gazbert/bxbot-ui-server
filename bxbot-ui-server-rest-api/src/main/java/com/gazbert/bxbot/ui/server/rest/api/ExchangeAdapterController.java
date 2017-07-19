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

package com.gazbert.bxbot.ui.server.rest.api;

import com.gazbert.bxbot.ui.server.domain.exchange.AuthenticationConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.ExchangeAdapterConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.NetworkConfig;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * Controller for directing Exchange Adapter config requests.
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api")
public class ExchangeAdapterController {

    /**
     * Returns the Exchange Adapter Details for a given Bot id.
     *
     * @param user the authenticated user.
     * @param id the id of the Bot to fetch the Exchange Adapter details for.
     * @return the Exchange Adapter Details configuration.
     */
    @RequestMapping(value = "/exchange_adapters/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getExchange(@AuthenticationPrincipal User user, @PathVariable String id) {

        final ExchangeAdapterConfig exchangeAdapterConfig = getExchangeAdapterConfig(id);
        return exchangeAdapterConfig != null
                ? new ResponseEntity<>(new ResponseDataWrapper(exchangeAdapterConfig), null, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

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
        networkConfig.setNonFatalErrorMessages(Arrays.asList("Connection reset", "Connection reset",
                "Remote host closed connection during handshake"));

        final ExchangeAdapterConfig exchangeAdapterConfig = new ExchangeAdapterConfig();
        exchangeAdapterConfig.setName("Bitstamp");
        exchangeAdapterConfig.setExchangeAdapter("com.gazbert.bxbot.exchanges.BitstampExchangeAdapter");
        exchangeAdapterConfig.setAuthenticationConfig(authenticationConfig);
        exchangeAdapterConfig.setNetworkConfig(networkConfig);

        return exchangeAdapterConfig;
    }
}

