package com.milou.gui.controllers;

import com.milou.models.User;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainWindowController {

    @FXML
    private Label welcomeLabel;

    public void initData(User user) {
        welcomeLabel.setText("Welcome, " + user.getName() + "!");
    }
}