package de.aikiit.mailclena;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MailConfigurationClass {

    private static final String HOST = "myHostIsYourHost";
    private static final String USER = "username";
    private static final String CRED = "credentials";

    @Test
    public void roundtripHostname() {
        final MailConfiguration mailConfiguration = new MailConfiguration();
        mailConfiguration.setHost(HOST);
        assertThat(mailConfiguration.getHost()).isEqualTo(HOST);
    }

    @Test
    public void roundtripUsername() {
        final MailConfiguration mailConfiguration = new MailConfiguration();
        mailConfiguration.setUsername(USER);
        assertThat(mailConfiguration.getUsername()).isEqualTo(USER);
    }

    @Test
    public void roundtripPassword() {
        final MailConfiguration mailConfiguration = new MailConfiguration();
        mailConfiguration.setPassword(CRED);
        assertThat(mailConfiguration.getPassword()).isEqualTo(CRED);
    }
}
