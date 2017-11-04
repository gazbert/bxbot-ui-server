package com.gazbert.bxbot.ui.server.rest.api.v1.runtime;

import com.gazbert.bxbot.ui.server.domain.bot.BotStatus;
import com.gazbert.bxbot.ui.server.services.runtime.BotStatusService;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests the Bot Status controller behaviour.
 *
 * @author gazbert
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class TestBotStatusController extends AbstractRuntimeControllerTest {

    private static final String STATUS_RESOURCE_PATH = "/status";

    private static final String UNKNOWN_BOT_ID = "unknown-bot-id";

    private static final String BOT_1_ID = "gdax-bot-1";
    private static final String BOT_1_DISPLAY_NAME = "GDAX";
    private static final String BOT_1_STATUS = "running";

    private static final String BOT_2_ID = "bitstamp-bot-1";
    private static final String BOT_2_DISPLAY_NAME = "Bitstamp";
    private static final String BOT_2_STATUS = "running";

    private BotStatus bot1Status;
    private BotStatus bot2Status;

    @MockBean
    BotStatusService botProcessService;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(ctx).addFilter(springSecurityFilterChain).build();
        bot1Status = new BotStatus(BOT_1_ID, BOT_1_DISPLAY_NAME, BOT_1_STATUS);
        bot2Status = new BotStatus(BOT_2_ID, BOT_2_DISPLAY_NAME, BOT_2_STATUS);
    }

    @Test
    public void whenGetBotStatusCalledWhenUserIsAuthenticatedThenExpectSuccess() throws Exception {

        given(botProcessService.getBotStatus(BOT_1_ID)).willReturn(bot1Status);

        mockMvc.perform(get(RUNTIME_ENDPOINT_BASE_URI + BOT_1_ID + STATUS_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.id").value(BOT_1_ID))
                .andExpect(jsonPath("$.data.displayName").value(BOT_1_DISPLAY_NAME))
                .andExpect(jsonPath("$.data.status").value(BOT_1_STATUS));

        verify(botProcessService, times(1)).getBotStatus(BOT_1_ID);
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
        mockMvc.perform(get(RUNTIME_ENDPOINT_BASE_URI + BOT_1_ID + STATUS_RESOURCE_PATH))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void whenGetAllBotStatusCalledWhenUserIsAuthenticatedThenExpectSuccess() throws Exception {

        final List<BotStatus> allBotStatus = new ArrayList<>();
        allBotStatus.add(bot1Status);
        allBotStatus.add(bot2Status);

        given(botProcessService.getAllBotStatus()).willReturn(allBotStatus);

        mockMvc.perform(get(RUNTIME_ENDPOINT_BASE_URI + STATUS_RESOURCE_PATH)
                .header("Authorization", "Bearer " + getJwt(VALID_USER_NAME, VALID_USER_PASSWORD)))
                .andDo(print())
                .andExpect(status().isOk())

                .andExpect(jsonPath("$.data.[0].id").value(BOT_1_ID))
                .andExpect(jsonPath("$.data.[0].displayName").value(BOT_1_DISPLAY_NAME))
                .andExpect(jsonPath("$.data.[0].status").value(BOT_1_STATUS))

                .andExpect(jsonPath("$.data.[1].id").value(BOT_2_ID))
                .andExpect(jsonPath("$.data.[1].displayName").value(BOT_2_DISPLAY_NAME))
                .andExpect(jsonPath("$.data.[1].status").value(BOT_2_STATUS));

        verify(botProcessService, times(1)).getAllBotStatus();
    }

    @Test
    public void whenGetAllBotStatusCalledWhenUserNotAuthenticatedThenExpectUnauthorizedResponse() throws Exception {
        mockMvc.perform(get(RUNTIME_ENDPOINT_BASE_URI + STATUS_RESOURCE_PATH))
                .andExpect(status().isUnauthorized());
    }
}

