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

import com.gazbert.bxbot.ui.server.domain.emailalerts.EmailAlertsConfig;
import com.gazbert.bxbot.ui.server.rest.security.model.User;
import com.gazbert.bxbot.ui.server.services.config.EmailAlertsConfigService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.gazbert.bxbot.ui.server.rest.api.v1.config.AbstractConfigController.CONFIG_ENDPOINT_BASE_URI;

/**
 * Controller for directing Email Alerts config requests.
 * <p>
 * Email Alerts config can only be fetched and updated - it cannot be deleted or created.
 * <p>
 * There is only 1 Email Alerts config per bot.
 * <p>
 * TODO - user param is null when using JWT Bearer token - what do we use? SecurityContext.getPrincipal?
 *
 * @author gazbert
 * @since 1.0
 */
@RestController
@RequestMapping(CONFIG_ENDPOINT_BASE_URI)
public class EmailAlertsConfigController extends AbstractConfigController {

    private static final Logger LOG = LogManager.getLogger();
    private static final String EMAIL_ALERTS_RESOURCE_PATH = "/email_alerts";
    private final EmailAlertsConfigService emailAlertsConfigService;

    public EmailAlertsConfigController(EmailAlertsConfigService emailAlertsConfigService) {
        this.emailAlertsConfigService = emailAlertsConfigService;
    }

    /**
     * Returns the Email Alerts Config for a Bot id.
     *
     * @param user  the authenticated user making the request.
     * @param botId the id of the Bot to fetch the Email Alerts config for.
     * @return the Email Alerts configuration.
     */
    @PreAuthorize("hasRole('USER')")
    @RequestMapping(value = "/{botId}" + EMAIL_ALERTS_RESOURCE_PATH, method = RequestMethod.GET)
    public ResponseEntity<?> getEmailAlerts(@AuthenticationPrincipal User user, @PathVariable String botId) {

        LOG.info("GET " + CONFIG_ENDPOINT_BASE_URI + botId + EMAIL_ALERTS_RESOURCE_PATH + " - getEmailAlerts() "); //- caller: " + user.getUsername());

        final EmailAlertsConfig emailAlertsConfig = emailAlertsConfigService.getEmailAlertsConfig(botId);
        return emailAlertsConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(emailAlertsConfig, HttpStatus.OK);
    }

    /**
     * Updates the Email Alerts configuration for a bot.
     *
     * @param user              the authenticated user making the request.
     * @param emailAlertsConfig the Email Alerts config to update.
     * @param botId             the id of the Bot to update the Email Alerts config for.
     * @return 200 'Ok' HTTP status code with updated Email Alerts config if update successful, some other HTTP status code otherwise.
     */
    @PreAuthorize("hasRole('ADMIN')")
    @RequestMapping(value = "/{botId}" + EMAIL_ALERTS_RESOURCE_PATH, method = RequestMethod.PUT)
    public ResponseEntity<?> updateEmailAlerts(@AuthenticationPrincipal User user, @RequestBody EmailAlertsConfig emailAlertsConfig,
                                               @PathVariable String botId) {

        LOG.info("PUT" + CONFIG_ENDPOINT_BASE_URI + botId + EMAIL_ALERTS_RESOURCE_PATH + " - updateEmailAlerts() "); //- caller: " + user.getUsername());
        LOG.info("Request: " + emailAlertsConfig);

        final EmailAlertsConfig updatedConfig = emailAlertsConfigService.updateEmailAlertsConfig(botId, emailAlertsConfig);
        return updatedConfig == null
                ? new ResponseEntity<>(HttpStatus.NOT_FOUND)
                : buildResponseEntity(updatedConfig, HttpStatus.OK);
    }
}

