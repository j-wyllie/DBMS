package odms.view.user;

import javafx.beans.value.ObservableValue;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.ProgressBarTableCell;
import javafx.util.Callback;

class TestProgressBar<S> extends TableCell<S, Double> {
    /***************************************************************************
     *                                                                         *
     * Static cell factories                                                   *
     *                                                                         *
     **************************************************************************/

    /**
     * Provides a {@link ProgressBar} that allows easy visualisation of a Number
     * value as it proceeds from 0.0 to 1.0. If the value is -1, the progress
     * bar will appear indeterminate.
     *
     * @return A {@link Callback} that can be inserted into the
     *      {@link TableColumn#cellFactoryProperty() cell factory property} of a
     *      TableColumn, that enables visualisation of a Number as it progresses
     *      from 0.0 to 1.0.
     */
    public static <S> Callback<TableColumn<S,Double>, TableCell<S,Double>> forTableColumn(String style) {
        return param -> new TestProgressBar<>(style);
    }



    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final ProgressBar progressBar;

    private ObservableValue<Double> observable;



    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a default {@link ProgressBarTableCell} instance
     */
    public TestProgressBar(String style) {
        this.progressBar = new ProgressBar();
        this.progressBar.setMaxWidth(Double.MAX_VALUE);

        progressBar.getStylesheets().clear();
        progressBar.getStylesheets().add(style);
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



    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    /** {@inheritDoc} */
    @Override public void updateItem(Double item, boolean empty) {
        super.updateItem(item, empty);
        if(item != null) {
            if (item >= 0.8) {
                setGreen();
            } else if (item >= 0.2) {
                setYellow();
            } else {
                setRed();
            }
        }



        if (empty) {
            setGraphic(null);
        } else {

            progressBar.progressProperty().unbind();

            final TableColumn<S,Double> column = getTableColumn();
            observable = column == null ? null : column.getCellObservableValue(getIndex());

            if (observable != null) {
                progressBar.progressProperty().bind(observable);
            } else if (item != null) {
                progressBar.setProgress(item);
            }

            setGraphic(progressBar);
        }
    }
//    public TestProgressBar(String style) {
//        super();
//
////        System.out.println(style);
//        super.getStyleClass().clear();
//        super.getStylesheets().clear();
//        super.getStylesheets().add(style);
////        this.setStyle(".progress-bar > .bar {\n" +
////                "    -fx-background-color: #000000;\n" +
////                "}");
////        this.getStyle().
////        this.getStyleClass().clear();
//        super.getStyleClass().clear();
//        super.getStyleClass().add("black-bar");
//    }
//
//    @Override public void updateItem(Double item, boolean empty) {
//        System.out.println(this.getStylesheets());
//        System.out.println(this.getStyleClass());
//        System.out.println(this.getStyle());
//
//        super.getStyleClass().clear();
//        super.getStyleClass().add("black-bar");
//
//
//
//        super.updateItem(item, empty);
//        super.getStyleClass().clear();
//        super.getStyleClass().add("black-bar");
//    }
//
//    public static <S> Callback<TableColumn<S,Double>, TableCell<S,Double>> forTableColumn(String style) {
//        return param -> new TestProgressBar<S>(style);
//    }
}
