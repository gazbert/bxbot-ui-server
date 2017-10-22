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

import com.gazbert.bxbot.ui.server.domain.exchange.ExchangeConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.NetworkConfig;
import com.gazbert.bxbot.ui.server.domain.exchange.OptionalConfig;
import com.gazbert.bxbot.ui.server.services.config.ExchangeConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the Exchange config controller behaviour.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestExchangeConfigController extends AbstractConfigControllerTest {

    private static final String EXCHANGE_RESOURCE_PATH = "/exchange";

    private static final String BOT_ID = "gdax-bot-1";
    private static final String UNKNOWN_BOT_ID = "unknown-bot-id";

    private static final String EXCHANGE_NAME = "Bitstamp";
    private static final String EXCHANGE_ADAPTER = "com.gazbert.bxbot.exchanges.TestExchangeAdapter";

    private static final Integer CONNECTION_TIMEOUT = 30;

    private static final int HTTP_STATUS_502 = 502;
    private static final int HTTP_STATUS_503 = 503;
    private static final int HTTP_STATUS_504 = 504;
    private static final List<Integer> NON_FATAL_ERROR_CODES = Arrays.asList(HTTP_STATUS_502, HTTP_STATUS_503, HTTP_STATUS_504);

    private static final String ERROR_MESSAGE_REFUSED = "Connection refused";
    private static final String ERROR_MESSAGE_RESET = "Connection reset";
    private static final String ERROR_MESSAGE_CLOSED = "Remote host closed connection during handshake";
    private static final List<String> NON_FATAL_ERROR_MESSAGES = Arrays.asList(
            ERROR_MESSAGE_REFUSED, ERROR_MESSAGE_RESET, ERROR_MESSAGE_CLOSED);

    private static final String BUY_FEE_CONFIG_ITEM_KEY = "buy-fee";
    private static final String BUY_FEE_CONFIG_ITEM_VALUE = "0.20";
    private static final String SELL_FEE_CONFIG_ITEM_KEY = "sell-fee";
    private static final String SELL_FEE_CONFIG_ITEM_VALUE = "0.25";

    @MockBean
    ExchangeConfigService exchangeConfigService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void whenGetExchangeConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(exchangeConfigService.getExchangeConfig(BOT_ID)).willReturn(someExchangeConfig());

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + BOT_ID + EXCHANGE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.exchangeName").value(EXCHANGE_NAME))
                .andExpect(jsonPath("$.data.exchangeAdapter").value(EXCHANGE_ADAPTER))

                // REST API does not expose AuthenticationConfig - potential security risk.
                .andExpect(jsonPath("$.data.authenticationConfig").doesNotExist())

                .andExpect(jsonPath("$.data.networkConfig.connectionTimeout").value(CONNECTION_TIMEOUT))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorCodes[0]").value(HTTP_STATUS_502))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorCodes[1]").value(HTTP_STATUS_503))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorCodes[2]").value(HTTP_STATUS_504))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorMessages[0]").value(ERROR_MESSAGE_REFUSED))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorMessages[1]").value(ERROR_MESSAGE_RESET))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorMessages[2]").value(ERROR_MESSAGE_CLOSED))

                .andExpect(jsonPath("$.data.optionalConfig.items.buy-fee").value(BUY_FEE_CONFIG_ITEM_VALUE))
                .andExpect(jsonPath("$.data.optionalConfig.items.sell-fee").value(SELL_FEE_CONFIG_ITEM_VALUE));

        verify(exchangeConfigService, times(1)).getExchangeConfig(BOT_ID);
    }

    @Test
    public void whenGetExchangeConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(exchangeConfigService.getExchangeConfig(UNKNOWN_BOT_ID)).willReturn(null); // none found!

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + UNKNOWN_BOT_ID + EXCHANGE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(exchangeConfigService, times(1)).getExchangeConfig(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetExchangeConfigCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + BOT_ID + EXCHANGE_RESOURCE_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetExchangeConfigCalledWithoutBotIdThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + EXCHANGE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateExchangeConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        final ExchangeConfig updatedConfig = someExchangeConfig();
        given(exchangeConfigService.updateExchangeConfig(eq(BOT_ID), any())).willReturn(updatedConfig);

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + BOT_ID + EXCHANGE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(updatedConfig)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.exchangeName").value(EXCHANGE_NAME))
                .andExpect(jsonPath("$.data.exchangeAdapter").value(EXCHANGE_ADAPTER))

                // REST API does not expose AuthenticationConfig - potential security risk.
                .andExpect(jsonPath("$.data.authenticationConfig").doesNotExist())

                .andExpect(jsonPath("$.data.networkConfig.connectionTimeout").value(CONNECTION_TIMEOUT))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorCodes[0]").value(HTTP_STATUS_502))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorCodes[1]").value(HTTP_STATUS_503))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorCodes[2]").value(HTTP_STATUS_504))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorMessages[0]").value(ERROR_MESSAGE_REFUSED))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorMessages[1]").value(ERROR_MESSAGE_RESET))
                .andExpect(jsonPath("$.data.networkConfig.nonFatalErrorMessages[2]").value(ERROR_MESSAGE_CLOSED))

                .andExpect(jsonPath("$.data.optionalConfig.items.buy-fee").value(BUY_FEE_CONFIG_ITEM_VALUE))
                .andExpect(jsonPath("$.data.optionalConfig.items.sell-fee").value(SELL_FEE_CONFIG_ITEM_VALUE));

        verify(exchangeConfigService, times(1)).updateExchangeConfig(eq(BOT_ID), any());
    }

    @Test
    public void whenUpdateExchangeConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        final ExchangeConfig updatedConfig = someExchangeConfig();
        given(exchangeConfigService.updateExchangeConfig(UNKNOWN_BOT_ID, updatedConfig)).willReturn(null);

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + UNKNOWN_BOT_ID + EXCHANGE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(updatedConfig)))
                .andExpect(status().isNotFound());

        verify(exchangeConfigService, times(1)).updateExchangeConfig(eq(UNKNOWN_BOT_ID), any());
    }

    @Test
    public void whenUpdateExchangeConfigCalledForKnownBotIdAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + BOT_ID + EXCHANGE_RESOURCE_PATH)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someExchangeConfig())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateExchangeConfigCalledAndUserIsNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + BOT_ID + EXCHANGE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someExchangeConfig())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenUpdateExchangeConfigCalledWithMissingBotIdThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + EXCHANGE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someExchangeConfig())))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    private static ExchangeConfig someExchangeConfig() {

        final NetworkConfig networkConfig = new NetworkConfig();
        networkConfig.setConnectionTimeout(CONNECTION_TIMEOUT);
        networkConfig.setNonFatalErrorCodes(NON_FATAL_ERROR_CODES);
        networkConfig.setNonFatalErrorMessages(NON_FATAL_ERROR_MESSAGES);

        final OptionalConfig optionalConfig = new OptionalConfig();
        optionalConfig.getItems().put(BUY_FEE_CONFIG_ITEM_KEY, BUY_FEE_CONFIG_ITEM_VALUE);
        optionalConfig.getItems().put(SELL_FEE_CONFIG_ITEM_KEY, SELL_FEE_CONFIG_ITEM_VALUE);

        final ExchangeConfig exchangeConfig = new ExchangeConfig();
        exchangeConfig.setExchangeName(EXCHANGE_NAME);
        exchangeConfig.setExchangeAdapter(EXCHANGE_ADAPTER);
        exchangeConfig.setNetworkConfig(networkConfig);
        exchangeConfig.setOptionalConfig(optionalConfig);

        return exchangeConfig;
    }
}

