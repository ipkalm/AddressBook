package main;/**
 * Created by astronaut on 06.09.15.
 */

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.model.Person;
import main.model.PersonListWrapper;
import main.controller.BirthdayStatisticController;
import main.controller.PersonEditDialogController;
import main.controller.PersonOverviewController;
import main.controller.RootLayoutController;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.prefs.Preferences;

public class MainApp extends Application {
    private Stage primaryStage;
    private BorderPane rootLayout;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("AddressApp");
        this.primaryStage.getIcons().add(new Image("file:src/resources/img/icon.png"));

        initRootLayout();

        showPersonOverView();
    }

    /**
     * Initialize root layout and tries to load last opened person file
     */
    public void initRootLayout() {
        try {
            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class
                    .getResource("view/RootLayout.fxml"));
            rootLayout = (BorderPane) loader.load();

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            // Give the controller access to the main app.
            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);

            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Try to load last opened person file.
        File file = getPersonFilePath();
        if (file != null) {
            loadPersonDataFromFile(file);
        }
    }

    /**
     * Show the person overview inside the root layout
     */
    public void showPersonOverView() {
        try {
            // Load person overview
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonOverview.fxml"));
            AnchorPane personOverview = (AnchorPane) loader.load();

            // Set person overview into the center of root layout
            rootLayout.setCenter(personOverview);

            // Give the controller address to the main app
            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the main stage
     *
     * @return
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * The data as an observer list of Persons
     */
    private ObservableList<Person> personData = FXCollections.observableArrayList();

    /**
     * Constructor
     */
    public MainApp() {
        // Add some sample data
        personData.add(new Person("Hans", "Muster"));
        personData.add(new Person("Ruth", "Mueller"));
        personData.add(new Person("Heinz", "Kurz"));
        personData.add(new Person("Cornelia", "Meier"));
        personData.add(new Person("Werner", "Meyer"));
        personData.add(new Person("Lydia", "Kunz"));
        personData.add(new Person("Anna", "Best"));
        personData.add(new Person("Stefan", "Meier"));
        personData.add(new Person("Martin", "Mueller"));
    }

    /**
     * Returns the data as an observer list of Persons
     */
    public ObservableList<Person> getPersonData() {
        return personData;
    }

    /**
     * Open a dialog to edit details for the specified person. If the user clocks "Ok"
     * the changes are saved into the provided person object and true is returned
     *
     * @param person the person object to be edited
     * @return true if user clicked Ok, false otherwise
     */
    public boolean showPersonEditDialog(Person person) {
        try {
            // Load fxml file and create a new stage for the popup dialog
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/PersonEditDialog.fxml"));
            AnchorPane pane = (AnchorPane) loader.load();

            // Create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(pane);
            dialogStage.setScene(scene);

            // Set the person into controller
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            // show the dialog and wait until user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();


        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specify register. If no such
     * preference can be found, null returned
     *
     * @return
     */
    public File getPersonFilePath() {
        Preferences preferences = Preferences.systemNodeForPackage(MainApp.class);
        String filePath = preferences.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Set the filepath of the currently loaded file.
     * The path is persisted in the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public void setPersonalFilePath(File file) {
        Preferences preferences = Preferences.systemNodeForPackage(MainApp.class);

        if (file != null) {
            preferences.put("filePath", file.getPath());

            // Update the stage title
            primaryStage.setTitle("AddressApp - " + file.getName());
        } else {
            preferences.remove("filePath");

            // Update the stage title
            primaryStage.setTitle("AddressApp");
        }
    }

    /**
     * Load person data from specified file. The current person data will be replaces.
     *
     * @param file
     */
    public void loadPersonDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // Reading XML from the file and unmarshling
            PersonListWrapper wrapper = (PersonListWrapper) unmarshaller.unmarshal(file);

            personData.clear();
            personData.addAll(wrapper.getPersons());

            // Save the file path to the registry
            setPersonalFilePath(file);
        } catch (Exception e) {
            ExceptionDialog(e, "Could not load data from file:\n" + file.getPath());
        }
    }

    /**
     * Save the current person data to specified file.
     *
     * @param file
     */
    public void savePersonDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our data
            PersonListWrapper wrapper = new PersonListWrapper();
            wrapper.setPersons(personData);

            // Marshalling and saving XML to the file.
            marshaller.marshal(wrapper, file);

            // Save the file path to the registry
            setPersonalFilePath(file);
        } catch (Exception e) {
            ExceptionDialog(e, "Could not save data to file:\n" + file.getPath());
        }
    }

    /**
     * Create dialog with exception message
     *
     * @param e       exception
     * @param message string message
     */
    private static void ExceptionDialog(Exception e, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);

        alert.setTitle("Error");
        alert.setHeaderText(message);

        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        e.printStackTrace(printWriter);
        String exceptionText = stringWriter.toString();

        Label label = new Label("The exception stacktrace was:");

        TextArea textArea = new TextArea(exceptionText);
        textArea.setEditable(false);
        textArea.setWrapText(true);

        textArea.setMaxWidth(Double.MAX_VALUE);
        textArea.setMaxHeight(Double.MAX_VALUE);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        GridPane.setHgrow(textArea, Priority.ALWAYS);

        GridPane expContent = new GridPane();
        expContent.setMaxWidth(Double.MAX_VALUE);
        expContent.add(label, 0, 0);
        expContent.add(textArea, 0, 1);

        // Set expandable Exception into the dialog pane.
        alert.getDialogPane().setExpandableContent(expContent);

        alert.showAndWait();
    }

    /**
     * Open a dialog to show birthday statistic
     */
    public void showBirthdayStatistic() {
        try {
            // Load fxml file and create a new stage for the popup
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("view/BirthdayStatistic.fxml"));

            AnchorPane pane = (AnchorPane) loader.load();
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Birthday Statistic");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(this.primaryStage);

            Scene scene = new Scene(pane);
            dialogStage.setScene(scene);

            // Set the persons into the controller
            BirthdayStatisticController controller = loader.getController();
            controller.setPersonData(personData);

            dialogStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
