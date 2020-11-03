package hua.lee.plm.fm;

import hua.lee.plm.utils.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

public class MD5Test {
    public static void main(String[] args) throws IOException {
        String str = "2a939f0e0cfacd698c1a31fcefea4ec8a77502e419af2e9a483cf2656affcb5ddc5e3c2329c99468aac72aa1a3ab9100\n";

        System.out.println("str md5:" + Arrays.toString(str.getBytes()));
        File file = new File("/Users/lijie/Desktop/temp.bin");
        FileInputStream is = new FileInputStream(file);
        int len = is.available();
        byte[] data = new byte[len];
        is.read(data);
        System.out.println("file md5:" + Arrays.toString(data));

        System.out.println(FileUtil.string2MD5(str.getBytes()));

    }

}
