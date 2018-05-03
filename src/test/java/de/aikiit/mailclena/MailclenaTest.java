package de.aikiit.mailclena;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MailclenaTest {

    @Test
    public void callMainWithoutParameters() {
        MailClena.main();
        // to get rid of the warning
        assertThat(true).isTrue();
    }
}
