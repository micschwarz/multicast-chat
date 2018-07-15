package de.ms.chat.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

class Window extends JFrame
{
    /**
     * Message History
     */
    private DefaultListModel<String> historyArrayList = new DefaultListModel<>();
    /**
     * Last used index of list
     */
    private int lastIndex = 0;
    /**
     * Local Multicast-Server
     */
    private MultiCastServer server;
    /**
     * List Element for history
     */
    private JList<String> historyList;
    /**
     * Current username
     */
    private String currentName;

    Window()
    {
        super("ChatClient");
        Container panel = getContentPane();

        panel.setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new BorderLayout());

        JTextField text = new JTextField();
        inputPanel.add(text, BorderLayout.CENTER);

        JTextField name = new JTextField(10);
        String nameStr = "user" + Integer.toString((int) Math.floor(Math.random() * 150));
        name.setText(nameStr);
        currentName = nameStr;
        inputPanel.add(name, BorderLayout.WEST);

        JButton button = new JButton("Send");
        inputPanel.add(button, BorderLayout.EAST);

        historyList = new JList<>(historyArrayList);
        JScrollPane historyPanel = new JScrollPane(historyList);

        panel.add(historyPanel, BorderLayout.CENTER);
        panel.add(inputPanel, BorderLayout.NORTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 200);

        button.addActionListener(e -> send(text, name));
        text.addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == 10) {
                    send(text, name);
                }
            }
        });

        name.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                String newName;
                if (!currentName.equals((newName = name.getText()))) {
                    server.send(currentName + " heisst nun " + newName);
                    currentName = name.getText();
                }
            }
        });

        addMessage("Willkommen im Chatroom!");

        connect();
        setVisible(true);
        text.requestFocus();
    }

    /**
     * Start local multicast-server
     */
    private void connect()
    {
        (server = new MultiCastServer(this)).start();
    }

    /**
     * Send Message
     *
     * @param text message text
     * @param name username
     */
    private void send(JTextField text, JTextField name)
    {
        String message = text.getText();
        if (!message.equals("")) {
            String clientName = name.getText();
            text.setText("");
            server.send(clientName + ": " + message);
        }
    }

    /**
     * Add message to window
     *
     * @param message message text
     */
    void addMessage(String message)
    {
        historyArrayList.add(lastIndex++, message);
        historyList.ensureIndexIsVisible(historyArrayList.getSize() - 1);

    }

    /**
     * Return Username
     *
     * @return username
     */
    String getClientName()
    {
        return currentName;
    }
}
