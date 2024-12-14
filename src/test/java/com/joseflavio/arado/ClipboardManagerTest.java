package com.joseflavio.arado;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static java.io.File.createTempFile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ClipboardManagerTest {

    private final ClipboardManager clipboardManager;
    private final Clipboard clipboard;

    ClipboardManagerTest() {

        clipboard = mock(Clipboard.class);
        clipboardManager = new ClipboardManager(clipboard);
    }

    @Test
    void givenEmptyClipboardWhenGetDataThenThrowException() {

        when(clipboard.getContents(any())).thenReturn(null);
        assertThrows(IOException.class, clipboardManager::getData);
    }

    @Test
    void givenImageContentInClipboardWhenGetDataThenReturnBufferedImage()
        throws IOException, UnsupportedFlavorException {

        var transferable = mock(Transferable.class);
        var image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);

        when(transferable.isDataFlavorSupported(DataFlavor.imageFlavor)).thenReturn(true);
        when(transferable.getTransferData(DataFlavor.imageFlavor)).thenReturn(image);
        when(clipboard.getContents(any())).thenReturn(transferable);

        var resultData = clipboardManager.getData();

        assertSame(image, resultData);
    }

    @Test
    void givenTextContentInClipboardWhenGetDataThenReturnString()
        throws IOException, UnsupportedFlavorException {

        var text = "text";
        testGetDataWithTextOnClipboard(text, text);
    }

    @Test
    void givenExistingFileAddressInClipboardWhenGetDataThenReturnFile()
        throws IOException, UnsupportedFlavorException {

        var file = createTempFile("temp", null);
        file.deleteOnExit();
        testGetDataWithTextOnClipboard(file.toURI().toString(), file);
        testGetDataWithTextOnClipboard(file.getAbsolutePath(), file);
    }

    @ParameterizedTest
    @ValueSource(strings = {
        "file:///non-existent-file",
        "/non-existent-file",
        "/directory/non-existent-file",
        "C:\\non-existent-file",
        "C:\\directory\\non-existent-file"})
    void givenNonExistentFileAddressInClipboardWhenGetDataThenReturnRawString(String fileAddress)
        throws IOException, UnsupportedFlavorException {

        testGetDataWithTextOnClipboard(fileAddress, fileAddress);
    }

    @Test
    void givenUnsupportedFlavorExceptionWhenGetDataThenThrowIoException()
        throws IOException, UnsupportedFlavorException {

        var transferable = mock(Transferable.class);

        when(transferable.isDataFlavorSupported(DataFlavor.imageFlavor)).thenReturn(true);
        when(transferable.getTransferData(DataFlavor.imageFlavor)).thenThrow(UnsupportedFlavorException.class);
        when(clipboard.getContents(any())).thenReturn(transferable);

        assertThrows(IOException.class, clipboardManager::getData);
    }

    private void testGetDataWithTextOnClipboard(String textOnClipboard, Object expectedResult)
        throws UnsupportedFlavorException, IOException {

        var transferable = mock(Transferable.class);

        when(transferable.isDataFlavorSupported(DataFlavor.imageFlavor)).thenReturn(false);
        when(transferable.isDataFlavorSupported(DataFlavor.stringFlavor)).thenReturn(true);
        when(transferable.getTransferData(DataFlavor.stringFlavor)).thenReturn(textOnClipboard);

        when(clipboard.getContents(any())).thenReturn(transferable);

        var result = clipboardManager.getData();

        assertEquals(expectedResult, result);
    }

}
