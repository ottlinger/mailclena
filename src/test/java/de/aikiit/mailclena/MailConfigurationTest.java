/*
  MailClena - Copyright (C) 2018, Aiki IT
  <p>
  This program is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.
  <p>
  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.
  <p>
  You should have received a copy of the GNU General Public License
  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.aikiit.mailclena;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

class MailConfigurationTest {

    @Test
    void ensurePasswordNotInToString() {
        String password = UUID.randomUUID().toString();
        assertThat(MailConfiguration.builder().password(password).username("username").host("host").command("cmd").build().toString()).doesNotContain(password);
    }

    @Test
    void regularObjectCreation() {
        final MailConfiguration configuration = new MailConfiguration("host", "username", "password", "myCommand");
        assertThat(configuration.host()).isEqualTo("host");
        assertThat(configuration.username()).isEqualTo("username");
        assertThat(configuration.password()).isEqualTo("password");
        assertThat(configuration.command()).isEqualTo("myCommand");
    }

    @Test
    void builderCreatesEquivalentRecord() {
        final MailConfiguration configuration = MailConfiguration.builder().host("h").username("u").password("p").command("c").build();
        assertThat(configuration.host()).isEqualTo("h");
        assertThat(configuration.username()).isEqualTo("u");
        assertThat(configuration.password()).isEqualTo("p");
        assertThat(configuration.command()).isEqualTo("c");
        assertThat(configuration).isEqualTo(new MailConfiguration("h", "u", "p", "c"));
        assertThat(configuration.hashCode()).isEqualTo(new MailConfiguration("h", "u", "p", "c").hashCode());
        assertThat(configuration).isNotEqualTo(new MailConfiguration("h", "u", "different", "c"));
    }

    @Test
    void toStringIncludesOnlyNonSecretConfiguration() {
        assertThat(new MailConfiguration("host", "username", "secret", "list").toString())
                .isEqualTo("MailConfiguration[host=host, username=username, command=list, isPasswordSet=true]")
                .doesNotContain("secret");
    }

    @Test
    void toStringReportsEmptyPasswordAsNotSet() {
        assertThat(new MailConfiguration("host", "username", "", "list").toString())
                .isEqualTo("MailConfiguration[host=host, username=username, command=list, isPasswordSet=false]");
    }

    @Test
    void toStringDoesNotRevealWhichPasswordIsSet() {
        MailConfiguration first = new MailConfiguration("host", "username", "first-secret", "list");
        MailConfiguration second = new MailConfiguration("host", "username", "second-secret", "list");

        assertThat(first.toString())
                .isEqualTo(second.toString())
                .contains("isPasswordSet=true")
                .doesNotContain("first-secret", "second-secret");
    }

    @Test
    void constructorRejectsNullValues() {
        assertThatNullPointerException().isThrownBy(() -> new MailConfiguration(null, "u", "p", "c"))
                .withMessage("host");
        assertThatNullPointerException().isThrownBy(() -> new MailConfiguration("h", null, "p", "c"))
                .withMessage("username");
        assertThatNullPointerException().isThrownBy(() -> new MailConfiguration("h", "u", null, "c"))
                .withMessage("password");
        assertThatNullPointerException().isThrownBy(() -> new MailConfiguration("h", "u", "p", null))
                .withMessage("command");
    }

    @Test
    void builderRejectsMissingValues() {
        assertThatNullPointerException()
                .isThrownBy(() -> MailConfiguration.builder().username("u").password("p").command("c").build())
                .withMessage("host");
        assertThatNullPointerException()
                .isThrownBy(() -> MailConfiguration.builder().host("h").password("p").command("c").build())
                .withMessage("username");
        assertThatNullPointerException()
                .isThrownBy(() -> MailConfiguration.builder().host("h").username("u").command("c").build())
                .withMessage("password");
        assertThatNullPointerException()
                .isThrownBy(() -> MailConfiguration.builder().host("h").username("u").password("p").build())
                .withMessage("command");
    }
}
