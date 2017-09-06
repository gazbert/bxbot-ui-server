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

import com.gazbert.bxbot.ui.server.domain.exchange.ExchangeConfig;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import com.gazbert.bxbot.ui.server.services.ExchangeConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for directing Exchange config requests.
 * <p>
 * Exchange config can only be fetched and updated - it cannot be deleted or created.
 * <p>
 * There is only 1 Exchange Adapter per bot.
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api/config")
public class ExchangeConfigController {

    private static final Logger LOG = LogManager.getLogger();

    private final ExchangeConfigService exchangeConfigService;

    public ExchangeConfigController(ExchangeConfigService exchangeConfigService) {
        Assert.notNull(exchangeConfigService, "exchangeConfigService dependency cannot be null!");
        this.exchangeConfigService = exchangeConfigService;
    }

    /**
     * Returns the Exchange Config for a Exchange id.
     * <p>
     * The Exchange id is the same as the Bot id, given the 1:1 relationship between an Exchange and a Bot:
     * a Bot can only have 1 Exchange Adapter.
     *
     * @param user the authenticated user making the request.
     * @param id   the id of the Exchange (Bot) to fetch the Exchange config for.
     * @return the Exchange configuration.
     */
    @RequestMapping(value = "/exchanges/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getExchange(@AuthenticationPrincipal User user, @PathVariable String id) {

        // TODO - user param is null when using JWT Bearer token - what do we use? SecurityContext.getPrincipal?
        LOG.info("GET /exchanges/" + id + " - getExchange() "); //- caller: " + user.getUsername());

        final ExchangeConfig exchangeConfig = exchangeConfigService.getExchangeConfig(id);
        return exchangeConfig != null
                ? new ResponseEntity<>(new ResponseDataWrapper(exchangeConfig), null, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Updates the Exchange configuration for a bot.
     *
     * @param user           the authenticated user making the request.
     * @param exchangeConfig the Exchange config to update.
     * @return 200 'OK' HTTP status code with updated Exchange config in the body if update successful, some other
     * HTTP status code otherwise.
     */
    @RequestMapping(value = "/exchange", method = RequestMethod.PUT)
    public ResponseEntity<?> updateExchange(@AuthenticationPrincipal User user, @RequestBody ExchangeConfig exchangeConfig) {

        LOG.info("PUT /exchange - updateExchange() "); //- caller: " + user.getUsername());
        LOG.info("Request: " + exchangeConfig);

        final ExchangeConfig updatedConfig = exchangeConfigService.updateExchangeConfig(exchangeConfig.getId(), exchangeConfig);
        return new ResponseEntity<>(updatedConfig, HttpStatus.OK);
    }
}

