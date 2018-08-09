//package GUI;
//
//import com.sun.javafx.robot.impl.FXRobotHelper;
//import javafx.collections.ObservableList;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.input.KeyCode;
//import javafx.scene.input.MouseButton;
//import javafx.stage.Modality;
//import javafx.stage.Stage;
//import javafx.stage.Window;
//import odms.controller.GuiMain;
//import odms.controller.data.ProfileDataIO;
//import odms.tools.TestDataCreator;
//import org.junit.After;
//import org.testfx.framework.junit.ApplicationTest;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//
///**
// * regularly used methods in testfx. Add to this if you find yourself copy and pasting a lot of
// * code.
// */
//abstract class TestFxMethods extends ApplicationTest {
//
//    protected GuiMain guiMain;
//
//    @After()
//    public void tearDown() throws Exception {
//        release(new KeyCode[]{});
//        release(new MouseButton[]{});
//        sleep(500);
//    }
//
//    /**
//     * Initializes the main gui
//     *
//     * @param stage current stage
//     * @throws Exception throws Exception
//     */
//    @Override
//    public void start(Stage stage) throws Exception {
//        guiMain = new GuiMain();
//        guiMain.setCurrentDatabase(new TestDataCreator().getDatabase());
//        guiMain.start(stage);
//    }
//
//    protected void closeStages() {
//        try {
//            Integer numberOfStages = FXRobotHelper.getStages().size();
//            ObservableList<Stage> stages = FXRobotHelper.getStages();
//
//            for (Integer i = 0; i < numberOfStages; i++) {
//                stages.get(i).close();
//            }
//        } catch (IllegalStateException e) {
//            System.out.println("Caught"); // TODO
//        }
//    }
//
//    /**
//     * logs in the clinician and opens up the search tab
//     */
//    protected void loginAsClinician() {
//        clickOn("#usernameField").write("0");
//        clickOn("#loginButton");
//    }
//
//    /**
//     * logs in as a donor with the given ID
//     *
//     * @param id
//     */
//    protected void loginAsDonor(Integer id) {
//        clickOn("#usernameField").write(id.toString());
//        clickOn("#loginButton");
//    }
//
//    /**
//     * Checks that the correct donor's profile is opened from the search table.
//     */
//    protected void openSearchedProfile(String name) {
//        clickOn("#searchTab");
//        clickOn("#searchField").write(name);
//        doubleClickOn(row("#searchTable", 0));
//    }
//
//    /**
//     * Presses yes on a confirmation dialogue
//     */
//    protected void closeYesConfirmationDialogue() {
//        Stage stage = getAlertDialogue();
//        DialogPane dialogPane = (DialogPane) stage.getScene().getRoot();
//        Button yesButton = (Button) dialogPane.lookupButton(ButtonType.YES);
//        clickOn(yesButton);
//    }
//
//    protected Integer getProfileIdFromWindow() {
//        Scene newScene = getTopScene();
//        Label userId = (Label) newScene.lookup("#userIdLabel");
//        return Integer.parseInt(userId.getText().substring(10, userId.getText().length()));
//    }
//
//    /**
//     * Deletes the line of text
//     */
//    protected void deleteLine() {
//        push(KeyCode.CONTROL, KeyCode.A).push(KeyCode.BACK_SPACE);
//    }
//
//    /**
//     * Saves the current database
//     */
//    protected void saveDatabase() {
//        ProfileDataIO.saveData(GuiMain.getCurrentDatabase(), "example/example.json");
//    }
//
//    /**
//     * gets current stage with all windows.
//     *
//     * @return All of the current windows
//     */
//    protected Scene getTopScene() {
//        // Get a list of windows but ordered from top[0] to bottom[n] ones.
//        // It is needed to get the first found modal window.
//        final List<Window> allWindows = new ArrayList<>(
//                robotContext().getWindowFinder().listWindows());
//        Collections.reverse(allWindows);
//
//        return allWindows.get(0).getScene();
//    }
//
//    /**
//     * gets current stage with all windows. Used to check that an alert controller has been created
//     * and is visible
//     *
//     * @return All of the current windows
//     */
//    protected javafx.stage.Stage getAlertDialogue() {
//        // Get a list of windows but ordered from top[0] to bottom[n] ones.
//        // It is needed to get the first found modal window.
//        final List<Window> allWindows = new ArrayList<>(
//                robotContext().getWindowFinder().listWindows());
//        Collections.reverse(allWindows);
//
//        return (javafx.stage.Stage) allWindows
//                .stream()
//                .filter(window -> window instanceof javafx.stage.Stage)
//                .filter(window -> ((javafx.stage.Stage) window).getModality()
//                        == Modality.APPLICATION_MODAL)
//                .findFirst()
//                .orElse(null);
//    }
//
//    /**
//     * Closes the currently open alert dialogue
//     *
//     * @param alert the alert DialogPane to be closed
//     */
//    protected void closeDialog(DialogPane alert) {
//        robotContext();
//        Button closeButton = (Button) alert.lookupButton(ButtonType.CLOSE);
//        closeButton.setId("Close");
//        clickOn("#Close");
//    }
//
//    /**
//     * @param tableSelector The id of the table to be used
//     * @return Returns a table view node from the given ID
//     */
//    protected TableView<?> getTableView(String tableSelector) {
//        Node node = lookup(tableSelector).queryTableView();
//        return (TableView<?>) node;
//    }
//
//    /**
//     * @param tableSelector The id of the table that contains the cell wanted
//     * @param row           row number
//     * @param column        column number
//     * @return returns the cell data.
//     */
//    protected Object cellValue(String tableSelector, int row, int column) {
//        return getTableView(tableSelector).getColumns().get(column).getCellData(row);
//    }
//
//    /**
//     * @param tableSelector Id of table that contains the row
//     * @param row           row number
//     * @return returns a table row
//     */
//    protected TableRow<?> row(String tableSelector, int row) {
//
//        TableView<?> tableView = getTableView(tableSelector);
//
//        List<Node> current = tableView.getChildrenUnmodifiable();
//        while (current.size() == 1) {
//            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
//        }
//
//        current = ((Parent) current.get(1)).getChildrenUnmodifiable();
//        while (!(current.get(0) instanceof TableRow)) {
//            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
//        }
//
//        Node node = current.get(row);
//        return (TableRow<?>) node;
//    }
//
//    /**
//     * @param tableSelector ID of the table that contains the cell wanted
//     * @param row           row number
//     * @param column        column number
//     * @return the cell of the table
//     */
//    protected TableCell<?, ?> cell(String tableSelector, int row, int column) {
//        List<Node> current = row(tableSelector, row).getChildrenUnmodifiable();
//        while (current.size() == 1 && !(current.get(0) instanceof TableCell)) {
//            current = ((Parent) current.get(0)).getChildrenUnmodifiable();
//        }
//
//        Node node = current.get(column);
//        return (TableCell<?, ?>) node;
//    }
//}
