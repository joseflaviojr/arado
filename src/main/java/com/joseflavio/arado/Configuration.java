package com.joseflavio.arado;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {

    static final String CONFIGURATION_FILE_NAME = "arado.properties";

    private static final String ARADO_DESCRIPTION = "Arado Clipboard Utility";
    private static final String LAST_DIRECTORY_KEY = "last_directory";
    private static final String USER_HOME = "user.home";

    private final File baseDirectory;

    private final Properties properties;

    public Configuration(File baseDirectory) throws IOException {

        this.baseDirectory = baseDirectory;
        this.properties = loadProperties();
    }

    public Configuration() throws IOException {

        this(new File(System.getProperty(USER_HOME)));
    }

    private File getPropertiesFile() {

        return new File(baseDirectory, CONFIGURATION_FILE_NAME);
    }

    private Properties loadProperties() throws IOException {

        var propertiesFile = getPropertiesFile();
        if (! propertiesFile.exists() && ! propertiesFile.createNewFile()) {
            throw new IOException("Could not create configuration file");
        }

        var properties = new Properties();
        try (var inputStream = new FileInputStream(propertiesFile)) {
            properties.load(inputStream);
        }
        return properties;
    }

    public File getLastDirectory() throws IOException {

        String lastDirectoryPath = properties.getProperty(LAST_DIRECTORY_KEY);
        if (lastDirectoryPath != null) {
            File lastDirectory = new File(lastDirectoryPath);
            if (lastDirectory.exists() && lastDirectory.isDirectory()) {
                return lastDirectory;
            }
        }
        return new File(System.getProperty(USER_HOME));
    }

    public void setLastDirectory(File directory) throws IOException {

        properties.setProperty(LAST_DIRECTORY_KEY, directory.getAbsolutePath());
        try (var outputStream = new FileOutputStream(getPropertiesFile())) {
            properties.store(outputStream, ARADO_DESCRIPTION);
        }
    }

}
