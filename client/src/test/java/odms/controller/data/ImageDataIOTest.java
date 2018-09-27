package odms.controller.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ImageDataIOTest {

    private final String accountName = "account";

    private final String imageFolder = "images";
    private final String imageNameJpg = "account.jpg";
    private final String imageNamePng = "account.png";
    private final String imagePath = imageFolder + File.separator + imageNameJpg;

    private final File testImageDir = new File(
            new File(System.getProperty("user.dir")) +
                    File.separator +
                    imageFolder
    );

    private final File testImageFilePath = new File(imageFolder);

    private final File testImageJpg = new File(testImageFilePath + File.separator + imageNameJpg);
    private final File testImagePng = new File(testImageFilePath + File.separator + imageNamePng);

    private final File testImageFile = FileUtils.toFile(
            Thread.currentThread().getContextClassLoader().getResource(
                    "imageDataIOTestData" +
                            File.separator +
                            "account.jpg"
            )
    );

    private void createTestDir() {
        testImageDir.mkdirs();
    }

    private void copyTestImageToImages(File image) throws IOException {
        // Generate something to delete
        Files.copy(
                this.testImageFile.toPath(),
                image.toPath()
        );

        // Ensure it exists to prevent false positives in test
        assertTrue(image.exists());
    }

    private void copyTestImagesToImages() throws IOException {
        copyTestImageToImages(testImageJpg);
        copyTestImageToImages(testImagePng);
    }

    private void deleteTestImages() {
        if (testImageJpg.exists()) {
            testImageJpg.delete();
        }
        if (testImagePng.exists()) {
            testImagePng.delete();
        }
    }

    @Before
    public void setup() {
        this.createTestDir();
    }

//    @After
//    public void cleanup() {
//        ImageDataIO.deleteImage(imageNamePng);
//        this.deleteTestImages();
//    }
//
//    @Test
//    public void testDeleteAndSaveImageWithValidFile() throws IOException {
//        copyTestImagesToImages();
//
//        ImageDataIO.deleteAndSaveImage(testImageFile, this.accountName);
//
//        assertFalse(testImagePng.exists());
//        assertTrue(testImageJpg.exists());
//    }
//
//    @Test
//    public void testDeleteAndSaveImageWithInvalidFile()
//            throws IOException, UnsupportedOperationException {
//
//        copyTestImagesToImages();
//
//        try {
//            ImageDataIO.deleteAndSaveImage(null, this.accountName);
//
//            throw new UnsupportedOperationException(
//                    "Test failed, deleteAndSaveImage did not throw IOException"
//            );
//        } catch (IOException e) {
//            assertNotNull(e);
//            assertEquals("No file specified", e.getMessage());
//        }
//    }
//
//    @Test
//    public void testDeleteImage() throws IOException {
//        copyTestImagesToImages();
//
//        ImageDataIO.deleteImage(accountName);
//        assertFalse(this.testImageJpg.exists());
//        assertFalse(this.testImagePng.exists());
//    }
//
//    @Test
//    public void testGetImagePathWithValidName() {
//        File imagePath = ImageDataIO.getImagePath(this.imageNameJpg);
//
//        assertNotNull(imagePath);
//        assertEquals(this.imageNameJpg, imagePath.getName());
//        assertTrue(imagePath.getPath().contains(this.imagePath));
//    }
//
//    @Test
//    public void testGetImagePathWithInvalidName() {
//        File imagePath = ImageDataIO.getImagePath(null);
//
//        assertNull(imagePath);
//    }

}
