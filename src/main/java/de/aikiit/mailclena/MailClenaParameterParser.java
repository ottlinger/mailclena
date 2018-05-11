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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;

import java.util.Optional;

@Log4j2
@NoArgsConstructor
public final class MailClenaParameterParser {

    public Optional<MailConfiguration> extractConfiguration(String... args) throws IllegalArgumentException {
        if (args == null || args.length == 0) {
            printHelp();
        } else {

            try {
                final CommandLineParser parser = new DefaultParser();
                final CommandLine cmd = parser.parse(getAvailableOptions(), args);
                final MailConfiguration.MailConfigurationBuilder mailConfigurationBuilder = MailConfiguration.builder();

                if (!Strings.isNullOrEmpty(cmd.getOptionValue(MailClenaCommandLineOptions.HOST.getOpt())) &&
                    !Strings.isNullOrEmpty(cmd.getOptionValue(MailClenaCommandLineOptions.USERNAME.getOpt())) &&
                    !Strings.isNullOrEmpty(cmd.getOptionValue(MailClenaCommandLineOptions.PASSWORD.getOpt()))) {
                    mailConfigurationBuilder.host(cmd.getOptionValue(MailClenaCommandLineOptions.HOST.getOpt()));
                    mailConfigurationBuilder.username(cmd.getOptionValue(MailClenaCommandLineOptions.USERNAME.getOpt()));
                    mailConfigurationBuilder.password(cmd.getOptionValue(MailClenaCommandLineOptions.PASSWORD.getOpt()));

                    final MailConfiguration mailConfiguration = mailConfigurationBuilder.build();
                    log.debug("Extracted configuration from given parameters : {}", mailConfiguration);
                    return Optional.of(mailConfiguration);
                }

            } catch (ParseException | NullPointerException e) {
                log.error("Unable to parse given command line parameters", e);
                printHelp();
                throw new IllegalArgumentException("Exception while parsing command line arguments");
            }
        }
        return Optional.empty();
    }

    private void printHelp() {
        final HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("MailClena", getAvailableOptions());
    }


    @VisibleForTesting
    Options getAvailableOptions() {
        Options o = new Options();

        for (MailClenaCommandLineOptions opt : MailClenaCommandLineOptions.values()) {
            o.addOption(new Option(opt.getOpt(), opt.name().toLowerCase(), true, opt.getDescription()));
        }
        return o;
    }

    private enum MailClenaCommandLineOptions {
        HOST("h", "Hostname - example: http://imap.yourisp.org"),
        USERNAME("u", "Username - example: myuser@tld.org"),
        PASSWORD("p", "Password - example: myfancypassword");

        private final String opt;
        private final String desc;

        String getOpt() {
            return opt;
        }

        String getDescription() {
            return desc;
        }

        MailClenaCommandLineOptions(String opt, String desc) {
            this.opt = opt;
            this.desc = desc;
        }
    }

}
