package de.aikiit.mailclena;

import org.junit.Test;

public class MailclenaTest {

    @Test(expected = IllegalArgumentException.class)
    public void callMainWithoutParametersYieldsException() {
        MailClena.main();
    }
}
