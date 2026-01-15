import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

import common.Protocol;
public class ClientHandler extends Thread {

    private Socket socket;
    private PrintWriter out;
    private String username;
    private Set<ClientHandler> clients;
    private Map<String, Set<ClientHandler>> groups;

    public ClientHandler(Socket socket, Set<ClientHandler> clients, Map<String, Set<ClientHandler>> groups) {
        this.socket = socket;
        this.clients = clients;
        this.groups = groups;
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

                    case Protocol.GROUP_CREATE:
                        createGroup(parts[1]);
                        break;

                    case Protocol.GROUP_JOIN:
                        joinGroup(parts[1]);
                        break;

                    case Protocol.GROUP_MSG:
                        sendGroupMessage(parts[1], parts[2]);
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
    private void createGroup(String groupName) {
        groups.putIfAbsent(groupName,
                Collections.synchronizedSet(new HashSet<>()));
        groups.get(groupName).add(this);
        out.println("Group created and joined: " + groupName);
    }
    private void joinGroup(String groupName) {
        Set<ClientHandler> group = groups.get(groupName);
        if (group == null) {
            out.println("Group does not exist");
            return;
        }
        group.add(this);
        out.println("Joined group: " + groupName);
    }
    private void sendGroupMessage(String groupName, String message) {
        Set<ClientHandler> group = groups.get(groupName);
        if (group == null) {
            out.println("Group does not exist");
            return;
        }

        for (ClientHandler client : group) {
            client.out.println("[Group:" + groupName + "] "
                    + username + ": " + message);
        }
    }
}