package de.aikiit.mailclena;

import org.junit.Test;

public class MailclenaTest {

    @Test
    public void callMainWithHostnameParameter() {
        MailClena.main(new String[] {"-h=boo.foo.bar"});
    }
}
