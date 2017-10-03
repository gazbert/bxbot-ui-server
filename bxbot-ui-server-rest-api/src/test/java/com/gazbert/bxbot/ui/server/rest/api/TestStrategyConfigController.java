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

import com.gazbert.bxbot.ui.server.domain.strategy.StrategyConfig;
import com.gazbert.bxbot.ui.server.services.StrategyConfigService;
import org.junit.Before;
import org.junit.Ignore;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
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

    private static final String BOT_ID_PARAM = "botId";

    private static final String BOT_ID = "gdax-bot-1";
    private static final String UNKNOWN_BOT_ID = "unknown-bot-id";

    private static final String UNKNOWN_STRAT_ID = "unknown-strat-id";

    private static final String STRAT_1_ID = "macd-long-position";
    private static final String STRAT_1_LABEL= "MACD Strat Algo";
    private static final String STRAT_1_DESCRIPTION = "Uses MACD as indicator and takes long position in base currency.";
    private static final String STRAT_1_CLASSNAME = "com.gazbert.nova.algos.MacdLongBase";

    private static final String STRAT_2_ID = "long-scalper";
    private static final String STRAT_2_LABEL= "Long Position Scalper Algo";
    private static final String STRAT_2_DESCRIPTION = "Scalps and goes long...";
    private static final String STRAT_2_CLASSNAME = "com.gazbert.nova.algos.LongScalper";

    private static final String BUY_PRICE_CONFIG_ITEM_KEY = "buy-price";
    private static final String BUY_PRICE_CONFIG_ITEM_VALUE = "671.15";
    private static final String AMOUNT_TO_BUY_CONFIG_ITEM_KEY = "buy-amount";
    private static final String AMOUNT_TO_BUY_CONFIG_ITEM_VALUE = "0.5";

    @MockBean
    StrategyConfigService strategyConfigService;


    @Before
    public void setupBeforeEachTest() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void whenGetAllStrategyConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(strategyConfigService.getAllStrategyConfig(any())).willReturn(allTheStrategiesConfig());

        mockMvc.perform(get("/api/config/strategies/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.[0].id").value(STRAT_1_ID))
                .andExpect(jsonPath("$.data.[0].name").value(STRAT_1_LABEL))
                .andExpect(jsonPath("$.data.[0].description").value(STRAT_1_DESCRIPTION))
                .andExpect(jsonPath("$.data.[0].className").value(STRAT_1_CLASSNAME))
                .andExpect(jsonPath("$.data.[0].configItems.buy-price").value(BUY_PRICE_CONFIG_ITEM_VALUE))
                .andExpect(jsonPath("$.data.[0].configItems.buy-amount").value(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE))

                .andExpect(jsonPath("$.data.[1].id").value(STRAT_2_ID))
                .andExpect(jsonPath("$.data.[1].name").value(STRAT_2_LABEL))
                .andExpect(jsonPath("$.data.[1].description").value(STRAT_2_DESCRIPTION))
                .andExpect(jsonPath("$.data.[1].className").value(STRAT_2_CLASSNAME))
                .andExpect(jsonPath("$.data.[1].configItems.buy-price").value(BUY_PRICE_CONFIG_ITEM_VALUE))
                .andExpect(jsonPath("$.data.[1].configItems.buy-amount").value(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE)
                );

        verify(strategyConfigService, times(1)).getAllStrategyConfig(BOT_ID);
    }

    public void whenGetAllStrategyConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(strategyConfigService.getAllStrategyConfig(UNKNOWN_BOT_ID)).willReturn(new ArrayList<>()); // none found!

        mockMvc.perform(get("/api/config/strategies/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(strategyConfigService, times(1)).getAllStrategyConfig(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetAllStrategyConfigCalledAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(get("/api/config/strategies/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGeStrategyConfigCalledForKnownBotIdAndUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(strategyConfigService.getStrategyConfig(BOT_ID, STRAT_1_ID)).willReturn(someStrategyConfig());

        mockMvc.perform(get("/api/config/strategies/" + STRAT_1_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.id").value(STRAT_1_ID))
                .andExpect(jsonPath("$.data.name").value(STRAT_1_LABEL))
                .andExpect(jsonPath("$.data.description").value(STRAT_1_DESCRIPTION))
                .andExpect(jsonPath("$.data.className").value(STRAT_1_CLASSNAME))
                .andExpect(jsonPath("$.data.configItems.buy-price").value(BUY_PRICE_CONFIG_ITEM_VALUE))
                .andExpect(jsonPath("$.data.configItems.buy-amount").value(AMOUNT_TO_BUY_CONFIG_ITEM_VALUE)
                );

        verify(strategyConfigService, times(1)).getStrategyConfig(BOT_ID, STRAT_1_ID);
    }

    @Test
    public void whenGeStrategyConfigCalledForUnknownBotIdAndUserIsAuthenticatedThenExpectNotFoundResponse() throws Exception {

        given(strategyConfigService.getStrategyConfig(UNKNOWN_BOT_ID, STRAT_1_ID)).willReturn(null);

        mockMvc.perform(get("/api/config/strategies/" + STRAT_1_ID + "/?" + BOT_ID_PARAM + "=" + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(strategyConfigService, times(1)).getStrategyConfig(UNKNOWN_BOT_ID, STRAT_1_ID);
    }

    @Test
    public void whenGetStrategyConfigCalledAndUserIsNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(get("/api/config/strategies/" + STRAT_1_ID + "/?" + BOT_ID_PARAM + "=" + BOT_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testUpdateStrategyConfig() throws Exception {

        given(strategyConfigService.updateStrategyConfig("botId", someStrategyConfig())).willReturn(someStrategyConfig());

        mockMvc.perform(put("/api/config/strategy/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someStrategyConfig())))
                .andExpect(status().isNoContent());
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testUpdateStrategyConfigWhenUnauthorized() throws Exception {

        mockMvc.perform(put("/api/config/strategy/" + STRAT_1_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someStrategyConfig())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("unauthorized")));
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testUpdateStrategyConfigWhenIdNotRecognized() throws Exception {

        given(strategyConfigService.updateStrategyConfig("botId", unrecognizedStrategyConfig())).willReturn(emptyStrategyConfig());

        mockMvc.perform(put("/api/config/strategy/" + UNKNOWN_STRAT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(CONTENT_TYPE)
                .content(jsonify(unrecognizedStrategyConfig())))
                .andExpect(status().isNotFound());
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testUpdateStrategyConfigWhenIdIsMissing() throws Exception {

        mockMvc.perform(put("/api/config/strategy/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someStrategyConfigWithMissingId())))
                .andExpect(status().isBadRequest());
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testDeleteStrategyConfig() throws Exception {

        given(strategyConfigService.deleteStrategyConfig("botId", STRAT_1_ID)).willReturn(someStrategyConfig());

        mockMvc.perform(delete("/api/config/strategy/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD)))
                .andExpect(status().isNoContent());
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testDeleteStrategyConfigWhenUnauthorized() throws Exception {

        mockMvc.perform(delete("/api/config/strategy/" + STRAT_1_ID)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("unauthorized")));
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testDeleteStrategyConfigWhenIdNotRecognized() throws Exception {

        given(strategyConfigService.deleteStrategyConfig("botId", UNKNOWN_STRAT_ID)).willReturn(emptyStrategyConfig());

        mockMvc.perform(delete("/api/config/strategy/" + UNKNOWN_STRAT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testCreateStrategyConfig() throws Exception {

        given(strategyConfigService.createStrategyConfig("botId", someStrategyConfig())).willReturn(someStrategyConfig());

        mockMvc.perform(post("/api/config/strategy/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someStrategyConfig())))
                .andExpect(status().isCreated());
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testCreateStrategyConfigWhenUnauthorized() throws Exception {

        mockMvc.perform(post("/api/config/strategy/" + STRAT_1_ID)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someStrategyConfig())))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error", is("unauthorized")));
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testCreateStrategyConfigWhenIdAlreadyExists() throws Exception {

        given(strategyConfigService.createStrategyConfig("botId", someStrategyConfig())).willReturn(emptyStrategyConfig());

        mockMvc.perform(post("/api/config/strategy/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someStrategyConfig())))
                .andExpect(status().isConflict());
    }

    @Ignore("Ignore til I get JWT up and running!")
    @Test
    public void testCreateStrategyConfigWhenIdIsMissing() throws Exception {

        mockMvc.perform(post("/api/config/strategy/" + STRAT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD))
                .accept(MediaType.APPLICATION_JSON)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someStrategyConfigWithMissingId())))
                .andExpect(status().isBadRequest());
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private static List<StrategyConfig> allTheStrategiesConfig() {

        final Map<String, String> configItems = new HashMap<>();

        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);

        final StrategyConfig strategyConfig1 = new StrategyConfig(STRAT_1_ID, STRAT_1_LABEL, STRAT_1_DESCRIPTION, STRAT_1_CLASSNAME, configItems);
        final StrategyConfig strategyConfig2 = new StrategyConfig(STRAT_2_ID, STRAT_2_LABEL, STRAT_2_DESCRIPTION, STRAT_2_CLASSNAME, configItems);

        final List<StrategyConfig> allStrategies = new ArrayList<>();
        allStrategies.add(strategyConfig1);
        allStrategies.add(strategyConfig2);
        return allStrategies;
    }

    private static StrategyConfig someStrategyConfig() {

        final Map<String, String> configItems = new HashMap<>();
        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return new StrategyConfig(STRAT_1_ID, STRAT_1_LABEL, STRAT_1_DESCRIPTION, STRAT_1_CLASSNAME, configItems);
    }

    private static StrategyConfig someStrategyConfigWithMissingId() {

        final Map<String, String> configItems = new HashMap<>();
        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return new StrategyConfig(null, STRAT_1_LABEL, STRAT_1_DESCRIPTION, STRAT_1_CLASSNAME, configItems);
    }

    private static StrategyConfig unrecognizedStrategyConfig() {

        final Map<String, String> configItems = new HashMap<>();
        configItems.put(BUY_PRICE_CONFIG_ITEM_KEY, BUY_PRICE_CONFIG_ITEM_VALUE);
        configItems.put(AMOUNT_TO_BUY_CONFIG_ITEM_KEY, AMOUNT_TO_BUY_CONFIG_ITEM_VALUE);
        return new StrategyConfig("unknown-id", STRAT_1_LABEL, STRAT_1_DESCRIPTION, STRAT_1_CLASSNAME, configItems);
    }

    private static StrategyConfig emptyStrategyConfig() {
        return new StrategyConfig();
    }
}
