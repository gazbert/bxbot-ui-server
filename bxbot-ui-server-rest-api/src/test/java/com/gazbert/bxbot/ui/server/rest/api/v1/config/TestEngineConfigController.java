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
import com.gazbert.bxbot.ui.server.services.config.EngineConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the Engine config controller behaviour.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestEngineConfigController extends AbstractConfigControllerTest {

    private static final String ENGINE_RESOURCE_PATH = "/engine";

    private static final String UNKNOWN_BOT_ID = "unknown-bot-id";
    private static final String BOT_ID = "bitstamp-bot-1";
    private static final String BOT_NAME = "Bitstamp Bot";
    private static final String ENGINE_EMERGENCY_STOP_CURRENCY = "BTC";
    private static final BigDecimal ENGINE_EMERGENCY_STOP_BALANCE = new BigDecimal("0.923232");
    private static final int ENGINE_TRADE_CYCLE_INTERVAL = 60;

    @MockBean
    EngineConfigService engineConfigService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void whenGetEngineConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(engineConfigService.getEngineConfig(BOT_ID)).willReturn(someEngineConfig());
        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + BOT_ID + ENGINE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.botId").value(BOT_ID))
                .andExpect(jsonPath("$.data.botName").value(BOT_NAME))
                .andExpect(jsonPath("$.data.tradeCycleInterval").value(ENGINE_TRADE_CYCLE_INTERVAL))
                .andExpect(jsonPath("$.data.emergencyStopCurrency").value(ENGINE_EMERGENCY_STOP_CURRENCY))
                .andExpect(jsonPath("$.data.emergencyStopBalance").value(ENGINE_EMERGENCY_STOP_BALANCE));

        verify(engineConfigService, times(1)).getEngineConfig(BOT_ID);
    }

    @Test
    public void whenGetEngineConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(engineConfigService.getEngineConfig(UNKNOWN_BOT_ID)).willReturn(null); // none found!

        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + UNKNOWN_BOT_ID + ENGINE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(engineConfigService, times(1)).getEngineConfig(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetEngineConfigCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + BOT_ID + ENGINE_RESOURCE_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetEngineConfigCalledWithoutBotIdThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get(CONFIG_ENDPOINT_BASE_URI + ENGINE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateEngineConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        final EngineConfig updatedConfig = someEngineConfig();
        given(engineConfigService.updateEngineConfig(eq(BOT_ID), any())).willReturn(updatedConfig);

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + BOT_ID + ENGINE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(updatedConfig)))
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.botId").value(BOT_ID))
                .andExpect(jsonPath("$.data.botName").value(BOT_NAME))
                .andExpect(jsonPath("$.data.tradeCycleInterval").value(ENGINE_TRADE_CYCLE_INTERVAL))
                .andExpect(jsonPath("$.data.emergencyStopCurrency").value(ENGINE_EMERGENCY_STOP_CURRENCY))
                .andExpect(jsonPath("$.data.emergencyStopBalance").value(ENGINE_EMERGENCY_STOP_BALANCE));

        verify(engineConfigService, times(1)).updateEngineConfig(eq(BOT_ID), any());
    }

    @Test
    public void whenUpdateEngineConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        final EngineConfig updatedConfig = someEngineConfig();
        given(engineConfigService.updateEngineConfig(UNKNOWN_BOT_ID, updatedConfig)).willReturn(null);

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + UNKNOWN_BOT_ID + ENGINE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(updatedConfig)))
                .andExpect(status().isNotFound());

        verify(engineConfigService, times(1)).updateEngineConfig(eq(UNKNOWN_BOT_ID), any());
    }

    @Test
    public void whenUpdateEngineConfigCalledForKnownBotIdAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + BOT_ID + ENGINE_RESOURCE_PATH)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someEngineConfig())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateEngineConfigCalledAndUserIsNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + BOT_ID + ENGINE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someEngineConfig())))
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenUpdateEngineConfigCalledWithMissingBotIdThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(put(CONFIG_ENDPOINT_BASE_URI + ENGINE_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someEngineConfig())))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------

    private static EngineConfig someEngineConfig() {
        final EngineConfig engineConfig = new EngineConfig();
        engineConfig.setBotId(BOT_ID);
        engineConfig.setBotName(BOT_NAME);
        engineConfig.setEmergencyStopCurrency(ENGINE_EMERGENCY_STOP_CURRENCY);
        engineConfig.setEmergencyStopBalance(ENGINE_EMERGENCY_STOP_BALANCE);
        engineConfig.setTradeCycleInterval(ENGINE_TRADE_CYCLE_INTERVAL);
        return engineConfig;
    }
}

