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

package com.gazbert.bxbot.ui.server.domain.market;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests a Market Config domain object behaves as expected.
 *
 * @author gazbert
 */
public class TestMarketConfig {

    private static final String ID = "gemini_usd/btc";
    private static final String NAME = "BTC/USD";
    private static final boolean IS_ENABLED = true;
    private static final String BASE_CURRENCY = "BTC";
    private static final String COUNTER_CURRENCY = "USD";
    private static final String STRATEGY_ID = "macd_trend_follower";


    @Test
    public void testInitialisationWorksAsExpected() {

        final MarketConfig marketConfig = new MarketConfig(ID, NAME, IS_ENABLED, BASE_CURRENCY, COUNTER_CURRENCY, STRATEGY_ID);
        assertEquals(NAME, marketConfig.getName());
        assertEquals(ID, marketConfig.getId());
        assertEquals(IS_ENABLED, marketConfig.isEnabled());
        assertEquals(BASE_CURRENCY, marketConfig.getBaseCurrency());
        assertEquals(COUNTER_CURRENCY, marketConfig.getCounterCurrency());
        assertEquals(STRATEGY_ID, marketConfig.getStrategyId());
    }

    @Test
    public void testSettersWorkAsExpected() {

        final MarketConfig marketConfig = new MarketConfig();
        assertEquals(null, marketConfig.getId());
        assertEquals(null, marketConfig.getName());
        assertEquals(false, marketConfig.isEnabled());
        assertEquals(null, marketConfig.getBaseCurrency());
        assertEquals(null, marketConfig.getCounterCurrency());
        assertEquals(null, marketConfig.getStrategyId());

        marketConfig.setId(ID);
        assertEquals(ID, marketConfig.getId());

        marketConfig.setName(NAME);
        assertEquals(NAME, marketConfig.getName());

        marketConfig.setEnabled(IS_ENABLED);
        assertEquals(IS_ENABLED, marketConfig.isEnabled());

        marketConfig.setBaseCurrency(BASE_CURRENCY);
        assertEquals(BASE_CURRENCY, marketConfig.getBaseCurrency());

        marketConfig.setCounterCurrency(COUNTER_CURRENCY);
        assertEquals(COUNTER_CURRENCY, marketConfig.getCounterCurrency());

        marketConfig.setStrategyId(STRATEGY_ID);
        assertEquals(STRATEGY_ID, marketConfig.getStrategyId());
    }

    @Test
    public void testCloningWorksAsExpected() {
        final MarketConfig marketConfig = new MarketConfig(
                ID, NAME, IS_ENABLED, BASE_CURRENCY, COUNTER_CURRENCY, STRATEGY_ID);
        final MarketConfig clonedMarketConfig = new MarketConfig(marketConfig);
        assertEquals(clonedMarketConfig, marketConfig);
    }
}
