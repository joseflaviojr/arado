package com.joseflavio.arado.strategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

public class FileStrategy implements Strategy {

    @Override
    public String suggestName(Object data) {

        return getNameWithoutExtension(((File) data).getName());
    }

    @Override
    public String suggestFileExtension(Object data) {
        
        return getExtension(((File) data).getName());
    }

    @Override
    public void saveToFile(Object data, File destination) throws IOException {

        Files.copy(((File) data).toPath(), destination.toPath(), REPLACE_EXISTING);
    }

    private static String getNameWithoutExtension(String fileName) {

        int lastDot = fileName.lastIndexOf('.');
        return lastDot == - 1 ? fileName : fileName.substring(0, lastDot);
    }

    private static String getExtension(String fileName) {

        int lastDot = fileName.lastIndexOf('.');
        boolean noDot = lastDot == - 1 || (lastDot + 1) == fileName.length();
        return noDot ? "" : fileName.substring(lastDot + 1);
    }

}
