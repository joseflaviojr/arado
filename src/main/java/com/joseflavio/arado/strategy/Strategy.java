package com.joseflavio.arado.strategy;

import java.io.File;
import java.io.IOException;

public interface Strategy {

    String suggestName(Object data);

    String suggestFileExtension(Object data);

    void saveToFile(Object data, File destination) throws IOException;

}
