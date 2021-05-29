package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Extramain extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/Home.fxml"));
        primaryStage.setTitle("(ne)Tamo dienynas");
        primaryStage.getIcons().add(new Image("tamoIcon.png"));
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        Controller.start();
    }


    public static void main(String[] args) {
        launch(args);
    }

}
