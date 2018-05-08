package de.aikiit.mailclena;

import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class MailClenaParameterParserTest {

    private MailClenaParameterParser parser;

    @Before
    public void getAFreshOne() {
        parser = new MailClenaParameterParser();
    }

    @Test
    public void callMainWithHostnameParameter() {
        assertThat(parser.extractConfiguration("-h=boo.foo.bar")).isEmpty();
    }

    @Test
    public void callMainWithUsernameParameter() {
        assertThat(parser.extractConfiguration("-u=foo")).isEmpty();
    }

    @Test
    public void callMainWithPasswordParameter() {
        assertThat(parser.extractConfiguration("-p=bar")).isEmpty();
    }

    @Test
    public void callWithNullParameter() {
        assertThat(parser.extractConfiguration(null)).isEmpty();
    }

    @Test
    public void callMainExtractParametersSuccessfully() {
        final Optional<MailConfiguration> mailConfiguration = parser.extractConfiguration("-h=boo.foo.bar", "-u=foo", "-p=bar");
        assertThat(mailConfiguration).isPresent();

        final MailConfiguration configuration = mailConfiguration.get();
        assertThat(configuration.getPassword()).isEqualTo("bar");
        assertThat(configuration.getUsername()).isEqualTo("foo");
        assertThat(configuration.getHost()).isEqualTo("boo.foo.bar");
    }

    @Test
    public void ensureSizeOfOptionsIsUnderTest() {
        assertThat(new MailClenaParameterParser().getAvailableOptions().getOptions()).isNotEmpty().hasSize(3);
    }
}
