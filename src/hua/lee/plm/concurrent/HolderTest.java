package hua.lee.plm.concurrent;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Holder Test
 *
 * @author lijie
 * @create 2019-10-25 13:54
 **/
public class HolderTest {
    public Holder holder;

    public void initialize() {
        holder = new Holder(42);
    }

    public static void main(String[] args) {
        File file = new File("/Users/lijie/checksum-upgrade.bin");
        System.out.println(getFileMD5(file, 0, true));
    }

    public static String getFileMD5(File file, int calcLen, boolean head) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte[] buffer = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            //获取文件长度总长度
            int totalLen = in.available();

            //比对待计算长度和文件长度，超出文件长度返回null
            if (calcLen * 1024 > totalLen) return null;

            boolean fullCalc = false;
            if (calcLen == 0) {
                //计算长度为0时，表示进行全文件md5计算
                fullCalc = true;
            }

            if (head || fullCalc) {
                //计算头部或者全文件计算时
                //计数统计
                int count = calcLen;
                while ((len = in.read(buffer, 0, 1024)) != -1) {
                    if (fullCalc) {
                        digest.update(buffer, 0, len);
                    } else {
                        if (count-- > 0) {
                            digest.update(buffer, 0, len);
                        }
                    }
                }
            } else {
                //计算尾部MD5的情况

                // 计算要读取的字节长度
                int readLine = calcLen * 1024;
                //计算文件偏移位置
                int offset = totalLen - readLine;
                System.out.println("readLine : " + readLine);
                System.out.println("offset : " + offset);
                byte[] calcBuffer = new byte[readLine];

                //读取特定偏移量下的字节
                long realSkip = in.skip(offset);
                if (realSkip == offset) {
                    len = in.read(calcBuffer);
                    System.out.println("read bytes : " + len);
                    digest.update(calcBuffer, 0, len);
                } else {
                    System.out.println("real skip != offset");
                    return null;
                }

            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return String.format("%032x",bigInt);
    }
}
