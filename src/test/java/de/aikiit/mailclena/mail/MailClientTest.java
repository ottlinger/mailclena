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
import jakarta.mail.Flags;
import jakarta.mail.Folder;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Store;
import me.tongfei.progressbar.ProgressBar;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MailClientTest {

    private static final MailConfiguration CONFIGURATION = MailConfiguration.builder().host("h").username("u").password("p").command("c").build();
    @Spy
    private final MailClient mailClient = spy(new MailClient(CONFIGURATION));

    @Mock
    private Pair<Store, Folder> storeAndFolder;
    @Mock
    private Store store;
    @Mock
    private Folder folder;
    @Mock
    private Message message;

    @BeforeEach
    void initWithConfig() {
        assertThat(CONFIGURATION).isNotNull();
        new MailClient(CONFIGURATION);
    }

    @Test
    void headlessProgressDoesNotInitializeTerminal() {
        List<Message> messages = Arrays.asList(message);
        try (MockedStatic<ProgressBar> progressBar = mockStatic(ProgressBar.class)) {
            assertThat(MailClient.withProgress(messages, "Listing", false)).isSameAs(messages);
            assertThat(MailClient.withProgress(messages, "Deleting", false)).isSameAs(messages);
            progressBar.verifyNoInteractions();
        }
    }

    @Test
    void interactiveProgressUsesProgressBar() {
        List<Message> messages = Arrays.asList(message);
        try (MockedStatic<ProgressBar> progressBar = mockStatic(ProgressBar.class)) {
            progressBar.when(() -> ProgressBar.wrap(messages, "Listing")).thenReturn(messages);

            assertThat(MailClient.withProgress(messages, "Listing", true)).isSameAs(messages);

            progressBar.verify(() -> ProgressBar.wrap(messages, "Listing"));
        }
    }

    @Test
    void listWithMockedMailInteraction() throws MessagingException {
        doReturn(Optional.of(storeAndFolder)).when(mailClient).openFolder(Folder.READ_ONLY);

        when(storeAndFolder.getLeft()).thenReturn(store);
        when(storeAndFolder.getRight()).thenReturn(folder);
        when(folder.getMessages()).thenReturn(new Message[]{message});

        mailClient.list();

        verify(folder).getMessages();
        verify(message).getFrom();
        verify(message).getSubject();
        verify(store).close();
    }

    @Test
    void listWithMockedMailInteractionAndNoMessages() throws MessagingException {
        doReturn(Optional.of(storeAndFolder)).when(mailClient).openFolder(Folder.READ_ONLY);

        when(storeAndFolder.getLeft()).thenReturn(store);
        when(storeAndFolder.getRight()).thenReturn(folder);
        when(folder.getMessages()).thenReturn(new Message[]{});

        mailClient.list();

        verify(folder).getMessages();
        verifyNoMoreInteractions(message);
        verify(store).close();
    }

    @Test
    void deleteWithMockedMailInteractionAndSizes() throws MessagingException {
        doReturn(Optional.of(storeAndFolder)).when(mailClient).openFolder(Folder.READ_WRITE);

        when(storeAndFolder.getLeft()).thenReturn(store);
        when(storeAndFolder.getRight()).thenReturn(folder);

        Message message1 = mock(Message.class);
        when(message1.getSize()).thenReturn(5);
        Message message2 = mock(Message.class);
        when(message2.getSize()).thenReturn(3);
        when(folder.getMessages()).thenReturn(new Message[]{message1, message2});

        assertThat(mailClient.delete()).isPresent().hasValue(8L);

        verify(folder).getMessages();
        verify(folder).close(true);
        for (Message m : Arrays.asList(message1, message2)) {
            verify(m).getSize();
            verify(m).getSentDate();
            verify(m).getSubject();
            verify(m).getFrom();
            verify(m).setFlag(Flags.Flag.DELETED, true);
        }
        verify(store).close();
    }

    @Test
    void deleteWithMockedMailInteractionAndNoMessages() throws MessagingException {
        doReturn(Optional.of(storeAndFolder)).when(mailClient).openFolder(Folder.READ_WRITE);

        when(storeAndFolder.getLeft()).thenReturn(store);
        when(storeAndFolder.getRight()).thenReturn(folder);
        when(folder.getMessages()).thenReturn(new Message[]{});

        mailClient.delete();

        verify(folder).getMessages();
        verify(store).close();

        verify(folder, times(0)).close(true);
        verifyNoMoreInteractions(message);
    }

    @Test
    void parseUnknownCommandAndChooseFallback() {
        mailClient.execute("notAValidOperation");

        verify(mailClient).list();
    }

    @Test
    void parseList() {
        mailClient.execute("lIsT");

        verify(mailClient).list();
    }

    @Test
    void parseDelete() {
        doReturn(Optional.empty()).when(mailClient).delete();
        mailClient.execute("   ClEAn ");

        verify(mailClient).delete();
        verify(mailClient, never()).list();
    }

    @Test
    void cleanOpensPopulatedInboxOnlyOnce() throws MessagingException {
        doReturn(Optional.of(storeAndFolder)).when(mailClient).openFolder(Folder.READ_WRITE);
        when(storeAndFolder.getLeft()).thenReturn(store);
        when(storeAndFolder.getRight()).thenReturn(folder);
        when(folder.getMessages()).thenReturn(new Message[]{message});
        when(message.getSize()).thenReturn(42);

        mailClient.execute("clean");

        verify(mailClient).openFolder(Folder.READ_WRITE);
        verify(mailClient, never()).openFolder(Folder.READ_ONLY);
        verify(mailClient, never()).list();
        verify(message).getSize();
        verify(message).getSentDate();
        verify(message).getSubject();
        verify(message).getFrom();
        verify(message).setFlag(Flags.Flag.DELETED, true);
        verify(folder).close(true);
        verify(store).close();
    }

    @Test
    void cleanOpensEmptyInboxOnlyOnce() throws MessagingException {
        doReturn(Optional.of(storeAndFolder)).when(mailClient).openFolder(Folder.READ_WRITE);
        when(storeAndFolder.getLeft()).thenReturn(store);
        when(storeAndFolder.getRight()).thenReturn(folder);
        when(folder.getMessages()).thenReturn(new Message[]{});

        mailClient.execute("clean");

        verify(mailClient).openFolder(Folder.READ_WRITE);
        verify(mailClient, never()).openFolder(Folder.READ_ONLY);
        verify(mailClient, never()).list();
        verify(folder).getMessages();
        verify(folder, never()).close(true);
        verifyNoMoreInteractions(message);
        verify(store).close();
    }

    @Test
    void verifyListingMessagesIsExceptionProof() throws MessagingException {
        doReturn(Optional.of(storeAndFolder)).when(mailClient).openFolder(Folder.READ_ONLY);

        when(storeAndFolder.getLeft()).thenReturn(store);
        when(storeAndFolder.getRight()).thenReturn(folder);
        when(folder.getMessages()).thenReturn(new Message[]{message});
        when(message.getFrom()).thenThrow(new MessagingException("verifyListingMessagesIsExceptionProof"));

        mailClient.list();

        verify(folder).getMessages();
        verify(message).getFrom();
        verify(store).close();
    }

    @Test
    void verifyDeletingMessagesIsExceptionProof() throws MessagingException {
        doReturn(Optional.of(storeAndFolder)).when(mailClient).openFolder(Folder.READ_WRITE);

        when(storeAndFolder.getLeft()).thenReturn(store);
        when(storeAndFolder.getRight()).thenReturn(folder);
        when(folder.getMessages()).thenReturn(new Message[]{message});
        when(message.getSubject()).thenThrow(new MessagingException("verifyDeletingMessagesIsExceptionProof"));

        mailClient.delete();

        verify(folder).getMessages();
        verify(store).close();
        verify(message).getSize();
        verify(message).getSubject();
    }

    @Test
    void verifyDeletingMessagesIsExceptionProofWithExceptionWhileFolderAccess() throws MessagingException {
        doReturn(Optional.of(storeAndFolder)).when(mailClient).openFolder(Folder.READ_WRITE);

        when(storeAndFolder.getRight()).thenReturn(folder);
        when(folder.getMessages()).thenThrow(new MessagingException("verifyDeletingMessagesIsExceptionProofWithExceptionWhileFolderAccess"));

        mailClient.delete();

        verify(folder).getMessages();
    }

    @Test
    void verifyDeleteWorksExceptionlessWhenFolderCannotBeOpened() throws MessagingException {
        doReturn(Optional.empty()).when(mailClient).openFolder(Folder.READ_WRITE);

        mailClient.delete();
        verifyNoMoreInteractions(store);
    }

    @Test
    void verifyListWorksExceptionlessWhenFolderCannotBeOpened() throws MessagingException {
        doReturn(Optional.empty()).when(mailClient).openFolder(Folder.READ_ONLY);

        mailClient.list();
        verifyNoMoreInteractions(store);
    }
}
