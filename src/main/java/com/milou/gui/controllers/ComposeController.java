package com.milou.gui.controllers;

import com.milou.models.Email;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ComposeController {

    @FXML private TextField toField;
    @FXML private TextField subjectField;
    @FXML private TextArea bodyArea;
    @FXML private Label statusLabel;

    private User sender;
    private Email originalEmail;
    private final EmailService emailService = new EmailService();

    public void initData(User sender) {
        this.sender = sender;
        this.originalEmail = null;
    }

    public void initDataForReply(User replier, Email originalEmail) {
        this.sender = replier;
        this.originalEmail = originalEmail;

        String subject = originalEmail.getSubject();
        subjectField.setText(subject.toLowerCase().startsWith("re:") ? subject : "[Re] " + subject);

        Set<String> newRecipientEmailSet = new HashSet<>();
        newRecipientEmailSet.add(originalEmail.getSender().getEmail());
        for (User recipient : originalEmail.getRecipients()) {
            if (!recipient.getId().equals(replier.getId())) {
                newRecipientEmailSet.add(recipient.getEmail());
            }
        }
        toField.setText(String.join(", ", newRecipientEmailSet));

        String quote = "\n\n--- On " + originalEmail.getFormattedTimestamp() + ", " +
                originalEmail.getSender().getName() + " wrote: ---\n" +
                originalEmail.getBody();
        bodyArea.setText(quote);
        bodyArea.positionCaret(0);
    }

    @FXML
    void sendButtonAction(ActionEvent event) {
        String body = bodyArea.getText();
        try {
            if (originalEmail != null) {
                emailService.replyToEmail(sender, originalEmail.getMessage_code(), body);
            } else {
                String recipientsInput = toField.getText().trim();
                String subject = subjectField.getText().trim();
                List<String> recipientEmails = Arrays.asList(recipientsInput.split("\\s*,\\s*"));
                emailService.sendEmail(sender, recipientEmails, subject, body, "Successfully sent your email.");
            }
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