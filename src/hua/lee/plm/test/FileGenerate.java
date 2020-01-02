package hua.lee.plm.test;

import java.io.*;

/**
 * WIFI 生成
 *
 * @author lijie
 * @create 2019-12-19 14:16
 **/
public class FileGenerate {
    public static void main(String[] args) throws IOException {

    }

    private static void generateFactFile(String name) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/lijie/Desktop/" + name));
        for (int i = 0; i < name.length(); i++) {
            char data = name.charAt(i);
            for (int j = 0; j < 4000000; j++) {
                writer.write(data);
            }
        }

        writer.flush();
    }
}
