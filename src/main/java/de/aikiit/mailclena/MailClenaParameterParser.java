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

/**
 * Extract application configuration from given main()-arguments.
 */
@Log4j2
@NoArgsConstructor
public final class MailClenaParameterParser {

    /**
     * Extracts any given configuration parameters into a {@link MailConfiguration}.
     *
     * @param args command-line arguments.
     * @return complete mail configuration.
     * @throws IllegalArgumentException if any parameter cannot be parsed properly.
     */
    Optional<MailConfiguration> extractConfiguration(String... args) throws IllegalArgumentException {
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

                    if (!Strings.isNullOrEmpty(cmd.getOptionValue(MailClenaCommandLineOptions.COMMAND.getOpt()))) {
                        mailConfigurationBuilder.command(cmd.getOptionValue(MailClenaCommandLineOptions.COMMAND.getOpt()));
                    } else {
                        mailConfigurationBuilder.command("list");
                    }

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

    /**
     * Convert enumeration elements into command-line options.
     *
     * @return available parameter options converted as CLI option elements.
     */
    @VisibleForTesting
    Options getAvailableOptions() {
        Options o = new Options();

        for (MailClenaCommandLineOptions option : MailClenaCommandLineOptions.values()) {
            o.addOption(new Option(option.getOpt(), option.name().toLowerCase(), true, option.getDescription()));
        }
        return o;
    }

    /**
     * Available command-line options to set MailClena's configuration parameters.
     */
    @VisibleForTesting
    enum MailClenaCommandLineOptions {
        /**
         * Specify host name option.
         */
        HOST("h", "Hostname - example: https://imap.yourisp.org"),
        /**
         * Specify username option.
         */
        USERNAME("u", "Username - example: myuser@tld.org"),
        /**
         * Specify password option.
         */
        PASSWORD("p", "Password - example: myfancypassword"),
        /**
         * Specify command for execution within the application.
         */
        COMMAND("c", "Command to execute - example: 'list' or 'clean', if no operation is given defaults to 'list'");

        private final String opt;
        private final String desc;

        MailClenaCommandLineOptions(final String option, final String description) {
            this.opt = option;
            this.desc = description;
        }

        /**
         * Returns the shortcut of the current command.
         *
         * @return option shortcut of the current command-line option.
         */
        String getOpt() {
            return opt;
        }

        /**
         * Returns a longer and verbald description of the current command.
         *
         * @return verbal description of the current command-line option.
         */
        String getDescription() {
            return desc;
        }
    }

}
