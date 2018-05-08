package de.aikiit.mailclena;

import com.google.common.base.Strings;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.cli.*;

@Log4j2
@NoArgsConstructor
public final class MailClenaParameterParser {

    public MailConfiguration extractConfiguration(String... args) throws IllegalArgumentException {
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine cmd = parser.parse(getAvailableOptions(), args);

            final MailConfiguration.MailConfigurationBuilder mailConfigurationBuilder = MailConfiguration.builder();
            if (cmd.hasOption(MailClenaCommandLineOptions.HOST.getOpt())) {

                if (!Strings.isNullOrEmpty(cmd.getOptionValue(MailClenaCommandLineOptions.HOST.getOpt())) &&
                        !Strings.isNullOrEmpty(cmd.getOptionValue(MailClenaCommandLineOptions.USERNAME.getOpt())) &&
                        !Strings.isNullOrEmpty(cmd.getOptionValue(MailClenaCommandLineOptions.PASSWORD.getOpt()))) {
                    mailConfigurationBuilder.host(cmd.getOptionValue(MailClenaCommandLineOptions.HOST.getOpt()));
                    mailConfigurationBuilder.username(cmd.getOptionValue(MailClenaCommandLineOptions.USERNAME.getOpt()));
                    mailConfigurationBuilder.password(cmd.getOptionValue(MailClenaCommandLineOptions.PASSWORD.getOpt()));

                    final MailConfiguration mailConfiguration = mailConfigurationBuilder.build();
                    log.info("Extracted configuration from given parameters : {}", mailConfiguration);
                    return mailConfiguration;
                }
            }

        } catch (ParseException | NullPointerException e) {
            log.error("Unable to parse given command line parameters", e);
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("MailClena", getAvailableOptions());
            throw new IllegalArgumentException("Exception while parsing command line arguments");
        }
        return null;
    }

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
