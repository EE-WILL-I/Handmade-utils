package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
//import javafx.scene.paint.Color;
//import javafx.scene.input.KeyCode;
import javafx.scene.image.Image;
import javafx.stage.Stage;
//import javafx.scene.Group;
//import javafx.scene.control.*;
//import java.util.logging.Level;
//import java.util.logging.Logger;

//import java.util.ResourceBundle;
//import java.util.concurrent.atomic.AtomicInteger;
//import java.beans.EventHandler;



public class Main extends Application {

    public static void main(String[] args) {

        System.out.println("Launching Application");
        launch(args);
    }

    @Override
    public void init() throws Exception {

        System.out.println("Application inits");
        super.init();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("sample.fxml"));

        Scene scene = new Scene(root);
        Controller.PS = primaryStage;
        scene.getStylesheets().add(getClass().getResource("sample.fxml").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Retranslator 10X");
        //primaryStage.getIcons().add(new Image("sample/Calc_icon.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {

        System.out.println("Application stops");
        super.stop();
    }
}