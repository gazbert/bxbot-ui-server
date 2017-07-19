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

import com.gazbert.bxbot.ui.server.domain.bot.BotDetails;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
public class BotDetailsController {

    /**
     * Returns the Bot details for all the bots.
     *
     * @return the BotDetails configuration.
     */
    @RequestMapping(value = "/bots", method = RequestMethod.GET)
    public ResponseDataWrapper getBots(@AuthenticationPrincipal User user) {
        return new ResponseDataWrapper(getBots());
    }

    /**
     * Returns the Bot Details for a given Bot id.
     *
     * @param user  the authenticated user.
     * @param botId the id of the Bot to fetch.
     * @return the Bot Details configuration.
     */
    @RequestMapping(value = "/bots/{botId}", method = RequestMethod.GET)
    public ResponseEntity<?> getStrategy(@AuthenticationPrincipal User user, @PathVariable String botId) {

        final BotDetails botDetails = getBot(botId);
        return botDetails.getId() != null
                ? new ResponseEntity<>(new ResponseDataWrapper(botDetails), null, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    /*
     * Stub for now.
     */
    private List<BotDetails> getBots() {

        final BotDetails bitstamp = new BotDetails();
        bitstamp.setId("bitstamp");
        bitstamp.setName("Bitstamp");
        bitstamp.setStatus("Running");

        final BotDetails btce = new BotDetails();
        btce.setId("btce");
        btce.setName("BTC-e");
        btce.setStatus("Running");

        final BotDetails gdax = new BotDetails();
        gdax.setId("gdax");
        gdax.setName("GDAX");
        gdax.setStatus("Stopped");

        final BotDetails gemini = new BotDetails();
        gemini.setId("gemini");
        gemini.setName("Gemini");
        gemini.setStatus("Running");

        final BotDetails okcoin = new BotDetails();
        okcoin.setId("okcoin");
        okcoin.setName("OK Coin");
        okcoin.setStatus("Stopped");

        final BotDetails huobi = new BotDetails();
        huobi.setId("huobi");
        huobi.setName("Huobi");
        huobi.setStatus("Stopped");

        final BotDetails itbit = new BotDetails();
        itbit.setId("itbit");
        itbit.setName("itBit");
        itbit.setStatus("Running");

        final BotDetails kraken = new BotDetails();
        kraken.setId("kraken");
        kraken.setName("Kraken");
        kraken.setStatus("Stopped");

        final BotDetails bitfinex = new BotDetails();
        bitfinex.setId("bitfinex");
        bitfinex.setName("Bitfinex");
        bitfinex.setStatus("Stopped");

        final List<BotDetails> cannedBotDetails = new ArrayList<>();
        cannedBotDetails.add(btce);
        cannedBotDetails.add(bitstamp);
        cannedBotDetails.add(gdax);
        cannedBotDetails.add(gemini);
        cannedBotDetails.add(okcoin);
        cannedBotDetails.add(huobi);
        cannedBotDetails.add(itbit);
        cannedBotDetails.add(kraken);
        cannedBotDetails.add(bitfinex);

        return cannedBotDetails;
    }

    /*
     * Stub for now.
     */
    private BotDetails getBot(String botId) {

        final BotDetails btce = new BotDetails();
        btce.setId(botId);
        btce.setName("Bitstamp");
        btce.setStatus("Running");

        return btce;
    }
}

