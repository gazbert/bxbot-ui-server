package com.gazbert.bxbot.ui.server.rest.api.v1.config;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.services.BotConfigService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the Bot config controller behaviour.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestBotsConfigController extends AbstractConfigControllerTest {

    private static final String BOTS_CONFIG_ENDPOINT_URI = "/api/v1/config/bots/";
    
    private static final String UNKNOWN_BOT_ID = "unknown-bot-id";

    private static final String BOT_1_ID = "bitstamp-bot-1";
    private static final String BOT_1_NAME = "Bitstamp Bot";
    private static final String BOT_1_STATUS = "Running";
    private static final String BOT_1_BASE_URL = "https://hostname.one/api";
    private static final String BOT_1_USERNAME = "admin";
    private static final String BOT_1_PASSWORD = "password";

    private static final String BOT_2_ID = "gdax-bot-1";
    private static final String BOT_2_NAME = "GDAX Bot";
    private static final String BOT_2_STATUS = "Running";
    private static final String BOT_2_BASE_URL = "https://hostname.two/api";
    private static final String BOT_2_USERNAME = "admin";
    private static final String BOT_2_PASSWORD = "password";

    private BotConfig someBotConfig;

    @MockBean
    BotConfigService botConfigService;


    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(springSecurityFilterChain).build();
        someBotConfig = new BotConfig(BOT_1_ID, BOT_1_NAME, BOT_1_STATUS, BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);
    }

    @Test
    public void whenGetAllBotConfigCalledWhenUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(botConfigService.getAllBotConfig()).willReturn(allTheBotsConfig());

        mockMvc.perform(get(BOTS_CONFIG_ENDPOINT_URI)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.[0].id").value(BOT_1_ID))
                .andExpect(jsonPath("$.data.[0].name").value(BOT_1_NAME))
                .andExpect(jsonPath("$.data.[0].status").value(BOT_1_STATUS))
                .andExpect(jsonPath("$.data.[0].baseUrl").value(BOT_1_BASE_URL))
                .andExpect(jsonPath("$.data.[0].username").value(BOT_1_USERNAME))
                .andExpect(jsonPath("$.data.[0].password").value(BOT_1_PASSWORD))

                .andExpect(jsonPath("$.data.[1].id").value(BOT_2_ID))
                .andExpect(jsonPath("$.data.[1].name").value(BOT_2_NAME))
                .andExpect(jsonPath("$.data.[1].status").value(BOT_2_STATUS))
                .andExpect(jsonPath("$.data.[1].baseUrl").value(BOT_2_BASE_URL))
                .andExpect(jsonPath("$.data.[1].username").value(BOT_2_USERNAME))
                .andExpect(jsonPath("$.data.[1].password").value(BOT_2_PASSWORD));

        verify(botConfigService, times(1)).getAllBotConfig();
    }

    @Test
    public void whenGetAllBotConfigCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get(BOTS_CONFIG_ENDPOINT_URI))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetBotConfigCalledWhenUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(botConfigService.getBotConfig(BOT_1_ID)).willReturn(someBotConfig);

        mockMvc.perform(get(BOTS_CONFIG_ENDPOINT_URI + BOT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.id").value(BOT_1_ID))
                .andExpect(jsonPath("$.data.name").value(BOT_1_NAME))
                .andExpect(jsonPath("$.data.status").value(BOT_1_STATUS))
                .andExpect(jsonPath("$.data.baseUrl").value(BOT_1_BASE_URL))
                .andExpect(jsonPath("$.data.username").value(BOT_1_USERNAME))
                .andExpect(jsonPath("$.data.password").value(BOT_1_PASSWORD));

        verify(botConfigService, times(1)).getBotConfig(BOT_1_ID);
    }

    @Test
    public void whenGetBotConfigCalledWithUnknownBotIdThenExpectNotFoundResponse() throws Exception {

        given(botConfigService.getBotConfig(UNKNOWN_BOT_ID)).willReturn(null);

        mockMvc.perform(get(BOTS_CONFIG_ENDPOINT_URI + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(botConfigService, times(1)).getBotConfig(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetBotConfigCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get(BOTS_CONFIG_ENDPOINT_URI + BOT_1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateBotConfigCalledWhenUserIsAuthenticatedThenExpectSuccess() throws Exception {


        final BotConfig updatedConfig = new BotConfig(BOT_1_ID, BOT_1_NAME, BOT_1_STATUS, BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);
        given(botConfigService.updateBotConfig(any())).willReturn(updatedConfig);

        mockMvc.perform(put(BOTS_CONFIG_ENDPOINT_URI + BOT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(updatedConfig)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.id").value(BOT_1_ID))
                .andExpect(jsonPath("$.data.name").value(BOT_1_NAME))
                .andExpect(jsonPath("$.data.status").value(BOT_1_STATUS))
                .andExpect(jsonPath("$.data.baseUrl").value(BOT_1_BASE_URL))
                .andExpect(jsonPath("$.data.username").value(BOT_1_USERNAME))
                .andExpect(jsonPath("$.data.password").value(BOT_1_PASSWORD));

        verify(botConfigService, times(1)).updateBotConfig(any());
    }

    @Test
    public void whenUpdateBotConfigCalledWithUnknownBotIdThenExpectNotFoundResponse() throws Exception {

        given(botConfigService.updateBotConfig(any())).willReturn(null);

        mockMvc.perform(put(BOTS_CONFIG_ENDPOINT_URI + BOT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someBotConfig)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(botConfigService, times(1)).updateBotConfig(any());
    }

    @Test
    public void whenUpdateBotConfigCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(put(BOTS_CONFIG_ENDPOINT_URI + BOT_1_ID)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someBotConfig)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenUpdateBotConfigCalledWhenUserIsNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(put(BOTS_CONFIG_ENDPOINT_URI + BOT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someBotConfig)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenUpdateBotConfigCalledWithBotIdMismatchThenExpectBadRequestResponse() throws Exception {

        mockMvc.perform(put(BOTS_CONFIG_ENDPOINT_URI + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someBotConfig)))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    public void whenCreateBotConfigCalledWhenUserIsAuthenticatedThenExpectSuccess() throws Exception {

        final BotConfig createdConfig = new BotConfig(null, BOT_1_NAME, BOT_1_STATUS, BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);
        final BotConfig createdConfigWithId = new BotConfig(BOT_1_ID, BOT_1_NAME, BOT_1_STATUS, BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);

        given(botConfigService.createBotConfig(any())).willReturn(createdConfigWithId);

        mockMvc.perform(post(BOTS_CONFIG_ENDPOINT_URI)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(createdConfig)))
                .andDo(print())
                .andExpect(status().isCreated())

                .andExpect(jsonPath("$.data.id").value(BOT_1_ID))
                .andExpect(jsonPath("$.data.name").value(BOT_1_NAME))
                .andExpect(jsonPath("$.data.status").value(BOT_1_STATUS))
                .andExpect(jsonPath("$.data.baseUrl").value(BOT_1_BASE_URL))
                .andExpect(jsonPath("$.data.username").value(BOT_1_USERNAME))
                .andExpect(jsonPath("$.data.password").value(BOT_1_PASSWORD));

        verify(botConfigService, times(1)).createBotConfig(any());
    }

    @Test
    public void whenCreateBotConfigCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {

        mockMvc.perform(post(BOTS_CONFIG_ENDPOINT_URI)
                .contentType(CONTENT_TYPE)
                .content(jsonify(someBotConfig)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenCreateBotConfigCalledWhenUserIsNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(post(BOTS_CONFIG_ENDPOINT_URI)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD))
                .contentType(CONTENT_TYPE)
                .content(jsonify(someBotConfig)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    @Test
    public void whenDeleteBotConfigCalledWhenUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(botConfigService.deleteBotConfig(BOT_1_ID)).willReturn(someBotConfig);

        mockMvc.perform(delete(BOTS_CONFIG_ENDPOINT_URI + BOT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNoContent());

        verify(botConfigService, times(1)).deleteBotConfig(BOT_1_ID);
    }

    @Test
    public void whenDeleteBotConfigCalledWithUnknownBotIdThenExpectNotFoundResponse() throws Exception {

        given(botConfigService.deleteBotConfig(UNKNOWN_BOT_ID)).willReturn(null);

        mockMvc.perform(delete(BOTS_CONFIG_ENDPOINT_URI + UNKNOWN_BOT_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_ADMIN_NAME, VALID_ADMIN_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(botConfigService, times(1)).deleteBotConfig(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenDeleteBotConfigCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get(BOTS_CONFIG_ENDPOINT_URI + BOT_1_ID))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenDeleteBotConfigCalledWhenUserIsNotAdminThenExpectForbiddenResponse() throws Exception {

        mockMvc.perform(delete(BOTS_CONFIG_ENDPOINT_URI + BOT_1_ID)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isForbidden());
    }

    // ------------------------------------------------------------------------------------------------
    // Private utils
    // ------------------------------------------------------------------------------------------------

    private static List<BotConfig> allTheBotsConfig() {

        final BotConfig bot1 = new BotConfig(BOT_1_ID, BOT_1_NAME, BOT_1_STATUS, BOT_1_BASE_URL, BOT_1_USERNAME, BOT_1_PASSWORD);
        final BotConfig bot2 = new BotConfig(BOT_2_ID, BOT_2_NAME, BOT_2_STATUS, BOT_2_BASE_URL, BOT_2_USERNAME, BOT_2_PASSWORD);

        final List<BotConfig> allTheBots = new ArrayList<>();
        allTheBots.add(bot1);
        allTheBots.add(bot2);
        return allTheBots;
    }
}

