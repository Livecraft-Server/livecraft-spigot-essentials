package com.gmail.mediusecho.livecraft_spigot_essentials.util;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class ResourceUtil {

    /**
     * Removes the fileNames extension
     * @param fileName
     * @return
     */
    @NotNull
    @Contract(pure = true)
    public static String removeExtension (@NotNull String fileName) {
        return fileName.replaceFirst("[.][^.]+$", "");
    }

    /**
     * Copies the file provided
     * @param in to copy
     * @param file to copy to
     * @throws IOException
     */
    public static void copyFile (@NotNull InputStream in, @NotNull File file) throws IOException
    {
        OutputStream out = new FileOutputStream(file);
        byte[] buf = new byte[1024];

        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }

        out.close();
        in.close();
    }

}
