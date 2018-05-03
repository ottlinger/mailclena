package de.aikiit.mailclena;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MailConfigurationClass {

    private static final String HOST = "myHostIsYourHost";

    @Test
    public void roundtripHostname() {
        final MailConfiguration mailConfiguration = new MailConfiguration();
        mailConfiguration.setHost(HOST);
        assertThat(mailConfiguration.getHost()).isEqualTo(HOST);
    }
}
