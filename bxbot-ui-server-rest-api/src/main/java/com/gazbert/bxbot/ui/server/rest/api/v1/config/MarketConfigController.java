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

import com.gazbert.bxbot.ui.server.domain.market.MarketConfig;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import com.gazbert.bxbot.ui.server.services.MarketConfigService;
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
 * Controller for directing Market config requests.
 * <p>
 * TODO - AuthenticationPrincipal User - get equivalent for use with JWT auth?
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api/v1/config")
public class MarketConfigController extends AbstractController {

    private static final Logger LOG = LogManager.getLogger();
    private static final String BOT_ID_PARAM = "botId";

    private final MarketConfigService marketConfigService;

    @Autowired
    public MarketConfigController(MarketConfigService marketConfigService) {
        this.marketConfigService = marketConfigService;
    }

    /**
     * Returns all of the Market configuration for the bot.
     *
     * @param user  the authenticated user.
     * @param botId the id of the Bot to fetch the Markets config for.
     * @return a list of Market configurations.
     */
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/markets", method = RequestMethod.GET)
    public ResponseEntity<?> getAllMarkets(@AuthenticationPrincipal User user, @Param(value = BOT_ID_PARAM) String botId) {

        LOG.info("GET /markets/?" + BOT_ID_PARAM + "=" + botId + " - getAllMarkets()"); // caller: " + user.getUsername());

        if (botId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final List<MarketConfig> allMarketConfig = marketConfigService.getAllMarketConfig(botId);
        return allMarketConfig.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(allMarketConfig, HttpStatus.OK);
    }

    /**
     * Returns the Market configuration for a given id.
     *
     * @param user     the authenticated user.
     * @param marketId the id of the Market to fetch.
     * @param botId    the id of the Bot to fetch the Market config for.
     * @return the Market configuration.
     */
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/markets/{marketId}", method = RequestMethod.GET)
    public ResponseEntity<?> getMarket(@AuthenticationPrincipal User user, @PathVariable String marketId,
                                       @Param(value = BOT_ID_PARAM) String botId) {

        LOG.info("GET /markets/" + marketId + "/?" + BOT_ID_PARAM + "=" + botId + " - getMarket() "); //- caller: " + user.getUsername());

        if (botId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final MarketConfig marketConfig = marketConfigService.getMarketConfig(botId, marketId);
        return marketConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(marketConfig, HttpStatus.OK);
    }

    /**
     * Updates a given Market configuration.
     *
     * @param user         the authenticated user.
     * @param marketId     id of the Market config to update.
     * @param marketConfig the updated Market config.
     * @param botId        the id of the Bot to update the Market config for.
     * @return 200 'Ok' and the updated Market config if successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/markets/{marketId}", method = RequestMethod.PUT)
    public ResponseEntity<?> updateMarket(@AuthenticationPrincipal User user, @PathVariable String marketId,
                                          @RequestBody MarketConfig marketConfig, @Param(value = BOT_ID_PARAM) String botId) {

        LOG.info("PUT /markets/" + marketId + "/?" + BOT_ID_PARAM + "=" + botId + " - updateMarket() "); //- caller: " + user.getUsername());
        LOG.info("Request: " + marketConfig);

        if (botId == null || marketConfig.getId() == null || !marketId.equals(marketConfig.getId())) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final MarketConfig updatedConfig = marketConfigService.updateMarketConfig(botId, marketConfig);
        return updatedConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(updatedConfig, HttpStatus.OK);
    }

    /**
     * Creates a new Market configuration.
     *
     * @param user         the authenticated user.
     * @param marketConfig the new Market config.
     * @param botId        the id of the Bot to update the Market config for.
     * @return 201 'Created' HTTP status code and created Market config if successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/markets", method = RequestMethod.POST)
    public ResponseEntity<?> createMarket(@AuthenticationPrincipal User user, @RequestBody MarketConfig marketConfig,
                                          @Param(value = BOT_ID_PARAM) String botId) {

        LOG.info("POST /markets/?" + BOT_ID_PARAM + "=" + botId + " - createMarket()"); // - caller: " + user.getUsername());
        LOG.info("Request: " + marketConfig);

        if (botId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final MarketConfig createdConfig = marketConfigService.createMarketConfig(botId, marketConfig);
        return createdConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(createdConfig, HttpStatus.CREATED);
    }

    /**
     * Deletes a Market configuration for a given id.
     *
     * @param user     the authenticated user.
     * @param marketId the id of the Market configuration to delete.
     * @param botId    the id of the Bot to delete the Market config for.
     * @return 204 'No Content' HTTP status code if delete successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/markets/{marketId}", method = RequestMethod.DELETE)
    public ResponseEntity<?> deleteMarket(@AuthenticationPrincipal User user, @PathVariable String marketId,
                                          @Param(value = BOT_ID_PARAM) String botId) {

        LOG.info("DELETE /markets/" + marketId + "/?" + BOT_ID_PARAM + "=" + botId + " - deleteMarket()"); // - caller: " + user.getUsername());

        if (botId == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        final boolean result = marketConfigService.deleteMarketConfig(botId, marketId);
        return !result
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
