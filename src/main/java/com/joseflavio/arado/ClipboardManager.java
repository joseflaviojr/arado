package com.joseflavio.arado;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ClipboardManager {

    private static final FilePattern[] FILE_PATTERNS = {
        new FilePattern("file:(//)?(.+)", 2),
        new FilePattern("/.+/.+", 0),
        new FilePattern("[A-Za-z]:\\\\.+", 0)
    };

    private static final String ERROR_UNSUPPORTED_CONTENT_TYPE = "Unsupported clipboard content type";
    private static final String ERROR_CLIPBOARD_IS_EMPTY = "The clipboard is empty";

    private final Clipboard clipboard;

    public ClipboardManager(Clipboard clipboard) {

        this.clipboard = clipboard;
    }

    public ClipboardManager() {

        this(Toolkit.getDefaultToolkit().getSystemClipboard());
    }

    public Object getData() throws IOException {

        return extractData(getTransferable());
    }

    private Transferable getTransferable() throws IOException {

        Transferable transferable = clipboard.getContents(null);
        if (transferable == null) {
            throw new IOException(ERROR_CLIPBOARD_IS_EMPTY);
        }
        return transferable;
    }

    private Object extractData(Transferable transferable) throws IOException {

        try {
            if (transferable.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                return transferable.getTransferData(DataFlavor.imageFlavor);

            } else if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                return extractDataFromString(transferable);
            }
        } catch (UnsupportedFlavorException e) {
            throw new IOException(ERROR_UNSUPPORTED_CONTENT_TYPE);
        }
        throw new IOException(ERROR_UNSUPPORTED_CONTENT_TYPE);
    }

    private static Object extractDataFromString(Transferable transferable)
        throws UnsupportedFlavorException, IOException {

        var string = (String) transferable.getTransferData(DataFlavor.stringFlavor);

        for (var filePattern : FILE_PATTERNS) {
            var file = filePattern.getFile(string);
            if (file != null) {
                return file;
            }
        }

        return string;
    }

    private static class FilePattern {

        private final Pattern pattern;
        private final int matchGroup;

        public FilePattern(String regex, int matchGroup) {

            this.pattern = Pattern.compile(regex);
            this.matchGroup = matchGroup;
        }

        public File getFile(String address) {

            var matcher = pattern.matcher(address);
            return matcher.matches() ? getFile(matcher) : null;
        }

        private File getFile(Matcher matcher) {

            var file = new File(matcher.group(matchGroup));
            return file.exists() && file.isFile() ? file : null;
        }

    }

}
