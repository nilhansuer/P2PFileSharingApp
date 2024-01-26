import java.io.DataInputStream;
import java.io.ObjectOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.List;

public class FileSender implements Runnable {
    private Socket socket;
    private static final int PORT = 8099;
    
    private static List<String> connectedClients = new ArrayList<>();

    public FileSender(Socket socket) {
        this.socket = socket;
    }

    public static void main(String[] args) {
        ExecutorService threadService = Executors.newCachedThreadPool();
        ServerSocket welcomeSocket = null;
        try {
            welcomeSocket = new ServerSocket(PORT);
            int serverPort = welcomeSocket.getLocalPort();
            System.out.println("Server is running on port: " + serverPort);

        } catch (Exception e) {
            e.printStackTrace();
        }   

        while (true) {
            try {
            	Socket socket = welcomeSocket.accept();
                
                DataInputStream dIS = new DataInputStream(socket.getInputStream());
                
                String clientIpAddr = dIS.readUTF();
                connectedClients.add(clientIpAddr);
                System.out.println(clientIpAddr + " has connected...");

                threadService.execute(new FileSender(socket));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    
    
    public void run() {
        try {
            DataInputStream dIS = new DataInputStream(socket.getInputStream());
            DataOutputStream dOS = new DataOutputStream(socket.getOutputStream());
            
            // client - server hello protocol to identify the presence of the application            
            dOS.writeUTF("HELLO_FROM_SERVER");

            File fileDirectory = new File(getClass().getClassLoader().getResource("sharedFiles").getPath());

            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.writeObject(connectedClients);
            
            if (fileDirectory.exists() && fileDirectory.isDirectory()) {
                File[] files = fileDirectory.listFiles();

                if (files != null) {
                    dOS.writeInt(files.length);

                    for (File file : files) {
                        dOS.writeUTF(file.getName());
                    }
                    
                    String selectedFileName = dIS.readUTF();
                    File selectedFile = new File(fileDirectory, selectedFileName);

                    if (selectedFile.exists() && selectedFile.isFile()) {
                        FileInputStream fis = new FileInputStream(selectedFile);
                        int length = (int) selectedFile.length();
                        int chunkCount = (int) Math.ceil((double) length / 512000.0);

                        dOS.writeInt(length);
                        System.out.println(socket.getInetAddress().getHostAddress() + " sent file size: " + length);

                        // Send file chunks
                        for (int loop = 0; loop < chunkCount; ++loop) {
                            dOS.writeInt(loop);
                            byte[] toSend = new byte[512000];
                            int read = fis.read(toSend);
                            dOS.writeInt(read);
                            dOS.write(toSend, 0, read);
                            dOS.flush();
                        }
                        
                        // Save file on the desktop
                        File desktopPath = new File(System.getProperty("user.home"), "Desktop");
                        File outputFile = new File(desktopPath, selectedFileName);
                        FileOutputStream fos = new FileOutputStream(outputFile);

                        fis.close();
                        dOS.close();
                        socket.close();
                    } else {
                        System.out.println("File not found: " + selectedFileName);
                        dOS.writeInt(0);
                    }
                } else {
                    System.out.println("No files found in the directory.");
                    dOS.writeInt(0); 
                }
            } else {
                System.out.println("Directory not found or is not a directory.");
                dOS.writeInt(0); 
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }   
}