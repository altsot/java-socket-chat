import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ChatHandler implements Runnable {
    private BufferedReader reader;
    private PrintWriter writer;
    private Socket socket;
    private List<ChatHandler> clients;

    public ChatHandler(Socket socket, List<ChatHandler> clients) throws IOException {
        this.socket = socket;
        this.clients = clients;
        this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.writer = new PrintWriter(socket.getOutputStream(), true);
    }

    @Override
    public void run() {
        System.out.println("New client: " + socket.getRemoteSocketAddress());
        writer.println("Hello friend! If you want to leave, write /quit");

        try {
            String message;
            while ((message = reader.readLine()) != null) {
                if ("/quit".equalsIgnoreCase(message.trim())) {
                    break;
                }
                broadcast(message);
            }
        } catch (IOException e) {
            System.err.println("Problem with client " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
        } finally {
            cleanup();
        }
    }

    private void broadcast(String message) {
        for (ChatHandler client : clients) {
            if (client != this) {
                client.sendMessage(message);
            }
        }
    }

    private void cleanup() {
        clients.remove(this);
        closeQuietly(reader);
        closeQuietly(writer);
        closeQuietly(socket);
    }

    private void closeQuietly(AutoCloseable e) {
        if (e != null) {
            try {
                e.close();
            } catch (Exception ignored) {}
        }
    }

    public void sendMessage(String message) {
        writer.println(message);
    }
}

