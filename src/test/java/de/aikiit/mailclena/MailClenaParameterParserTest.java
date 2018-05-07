package de.aikiit.mailclena;

import org.junit.Before;
import org.junit.Test;

public class MailClenaParameterParserTest {

    private MailClenaParameterParser parser;

    @Before
    public void getAFreshOne() {
        parser = new MailClenaParameterParser();
    }

    @Test
    public void callMainWithHostnameParameter() {
        parser.extractConfiguration("-h=boo.foo.bar");
    }

    @Test
    public void callMainWithUsernameParameter() {
        parser.extractConfiguration("-u=foo");
    }

    @Test
    public void callMainWithPasswordParameter() {
        parser.extractConfiguration("-p=bar");
    }

    @Test
    public void callMainExtractParametersSuccessfully() {
        parser.extractConfiguration("-h=boo.foo.bar", "-u=foo", "-p=bar");
    }
}
