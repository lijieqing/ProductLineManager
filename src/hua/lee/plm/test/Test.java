package hua.lee.plm.test;


import hua.lee.plm.base.DataTypeUtil;
import hua.lee.plm.base.RxDataCallback;

import java.io.*;
import java.util.*;

import static hua.lee.plm.base.DataTypeUtil.*;

/**
 * test
 *
 * @author lijie
 * @create 2018-10-30 15:30
 **/
public class Test implements RxDataCallback {


    public static void main(String[] args) {

//        HashMap<String,Command> d = new HashMap<>();
//        d.put("Aa",new Command(new byte[2]));
//        d.put("BB",new Command(new byte[2]));
    }

    @Override
    public void notifyDataReceived(String cmdID, byte[] data) {
        System.out.println("ID=" + cmdID + " | recv data \n :: " + Arrays.toString(data));
    }

    @org.junit.Test
    public void testHdcp() throws IOException {

    }

    public void setImgPath(String hdcp22Path) throws IOException {
        byte[] readBuf;
        File file = new File(hdcp22Path);
        AmlResImgHead imgHead;

        if (!file.exists()) {
            System.out.println("no file exist");
            return;
        }
        InputStream is = new FileInputStream(file);
        int len = is.available();
        if (len > 0) {
            readBuf = new byte[len];
            is.read(readBuf);

            imgHead = new AmlResImgHead(readBuf);
            //imgHead.showInfo();
            for (int i = 0; i < imgHead.itemHeadList.size(); i++) {
                AmlResItemHead item = imgHead.itemHeadList.get(i);
                item.itemSplit(readBuf);
            }
        } else {
            System.out.println("file read error");
        }
    }

    /**
     * key 文件item描述类
     */
    private class AmlResItemHead {
        /**
         * key item 文件大小
         */
        byte[] totalSz = new byte[4];
        /**
         * key item 数据长度
         */
        byte[] dataSz = new byte[4];
        /**
         * key item 在 key 文件中的偏移位置
         */
        byte[] dataOffset = new byte[4];
        /**
         * key item 类型
         */
        byte[] type = new byte[1];
        /**
         * key item compression type
         */
        byte[] comp = new byte[1];
        /**
         * reserve
         */
        byte[] reserv = new byte[2];
        /**
         * key item name
         */
        byte[] name = new byte[32];

        public AmlResItemHead(byte[] datas, int startPos) {
            initItem(datas, startPos);
        }

        /**
         * 初始化
         * @param datas 完整的 key 文件字节数据
         * @param startPos  key item 起始位置
         */
        private void initItem(byte[] datas, int startPos) {
            int curPos = startPos;
            curPos = arrayCopy(datas, totalSz, curPos);
            curPos = arrayCopy(datas, dataSz, curPos);
            curPos = arrayCopy(datas, dataOffset, curPos);
            curPos = arrayCopy(datas, type, curPos);
            curPos = arrayCopy(datas, comp, curPos);
            curPos = arrayCopy(datas, reserv, curPos);
            curPos = arrayCopy(datas, name, curPos);
        }

        void show() {
            System.out.println("total Size == " + Arrays.toString(totalSz));
            System.out.println("data size == " + Arrays.toString(dataSz));
            System.out.println("data offset == " + Arrays.toString(dataOffset));
            System.out.println("type == " + Arrays.toString(type));
            System.out.println("comp == " + Arrays.toString(comp));
            System.out.println("reserv == " + Arrays.toString(reserv));
            System.out.println("name == " + Arrays.toString(name) + new String(name));
        }

        String getName() {
            return new String(name);
        }

        int getDataSize() {
            return DataTypeUtil.bytesToInt(dataSz, 0);
        }

        int getTotalSize() {
            return DataTypeUtil.bytesToInt(totalSz, 0);
        }

        int getDataOffset() {
            return DataTypeUtil.bytesToInt(dataOffset, 0);
        }

