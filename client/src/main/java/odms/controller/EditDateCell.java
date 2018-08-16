package odms.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javafx.beans.value.ChangeListener;
import javafx.event.Event;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import odms.commons.model.profile.Condition;

public class EditDateCell extends TableCell<Condition, LocalDate> {

    private TextField textField;

    public EditDateCell() {
    }

    @Override
    public void startEdit() {
        super.startEdit();

        if (textField == null) {
            createTextField();
        }

        setGraphic(textField);
        setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
        textField.selectAll();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();

        setText(String.valueOf(getItem()));
        setContentDisplay(ContentDisplay.TEXT_ONLY);
    }

    @Override
    public void updateItem(LocalDate item, boolean empty) {
        super.updateItem(item, empty);

        if (empty) {
            setText(null);
            setGraphic(null);
        } else {
            if (isEditing()) {
                if (textField != null) {
                    textField.setText(getString());
                }
                setGraphic(textField);
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            } else {
                setText(getString());
                setContentDisplay(ContentDisplay.TEXT_ONLY);
            }
        }
    }

    private void createTextField() {
        textField = new TextField(getString());
        textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        ChangeListener<? super Boolean> changeListener;
        changeListener = (observable, oldSelection, newSelection) -> {
            if (!newSelection) {
                try {
                    commitEdit(LocalDate.parse(textField.getText(), dtf));
                } catch (Exception e) {
                    // noop
                }
            }
        };
        textField.focusedProperty().addListener(changeListener);

        textField.setOnKeyPressed(ke -> {
            if (ke.getCode() == KeyCode.ENTER) {
                try {
                    textField.focusedProperty().removeListener(changeListener);
                    commitEdit(LocalDate.parse(textField.getText(), dtf));
                } catch (Exception e) {
                    // noop
                }
                textField.focusedProperty().addListener(changeListener);
            }
            if (ke.getCode().equals(KeyCode.ESCAPE)) {
                textField.focusedProperty().removeListener(changeListener);
                cancelEdit();
                textField.focusedProperty().addListener(changeListener);

            }
        });
    }

    private String getString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return getItem() == null ? "" : getItem().format(dtf);
    }

    @Override
    public void commitEdit(LocalDate item) {

        if (isEditing()) {
            super.commitEdit(item);
        } else {
            final TableView table = getTableView();
            if (table != null) {
                TablePosition position = new TablePosition(getTableView(), getTableRow().getIndex(),
                        getTableColumn());
                TableColumn.CellEditEvent editEvent = new TableColumn.CellEditEvent(table, position,
                        TableColumn.editCommitEvent(), item);
                Event.fireEvent(getTableColumn(), editEvent);
            }
            updateItem(item, false);
            if (table != null) {
                table.edit(-1, null);
            }

        }
    }
}
