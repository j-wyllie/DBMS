package odms.view.user;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.util.Callback;

class TestProgressBar<S> extends TableCell<S, Double> {

    public static <S> Callback<TableColumn<S, Double>, TableCell<S, Double>> forTableColumn(
            String style) {
        return param -> new TestProgressBar<>(style);
    }

    private final ProgressBar progressBar;

    private ObservableValue<Double> observable;
    public TestProgressBar(String style) {
        this.progressBar = new ProgressBar();
        this.progressBar.setMaxWidth(Double.MAX_VALUE);

        progressBar.getStylesheets().clear();
        progressBar.getStylesheets().add(style);
        getStylesheets().clear();
        getStylesheets().add(style);
    }


    public void setGreen() {
        progressBar.getStyleClass().add("progress-bar-green");
    }

    public void setRed() {
        progressBar.getStyleClass().add("progress-bar-red");
    }

    public void setYellow() {
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
        if (value.contains("HEART") || value.contains("LUNG") || value.contains("KIDNEY")) {
            getStyleClass().add("lower-bound-heart");
        } else if (value.contains("CORNEAS")) {
            getStyleClass().add("lower-bound-corneas");
        } else if (value.contains("PANCREAS")) {
            getStyleClass().add("lower-bound-pancreas");
        } else if (!value.equals("") && !value.contains("LIVER")) {
            getStyleClass().add("lower-bound-generic");
        }

        if (empty) {
            setGraphic(null);
        } else {

            progressBar.progressProperty().unbind();

            final TableColumn<S, Double> column = getTableColumn();
            observable = column == null ? null : column.getCellObservableValue(getIndex());

            if (observable != null) {
                progressBar.progressProperty().bind(observable);
            } else if (item != null) {
                progressBar.setProgress(item);
            }

            setGraphic(progressBar);
        }
    }
}
