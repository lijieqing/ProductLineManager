package hua.lee.plm.base;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * 抽象通讯端口
 *
 * @author lijie
 * @create 2019-02-22 13:28
 **/
public abstract class CommunicatePort {
    protected InputStream inputStream;
    protected OutputStream outputStream;

    public CommunicatePort() {
        initPort();
    }

    /**
     * 加载 InputStream for data read
     *
     * @return inputStream
     */
    public InputStream loadInputStream() {
        return inputStream;
    }

    /**
     * 加载 OutputStream for data write
     *
     * @return outputStream
     */
    public OutputStream loadOutputStream() {
        return outputStream;
    }

    public abstract int readData(byte[] recvBuffer, int off, int len);

    public abstract void writeData(byte[] data);

    /**
     * 通讯初始化，包括 open  port 和 IO 打开
     */
    public abstract void initPort();

    /**
     * 通讯关闭，包括 close port 和 IO 关闭
     */
    public abstract void closePort();

    public abstract int dataAvailable();
}
