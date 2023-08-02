package com.ezadmin.common.utils;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.InputStream;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class IoUtils {
    public static final String FILE_SEPARATOR = "/";

    public static void streamToFile(URL url, Path path) throws IOException {
        try {
            if (Files.exists(path)) {
                return;
            }
            InputStream input = url.openStream();
            Files.createFile(path);
            Files.copy(input, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String streamToString(InputStream inputStream) {
        try {
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            String str = result.toString(StandardCharsets.UTF_8.name());
            return str;
        } catch (Exception e) {
            return "";
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
