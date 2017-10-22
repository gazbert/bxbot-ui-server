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

package com.gazbert.bxbot.ui.server.rest.api.v1.config;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import com.gazbert.bxbot.ui.server.services.config.BotConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gazbert.bxbot.ui.server.rest.api.v1.config.AbstractConfigController.CONFIG_ENDPOINT_BASE_URI;

/**
 * Controller for directing Bot config requests.
 * <p>
 * TODO - AuthenticationPrincipal User - get equivalent for use with JWT auth?
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping(CONFIG_ENDPOINT_BASE_URI)
public class BotsConfigController extends AbstractConfigController {

    private static final Logger LOG = LogManager.getLogger();
    private final BotConfigService botConfigService;

    @Autowired
    public BotsConfigController(BotConfigService botConfigService) {
        this.botConfigService = botConfigService;
    }

    /**
     * Returns the Bot config for all the bots.
     *
     * @param user the authenticated user.
     * @return all the Bots configuration.
     */
    @PreAuthorize("hasRole('USER')") // Spring Security maps USER to ROLE_USER in database - ROLE_ prefix must be used.
    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getBots(@AuthenticationPrincipal User user) {

        LOG.info("GET " + CONFIG_ENDPOINT_BASE_URI + " - getBots()"); // caller: " + user.getUsername());

        final List<BotConfig> botConfigs = botConfigService.getAllBotConfig();
        return botConfigs.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(botConfigs, HttpStatus.OK);
    }

    /**
     * Returns the Bot config for a given Bot id.
     *
     * @param user  the authenticated user.
     * @param botId the id of the Bot to fetch.
     * @return the Bot config for the given id.
     */
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{botId}", method = RequestMethod.GET)
    public ResponseEntity<?> getBot(@AuthenticationPrincipal User user, @PathVariable String botId) {

        LOG.info("GET " + CONFIG_ENDPOINT_BASE_URI + botId + " - getBot()"); // - caller: " + user.getUsername());

        final BotConfig botConfig = botConfigService.getBotConfig(botId);
        return botConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(botConfig, HttpStatus.OK);
    }

    /**
     * Updates the Bot config configuration for a given Bot id.
     *
     * @param user      the authenticated user making the request.
     * @param botConfig the Bot config to update.
     * @return 200 'OK' HTTP status code with updated Bot config if successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{botId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateBot(@AuthenticationPrincipal User user, @PathVariable String botId, @RequestBody BotConfig botConfig) {

        LOG.info("PUT " + CONFIG_ENDPOINT_BASE_URI + botId + " - updateBot()"); // - caller: " + user.getUsername());
        LOG.info("Request: " + botConfig);

        if (!botId.equals(botConfig.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final BotConfig updateBotConfig = botConfigService.updateBotConfig(botConfig);
        return updateBotConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(updateBotConfig, HttpStatus.OK);
    }

    /**
     * Creates a new Bot configuration.
     *
     * @param user      the authenticated user.
     * @param botConfig the new Bot config.
     * @return 201 'Created' HTTP status code if create successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<?> createBot(@AuthenticationPrincipal User user, @RequestBody BotConfig botConfig) {

        LOG.info("POST " + CONFIG_ENDPOINT_BASE_URI + " - createBot()"); // - caller: " + user.getUsername());
        LOG.info("Request: " + botConfig);

        final BotConfig createdConfig = botConfigService.createBotConfig(botConfig);
        return createdConfig == null
                ? new ResponseEntity<>(HttpStatus.CONFLICT)
                : buildResponseEntity(createdConfig, HttpStatus.CREATED);
    }

    /**
     * Deletes a Bot configuration for a given id.
     *
     * @param user  the authenticated user.
     * @param botId the id of the Bot configuration to delete.
     * @return 204 'No Content' HTTP status code if delete successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{botId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteBot(@AuthenticationPrincipal User user, @PathVariable String botId) {

        LOG.info("DELETE " + CONFIG_ENDPOINT_BASE_URI + botId + " - deleteBot()"); // - caller: " + user.getUsername());

        final BotConfig deletedConfig = botConfigService.deleteBotConfig(botId);
        return deletedConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

