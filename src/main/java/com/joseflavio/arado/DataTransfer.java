package com.joseflavio.arado;

import com.joseflavio.arado.strategy.FileStrategy;
import com.joseflavio.arado.strategy.ImageStrategy;
import com.joseflavio.arado.strategy.Strategy;
import com.joseflavio.arado.strategy.TextStrategy;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.util.Comparator.naturalOrder;

public class DataTransfer {

    private static final Pattern FILE_NUMBER_PATTERN = Pattern.compile("^(\\d{1,4})[ ._-].*");
    private static final String FILE_NUMBER_FORMAT = "%04d";

    private final Configuration configuration;
    private final ClipboardManager clipboardManager;
    private final FileChooser fileChooser;
    private final Map<Class<?>,Class<? extends Strategy>> strategies;

    public DataTransfer(
        Configuration configuration, ClipboardManager clipboardManager, FileChooser fileChooser) {

        this.configuration = configuration;
        this.clipboardManager = clipboardManager;
        this.fileChooser = fileChooser;
        this.strategies = new HashMap<>();
        this.defineAvailableStrategies();
    }

    public File saveClipboardContentAsFile() throws IOException {

        try {

            Object data = clipboardManager.getData();
            Strategy strategy = getStrategy(data);

            File lastDirectory = configuration.getLastDirectory();
            File suggestedFile = suggestFile(lastDirectory, data, strategy);
            File destinationFile = fileChooser.chooseFile(suggestedFile, "Choose destination file");

            if (destinationFile != null) {
                saveLastDirectory(destinationFile, lastDirectory);
                strategy.saveToFile(data, destinationFile);
                return destinationFile;
            }

            return null;

        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private void defineAvailableStrategies() {

        strategies.put(String.class, TextStrategy.class);
        strategies.put(BufferedImage.class, ImageStrategy.class);
        strategies.put(File.class, FileStrategy.class);
    }

    private Strategy getStrategy(Object data)
        throws InstantiationException, IllegalAccessException,
        InvocationTargetException, NoSuchMethodException {

        var strategyClass = strategies.get(data.getClass());
        return strategyClass.getConstructor().newInstance();
    }

    private File suggestFile(File directory, Object data, Strategy strategy) throws IOException {

        String name = strategy.suggestName(data);
        String extension = strategy.suggestFileExtension(data);

        if (FILE_NUMBER_PATTERN.matcher(name).matches()) {
            return new File(directory, name + "." + extension);
        }

        int number;

        try (var pathStream = Files.walk(directory.toPath(), 1, FOLLOW_LINKS)) {
            number = pathStream
                .map(Path::toFile)
                .filter(File::isFile)
                .map(file -> FILE_NUMBER_PATTERN.matcher(file.getName()))
                .filter(Matcher::find)
                .map(matcher -> parseInt(matcher.group(1)))
                .max(naturalOrder())
                .map(Math::incrementExact)
                .orElse(1);
        }

        String separator = name.isEmpty() ? "" : " - ";
        String format = FILE_NUMBER_FORMAT + "%s%s.%s";
        String fileName = String.format(format, number, separator, name, extension);
        return new File(directory, fileName);
    }

    private void saveLastDirectory(File destinationFile, File lastDirectory) throws IOException {

        File parentDirectoty = destinationFile.getParentFile();
        if (! parentDirectoty.equals(lastDirectory)) {
            configuration.setLastDirectory(parentDirectoty);
        }
    }

}
