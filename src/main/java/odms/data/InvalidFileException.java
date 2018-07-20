package odms.data;

import java.io.File;

public class InvalidFileException extends Exception {

    private File file;

    public InvalidFileException(String message, File file) {
        super(message);
        this.file = file;
    }

    public File getFile() {
        return file;
    }

}