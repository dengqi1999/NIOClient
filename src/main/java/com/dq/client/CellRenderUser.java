package com.dq.client;

import javax.swing.*;
import java.awt.*;

public class CellRenderUser extends JLabel implements ListCellRenderer{
    Font font = new Font("宋体", Font.BOLD, 15);
    Chart chart;

    public CellRenderUser setChart(Chart chart) {
        this.chart = chart;
        return this;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
        User user=(User)value;
        if(isSelected){
            setText(user.getName()+"聊天中");
            chart.talkUser=user;
            chart.listModel.clear();
            for(DisplayData data:user.getMsg()){
                chart.listModel.addElement(data);
            }
        }else{
            setText(user.getName());
        }
        setFont(font);

        return this;
    }
}
