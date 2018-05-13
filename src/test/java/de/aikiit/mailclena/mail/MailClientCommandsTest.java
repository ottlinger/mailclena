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
package de.aikiit.mailclena.mail;

import org.junit.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class MailClientCommandsTest {

    @Test
    public void parseWithUnknownValue() {
        for (String input : Arrays.asList(null, "", "nothingNewOutHere")) {
            assertThat(MailClient.MailClientCommands.parse(input)).isEmpty();
        }
    }

    @Test
    public void parseWithPossibleValuesIgnoringCasing() {
        for (MailClient.MailClientCommands cmd : MailClient.MailClientCommands.values()) {
            assertThat(MailClient.MailClientCommands.parse(cmd.name().toLowerCase())).isEqualTo(Optional.of(cmd));
            assertThat(MailClient.MailClientCommands.parse(cmd.name().toUpperCase())).isEqualTo(Optional.of(cmd));
            assertThat(MailClient.MailClientCommands.parse("    " + cmd.name() + "   ")).isEqualTo(Optional.of(cmd));
        }
    }

}
