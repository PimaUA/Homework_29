package server;

import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ServerTest {

    //We are asserting that SocketCreator.createServerSocket(1234);
    // returns something that is not null,
    // which in this case is a new ServerSocket.
    @Test
    public void testServerSocketGetsCreated() throws IOException {
        assertNotNull(Server.createServerSocket(1234));
    }

    //test to verify that the port in which the ServerSocket
    // listens on is the port being passed into the new ServerSocket's constructor
    @Test
    public void testServerSocketWithSpecificPortGetsCreated() throws IOException {
        final int testPort = 9001;
        ServerSocket testServerSocket =
                Server.createServerSocket(testPort);
        assertEquals(testServerSocket.getLocalPort(), testPort);
    }

    //test to assert that a Socket gets created
    @Test
    public void testClientSocketGetsCreated() throws IOException {
        ServerSocket mockServerSocket = mock(ServerSocket.class);
        when(mockServerSocket.accept()).thenReturn(new Socket());
        assertNotNull(
                Server.createClientSocket(mockServerSocket));
    }

    //tests for reading and writing messages.
    @Test
    public void testReadClientStreamLine() throws IOException {
        String inputString = "Hello\n";
        BufferedReader input = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(inputString.getBytes())));
        assertEquals("Hello", Server.ClientHandler.readFromInputStream(input));
    }

    @Test
    public void testWriteClientStreamLine() {
        String inputString = "World";
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(
                outContent, true);
        Server.ClientHandler.writeToOutputStream(printWriter, inputString);
       assertEquals("World\n", outContent.toString());
    }
}