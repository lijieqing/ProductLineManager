package hua.lee.plm.test;

import java.util.Arrays;

/**
 * 字符串解析测试
 *
 * @author lijie
 * @create 2019-05-08 14:24
 **/
public class StrTest {
    private static String str = "mcu_status_flag: 0xbb  [0]INITED:\t 1[1]ACOK:\t 1[2]TV_PWR_ON:\t 0[3]TV_SYS_UP:\t 1[4]PROCHOT:\t 1[5]CHARGING:\t 1[6]TEMP_SHUTDOWN:\t 0[7]IBAT_LOW:\t 1[8]VBAT_HIGH:\t 0[9]STATUS_VBAT_LOW:\t 0[10]STATUS_VBAT_SAVE:\t 0";

    public static void main(String[] args) {
//        String[] ss = str.split("\\[[0-9]{1,2}\\]");
//        for (String s : ss) {
//            String[] map = s.split(":");
//            if (map.length == 2) {
//                System.out.println(map[0] + "=" + map[1].trim());
//            }
//        }

        int b = -104;

        System.out.println(b & 0xFF);
        String s = "255:0:0";

        System.out.println(s.matches("[0-9]{1,3}:[0-9]{1,3}:[0-9]{1,3}"));
        int i = 0;
        do {
            i++;
            System.out.println("do while i=" + i);
        } while (i < 10);

    }
}
