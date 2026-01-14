import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {

    private static Set<ClientHandler> clients =
            Collections.synchronizedSet(new HashSet<>());

    public static void main(String[] args) {
        int port = 12345;
        System.out.println("Server started on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler =
                        new ClientHandler(clientSocket, clients);
                clients.add(handler);
                handler.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
