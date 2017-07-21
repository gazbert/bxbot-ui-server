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

import com.gazbert.bxbot.ui.server.domain.exchange.ExchangeAdapterConfig;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import com.gazbert.bxbot.ui.server.services.ExchangeAdapterConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for directing Exchange Adapter config requests.
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api")
public class ExchangeAdapterController {

    private final ExchangeAdapterConfigService exchangeAdapterConfigService;

    public ExchangeAdapterController(ExchangeAdapterConfigService exchangeAdapterConfigService) {
        this.exchangeAdapterConfigService = exchangeAdapterConfigService;
    }

    /**
     * Returns the Exchange Adapter Details for a Exchange id. The Exchange id is the same as the Bot id, given the
     * 1:1 relationship between an Exchange and a Bot: a Bot can only run 1 Exchange.
     *
     * @param user the authenticated user.
     * @param id   the id of the Exchange (Bot) to fetch the Exchange Adapter details for.
     * @return the Exchange Adapter Details configuration.
     */
    @RequestMapping(value = "/exchange_adapters/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getExchange(@AuthenticationPrincipal User user, @PathVariable String id) {

        final ExchangeAdapterConfig exchangeAdapterConfig = exchangeAdapterConfigService.fetchExchangeAdapterConfigForBot(id);
        return exchangeAdapterConfig != null
                ? new ResponseEntity<>(new ResponseDataWrapper(exchangeAdapterConfig), null, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

