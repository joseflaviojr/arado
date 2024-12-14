package com.joseflavio.arado;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.mockito.stubbing.Answer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;
import static java.io.File.createTempFile;
import static java.nio.file.Files.readString;
import static java.nio.file.Files.writeString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DataTransferTest {

    private static final File TEMPORARY_DIRECTORY = createTemporaryDirectory();
    private static final String TEXT_CONTENT = "test/text\ncontent";
    private static final String SUGGESTED_TEXT_FILE_NAME = "test_text content.txt";
    private static final String TEXT_FILE_NAME = "test.txt";
    private static final BufferedImage IMAGE_CONTENT = new BufferedImage(10, 10, TYPE_INT_RGB);
    private static final String IMAGE_FILE_NAME = "test.png";

    private final Configuration configuration;
    private final ClipboardManager clipboardManager;
    private final FileChooser fileChooser;
    private final DataTransfer dataTransfer;

    DataTransferTest() {

        this.configuration = mock(Configuration.class);
        this.clipboardManager = mock(ClipboardManager.class);
        this.fileChooser = mock(FileChooser.class);
        this.dataTransfer = new DataTransfer(configuration, clipboardManager, fileChooser);
    }

    @AfterAll
    static void afterAll() {

        deleteTemporaryDirectory();
    }

    @Test
    void givenTextContentWhenSaveClipboardContentAsFileThenSaveTxtFile() throws IOException {

        mockDependencies(TEXT_CONTENT, TEXT_FILE_NAME);

        var file = dataTransfer.saveClipboardContentAsFile();

        assertEquals(TEXT_CONTENT, readString(file.toPath()));
        assertEquals(TEXT_FILE_NAME, file.getName());
    }

    @Test
    void givenImageContentWhenSaveClipboardContentAsFileThenSavePngFile() throws IOException {

        mockDependencies(IMAGE_CONTENT, IMAGE_FILE_NAME);

        var file = dataTransfer.saveClipboardContentAsFile();

        assertTrue(equals(IMAGE_CONTENT, ImageIO.read(file)));
        assertEquals(IMAGE_FILE_NAME, file.getName());
    }

    @Test
    void givenFileReferenceWhenSaveClipboardContentAsFileThenSaveFileCopy() throws IOException {

        var file = createTempFile("temp", null);
        writeString(file.toPath(), TEXT_CONTENT);
        file.deleteOnExit();

        mockDependencies(file);

        var fileCopy = dataTransfer.saveClipboardContentAsFile();

        assertEquals(TEXT_CONTENT, readString(fileCopy.toPath()));
        assertTrue(fileCopy.getName().contains(file.getName()));
    }

    @Test
    void givenAnyContentWhenSaveClipboardContentAsFileThenCheckSuggestedFileName() throws IOException {

        mockDependencies(TEXT_CONTENT);

        var file1 = dataTransfer.saveClipboardContentAsFile();
        var file2 = dataTransfer.saveClipboardContentAsFile();

        assertEquals("0001 - " + SUGGESTED_TEXT_FILE_NAME, file1.getName());
        assertEquals("0002 - " + SUGGESTED_TEXT_FILE_NAME, file2.getName());
    }

    @Test
    void givenIoExceptionWhenSaveClipboardContentAsFileThenThrowIoException() throws IOException {

        when(clipboardManager.getData()).thenThrow(IOException.class);
        assertThrows(IOException.class, dataTransfer::saveClipboardContentAsFile);
    }

    @Test
    void givenRuntimeExceptionWhenSaveClipboardContentAsFileThenThrowIoException() throws IOException {

        when(clipboardManager.getData()).thenThrow(RuntimeException.class);
        assertThrows(IOException.class, dataTransfer::saveClipboardContentAsFile);
    }

    private void mockDependencies(Object clipboardContent) throws IOException {

        mockDependencies(clipboardContent, call -> call.getArgument(0, File.class));
    }

    private void mockDependencies(Object clipboardContent, String chosenFileName) throws IOException {

        mockDependencies(clipboardContent, call -> new File(TEMPORARY_DIRECTORY, chosenFileName));
    }

    private void mockDependencies(Object clipboardContent, Answer<File> fileAnswer) throws IOException {

        when(clipboardManager.getData()).thenReturn(clipboardContent);
        when(configuration.getLastDirectory()).thenReturn(TEMPORARY_DIRECTORY);
        doNothing().when(configuration).setLastDirectory(any());
        when(fileChooser.chooseFile(any(), any())).thenAnswer(fileAnswer);
    }

    public static boolean equals(BufferedImage img1, BufferedImage img2) {

        if (img1.getWidth() != img2.getWidth() || img1.getHeight() != img2.getHeight()) {
            return false;
        }

        for (int x = 0; x < img1.getWidth(); x++) {
            for (int y = 0; y < img1.getHeight(); y++) {
                if (img1.getRGB(x, y) != img2.getRGB(x, y)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static File createTemporaryDirectory() {

        var systemTemporaryDirectory = System.getProperty("java.io.tmpdir");
        var directoryName = "TestTempDir-" + Math.random();
        var temporaryDirectory = new File(systemTemporaryDirectory, directoryName);
        temporaryDirectory.mkdir();
        return temporaryDirectory;
    }

    private static void deleteTemporaryDirectory() {

        for (var file : TEMPORARY_DIRECTORY.listFiles()) {
            file.delete();
        }
        TEMPORARY_DIRECTORY.delete();
    }

}
