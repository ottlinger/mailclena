package de.aikiit.mailclena;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MailClena {

    public static void main(String... args) {
        log.info("Hello World :-)");

        if (args.length == 0) {
            log.error("You need to provide parameters - expecting at least: " + new MailConfiguration().toString());
            throw new IllegalArgumentException("Invalid parameters given");
        }
    }
}
