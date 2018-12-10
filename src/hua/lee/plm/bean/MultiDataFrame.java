package hua.lee.plm.bean;

import hua.lee.plm.base.Command;
import hua.lee.plm.engine.CommandFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

/**
 * 多数据帧对象
 *
 * @author lijie
 * @create 2018-12-06 23:12
 **/
public class MultiDataFrame extends Command {
    private byte[] originFrame;

    public MultiDataFrame(byte[] data) {
        this.originFrame = new byte[data.length - 2];
        String left = Integer.toHexString(data[0] & 0xff).toUpperCase();
        if (left.length() != 2) {
            left = "0" + left;
        }
        String right = Integer.toHexString(data[1] & 0xff).toUpperCase();
        if (right.length() != 2) {
            right = "0" + right;
        }
        System.arraycopy(data, 2, originFrame, 0, originFrame.length);

        setCMD_ID(left + right);
        setCommandVO(CommandFactory.getCommadVO(left + right));
    }

    @Override
    public byte[] transToByte() {
        return originFrame;
    }

    public void generateFile() throws IOException {
        System.out.println("Multi ==> generateFile");
        File file = new File("/Users/lijie/Desktop/TE/" + new Date().getSeconds() + ".bin");
        if (!file.exists()) {
            file.createNewFile();
        }
        FileOutputStream out = new FileOutputStream(file);
        out.write(originFrame);
        out.flush();
        out.close();
    }
}
