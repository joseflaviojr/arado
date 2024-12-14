package com.joseflavio.arado;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static com.joseflavio.arado.Configuration.CONFIGURATION_FILE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ConfigurationTest {

    private static final File USER_DIRECTORY = new File(System.getProperty("user.home"));
    private static final File TEMPORARY_DIRECTORY = new File(System.getProperty("java.io.tmpdir"));

    @AfterAll
    static void afterAll() {

        deleteConfigurationFile();
    }

    @Test
    void givenFirstCallWhenGetLastDirectoryThenReturnUserDirectory() throws IOException {

        var configuration = createConfiguration();
        assertEquals(USER_DIRECTORY, configuration.getLastDirectory());
    }

    @Test
    void givenRecentCallToSetLastDirectoryWhenGetLastDirectoryThenReturnPreviouslyDefinedDirectory() throws IOException {

        var configuration = createConfiguration();
        configuration.setLastDirectory(TEMPORARY_DIRECTORY);
        assertEquals(TEMPORARY_DIRECTORY, configuration.getLastDirectory());
    }

    @Test
    void givenConfigurationReloadedWhenGetLastDirectoryThenReturnPreviouslySavedDirectory() throws IOException {

        var configuration = createConfiguration();
        configuration.setLastDirectory(TEMPORARY_DIRECTORY);
        configuration = reloadConfiguration();
        assertEquals(TEMPORARY_DIRECTORY, configuration.getLastDirectory());
    }

    @Test
    void givenLastDirectoryDefinedWithFileAddressWhenGetLastDirectoryThenReturnUserDirectory() throws IOException {

        var configuration = createConfiguration();
        var tempFile = File.createTempFile("tempfile", null);
        tempFile.deleteOnExit();
        configuration.setLastDirectory(tempFile);
        assertEquals(USER_DIRECTORY, configuration.getLastDirectory());
    }

    @Test
    void givenNonExistentDirectoryWhenGetLastDirectoryThenReturnUserDirectory() throws IOException {

        var configuration = createConfiguration();
        configuration.setLastDirectory(new File(TEMPORARY_DIRECTORY, "abcdefghij.z"));
        assertEquals(USER_DIRECTORY, configuration.getLastDirectory());
    }

    private static void deleteConfigurationFile() {

        new File(TEMPORARY_DIRECTORY, CONFIGURATION_FILE_NAME).delete();
    }

    private Configuration reloadConfiguration() throws IOException {

        return new Configuration(TEMPORARY_DIRECTORY);
    }

    private Configuration createConfiguration() throws IOException {

        deleteConfigurationFile();
        return reloadConfiguration();
    }

}
