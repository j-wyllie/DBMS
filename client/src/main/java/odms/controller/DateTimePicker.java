package odms.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;
import odms.data.DefaultLocale;

/**
 * Date picker with added time picking functionality
 */
public class DateTimePicker extends DatePicker {

    private static final String DefaultFormat = "d/M/yyyy H:mm";

    private DateTimeFormatter formatter;
    private ObjectProperty<LocalDateTime> dateTimeValue = new SimpleObjectProperty<>(
            LocalDateTime.now());
    private ObjectProperty<String> format = new SimpleObjectProperty<String>() {
        public void set(String newValue) {
            super.set(newValue);
            formatter = DateTimeFormatter.ofPattern(newValue);
        }
    };

    /**
     * Creates a new date time picker.
     */
    public DateTimePicker() {
        getStyleClass().add("datetime-picker");
        setFormat(DefaultFormat);
        setConverter(new InternalConverter());

        // Synchronize changes to the underlying date value back to the dateTimeValue
        valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null) {
                dateTimeValue.set(null);
            } else {
                if (dateTimeValue.get() == null) {
                    dateTimeValue.set(LocalDateTime.of(newValue, LocalTime.now()));
                } else {
                    LocalTime time = dateTimeValue.get().toLocalTime();
                    dateTimeValue.set(LocalDateTime.of(newValue, time));
                }
            }
        });

        // Synchronize changes to dateTimeValue back to the underlying date value
        dateTimeValue.addListener((observable, oldValue, newValue) -> {
            setValue(newValue == null ? null : newValue.toLocalDate());
        });
    }


    public LocalDateTime getDateTimeValue() {
        return dateTimeValue.get();
    }

    /**
     * Sets the date and time of the DateTimePicker if it is after 1971. Otherwise sets it to null.
     *
     * @param dateTimeValue Value to be set.
     */
    public void setDateTimeValue(LocalDateTime dateTimeValue) {
        if (dateTimeValue.isAfter(LocalDateTime.of(1900, 6, 30, 12, 00))) {
            this.dateTimeValue.set(dateTimeValue);
        } else {
            this.dateTimeValue.set(null);
        }
    }


    /**
     * Sets the date time value to null.
     */
    public void clearDateTimeValue() {
        this.dateTimeValue.set(null);
    }

    public String getFormat() {
        return format.get();
    }

    /**
     * Sets the format of the date time picker.
     *
     * @param format Format to be set. ie. DefaultFormat.
     */
    public void setFormat(String format) {
        this.format.set(format);
    }

    /**
     * Class to convert the dates to a string and vice-versa.
     */
    class InternalConverter extends StringConverter<LocalDate> {

        public String toString(LocalDate object) {

            LocalDateTime value = getDateTimeValue();
            return (value != null) ? value.format(formatter) : "";
        }

        /**
         * Gets a localdate from a string.
         *
         * @param value string to be converted to a date.
         * @return A local date.
         */
        public LocalDate fromString(String value) {
            if (value == null) {
                dateTimeValue.set(null);
                return null;
            }

            dateTimeValue.set(LocalDateTime.parse(value, formatter));
            return dateTimeValue.get().toLocalDate();
        }
    }
}

