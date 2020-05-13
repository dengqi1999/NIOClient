package com.dq.client;

import javax.swing.*;
import java.awt.*;

public class CellRender extends JLabel implements ListCellRenderer {


	private static final long serialVersionUID = 1L;

	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
			boolean cellHasFocus) {
		DisplayData data=(DisplayData)value;
		if(data.getIsLeft()) {
			setHorizontalAlignment(JLabel.LEFT);
			setText(data.name+":"+data.getData());
		}else {
			setHorizontalAlignment(JLabel.RIGHT);
			setText(data.getData()+":我");
		}
		Font font = new Font("宋体", Font.BOLD, 20);
		setFont(font);
        return this;
	}
 
    
}
