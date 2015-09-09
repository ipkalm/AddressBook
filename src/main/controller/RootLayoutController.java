package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import main.MainApp;

import java.io.File;

/**
 * Created by astronaut on 07.09.15.
 * <p>
 * The controller for the root layout. The root layout provide the basic
 * application layout containing a menu bar and space whre other JavaFX elements
 * can be placed.
 *
 * @author astronaut
 */
public class RootLayoutController {
    // Reference to the main app
    private MainApp mainApp;

    /**
     * Is called by the main app to give a reference back to itself
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Create an empty address book
     */
    @FXML
    private void handleNew() {
        mainApp.getPersonData().clear();
        mainApp.setPersonalFilePath(null);
    }

    /**
     * Opens a FileChooser to let the user select an address book to load.
     */
    @FXML
    private void handleOpen() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // show save file dialog
        File file = fileChooser.showOpenDialog(mainApp.getPrimaryStage());

        if (file != null) {
            mainApp.loadPersonDataFromFile(file);
        }
    }

    /**
     * Save the file to the person file that currently open. If there is no open
     * file, the "Save as..." dialog is shown.
     */
    @FXML
    private void handleSave() {
        File personFile = mainApp.getPersonFilePath();
        if (personFile != null)
            mainApp.savePersonDataToFile(personFile);
        else
            handleSaveAs();
    }

    /**
     * Open a FileChooser to let the user select a file to save to.
     */
    @FXML
    private void handleSaveAs() {
        FileChooser fileChooser = new FileChooser();

        // Set extension filter
        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("XML Files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extensionFilter);

        // Show save dialog
        File file = fileChooser.showSaveDialog(mainApp.getPrimaryStage());

        if (file != null) {
            // Make sure that correct extension
            if (!file.getPath().endsWith(".xml")) {
                file = new File(file.getPath() + ".xml");
            }
            mainApp.savePersonDataToFile(file);
        }
    }

    /**
     * Opens an about alert
     */
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);

        alert.setTitle("AddressApp");
        alert.setHeaderText("About");
        alert.setContentText("Author: Petr Kalmykov\nSite: http://www.stonedastronaut.github.io");
        alert.showingProperty();

        alert.showAndWait();
    }

    /**
     * Close app
     */
    @FXML
    private void handleExit() {
        System.exit(0);
    }

    /**
     * Opens the birthday statistic
     */
    @FXML
    private void handleShowBirthdayStatistic() {
        mainApp.showBirthdayStatistic();
    }
}
