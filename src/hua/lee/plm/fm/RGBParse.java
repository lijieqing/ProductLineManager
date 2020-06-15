package hua.lee.plm.fm;

import java.util.Arrays;

public class RGBParse {
    private static String[] arithmetic = new String[]{"+", "-", "*", "/"};
    private static String method = "R-B>0;B-R>4;BP-R>10";

    private static void parseAbnormalTempLogic(String logic) {
        String[] logs = logic.split(";");
        for (String log : logs) {
            if (!log.matches("[RGBP]{1,2}[-+*/][RGBP]{1,2}[><][0-9]{1,2}")) {
                return;
            }
        }
        System.out.println(Arrays.toString(logs));
        boolean[] res = new boolean[logs.length];
        for (int i = 0; i < logs.length; i++) {
            res[i] = parseLogic(logs[i]);
        }

        System.out.println(Arrays.toString(res));
    }

    private static Boolean parseLogic(String logic) {
        String ari = null;
        String inequality = null;
        for (String s : arithmetic) {
            if (logic.contains(s)) {
                ari = s;
                break;
            }
        }
        if (ari == null){
            System.out.println("非法字符");
            return null;
        }
        int ariIndex = logic.indexOf(ari);
        int inequalityIndex = logic.contains(">") ? logic.indexOf(">") : logic.indexOf("<");
        inequality = String.valueOf(logic.charAt(inequalityIndex));

        System.out.println("arithmetic = " + ari);
        System.out.println("inequality = " + inequality);

        String preData = logic.substring(0, ariIndex);
        String afterData = logic.substring(ariIndex + 1, inequalityIndex);
        String expectedData = logic.substring(inequalityIndex + 1);

        System.out.println("preData = " + preData);
        System.out.println("afterData = " + afterData);
        System.out.println("expectedData = " + expectedData);

        return arithmeticMatch(inequality, ari, preData, afterData, expectedData);
    }

    private static boolean arithmeticMatch(String inequality, String ari, String preData, String afterData, String expectData) {
        Integer pre = Integer.parseInt(getData(preData));
        Integer after = Integer.parseInt(getData(afterData));
        int expect = Integer.parseInt(expectData);

        int temp = 0;
        boolean res = false;
        switch (ari) {
            case "+":
                temp = pre + after;
                break;
            case "-":
                temp = pre - after;
                break;
            case "*":
                temp = pre * after;
                break;
            case "/":
                temp = pre / after;
                break;
        }
        res = inequality.equals(">") ? temp > expect : temp < expect;
        return res;
    }

    private static String getData(String name) {
        switch (name) {
            case "R":
                return "10";
            case "G":
                return "5";
            case "B":
                return "6";
            case "BP":
                return "3";
            default:
                return "0";
        }
    }

    public static void main(String[] args) {
        parseAbnormalTempLogic(method);
    }
}
