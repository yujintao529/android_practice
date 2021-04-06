package com.demon.yu.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

public class BaseFileUtils {

    protected static final String TAG = "FileUtils";

    public static final int GLOBLE_BUFFER_SIZE = 512 * 1024;

    public static boolean closeStream(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
                return true;
            }
        } catch (IOException e) {
        }

        return false;
    }

    public static InputStream makeInputBuffered(InputStream input) {

        if ((input instanceof BufferedInputStream)) {
            return input;
        }

        return new BufferedInputStream(input, GLOBLE_BUFFER_SIZE);
    }

    public static OutputStream makeOutputBuffered(OutputStream output) {
        if ((output instanceof BufferedOutputStream)) {
            return output;
        }

        return new BufferedOutputStream(output, GLOBLE_BUFFER_SIZE);
    }

    public static FileInputStream getFileInputStream(File file) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
        }

        return fis;
    }

    public static FileInputStream getFileInputStream(String path) {
        return getFileInputStream(new File(path));
    }

    public static boolean doesExisted(File file) {
        return (file != null) && (file.exists());
    }

    public static void copyWithoutOutputClosing(InputStream input, OutputStream output) throws IOException {
        try {
            byte[] buffer = new byte[GLOBLE_BUFFER_SIZE];
            int temp = -1;
            input = makeInputBuffered(input);
            output = makeOutputBuffered(output);
            while ((temp = input.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
                output.flush();
            }
            output.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            closeStream(input);
        }
    }

    public static byte[] readInputStream(InputStream is) {
        if (is != null) {
            ByteArrayOutputStream baos = null;
            try {
                baos = new ByteArrayOutputStream(GLOBLE_BUFFER_SIZE);
                copyWithoutOutputClosing(is, baos);
                byte[] res = baos.toByteArray();
                byte[] arrayOfByte1 = res;
                return arrayOfByte1;
            } catch (IOException e) {
            } finally {
                closeStream(baos);
            }
        }

        return null;
    }

    public static byte[] readFile(File file) {
        return readInputStream(getFileInputStream(file));
    }

    public static byte[] readFile(String path) throws FileNotFoundException {
        File f = new File(path);
        if ((doesExisted(f)) && (f.isFile()))
            return readFile(f);
        throw new FileNotFoundException(f.getAbsolutePath());
    }

    public static String readFileForString(File file, String charset) throws UnsupportedEncodingException {
        return new String(readFile(file), charset);
    }

    public static String readFileForString(String path, String charset) throws UnsupportedEncodingException, FileNotFoundException {
        return new String(readFile(path), charset);
    }

    public static String readFileForString(File file) {
        return new String(readFile(file));
    }

    public static String readFileForString(String path) throws FileNotFoundException {
        return new String(readFile(path));
    }

    public static long getFileSize(File file) {
        long length = 0;
        if (file != null && file.exists()) {
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    if (f != null && f.exists() && f.canRead()) {
                        length += f.length();
                    }
                }
            } else if (file.isFile() && file.canRead()) {
                length = file.length();
            }
        }
        return length;
    }
}