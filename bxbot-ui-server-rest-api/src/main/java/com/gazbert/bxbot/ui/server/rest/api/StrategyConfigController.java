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

import com.gazbert.bxbot.ui.server.domain.strategy.StrategyConfig;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import com.gazbert.bxbot.ui.server.services.StrategyConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for directing Strategy config requests.
 * <p>
 * TODO - AuthenticationPrincipal User - get equivalent for use with JWT auth?
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api/config")
public class StrategyConfigController extends AbstractController {

    private static final Logger LOG = LogManager.getLogger();
    private static final String BOT_ID_PARAM = "botId";

    private final StrategyConfigService strategyConfigService;

    @Autowired
    public StrategyConfigController(StrategyConfigService strategyConfigService) {
        this.strategyConfigService = strategyConfigService;
    }

    /**
     * Returns all of the Strategy configuration for the bot.
     *
     * @param user  the authenticated user.
     * @param botId the id of the Bot to fetch the Strategies config for.
     * @return a list of Strategy configurations.
     */
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/strategies", method = RequestMethod.GET)
    public ResponseEntity<?> getAllStrategies(@AuthenticationPrincipal User user, @Param(value = BOT_ID_PARAM) String botId) {

        LOG.info("GET /strategies/?" + BOT_ID_PARAM + "=" + botId + " - getAllStrategies()"); // caller: " + user.getUsername());

        if (botId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final List<StrategyConfig> strategyConfigs = strategyConfigService.getAllStrategyConfig(botId);
        return strategyConfigs.isEmpty() ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(strategyConfigs, HttpStatus.OK);
    }

    /**
     * Returns the Strategy configuration for a given id.
     *
     * @param user       the authenticated user.
     * @param strategyId the id of the Strategy to fetch.
     * @param botId      the id of the Bot to fetch the Strategy config for.
     * @return the Strategy configuration.
     */
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/strategies/{strategyId}", method = RequestMethod.GET)
    public ResponseEntity<?> getStrategy(@AuthenticationPrincipal User user, @PathVariable String strategyId,
                                         @Param(value = BOT_ID_PARAM) String botId) {

        LOG.info("GET /strategies/" + strategyId + "/?" + BOT_ID_PARAM + "=" + botId + " - getStrategy() "); //- caller: " + user.getUsername());

        if (botId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final StrategyConfig strategyConfig = strategyConfigService.getStrategyConfig(botId, strategyId);
        return strategyConfig == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                buildResponseEntity(strategyConfig, HttpStatus.OK);
    }

    /**
     * Updates a given Strategy configuration.
     *
     * @param user           the authenticated user.
     * @param strategyId     id of the Strategy config to update.
     * @param strategyConfig the updated Strategy config.
     * @param botId          the id of the Bot to update the Strategy config for.
     * @return 200 'Ok' and the updated Strategy config if successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/strategies/{strategyId}", method = RequestMethod.PUT)
    ResponseEntity<?> updateStrategy(@AuthenticationPrincipal User user, @PathVariable String strategyId,
                                     @RequestBody StrategyConfig strategyConfig, @Param(value = BOT_ID_PARAM) String botId) {

        LOG.info("PUT /strategies/" + strategyId + "/?" + BOT_ID_PARAM + "=" + botId + " - updateExchange() "); //- caller: " + user.getUsername());
        LOG.info("Request: " + strategyConfig);

        if (botId == null || !strategyId.equals(strategyConfig.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final StrategyConfig updatedConfig = strategyConfigService.updateStrategyConfig(botId, strategyConfig);
        return updatedConfig == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND) :
                buildResponseEntity(updatedConfig, HttpStatus.OK);
    }

    /**
     * Creates a new Strategy configuration.
     *
     * @param user           the authenticated user.
     * @param strategyConfig the new Strategy config.
     * @param botId          the id of the Bot to update the Strategy config for.
     * @return 201 'Created' HTTP status code and created Strategy config if successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/strategies", method = RequestMethod.POST)
    ResponseEntity<?> createStrategy(@AuthenticationPrincipal User user, @RequestBody StrategyConfig strategyConfig,
                                     @Param(value = BOT_ID_PARAM) String botId) {

        LOG.info("POST /strategies - createStrategy()"); // - caller: " + user.getUsername());
        LOG.info("Request: " + strategyConfig);

        if (botId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final StrategyConfig createdConfig = strategyConfigService.createStrategyConfig(botId, strategyConfig);
        return createdConfig == null ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(createdConfig, HttpStatus.CREATED);
    }

    /**
     * Deletes a Strategy configuration for a given id.
     *
     * @param user       the authenticated user.
     * @param strategyId the id of the Strategy configuration to delete.
     * @param botId      the id of the Bot to delete the Strategy config for.
     * @return 204 'No Content' HTTP status code if delete successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/strategies/{strategyId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStrategy(@AuthenticationPrincipal User user, @PathVariable String strategyId,
                                            @Param(value = BOT_ID_PARAM) String botId) {

        LOG.info("DELETE /strategies/" + botId + " - deleteStrategy()"); // - caller: " + user.getUsername());

        if (botId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final StrategyConfig deletedConfig = strategyConfigService.deleteStrategyConfig(botId, strategyId);
        return deletedConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

