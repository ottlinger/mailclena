/*
 MailClena - Copyright (C) 2018, Aiki IT

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */
package de.aikiit.mailclena;

import org.apache.commons.cli.Options;
import org.apache.commons.cli.help.HelpFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.MockedStatic;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

class MailClenaParameterParserTest {

    private MailClenaParameterParser parser;

    @BeforeEach
    void getAFreshOne() {
        parser = new MailClenaParameterParser();
    }

    @Test
    void callMainWithHostnameParameter() {
        assertThat(parser.extractConfiguration("-h=boo.foo.bar")).isEmpty();
    }

    @Test
    void callMainWithUsernameParameter() {
        assertThat(parser.extractConfiguration("-u=foo")).isEmpty();
    }

    @Test
    void callMainWithPasswordParameter() {
        assertThat(parser.extractConfiguration("-p=bar")).isEmpty();
    }

    @Test
    void callMainWithTwoParametersOneMissing() {
        assertThat(parser.extractConfiguration("-h=bar", "-u=usr")).isEmpty();
    }

    @Test
    void callWithNullOrEmptyParameter() {
        assertHelpOutput(() -> assertThat(parser.extractConfiguration((String[]) null)).isEmpty());
        assertHelpOutput(() -> assertThat(parser.extractConfiguration()).isEmpty());
    }

    @Test
    void callWithoutParameterValuesButAllKeys() {
        assertHelpOutput(() -> assertThatThrownBy(() -> parser.extractConfiguration("-h", "-u", "-p"))
                .isInstanceOf(IllegalArgumentException.class));
    }

    @Test
    void helpOutputFailureRetainsCause() throws IOException {
        final HelpFormatter formatter = mock(HelpFormatter.class);
        final HelpFormatter.Builder builder = mock(HelpFormatter.Builder.class);
        final IOException failure = new IOException("Output unavailable");
        when(builder.setShowSince(false)).thenReturn(builder);
        when(builder.get()).thenReturn(formatter);
        doThrow(failure).when(formatter).printHelp(eq("MailClena"), isNull(), any(Options.class), isNull(), eq(false));

        try (MockedStatic<HelpFormatter> formatters = mockStatic(HelpFormatter.class)) {
            formatters.when(HelpFormatter::builder).thenReturn(builder);
            assertThatThrownBy(() -> parser.extractConfiguration())
                    .isInstanceOf(UncheckedIOException.class)
                    .hasMessage("Unable to print command line help")
                    .hasCause(failure);
        }
    }

    private void assertHelpOutput(Runnable invocation) {
        final PrintStream originalOut = System.out;
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (PrintStream capturedOut = new PrintStream(output, true, StandardCharsets.UTF_8)) {
            System.setOut(capturedOut);
            invocation.run();
        } finally {
            System.setOut(originalOut);
        }
        assertThat(output.toString(StandardCharsets.UTF_8))
                .contains("MailClena", "--host", "--username", "--password", "--command",
                        "Hostname", "Username", "Password", "Command to execute")
                .doesNotContain("Since");
    }

    @Test
    void callMainExtractParametersSuccessfully() {
        final Optional<MailConfiguration> mailConfiguration = parser.extractConfiguration("-h=boo.foo.bar", "-u=foo", "-p=bar");
        assertThat(mailConfiguration).isPresent();

        final MailConfiguration configuration = mailConfiguration.get();
        assertThat(configuration.password()).isEqualTo("bar");
        assertThat(configuration.username()).isEqualTo("foo");
        assertThat(configuration.host()).isEqualTo("boo.foo.bar");
    }

    @Test
    void ensureAllOptionsAreMentionedAndParsed() {
        assertThat(new MailClenaParameterParser().getAvailableOptions().getOptions()).isNotEmpty().hasSize(MailClenaParameterParser.MailClenaCommandLineOptions.values().length);
    }

    @Test
    void verifyCommandIsParsedIfPresent() {
        final String unknown = "unknown";
        final Optional<MailConfiguration> configuration = new MailClenaParameterParser().extractConfiguration("-h=host", "-u=user", "-p=password", "-c=" + unknown);
        assertThat(configuration).isPresent();
        assertThat(configuration.get().command()).isEqualTo(unknown);
    }

}
