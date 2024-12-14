package com.joseflavio.arado.strategy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.charset.StandardCharsets.UTF_8;

public class TextStrategy implements Strategy {

    @Override
    public String suggestName(Object data) {

        return cleanAndTruncate((String) data);
    }

    @Override
    public String suggestFileExtension(Object data) {

        return "txt";
    }

    @Override
    public void saveToFile(Object data, File destination) throws IOException {

        Files.writeString(destination.toPath(), (String) data, UTF_8);
    }

    private static String cleanAndTruncate(String text) {

        var result = text.replace("\r", "")
            .replaceAll("[\n\t]", " ")
            .replaceAll("[:/\\\\]", "_")
            .replaceAll(" {2,}", " ");

        return result.length() > 100 ? result.substring(0, 100) : result;
    }

}
