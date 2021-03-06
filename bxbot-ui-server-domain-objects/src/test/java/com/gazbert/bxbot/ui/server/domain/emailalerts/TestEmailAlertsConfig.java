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

package com.gazbert.bxbot.ui.server.domain.emailalerts;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests a Email Alerts Config domain object behaves as expected.
 *
 * @author gazbert
 */
public class TestEmailAlertsConfig {

    private static final String ID = "gdax-1";
    private static final boolean ENABLED = true;
    private static final SmtpConfig SMTP_CONFIG = new SmtpConfig();

    @Test
    public void testInitialisationWorksAsExpected() {

        final EmailAlertsConfig emailAlertsConfig = new EmailAlertsConfig(ID, ENABLED, SMTP_CONFIG);
        assertEquals(ID, emailAlertsConfig.getId());
        assertEquals(ENABLED, emailAlertsConfig.isEnabled());
        assertEquals(SMTP_CONFIG, emailAlertsConfig.getSmtpConfig());
    }

    @Test
    public void testSettersWorkAsExpected() {

        final EmailAlertsConfig emailAlertsConfig = new EmailAlertsConfig();
        assertEquals(false, emailAlertsConfig.isEnabled());
        assertEquals(null, emailAlertsConfig.getSmtpConfig());

        emailAlertsConfig.setId(ID);
        assertEquals(ID, emailAlertsConfig.getId());

        emailAlertsConfig.setEnabled(ENABLED);
        assertEquals(ENABLED, emailAlertsConfig.isEnabled());

        emailAlertsConfig.setSmtpConfig(SMTP_CONFIG);
        assertEquals(SMTP_CONFIG, emailAlertsConfig.getSmtpConfig());
    }
}
