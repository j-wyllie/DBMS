package odms.controller.data;

import java.io.*;

final class CommonDataIO {

    private CommonDataIO() {
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
