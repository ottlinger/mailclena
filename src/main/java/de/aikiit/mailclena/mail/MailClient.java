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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Strings;
import de.aikiit.mailclena.MailConfiguration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.tuple.Pair;

import javax.mail.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

import static de.aikiit.mailclena.mail.MailClient.MailClientCommands.LIST;
import static de.aikiit.mailclena.mail.MailClient.MailClientCommands.parse;

/**
  Encapsulates technical access to mail inbox based on the given application/mail configuration.
 */
@AllArgsConstructor
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MailClient {

    private static final String INBOX = "INBOX";
    private static final String POP3S = "pop3s";

    private MailConfiguration mailConfiguration;

    private Properties getProperties() {
        Properties properties = new Properties();
        properties.put("mail.pop3s.host", mailConfiguration.getHost());
        properties.put("mail.pop3.starttls.enable", "true");
        properties.put("mail.pop3s.port", "995");
        properties.put("mail.store.protocol", "pop3");
        return properties;
    }

    @VisibleForTesting
    Optional<Pair<Store, Folder>> openFolder(int mode) throws MessagingException {
        Session emailSession = Session.getDefaultInstance(getProperties());
        // emailSession.setDebug(true);

        Store store = emailSession.getStore(POP3S);
        store.connect(mailConfiguration.getHost(), mailConfiguration.getUsername(), mailConfiguration.getPassword());

        Folder emailFolder = store.getFolder(INBOX);
        emailFolder.open(mode);
        return Optional.of(Pair.of(store, emailFolder));
    }

    // TODO show date of mails YYYYMMDD
    @VisibleForTesting
    void list() {
        try {
            Optional<Pair<Store, Folder>> folder = openFolder(Folder.READ_ONLY);

            if (!folder.isPresent()) {
                log.error("Unable to open folder in read-only mode to list mails, will abort.");
                return;
            }

            Pair<Store, Folder> storeAndFolder = folder.get();
            List<Message> messages = Arrays.asList(storeAndFolder.getRight().getMessages());
            final int size = messages.size();

            if (size == 0) {
                log.info("No messages found - nothing to be done here.");
            } else {

                log.info("Found {} messages.", size);

                messages.forEach(m -> {
                    try {
                        log.info(m.getSize() + " bytes / " + m.getSentDate() + " / Message: " + m.getSubject() + " / From: " + Arrays.toString(m.getFrom()));
                    } catch (MessagingException e) {
                        log.error("Error while traversing messages", e);
                    }
                });
            }

            storeAndFolder.getLeft().close();
        } catch (MessagingException e) {
            log.error(e);
        }
    }

    @VisibleForTesting
    Optional<Long> delete() {
        try {
            Optional<Pair<Store, Folder>> folder = openFolder(Folder.READ_WRITE);

            if (!folder.isPresent()) {
                log.error("Unable to open folder in write mode to remove mails, will abort.");
                return Optional.empty();
            }

            Pair<Store, Folder> storeAndFolder = folder.get();
            final Folder f = storeAndFolder.getRight();
            List<Message> messages = Arrays.asList(f.getMessages());

            final int count = messages.size();
            final AtomicLong mailSize = new AtomicLong(0L);
            if (count == 0) {
                log.info("No messages found - nothing to be done here.");
            } else {
                log.info("Starting to delete {} messages.", count);

                messages.forEach(message -> {
                    try {
                        long messageSize = message.getSize();
                        log.debug("Marking for deletion " + messageSize + " bytes, " + message.getSubject() + " From: " + Arrays.toString(message.getFrom()));
                        message.setFlag(Flags.Flag.DELETED, true);
                        mailSize.addAndGet(messageSize);
                    } catch (MessagingException e) {
                        log.error("Error while traversing messages for deletion", e);
                    }
                });

                f.close(true);
                log.info("Expunge folder to actually remove messages.");
                log.info("Finished to delete {} messages, set {} bytes free", count, mailSize.get());
            }
            storeAndFolder.getLeft().close();

            return Optional.of(mailSize.longValue());
        } catch (MessagingException e) {
            log.error(e);
        }
        return Optional.empty();
    }

    /**
     * Execute the given command or print an error message if the command is unknown.
     * @param command command to execute, should be one of {@link MailClientCommands}.
     */
    public void execute(String command) {
        Optional<MailClientCommands> cmd = parse(command);
        if (!cmd.isPresent()) {
            log.info("No explicit command given, will fallback to 'list'");
            cmd = Optional.of(LIST);
        }

        switch (cmd.get()) {
            case CLEAN:
                delete();
                list();
                break;
            case LIST:
                list();
                break;
            default:
                log.error("If you see this message, please report a bug since the CLI parser has new commands.");
                // NOOP: avoid findbugs warning
                break;
        }
    }

    @VisibleForTesting
    enum MailClientCommands {
        LIST,
        CLEAN;

        static Optional<MailClientCommands> parse(String command) {
            if (!Strings.isNullOrEmpty(command)) {
                String normalized = command.trim();
                for (MailClientCommands cmd : values()) {
                    if (normalized.equalsIgnoreCase(cmd.toString())) {
                        return Optional.of(cmd);
                    }
                }
            }

            return Optional.empty();
        }

    }

}
