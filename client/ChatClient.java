import java.net.Socket;

public class ChatClient {

    public static void main(String[] args) {
        String serverAddress = "localhost";
        int port = 12345;

        try {
            Socket socket = new Socket(serverAddress, port);
            System.out.println("Connected to chat server!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
