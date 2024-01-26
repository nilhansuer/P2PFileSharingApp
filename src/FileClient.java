import java.io.*;
import java.net.*;
import java.util.List;
import java.util.Scanner;

public class FileClient {

    private static final int PORT = 8099;

    private static List<String> connectedClients = null;

    public void start() {
        try {
            new Thread(() -> {
                try (DatagramSocket socket = new DatagramSocket()) {
                    socket.setBroadcast(true);

                    while (true) {
                        byte[] buffer = new byte[1024];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                        socket.receive(packet);                        
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

            Socket socket = new Socket("10.2.11.165", PORT); // 192.168.1.21 //Wifi: 172.20.10.3

            DataInputStream dIS = new DataInputStream(socket.getInputStream());
            DataOutputStream dOS = new DataOutputStream(socket.getOutputStream());

            InetAddress localhost = InetAddress.getLocalHost();
            dOS.writeUTF(localhost.getHostAddress());

            String helloMessage = dIS.readUTF();
            if ("HELLO_FROM_SERVER".equals(helloMessage)) {
                System.out.println("Hello From Server! P2P application is running...");
            } else {
                System.out.println("P2P application is not running!!!");
            }

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            List<String> newConnectedClients = (List<String>) ois.readObject();

            if (connectedClients == null || !connectedClients.equals(newConnectedClients)) {
                connectedClients = newConnectedClients;

                StringBuilder connectedClientsText = new StringBuilder("");
                for (String client : connectedClients) {
                    connectedClientsText.append(client).append("\n");
                }
                FileTransferScreenGUI.updateComputersTextArea(connectedClientsText.toString());
            }

            int fileCount = dIS.readInt();

            StringBuilder filesText = new StringBuilder("");
            for (int i = 0; i < fileCount; i++) {
                String fileName = dIS.readUTF();
                filesText.append((i + 1)).append(". ").append(fileName).append("\n");
            }
            FileTransferScreenGUI.updateFilesTextArea(filesText.toString());

            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter the filename to download:");
            String selectedFileName = scanner.nextLine();

            dOS.writeUTF(selectedFileName);

            FileTransferScreenGUI.updateTransfersTextArea("Downloading: " + selectedFileName + "\n");

            int fileSize = dIS.readInt();

            byte[] allData = new byte[fileSize];

            int chunkCount = (int) Math.ceil((double) fileSize / 512000);
            System.out.println("Max chunk no: " + chunkCount);

            // Receive chunks
            for (int loop = 0; loop < chunkCount; ++loop) {
                int chunkID = dIS.readInt();
                System.out.println("Received chunk ID: " + chunkID);

                int chunkSize = dIS.readInt();
                System.out.println("Received chunk size: " + chunkSize);

                byte[] arr = new byte[chunkSize];
                dIS.readFully(arr);

                System.arraycopy(arr, 0, allData, chunkID * 512000, chunkSize);
            }

            FileOutputStream fos = new FileOutputStream(selectedFileName);
            fos.write(allData);
            fos.close();

            System.out.println("File downloaded successfully: " + selectedFileName);
            FileTransferScreenGUI.updateTransfersTextArea("File downloaded: " + selectedFileName + "\n");
            socket.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}