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
import com.gazbert.bxbot.ui.server.services.MarketConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the Market config controller behaviour.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestMarketConfigController extends AbstractConfigControllerTest {

    private static final String MARKETS_CONFIG_ENDPOINT_URI = "/api/v1/config/markets/";

    private static final String BOT_ID_PARAM = "botId";
    private static final String BOT_ID = "gdax-bot-1";
    private static final String UNKNOWN_BOT_ID = "unknown-bot-id";

    private static final String MARKET_1_ID = "btc_usd";
    private static final String MARKET_1_NAME = "BTC/USD";
    private static final String MARKET_1_BASE_CURRENCY = "BTC";
    private static final String MARKET_1_COUNTER_CURRENCY = "USD";
    private static final boolean MARKET_1_ENABLED = true;
    private static final String MARKET_1_STRATEGY_ID = "scalper-strategy";

    private static final String MARKET_2_ID = "btc_gbp";
    private static final String MARKET_2_NAME = "BTC/GBP";
    private static final String MARKET_2_BASE_CURRENCY = "BTC";
    private static final String MARKET_2_COUNTER_CURRENCY = "GBP";
    private static final boolean MARKET_2_ENABLED = false;
    private static final String MARKET_2_STRATEGY_ID = "macd-strategy";

    private MarketConfig marketConfig_1;
    private MarketConfig marketConfig_2;

    @MockBean
    MarketConfigService marketConfigService;


    @Before
    public void setupBeforeEachTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(springSecurityFilterChain).build();

        marketConfig_1 = new MarketConfig(MARKET_1_ID, MARKET_1_NAME, MARKET_1_BASE_CURRENCY,
                MARKET_1_COUNTER_CURRENCY, MARKET_1_ENABLED, MARKET_1_STRATEGY_ID);

        marketConfig_2 = new MarketConfig(MARKET_2_ID, MARKET_2_NAME, MARKET_2_BASE_CURRENCY,
                MARKET_2_COUNTER_CURRENCY, MARKET_2_ENABLED, MARKET_2_STRATEGY_ID);
    }

    @Test
    public void whenGetAllMarketConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(marketConfigService.getAllMarketConfig(BOT_ID)).willReturn(allTheMarketConfig());

        mockMvc.perform(get(MARKETS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.[0].id").value(MARKET_1_ID))
                .andExpect(jsonPath("$.data.[0].name").value(MARKET_1_NAME))
                .andExpect(jsonPath("$.data.[0].enabled").value(MARKET_1_ENABLED))
                .andExpect(jsonPath("$.data.[0].baseCurrency").value(MARKET_1_BASE_CURRENCY))
                .andExpect(jsonPath("$.data.[0].counterCurrency").value(MARKET_1_COUNTER_CURRENCY))
                .andExpect(jsonPath("$.data.[0].tradingStrategyId").value(MARKET_1_STRATEGY_ID))

                .andExpect(jsonPath("$.data.[1].id").value(MARKET_2_ID))
                .andExpect(jsonPath("$.data.[1].name").value(MARKET_2_NAME))
                .andExpect(jsonPath("$.data.[1].enabled").value(MARKET_2_ENABLED))
                .andExpect(jsonPath("$.data.[1].baseCurrency").value(MARKET_2_BASE_CURRENCY))
                .andExpect(jsonPath("$.data.[1].counterCurrency").value(MARKET_2_COUNTER_CURRENCY))
                .andExpect(jsonPath("$.data.[1].tradingStrategyId").value(MARKET_2_STRATEGY_ID));

        verify(marketConfigService, times(1)).getAllMarketConfig(BOT_ID);
    }

    @Test
    public void whenGetAllMarketConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(marketConfigService.getAllMarketConfig(UNKNOWN_BOT_ID)).willReturn(new ArrayList<>()); // none found!

        mockMvc.perform(get(MARKETS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(marketConfigService, times(1)).getAllMarketConfig(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetAllMarketConfigCalledAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(get(MARKETS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + BOT_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetAllMarketConfigCalledWithMissingBotIdThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(get(MARKETS_CONFIG_ENDPOINT_URI)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenGetMarketConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(marketConfigService.getMarketConfig(BOT_ID, MARKET_1_ID)).willReturn(marketConfig_1);

        mockMvc.perform(get(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.id").value(MARKET_1_ID))
                .andExpect(jsonPath("$.data.name").value(MARKET_1_NAME))
                .andExpect(jsonPath("$.data.enabled").value(MARKET_1_ENABLED))
                .andExpect(jsonPath("$.data.baseCurrency").value(MARKET_1_BASE_CURRENCY))
                .andExpect(jsonPath("$.data.counterCurrency").value(MARKET_1_COUNTER_CURRENCY))
                .andExpect(jsonPath("$.data.tradingStrategyId").value(MARKET_1_STRATEGY_ID));

        verify(marketConfigService, times(1)).getMarketConfig(BOT_ID, MARKET_1_ID);
    }

    @Test
    public void whenGetMarketConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(marketConfigService.getMarketConfig(UNKNOWN_BOT_ID, MARKET_1_ID)).willReturn(null);

        mockMvc.perform(get(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(marketConfigService, times(1)).getMarketConfig(UNKNOWN_BOT_ID, MARKET_1_ID);
    }

    @Test
    public void whenGetMarketConfigCalledAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(get(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetMarketConfigCalledWithMissingBotIdThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(get(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenUpdateMarketConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(marketConfigService.updateMarketConfig(BOT_ID, marketConfig_1)).willReturn(marketConfig_1);

        mockMvc.perform(put(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(marketConfig_1)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.id").value(MARKET_1_ID))
                .andExpect(jsonPath("$.data.name").value(MARKET_1_NAME))
                .andExpect(jsonPath("$.data.enabled").value(MARKET_1_ENABLED))
                .andExpect(jsonPath("$.data.baseCurrency").value(MARKET_1_BASE_CURRENCY))
                .andExpect(jsonPath("$.data.counterCurrency").value(MARKET_1_COUNTER_CURRENCY))
                .andExpect(jsonPath("$.data.tradingStrategyId").value(MARKET_1_STRATEGY_ID));

        verify(marketConfigService, times(1)).updateMarketConfig(BOT_ID, marketConfig_1);
    }

    @Test
    public void whenUpdateMarketConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(marketConfigService.updateMarketConfig(UNKNOWN_BOT_ID, marketConfig_1)).willReturn(null);

        mockMvc.perform(put(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(marketConfig_1)))
                .andExpect(status().isNotFound());

        verify(marketConfigService, times(1)).updateMarketConfig(UNKNOWN_BOT_ID, marketConfig_1);
    }

    @Test
    public void whenUpdateMarketConfigCalledAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(put(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .contentType(CONTENT_TYPE)
                .content(jsonify(marketConfig_1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateMarketConfigCalledAndUserNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(put(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(marketConfig_1)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenUpdateMarketConfigCalledWithMissingBotIdThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(put(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(marketConfig_1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenUpdateMarketConfigCalledWithBotIdMismatchThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(put(MARKETS_CONFIG_ENDPOINT_URI + MARKET_2_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(marketConfig_1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenDeleteMarketConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(marketConfigService.deleteMarketConfig(BOT_ID, MARKET_1_ID)).willReturn(true);

        mockMvc.perform(delete(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD)))
                .andExpect(status().isNoContent());

        verify(marketConfigService, times(1)).deleteMarketConfig(BOT_ID, MARKET_1_ID);
    }

    @Test
    public void whenDeleteMarketConfigCalledAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(delete(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenDeleteMarketConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(marketConfigService.deleteMarketConfig(UNKNOWN_BOT_ID, MARKET_1_ID)).willReturn(false);

        mockMvc.perform(delete(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD)))
                .andExpect(status().isNotFound());

        verify(marketConfigService, times(1)).deleteMarketConfig(UNKNOWN_BOT_ID, MARKET_1_ID);
    }

    @Test
    public void whenDeleteMarketConfigCalledAndUserIsNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(delete(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenDeleteMarketConfigCalledWithMissingBotIdThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(delete(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenCreateMarketConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        final MarketConfig createdConfig = someNewMarketConfig();
        given(marketConfigService.createMarketConfig(BOT_ID, createdConfig)).willReturn(marketConfig_2);

        mockMvc.perform(post(MARKETS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(createdConfig)))
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.data.id").value(MARKET_2_ID))
                .andExpect(jsonPath("$.data.name").value(MARKET_2_NAME))
                .andExpect(jsonPath("$.data.enabled").value(MARKET_2_ENABLED))
                .andExpect(jsonPath("$.data.baseCurrency").value(MARKET_2_BASE_CURRENCY))
                .andExpect(jsonPath("$.data.counterCurrency").value(MARKET_2_COUNTER_CURRENCY))
                .andExpect(jsonPath("$.data.tradingStrategyId").value(MARKET_2_STRATEGY_ID));

        verify(marketConfigService, times(1)).createMarketConfig(BOT_ID, createdConfig);
    }

    @Test
    public void whenCreateMarketConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        final MarketConfig createdConfig = someNewMarketConfig();
        given(marketConfigService.createMarketConfig(UNKNOWN_BOT_ID, createdConfig)).willReturn(null);

        mockMvc.perform(post(MARKETS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(createdConfig)))
                .andExpect(status().isNotFound());

        verify(marketConfigService, times(1)).createMarketConfig(UNKNOWN_BOT_ID, createdConfig);
    }

    @Test
    public void whenCreateMarketConfigCalledAndUserIsNotAuthenticatedThenExpectNotFoundResponse() throws Exception {

        mockMvc.perform(post(MARKETS_CONFIG_ENDPOINT_URI + MARKET_1_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someNewMarketConfig())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenCreateMarketConfigCalledAndUserIsNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(post(MARKETS_CONFIG_ENDPOINT_URI + "?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someNewMarketConfig())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenCreateMarketConfigCalledWithMissingBotIdThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(post(MARKETS_CONFIG_ENDPOINT_URI)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someNewMarketConfig())))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private List<MarketConfig> allTheMarketConfig() {
        final List<MarketConfig> allMarkets = new ArrayList<>();
        allMarkets.add(marketConfig_1);
        allMarkets.add(marketConfig_2);
        return allMarkets;
    }

    private static MarketConfig someNewMarketConfig() {
        return new MarketConfig(null, MARKET_2_NAME, MARKET_2_BASE_CURRENCY,
                MARKET_2_COUNTER_CURRENCY, MARKET_2_ENABLED, MARKET_2_STRATEGY_ID);
    }
}
