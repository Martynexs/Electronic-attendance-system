package Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.util.function.Consumer;

public class Client implements NetworkConnection{

    private ClientConnectionThread conThread = new ClientConnectionThread();
    private Consumer<Serializable> onReceiveCallback;
    private String ip;
    private int port;

    public Client(String ip, int port, Consumer<Serializable> onReceiveCallback) {
        this.onReceiveCallback = onReceiveCallback;
        conThread.setDaemon(true);
        this.ip = ip;
        this.port = port;
    }

    public void startConnection() throws Exception
    {
        conThread.start();
    }

    public  void  send(Serializable data) throws Exception
    {
        conThread.out.writeObject(data);
    }

    public  void closeConnection() throws Exception
    {
        if(conThread.socket != null) {
            conThread.socket.close();
        }
    }

    public int getPort() {
        return port;
    }

    public String getIP() {
        return ip;
    }


    private class ClientConnectionThread extends Thread{
        private Socket socket;
        private ObjectOutputStream out;

        @Override
        public void run()
        {
            try(
                Socket socket = new Socket(getIP(), getPort());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream())){

                this.socket = socket;
                this.out = out;
                socket.setTcpNoDelay(true);

                while (true)
                {
                    Serializable data = (Serializable) in.readObject();
                    onReceiveCallback.accept(data);
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
