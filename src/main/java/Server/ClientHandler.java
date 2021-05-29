package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

class ClientHandler implements Runnable{

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Consumer<Serializable> onReceiveCallback;
    private ArrayList<ClientHandler> clients;

    ClientHandler(Socket socket, Consumer<Serializable> onReceiveCallback, ArrayList<ClientHandler> clients) throws IOException {
        this.socket = socket;
        this.socket.setTcpNoDelay(true);
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
        this.onReceiveCallback = onReceiveCallback;
        this.clients = clients;
        new Thread(this).start();
    }

    public void send(Serializable data) throws IOException {
        out.writeObject(data);
    }

    public void closeConnection() throws IOException {
        socket.close();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Serializable data = (Serializable) in.readObject();
                onReceiveCallback.accept(data);
            }
        } catch (Exception e) {
            e.printStackTrace();
            clients.remove(this);
        }
        finally {
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
