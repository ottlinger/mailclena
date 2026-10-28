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

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import de.aikiit.mailclena.MailConfiguration;
import jakarta.mail.AuthenticationFailedException;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.Store;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MailClientIntegrationTest {

    private static final String HOST = "localhost";
    private static final String EMAIL = "recipient@example.test";
    private static final String USERNAME = "recipient";
    private static final String PASSWORD = "secret";

    private GreenMail greenMail;
    private Session session;
    private MailClient mailClient;

    @BeforeEach
    void startMailServers() {
        greenMail = new GreenMail(new ServerSetup[]{
                new ServerSetup(0, HOST, ServerSetup.PROTOCOL_SMTP),
                new ServerSetup(0, HOST, ServerSetup.PROTOCOL_POP3S)
        });
        greenMail.start();
        greenMail.setUser(EMAIL, USERNAME, PASSWORD);

        Properties properties = new Properties();
        properties.setProperty("mail.pop3s.port", Integer.toString(greenMail.getPop3s().getPort()));
        // GreenMail's self-signed certificate does not match localhost.
        // Restrict both TLS exceptions to this test session, never JVM-wide settings.
        properties.setProperty("mail.pop3s.ssl.trust", HOST);
        properties.setProperty("mail.pop3s.ssl.checkserveridentity", "false");
        properties.setProperty("mail.pop3s.connectiontimeout", "5000");
        properties.setProperty("mail.pop3s.timeout", "5000");
        properties.setProperty("mail.pop3s.writetimeout", "5000");
        session = Session.getInstance(properties);
        mailClient = new MailClient(configuration(PASSWORD), session);
    }

    @AfterEach
    void stopMailServers() {
        if (greenMail != null) {
            greenMail.stop();
        }
    }

    @Test
    void opensInboxReadOnlyAndReadsMessageSentViaSmtp() throws Exception {
        sendMessages();

        Pair<Store, Folder> inbox = mailClient.openFolder(Folder.READ_ONLY).orElseThrow(AssertionError::new);
        try (Store store = inbox.getLeft()) {
            Folder folder = inbox.getRight();
            assertThat(store.isConnected()).isTrue();
            assertThat(folder.isOpen()).isTrue();
            assertThat(folder.getMode()).isEqualTo(Folder.READ_ONLY);
            assertThat(folder.getMessageCount()).isEqualTo(2);
            Message message = folder.getMessage(1);
            assertThat(message.getSubject()).isEqualTo("First message");
            assertThat(message.getFrom()[0].toString()).isEqualTo("sender@example.test");
            assertThat(message.getContent().toString()).contains("First body");
        }
    }

    @Test
    void opensInboxReadWrite() throws MessagingException {
        Pair<Store, Folder> inbox = mailClient.openFolder(Folder.READ_WRITE).orElseThrow(AssertionError::new);
        try (Store store = inbox.getLeft()) {
            assertThat(store.isConnected()).isTrue();
            assertThat(inbox.getRight().getMode()).isEqualTo(Folder.READ_WRITE);
        }
    }

    @Test
    void listsMessagesWithoutDeletingThem() {
        sendMessages();

        assertThat(mailClient.list()).isEqualTo(2);
        assertThat(mailClient.list()).isEqualTo(2);
        assertThat(greenMail.getReceivedMessages()).hasSize(2);
    }

    @Test
    void listsEmptyInbox() {
        assertThat(mailClient.list()).isZero();
    }

    @Test
    void deletesMessagesAndReportsFreedBytes() throws MessagingException {
        sendMessages();
        long expectedBytes = 0;
        Pair<Store, Folder> inbox = mailClient.openFolder(Folder.READ_ONLY).orElseThrow(AssertionError::new);
        try (Store store = inbox.getLeft()) {
            assertThat(store.isConnected()).isTrue();
            for (Message message : inbox.getRight().getMessages()) {
                expectedBytes += message.getSize();
            }
        }
        assertThat(expectedBytes).isPositive();

        assertThat(mailClient.delete()).hasValue(expectedBytes);

        // Reconnect to verify that POP3 DELE was committed by closing the folder/store.
        assertThat(mailClient.list()).isZero();
        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }

    @Test
    void deletingEmptyInboxReturnsZero() {
        assertThat(mailClient.delete()).hasValue(0L);
        assertThat(mailClient.list()).isZero();
    }

    @Test
    void cleanCommandRemovesDeliveredMessages() {
        sendMessages();

        mailClient.execute("clean");

        assertThat(mailClient.list()).isZero();
        assertThat(greenMail.getReceivedMessages()).isEmpty();
    }

    @Test
    void listCommandPreservesDeliveredMessages() {
        sendMessages();

        mailClient.execute("list");

        assertThat(mailClient.list()).isEqualTo(2);
    }

    @Test
    void rejectsInvalidCredentials() {
        MailClient invalidClient = new MailClient(configuration("wrong-password"), session);

        assertThatThrownBy(() -> invalidClient.openFolder(Folder.READ_ONLY))
                .isInstanceOf(AuthenticationFailedException.class);
    }

    private MailConfiguration configuration(String password) {
        return MailConfiguration.builder()
                .host(HOST).username(USERNAME).password(password).command("list").build();
    }

    private void sendMessages() {
        ServerSetup smtp = greenMail.getSmtp().getServerSetup();
        GreenMailUtil.sendTextEmail(EMAIL, "sender@example.test", "First message", "First body", smtp);
        GreenMailUtil.sendTextEmail(EMAIL, "sender@example.test", "Second message", "A longer second body", smtp);
        assertThat(greenMail.waitForIncomingEmail(5000, 2)).isTrue();
    }
}
