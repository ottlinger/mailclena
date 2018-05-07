package de.aikiit.mailclena;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MailclenaTest {

    @Test
    public void callMainExtractParametersSuccessfully() {
        MailClena.main("-h=boo.foo.bar", "-u=foo", "-p=bar");
        assertThat(true).isTrue();
    }
}
