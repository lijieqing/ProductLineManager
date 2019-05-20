package hua.lee.plm;

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
    public static void macSplit() {
        String sS, sE;
        int start = 0xEF7100;
        for (int i = 0; i < 9; i++) {
            sS = Integer.toHexString(start).toUpperCase();
            sE = Integer.toHexString(start += 19999).toUpperCase();
            showMac("20K", sS, sE);
            start += 1;
        }
        sS = Integer.toHexString(start).toUpperCase();
        sE = Integer.toHexString(start += 9999).toUpperCase();
        showMac("10K", sS, sE);
        start += 1;
        sS = Integer.toHexString(start).toUpperCase();
        sE = Integer.toHexString(start += 6607).toUpperCase();
        showMac("6608", sS, sE);
        start += 1;
        //System.out.println(""+Integer.toHexString(0xF424+3035).toUpperCase());
    }

    private static void showMac(String size, String sS, String sE) {
        System.out.println(size + "::: mac " +
                "D0:D9:4F:" + sS.substring(0, 2) + ":" + sS.substring(2, 4) + ":" + sS.substring(4, 6) +
                "<<==>> " +
                "D0:D9:4F:" + sE.substring(0, 2) + ":" + sE.substring(2, 4) + ":" + sE.substring(4, 6));

    }

    public static void main(String[] args) throws IOException {
//        System.out.println(0xEFFFFF - 0xED0000 + 1);
//        int a = 0xEF7100 + 9999;
//        System.out.println(Integer.toHexString(a).toUpperCase());
//        a++;
//        System.out.println(Integer.toHexString(a).toUpperCase());
//        System.out.println(Integer.toHexString(a + 9999).toUpperCase());

        //macSplit();

        //generateMacTxt();

        showMac_S_and_E("D0:D9:4F:EF:",0xe694,100);
    }

    private static void generateMacTxt() throws IOException {
        FileOutputStream outputStream = new FileOutputStream("/Users/lijie/Desktop/macList.txt");

        FileWriter writer = new FileWriter("/Users/lijie/Desktop/macList.txt");
//        int c = 0;
//        for (int i = 0xED0000; i <= 0xEFFFFF; i++) {
//            String sS = Integer.toHexString(i).toUpperCase();
//            sS = "D0:D9:4F:" + sS.substring(0, 2) + ":" + sS.substring(2, 4) + ":" + sS.substring(4, 6) +"\n";
//
//            c++;
//        }
        int data = 0xEFFE37;
        for (int i = 0; i < 5000; i++) {
            String sS = Integer.toHexString(data).toUpperCase();
            sS = "D0:D9:4F:" + sS.substring(0, 2) + ":" + sS.substring(2, 4) + ":" + sS.substring(4, 6) +"\n";
            writer.write(sS);
            data++;
        }
        //System.out.println(c);
    }
    private static void showMac_S_and_E(String prefix,int start,int len){
        //D0:D9:4F:EF:E6:30
        //D0:D9:4F:EF:FF:FF
        start--;
        for (int i = 0; i < len; i++) {
            start++;
            String val = Integer.toHexString(start).toUpperCase();
            val = prefix+val.substring(0,2)+":"+val.substring(2,4);
            System.out.println(val);
        }
    }
}
