package com.milou.gui.controllers;

import com.milou.MilouGUI;
import com.milou.models.Email;
import com.milou.models.User;
import com.milou.services.EmailService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class MainWindowController {

    @FXML private ListView<String> folderListView;
    @FXML private TableView<Email> emailTableView;
    @FXML private TableColumn<Email, String> fromColumn;
    @FXML private TableColumn<Email, String> subjectColumn;
    @FXML private TableColumn<Email, String> dateColumn;
    @FXML private TextArea emailBodyArea;
    @FXML private Button newEmailButton;
    @FXML private Button replyButton;
    @FXML private Button forwardButton;

    private User currentUser;
    private final EmailService emailService = new EmailService();

    @FXML
    public void initialize() {
        fromColumn.setCellValueFactory(new PropertyValueFactory<>("from"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subject"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("formattedTimestamp"));

        emailTableView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showEmailDetails(newValue)
        );

        folderListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> onFolderSelectionChanged(newValue)
        );
    }

    public void initData(User user) {
        this.currentUser = user;
        loadInitialFolders();
    }

    @FXML
    void newEmailButtonAction(ActionEvent event) {
        openComposeWindow("Compose New Email", currentUser, null);
    }

    @FXML
    void replyButtonAction(ActionEvent event) {
        Email selectedEmail = emailTableView.getSelectionModel().getSelectedItem();
        if (selectedEmail == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an email to reply to.");
            return;
        }
        openComposeWindow("Reply to: " + selectedEmail.getSubject(), currentUser, selectedEmail.getMessage_code());
    }

    @FXML
    void forwardButtonAction(ActionEvent event) {
        // TODO: منطق فوروارد ایمیل در اینجا پیاده‌سازی خواهد شد
        showAlert(Alert.AlertType.INFORMATION, "Not Implemented", "Forward functionality will be added soon!");
    }

    private void loadInitialFolders() {
        folderListView.getItems().addAll("Inbox", "Unread", "Sent");
        folderListView.getSelectionModel().select("Inbox");
    }

    private void onFolderSelectionChanged(String selectedFolder) {
        if (selectedFolder == null || currentUser == null) return;

        List<Email> emailsToShow;
        switch (selectedFolder) {
            case "Inbox" -> emailsToShow = emailService.allMails(currentUser.getId());
            case "Unread" -> emailsToShow = emailService.unreadMails(currentUser.getId());
            case "Sent" -> emailsToShow = emailService.sentMails(currentUser.getId());
            default -> emailsToShow = List.of();
        }
        updateEmailTable(emailsToShow);
    }

    private void updateEmailTable(List<Email> emails) {
        emailTableView.setItems(FXCollections.observableArrayList(emails));
        emailBodyArea.clear();
    }

    private void showEmailDetails(Email email) {
        if (email != null) {
            emailService.readByCode(currentUser.getId(), email.getMessage_code())
                    .ifPresent(fullEmail -> emailBodyArea.setText(fullEmail.toString()));
        } else {
            emailBodyArea.clear();
        }
    }

    private void openComposeWindow(String title, User user, String originalEmailCode) {
        try {
            FXMLLoader loader = new FXMLLoader(MilouGUI.class.getResource("/views/compose-view.fxml"));
            Stage composeStage = new Stage();
            composeStage.setTitle(title);
            composeStage.setScene(new Scene(loader.load()));

            ComposeController controller = loader.getController();

            if (originalEmailCode != null) {
                Optional<Email> originalEmailOpt = emailService.readByCode(user.getId(), originalEmailCode);
                if (originalEmailOpt.isPresent()) {
                    controller.initDataForReply(user, originalEmailOpt.get());
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Could not load the original email to reply to.");
                    return;
                }
            } else {
                controller.initData(user);
            }

            composeStage.initModality(Modality.APPLICATION_MODAL);
            composeStage.showAndWait();

            onFolderSelectionChanged(folderListView.getSelectionModel().getSelectedItem());

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "UI Error", "Could not open the compose window.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}