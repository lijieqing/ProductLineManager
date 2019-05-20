package hua.lee.plm.view;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;

/**
 * @author lijie
 * @create 2019-04-24 18:59
 **/
public class CMDView {
    private JPanel rootView;
    private JTable cmdTable;

    public void init() {
        TableColumn c1 = new TableColumn();
        TableColumn c2 = new TableColumn();
        TableColumn c3 = new TableColumn();
        c1.setHeaderValue("1111");
        c2.setHeaderValue("2222");
        c3.setHeaderValue("3333");
        cmdTable.addColumn(c1);
        cmdTable.addColumn(c2);
        cmdTable.addColumn(c3);
    }

    public static void main(String[] args) {
        JFrame jFrame = new JFrame();
        CMDView cmdView = new CMDView();
        // 表头（列名）
        Object[] columnNames = {"姓名", "语文", "数学", "英语", "总分"};

        // 表格所有行数据
        Object[][] rowData = {
                {"张三", 80, 80, 80, 240},
                {"John", 70, 80, 90, 240},
                {"Sue", 70, 70, 70, 210},
                {"Jane", 80, 70, 60, 210},
                {"Joe", 80, 70, 60, 210}
        };

        // 创建一个表格，指定 所有行数据 和 表头
        JTable table = new JTable(rowData, columnNames);

        // 把 表头 添加到容器顶部（使用普通的中间容器添加表格时，表头 和 内容 需要分开添加）
        cmdView.rootView.add(table.getTableHeader(), BorderLayout.NORTH);
        // 把 表格内容 添加到容器中心
        cmdView.rootView.add(table, BorderLayout.CENTER);

        jFrame.setContentPane(cmdView.rootView);
        jFrame.pack();
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);
    }
}
