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

import org.junit.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class MailConfigurationTest {

    @Test
    public void ensurePasswordNotInToString() {
        String password = UUID.randomUUID().toString();
        assertThat(MailConfiguration.builder().password(password).username("username").host("host").build().toString()).doesNotContain(password);
    }

    @Test
    public void regularObjectCreation() {
        final MailConfiguration configuration = new MailConfiguration("host", "username", "password", "myCommand");
        configuration.setPassword("p");
        assertThat(configuration.getPassword()).isEqualTo("p");

        configuration.setUsername("u");
        assertThat(configuration.getUsername()).isEqualTo("u");

        configuration.setHost("h");
        assertThat(configuration.getHost()).isEqualTo("h");

        configuration.setCommand("c");
        assertThat(configuration.getCommand()).isEqualTo("c");
    }

    @Test
    public void builderForObjectCreationWithDefaultCommand() {
        final MailConfiguration configuration = MailConfiguration.builder().host("h").username("u").password("p").build();
        assertThat(configuration.getPassword()).isEqualTo("p");
        assertThat(configuration.getUsername()).isEqualTo("u");
        assertThat(configuration.getHost()).isEqualTo("h");

        assertThat(configuration.getCommand()).isEqualTo("list");
    }
}
