package com.example.mypractice.process;

import android.os.IBinder;
import android.os.IInterface;
import android.os.RemoteException;

import com.example.mypractice.Logger;

public class PlusProxy implements IPlus {

    private IBinder iBinder;

    public PlusProxy(IBinder iBinder) {
        this.iBinder = iBinder;
    }

    @Override
    public int add(int a, int b) {
        android.os.Parcel data = android.os.Parcel.obtain();
        android.os.Parcel reply = android.os.Parcel.obtain();
        int _result = 0;
        data.writeInterfaceToken("add");
        data.writeInt(a);
        data.writeInt(b);
        try {
            iBinder.transact(0, data, reply, 0);
            reply.readException();
            _result = reply.readInt();
            Logger.d(" 2 + 3 = " + _result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return _result;
    }

    @Override
    public IBinder asBinder() {
        return iBinder;
    }
}
