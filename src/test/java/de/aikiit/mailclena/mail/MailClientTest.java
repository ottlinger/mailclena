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
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

import javax.mail.*;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MailClientTest {

    private static final MailConfiguration CONFIGURATION = MailConfiguration.builder().host("h").username("u").password("p").command("c").build();
    @Spy
    private MailClient mailClient;

    @Mock
    private Pair<Store, Folder> storeAndFolder;
    @Mock
    private Store store;
    @Mock
    private Folder folder;
    @Mock
    private Message message;

    @Test
    public void initWithConfig() {
        assertThat(CONFIGURATION).isNotNull();
        new MailClient(CONFIGURATION);
    }

    @Test
    public void listWithMockedMailInteraction() throws MessagingException {
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
    public void listWithMockedMailInteractionAndNoMessages() throws MessagingException {
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
    public void deleteWithMockedMailInteraction() throws MessagingException {
        doReturn(Optional.of(storeAndFolder)).when(mailClient).openFolder(Folder.READ_WRITE);

        when(storeAndFolder.getLeft()).thenReturn(store);
        when(storeAndFolder.getRight()).thenReturn(folder);
        when(folder.getMessages()).thenReturn(new Message[]{message});

        mailClient.delete();

        verify(folder).getMessages();
        verify(folder).close(true);
        verify(message).getFrom();
        verify(message).getSubject();
        verify(message).setFlag(Flags.Flag.DELETED, true);
        verify(store).close();
    }

    @Test
    public void deleteWithMockedMailInteractionAndNoMessages() throws MessagingException {
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
    public void parseUnknownCommandAndChooseFallback() throws MessagingException {
        doNothing().when(mailClient).list();
        mailClient.execute("notAValidOperation");

        verify(mailClient).list();
    }

    @Test
    public void parseList() {
        doNothing().when(mailClient).list();
        mailClient.execute("lIsT");

        verify(mailClient).list();
    }

    @Test
    public void parseDelete() {
        doNothing().when(mailClient).list();
        doNothing().when(mailClient).delete();
        mailClient.execute("   ClEAn ");

        verify(mailClient, times(2)).list();
        verify(mailClient).delete();
    }
}