        /**
         * key item 分类写入
         * @param datas 完整的 key 文件字节数据
         */
        void itemSplit(byte[] datas) {
            byte[] buffer = new byte[getDataSize() + 4];
            String keyName = null;
            int off = getDataOffset();
            int size = getDataSize();
            if ((off + size) > datas.length) {
                System.out.println("item split error, off + size > data len");
            }

            String temp = getName();
            System.arraycopy(datas, off, buffer, 0, size);

            if (temp.contains("hdcp22_rx_private")) {
                buffer = hdcp2DataEncryption(getDataSize(), buffer);
                keyName = "hdcp22_rx_private";
            } else if (temp.contains("hdcp2_rx")) {
                keyName = "hdcp2_rx";
            } else if (temp.contains("extractedKey")) {
                keyName = "hdcp22_rx_fw";
            }

            System.out.println(keyName + " | " + Arrays.toString(buffer));
            // TODO: 2019-02-11 write key here
        }

        private byte generateDataChange(byte input) {
            byte result = 0;
            for (int i = 0; i < 8; i++) {
                if ((input & (1 << i)) != 0) {
                    result |= (1 << (7 - i));
                } else {
                    result &= ~(1 << (7 - i));
                }
            }
            return result;
        }

        private byte[] hdcp2DataEncryption(int len, byte[] in) {
            for (int i = 0; i < len; i++) {
                in[i] = generateDataChange(in[i]);
            }
            return in;
        }
    }

    /**
     * hdcp 22 key 文件头描述类
     */
    private class AmlResImgHead {
        /**
         * CRC 校验值
         */
        byte[] crc = new byte[4];
        /**
         * KEY 文件版本
         */
        byte[] version = new byte[4];
        /**
         * 魔数
         */
        byte[] magic = new byte[8];
        /**
         * key 文件大小
         */
        byte[] imgSz = new byte[4];
        /**
         * key item 数量
         */
        byte[] imgItemNum = new byte[4];
        /**
         * key item 对象集合
         */
        List<AmlResItemHead> itemHeadList = new ArrayList<>();

        public AmlResImgHead(byte[] datas) {
            if (datas.length >= 24) {
                initImg(datas);
                initItem(datas);
            } else {
                System.out.println("data len < 24 :: " + datas.length);
            }
        }

        /**
         * 初始化 key 文件头
         *
         * @param datas
         */
        private void initImg(byte[] datas) {
            int curPos = 0;
            curPos = arrayCopy(datas, crc, curPos);
            curPos = arrayCopy(datas, version, curPos);
            curPos = arrayCopy(datas, magic, curPos);
            curPos = arrayCopy(datas, imgSz, curPos);
            curPos = arrayCopy(datas, imgItemNum, curPos);
        }

        /**
         * 初始化 key item
         *
         * @param datas key文件字节数组
         */
        private void initItem(byte[] datas) {
            int itemNum = DataTypeUtil.bytesToInt(imgItemNum, 0);
            int startPos = 24;
            int dataSize = 0;
            int dataOff = 0;
            AmlResItemHead amlItem;
            for (int i = 0; i < itemNum; i++) {
                amlItem = new AmlResItemHead(datas, startPos);
                //amlItem.show();
                dataSize = bytesToInt(amlItem.dataSz, 0);
                dataOff = bytesToInt(amlItem.dataOffset, 0);
                //System.out.println("dataSize==" + dataSize + " || data off==" + dataOff);
                //System.out.println(new String(amlItem.name));
                itemHeadList.add(amlItem);
                startPos += 48;
            }
        }

        /**
         * 打印 key 文件头信息
         */
        void showInfo() {
            System.out.println("CRC " + Arrays.toString(crc) + DataTypeUtil.bytesToInt(crc, 0));
            System.out.println("Version " + Arrays.toString(version) + DataTypeUtil.bytesToInt(version, 0));
            System.out.println("magic " + Arrays.toString(magic) + new String(magic));
            System.out.println("imgSize " + Arrays.toString(imgSz) + DataTypeUtil.bytesToInt(imgSz, 0));
            System.out.println("imgItemNum " + Arrays.toString(imgItemNum) + DataTypeUtil.bytesToInt(imgItemNum, 0));
        }
    }

    //数组拷贝
    static int arrayCopy(byte[] src, byte[] dst, int curPos) {
        System.arraycopy(src, curPos, dst, 0, dst.length);
        curPos = curPos + dst.length;
        return curPos;
    }
}
