package server.model.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class CommonDataIO {

    public CommonDataIO() {
        throw new UnsupportedOperationException();
    }

    /**
     * Populate the fileBuffer with the content of the file.
     *
     * @param file       the file path to read
     * @param fileBuffer the buffer to populate
     */
    static void loadDataInBuffer(File file, StringBuilder fileBuffer) {
        BufferedReader readFile;
        String lineBuffer;
        try {
            readFile = new BufferedReader(new FileReader(file));

            while ((lineBuffer = readFile.readLine()) != null) {
                fileBuffer.append(lineBuffer);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.out.println("File requested: " + file);
        } catch (IOException e) {
            System.out.println("IO exception, please check the specified file");
            System.out.println("File requested: " + file);
        }
    }
}
