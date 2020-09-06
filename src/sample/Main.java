package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;



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
        primaryStage.setTitle("Retransmitter 10X");
        primaryStage.getIcons().add(new Image("sample/translator_icon.png"));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {

        System.out.println("Application stops");
        super.stop();
    }
}