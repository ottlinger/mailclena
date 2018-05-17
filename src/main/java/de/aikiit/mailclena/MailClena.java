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

import de.aikiit.mailclena.mail.MailClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MailClena {

    public static void main(String... args) {
        Optional<MailConfiguration> mailConfiguration = new MailClenaParameterParser().extractConfiguration(args);

        if (mailConfiguration.isPresent()) {
            log.info("MailClena is launching with the given configuration ....");
            final MailConfiguration configuration = mailConfiguration.get();
            final MailClient client = new MailClient(configuration);

            client.execute(configuration.getCommand());
        }
        log.info("MailClena is shutting down .... bye bye :-)");
    }

}
