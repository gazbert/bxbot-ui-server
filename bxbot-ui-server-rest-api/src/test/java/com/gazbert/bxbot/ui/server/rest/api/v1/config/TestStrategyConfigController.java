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

import com.gazbert.bxbot.ui.server.domain.strategy.StrategyConfig;
import com.gazbert.bxbot.ui.server.services.config.StrategyConfigService;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the Strategy config controller behaviour.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestStrategyConfigController extends AbstractConfigControllerTest {

    private static final String STRATEGIES_RESOURCE_PATH = "/strategies";

    private static final String BOT_ID = "gdax-bot-1";
    private static final String UNKNOWN_BOT_ID = "unknown-bot-id";

    private static final String STRAT_1_ID = "macd-long-position";
    private static final String STRAT_1_NAME = "MACD Strat Algo";
    private static final String STRAT_1_DESCRIPTION = "Uses MACD as indicator and takes long position in base currency.";
    private static final String STRAT_1_CLASSNAME = "com.gazbert.nova.algos.MacdLongBase";

    private static final String STRAT_2_ID = "long-scalper";
    private static final String STRAT_2_NAME = "Long Position Scalper Algo";
    private static final String STRAT_2_DESCRIPTION = "Scalps and goes long...";
    private static final String STRAT_2_CLASSNAME = "com.gazbert.nova.algos.LongScalper";

    private static final String BUY_PRICE_CONFIG_ITEM_KEY = "buy-price";
    private static final String BUY_PRICE_CONFIG_ITEM_VALUE = "671.15";
    private static final String AMOUNT_TO_BUY_CONFIG_ITEM_KEY = "buy-amount";
    private static final String AMOUNT_TO_BUY_CONFIG_ITEM_VALUE = "0.5";

    private StrategyConfig strategyConfig_1;
    private StrategyConfig strategyConfig_2;

    @MockBean
    StrategyConfigService strategyConfigService;


    @Before
    public void setupBeforeEachTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(springSecurityFilterChain).build();
        strategyConfig_1 = new StrategyConfig(STRAT_1_ID, STRAT_1_NAME, STRAT_1_DESCRIPTION, STRAT_1_CLASSNAME, someConfigItems());
        strategyConfig_2 = new StrategyConfig(STRAT_2_ID, STRAT_2_NAME, STRAT_2_DESCRIPTION, STRAT_2_CLASSNAME, someConfigItems());
    }

    @Test
    public void whenGetAllStrategyConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(strategyConfigService.getAllStrategyConfig(BOT_ID)).willReturn(allTheStrategiesConfig());

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.[0].id").value(STRAT_1_ID))
                .andExpect(jsonPath("$.data.[0].name").value(STRAT_1_NAME))
                .andExpect(jsonPath("$.data.[0].description").value(STRAT_1_DESCRIPTION))
                .andExpect(jsonPath("$.data.[0].className").value(STRAT_1_CLASSNAME))
                .andExpect(jsonPath("$.data.[0].optionalConfig.buy-price").value(BUY_PRICE_CONFIG_ITEM_VALUE))
                .andExpect(jsonPath("$.data.[0].optionalConfig.buy-amount").value(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE))

                .andExpect(jsonPath("$.data.[1].id").value(STRAT_2_ID))
                .andExpect(jsonPath("$.data.[1].name").value(STRAT_2_NAME))
                .andExpect(jsonPath("$.data.[1].description").value(STRAT_2_DESCRIPTION))
                .andExpect(jsonPath("$.data.[1].className").value(STRAT_2_CLASSNAME))
                .andExpect(jsonPath("$.data.[1].optionalConfig.buy-price").value(BUY_PRICE_CONFIG_ITEM_VALUE))
                .andExpect(jsonPath("$.data.[1].optionalConfig.buy-amount").value(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE)
                );

        verify(strategyConfigService, times(1)).getAllStrategyConfig(BOT_ID);
    }

    @Test
    public void whenGetAllStrategyConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(strategyConfigService.getAllStrategyConfig(UNKNOWN_BOT_ID)).willReturn(new ArrayList<>()); // none found!

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + UNKNOWN_BOT_ID + STRATEGIES_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(strategyConfigService, times(1)).getAllStrategyConfig(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetAllStrategyConfigCalledAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetAllStrategyConfigCalledWithMissingBotIdThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + STRATEGIES_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetStrategyConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(strategyConfigService.getStrategyConfig(BOT_ID, STRAT_1_ID)).willReturn(strategyConfig_1);

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.id").value(STRAT_1_ID))
                .andExpect(jsonPath("$.data.name").value(STRAT_1_NAME))
                .andExpect(jsonPath("$.data.description").value(STRAT_1_DESCRIPTION))
                .andExpect(jsonPath("$.data.className").value(STRAT_1_CLASSNAME))
                .andExpect(jsonPath("$.data.optionalConfig.buy-price").value(BUY_PRICE_CONFIG_ITEM_VALUE))
                .andExpect(jsonPath("$.data.optionalConfig.buy-amount").value(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));

        verify(strategyConfigService, times(1)).getStrategyConfig(BOT_ID, STRAT_1_ID);
    }

    @Test
    public void whenGetStrategyConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(strategyConfigService.getStrategyConfig(UNKNOWN_BOT_ID, STRAT_1_ID)).willReturn(null);

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + UNKNOWN_BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(strategyConfigService, times(1)).getStrategyConfig(UNKNOWN_BOT_ID, STRAT_1_ID);
    }

    @Test
    public void whenGetStrategyConfigCalledAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetStrategyConfigCalledWithMissingBotIdThenExpectNotFoundResponse() throws Exception {

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenUpdateStrategyConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(strategyConfigService.updateStrategyConfig(BOT_ID, strategyConfig_1)).willReturn(strategyConfig_1);

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(strategyConfig_1)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.id").value(STRAT_1_ID))
                .andExpect(jsonPath("$.data.name").value(STRAT_1_NAME))
                .andExpect(jsonPath("$.data.description").value(STRAT_1_DESCRIPTION))
                .andExpect(jsonPath("$.data.className").value(STRAT_1_CLASSNAME))
                .andExpect(jsonPath("$.data.optionalConfig.buy-price").value(BUY_PRICE_CONFIG_ITEM_VALUE))
                .andExpect(jsonPath("$.data.optionalConfig.buy-amount").value(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));

        verify(strategyConfigService, times(1)).updateStrategyConfig(BOT_ID, strategyConfig_1);
    }

    @Test
    public void whenUpdateStrategyConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(strategyConfigService.updateStrategyConfig(UNKNOWN_BOT_ID, strategyConfig_1)).willReturn(null);

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + UNKNOWN_BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(strategyConfig_1)))
                .andExpect(status().isNotFound());

        verify(strategyConfigService, times(1)).updateStrategyConfig(UNKNOWN_BOT_ID, strategyConfig_1);
    }

    @Test
    public void whenUpdateStrategyConfigCalledAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .contentType(CONTENT_TYPE)
                .content(jsonify(strategyConfig_1)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateStrategyConfigCalledAndUserNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(strategyConfig_1)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenUpdateStrategyConfigCalledWithMissingBotIdThenExpectNotFoundResponse() throws Exception {

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(strategyConfig_1)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenUpdateStrategyConfigCalledWithBotIdMismatchThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_2_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(strategyConfig_1)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenDeleteStrategyConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(strategyConfigService.deleteStrategyConfig(BOT_ID, STRAT_1_ID)).willReturn(true);

        mockMvc.perform(delete(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD)))
                .andExpect(status().isNoContent());

        verify(strategyConfigService, times(1)).deleteStrategyConfig(BOT_ID, STRAT_1_ID);
    }

    @Test
    public void whenDeleteStrategyConfigCalledAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(delete(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenDeleteStrategyConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(strategyConfigService.deleteStrategyConfig(UNKNOWN_BOT_ID, STRAT_1_ID)).willReturn(false);

        mockMvc.perform(delete(CONFIG_ENDPOINT_BASE_URI + UNKNOWN_BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD)))
                .andExpect(status().isNotFound());

        verify(strategyConfigService, times(1)).deleteStrategyConfig(UNKNOWN_BOT_ID, STRAT_1_ID);
    }

    @Test
    public void whenDeleteStrategyConfigCalledAndUserIsNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(delete(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenDeleteStrategyConfigCalledWithMissingBotIdThenExpectNotFoundResponse() throws Exception {

        mockMvc.perform(delete(CONFIG_ENDPOINT_BASE_URI + STRATEGIES_RESOURCE_PATH + "/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void whenCreateStrategyConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        final StrategyConfig createdConfig = someNewStrategyConfig();
        given(strategyConfigService.createStrategyConfig(BOT_ID, createdConfig)).willReturn(strategyConfig_1);

        mockMvc.perform(post(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(createdConfig)))
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.data.id").value(STRAT_1_ID))
                .andExpect(jsonPath("$.data.name").value(STRAT_1_NAME))
                .andExpect(jsonPath("$.data.description").value(STRAT_1_DESCRIPTION))
                .andExpect(jsonPath("$.data.className").value(STRAT_1_CLASSNAME))
                .andExpect(jsonPath("$.data.optionalConfig.buy-price").value(BUY_PRICE_CONFIG_ITEM_VALUE))
                .andExpect(jsonPath("$.data.optionalConfig.buy-amount").value(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE));

        verify(strategyConfigService, times(1)).createStrategyConfig(BOT_ID, createdConfig);
    }

    @Test
    public void whenCreateStrategyConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        final StrategyConfig createdConfig = someNewStrategyConfig();
        given(strategyConfigService.createStrategyConfig(UNKNOWN_BOT_ID, createdConfig)).willReturn(null);

        mockMvc.perform(post(CONFIG_ENDPOINT_BASE_URI + UNKNOWN_BOT_ID + STRATEGIES_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(createdConfig)))
                .andExpect(status().isNotFound());

        verify(strategyConfigService, times(1)).createStrategyConfig(UNKNOWN_BOT_ID, createdConfig);
    }

    @Test
    public void whenCreateStrategyConfigCalledAndUserIsNotAuthenticatedThenExpectNotFoundResponse() throws Exception {

        mockMvc.perform(post(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someNewStrategyConfig())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenCreateStrategyConfigCalledAndUserIsNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(post(CONFIG_ENDPOINT_BASE_URI + BOT_ID + STRATEGIES_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someNewStrategyConfig())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenCreateStrategyConfigCalledWithMissingBotIdThenExpectNoSuchMethodResponse() throws Exception {

        mockMvc.perform(post(CONFIG_ENDPOINT_BASE_URI + STRATEGIES_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someNewStrategyConfig())))
                .andExpect(status().is(405));
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private List<StrategyConfig> allTheStrategiesConfig() {
        final List<StrategyConfig> allStrategies = new ArrayList<>();
        allStrategies.add(strategyConfig_1);
        allStrategies.add(strategyConfig_2);
        return allStrategies;
    }

    private static StrategyConfig someNewStrategyConfig() {
        final Map<String, String> optionalConfig = new HashMap<>();
        optionalConfig.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        optionalConfig.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return new StrategyConfig(null, STRAT_1_NAME, STRAT_1_DESCRIPTION, STRAT_1_CLASSNAME, optionalConfig);
    }

    private static Map<String, String> someConfigItems() {
        final Map<String, String> optionalConfig = new HashMap<>();
        optionalConfig.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        optionalConfig.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return optionalConfig;
    }
}
