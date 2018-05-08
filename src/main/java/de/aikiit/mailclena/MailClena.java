package de.aikiit.mailclena;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.util.Optional;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MailClena {

    public static void main(String... args) {
        log.info("Hello World :-)");
        Optional<MailConfiguration> mailConfiguration = new MailClenaParameterParser().extractConfiguration(args);

        if(mailConfiguration.isPresent()) {
            log.info("Deleting mails ....");
        }
    }

}
