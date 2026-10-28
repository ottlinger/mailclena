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

import lombok.Builder;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * Immutable configuration of this application.
 *
 * @param host mail server hostname.
 * @param username mailbox username.
 * @param password mailbox password, excluded from {@link #toString()}.
 * @param command command to execute.
 */
@Builder
public record MailConfiguration(String host, String username, String password, String command) {

    /**
     * Creates a configuration with all required values.
     *
     * @param host mail server hostname.
     * @param username mailbox username.
     * @param password mailbox password.
     * @param command command to execute.
     * @throws NullPointerException if any value is null.
     */
    public MailConfiguration {
        Objects.requireNonNull(host, "host");
        Objects.requireNonNull(username, "username");
        Objects.requireNonNull(password, "password");
        Objects.requireNonNull(command, "command");
    }

    @Override
    public String toString() {
        return "MailConfiguration[host=" + host + ", username=" + username + ", command=" + command + ", isPasswordSet=" + StringUtils.isNotEmpty(password) + "]";
    }
}
