package client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket clientSocket;
    private static BufferedReader clientReader;
    private static PrintWriter clientWriter;
    private static final Logger LOGGER = LogManager.getLogger(Client.class);

    public void startConnection(String host, int port) throws IOException {
        clientSocket = new Socket(host, port);
        clientWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
        clientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendMessage(String msg) throws IOException {
        writeToOutputStream(clientWriter, msg);
        LOGGER.info("MESSAGE SENT");
        while (true) {
            String serverResponse = readFromInputStream(clientReader);
            LOGGER.info(serverResponse);
            return serverResponse;
        }
    }

    public String sendFile(String sourcePath) throws IOException {
        clientWriter.println("-file ");
        writeToOutputStream(clientWriter, sourcePath);
        LOGGER.info("FILE SENT");
        while (true) {
            String serverResponse = readFromInputStream(clientReader);
            LOGGER.info(serverResponse);
            return serverResponse;
        }
    }

    static void writeToOutputStream(PrintWriter clientWriter, String msg) {
        clientWriter.println(msg);
    }

    static String readFromInputStream(BufferedReader clientReader) throws IOException {
        return clientReader.readLine();
    }

    public void closeConnection() throws IOException {
        clientReader.close();
        clientWriter.close();
        clientSocket.close();
    }

    public static void main(String[] args) throws IOException {
        Client client1 = new Client();
        client1.startConnection("127.0.0.1", 8888);
        client1.sendMessage("test");
        client1.sendMessage("hello");
        client1.sendFile("C:/Users/Sasha/Downloads/file1.txt");
        client1.sendMessage("-exit");
        client1.closeConnection();

        Client client2 = new Client();
        client2.startConnection("127.0.0.1", 8888);
        client2.sendFile("C:/Users/Sasha/Downloads/file2.txt");
        client2.sendMessage("Hi");
        client2.closeConnection();
    }
}
