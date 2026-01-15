// package client;
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

    public ChatClientGUI() {
        frame = new JFrame("Java Chat App");

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);

        JScrollPane scrollPane = new JScrollPane(chatArea);

        inputField = new JTextField();
        sendButton = new JButton("Send");

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(inputField, BorderLayout.CENTER);
        bottomPanel.add(sendButton, BorderLayout.EAST);

        frame.setLayout(new BorderLayout());
        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setSize(400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
        try {
            socket = new Socket("localhost", 12345);
            in = new BufferedReader(
                    new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(frame,
                    "Could not connect to server");
        }
        startMessageReader();

        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        
    }
    
    private void sendMessage() {
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;

        out.println(text);
        inputField.setText("");
    }
    private void startMessageReader() {
        new Thread(() -> {
            try {
                String msg;
                while ((msg = in.readLine()) != null) {
                    chatArea.append(msg + "\n");
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
