package de.aikiit.mailclena;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MailConfigurationClass {

    private static final String HOST = "myHostIsYourHost";
    private static final String USER = "username";
    private static final String CRED = "credentials";

    @Test
    public void roundtripHostname() {
        final MailConfiguration.MailConfigurationBuilder builder = MailConfiguration.builder();
        builder.host(HOST);
        assertThat(builder.build().getHost()).isEqualTo(HOST);
    }

    @Test
    public void roundtripUsername() {
        final MailConfiguration.MailConfigurationBuilder builder = MailConfiguration.builder();
        builder.username(USER);
        assertThat(builder.build().getUsername()).isEqualTo(USER);
    }

    @Test
    public void roundtripPassword() {
        final MailConfiguration.MailConfigurationBuilder builder = MailConfiguration.builder();
        builder.password(CRED);
        assertThat(builder.build().getPassword()).isEqualTo(CRED);
    }
}
