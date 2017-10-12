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

package com.gazbert.bxbot.ui.server.repository.remote.impl;

import com.gazbert.bxbot.ui.server.domain.bot.BotConfig;
import com.gazbert.bxbot.ui.server.domain.emailalerts.EmailAlertsConfig;
import com.gazbert.bxbot.ui.server.repository.remote.EmailAlertsConfigRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

/**
 * A REST client implementation of the remote Email Alerts config repository.
 *
 * @author gazbert
 */
@Repository("emailAlertsConfigRepository")
@Transactional
public class EmailAlertsConfigRepositoryRestClient implements EmailAlertsConfigRepository {

    private static final Logger LOG = LogManager.getLogger();
    private static final String REST_ENDPOINT_PATH = "/config/email-alerts";

    private RestTemplate restTemplate;


    public EmailAlertsConfigRepositoryRestClient(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public EmailAlertsConfig get(BotConfig botConfig) {

        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                botConfig.getUsername(), botConfig.getPassword()));

        final String endpointUrl = botConfig.getBaseUrl() + REST_ENDPOINT_PATH;
        LOG.info(() -> "Fetching EmailAlertsConfig from: " + endpointUrl);

        final EmailAlertsConfig config = restTemplate.getForObject(endpointUrl, EmailAlertsConfig.class);

        LOG.info(() -> "Response received from remote Bot: " + config);
        return config;
    }

    @Override
    public EmailAlertsConfig save(BotConfig botConfig, EmailAlertsConfig emailAlertsConfig) {

        LOG.info(() -> "About to save EmailAlertsConfig: " + emailAlertsConfig);

        restTemplate.getInterceptors().clear();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(
                botConfig.getUsername(), botConfig.getPassword()));

        final String endpointUrl = botConfig.getBaseUrl() + REST_ENDPOINT_PATH;
        LOG.info(() -> "Sending EmailAlertsConfig to: " + endpointUrl);

        final HttpEntity<EmailAlertsConfig> requestUpdate = new HttpEntity<>(emailAlertsConfig);
        final ResponseEntity<EmailAlertsConfig> savedConfig = restTemplate.exchange(
                endpointUrl, HttpMethod.PUT, requestUpdate, EmailAlertsConfig.class);

        LOG.info(() -> "Response received from remote Bot: " + savedConfig);
        return savedConfig.getBody();
    }
}