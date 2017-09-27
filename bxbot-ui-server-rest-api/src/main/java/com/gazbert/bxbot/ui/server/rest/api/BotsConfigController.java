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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for directing Bot Details requests.
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api")
public class BotsConfigController {

    private final BotConfigService botConfigService;

    @Autowired
    public BotsConfigController(BotConfigService botConfigService) {
        this.botConfigService = botConfigService;
    }

    /**
     * Returns the Bot details for all the bots.
     *
     * @return the BotConfig configuration.
     */
    @PreAuthorize("hasRole('USER')") // Spring Security maps USER to ROLE_USER in database - ROLE_ prefix must be used.
    @RequestMapping(value = "/bots", method = RequestMethod.GET)
    public ResponseDataWrapper getBots(@AuthenticationPrincipal User user) {
        return new ResponseDataWrapper(botConfigService.getAllBotConfig());
    }

    /**
     * Returns the Bot Details for a given Bot id.
     *
     * @param user  the authenticated user.
     * @param botId the id of the Bot to fetch.
     * @return the Bot Details configuration.
     */
    @RequestMapping(value = "/bots/{botId}", method = RequestMethod.GET)
    public ResponseEntity<?> getBot(@AuthenticationPrincipal User user, @PathVariable String botId) {

        final BotConfig botConfig = botConfigService.getBotConfig(botId);
        return botConfig.getId() != null
                ? new ResponseEntity<>(new ResponseDataWrapper(botConfig), null, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

