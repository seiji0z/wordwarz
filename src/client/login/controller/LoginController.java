package client.login.controller;

import client.admin.controller.AdminDashboardController;
import client.admin.model.AdminDashboardModel;
import client.admin.view.AdminDashboardView;
import client.login.model.LoginModel;
import client.login.view.LoginView;
import WordWarZ.InvalidCredentials;
import WordWarZ.AlreadyLoggedIn;
import client.player.controller.MainMenuController;
import org.omg.CORBA.ORB;

import java.io.FileNotFoundException;

public class LoginController {
    private final LoginModel model;
    private final LoginView view;
    private ORB orb;

    public LoginController(ORB orb) throws FileNotFoundException {
        this.orb = orb;
        this.model = new LoginModel(orb);
        this.view = new LoginView();

        initialize();
    }

    private void initialize() throws FileNotFoundException {
        // Set the event when play is pressed
        this.view.setOnPlay(this::handleLogin);

        // Show the view
        this.view.start();
    }

    private void handleLogin() {
        String username = view.getUsernameField().getText();
        String password = view.getPasswordField().getText();

        try {
            String tokenResponse = model.login(username, password);
            String[] parts = tokenResponse.split(":");
            String token = parts[0];
            boolean isAdmin = Boolean.parseBoolean(parts[1]);

            // Close login view
            view.close();

            // Route based on admin status
            if (isAdmin) {  // Check if user is admin
                new AdminDashboardController(new AdminDashboardView(), new AdminDashboardModel(), orb, token);
            } else {
                new MainMenuController(new AdminDashboardView(), new AdminDashboardModel(), orb, token);
            }

        } catch (AlreadyLoggedIn e) {
            view.showError("User already logged in");
        } catch (InvalidCredentials e) {
            view.showError("Invalid username or password");
        } catch (Exception e) {
            view.showError("Login failed: " + e.getMessage());
            System.out.println(e.getMessage());
        }
    }

}