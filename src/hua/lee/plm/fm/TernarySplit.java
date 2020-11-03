package hua.lee.plm.fm;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 米家三元组切割
 */
public class TernarySplit {
    private static int space = 4999;
    public static void main(String[] args) throws IOException {
        String modelName = "135FCNPro";
        String fileName = "135FCNPro-44D5F241FBD0-44D5F2420F57-20200720";
        String originPath = "/Users/lijie/Desktop/ternary-0720/" + fileName + ".txt";
        String outPath = "/Users/lijie/Desktop/ternary-key/" + modelName;
        ternarySplit(originPath, outPath, modelName);
        //macDecorate();
    }

    private static void macDecorate() throws IOException {
        String filePath = "/Users/lijie/Desktop/ternary-key/M085JCN/M085JCN-TernaryKey-20200305-00000000.bin";
        FileInputStream ins = new FileInputStream(filePath);
        byte[] datas = new byte[39];
        ins.read(datas);

        String ternary = new String(datas);
        String[] keys = ternary.split("\\|");
        if (keys.length == 3 && keys[0].length() == 12 && keys[1].length() == 9 && keys[2].length() == 16) {
            String mac = keys[0];
            String did = keys[1];
            String miKey = keys[2];
            //50 EC 50 FF F3 42
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 6; i++) {
                String s = mac.substring(i * 2, i * 2 + 2);
                if (i == 5) {
                    sb.append(s);
                } else {
                    sb.append(s).append(":");
                }
            }

            System.out.println(sb.toString());
        }
    }

    private static void ternarySplit(String originPath, String outPath, String modelName) throws IOException {
        FileReader reader = new FileReader(originPath);
        BufferedReader br = new BufferedReader(reader);
        File file = new File(outPath);
        if (!file.exists()) {
            file.mkdirs();
        }
        String key;
        int startSerial = 0;
        int serialLen = 8;
        int count = 0;
        String oldSerial = startSerial + "";

        while ((key = br.readLine()) != null) {
            byte[] data = key.getBytes();
            String[] keys = key.split("\\|");
            if (keys.length == 3 && keys[0].length() == 12 && keys[1].length() == 9 && keys[2].length() == 16) {
                String serialName = incrementSerial(startSerial++, serialLen);
                String name = modelName + "-TernaryKey-" + currentDate() + "-" + serialName + ".bin";
                String tempPath;

                if (count > space) {
                    count = 0;

                    zipFiles(outPath+"/"+oldSerial,outPath,oldSerial);

                    tempPath = outPath + "/" + serialName;
                    oldSerial = serialName;
                } else {
                    tempPath = outPath + "/" + oldSerial;
                }

                File outDirect = new File(tempPath);
                if (!outDirect.exists()) {
                    outDirect.mkdir();
                }
                FileOutputStream out = new FileOutputStream(tempPath + "/" + name);
                out.write(data);
                out.flush();
                out.close();
                count++;
            } else {
                throw new IllegalArgumentException("三元组解析异常，请确认数据是否正确");
            }

        }
    }

    private static String incrementSerial(int serial, int len) {
        StringBuilder res = new StringBuilder();
        String s = String.valueOf(serial);
        if (s.length() <= len) {
            int zeroSize = len - s.length();
            for (int i = 0; i < zeroSize; i++) {
                res.append("0");
            }
            res.append(s);
            return res.toString();
        } else {
            throw new RuntimeException("长度超出限制");
        }
    }

    private static String currentDate() {
        SimpleDateFormat sdf = (SimpleDateFormat) SimpleDateFormat.getInstance();
        sdf.applyPattern("yyyyMMdd");

        return sdf.format(new Date());
    }

    private static void zipFiles(String srcPath,String outPath,String name) {
        int start = Integer.parseInt(name);
        int end = start + space;
        try {
            FileOutputStream os = new FileOutputStream(outPath+"/"+start+"-"+end+".zip");
            ZipUtils.toZip(srcPath, os, true);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
