package com.joseflavio.arado.strategy;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageStrategy implements Strategy {

    private static final String PNG_EXTENSION = "png";

    @Override
    public String suggestName(Object data) {

        return "";
    }

    @Override
    public String suggestFileExtension(Object data) {

        return PNG_EXTENSION;
    }

    @Override
    public void saveToFile(Object data, File destination) throws IOException {

        ImageIO.write((BufferedImage) data, PNG_EXTENSION, destination);
    }

}
