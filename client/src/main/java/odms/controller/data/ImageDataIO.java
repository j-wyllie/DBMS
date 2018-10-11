package odms.controller.data;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * Support class to manage image IO operations.
 */
@Slf4j
public final class ImageDataIO {
    private static final String IMAGES_DIR = "client/src/main/resources/profile_images";
    private static  String WORKING_DIR;
    private static File PATH;

    /**
     * Prevent instantiation.
     */
    private ImageDataIO() {
        throw new UnsupportedOperationException();
    }

    private static void createWorkingDir() throws IOException {
        WORKING_DIR = new File(".").getCanonicalPath();
        PATH = new File(WORKING_DIR + File.separator + IMAGES_DIR);
    }


    /**
     * Copies a file from source to destination.
     *
     * @param source File source in local directory.
     * @param dest File destination in local directory.
     */
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                System.out.println("Error in closing input stream for source." + source);
            }
            try {
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                System.out.println("Error in closing output stream for destination." + dest);
            }
        }
    }

    /**
     * Remove the old profile or user image and save the new image.
     *
     * @param image the new image file.
     * @param name the user or profile name.
     * @return return the filename used when saving.
     * @throws IOException if an IO operation fails.
     */
    public static String deleteAndSaveImage(File image, String name) throws IOException {
        deleteImage(name);

        if (image == null) {
            throw new IOException("No file specified");
        }

        File destination = getSaveDestination(image, name);
        copyFileUsingStream(image, destination);

        return destination.getName();
    }

    /**
     * Delete an old image.
     *
     * @param name the profile or user name to check against.
     */
    public static void deleteImage(String name) {
        try {
            for (File file : getExistingImages(name)) {
                Files.delete(file.toPath());
            }
        } catch (IOException e) {
            System.out.println("Failed to delete the old file.");
            System.out.println("Error: " + e.getMessage());
        }
    }

    /**
     * Support function to find old profile or user images.
     *
     * @param name the profile or user name to check against.
     * @return a File array of files found.
     */
    private static File[] getExistingImages(String name) {
        FileFilter fileFilter = new WildcardFileFilter(name + ".*");
        return getPath().listFiles(fileFilter);
    }

    /**
     * returns a string that is the file extension of given file.
     *
     * @param file File to retrieve extension from.
     */
    private static String getFileExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Generate image path based on image name.
     *
     * @param imageName the chosen name.
     * @return file path object.
     */
    public static File getImagePath(String imageName) {
        if (imageName != null && !imageName.isEmpty()) {
            return new File(getPath() + File.separator + imageName);
        }
        return null;
    }

    /**
     * Return path File object for base images path.
     * - Creates the path if it does not exist.
     *
     * @return path object.
     */
    public static File getPath() {
        try {
            createWorkingDir();
        } catch (IOException e) {
            log.error("Failed to get PATH for images");
            log.error(e.getMessage(), e);
        }


        if (!PATH.exists()) {
            PATH.mkdirs();
        }
        return PATH;
    }

    /**
     * Generates the save destination based on profile / user name.
     *
     * @param image the selected image
     * @param name the profile / user name
     * @return destination path
     */
    private static File getSaveDestination(File image, String name) {
        String extension = getFileExtension(image).toLowerCase();
        return new File(getImagePath(name) + "." + extension);
    }

}

