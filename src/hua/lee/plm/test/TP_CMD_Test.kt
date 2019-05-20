package hua.lee.plm.test

import hua.lee.plm.base.GlobalCommandReceiveListener
import hua.lee.plm.base.PLMContext
import hua.lee.plm.bean.CommandRxWrapper
import hua.lee.plm.bean.CommandTxWrapper
import java.util.*

class TP_CMD_Test : GlobalCommandReceiveListener {
    override fun onRXWrapperReceived(cmdID: String?, data: ByteArray?) {
        //To change body of created functions use File | Settings | File Templates.
//        var res = ""
//        if (data != null) {
//            res = String(data)
//        }
        println("CMD=$cmdID, data=${data!!.size}")
        Thread.sleep(1000)
        RECEIVED = true
    }

    companion object {
        var RECEIVED = false;
        //        const val CMD_LIST: String =
//                "CMD=1470,FUNC=W\n" +
//                        "CMD=1471,FUNC=R\n" +
//                        "CMD=1474,FUNC=W\n" +
//                        "CMD=1475,FUNC=R\n" +
//                        "CMD=14A1,FUNC=W\n" +
//                        "CMD=14A2,FUNC=R\n" +
//                        "CMD=1472,FUNC=W\n" +
//                        "CMD=1473,FUNC=R\n" +
//                        "CMD=1476,FUNC=W\n" +
//                        "CMD=1477,FUNC=R\n" +
//                        "CMD=1478,FUNC=W\n" +
//                        "CMD=1479,FUNC=R\n" +
//                        "CMD=147A,FUNC=W\n" +
//                        "CMD=147B,FUNC=R\n" +
//                        "CMD=1409,FUNC=R\n" +
//                        "CMD=1411,FUNC=R\n" +
//                        "CMD=1457,FUNC=R\n" +
//                        "CMD=1456,FUNC=R\n" +
//                        "CMD=1272,FUNC=W\n" +
//                        "CMD=1463,FUNC=R\n" +
//                        "CMD=1139,FUNC=R\n" +
//                        "CMD=14B0,FUNC=R\n" +
//                        "CMD=14B1,FUNC=R\n" +
//                        "CMD=14B2,FUNC=R\n" +
//                        "CMD=14b6,FUNC=W\n" +
//                        "CMD=1416,FUNC=R\n" +
//                        "CMD=1418,FUNC=R\n" +
//                        "CMD=14B4,FUNC=R\n" +
//                        "CMD=14B9,FUNC=R"
        const val CMD_LIST: String =
                "CMD=1409,FUNC=R\n" +
                        "CMD=1411,FUNC=R"
    }
}

fun main() {
    var count = 0
    while (count < 1) {
        PLMContext.initServer()
        CommandRxWrapper.addGlobalRXListener(TP_CMD_Test())
        strParse()
        count++
    }

}

fun strParse() {
    var cmdItems = TP_CMD_Test.CMD_LIST.split("\n")
    cmdItems.forEach {
        var cmdItem = it.split(",")

        var cmd = cmdItem[0].replace("CMD=", "")
        var prop = cmdItem[1].replace("FUNC=", "")

        var param: String = ""
        if (prop == "W") {
            param = cmd + "Test"
        }

        var tx = CommandTxWrapper.initTX(cmd, param, null, CommandTxWrapper.DATA_STRING, PLMContext.TYPE_FUNC.toInt())
        TP_CMD_Test.RECEIVED = false
        tx.send()


        while (TP_CMD_Test.RECEIVED) {

        }
    }
}