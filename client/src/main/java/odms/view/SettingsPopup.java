package odms.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import odms.controller.SettingsPopupController;

public class SettingsPopup {

    private SettingsPopupController controller = new SettingsPopupController(this);

    @FXML private ChoiceBox languageSelect;

    public void initialize() {
        ObservableList<String> observableLanguageSelect = FXCollections.observableArrayList();
        observableLanguageSelect.addAll(controller.getLanguageOptions());
        languageSelect.setItems(observableLanguageSelect);
    }
}
