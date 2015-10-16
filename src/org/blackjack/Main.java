package org.blackjack;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class Main extends Application {

    private static final String MAIN_WINDOW_FXML = "/fxml/MainWindow.fxml";
    private static final int MAIN_WINDOW_WIDTH = 500;
    private static final int MAIN_WINDOW_HEIGHT = 250;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = createMainWindow(MAIN_WINDOW_FXML);
        primaryStage.setScene(scene);
        primaryStage.sizeToScene();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * create fxml window
     * @param fxml
     * @return
     */
    public Scene createMainWindow(String fxml) {
        FXMLLoader loader = new FXMLLoader();
        URL location = getClass().getResource(fxml);
        loader.setBuilderFactory(new JavaFXBuilderFactory());
        loader.setLocation(ClassLoader.getSystemResource(fxml));
        try {
            VBox page = loader.load(location.openStream());
            return new Scene(page, MAIN_WINDOW_WIDTH, MAIN_WINDOW_HEIGHT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
