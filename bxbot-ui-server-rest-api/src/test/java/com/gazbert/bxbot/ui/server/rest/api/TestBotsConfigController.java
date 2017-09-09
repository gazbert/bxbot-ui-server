package com.gazbert.bxbot.ui.server.rest.api;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.services.BotConfigService;
import org.junit.Before;
import org.junit.Ignore;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * tests the Bot config controller behaviour.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestBotsConfigController extends AbstractConfigControllerTest {

    // This must match a user's login_id in the user table in src/test/resources/import.sql
    private static final String VALID_USERNAME = "admin";

    // This must match a user's password in the user table in src/test/resources/import.sql
    private static final String VALID_PASSWORD = "admin";

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

    private static final String NEW_BOT_NAME = "Gemini Bot";
    private static final String NEW_BOT_STATUS = "Stopped";
    private static final String NEW_BOT_URL = "https://hostname.new/api";
    private static final String NEW_BOT_USERNAME = "admin";
    private static final String NEW_BOT_PASSWORD = "password";


    @MockBean
    BotConfigService botConfigService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(springSecurityFilterChain).build();
    }

    @Test
    public void whenGetAllBotConfigCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get("/api/bots"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetAllBotConfigCalledWhenUserIsAuthenticatedThenExpectAllBotConfigToBeReturned() throws Exception {

        given(botConfigService.getAllBotConfig()).willReturn(allTheBotsConfig());

        mockMvc.perform(get("/api/bots/")
                .header("Authorization", "Bearer " + getJwt(VALID_USERNAME, VALID_PASSWORD)))
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

