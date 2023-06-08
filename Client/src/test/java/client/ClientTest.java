package client;

import org.junit.jupiter.api.*;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    @Test
    public void testReadServerStreamLine() throws IOException {
        String inputString = "Hello\n";
        BufferedReader input = new BufferedReader(new InputStreamReader(
                new ByteArrayInputStream(inputString.getBytes())));
        assertEquals("Hello", Client.readFromInputStream(input));
    }

    @Test
    public void testWriteClientStreamLine() {
        String inputString = "World";
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintWriter printWriter = new PrintWriter(
                outContent, true);
        Client.writeToOutputStream(printWriter, inputString);
        assertEquals("World\n", outContent.toString());
    }
}