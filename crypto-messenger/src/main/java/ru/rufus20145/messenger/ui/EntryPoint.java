package ru.rufus20145.messenger.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EntryPoint extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Scene.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setTitle("P2P чат со сквозным шифрованием");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();

        MainController controller = loader.getController();
        primaryStage.setOnCloseRequest(event -> controller.stopMessenger());
    }

    public static void startApp(String[] args) {
        launch(args);
    }
}