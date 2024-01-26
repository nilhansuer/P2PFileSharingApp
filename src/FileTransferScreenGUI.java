import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class FileTransferScreenGUI {

    private static JTextArea computersTextArea;
    private static JTextArea filesTextArea;
    private static JTextArea transfersTextArea;
    
    static void createAndShowGUI() throws UnknownHostException {
        JFrame frame = new JFrame("File Transfer Application");
        frame.setSize(800, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        JMenuItem connectMenuItem = new JMenuItem("Connect");
        connectMenuItem.addActionListener(e -> startFileClient());
  
        JMenuItem disconnectMenuItem = new JMenuItem("Disconnect");
        
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(e -> System.exit(0));
        fileMenu.add(connectMenuItem);
        fileMenu.add(disconnectMenuItem);
        fileMenu.add(exitMenuItem);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutMenuItem);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        JPanel mainPanel = new JPanel(new GridLayout(2, 1));
        JPanel upperPanel = new JPanel(new GridLayout(1, 2));
        JPanel lowerPanel = new JPanel(new BorderLayout());

        computersTextArea = new JTextArea();
        filesTextArea = new JTextArea();
        transfersTextArea = new JTextArea();

        computersTextArea.setEditable(false);
        filesTextArea.setEditable(false);
        transfersTextArea.setEditable(false);

        JPanel computersPanel = createComputersPanel();
        JPanel filesPanel = createFilesPanel();
        JPanel transfersPanel = createTransfersPanel();
        JPanel infoPanel = createInfoPanel();

        frame.getContentPane().add(mainPanel);
        mainPanel.add(upperPanel);
        mainPanel.add(lowerPanel);
        upperPanel.add(computersPanel);
        upperPanel.add(filesPanel);
        lowerPanel.add(transfersPanel, BorderLayout.CENTER);
        lowerPanel.add(infoPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static JPanel createComputersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Computers in Network"));
        JScrollPane scrollPane = new JScrollPane(computersTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel createFilesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Files Found"));
        JScrollPane scrollPane = new JScrollPane(filesTextArea);

        filesTextArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) { 
                    int caretPosition = filesTextArea.getCaretPosition();
                    int lineNumber = getLineNumber(caretPosition);
                    String[] fileNames = filesTextArea.getText().split("\n");

                    if (lineNumber >= 0 && lineNumber < fileNames.length) {
                        String selectedFileName = fileNames[lineNumber];
                        initiateFileDownload(selectedFileName);
                    }
                }
            }
        });
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private static int getLineNumber(int caretPosition) {
        try {
            return filesTextArea.getLineOfOffset(caretPosition);
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    private static void initiateFileDownload(String selectedFileName) {
        updateTransfersTextArea("Downloading: " + selectedFileName + "\n");
    }

    private static JPanel createTransfersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder("File Transfers"));
        JScrollPane scrollPane = new JScrollPane(transfersTextArea);
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }

    private static JPanel createInfoPanel() throws UnknownHostException {
        JPanel panel = new JPanel(new BorderLayout());
        InetAddress localHost = InetAddress.getLocalHost();
        String hostname = localHost.getHostName();

        JLabel computerHostnameLabel = new JLabel("Computer Hostname: " + hostname);

        JPanel ipPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel computerIpLabel = new JLabel("Computer IP: " + localHost.getHostAddress());
        ipPanel.add(computerIpLabel);

        panel.add(computerHostnameLabel, BorderLayout.WEST);
        panel.add(ipPanel, BorderLayout.CENTER);

        return panel;
    }

    private static void showAboutDialog() {
        JOptionPane.showMessageDialog(null, "P2P File Sharing App\nDeveloped By: Nilhan Süer\nStudent No:20190702121\nYeditepe University\nComputer Engineering Department");
    }

    private static void startFileClient() {
        new Thread(() -> {
            try {
                FileClient fileClient = new FileClient();
                fileClient.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void updateComputersTextArea(String computersText) {
        SwingUtilities.invokeLater(() -> computersTextArea.setText(computersText));
    }

    public static void updateFilesTextArea(String filesText) {
        SwingUtilities.invokeLater(() -> filesTextArea.setText(filesText));
    }

    public static void updateTransfersTextArea(String transfersText) {
        SwingUtilities.invokeLater(() -> transfersTextArea.append(transfersText));
    }
}
