package server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

public class Server {
    private ServerSocket serverSocket;
    private static final Logger LOGGER = LogManager.getLogger(Server.class);
    private static final HashSet<ClientHandler> serverClients = new HashSet<>();

    public void startConnection(int port) throws IOException {
        serverSocket = createServerSocket(port);
        while (true) {
            Socket socket = createClientSocket(serverSocket);
            ClientHandler eachClient = new ClientHandler(socket);
            serverClients.add(eachClient);
            eachClient.start();
        }
    }

    public void stopServer() throws IOException {
        serverSocket.close();
    }

    public static ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port);
    }

    public static Socket createClientSocket(ServerSocket serverSocket) throws IOException {
        return serverSocket.accept();
    }

    static class ClientHandler extends Thread {
        private static BufferedReader serverReader;
        private static PrintWriter serverWriter;
        private static Socket clientSocket;
        private final int int_random = ThreadLocalRandom.current().nextInt();
        private final String clientName = "Client " + int_random;
        private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        private final LocalDateTime now = LocalDateTime.now();
        private final String timeOfConnection = dateTimeFormat.format(now);

        public ClientHandler(Socket socket) {
            clientSocket = socket;
        }

        static PrintWriter createSocketWriter(Socket clientSocket) {
            try {
                serverWriter = new PrintWriter
                        (new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverWriter;
        }

        static BufferedReader createSocketReader(Socket clientSocket) {
            try {
                serverReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return serverReader;
        }

        //method run
        public void run() {
            createSocketReader(clientSocket);
            createSocketWriter(clientSocket);

            //connection message
            for (ClientHandler each : serverClients) {
                serverWriter.write(each.clientName + " have been successfully connected to server. ");
            }

            //main logic
            String inputLine;
            while (true) {
                try {
                    if ((inputLine = readFromInputStream(serverReader)) == null) break;
                    else if ("-exit".equals(inputLine)) {
                        serverWriter.println(clientName + " have been disconnected");
                        serverClients.remove(this);
                        LOGGER.info(this.clientName + " have been removed from active connections");
                        break;
                    } else if (("-file ").equals(inputLine)) {
                        inputLine = readFromInputStream(serverReader);
                        copyFile(inputLine);
                        LOGGER.info("File saved to directory");
                    }
                    writeToOutputStream(serverWriter, inputLine);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            closeConnection();
        }

        public void copyFile(String inputLine) {
            Path p = Paths.get(inputLine);
            String newFileName = p.getFileName().toString();
            final String pathToDirectory = "C:/IdeaProjects/Homework_29/Server/src/main/resources/SavedFiles";
            final File dest = new File(pathToDirectory + "/" + newFileName);
            try (FileChannel sourceChannel = new FileInputStream(inputLine).getChannel();
                 FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
                destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        static String readFromInputStream(BufferedReader serverReader) throws IOException {
            String inputLine;
            inputLine = serverReader.readLine();
            return inputLine;
        }

        static void writeToOutputStream(PrintWriter serverWriter, String inputLine) {
            serverWriter.println(inputLine);
        }

        public void closeConnection() {
            try {
                serverReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverWriter.close();
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            LOGGER.info("Connection closed");
        }
    }

    public static void main(String[] args) throws IOException {
        Server server = new Server();
        server.startConnection(8888);
    }
}
