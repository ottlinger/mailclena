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

import de.aikiit.mailclena.MailConfiguration;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import javax.mail.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

@AllArgsConstructor
@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MailClient {

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

    // TODO show date of mails YYYYMMDD

    public void list() {
        try {
            Session emailSession = Session.getDefaultInstance(getProperties());
            // emailSession.setDebug(true);

            Store store = emailSession.getStore(POP3S);

            store.connect(mailConfiguration.getHost(), mailConfiguration.getUsername(), mailConfiguration.getPassword());
            Folder emailFolder = store.getFolder(INBOX);
            emailFolder.open(Folder.READ_ONLY);

            List<Message> messages = Arrays.asList(emailFolder.getMessages());
            log.info("Found {} messages.", messages.size());

            messages.forEach(m -> {
                try {
                    log.info("Message: " + m.getSubject() + " From: " + Arrays.toString(m.getFrom()));
                } catch (MessagingException e) {
                    log.error(e);
                }
            });

            store.close();
        } catch (MessagingException e) {
            log.error(e);
        }
    }

    public void delete() {
        try {
            Session emailSession = Session.getDefaultInstance(getProperties());
            // emailSession.setDebug(true);

            Store store = emailSession.getStore(POP3S);

            store.connect(mailConfiguration.getHost(), mailConfiguration.getUsername(), mailConfiguration.getPassword());
            Folder emailFolder = store.getFolder(INBOX);
            emailFolder.open(Folder.READ_WRITE);

            List<Message> messages = Arrays.asList(emailFolder.getMessages());
            log.info("Starting to delete {} messages.", messages.size());

            messages.forEach(message -> {
                try {
                    log.debug("Marking for deletion " + message.getSubject() + " From: " + Arrays.toString(message.getFrom()));
                    message.setFlag(Flags.Flag.DELETED, true);
                } catch (MessagingException e) {
                    log.error(e);
                }
            });

            emailFolder.close(true);
            log.info("Expunge folder to actually remove messages.");
            store.close();
            log.info("Finished to delete {} messages.", messages.size());
        } catch (MessagingException e) {
            log.error(e);
        }
    }
}
