package com.joseflavio.arado;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static javax.swing.JFileChooser.APPROVE_OPTION;

public class SwingFileChooser implements FileChooser {

    @Override
    public File chooseFile(File suggestedFile, String title) {

        var fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(title);
        fileChooser.setSelectedFile(suggestedFile);
        
        var screenHeight = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight();
        fileChooser.setPreferredSize(new Dimension(800, screenHeight));

        var action = fileChooser.showSaveDialog(null);
        return action == APPROVE_OPTION ? fileChooser.getSelectedFile() : null;
    }

}
