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

import com.gazbert.bxbot.ui.server.domain.engine.EngineConfig;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import com.gazbert.bxbot.ui.server.services.EngineConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.gazbert.bxbot.ui.server.rest.api.v1.config.AbstractController.CONFIG_ENDPOINT_BASE_URI;

/**
 * Controller for directing Engine config requests.
 * <p>
 * Engine config can only be fetched and updated - it cannot be deleted or created.
 * <p>
 * There is only 1 Engine config per bot.
 * <p>
 * TODO - user param is null when using JWT Bearer token - what do we use? SecurityContext.getPrincipal?
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping(CONFIG_ENDPOINT_BASE_URI)
public class EngineConfigController extends AbstractController {

    private static final Logger LOG = LogManager.getLogger();
    private static final String ENGINE_RESOURCE_PATH = "/engine";
    private final EngineConfigService engineConfigService;

    public EngineConfigController(EngineConfigService engineConfigService) {
        this.engineConfigService = engineConfigService;
    }

    /**
     * Returns the Engine Config for a Bot id.
     *
     * @param user  the authenticated user making the request.
     * @param botId the id of the Bot to fetch the Engine config for.
     * @return the Engine configuration.
     */
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "{botId}" + ENGINE_RESOURCE_PATH, method = RequestMethod.GET)
    public ResponseEntity<?> getEngine(@AuthenticationPrincipal User user, @PathVariable String botId) {

        LOG.info("GET " + CONFIG_ENDPOINT_BASE_URI + botId + ENGINE_RESOURCE_PATH + " - getEngine() "); //- caller: " + user.getUsername());

        final EngineConfig engineConfig = engineConfigService.getEngineConfig(botId);
        return engineConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(engineConfig, HttpStatus.OK);
    }

    /**
     * Updates the Engine configuration for a bot.
     *
     * @param user         the authenticated user making the request.
     * @param engineConfig the Engine config to update.
     * @param botId        the id of the Bot to update the Engine config for.
     * @return 200 'Ok' HTTP status code with updated Engine config if update successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "{botId}" + ENGINE_RESOURCE_PATH, method = RequestMethod.PUT)
    public ResponseEntity<?> updateEngine(@AuthenticationPrincipal User user, @RequestBody EngineConfig engineConfig,
                                          @PathVariable String botId) {

        LOG.info("PUT " + CONFIG_ENDPOINT_BASE_URI + botId + ENGINE_RESOURCE_PATH + " - updateEngine() "); //- caller: " + user.getUsername());
        LOG.info("Request: " + engineConfig);

        final EngineConfig updatedConfig = engineConfigService.updateEngineConfig(botId, engineConfig);
        return updatedConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(updatedConfig, HttpStatus.OK);
    }
}

