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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

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
        assertThat(parser.extractConfiguration((String[]) null)).isEmpty();
        assertThat(parser.extractConfiguration()).isEmpty();
    }

    @Test
    void callWithoutParameterValuesButAllKeys() {
        assertThrows(IllegalArgumentException.class, () -> parser.extractConfiguration("-h", "-u", "-p"));
    }

    @Test
    void callMainExtractParametersSuccessfully() {
        final Optional<MailConfiguration> mailConfiguration = parser.extractConfiguration("-h=boo.foo.bar", "-u=foo", "-p=bar");
        assertThat(mailConfiguration).isPresent();

        final MailConfiguration configuration = mailConfiguration.get();
        assertThat(configuration.getPassword()).isEqualTo("bar");
        assertThat(configuration.getUsername()).isEqualTo("foo");
        assertThat(configuration.getHost()).isEqualTo("boo.foo.bar");
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
        assertThat(configuration.get().getCommand()).isEqualTo(unknown);
    }

}
