package hua.lee.plm.base;

public interface DataReceivedCallback {
    void onSingleDataReceived(byte[] data);
    void onMultiDataReceived(byte[] data);
}