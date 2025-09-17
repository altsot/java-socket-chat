import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class ChatClient {

    private Scanner scanner;
    private Socket socket;
    private PrintWriter writer;
    private BufferedReader reader;

    public ChatClient(String serverAddress, int serverPort) throws Exception {
    
    this.socket = new Socket(serverAddress, serverPort);
    this.writer = new PrintWriter(socket.getOutputStream(), true);
    this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void readMessage() {
        try {
            String message;
            while (!socket.isClosed() && (message = reader.readLine()) != null) {
                System.out.println(message);
            }
        } catch (Exception e) {
            System.out.println("Error receiving message: " + e.getMessage());
        }
    }

    private void sendMessage() {
        Scanner scanner = new Scanner(System.in);
        try {
            while (true) { 
                String message = scanner.nextLine();
                if ("/quit".equalsIgnoreCase(message.trim())) {
                    writer.println("/quit");
                    disconnect();
                    System.exit(0);
                }
                writer.println(message);
            }
        } catch (Exception e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }

    private void disconnect() {
        try {
            if (reader != null) {
                reader.close();
            }
        } catch (Exception ignored) {}

        try {
            if (writer != null) {
                writer.close();
            }
        } catch (Exception ignored) {}

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (Exception ignored) {}
        
        try {
            scanner.close();
        } catch (Exception ignored) {}

    }

    public static void main(String[] args) throws Exception {
        ChatClient client = new ChatClient("localhost", 12345);
        new Thread(() -> client.readMessage()).start();
        client.sendMessage();
    }

}