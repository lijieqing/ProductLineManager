package hua.lee.plm;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * mac 地址分割
 *
 * @author lijie
 * @create 2018-12-06 10:01
 **/
public class MacSplit {

    private static void macSplit(int start, int end) {
        int sizeMac = 10000;
        int size = end - start + 1;
        int size10K = size / sizeMac;
        int sizeTail = size % sizeMac;

        System.out.println("size 10k = " + size10K);
        System.out.println("size tail = " + sizeTail);

        int startPos = 0;
        int endPos = 0;
        for (int i = 0; i < size10K; i++) {
            startPos = start + i * sizeMac;
            endPos = start + i * sizeMac + sizeMac - 1;
            showMac("44:D5:F2:", "10K",
                    Integer.toHexString(startPos).toUpperCase(),
                    Integer.toHexString(endPos).toUpperCase());
            generateMacTxt(Integer.toHexString(startPos).toUpperCase()
                            + "-" +
                            Integer.toHexString(endPos).toUpperCase(),
                    "44:D5:F2:", startPos, endPos);
        }

        startPos = endPos + 1;
        endPos = startPos + sizeTail - 1;
        showMac("44:D5:F2:", sizeTail + "",
                Integer.toHexString(startPos).toUpperCase(),
                Integer.toHexString(endPos).toUpperCase());
        generateMacTxt(Integer.toHexString(startPos).toUpperCase()
                        + "-" +
                        Integer.toHexString(endPos).toUpperCase(),
                "44:D5:F2:", startPos, endPos);
    }

    private static void showMac(String head, String size, String sS, String sE) {
        System.out.println(size + "::: mac " +
                head + sS.substring(0, 2) + ":" + sS.substring(2, 4) + ":" + sS.substring(4, 6) +
                " <<==>> " +
                head + sE.substring(0, 2) + ":" + sE.substring(2, 4) + ":" + sE.substring(4, 6));

    }

    public static void main(String[] args) throws IOException {
        macSplit(0x400000, 0x43FFFF);
    }

    private static void generateMacTxt(String name, String head, int start, int end) {
        try {
            FileWriter writer = new FileWriter("/Users/lijie/Desktop/mac/" + name + ".txt");
            for (int i = start; i <= end; i++) {
                String sS = Integer.toHexString(i).toUpperCase();
                sS = head + sS.substring(0, 2) + ":" + sS.substring(2, 4) + ":" + sS.substring(4, 6) + "\n";
                writer.write(sS);
                writer.flush();
            }
            System.out.println(start + "----write end----" + end);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
