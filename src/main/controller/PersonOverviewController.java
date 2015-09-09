package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.MainApp;
import main.model.Person;
import main.util.DateUtil;

/**
 * Created by astronaut on 06.09.15.
 */
public class PersonOverviewController {
    @FXML
    private TableView<Person> personTable;
    @FXML
    private TableColumn<Person, String> firstNameColumn;
    @FXML
    private TableColumn<Person, String> lastNameColumn;

    @FXML
    private Label firstNameLabel;
    @FXML
    private Label lastNameLabel;
    @FXML
    private Label streetLabel;
    @FXML
    private Label cityLabel;
    @FXML
    private Label postalCodeLabel;
    @FXML
    private Label birthDayLabel;

    // Reference to the man application
    private MainApp mainApp;

    /**
     * The constructor
     * The constructor is called before the initialize() method
     */
    public PersonOverviewController() {
    }

    /**
     * Initializes the controller class. This method automatically called
     * after the fxml file has been loaded
     */
    @FXML
    private void initialize() {
        // initialize the person table with the two columns
        firstNameColumn.setCellValueFactory(cellData -> cellData.getValue().firstNameProperty());
        lastNameColumn.setCellValueFactory(cellData -> cellData.getValue().lastNameProperty());

        // clear person details
        showPersonDetails(null);

        // Listen for selection changes and show the person details when changed
        personTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails(newValue));
    }

    /**
     * Is called by the main application to give a reference back to itself.
     *
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        personTable.setItems(mainApp.getPersonData());
    }

    private void showPersonDetails(Person person) {
        if (person != null) {
            // fill the labels with info from the person object
            firstNameLabel.setText(person.getFirstName());
            lastNameLabel.setText(person.getLastName());
            streetLabel.setText(person.getStreet());
            postalCodeLabel.setText(Integer.toString(person.getPostalCode()));
            cityLabel.setText(person.getCity());
            birthDayLabel.setText(DateUtil.format(person.getBirthday()));
        } else {
            // person is null remove all the text
            firstNameLabel.setText("");
            lastNameLabel.setText("");
            streetLabel.setText("");
            postalCodeLabel.setText("");
            cityLabel.setText("");
            birthDayLabel.setText("");
        }
    }

    /**
     * Called when user click on the delete button.
     */
    @FXML
    private void handleDeletePerson() {
        int selectedIndex = personTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            personTable.getItems().remove(selectedIndex);
        } else {
            // Nothing selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("No selection");
            alert.setHeaderText("No person selected");
            alert.setContentText("Please select a person in the table");

            alert.showAndWait();
        }
    }

    /**
     * Called when user click on the "new" button
     */
    @FXML
    private void handleNewPerson() {
        Person person = new Person();
        boolean okClicked = mainApp.showPersonEditDialog(person);

        if (okClicked) {
            mainApp.getPersonData().add(person);
        }
    }

    /**
     * Called when user clicked on the "edit" button.
     * Opens a dialog to edit details for the selected person
     */
    @FXML
    private void handleEditPerson() {
        Person person = personTable.getSelectionModel().getSelectedItem();

        if (person != null) {
            boolean okClicked = mainApp.showPersonEditDialog(person);
            if (okClicked) {
                showPersonDetails(person);
            }
        } else {
            // nothing selected
            Alert alert = new Alert(Alert.AlertType.ERROR);

            alert.setTitle("No selection");
            alert.setHeaderText("No person selected");
            alert.setContentText("Please select a person in the table");

            alert.showAndWait();
        }
    }
}
