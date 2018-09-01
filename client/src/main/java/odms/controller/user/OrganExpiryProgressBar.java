package odms.controller.user;

import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.shape.Line;
import javafx.util.Callback;

/**
 * Configures the organ expiry progress bar in the table view.
 * @param <S>
 */
public final class OrganExpiryProgressBar<S> extends TableCell<S, Double> {

    private final ProgressBar progressBar;

    /**
     * Organ expiry progress bar constructor.
     * @param style style to set the progress bar.
     */
    private OrganExpiryProgressBar(String style) {
        this.progressBar = new ProgressBar();
        this.progressBar.setMaxWidth(Double.MAX_VALUE);
        this.progressBar.setPadding(new Insets(5));

        progressBar.getStylesheets().clear();
        progressBar.getStylesheets().add(style);
        getStylesheets().clear();
        getStylesheets().add(style);
    }

    /**
     * Gets an organ expiry progress bar with the correct style.
     * @param style style being set.
     * @param <S> table cell value.
     * @return organ expiry progress bar being set.
     */
    public static <S> Callback<TableColumn<S, Double>, TableCell<S, Double>> forTableColumn(
            String style) {
        return param -> new OrganExpiryProgressBar<>(style);
    }

    /**
     * Sets the progress bar colour to green.
     */
    private void setGreen() {
        progressBar.getStyleClass().add("progress-bar-green");
    }

    /**
     * Sets the progress bar colour to red.
     */
    private void setRed() {
        progressBar.getStyleClass().add("progress-bar-red");
    }

    /**
     * Sets the progress bar colour to yellow.
     */
    private void setYellow() {
        progressBar.getStyleClass().add("progress-bar-yellow");
    }

    @Override
    public void updateItem(Double item, boolean empty) {
        String value;

        try {
            value = String.valueOf(super.getTableView().getItems().get(super.getIndex()));
        } catch (IndexOutOfBoundsException e) {
            value = "";
        }
        super.updateItem(item, empty);
        if (item != null) {
            if (item >= 0.8) {
                setGreen();
            } else if (item >= 0.2) {
                setYellow();
            } else {
                setRed();
            }
        }

        configureGraphic(item, empty, value);
    }

    /**
     * Sets the graphic of the progress bar based on the organ.
     * @param item Item being set.
     * @param empty Boolean to check if item is empty.
     * @param value Value of table view item.
     */
    private void configureGraphic(Double item, boolean empty, String value) {
        if (empty) {
            setGraphic(null);
        } else {
            progressBar.progressProperty().unbind();

            final TableColumn<S, Double> column = getTableColumn();
            ObservableValue<Double> observable = column == null ? null : column.getCellObservableValue(getIndex());

            if (observable != null) {
                progressBar.progressProperty().bind(observable);
            } else if (item != null) {
                progressBar.setProgress(item);
            }
            progressBar.setMinWidth(getTableColumn().getWidth());
            progressBar.setMaxWidth(getTableColumn().getWidth());

            Group group = new Group();
            group.getChildren().add(progressBar);
            if (value.contains("HEART") || value.contains("LUNG") || value.contains("KIDNEY")) {
                Line line = new Line(30 * getTableColumn().getWidth() / 100, 20,
                        30 * getTableColumn().getWidth() / 100,0);
                group.getChildren().add(line);
            } else if (value.contains("CORNEA")) {
                Line line = new Line(28 * getTableColumn().getWidth() / 100, 20,
                        28 * getTableColumn().getWidth() / 100,0);
                group.getChildren().add(line);
            } else if (value.contains("PANCREAS")) {
                Line line = new Line(50 * getTableColumn().getWidth() / 100, 20,
                        50 * getTableColumn().getWidth() / 100,0);
                group.getChildren().add(line);
            } else if (!value.equals("") && !value.contains("LIVER")) {
                Line line = new Line(70 * getTableColumn().getWidth() / 100, 20,
                        70 * getTableColumn().getWidth() / 100,0);
                group.getChildren().add(line);
            }
            setGraphic(group);
        }
    }
}
