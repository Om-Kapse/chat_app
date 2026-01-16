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

    JList<String> userList;
    JList<String> groupList;

    DefaultListModel<String> userListModel;
    DefaultListModel<String> groupListModel;


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

        frame.setSize(650, 550);

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
        
        // -------- USER LIST --------
        userListModel = new DefaultListModel<>();
        userList = new JList<>(userListModel);

        JPanel userPanel = new JPanel(new BorderLayout());
        userPanel.add(new JLabel("Users"), BorderLayout.NORTH);
        userPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        // -------- GROUP LIST --------
        groupListModel = new DefaultListModel<>();
        groupList = new JList<>(groupListModel);

        JPanel groupPanel = new JPanel(new BorderLayout());
        groupPanel.add(new JLabel("Groups"), BorderLayout.NORTH);
        groupPanel.add(new JScrollPane(groupList), BorderLayout.CENTER);

        // -------- LEFT PANEL --------
        JPanel leftPanel = new JPanel(new GridLayout(2, 1));
        leftPanel.add(userPanel);
        leftPanel.add(groupPanel);

        frame.add(leftPanel, BorderLayout.WEST);

            groupList.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            activeGroup = groupList.getSelectedValue();
            if (activeGroup != null) {
                chatArea.append(
                    "Switched to group: " + activeGroup + "\n"
                );
            }
        }
    });

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

                handleSystemMessage(msg);
            }
        } catch (Exception e) {
            chatArea.append("Disconnected from server\n");
        }
    }).start();
}


    private void handleSystemMessage(String msg) {
        // User joined
        if (msg.startsWith("🟢")) {
            String user = msg.replace("🟢", "")
                            .replace("joined the chat", "")
                            .trim();
            if (!userListModel.contains(user)) {
                userListModel.addElement(user);
            }
        }

        // User left
        else if (msg.startsWith("🔴")) {
            String user = msg.replace("🔴", "")
                            .replace("left the chat", "")
                            .trim();
            userListModel.removeElement(user);
        }

        // Group created / joined
        else if (msg.startsWith("Group")) {
            String[] parts = msg.split(":");
            if (parts.length > 1) {
                String group = parts[1].trim();
                if (!groupListModel.contains(group)) {
                    groupListModel.addElement(group);
                }
            }
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ChatClientGUI::new);
    }
}