package com.demo.yu.context.files;

import android.content.Context;
import android.os.Environment;

import com.demon.yu.utils.FileUtils;
import com.example.mypractice.common.Common;

import java.io.File;

public class FilesManager {
    private volatile static FilesManager filesManager;

    private Context context;


    public FilesManager(Context context) {
        this.context = context;
    }

    public File getSDCardBaseFile() {
        return Environment.getExternalStorageDirectory();
    }


    public File getSystemDCIM() {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
    }

    /**
     * /sdcard/Android/data/[package]/
     *
     * @return
     */
    public File getExternalWorkSpace() {
        return new File(getSDCardBaseFile(), "Android/data/" + context.getPackageName());
    }

    public File getSDCardWorkSpace() {
        File root = new File(getSDCardBaseFile(), context.getPackageName());
        FileUtils.makeSureFileExist(root);
        return root;
    }

    public File getSDCardRootFiles(String child) {
        File parent = getSDCardWorkSpace();
        File dest = new File(parent, child);
        FileUtils.makeSureFileExist(dest);
        return dest;
    }

    public File createExternalFiles(String child) {
        File parent = getExternalWorkSpace();
        File dest = new File(parent, child);
        FileUtils.makeSureFileExist(dest);
        return dest;
    }


    public static FilesManager getInstance() {
        if (filesManager == null) {
            synchronized (FilesManager.class) {
                if (filesManager == null) {
                    filesManager = new FilesManager(Common.application);
                }
            }
        }
        return filesManager;
    }
}
