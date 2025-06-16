package com.milou.utils;

import com.milou.MilouGUI;
import com.milou.gui.controllers.MainWindowController;
import com.milou.models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    public static void openMainWindow(ActionEvent event, User user) {
        try {
            FXMLLoader loader = new FXMLLoader(MilouGUI.class.getResource("/views/main-window-view.fxml"));

            Scene scene = new Scene(loader.load(), 800, 600);

            MainWindowController controller = loader.getController();
            controller.initData(user);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setTitle("Milou - " + user.getName());
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}