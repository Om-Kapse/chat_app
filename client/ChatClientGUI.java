import java.io.*;
import java.net.Socket;
import javax.swing.*;
import java.awt.*;

public class ChatClientGUI {

    Socket socket;
    BufferedReader in;
    PrintWriter out;

    JFrame frame;
    JTextArea chatArea;
    JTextField inputField;
    JButton sendButton;
    JButton createGroupBtn;
    JButton joinGroupBtn;

    String username;
    String activeGroup = null;

    public ChatClientGUI() {
        // -------- FRAME --------
        frame = new JFrame("Java Chat App");

        // -------- CHAT AREA --------
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(chatArea);

        // -------- INPUT --------
        inputField = new JTextField();
        inputField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        sendButton = new JButton("Send");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        // -------- TOP PANEL (GROUP CONTROLS) --------
        createGroupBtn = new JButton("Create Group");
        joinGroupBtn = new JButton("Join Group");

        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.add(createGroupBtn);
        topPanel.add(joinGroupBtn);

        // -------- LAYOUT --------
        frame.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setSize(450, 550);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // -------- CONNECT TO SERVER --------
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Could not connect to server");
            System.exit(0);
        }

        // -------- LOGIN (HIDDEN PROTOCOL) --------
        username = JOptionPane.showInputDialog(
                frame,
                "Enter your username:",
                "Login",
                JOptionPane.PLAIN_MESSAGE
        );

        if (username == null || username.trim().isEmpty()) {
            System.exit(0);
        }

        out.println("LOGIN:" + username);
        frame.setTitle("Java Chat App - " + username);

        // -------- LISTENERS --------
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        createGroupBtn.addActionListener(e -> createGroup());
        joinGroupBtn.addActionListener(e -> joinGroup());

        // -------- START READER THREAD --------
        startMessageReader();
    }

    // -------- SEND MESSAGE --------
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        if (activeGroup == null) {
            out.println("MSG:" + text);
        } else {
            out.println("GROUP_MSG:" + activeGroup + ":" + text);
        }

        inputField.setText("");
    }

    // -------- CREATE GROUP --------
    private void createGroup() {
        String group = JOptionPane.showInputDialog(
                frame,
                "Enter group name:"
        );

        if (group != null && !group.trim().isEmpty()) {
            activeGroup = group.trim();
            out.println("GROUP_CREATE:" + activeGroup);
            chatArea.append("You created and joined group: " + activeGroup + "\n");
        }
    }

    // -------- JOIN GROUP --------
    private void joinGroup() {
        String group = JOptionPane.showInputDialog(
                frame,
                "Enter group name to join:"
        );

        if (group != null && !group.trim().isEmpty()) {
            activeGroup = group.trim();
            out.println("GROUP_JOIN:" + activeGroup);
            chatArea.append("You joined group: " + activeGroup + "\n");
        }
    }

    // -------- READ MESSAGES --------
    private void startMessageReader() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    chatArea.append(msg + "\n");
                    chatArea.setCaretPosition(
                            chatArea.getDocument().getLength());
                }
            } catch (Exception e) {
                chatArea.append("Disconnected from server\n");
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClientGUI::new);
    }
}