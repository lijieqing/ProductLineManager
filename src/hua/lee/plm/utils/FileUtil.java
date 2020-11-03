package hua.lee.plm.utils;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

public final class FileUtil {
    private static final String TAG = "Factory_FileUtil";
    private FileUtil() {
    }

    /**
     * 写入指定的文本文件
     *
     * @param filePath 文件路径
     * @param append   是否追加
     * @param text     文本内容
     * @return 写入成功 or 失败
     */
    public static synchronized boolean write(String filePath, boolean append, String text) {
        if (text == null)
            return false;
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(filePath, append));
            try {
                out.write(text);
            } finally {
                out.close();
            }
            return true;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获取单个文件的MD5值！
     *
     * @param file    待计算文件
     * @param calcLen 需要进行计算的文件长度。<p/>
     *                <p>单位:kb</p>
     *                <p>0:表示进行整个文件的MD5计算</p>
     * @param head    取文件前端还是后端，true 表示前端
     * @return MD5
     */

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
            long totalLen = in.available();
            if (totalLen == 0) {
                // 当文件大小超过2G时，available返回0，此时我们直接使用 file.length()
                totalLen = file.length();
            }

            //比对待计算长度和文件长度，超出文件长度返回null
            if (calcLen * 1024 > totalLen) {
                return null;
            }

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
                long offset = totalLen - readLine;
                byte[] calcBuffer = new byte[readLine];

                //读取特定偏移量下的字节
                long realSkip = in.skip(offset);
                if (realSkip == offset) {
                    len = in.read(calcBuffer);
                    digest.update(calcBuffer, 0, len);
                } else {
                    return null;
                }

            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return String.format("%032x", bigInt);
    }

    /***
     *  计算输出字节的32位md5码
     */
    public static String string2MD5(byte[] key) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        byte[] md5Bytes = md5.digest(key);
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
