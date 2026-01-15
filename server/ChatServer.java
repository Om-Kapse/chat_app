import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ChatServer {

    static Set<ClientHandler> clients =
            Collections.synchronizedSet(new HashSet<>());

    static Map<String, Set<ClientHandler>> groups =
            Collections.synchronizedMap(new HashMap<>());

    public static void main(String[] args) {
        int port = 12345;
        System.out.println("Server started on port " + port);

        try (ServerSocket serverSocket = new ServerSocket(port)) {

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler handler =
                        new ClientHandler(clientSocket, clients, groups);
                clients.add(handler);
                handler.start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
