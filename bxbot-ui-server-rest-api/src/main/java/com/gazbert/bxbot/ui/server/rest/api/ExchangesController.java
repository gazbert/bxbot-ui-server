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

import com.gazbert.bxbot.ui.server.domain.exchange.BaseResponse;
import com.gazbert.bxbot.ui.server.domain.exchange.Exchange;
//import com.gazbert.bxbot.domain.exchange.Exchanges;
import com.gazbert.bxbot.ui.server.rest.security.User;
//import com.gazbert.bxbot.domain.exchange.ExchangeConfig;
//import com.gazbert.bxbot.services.ExchangeConfigService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * Controller for directing Exchange config requests.
 * <p>
 * Exchange config can only be fetched and updated - there is only 1 Exchange Adapter per bot.
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping("/api/")
public class ExchangesController {

//    private final ExchangeConfigService exchangeConfigService;

//    @Autowired
//    public ExchangesController(ExchangeConfigService exchangeConfigService) {
//        Assert.notNull(exchangeConfigService, "exchangeConfigService dependency cannot be null!");
//        this.exchangeConfigService = exchangeConfigService;
//    }

    /**
     * Returns Exchange configuration for the bot.
     *
     * @return the Exchange configuration.
     */
    @RequestMapping(value = "/exchanges", method = RequestMethod.GET)
    public BaseResponse getExchange(@AuthenticationPrincipal User user) {

//        final ExchangeConfig exchangeConfig = exchangeConfigService.getConfig();
//        // Strip out the Authentication config for now - too risky to expose trading api keys
//        exchangeConfig.setAuthenticationConfig(null);

        final BaseResponse baseResponse = new BaseResponse();
        baseResponse.setData(getExchanges());
        return baseResponse;
    }

    /**
     * Updates Exchange configuration for the bot.
     *
     * @return 204 'No Content' HTTP status code if exchange config was updated, some other HTTP status code otherwise.
     */
//    @RequestMapping(value = "/exchange", method = RequestMethod.PUT)
//    ResponseEntity<?> updateExchange(@AuthenticationPrincipal User user, @RequestBody ExchangeConfig config) {
//
//        exchangeConfigService.updateConfig(config);
//        final HttpHeaders httpHeaders = new HttpHeaders();
//        httpHeaders.setLocation(ServletUriComponentsBuilder.fromCurrentRequest().path("/").buildAndExpand().toUri());
//        return new ResponseEntity<>(null, httpHeaders, HttpStatus.NO_CONTENT);
//    }

    // Stub for now
    private List<Exchange> getExchanges() {

        final Exchange btce = new Exchange();
        btce.setId("btce");
        btce.setName("BTC-e");
        btce.setStatus("Running");

        final Exchange bitstamp = new Exchange();
        bitstamp.setId("bitstamp");
        bitstamp.setName("Bitstamp");
        bitstamp.setStatus("Running");

        final Exchange gdax = new Exchange();
        gdax.setId("gdax");
        gdax.setName("GDAX");
        gdax.setStatus("Stopped");

        final Exchange gemini = new Exchange();
        gemini.setId("gemini");
        gemini.setName("Gemini");
        gemini.setStatus("Running");

        final Exchange okcoin = new Exchange();
        okcoin.setId("okcoin");
        okcoin.setName("OK Coin");
        okcoin.setStatus("Stopped");

        final Exchange huobi = new Exchange();
        huobi.setId("huobi");
        huobi.setName("Huobi");
        huobi.setStatus("Stopped");

        final Exchange itbit = new Exchange();
        itbit.setId("itbit");
        itbit.setName("itBit");
        itbit.setStatus("Running");

        final Exchange kraken = new Exchange();
        kraken.setId("kraken");
        kraken.setName("Kraken");
        kraken.setStatus("Stopped");

        final Exchange bitfinex = new Exchange();
        bitfinex.setId("bitfinex");
        bitfinex.setName("Bitfinex");
        bitfinex.setStatus("Stopped");

        final List<Exchange> cannedExchanges = new ArrayList<>();
        cannedExchanges.add(btce);
        cannedExchanges.add(bitstamp);
        cannedExchanges.add(gdax);
        cannedExchanges.add(gemini);
        cannedExchanges.add(okcoin);
        cannedExchanges.add(huobi);
        cannedExchanges.add(itbit);
        cannedExchanges.add(kraken);
        cannedExchanges.add(bitfinex);

        return cannedExchanges;
    }
}

