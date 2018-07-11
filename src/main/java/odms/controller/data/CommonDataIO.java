package odms.controller.data;

import java.io.*;

class CommonDataIO {

    /**
     * Populate the fileBuffer with the content of the file.
     *
     * @param file       the file path to read
     * @param fileBuffer the buffer to populate
     */
    static void loadDataInBuffer(File file, StringBuilder fileBuffer) {
        String lineBuffer;
        try {
            BufferedReader readFile = new BufferedReader(new FileReader(file));

            while ((lineBuffer = readFile.readLine()) != null) {
                fileBuffer.append(lineBuffer);
            }

            readFile.close();

        } catch (FileNotFoundException e) {
            System.out.println("File not found");
            System.out.println("File requested: " + file);
        } catch (IOException e) {
            System.out.println("IO exception, please check the specified file");
            System.out.println("File requested: " + file);
        }
    }
}
