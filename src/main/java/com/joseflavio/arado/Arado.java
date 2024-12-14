package com.joseflavio.arado;

import javax.swing.*;
import java.io.IOException;

import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

public class Arado {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try {

                var configuration = new Configuration();
                var clipboardManager = new ClipboardManager();
                var fileChooser = new SwingFileChooser();
                var dataTransfer = new DataTransfer(configuration, clipboardManager, fileChooser);

                dataTransfer.saveClipboardContentAsFile();
                
            } catch (IOException e) {
                handleError(e);
            }
        });
    }

    private static void handleError(IOException e) {

        showMessageDialog(null, e.getMessage(), "Error", ERROR_MESSAGE);
        System.exit(1);
    }

}
