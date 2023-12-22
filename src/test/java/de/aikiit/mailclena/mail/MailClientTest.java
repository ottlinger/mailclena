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
import jakarta.mail.*;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailClientTest {

    private static final MailConfiguration CONFIGURATION = MailConfiguration.builder().host("h").username("u").password("p").command("c").build();
    @Spy
    private MailClient mailClient = Mockito.spy(new MailClient(CONFIGURATION));

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
            verify(m).getSubject();
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
        when(mailClient.list()).thenReturn(1234L);
        mailClient.execute("   ClEAn ");

        verify(mailClient).delete();
        // since we returned something from the list()-call above
        verify(mailClient, times(2)).list();
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

    @Disabled("TODO test openFolder")
    void openFolderInnerworkings() throws MessagingException {
        assertThat(mailClient.openFolder(Folder.READ_ONLY)).isEmpty();
        // TODO test     Optional<Pair<Store, Folder>> openFolder(int mode) throws MessagingException
    }
}
