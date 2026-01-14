import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {

    public static void main(String[] args) {
        int port = 12345;

        System.out.println("Starting chat server...");

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                // later: new ClientHandler(clientSocket).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
