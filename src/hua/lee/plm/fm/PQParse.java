package hua.lee.plm.fm;

import hua.lee.plm.bean.PQData;
import hua.lee.plm.utils.ThreadUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class PQParse {
    private static Map<String, List<String>> mapPidPath;
    private static int taskSize = 0;
    private static CountDownLatch latch;

    public static void main(String[] args) throws Exception {
        System.out.println(new Date());
        mapPidPath = new HashMap<>();
        //step 1
        ParseLog();

        //step 2
        FinalCSV();

        System.out.println(new Date());
    }


    /**
     * STEP 1
     * Factory Log Parse,and generate CSV middle files
     * CSV middle file may contents the same SN,so we need STEP2
     */
    private static void ParseLog() throws InterruptedException {
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
            dataPaths.add("/home/lee/文档/testlog/testlog" + i + ".txt");
        }
        taskSize = PIDs.length * dataPaths.size();

        latch = new CountDownLatch(taskSize);
        int trueCount = 0;
        for (String pid : PIDs) {
            for (String dataPath : dataPaths) {
                trueCount++;
                ThreadUtils.runCompletionTask(new Callable<String[]>() {
                    @Override
                    public String[] call() throws Exception {
                        String[] res = null;
                        String filePath = null;
                        try {
                            List<String> snList = new ArrayList<>();
                            filePath = "/home/lee/文档/out/PID-" + pid + "-" + new Date() + "-" + System.currentTimeMillis() + ".csv";
                            File file = new File(filePath);
                            if (!file.exists()) {
                                file.createNewFile();
                            }
                            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

                            String data;
                            String oldSN = null;
                            PQData pqData = null;
                            Set<String> invalidCount = new HashSet<>();

                            //第一遍按顺序读取数据，记录异常数据 SN
                            //异常原因是因为 SN 数据不连贯
                            BufferedReader reader = new BufferedReader(new FileReader(dataPath));
                            while ((data = reader.readLine()) != null) {
                                String[] logs = data.split("\t");
                                String sn ;
                                if (logs.length > 2) {
                                    sn = logs[1].replace("\"", "");
                                }else {
                                    continue;
                                }
                                if (oldSN == null || !oldSN.equals(sn)) {
                                    if (pqData != null && !snList.contains(pqData.getSN())) {
                                        if (pqData.containNull()) {
                                            invalidCount.add(pqData.getSN());
                                        } else {
                                            //判断是否为需要提取的产品数据 PID
                                            if (pqData.getSN().startsWith(pid)) {
                                                writer.append(pqData.toCSVNew());
                                                snList.add(pqData.getSN());
                                            }
                                        }

                                    }
                                    //System.out.println("new SN,we create new PQData");
                                    pqData = new PQData();
                                    pqData.setSN(sn);
                                } else {
                                    //System.out.println("old sn " + sn);
                                }
                                pqDataParse(pqData, data);
                                oldSN = sn;
                                //System.out.println(pqData.toString());
                            }
                            reader.close();

                            //System.out.println("invalid sn list = " + Arrays.toString(invalidCount.toArray()));
                            //System.out.println("invalid sn count = " + invalidCount.size());

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
                                    if (!sn.matches("[0-9]{13}")) {
                                        continue;
                                    }
                                    if (data.contains(sn.substring(5))) {
                                        //System.out.println("find fixed Data");
                                        PQData temp = fixedMap.get(sn);
                                        if (temp != null) {
                                            pqDataParse(temp, data);
                                        }
                                        break;
                                    }
                                }
                            }

                            for (String sn : invalidCount) {
                                if (sn.startsWith(pid)) {
                                    writer.append(fixedMap.get(sn).toCSVNew());
                                    //System.out.println("fixed sn PQ data = " + fixedMap.get(sn).toCSVNew());
                                }
                            }

                            reader.close();
                            writer.flush();
                            writer.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("ops IO error");
                        } finally {
                            latch.countDown();
                        }
                        return new String[]{pid, filePath};
                    }
                });
                Thread.sleep(20);
            }
        }
        System.out.println("total Task Size = " + taskSize);
        System.out.println("true Task Count = " + trueCount);
        latch.await();
    }

    private static void FinalCSV() throws InterruptedException, ExecutionException {
        for (int i = 0; i < taskSize; i++) {
            Future<String[]> task = ThreadUtils.getCompletionService().take();
            String[] res = task.get();
            System.out.println("FinalCSV task Get = " + Arrays.toString(res));
            if (mapPidPath.containsKey(res[0])) {
                mapPidPath.get(res[0]).add(res[1]);
            } else {
                List<String> list = new ArrayList<>();
                list.add(res[1]);
                mapPidPath.put(res[0], list);
            }
        }
        ThreadUtils.runTaskOnBack(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String key : mapPidPath.keySet()) {
                        List<String> filePaths = mapPidPath.get(key);
                        //parse CSV middle file,可能存在重复的 SN
                        Map<String, PQData> result = new HashMap<>();
                        for (String filePath : filePaths) {
                            BufferedReader reader = new BufferedReader(new FileReader(filePath));
                            try {
                                String data;
                                while ((data = reader.readLine()) != null) {
                                    parseCSV(result, data);
                                }
                            } finally {
                                reader.close();
                            }
                        }
                        // finally out final CSV final
                        File file = new File("/home/lee/文档/out/Final-" + key + "-" + new Date() + ".csv");
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                        System.out.println("total data is " + result.values().size());
                        //写入 CSV 标题
                        writer.append("SN,N_RGain,C_RGain,W_RGain,N_GGain,C_GGain,W_GGain,N_BGain,C_BGain,W_BGain,W_ROffset,W_GOffset,W_BOffset,C_ROffset,C_GOffset,C_BOffset,N_ROffset,N_GOffset,N_BOffset,TimeStamp\n");
                        for (PQData value : result.values()) {
                            if (value.containNull()) {
                                continue;
                            }
                            writer.write(value.toCSVNew());
                        }
                        writer.flush();
                        writer.close();
                    }

                    for (List<String> value : mapPidPath.values()) {
                        for (String path : value) {
                            File file = new File(path);
                            file.delete();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    private static synchronized void pqDataParse(PQData pqData, String data) {
        String value = parseData(data);
        if (data.contains("读取标准色温白平衡红色增益")) {
            pqData.setNormalGainRed(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取冷色色温白平衡红色增益")) {
            pqData.setCoolGainRed(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取暖色色温白平衡红色增益")) {
            pqData.setWarmGainRed(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取标准色温白平衡绿色增益")) {
            pqData.setNormalGainGreen(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取冷色色温白平衡绿色增益")) {
            pqData.setCoolGainGreen(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取暖色色温白平衡绿色增益")) {
            pqData.setWarmGainGreen(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取标准色温白平衡蓝色增益")) {
            pqData.setNormalGainBlue(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取冷色色温白平衡蓝色增益")) {
            pqData.setCoolGainBlue(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取暖色色温白平衡蓝色增益")) {
            pqData.setWarmGainBlue(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取标准色温白平衡红色偏移")) {
            pqData.setNormalOffRed(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取冷色色温白平衡红色偏移")) {
            pqData.setCoolOffRed(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取暖色色温白平衡红色偏移")) {
            pqData.setWarmOffRed(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取标准色温白平衡绿色偏移")) {
            pqData.setNormalOffGreen(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取冷色色温白平衡绿色偏移")) {
            pqData.setCoolOffGreen(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取暖色色温白平衡绿色偏移")) {
            pqData.setWarmOffGreen(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取标准色温白平衡蓝色偏移")) {
            pqData.setNormalOffBlue(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取冷色色温白平衡蓝色偏移")) {
            pqData.setCoolOffBlue(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
        if (data.contains("读取暖色色温白平衡蓝色偏移")) {
            pqData.setWarmOffBlue(value);
            pqData.setTimeStamp(parseTimeStamp(data));
        }
    }

    private static synchronized String parseData(String data) {
        String[] logs = data.split("\t");
        String res = null;
        //String res = data.substring(69, 73);
        if (logs.length > 6) {
            res = logs[6];
            if (res.contains("\"")) res = res.replace("\"", "");
            //System.out.println("parse data " + res);
        }
        return res;
    }

    private static synchronized String parseTimeStamp(String data) {
        String res = null;
        String[] params = data.split("\t");
        if (params.length > 7) {
            res = params[7];
            if (res.contains("\"")) res = res.replace("\"", "");
            //System.out.println("parse data " + res);
        }
        return res;
    }


    private static synchronized void parseCSV(Map<String, PQData> result, String data) {
        String[] params = data.split(",");
        if (params.length != 20) {
            //System.out.println("PQ data is illegal content=" + Arrays.toString(params));
            //System.out.println("PQ data len is illegal " + params.length);
            return;
        }
        if (data.contains("null")) {
            return;
        }
        if (result.containsKey(params[0])) {
            String oldTime = result.get(params[0]).getTimeStamp();
            try {
                Date oldD = new Date(oldTime);
                Date newD = new Date(params[19]);
                if (oldD.before(newD)) {
                    PQData pd = new PQData(
                            params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7],
                            params[8], params[9], params[10], params[11], params[12], params[13], params[14], params[15],
                            params[16], params[17], params[18], params[19]
                    );
                    result.put(params[0], pd);
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                result.get(params[0]).setTimeStamp(params[19]);
            }

        } else {
            PQData pd = new PQData(
                    params[0], params[1], params[2], params[3], params[4], params[5], params[6], params[7],
                    params[8], params[9], params[10], params[11], params[12], params[13], params[14], params[15],
                    params[16], params[17], params[18], params[19]
            );
            result.put(params[0], pd);
        }
    }
}
