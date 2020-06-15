package hua.lee.plm.bean;

public class PQData {
    private String SN;
    private String NormalGainRed;
    private String NormalGainGreen;
    private String NormalGainBlue;
    private String CoolGainRed;
    private String CoolGainGreen;
    private String CoolGainBlue;
    private String WarmGainRed;
    private String WarmGainGreen;
    private String WarmGainBlue;

    private String NormalOffRed;
    private String NormalOffGreen;
    private String NormalOffBlue;
    private String CoolOffRed;
    private String CoolOffGreen;
    private String CoolOffBlue;
    private String WarmOffRed;
    private String WarmOffGreen;
    private String WarmOffBlue;
    private String TimeStamp;

    public PQData(String SN, String normalGainRed, String normalGainGreen, String normalGainBlue, String coolGainRed, String coolGainGreen, String coolGainBlue, String warmGainRed, String warmGainGreen, String warmGainBlue, String normalOffRed, String normalOffGreen, String normalOffBlue, String coolOffRed, String coolOffGreen, String coolOffBlue, String warmOffRed, String warmOffGreen, String warmOffBlue, String timeStamp) {
        this.SN = SN;
        NormalGainRed = normalGainRed;
        NormalGainGreen = normalGainGreen;
        NormalGainBlue = normalGainBlue;
        CoolGainRed = coolGainRed;
        CoolGainGreen = coolGainGreen;
        CoolGainBlue = coolGainBlue;
        WarmGainRed = warmGainRed;
        WarmGainGreen = warmGainGreen;
        WarmGainBlue = warmGainBlue;
        NormalOffRed = normalOffRed;
        NormalOffGreen = normalOffGreen;
        NormalOffBlue = normalOffBlue;
        CoolOffRed = coolOffRed;
        CoolOffGreen = coolOffGreen;
        CoolOffBlue = coolOffBlue;
        WarmOffRed = warmOffRed;
        WarmOffGreen = warmOffGreen;
        WarmOffBlue = warmOffBlue;
        TimeStamp = timeStamp;
    }

    public PQData() {
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String sn) {
        if (sn.contains("/")) sn = sn.replace("/", "");
        this.SN = sn;
    }

    public String getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        TimeStamp = timeStamp;
    }

    public String getNormalGainRed() {
        return NormalGainRed;
    }

    public void setNormalGainRed(String normalGainRed) {
        NormalGainRed = normalGainRed;
    }

    public String getNormalGainGreen() {
        return NormalGainGreen;
    }

    public void setNormalGainGreen(String normalGainGreen) {
        NormalGainGreen = normalGainGreen;
    }

    public String getNormalGainBlue() {
        return NormalGainBlue;
    }

    public void setNormalGainBlue(String normalGainBlue) {
        NormalGainBlue = normalGainBlue;
    }

    public String getCoolGainRed() {
        return CoolGainRed;
    }

    public void setCoolGainRed(String coolGainRed) {
        CoolGainRed = coolGainRed;
    }

    public String getCoolGainGreen() {
        return CoolGainGreen;
    }

    public void setCoolGainGreen(String coolGainGreen) {
        CoolGainGreen = coolGainGreen;
    }

    public String getCoolGainBlue() {
        return CoolGainBlue;
    }

    public void setCoolGainBlue(String coolGainBlue) {
        CoolGainBlue = coolGainBlue;
    }

    public String getWarmGainRed() {
        return WarmGainRed;
    }

    public void setWarmGainRed(String warmGainRed) {
        WarmGainRed = warmGainRed;
    }

    public String getWarmGainGreen() {
        return WarmGainGreen;
    }

    public void setWarmGainGreen(String warmGainGreen) {
        WarmGainGreen = warmGainGreen;
    }

    public String getWarmGainBlue() {
        return WarmGainBlue;
    }

    public void setWarmGainBlue(String warmGainBlue) {
        WarmGainBlue = warmGainBlue;
    }

    public String getNormalOffRed() {
        return NormalOffRed;
    }

    public void setNormalOffRed(String normalOffRed) {
        NormalOffRed = normalOffRed;
    }

    public String getNormalOffGreen() {
        return NormalOffGreen;
    }

    public void setNormalOffGreen(String normalOffGreen) {
        NormalOffGreen = normalOffGreen;
    }

    public String getNormalOffBlue() {
        return NormalOffBlue;
    }

    public void setNormalOffBlue(String normalOffBlue) {
        NormalOffBlue = normalOffBlue;
    }

    public String getCoolOffRed() {
        return CoolOffRed;
    }

    public void setCoolOffRed(String coolOffRed) {
        CoolOffRed = coolOffRed;
    }

    public String getCoolOffGreen() {
        return CoolOffGreen;
    }

    public void setCoolOffGreen(String coolOffGreen) {
        CoolOffGreen = coolOffGreen;
    }

    public String getCoolOffBlue() {
        return CoolOffBlue;
    }

    public void setCoolOffBlue(String coolOffBlue) {
        CoolOffBlue = coolOffBlue;
    }

    public String getWarmOffRed() {
        return WarmOffRed;
    }

    public void setWarmOffRed(String warmOffRed) {
        WarmOffRed = warmOffRed;
    }

    public String getWarmOffGreen() {
        return WarmOffGreen;
    }

    public void setWarmOffGreen(String warmOffGreen) {
        WarmOffGreen = warmOffGreen;
    }

    public String getWarmOffBlue() {
        return WarmOffBlue;
    }

    public void setWarmOffBlue(String warmOffBlue) {
        WarmOffBlue = warmOffBlue;
    }

    @Override
    public String toString() {
        return "PQData{" +
                "SN='" + SN + '\'' +
                ", NormalGainRed='" + NormalGainRed + '\'' +
                ", NormalGainGreen='" + NormalGainGreen + '\'' +
                ", NormalGainBlue='" + NormalGainBlue + '\'' +
                ", CoolGainRed='" + CoolGainRed + '\'' +
                ", CoolGainGreen='" + CoolGainGreen + '\'' +
                ", CoolGainBlue='" + CoolGainBlue + '\'' +
                ", WarmGainRed='" + WarmGainRed + '\'' +
                ", WarmGainGreen='" + WarmGainGreen + '\'' +
                ", WarmGainBlue='" + WarmGainBlue + '\'' +
                ", NormalOffRed='" + NormalOffRed + '\'' +
                ", NormalOffGreen='" + NormalOffGreen + '\'' +
                ", NormalOffBlue='" + NormalOffBlue + '\'' +
                ", CoolOffRed='" + CoolOffRed + '\'' +
                ", CoolOffGreen='" + CoolOffGreen + '\'' +
                ", CoolOffBlue='" + CoolOffBlue + '\'' +
                ", WarmOffRed='" + WarmOffRed + '\'' +
                ", WarmOffGreen='" + WarmOffGreen + '\'' +
                ", WarmOffBlue='" + WarmOffBlue + '\'' +
                '}';
    }

