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

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import com.gazbert.bxbot.ui.server.services.BotConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for directing Bot config requests.
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api")
public class BotsConfigController {

    private static final Logger LOG = LogManager.getLogger();

    private final BotConfigService botConfigService;

    @Autowired
    public BotsConfigController(BotConfigService botConfigService) {
        this.botConfigService = botConfigService;
    }

    /**
     * Returns the Bot config for all the bots.
     *
     * @return the BotConfig configuration.
     */
    @PreAuthorize("hasRole('USER')") // Spring Security maps USER to ROLE_USER in database - ROLE_ prefix must be used.
    @RequestMapping(value = "/bots", method = RequestMethod.GET)
    public ResponseDataWrapper getBots(@AuthenticationPrincipal User user) {

        LOG.info("GET /bots - getBots()"); // - caller: " + user.getUsername());
        final ResponseDataWrapper responseDataWrapper = new ResponseDataWrapper(botConfigService.getAllBotConfig());

        LOG.info("Response: " + responseDataWrapper);
        return responseDataWrapper;
    }

    /**
     * Returns the Bot config for a given Bot id.
     *
     * @param user  the authenticated user.
     * @param botId the id of the Bot to fetch.
     * @return the Bot Details configuration.
     */
    @RequestMapping(value = "/bots/{botId}", method = RequestMethod.GET)
    public ResponseEntity<?> getBot(@AuthenticationPrincipal User user, @PathVariable String botId) {

        LOG.info("GET /bots/" + botId + " - getBot()"); // - caller: " + user.getUsername());
        return createResponseWrapper(botConfigService.getBotConfig(botId));
    }

    /**
     * Updates the Bot config configuration for a given Bot id.
     *
     * @param user      the authenticated user making the request.
     * @param botConfig the Bot config to update.
     * @return 200 'OK' HTTP status code with updated Bot config in the body if update successful, some other
     * HTTP status code otherwise.
     */
    @RequestMapping(value = "/bots/{botId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateBot(@AuthenticationPrincipal User user, @PathVariable String botId, @RequestBody BotConfig botConfig) {

        LOG.info("PUT /api/bots/" + botId + " - updateBot()"); // - caller: " + user.getUsername());
        LOG.info("Request: " + botConfig);

        if (botConfig == null || botConfig.getId() == null || !botId.equals(botConfig.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final BotConfig updateBotConfig = botConfigService.updateBotConfig(botConfig);
        return createResponseWrapper(updateBotConfig);
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    private ResponseEntity<ResponseDataWrapper> createResponseWrapper(BotConfig botConfig) {
        if (botConfig.getId() != null) {
            final ResponseDataWrapper responseDataWrapper = new ResponseDataWrapper(botConfig);
            LOG.info("Response: " + responseDataWrapper);
            return new ResponseEntity<>(responseDataWrapper, null, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}

