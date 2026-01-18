import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

public class Main {

    private static boolean darkMode = false; // toggle theme
    private static Color userColor = new Color(0, 162, 255);
    private static Color otherColor = new Color(255, 182, 193);
    private static Color backgroundLight = new Color(230, 240, 255);
    private static Color backgroundDark = new Color(50, 50, 50);
    private static Color inputLight = new Color(245, 230, 250);
    private static Color inputDark = new Color(80, 80, 80);

    public static void main(String[] args) {
        JFrame frame = new JFrame("Chat App");
        frame.setSize(450, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        // ------------------- Top Bar -------------------
        JPanel topBar = new JPanel();
        topBar.setBackground(new Color(7, 94, 84));
        topBar.setLayout(new BorderLayout());
        topBar.setPreferredSize(new Dimension(450, 50));

        JLabel title = new JLabel("Chat App");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        // Profile icon
        JLabel profileIcon = new JLabel("\uD83D\uDC64");
        profileIcon.setFont(new Font("Arial", Font.PLAIN, 20));
        profileIcon.setForeground(Color.WHITE);
        profileIcon.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));

        // Dark/Light mode toggle
        JButton themeToggle = new JButton("\u2600"); // sun icon
        themeToggle.setFocusPainted(false);
        themeToggle.setBackground(new Color(7, 94, 84));
        themeToggle.setForeground(Color.WHITE);
        themeToggle.setBorder(null);

        topBar.add(title, BorderLayout.WEST);
        topBar.add(profileIcon, BorderLayout.CENTER);
        topBar.add(themeToggle, BorderLayout.EAST);

        frame.add(topBar, BorderLayout.NORTH);

        // ------------------- Chat Panel -------------------
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
        chatPanel.setBackground(backgroundLight);

        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        frame.add(scrollPane, BorderLayout.CENTER);

        // ------------------- Input Panel -------------------
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        inputPanel.setBackground(inputLight);

        JButton emojiButton = new JButton("\uD83D\uDE03"); // smile emoji
        emojiButton.setFont(new Font("Arial", Font.PLAIN, 18));
        emojiButton.setFocusPainted(false);

        JTextField messageField = new JTextField();
        messageField.setFont(new Font("Arial", Font.PLAIN, 14));
        messageField.setBackground(Color.WHITE);
        messageField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));

        JButton sendButton = new JButton("\u2708"); // paper plane
        sendButton.setBackground(userColor);
        sendButton.setForeground(Color.WHITE);
        sendButton.setFocusPainted(false);
        sendButton.setFont(new Font("Arial", Font.BOLD, 16));
        sendButton.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        inputPanel.add(emojiButton, BorderLayout.WEST);
        inputPanel.add(messageField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.setVisible(true);

        // ------------------- Emoji Panel -------------------
        JPopupMenu emojiPopup = new JPopupMenu();
        String[] emojis = {"😀","😂","😍","😎","🤔","😭","😡","👍","👎"};
        JPanel emojiPanel = new JPanel(new GridLayout(2, 5));
        for(String em : emojis){
            JButton b = new JButton(em);
            b.setFont(new Font("Arial", Font.PLAIN, 18));
            b.setFocusPainted(false);
            b.setBorder(null);
            b.setBackground(Color.WHITE);
            b.addActionListener(ev -> {
                messageField.setText(messageField.getText() + em);
                emojiPopup.setVisible(false);
            });
            emojiPanel.add(b);
        }
        emojiPopup.add(emojiPanel);

        emojiButton.addActionListener(e -> {
            emojiPopup.show(emojiButton, 0, -emojiButton.getHeight()*2);
        });

        // ------------------- Send Message Logic -------------------
        ActionListener sendAction = e -> {
            String message = messageField.getText().trim();
            if(!message.isEmpty()){
                addMessage(chatPanel, "You", message, true);
                messageField.setText("");
                JScrollBar vertical = scrollPane.getVerticalScrollBar();
                vertical.setValue(vertical.getMaximum());
            }
        };

        sendButton.addActionListener(sendAction);
        messageField.addActionListener(sendAction);

        // ------------------- Dark/Light Mode Toggle -------------------
        themeToggle.addActionListener(e -> {
            darkMode = !darkMode;
            if(darkMode){
                chatPanel.setBackground(backgroundDark);
                inputPanel.setBackground(inputDark);
                messageField.setBackground(new Color(100,100,100));
                messageField.setForeground(Color.WHITE);
            } else {
                chatPanel.setBackground(backgroundLight);
                inputPanel.setBackground(inputLight);
                messageField.setBackground(Color.WHITE);
                messageField.setForeground(Color.BLACK);
            }
            chatPanel.repaint();
            inputPanel.repaint();
            frame.repaint();
        });
    }

    // ------------------- Add Chat Bubble Method -------------------
    private static void addMessage(JPanel chatPanel, String username, String message, boolean isUser){
        JPanel bubblePanel = new JPanel();
        bubblePanel.setLayout(new BorderLayout());
        bubblePanel.setOpaque(false);

        // username label
        JLabel userLabel = new JLabel(username);
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // profile picture
        JLabel picLabel = new JLabel(isUser ? "\uD83D\uDC68" : "\uD83D\uDC69"); // male/female icons
        picLabel.setFont(new Font("Arial", Font.PLAIN, 20));

        // message bubble
        JLabel msgLabel = new JLabel("<html><p style='width:200px'>" + message + "</p></html>");
        msgLabel.setOpaque(true);
        msgLabel.setBorder(new EmptyBorder(10,10,10,10));
        msgLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        msgLabel.setBackground(isUser ? new Color(0,162,255) : new Color(255,182,193));
        msgLabel.setForeground(isUser ? Color.WHITE : Color.BLACK);
        msgLabel.setBorder(new CompoundBorder(
                new LineBorder(Color.DARK_GRAY, 1, true),
                msgLabel.getBorder()
        ));

        // assemble
        JPanel temp = new JPanel();
        temp.setLayout(new BoxLayout(temp, BoxLayout.X_AXIS));
        temp.setOpaque(false);
        if(isUser){
            temp.add(Box.createHorizontalGlue());
            JPanel inner = new JPanel();
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
            inner.setOpaque(false);
            inner.add(userLabel);
            inner.add(msgLabel);
            temp.add(inner);
            temp.add(picLabel);
        } else {
            temp.add(picLabel);
            JPanel inner = new JPanel();
            inner.setLayout(new BoxLayout(inner, BoxLayout.Y_AXIS));
            inner.setOpaque(false);
            inner.add(userLabel);
            inner.add(msgLabel);
            temp.add(inner);
            temp.add(Box.createHorizontalGlue());
        }

        chatPanel.add(temp);
        chatPanel.add(Box.createVerticalStrut(5));
        chatPanel.revalidate();
    }
}
