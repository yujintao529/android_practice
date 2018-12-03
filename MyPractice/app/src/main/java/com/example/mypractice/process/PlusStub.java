package com.example.mypractice.process;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public abstract class PlusStub extends Binder implements IPlus {
    public PlusStub() {
        attachInterface(this, "add");
    }

    @Override
    protected boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {

        switch (code) {
            case 0: {
                data.enforceInterface("add");
                int _arg0;
                _arg0 = data.readInt();
                int _arg1;
                _arg1 = data.readInt();
                int _result = ((IPlus) queryLocalInterface("add")).add(_arg0, _arg1);
                reply.writeNoException();
                reply.writeInt(_result);
                return true;
            }
        }
        return super.onTransact(code, data, reply, flags);
    }

    @Override
    public IBinder asBinder() {
        return this;
    }

    public static IPlus asInterface(IBinder iBinder) {
        if (iBinder == null) {
            return null;
        }
        IInterface iInterface = iBinder.queryLocalInterface("add");
        if (iInterface != null && iInterface instanceof IPlus) {
            return (IPlus) iInterface;
        }
        return new PlusProxy(iBinder);
    }
}
