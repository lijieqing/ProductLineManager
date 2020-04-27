package hua.lee.plm.fm;

import hua.lee.plm.bean.PQData;
import hua.lee.plm.utils.ThreadUtils;

import java.io.*;
import java.util.*;

public class PQParse {
    public static void main(String[] args) throws IOException {
        String[] PIDs = new String[]{
                "18301",
                "19301",
                "19302",
                "21236",
                "20301",
                "20302",
                "20303",
                "28022",
                "27859",
                "27858",
        };
        List<String> dataPaths = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            dataPaths.add("/Users/lijie/Desktop/testlog/testlog" + i + ".txt");
        }

        for (String pid : PIDs) {
            for (String dataPath : dataPaths) {
                ThreadUtils.runTaskOnBack(() -> {
                    try {
                        generatePQData(pid, dataPath);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.out.println("ops IO error");
                    }
                });
            }
        }

    }


    private static void pqDataParse(PQData pqData, String data) {
        String value = parseData(data);
        if (data.contains("读取标准色温白平衡红色增益")) {
            pqData.setNormalGainRed(value);
        }
        if (data.contains("读取冷色色温白平衡红色增益")) {
            pqData.setCoolGainRed(value);
        }
        if (data.contains("读取暖色色温白平衡红色增益")) {
            pqData.setWarmGainRed(value);
        }
        if (data.contains("读取标准色温白平衡绿色增益")) {
            pqData.setNormalGainGreen(value);
        }
        if (data.contains("读取冷色色温白平衡绿色增益")) {
            pqData.setCoolGainGreen(value);
        }
        if (data.contains("读取暖色色温白平衡绿色增益")) {
            pqData.setWarmGainGreen(value);
        }
        if (data.contains("读取标准色温白平衡蓝色增益")) {
            pqData.setNormalGainBlue(value);
        }
        if (data.contains("读取冷色色温白平衡蓝色增益")) {
            pqData.setCoolGainBlue(value);
        }
        if (data.contains("读取暖色色温白平衡蓝色增益")) {
            pqData.setWarmGainBlue(value);
        }
        if (data.contains("读取标准色温白平衡红色偏移")) {
            pqData.setNormalOffRed(value);
        }
        if (data.contains("读取冷色色温白平衡红色偏移")) {
            pqData.setCoolOffRed(value);
        }
        if (data.contains("读取暖色色温白平衡红色偏移")) {
            pqData.setWarmOffRed(value);
        }
        if (data.contains("读取标准色温白平衡绿色偏移")) {
            pqData.setNormalOffGreen(value);
        }
        if (data.contains("读取冷色色温白平衡绿色偏移")) {
            pqData.setCoolOffGreen(value);
        }
        if (data.contains("读取暖色色温白平衡绿色偏移")) {
            pqData.setWarmOffGreen(value);
        }
        if (data.contains("读取标准色温白平衡蓝色偏移")) {
            pqData.setNormalOffBlue(value);
        }
        if (data.contains("读取冷色色温白平衡蓝色偏移")) {
            pqData.setCoolOffBlue(value);
        }
        if (data.contains("读取暖色色温白平衡蓝色偏移")) {
            pqData.setWarmOffBlue(value);
        }
    }

    private static String parseData(String data) {
        if (data.length() < 75) {
            return null;
        }
        String res = data.substring(69, 73);
        if (res.contains("\"")) res = res.replace("\"", "");
        System.out.println("parse data " + res);
        return res;
    }

    private static void generatePQData(String PID, String dataPath) throws IOException {
        List<String> snList = new ArrayList<>();
        File file = new File("/Users/lijie/Desktop/pq/PID-" + PID + "-" + new Date() + ".csv");
        //File file = new File("/Users/lijie/Desktop/pq/FM05501.csv");
        if (!file.exists()) {
            file.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        //写入 CSV 标题
        writer.append("SN,N_RGain,C_RGain,W_RGain,N_GGain,C_GGain,W_GGain,N_BGain,C_BGain,W_BGain,W_ROffset,W_GOffset,W_BOffset,C_ROffset,C_GOffset,C_BOffset,N_ROffset,N_GOffset,N_BOffset\n");


        String data;
        String oldSN = null;
        PQData pqData = null;
        Set<String> invalidCount = new HashSet<>();

        //第一遍按顺序读取数据，记录异常数据 SN
        //异常原因是因为 SN 数据不连贯
        BufferedReader reader = new BufferedReader(new FileReader(dataPath));
        while ((data = reader.readLine()) != null) {
            String sn = data.substring(12, 26);
            if (oldSN == null || !oldSN.equals(sn)) {
                if (pqData != null && !snList.contains(pqData.getSN())) {
                    if (pqData.containNull()) {
                        invalidCount.add(pqData.getSN());
                    } else {
                        //判断是否为需要提取的产品数据 PID
                        if (pqData.getSN().startsWith(PID)) {
                            writer.append(pqData.toCSVNew());
                            snList.add(pqData.getSN());
                        }
                    }

                }
                System.out.println("new SN,we create new File");
                pqData = new PQData();
                pqData.setSN(sn);
            } else {
                System.out.println("old sn " + sn);
            }
            pqDataParse(pqData, data);
            oldSN = sn;
            System.out.println(pqData.toString());
        }
        reader.close();

        System.out.println("invalid sn list = " + Arrays.toString(invalidCount.toArray()));
        System.out.println("invalid sn count = " + invalidCount.size());

        Map<String, PQData> fixedMap = new HashMap<>();
        for (String sn : invalidCount) {
            if (sn.matches("[0-9]{13}")) {
                PQData temp = new PQData();
                temp.setSN(sn);
                fixedMap.put(sn, temp);
            }
        }
        //第二遍重新查询异常 SN 的数据
        reader = new BufferedReader(new FileReader(dataPath));
        while ((data = reader.readLine()) != null) {
            for (String sn : invalidCount) {
                if (data.contains(sn.substring(5))) {
                    System.out.println("find fixed Data");
                    PQData temp = fixedMap.get(sn);
                    if (temp != null) {
                        pqDataParse(temp, data);
                    }
                    break;
                }
            }
        }

        for (String sn : invalidCount) {
            if (sn.startsWith(PID)) {
                writer.append(fixedMap.get(sn).toCSVNew());
                System.out.println("fixed sn PQ data = " + fixedMap.get(sn).toCSVNew());
            }
        }

        reader.close();
        writer.flush();
    }
}
