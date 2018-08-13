package odms.view.user;


import java.io.IOException;
import java.util.List;
import javafx.beans.Observable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import odms.controller.GuiMain;
import odms.controller.database.MySqlCountryDAO;
import odms.controller.history.Redo;
import odms.controller.history.Undo;
import odms.model.enums.CountriesEnum;
import odms.model.user.User;

public class UserGeneral {

    @FXML
    private Label givenNamesLabel;
    @FXML
    private Label staffIdLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label regionLabel;
    @FXML
    private TableView countriesTable;
    @FXML
    private TableColumn<CountriesEnum, String> countriesColumn;
    @FXML
    private TableColumn<CountriesEnum, Boolean> allowedColumn;

    private Redo redoController = new Redo();
    private Undo undoController = new Undo();
    private User currentUser;
    private MySqlCountryDAO mySqlCountryDAO = new MySqlCountryDAO();
    private ObservableList<CountriesEnum> countriesEnumObservableList = FXCollections
            .observableArrayList(
                    new Callback<CountriesEnum, Observable[]>() {
                        @Override
                        public Observable[] call(CountriesEnum param) {
                            return new Observable[]{param.getValidProperty()};
                        }
                    });


    /**
     * Button handler to undo last action.
     *
     * @param event clicking on the undo button.
     */
    @FXML
    private void handleUndoButtonClicked(ActionEvent event) throws IOException {
        //todo replace with standardised?
        undoController.undo(GuiMain.getCurrentDatabase());
        Parent parent = FXMLLoader.load(getClass().getResource("/view/ClinicianProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Button handler to redo last undo action.
     *
     * @param event clicking on the redo button.
     */
    @FXML
    private void handleRedoButtonClicked(ActionEvent event) throws IOException {
        //todo replace with standardised?
        redoController.redo(GuiMain.getCurrentDatabase());
        Parent parent = FXMLLoader.load(getClass().getResource("/view/ClinicianProfile.fxml"));
        Scene newScene = new Scene(parent);
        Stage appStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        appStage.setScene(newScene);
        appStage.show();
    }

    /**
     * Button handler to make fields editable.
     *
     * @param event clicking on the edit button.
     */
    @FXML
    private void handleEditButtonClicked(ActionEvent event) throws IOException {

        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/view/ClinicianProfileEdit.fxml"));

        Scene scene = new Scene(fxmlLoader.load());
        //todo replace scene change with standardised and controller with view
        ClinicianEdit v = fxmlLoader.getController();
        v.setCurrentUser(currentUser);
        v.initialize();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Edit profile");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Populates the countries table with a list of countries. Populates a column with a checkbox
     * that is ticked if the country is valid.
     */
    private void setupCountriesTable() {
        List<String> allCountries = mySqlCountryDAO.getAll();

        List<String> validCountries = mySqlCountryDAO.getAll(true);
        for (String country : allCountries) {
            CountriesEnum countryEnum = CountriesEnum.getEnumByString(country);
            if (countryEnum != null && validCountries.contains(country)) {
                countryEnum.setValid(true);
                countriesEnumObservableList.add(countryEnum);
            } else {
                if (countryEnum != null) {
                    countryEnum.setValid(false);
                    countriesEnumObservableList.add(countryEnum);
                }
            }
        }
        countriesColumn.setSortable(false);
        allowedColumn.setSortable(false);
        countriesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        countriesTable.setItems(countriesEnumObservableList);
        countriesColumn.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        allowedColumn.setCellValueFactory(
                cellData -> new SimpleBooleanProperty(cellData.getValue().getValid()));
    }

    /**
     * Adds listeners to the valid countries checkboxes. One for key pressed and one for mouse
     * pressed.
     */
    private void addAllowedColumnListeners() {
        allowedColumn.setCellFactory(p -> {
            CheckBox checkBox = new CheckBox();
            TableCell<CountriesEnum, Boolean> tableCell = new TableCell<CountriesEnum, Boolean>() {

                @Override
                protected void updateItem(Boolean item, boolean empty) {

                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        setGraphic(checkBox);
                        checkBox.setSelected(item);
                    }
                }
            };

            checkBox.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {
                CountriesEnum countriesEnum = ((CountriesEnum) tableCell.getTableRow().getItem());
                countriesEnum.setValid(!countriesEnum.getValid());
                countriesEnumObservableList.set(tableCell.getTableRow().getIndex(), countriesEnum);

                Integer count = 0;
                for (CountriesEnum country : countriesEnumObservableList) {
                    if (country.getValid()) {
                        count++;
                        if (count > 1) {
                            break;
                        }
                    }
                }
                if (count == 0) {
                    checkBox.setSelected(checkBox.isSelected());
                    countriesEnum.setValid(!countriesEnum.getValid());
                    countriesEnumObservableList.set(tableCell.getTableRow().getIndex(), countriesEnum);
                }

                mySqlCountryDAO.update(countriesEnum,
                        countriesEnum.getValid());
            });

            checkBox.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (event.getCode() == KeyCode.SPACE) {
                    CountriesEnum countriesEnum = ((CountriesEnum) tableCell.getTableRow().getItem());
                    checkBox.setSelected(!countriesEnum.getValid());
                }
            });

            tableCell.setAlignment(Pos.CENTER);
            tableCell.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            return tableCell;
        });
    }

    public void initialize(User currentUser) {
        this.currentUser = currentUser;
        givenNamesLabel.setText(
                givenNamesLabel.getText() + (
                        currentUser.getName() != null ? currentUser.getName() : ""));
        staffIdLabel.setText(
                staffIdLabel.getText() + (
                        currentUser.getStaffID() != null ? currentUser.getStaffID() : ""));
        addressLabel.setText(
                addressLabel.getText() +
                        (currentUser.getWorkAddress() != null ? currentUser.getWorkAddress() : "")
        );
        regionLabel.setText(
                regionLabel.getText() +
                        (currentUser.getRegion() != null ? currentUser.getRegion() : "")
        );

        setupCountriesTable();
        addAllowedColumnListeners();
    }
}
