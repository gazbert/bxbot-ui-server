package com.gazbert.bxbot.ui.server.rest.api.v1.runtime;

import com.gazbert.bxbot.ui.server.domain.bot.BotStatus;
import com.gazbert.bxbot.ui.server.services.runtime.BotProcessService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the Bot process controller behaviour.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestBotProcessController extends AbstractRuntimeControllerTest {

    private static final String STATUS_RESOURCE_PATH = "/process/status";

    private static final String UNKNOWN_BOT_ID = "unknown-bot-id";
    private static final String BOT_ID = "gdax-bot-1";
    private static final String BOT_DISPLAY_NAME = "GDAX";
    private static final String BOT_STATUS = "running";

    private BotStatus someBotStatus;

    @MockBean
    BotProcessService botProcessService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(springSecurityFilterChain).build();
        someBotStatus = new BotStatus(BOT_ID, BOT_DISPLAY_NAME, BOT_STATUS);
    }

    @Test
    public void whenGetBotStatusCalledWhenUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(botProcessService.getBotStatus(BOT_ID)).willReturn(someBotStatus);

        mockMvc.perform(get(RUNTIME_ENDPOINT_BASE_URI + BOT_ID + STATUS_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.id").value(BOT_ID))
                .andExpect(jsonPath("$.data.displayName").value(BOT_DISPLAY_NAME))
                .andExpect(jsonPath("$.data.status").value(BOT_STATUS));

        verify(botProcessService, times(1)).getBotStatus(BOT_ID);
    }

    @Test
    public void whenGetBotStatusCalledWithUnknownBotIdThenExpectNotFoundResponse() throws Exception {

        given(botProcessService.getBotStatus(UNKNOWN_BOT_ID)).willReturn(null);

        mockMvc.perform(get(RUNTIME_ENDPOINT_BASE_URI + UNKNOWN_BOT_ID + STATUS_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isNotFound());

        verify(botProcessService, times(1)).getBotStatus(UNKNOWN_BOT_ID);
    }

    @Test
    public void whenGetBotStatusCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get(RUNTIME_ENDPOINT_BASE_URI + BOT_ID + STATUS_RESOURCE_PATH))
                .andExpect(status().isUnauthorized());
    }
}

