package de.aikiit.mailclena;

import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class MailClenaParameterParserTest {

    private MailClenaParameterParser parser;

    @Before
    public void getAFreshOne() {
        parser = new MailClenaParameterParser();
    }

    @Test
    public void callMainWithHostnameParameter() {
        assertThat(parser.extractConfiguration("-h=boo.foo.bar")).isNull();
    }

    @Test
    public void callMainWithUsernameParameter() {
        assertThat(parser.extractConfiguration("-u=foo")).isNull();
    }

    @Test
    public void callMainWithPasswordParameter() {
        assertThat(parser.extractConfiguration("-p=bar")).isNull();
    }

    @Test
    public void callWithNullParameter() {
        assertThat(parser.extractConfiguration(null)).isNull();
    }

    @Test
    public void callMainExtractParametersSuccessfully() {
        final MailConfiguration mailConfiguration = parser.extractConfiguration("-h=boo.foo.bar", "-u=foo", "-p=bar");
        assertThat(mailConfiguration).isNotNull();
        assertThat(mailConfiguration.getPassword()).isEqualTo("bar");
        assertThat(mailConfiguration.getUsername()).isEqualTo("foo");
        assertThat(mailConfiguration.getHost()).isEqualTo("boo.foo.bar");
    }

    @Test
    public void ensureSizeOfOptionsIsUnderTest() {
        assertThat(new MailClenaParameterParser().getAvailableOptions().getOptions()).isNotEmpty().hasSize(3);
    }
}
