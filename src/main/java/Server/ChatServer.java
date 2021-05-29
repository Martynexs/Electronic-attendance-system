package Server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class ChatServer extends Application {


    private TextArea messages = new TextArea();

    private Server connection = createServer();

    public static void main(String[] args)
    {
        launch(args);
    }

    private Parent createContent()
    {
        messages.setPrefHeight(550);
        TextField input = new TextField();

        input.setOnAction(event -> {
            String msg = "Server: ";
            msg += input.getText();
            input.clear();
            messages.appendText(msg + "\n");

            try {
                connection.send(msg);
            } catch (Exception e) {
                e.printStackTrace();
                messages.appendText("Could not send\n");
            }
        });
        VBox root = new VBox(20, messages, input);
        root.setPrefSize(600, 600);
        return root;
    }

    @Override
    public void init() throws Exception {
        connection.startConnection();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(createContent()));
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        connection.closeConnection();
    }

    private Server createServer()
    {
        return new Server(55555, data -> {
            Platform.runLater(() ->{
                messages.appendText(data.toString() + "\n");
                try {
                    connection.send(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        });
    }


}
