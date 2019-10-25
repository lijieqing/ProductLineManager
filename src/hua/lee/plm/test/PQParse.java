package hua.lee.plm.test;

import java.io.*;

/**
 * @author lijie
 * @create 2019-06-28 17:50
 **/
public class PQParse {
    public static void main(String[] args) throws IOException {
        parsePQData(new File("/Users/lijie/Desktop/TE/2123600047052.ini"));
    }

    private static void parsePQData(File file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String s;
        while ((s = reader.readLine()) != null) {
            if (s.matches("item143[0-9]{2}=[0-9]{1,}")) {
                System.out.println(s);
                String[] item_value = s.split("=");
                String itemInfo = item_value[0];
                String itemValue = item_value[1];
            }
        }
    }
}
