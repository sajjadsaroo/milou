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
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MainWindowController {

    @FXML
    private Button newEmailButton;

    @FXML
    private ListView<String> folderListView;
    @FXML
    private TableView<Email> emailTableView;
    @FXML
    private TableColumn<Email, String> fromColumn;
    @FXML
    private TableColumn<Email, String> subjectColumn;
    @FXML
    private TableColumn<Email, String> dateColumn;
    @FXML
    private TextArea emailBodyArea;

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

    /**
     * پوشه‌های اولیه را در منو قرار داده و Inbox را به عنوان پیش‌فرض انتخاب می‌کند.
     */
    private void loadInitialFolders() {
        folderListView.getItems().addAll("Inbox", "Unread", "Sent");
        folderListView.getSelectionModel().select("Inbox");
    }

    private void onFolderSelectionChanged(String selectedFolder) {
        if (selectedFolder == null || currentUser == null) {
            return;
        }

        List<Email> emailsToShow;
        switch (selectedFolder) {
            case "Inbox":
                emailsToShow = emailService.allMails(currentUser.getId());
                break;
            case "Unread":
                emailsToShow = emailService.unreadMails(currentUser.getId());
                break;
            case "Sent":
                emailsToShow = emailService.sentMails(currentUser.getId());
                break;
            default:
                emailsToShow = List.of();
        }

        updateEmailTable(emailsToShow);
    }

    private void updateEmailTable(List<Email> emails) {
        ObservableList<Email> observableEmails = FXCollections.observableArrayList(emails);
        emailTableView.setItems(observableEmails);
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


    @FXML
    void newEmailButtonAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(MilouGUI.class.getResource("/views/compose-view.fxml"));

            Stage composeStage = new Stage();
            composeStage.setTitle("Compose New Email");
            composeStage.setScene(new Scene(loader.load()));

            ComposeController controller = loader.getController();
            controller.initData(this.currentUser);

            composeStage.initModality(Modality.APPLICATION_MODAL);
            composeStage.showAndWait();

            // TODO: بعد از بسته شدن پنجره، لیست ایمیل‌های ارسالی را رفرش کن

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}