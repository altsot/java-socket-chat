import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ChatServer {

    public static void main(String[] args) {
        int port = 12345;
        List<ChatHandler> clients = new CopyOnWriteArrayList<>();

        try (ServerSocket server = new ServerSocket(port)) {
            System.out.println("Server run on port: " + port);

            while (true) {
                Socket clientSocket = server.accept();
                System.out.println("Client connected: " + clientSocket.getRemoteSocketAddress());

                ChatHandler handler = new ChatHandler(clientSocket, clients);
                clients.add(handler);

                Thread thread = new Thread(handler);
                thread.start();
            }

        } catch (Exception e) {
            System.err.println("Server error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