//    public String toCSV() {
//        return SN + "," +
//                CoolGainRed + "," + CoolGainGreen + "," + CoolGainBlue + "," +
//                CoolOffRed + "," + CoolOffGreen + "," + CoolOffBlue + "," +
//                NormalGainRed + "," + NormalGainGreen + "," + NormalGainBlue + "," +
//                NormalOffRed + "," + NormalOffGreen + "," + NormalOffBlue + "," +
//                WarmGainRed + "," + WarmGainGreen + "," + WarmGainBlue + "," +
//                WarmOffRed + "," + WarmOffGreen + "," + WarmOffBlue + "\n";
//    }

    public String toCSVNew() {
        return SN + "," +
                dataCheck(NormalGainRed) + "," + dataCheck(CoolGainRed) + "," + dataCheck(WarmGainRed) + "," +
                dataCheck(NormalGainGreen) + "," + dataCheck(CoolGainGreen) + "," + dataCheck(WarmGainGreen) + "," +
                dataCheck(NormalGainBlue) + "," + dataCheck(CoolGainBlue) + "," + dataCheck(WarmGainBlue) + "," +
                dataCheck(WarmOffRed) + "," + dataCheck(WarmOffGreen) + "," + dataCheck(WarmOffBlue) + "," +
                dataCheck(CoolOffRed) + "," + dataCheck(CoolOffGreen) + "," + dataCheck(CoolOffBlue) + "," +
                dataCheck(NormalOffRed) + "," + dataCheck(NormalOffGreen) + "," + dataCheck(NormalOffBlue) + "," + TimeStamp+ "\n";
    }

    public boolean containNull() {
        return (CoolGainRed == null ||
                CoolGainGreen == null ||
                CoolGainBlue == null ||
                CoolOffRed == null ||
                CoolOffGreen == null ||
                CoolOffBlue == null ||
                NormalGainRed == null ||
                NormalGainGreen == null ||
                NormalGainBlue == null ||
                NormalOffRed == null ||
                NormalOffGreen == null ||
                NormalOffBlue == null ||
                WarmGainRed == null ||
                WarmGainGreen == null ||
                WarmGainBlue == null ||
                WarmOffRed == null ||
                WarmOffGreen == null ||
                WarmOffBlue == null ||
                TimeStamp == null ||
                SN == null || !SN.matches("[0-9]{13}")
        );
    }
    private String dataCheck(String field){
        if (field.startsWith("3")){
            int len = field.length();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) {
                if (i%2 == 1){
                    sb.append(field.charAt(i));
                }
            }
            return  sb.toString();
        }else {
            return field;
        }
    }
}
