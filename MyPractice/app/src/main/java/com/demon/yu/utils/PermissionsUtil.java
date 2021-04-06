package com.demon.yu.utils;

import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

import com.example.mypractice.common.Common;

public class PermissionsUtil {

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if (grantResults.length < 1) {
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public static boolean hasPermission(String permission) {
        return ActivityCompat.checkSelfPermission(Common.application, permission) == PackageManager.PERMISSION_GRANTED;
    }
}