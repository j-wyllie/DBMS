package odms.controller.profile;

import java.io.File;

public class InvalidFileException extends Exception {

        private File file;

        InvalidFileException(String message, File file) {
            super(message);
            this.file = file;
        }

        public File getFile() {
            return file;
    }

}
