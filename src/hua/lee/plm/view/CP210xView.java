package hua.lee.plm.view;

import hua.lee.plm.base.GlobalCommandReceiveListener;
import hua.lee.plm.base.PLMContext;
import hua.lee.plm.bean.CommandRxWrapper;
import hua.lee.plm.bean.CommandTxWrapper;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;

/**
 * @author lijie
 * @create 2019-04-10 10:38
 **/
public class CP210xView implements GlobalCommandReceiveListener {
    private JEditorPane cmdID;
    private JEditorPane cmdValue;
    private JButton btnSend;
    private JButton btnInit;
    private JTextArea textInfo;
    private JPanel rootView;
    private JCheckBox cbPath;
    private JCheckBox cbType;
    private JButton btnStop;

    public void init() {
        btnInit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PLMContext.initServer();
            }
        });
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PLMContext.closeServer();
                //退出应用
                System.exit(0);
            }
        });
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PLMContext.d("ACTION",e.getID()+e.getActionCommand());
                String cmd = cmdID.getText();
                String data = cmdValue.getText();
                boolean stringType = cbType.isSelected();
                boolean fileType = cbPath.isSelected();
                CommandTxWrapper tx = null;
                if (stringType) {
                    if (fileType) {
                        tx = CommandTxWrapper.initTX(cmd, data, null, CommandTxWrapper.DATA_FILE, PLMContext.TYPE_FUNC);
                    } else {
                        tx = CommandTxWrapper.initTX(cmd, data, null, CommandTxWrapper.DATA_STRING, PLMContext.TYPE_FUNC);
                    }
                } else {

                }
                if (tx != null) {
                    tx.send();
                }
                textInfo.setText("");
            }
        });

        CommandRxWrapper.addGlobalRXListener(this);

    }

    public JPanel panel() {
        return rootView;
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        CP210xView cp210xView = new CP210xView();
        cp210xView.init();

        jFrame.setContentPane(cp210xView.panel());
        jFrame.pack();
        jFrame.setVisible(true);
    }

    @Override
    public void onRXWrapperReceived(String cmdID, byte[] data) {
        StringBuilder sb = new StringBuilder();
        sb.append(cmdID).append(":bytes = ").append(Arrays.toString(data))
                .append("\n")
                .append(cmdID).append(":str = ").append(new String(data));
        textInfo.setText(sb.toString());
    }
}
