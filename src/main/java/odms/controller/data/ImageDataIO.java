package odms.controller.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Support class to manage image IO operations.
 */
public final class ImageDataIO {
    private static final String IMAGES_DIR = "images";
    private static final File WORKING_DIR = new File(System.getProperty("user.dir"));
    private static final File PATH = new File(WORKING_DIR + File.separator + IMAGES_DIR);

    /**
     * Prevent instantiation.
     */
    private ImageDataIO() {
        throw new UnsupportedOperationException();
    }

    /**
     * Copies a file from source to destination.
     *
     * @param source File source in local directory
     * @param dest File destination in local directory
     */
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;

        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            Integer length;
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
     * @param image the new image file
     * @param name the user or profile name
     * @return return the filename used when saving
     * @throws IOException if an IO operation fails
     */
    public static String deleteAndSaveImage(File image, String name) throws IOException {
        deleteImage(name);

        File destination = getSaveDestination(image, name);
        copyFileUsingStream(image, destination);

        return destination.getName();
    }

    /**
     * Delete an old image.
     *
     * @param name the profile or user name to check against
     */
    private static void deleteImage(String name) {
        File defaultPath = getImagePath(name);
        Boolean deleted = false;

        if (new File(defaultPath + ".jpg").exists()) {
            deleted = new File(getImagePath(name) + ".jpg").delete();
        }

        if (new File(defaultPath + ".png").exists()) {
            deleted = new File(getImagePath(name) + ".png").delete();
        }

        if (deleted) {
            System.out.println("Old file deleted successfully");
        } else {
            System.out.println("Failed to delete the old file");
        }
    }

    /**
     * returns a string that is the file extension of given file.
     *
     * @param file File to retrieve extension from
     */
    private static String getFileExtension(File file) {
        String name = file.getName();
        return name.substring(name.lastIndexOf('.') + 1);
    }

    /**
     * Generate image path based on image name.
     *
     * @param imageName the chosen name
     * @return file path object
     */
    public static File getImagePath(String imageName) {
        return new File(getPath() + File.separator + imageName);
    }

    /**
     * Return path File object for base images path.
     * - Creates the path if it does not exist.
     *
     * @return path object
     */
    private static File getPath() {
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
