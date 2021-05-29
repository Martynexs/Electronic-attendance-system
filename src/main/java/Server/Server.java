package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.function.Consumer;

public class Server implements NetworkConnection{

    private ServerConnectionThread conThread = new ServerConnectionThread();
    private Consumer<Serializable> onReceiveCallback;
    private int port;
    private ArrayList<ClientHandler> clients = new ArrayList<ClientHandler>();

    public Server(int port, Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
        conThread.setDaemon(true);
        this.port = port;
    }

    public void startConnection() throws Exception
    {
        conThread.start();
    }

    public  void  send(Serializable data) throws Exception
    {
       clients.forEach(clientHandler -> {
           try {
               clientHandler.send(data);
           } catch (IOException e) {
               e.printStackTrace();
           }
       });
    }

    public  void closeConnection() throws Exception
    {
        clients.forEach(clientHandler -> {
            try {
                clientHandler.closeConnection();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public int getPort() {
        return port;
    }

    public String getIP() {
        return null;
    }


    private class ServerConnectionThread extends Thread{

        @Override
        public void run()
        {
            try{
                ServerSocket server = new ServerSocket(getPort());

                while(true) {
                    Socket socket = server.accept();
                    ClientHandler client = new ClientHandler(socket, onReceiveCallback, clients);
                    clients.add(client);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                onReceiveCallback.accept("Connection closed");
            }
        }
    }


}
