package hua.lee.plm.base;


import com.sun.istack.internal.Nullable;

public interface GlobalCommandReceiveListener {
    void onRXWrapperReceived(String cmdID, @Nullable byte[] data);
}