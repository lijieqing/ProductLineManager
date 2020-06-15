package hua.lee.plm.fm;

import java.io.*;

public class GsensorDataParse {
    public static void main(String[] args) throws IOException {
        splitData2NewFile();
    }
    private static void splitData2NewFile() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("/Users/lijie/Desktop/05Data"));
        BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/lijie/Desktop/05DataOut"));
        String data;
        int count = 0;
        while ((data=reader.readLine()) != null){
            String[] sensors = data.split(",");
            if (sensors.length == 3){
                writer.append(String.valueOf(count));
                writer.append("\t");
                writer.append(sensors[0]);
                writer.append("\t");
                writer.append(sensors[1]);
                writer.append("\t");
                writer.append(sensors[2]);
                writer.append("\n");
                writer.flush();
                count++;
            }
        }
        writer.close();
        System.out.println("we parse data times = " + count);
    }
}
