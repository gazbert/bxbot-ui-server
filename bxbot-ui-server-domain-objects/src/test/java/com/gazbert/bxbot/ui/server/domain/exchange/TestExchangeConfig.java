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

package com.gazbert.bxbot.ui.server.domain.exchange;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests ExchangeConfig domain object behaves as expected.
 *
 * @author gazbert
 */
public class TestExchangeConfig {

    private static final String EXCHANGE_ID = "bitstamp-1";
    private static final String EXCHANGE_NAME = "Bitstamp";
    private static final String ADAPTER_CLASS = "com.gazbert.bxbot.exchanges.TestExchangeAdapter";
    private static final NetworkConfig NETWORK_CONFIG = new NetworkConfig();
    private static final OptionalConfig OTHER_CONFIG = new OptionalConfig();


    @Test
    public void testInitialisationWorksAsExpected() {

        final ExchangeConfig exchangeConfig = new ExchangeConfig();
        assertEquals(null, exchangeConfig.getId());
        assertEquals(null, exchangeConfig.getName());
        assertEquals(null, exchangeConfig.getAdapterClass());
        assertEquals(null, exchangeConfig.getNetworkConfig());
        assertEquals(null, exchangeConfig.getOptionalConfig());
    }

    @Test
    public void testSettersWorkAsExpected() {

        final ExchangeConfig exchangeConfig = new ExchangeConfig();

        exchangeConfig.setId(EXCHANGE_ID);
        assertEquals(EXCHANGE_ID, exchangeConfig.getId());

        exchangeConfig.setName(EXCHANGE_NAME);
        assertEquals(EXCHANGE_NAME, exchangeConfig.getName());

        exchangeConfig.setAdapterClass(ADAPTER_CLASS);
        assertEquals(ADAPTER_CLASS, exchangeConfig.getAdapterClass());

        exchangeConfig.setNetworkConfig(NETWORK_CONFIG);
        assertEquals(NETWORK_CONFIG, exchangeConfig.getNetworkConfig());

        exchangeConfig.setOptionalConfig(OTHER_CONFIG);
        assertEquals(OTHER_CONFIG, exchangeConfig.getOptionalConfig());
    }
}
