package com.gazbert.bxbot.ui.server.rest.api;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestBotsConfigController {

    private MockMvc mvc;

    @Autowired
    private WebApplicationContext context;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Ignore("Needs fixing")
    @Test
    public void whenGetBotsCalledWhenUserNotInRoleThenExpectUnauthorizedResponse() throws Exception {

        this.mvc.perform(get("/api/bots"))
                .andExpect(status().isUnauthorized());
    }

    @Ignore("Needs fixing")
    @Test
    @WithMockUser(roles = "USER")
    public void whenGetBotsCalledWhenUserInRoleThenExpectSuccessResponse() throws Exception {

        this.mvc.perform(get("/api/bots"))
                .andExpect(status().is2xxSuccessful());
    }
}

