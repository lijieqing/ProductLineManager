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
        const val CMD_LIST: String =
                "CMD=1409,FUNC=R\n" +
                        "CMD=1411,FUNC=R"
    }
}

fun main() {
//    var count = 0
//    while (count < 1) {
//        PLMContext.initServer()
//        CommandRxWrapper.addGlobalRXListener(TP_CMD_Test())
//        strParse()
//        count++
//    }
    strSplit("1112/")
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

fun strSplit(cmd: String) {
    val cmdInfo = cmd.split("/")
    if (cmdInfo.isNotEmpty()) {
        if (cmdInfo.size == 1 && cmdInfo[0].isNotEmpty()) {
            print("auto Run cmd id=${cmdInfo[0]}")
        } else if (cmdInfo.size == 2 && cmdInfo[0].isNotEmpty()) {
            print("auto Run cmd id=${cmdInfo[0]}, param=${cmdInfo[1]}")
        }
    }
}