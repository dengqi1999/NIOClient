package com.dq.client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Chart extends JFrame {

	private JPanel contentPane;
	DefaultListModel<DisplayData> listModel;
	DefaultListModel<User> listModelUser;
	Client client;
	JList<User> listUser;
	User talkUser;
	JLabel nameLabel;
	JButton logButton;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		final Client client=new Client();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					//加载主页面
					Chart frame = new Chart();
					//设置依赖
					frame.client=client;
					frame.setResizable(false); 
					frame.setVisible(true);
					//设置依赖
					client.setChart(frame);
					//启动监听线程
					client.launch();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public Chart() {
		setBounds(250, 100, 825, 600);
		getContentPane().setLayout(null);
		//消息列表
		JPanel panel1 = new JPanel();
		panel1.setBounds(176, 46, 617, 431);
		Dimension dims = new Dimension(617, 464);
		listModel = new DefaultListModel<DisplayData>();
		JList<DisplayData> list = new JList<DisplayData>(listModel);
		list.setCellRenderer(new CellRender());
		JScrollPane scrollPane = new JScrollPane(list);
		scrollPane.setPreferredSize(dims);
		panel1.add(scrollPane);
		getContentPane().add(panel1);
		
		//消息输入框
		JPanel panel2=new JPanel();
		Dimension dimt = new Dimension(523, 30);
		panel2.setBounds(176, 490, 523, 50);
		final JTextField textName = new JTextField();
		textName.setPreferredSize(dimt);
		panel2.add(textName);
		getContentPane().add(panel2);
		
		//发送按钮
		JPanel panel3=new JPanel();
		panel3.setBounds(713, 490, 80, 50);
		JButton jb1 = new JButton("发送");
		jb1.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				String text=textName.getText();
				textName.setText("");
				if(talkUser==null)return ;
				if(talkUser.isGroup){
					client.sendPeople(text,talkUser.getId());
				}else{
					client.sendSingle(text,talkUser.getId());
				}
				DisplayData displayData=new DisplayData();
				displayData.setIsLeft(false);
				displayData.setData(text);
				listModel.addElement(displayData);
				talkUser.addMsg(displayData);
			}
		});
		panel3.add(jb1);
		jb1.setPreferredSize(dimt);
		getContentPane().add(panel3);
		
		
		//在线列表
		JPanel panel4 = new JPanel();
		Font font4 = new Font("宋体", Font.BOLD, 20);
		panel4.setBounds(14, 46, 154, 494);
		listModelUser = new DefaultListModel<User>();
		listUser = new JList<User>(listModelUser);
		listUser.setCellRenderer(new CellRenderUser().setChart(this));
		listUser.setFont(font4);

		JScrollPane scrollPaneUser = new JScrollPane(listUser);
		Dimension dimU = new Dimension(154, 527);
		scrollPaneUser.setPreferredSize(dimU);
		panel4.add(scrollPaneUser);
		getContentPane().add(panel4);

		JLabel lblNewLabel = new JLabel("NIO聊天工具");
		Font font5 = new Font("宋体", Font.BOLD, 25);
		lblNewLabel.setBounds(14, 13, 211, 34);
		lblNewLabel.setFont(font5);
		getContentPane().add(lblNewLabel);

		nameLabel = new JLabel("");
		Font font6 = new Font("宋体", Font.BOLD, 20);
		nameLabel.setFont(font6);
		nameLabel.setBounds(589, 13, 110, 34);
		getContentPane().add(nameLabel);

		logButton = new JButton("登录");
		logButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(client.mine==null){
					client.login();
				}else{
					client.logout();
					System.exit(0);
				}
			}
		});
		logButton.setBounds(712, 17, 81, 27);
		getContentPane().add(logButton);
	}
}
