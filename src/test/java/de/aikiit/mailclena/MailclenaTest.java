package de.aikiit.mailclena;

import org.junit.Test;

public class MailclenaTest {

    @Test
    public void callMainWithHostnameParameter() {
        MailClena.main("-h=boo.foo.bar");
    }

    @Test
    public void callMainWithUsernameParameter() {
        MailClena.main("-u=foo");
    }
}
