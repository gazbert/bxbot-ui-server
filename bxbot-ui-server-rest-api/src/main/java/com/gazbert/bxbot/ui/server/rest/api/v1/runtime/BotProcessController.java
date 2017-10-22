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

package com.gazbert.bxbot.ui.server.rest.api.v1.runtime;

import com.gazbert.bxbot.ui.server.domain.bot.BotStatus;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import com.gazbert.bxbot.ui.server.services.runtime.BotProcessService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import static com.gazbert.bxbot.ui.server.rest.api.v1.runtime.AbstractRuntimeController.RUNTIME_ENDPOINT_BASE_URI;

/**
 * Controller for directing Bot process requests.
 * <p>
 * TODO - AuthenticationPrincipal User - get equivalent for use with JWT auth?
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping(RUNTIME_ENDPOINT_BASE_URI)
public class BotProcessController extends AbstractRuntimeController {

    private static final Logger LOG = LogManager.getLogger();
    private static final String STATUS_RESOURCE_PATH = "/status";
    private static final String START_RESOURCE_PATH = "/start";
    private static final String STOP_RESOURCE_PATH = "/stop";
    private final BotProcessService botProcessService;

    @Autowired
    public BotProcessController(BotProcessService botProcessService) {
        this.botProcessService = botProcessService;
    }

    /**
     * Returns the Bot status for a given Bot id.
     *
     * @param user  the authenticated user.
     * @param botId the id of the Bot to fetch.
     * @return the Bot status for the given id.
     */
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "{botId}" + STATUS_RESOURCE_PATH, method = RequestMethod.GET)
    public ResponseEntity<?> getStatus(@AuthenticationPrincipal User user, @PathVariable String botId) {

        LOG.info("GET " + RUNTIME_ENDPOINT_BASE_URI + botId + STATUS_RESOURCE_PATH + " - getStatus()"); // - caller: " + user.getUsername());

        final BotStatus botStatus = botProcessService.getStatus(botId);
        return botStatus == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(botStatus, HttpStatus.OK);
    }
}

