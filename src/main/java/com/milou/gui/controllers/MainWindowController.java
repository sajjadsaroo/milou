package com.milou.gui.controllers;

import com.milou.models.Email;
import com.milou.models.User;
import com.milou.services.EmailService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MainWindowController {

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

        folderListView.getItems().addAll("Inbox", "Unread", "Sent");
        folderListView.getSelectionModel().select("Inbox");
    }

    public void initData(User user) {
        this.currentUser = user;
        loadEmails();
    }

    private void loadEmails() {
        if (currentUser != null) {
            List<Email> emails = emailService.allMails(currentUser.getId());
            ObservableList<Email> observableEmails = FXCollections.observableArrayList(emails);
            emailTableView.setItems(observableEmails);
        }
    }

    private void showEmailDetails(Email email) {
        if (email != null) {
            emailService.readByCode(currentUser.getId(), email.getMessage_code())
                    .ifPresent(fullEmail -> emailBodyArea.setText(fullEmail.toString()));
        } else {
            emailBodyArea.clear();
        }
    }
}