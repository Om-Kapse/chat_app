import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Set;

import common.Protocol;
public class ClientHandler extends Thread {

    private Socket socket;
    private PrintWriter out;
    private String username;
    private Set<ClientHandler> clients;

    public ClientHandler(Socket socket, Set<ClientHandler> clients) {
        this.socket = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            BufferedReader in =
                    new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // First message = username
            out.println("Enter your username:");
            username = in.readLine();

            broadcast("🟢 " + username + " joined the chat");

            String message;
            while ((message = in.readLine()) != null) {

            String[] parts = message.split(":", 3);
            String type = parts[0];

            switch (type) {

                case Protocol.MSG:
                    broadcast(username + ": " + parts[1]);
                    break;

                case Protocol.PRIVATE:
                    sendPrivate(parts[1], parts[2]);
                    break;

                default:
                    out.println("Unknown command");
            }
        }

        } catch (Exception e) {
            System.out.println(username + " disconnected");
        } finally {
            clients.remove(this);
            broadcast("🔴 " + username + " left the chat");
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.out.println(message);
        }
    }
    private void sendPrivate(String targetUser, String message) {
        for (ClientHandler client : clients) {
            if (client.username.equals(targetUser)) {
                client.out.println("🔒 " + username + ": " + message);
                return;
            }
        }
        out.println("User not found");
    }

}
