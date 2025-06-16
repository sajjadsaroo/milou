package com.milou.gui.controllers;

import com.milou.models.User;
import com.milou.services.EmailService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;

public class ComposeController {

    @FXML
    private TextField toField;
    @FXML
    private TextField subjectField;
    @FXML
    private TextArea bodyArea;
    @FXML
    private Label statusLabel;

    private User sender;
    private final EmailService emailService = new EmailService();

    public void initData(User sender) {
        this.sender = sender;
    }

    @FXML
    void sendButtonAction(ActionEvent event) {
        String recipientsInput = toField.getText().trim();
        if (recipientsInput.isEmpty()) {
            statusLabel.setText("Recipient field cannot be empty.");
            return;
        }

        String subject = subjectField.getText().trim();
        String body = bodyArea.getText().trim();

        List<String> recipientEmails = Arrays.asList(recipientsInput.split("\\s*,\\s*"));

        try {
            emailService.sendEmail(sender, recipientEmails, subject, body, "Successfully sent your email.");
            closeWindow(event);
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void cancelButtonAction(ActionEvent event) {
        closeWindow(event);
    }

    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
}