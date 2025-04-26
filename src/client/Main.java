package client;

import client.login.controller.LoginController;
import javafx.application.Application;
import javafx.stage.Stage;
import org.omg.CORBA.ORB;

public class Main extends Application {

    private static ORB orb;

    @Override
    public void start(Stage primaryStage) throws Exception {
        new LoginController(orb);
    }

    public static void main(String[] args) {
        orb = ORB.init(args, null);
        launch(args);
    }
}
