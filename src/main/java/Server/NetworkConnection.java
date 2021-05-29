package Server;

import java.io.Serializable;

public interface NetworkConnection {

    void startConnection() throws Exception;

    void  send(Serializable data) throws Exception;

    void closeConnection() throws Exception;

    int getPort();
    String getIP();

}
