package com.demon.yu.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class FileUtils extends BaseFileUtils {
    public static final String ZIP_SUFIIX = ".zip";

    public static String printSDCardRoot() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 返回fresco能显示的路径
     *
     * @param path
     * @return
     */
    public static String getUriFromFile(String path) {
        return "file://" + path;
    }


    public static boolean isWebUri(String uri) {
        return TextUtils.isEmpty(uri) ? false : uri.startsWith("http");
    }

    public static File getFileFromUri(Uri uri) {

        if ((uri.getScheme().equals("file")) && (!TextUtils.isEmpty(uri.getPath()))) {
            return new File(uri.getPath());
        }

        return null;
    }

    public static boolean hasSDCardMounted() {
        return Environment.getExternalStorageState().equals("mounted");
    }

    // 检测文件是否存在
    public static boolean isFileExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.isFile() && file.exists() && file.length() > 0;
    }

    // 检测目录是否存在
    public static boolean isDirectoryExist(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        return file.isDirectory() && file.exists();
    }

    // 检测目录是否存在
    public static boolean isDirectoryNotEmpty(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (file.isDirectory() && file.exists()) {
            String[] files = file.list();
            return files != null && files.length > 0;
        }

        return false;
    }

    // 返回目录下所有文件
    public static String[] getFilesFromDirectory(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return null;
        }
        File file = new File(filePath);
        if (file.isDirectory() && file.exists()) {
            return file.list();
        }

        return null;
    }

    /**
     * @param filePath   filePath
     * @param maxStorage maxStorage  MB
     */
    public static boolean isDirLarge(String filePath, int maxStorage) {
        if (!isDirectoryNotEmpty(filePath)) {
            return false;
        }
        File file = new File(filePath);
        long size = 0;
        File[] children = file.listFiles();
        for (File f : children) {
            size += f.length();
        }
        return size / (1024 * 1024) > maxStorage;
    }

    /**
     * 以字符的方式读取文件,主要是log里用的，所以oom和异常不抛出，静默处理
     *
     * @param path
     * @return
     */
    public static String readStringFromFile(String path) {
        if (!isFileExist(path)) {
            return "";
        }
        File file = new File(path);
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder((int) file.length());
        try {
            reader = new BufferedReader(new FileReader(file));
            String temp = null;
            while ((temp = reader.readLine()) != null) {
                content.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }

        }
        return content.toString();
    }

    // 如果文件大写超过maxSize，则不写入
    public static void writeFile(String filePath, String content, boolean isAppend, long maxSize) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File file = new File(filePath);
        if (file.exists() && file.length() >= maxSize) {
            return;
        }
        writeFile(filePath, content, isAppend);
    }

    public synchronized static void writeFile(String filePath, String content, boolean isAppend) {
        if (filePath == null) {
            return;
        }
        if (content == null) {
            return;
        }
        File file = new File(filePath);
        FileWriter fileWriter = null;
        try {
            if (!file.exists()) {
                makeSureFileExist(file);
            }
            fileWriter = new FileWriter(file, isAppend);
            fileWriter.write(content);
            fileWriter.flush();
        } catch (IOException e) {
        } finally {
            FileUtils.closeStream(fileWriter);
        }
    }

    public static void writeFile(String filePath, byte[] bytes) {
        if (filePath == null) {
            return;
        }
        if (bytes == null || bytes.length == 0) {
            return;
        }
        File file = new File(filePath);
        FileOutputStream fileWriter = null;
        try {
            if (!file.exists()) {
                makeSureFileExist(file);
            }
            fileWriter = new FileOutputStream(file);
            fileWriter.write(bytes);
            fileWriter.flush();
        } catch (IOException e) {
        } finally {
            FileUtils.closeStream(fileWriter);
        }
    }

    // Convert Uri to absolute path
    public static String getAbsolutePathFromUri(String uriString) {

        if (TextUtils.isEmpty(uriString)) {
            return null;
        }

        Uri uri = Uri.parse(uriString);

        if (uri == null) {
            return null;
        }

        return uri.getPath();
    }

    private static final int DEFAULT_DELETE_LINITE = 5;

    public static void copy(String pathIn, String pathOut) throws IOException {
        copy(new File(pathIn), new File(pathOut));
    }

    public static void copy(File in, File out) throws IOException {
        if (in == null || out == null) {
            return;
        }

        makeSureParentExist(out);
        copy(new FileInputStream(in), new FileOutputStream(out));
    }

    public static void makeSureParentExist(File file) {
        File parent = file.getParentFile();
        if ((parent != null) && (!parent.exists())) {
            mkdirs(parent);
        }
    }

    public static void makeSureParentExist(String filepath) {
        makeSureParentExist(new File(filepath));
    }

    /**
     * 确保某文件或文件夹的父文件夹存在
     *
     * @param file
     */
    private static boolean makesureParentDirExist(File file) {

        final File parent = file.getParentFile();
        if (parent == null || parent.exists()) {
            return true;
        }
        return mkdirs(parent);
    }

    /**
     * 验证文件夹创建操作成功有否
     *
     * @param dir
     */
    private static boolean mkdirs(File dir) {
        if (dir == null)
            return false;
        return dir.mkdirs();
    }

    public static void copy(InputStream input, OutputStream output) throws IOException {
        try {
            byte[] buffer = new byte[BaseFileUtils.GLOBLE_BUFFER_SIZE];
            int temp = -1;
            input = BaseFileUtils.makeInputBuffered(input);
            output = BaseFileUtils.makeOutputBuffered(output);
            while ((temp = input.read(buffer)) != -1) {
                output.write(buffer, 0, temp);
                output.flush();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            FileUtils.closeStream(input);
            FileUtils.closeStream(output);
        }
    }

    public static Object loadObject(String filepath) throws IOException, ClassNotFoundException {
        if (BaseFileUtils.doesExisted(new File(filepath)))
            return loadObject(new FileInputStream(filepath));
        return null;
    }

    public static Object loadObject(InputStream input) throws IOException, ClassNotFoundException {
        ObjectInputStream ois = null;
        try {
            Object obj = null;
            ois = new ObjectInputStream(input = BaseFileUtils.makeInputBuffered(input));
            obj = ois.readObject();
            Object localObject2 = obj;
            return localObject2;
        } catch (IOException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw e;
        } finally {
            FileUtils.closeStream(ois);
            FileUtils.closeStream(input);
        }
    }

    public static void saveObject(Object obj, String filepath) throws IOException {
        saveObject(obj, new FileOutputStream(filepath));
    }

    public static void saveObject(Object obj, OutputStream output) throws IOException {
        ObjectOutputStream oos = null;
        try {
            oos = new ObjectOutputStream(BaseFileUtils.makeOutputBuffered(output));
            oos.writeObject(obj);
        } catch (IOException e) {
            throw e;
        } finally {
            FileUtils.closeStream(oos);
        }
    }

    public static void copyAssetToStorage(Context context, String src, String desPath, String des) {
        if (TextUtils.isEmpty(src) || TextUtils.isEmpty(des) || FileUtils.isFileExist(des)) {
            return;
        }

        InputStream is = null;
        FileOutputStream fos = null;
        try {
            is = context.getAssets().open(src);

            File tmp = new File(desPath);
            tmp.mkdirs();
            tmp = new File(des);
            tmp.createNewFile();

            fos = new FileOutputStream(tmp);
            byte[] buffer = new byte[4 * 1024];
            int byteCount = 0;
            while ((byteCount = is.read(buffer)) != -1) {//循环从输入流读取 buffer字节
                fos.write(buffer, 0, byteCount);//将读取的输入流写入到输出流
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.flush();//刷新缓冲区
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void unZipFile(File source, String destDir) throws IOException {
        if (isDirectoryNotEmpty(destDir)) {
            return;
        }
        File outFile = new File(destDir);
        if (!outFile.exists()) {
            outFile.mkdirs();
        }
        ZipFile zipFile = new ZipFile(source);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
            final String name = entry.getName();
            File destination = new File(outFile, name);
            if (entry.isDirectory()) {
                destination.mkdirs();
            } else if (name != null && name.contains("../")) {
                // 防止zip压缩文件的漏洞
                continue;
            } else {
                destination.createNewFile();
                FileOutputStream outStream = new FileOutputStream(destination);
                try {
                    copy(zipFile.getInputStream(entry), outStream);
                } catch (Exception e) {
                }
                outStream.close();
            }
        }
        zipFile.close();
    }

    public static final void unZipFile(File zip, File extractTo) throws IOException {
        ZipFile archive = new ZipFile(zip);
        Enumeration e = archive.entries();
        while (e.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) e.nextElement();
            File file = new File(extractTo, entry.getName());
            if (file.getAbsolutePath().contains("_MACOSX")) {
                continue;
            }
            if (entry.isDirectory() && !file.exists()) {
                file.mkdirs();
            } else {
                if (!file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }

                InputStream in = archive.getInputStream(entry);
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(file));

                byte[] buffer = new byte[8192];
                int read;

                while (-1 != (read = in.read(buffer))) {
                    out.write(buffer, 0, read);
                }
                in.close();
                out.close();
            }
        }
        archive.close();
    }

    public static FileOutputStream getEmptyFileOutputStream(String path) throws FileNotFoundException {
        return getEmptyFileOutputStream(new File(path));
    }

    public static FileOutputStream getEmptyFileOutputStream(File file) throws FileNotFoundException {

        if (!file.exists()) {
            makeSureParentExist(file);
        } else {
            delete(file);
        }
        createNewFile(file);
        return new FileOutputStream(file);
    }

    public static void deleteFiles(File file) {
        if ((file.exists()) && (file.isDirectory())) {
            File[] childrenFile = file.listFiles();
            if (childrenFile != null) {
                for (File f : childrenFile) {
                    if (f.isFile()) {
                        delete(f);
                    } else if (f.isDirectory()) {
                        deleteFiles(f);
                    }
                }
            }
            delete(file);
        } else if ((file.exists()) && (file.isFile())) {
            delete(file);
        }
    }

    public static void clearDirectory(String path) {
        if (TextUtils.isEmpty(path)) {
            return;
        }
        File file = new File(path);
        if ((file.exists()) && (file.isDirectory())) {
            File[] childrenFile = file.listFiles();
            if (childrenFile != null) {
                for (File f : childrenFile) {
                    if (f.isFile()) {
                        delete(f);
                    } else if (f.isDirectory()) {
                        deleteFiles(f);
                    }
                }
            }
        } else if ((file.exists()) && (file.isFile())) {
            delete(file);
        }
    }


    public static void deleteFiles(String path) {
        deleteFiles(new File(path));
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

    public static long getFileSize(String filepath) {
        return getFileSize(new File(filepath));
    }

    public static boolean doesExisted(String filepath) {
        if (TextUtils.isEmpty(filepath))
            return false;
        return BaseFileUtils.doesExisted(new File(filepath));
    }

    public static void renameLowercase(File file) {
        if ((BaseFileUtils.doesExisted(file)) && (file.isFile())) {
            String parent = file.getParent();
            parent = parent == null ? "" : parent;
            String newPath = parent + "/" + file.getName().toLowerCase(Locale.US);
            if (!newPath.equals(file.getAbsolutePath()))
                renameTo(file, new File(newPath));
        }
    }

    public static void delete(File f) {
        if ((f != null) && (f.exists())) {
            f.delete();
        }
    }

    public static void delete(String path) {
        File f = new File(path);
        if ((f != null) && (f.exists()))
            f.delete();
    }


    public static void renameTo(String srcPath, String dstPath) {
        renameTo(new File(srcPath), new File(dstPath));
    }

    public static void renameTo(File src, File dest) {
        if ((src != null) && (dest != null) && (src.exists())) {
            src.renameTo(dest);
        }
    }

    public static boolean createNewFile(File file) {
        makeSureParentExist(file);
        if (file.exists())
            delete(file);
        try {
            return file.createNewFile();
        } catch (IOException e) {
        }

        return false;
    }

    public static FileOutputStream getFileOutputStream(File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
        }
        return fos;
    }

    /**
     * 如果doesExisted(filepath_)为true且是文件类型则删除文件，并返回true； 否则什么也不做并且返回false。
     * 如果删除失败，不会重试。
     *
     * @param file
     * @return
     */
    public static boolean deleteDependon(File file) {
        return FileUtils.deleteDependon(file, 0);
    }

    /**
     * 如果doesExisted(file_)为true则删除文件，并返回true； 否则什么也不做并且返回false。
     * 其中，删除文件的时候如果删除失败会尝试最多删除 maxRetryCount 次.
     *
     * @param file
     * @return
     */
    public static boolean deleteDependon(File file, int maxRetryCount) {
        int retryCount = 1;
        maxRetryCount = maxRetryCount < 1 ? FileUtils.DEFAULT_DELETE_LINITE : maxRetryCount;
        boolean isDeleted = false;

        if (file != null) {
            while (!isDeleted && (retryCount <= maxRetryCount) && BaseFileUtils.doesExisted(file)) {
                if (!(isDeleted = file.delete())) {

                    retryCount++;
                }
            }

        }

        return isDeleted;
    }

    /**
     * 如果doesExisted(filepath_)为true且是文件类型则删除文件，并返回true； 否则什么也不做并且返回false。
     * 如果删除失败，不会重试。
     *
     * @param filepath
     * @return
     */
    public static boolean deleteDependon(String filepath) {
        return FileUtils.deleteDependon(filepath, 0);
    }

    /**
     * 如果doesExisted(file_)为true且是文件类型则删除文件，并返回true； 否则什么也不做并且返回false。
     * 其中，删除文件的时候如果删除失败会尝试最多删除 maxRetryCount 次.
     *
     * @param filepath
     * @param maxRetryCount
     * @return
     */
    public static boolean deleteDependon(String filepath, int maxRetryCount) {
        if (TextUtils.isEmpty(filepath)) {
            return false;
        }
        return FileUtils.deleteDependon(new File(filepath), maxRetryCount);
    }

    public static void openAPKFileByOS(Context context, String filepath) {
        if (TextUtils.isEmpty(filepath)) {
            return;
        }
        File file = new File(filepath);
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = "application/vnd.android.package-archive";
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // no activity found
        }
    }

    /**
     * 依靠系统打开文件，类型依靠filepath的后缀名获取
     *
     * @param context
     * @param filepath
     */
    public static void openFileByOS(Context context, String filepath) {
        if (TextUtils.isEmpty(filepath) || !filepath.contains(".")) {
            return;
        }
        File file = new File(filepath);
        if (file == null || !file.exists()) {
            return;
        }
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        String type = getMIMEType(file);
        intent.setDataAndType(Uri.fromFile(file), type);
        try {
            context.startActivity(intent);
        } catch (Exception e) {
            // no activity found
        }
    }

    /**
     * 根据后缀名获取文件的MIME
     *
     * @param file
     * @return
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fileName.substring(dotIndex, fileName.length());
        if (TextUtils.isEmpty(end) && end.length() < 2) {
            return type;
        }
        end = end.substring(1, end.length()).toLowerCase(Locale.US);
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        type = mimeTypeMap.getMimeTypeFromExtension(end);
        return type;
    }

    /**
     * 获取本地apk文件的信息
     *
     * @param context
     * @param file
     * @return
     */
    public static PackageInfo getPackageInfo(Context context, String file) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(file, PackageManager.GET_ACTIVITIES);
        return info;
    }

    /**
     * 确保指定文件或者文件夹存在
     *
     * @param file
     * @return
     */
    public static boolean makeSureFileExist(File file) {

        if (makesureParentDirExist(file)) {
            if (file.isFile()) {
                try {
                    return file.createNewFile();
                } catch (IOException e) {
                }
            } else {
                return file.mkdirs();
            }
        }

        return false;
    }

    public static boolean makeDir(String dirPath) {
        if (TextUtils.isEmpty(dirPath)) {
            return false;
        }
        File dir = new File(dirPath);
        if (dir.isDirectory() && dir.exists()) {
            return true;
        }
        return dir.mkdirs();
    }

    /**
     * 确保指定文件或者文件夹存在
     *
     * @param filePath
     * @return
     */
    public static boolean makeSureFileExist(String filePath) {
        return makeSureFileExist(new File(filePath));
    }

    public static void unZipFolder(String zipFilePath, String outPath) throws Exception {
        if (isDirectoryExist(outPath)) return;

        ZipInputStream inZip;
        BufferedOutputStream out;
        inZip = new ZipInputStream(new BufferedInputStream(new FileInputStream(zipFilePath)));
        ZipEntry zipEntry;
        while ((zipEntry = inZip.getNextEntry()) != null) {
            String szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                // get the folder name of the widget
                File folder = new File(outPath + File.separator + szName);
                folder.mkdirs();
            } else {
                File file = new File(outPath + File.separator + szName);
                if (file.exists()) {
                    file.delete();
                } else {
                    file.getParentFile().mkdirs();
                }
                file.createNewFile();
                // get the output stream of the file
                out = new BufferedOutputStream(new FileOutputStream(file));
                int len;
                byte[] buffer = new byte[2048];
                // read (len) bytes into buffer
                while ((len = inZip.read(buffer)) != -1) {
                    // write (len) byte from buffer at the position 0
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
            }
        }//end of while
        inZip.close();
    }//end of func
}
