package com.milou.gui.controllers;

import com.milou.models.User;
import com.milou.services.AuthService;
import com.milou.utils.SceneManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;


public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label statusLabel;

    private final AuthService authService = new AuthService();

    @FXML
    protected void loginButtonAction(ActionEvent event) {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Email and password cannot be empty.");
            return;
        }

        User user = authService.login(email, password);

        if (user != null) {
            SceneManager.openMainWindow(event, user);
        } else {
            statusLabel.setTextFill(Color.RED);
            statusLabel.setText("Invalid email or password.");
        }
    }
}