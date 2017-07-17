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

import com.gazbert.bxbot.ui.server.domain.bot.*;
//import com.gazbert.bxbot.domain.exchange.ExchangeConfig;
//import com.gazbert.bxbot.services.ExchangeConfigService;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for directing Bot details.
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api/")
public class BotDetailsController {

//    private final BotDetailsConfigService BotDetailsConfigService;

//    @Autowired
//    public BotsController(BotDetailsConfigService BotDetailsConfigService) {
//        Assert.notNull(BotDetailsConfigService, "BotDetailsConfigService dependency cannot be null!");
//        this.BotDetailsConfigService = BotDetailsConfigService;
//    }

    /**
     * Returns Bot details.
     *
     * @return the BotDetails configuration.
     */
    @RequestMapping(value = "/bots", method = RequestMethod.GET)
    public BaseResponse getBots(@AuthenticationPrincipal User user) {

//        final BotDetailsConfig BotDetailsConfig = BotDetailsConfigService.getConfig();
//        // Strip out the Authentication config for now - too risky to expose trading api keys
//        BotDetailsConfig.setAuthenticationConfig(null);

        final BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(getBots());
        return baseResponse;
    }

    /**
     * Updates BotDetails configuration for the bot.
     *
     * @return 204 'No Content' HTTP status code if BotDetails config was updated, some other HTTP status code otherwise.
     */
//    @RequestMapping(value = "/BotDetails", method = RequestMethod.PUT)
//    ResponseEntity<?> updateBotDetails(@AuthenticationPrincipal User user, @RequestBody BotDetailsConfig config) {
//
//        BotDetailsConfigService.updateConfig(config);
//        final HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/").buildAndExpand().toUri());
//        return new ResponseEntity<>(null, httpHeaders, HttpStatus.NO_CONTENT);
//    }

    // Stub for now
    private List<BotDetails> getBots() {

        final BotDetails btce = new BotDetails();
        btce.setId("btce");
        btce.setName("BTC-e");
        btce.setStatus("Running");

        final BotDetails bitstamp = new BotDetails();
        bitstamp.setId("bitstamp");
        bitstamp.setName("Bitstamp");
        bitstamp.setStatus("Running");

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
}

