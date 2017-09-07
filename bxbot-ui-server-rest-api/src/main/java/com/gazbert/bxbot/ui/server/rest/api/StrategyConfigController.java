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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

/**
 * Controller for directing Strategy config requests.
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api/config")
public class StrategyConfigController {

    private final StrategyConfigService strategyConfigService;

    @Autowired
    public StrategyConfigController(StrategyConfigService strategyConfigService) {
        Assert.notNull(strategyConfigService, "strategyConfigService dependency cannot be null!");
        this.strategyConfigService = strategyConfigService;
    }

    /**
     * Returns all of the Strategy configuration for the bot.
     *
     * @param user the authenticated user.
     * @return a list of Strategy configurations.
     */
    @RequestMapping(value = "/strategies", method = RequestMethod.GET)
    public List<StrategyConfig> getAllStrategies(@AuthenticationPrincipal User user) {
        String botId = "todo";
        return strategyConfigService.getAllStrategyConfig(botId);
    }

    /**
     * Returns the Strategy configuration for a given id.
     *
     * @param user the authenticated user.
     * @param strategyId the id of the Strategy to fetch.
     * @return the Strategy configuration.
     */
    @RequestMapping(value = "/strategies/{strategyId}", method = RequestMethod.GET)
    public ResponseEntity<?> getStrategy(@AuthenticationPrincipal User user, @PathVariable String strategyId) {

        String botId = "todo";
        final StrategyConfig strategyConfig = strategyConfigService.getStrategyConfig(botId, strategyId);
        return strategyConfig.getId() != null
                ? new ResponseEntity<>(strategyConfig, null, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Updates a given Strategy configuration.
     *
     * @param user the authenticated user.
     * @param strategyId id of the Strategy config to update.
     * @param strategyConfig the updated Strategy config.
     * @return 204 'No Content' HTTP status code if update successful, 404 'Not Found' HTTP status code if Strategy config not found.
     */
    @RequestMapping(value = "/strategies/{strategyId}", method = RequestMethod.PUT)
    ResponseEntity<?> updateStrategy(@AuthenticationPrincipal User user, @PathVariable String strategyId, @RequestBody StrategyConfig strategyConfig) {

        if (strategyConfig == null || strategyConfig.getId() == null || !strategyId.equals(strategyConfig.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String botId = "todo";
        final StrategyConfig updatedConfig = strategyConfigService.updateStrategyConfig(botId, strategyConfig);
        return updatedConfig.getId() != null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    /**
     * Creates a new Strategy configuration.
     *
     * @param user the authenticated user.
     * @param strategyId id of the Strategy config to create.
     * @param strategyConfig the new Strategy config.
     * @return 201 'Created' HTTP status code if create successful, 409 'Conflict' HTTP status code if Strategy config already exists.
     */
    @RequestMapping(value = "/strategies/{strategyId}", method = RequestMethod.POST)
    ResponseEntity<?> createStrategy(@AuthenticationPrincipal User user, @PathVariable String strategyId, @RequestBody StrategyConfig strategyConfig) {

        if (strategyConfig == null || strategyConfig.getId() == null || !strategyId.equals(strategyConfig.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String botId = "todo";
        final StrategyConfig createdConfig = strategyConfigService.createStrategyConfig(botId, strategyConfig);
        if (createdConfig.getId() != null) {
            final HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setLocation(ServletUriComponentsBuilder
                    .fromCurrentRequest().path("/{strategyId}")
                    .buildAndExpand(createdConfig.getId()).toUri());
            return new ResponseEntity<>(null, httpHeaders, HttpStatus.CREATED);

        } else {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Deletes a Strategy configuration for a given id.
     *
     * @param user the authenticated user.
     * @param strategyId the id of the Strategy configuration to delete.
     * @return 204 'No Content' HTTP status code if update successful, 404 'Not Found' HTTP status code if Strategy config not found.
     */
    @RequestMapping(value = "/strategies/{strategyId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteStrategy(@AuthenticationPrincipal User user, @PathVariable String strategyId) {

        String botId = "todo";
        final StrategyConfig deletedConfig = strategyConfigService.deleteStrategyConfig(botId, strategyId);
        return deletedConfig.getId() != null
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}

