import javax.swing.*;
import java.awt.*;

public class SetupScreenGUI {

    private JTextField sharedFolderLocField;
    private JPasswordField sharedSecretField;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SetupScreenGUI().createAndShowGUI());
    }    

    private void createAndShowGUI() {
        JFrame frame = new JFrame("P2P File Sharing App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        JMenuItem connectMenuItem = new JMenuItem("Connect");
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

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);

        JLabel sharedFolderLabel = new JLabel("Shared Folder Location");
        constraints.gridx = 0;
        constraints.gridy = 0;
        panel.add(sharedFolderLabel, constraints);

        sharedFolderLocField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 0;
        panel.add(sharedFolderLocField, constraints);

        JLabel secretKeyLabel = new JLabel("Shared Secret");
        constraints.gridx = 0;
        constraints.gridy = 1;
        panel.add(secretKeyLabel, constraints);

        sharedSecretField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(sharedSecretField, constraints);

        JButton startButton = new JButton("Start");
        startButton.addActionListener(e -> startApplication(frame));
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(startButton, constraints);

        frame.getContentPane().add(panel);
        frame.setSize(800, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private void startApplication(JFrame currentFrame) {
        char[] passwordChars = sharedSecretField.getPassword();
        String secretKey = new String(passwordChars);
        
        String folderLocation = sharedFolderLocField.getText();

        if (("cse471fall2023".equals(secretKey)) && ("/home/mint/Documents/Shared".equals(folderLocation))) {
            currentFrame.dispose();

            SwingUtilities.invokeLater(() -> {
                try {
                    new FileTransferScreenGUI();
					FileTransferScreenGUI.createAndShowGUI();
                } catch (Exception e) {
                	e.printStackTrace();
                }
            });
        } else {
            JOptionPane.showMessageDialog(null, "Incorrect path or secret key. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showAboutDialog() {
        JOptionPane.showMessageDialog(null, "P2P File Sharing App\nDeveloped By: Nilhan Süer\nStudent No:20190702121\nYeditepe University\nComputer Engineering Department");
    }
}
