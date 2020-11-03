package hua.lee.plm.fm;

import java.io.*;

/**
 * Netflix key write
 *
 * @author lijie
 * @create 2020-01-09 11:44
 **/
public class NetflixSplit {
    public static void main(String[] args) throws IOException {
        File file = new File("/Users/lijie/Desktop/NFANDROID2-PRV-EVA-MGK0000000000000000070000.csv");
        FileReader fr = new FileReader(file);
        BufferedReader reader = new BufferedReader(fr);
        String key;
        while ((key = reader.readLine()) != null) {
            String[] splits = key.split(",");
            String name = "/Users/lijie/Desktop/nts-key/" + splits[0] + ".bin";
            byte[] data = key.getBytes();
            FileOutputStream out = new FileOutputStream(name);
            out.write(data);
            out.flush();
            out.close();
        }
    }
}
