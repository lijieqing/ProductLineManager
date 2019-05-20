package hua.lee.plm.test;

import java.io.*;
import java.util.*;

/**
 * 联想测试二站文件解析
 *
 * @author lijie
 * @create 2019-05-14 10:46
 **/
public class FileParse {
    private static Map<String, List<PQData>> result = new HashMap<>();

    public static void main(String[] args) {
        String root = "/Users/lijie/Desktop/data";
        File file = new File(root);
        String[] list = file.list();
        for (String s : list) {
            String fileP = root + File.separator + s;
            File data = new File(fileP);
            List<PQData> dataList = new ArrayList<>();
            result.put(s, dataList);
            if (data.exists()) {
                fileParse(data, dataList);
            }
        }

//        int countAll = 0;
//        for (String key : result.keySet()) {
//            List<PQData> res = result.get(key);
//            boolean pass = true;
//            int count = 0;
//            for (PQData re : res) {
//                if (re.pass) {
//                    count++;
//                }
//            }
//            if (count < 5){
//                countAll++;
//            }
//        }
//        System.out.println("异常数据记录："+countAll);

//        try {
//            BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/lijie/Desktop/result.txt"));
//            for (String s : result.keySet()) {
//                writer.append("-----------------" + s + "------------------").append("\n");
//                for (PQData pqData : result.get(s)) {
//                    writer.append(pqData.toString()).append("\n");
//                }
//                writer.append("-----------------" + s + "------------------").append("\n");
//            }
//            writer.flush();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    private static void fileParse(File file, List<PQData> list) {
        try {
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String s = null;
            while ((s = reader.readLine()) != null) {
                if (s.contains("item126C70IREcolor")) {
                    PQData pq = new PQData();
                    pq.ire = 70;
                    richPQ(pq, s);
                    list.add(pq);
                }
                if (s.contains("item126C30IREcolor")) {
                    PQData pq = new PQData();
                    pq.ire = 30;
                    richPQ(pq, s);
                    list.add(pq);
                }
                if (s.contains("RES=")) {
                    PQData pq = new PQData();
                    pq.desc = s;
                    list.add(pq);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void richPQ(PQData pq, String item) {
        if (pq != null) {
            String[] ss = item.split("=");
            String key = ss[0];
            String val = ss[1];
            if (key.contains("color0")) {
                pq.colorTemp = "冷";
                pq.color = 0;
            }
            if (key.contains("color1")) {
                pq.colorTemp = "标准";
                pq.color = 1;
            }
            if (key.contains("color2")) {
                pq.colorTemp = "暖";
                pq.color = 2;
            }
            String[] xyDatas = val.split("[X,Y]{1}");
            if (xyDatas.length == 3) {
                pq.lv = xyDatas[0];
                pq.x = Integer.parseInt(xyDatas[1]);
                pq.y = Integer.parseInt(xyDatas[2]);
                pq.verify();
            } else {
                pq.pass = false;
                pq.desc = "XY 格式异常";
            }
            //System.out.println(pq.toString());
        }
    }

    static class PQData {
        String colorTemp;
        int color;
        int ire;
        int x;
        int y;
        String lv;
        boolean pass;
        String desc;

        public void verify() {
            switch (ire) {
                case 70:
                    switch (color) {
                        case 0:
                            if (x >= 2720 && x <= 2780 && y >= 2720 && y <= 2780) {
                                pass = true;
                            } else {
                                pass = false;
                            }
                            break;
                        case 1:
                            if (x >= 2870 && x <= 2930 && y >= 3070 && y <= 3130) {
                                pass = true;
                            } else {
                                pass = false;
                            }
                            break;
                        case 2:
                            if (x >= 3120 && x <= 3180 && y >= 3230 && y < 3280) {
                                pass = true;
                            } else {
                                pass = false;
                            }
                            break;
                    }
                    break;
                case 30:
                    switch (color) {
                        case 0:
                            if (x >= 2690 && x <= 2810 && y >= 2690 && y <= 2810) {
                                pass = true;
                            } else {
                                pass = false;
                            }
                            break;
                        case 1:
                            if (x >= 2840 && x <= 2960 && y >= 3040 && y <= 3160) {
                                pass = true;
                            } else {
                                pass = false;
                            }
                            break;
                        case 2:
                            if (x >= 3090 && x <= 3210 && y >= 3190 && y < 3310) {
                                pass = true;
                            } else {
                                pass = false;
                            }
                            break;
                    }
                    break;
            }
        }

        @Override
        public String toString() {
            return "PQData{" +
                    "colorTemp='" + colorTemp + '\'' +
                    ", color=" + color +
                    ", ire=" + ire +
                    ", x=" + x +
                    ", y=" + y +
                    ", lv=" + lv +
                    ", pass=" + (pass ? "合格" : "不符合标准") +
                    ", desc='" + desc + '\'' +
                    '}';
        }
    }
}
