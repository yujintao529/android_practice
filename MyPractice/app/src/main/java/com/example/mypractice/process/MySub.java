package com.example.mypractice.process;

import android.os.RemoteException;

import com.example.mypractice.ISub;
import com.example.mypractice.ISub.Stub;

public class MySub extends Stub {

    @Override
    public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

    }

    @Override
    public int sub(int a, int b) throws RemoteException {
        return 0;
    }
}
