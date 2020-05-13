package com.dq.client;

import com.alibaba.fastjson.JSONObject;
import com.dq.client.FunctionType;
import com.dq.client.Msg;

import javax.swing.*;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

public class Client {
    public static final int DEFAULT_BUFFER_SIZE = 1024;
    private Selector selector;
    private SocketChannel clientChannel;
    private ByteBuffer buf;
    public User mine;
    private boolean isLogin = false;
    private boolean isConnected = false;
    private ReceiverHandler listener;
    private Charset charset = StandardCharsets.UTF_8;

    Chart chart;

    public void setChart(Chart chart){
        this.chart=chart;
    }

    public Client(){
        initNetWork();
    }
    /**
     * 初始化网络模块
     */
    private void initNetWork() {
        try {
            selector = Selector.open();
            clientChannel = SocketChannel.open(new InetSocketAddress("127.0.0.1", 9000));
            //设置客户端为非阻塞模式
            clientChannel.configureBlocking(false);
            clientChannel.register(selector, SelectionKey.OP_READ);
            buf = ByteBuffer.allocate(DEFAULT_BUFFER_SIZE);
            isConnected = true;
        } catch (ConnectException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载后台接受消息线程
     */
    public void launch() {
        this.listener = new ReceiverHandler();
        new Thread(listener).start();
    }

    public void login() {
        String username = JOptionPane.showInputDialog("请输入用户Id");
        String password = JOptionPane.showInputDialog("请输入密码");
        Msg msg=new Msg();
        msg.setCode(FunctionType.LOGIN.getCode());
        msg.setFromId(Integer.valueOf(username));
        msg.setMsg(password);
        sendTest(JSONObject.toJSONString(msg));
        this.mine = new User();
        mine.setId(Integer.valueOf(username));
        mine.setPassword(password);
    }
    public void logout() {
        System.out.println("客户端发送下线请求");
        Msg msg=new Msg();
        msg.setCode(FunctionType.QUIT.getCode());
        msg.setFromId(mine.getId());
        sendTest(JSONObject.toJSONString(msg));
    }

    /**
     * 实际发送消息方法，实现粘包和断包处理
     * @param content
     */
    public void sendTest(String content){
        String start="ab";//占位符
        try {
            int length=content.getBytes("UTF-8").length;
            content=start+content;
            byte[]bytes=content.getBytes("UTF-8");
            bytes[0]= (byte) (length&0xff);
            bytes[1]= (byte) ((length&0xffff)>>8);
            clientChannel.write(ByteBuffer.wrap(bytes));
        } catch (IOException  e) {
            e.printStackTrace();
        }
    }
    /**
     * 发送单对单信息
     * @param content
     */
    public void sendSingle(String content,Integer toId) {
        /*if (!isLogin) {
            JOptionPane.showMessageDialog(null, "尚未登录");
            return;
        }*/
        Msg msg=new Msg();
        msg.setCode(FunctionType.SINGLE_CHART.getCode());
        msg.setFromId(mine.getId());
        msg.setToId(toId);
        msg.setMsg(content);
        sendTest(JSONObject.toJSONString(msg));
    }
    /**
     * 发送群聊信息
     * @param content
     */
    public void sendPeople(String content,Integer toId) {
        /*if (!isLogin) {
            JOptionPane.showMessageDialog(null, "尚未登录");
            return;
        }*/
        Msg msg=new Msg();
        msg.setCode(FunctionType.PEOPLE_CHART.getCode());
        msg.setFromId(mine.getId());
        msg.setToId(toId);
        msg.setMsg(content);
        sendTest(JSONObject.toJSONString(msg));
    }
    /**
     * 用于接收信息的处理器，单独线程处理
     */
    private class ReceiverHandler implements Runnable {
        private boolean connected = true;

        public void shutdown() {
            connected = false;
        }
        public void run() {
            try {
                while (connected) {
                    int size = 0;
                    selector.select();
                    for (Iterator<SelectionKey> it = selector.selectedKeys().iterator(); it.hasNext(); ) {
                        SelectionKey selectionKey = it.next();
                        it.remove();
                        if (selectionKey.isReadable()) {
                            StringBuffer infor = new StringBuffer();
                            buf.clear();
                            while ((size = clientChannel.read(buf)) > 0) {
                                buf.flip();
                                infor.append(new String(buf.array(), 0, size,"UTF-8"));
                                buf.clear();
                            }
                            System.out.println(infor.toString());
                            JSONObject object= JSONObject.parseObject(infor.toString());
                            //一对一单聊
                            if(object.getInteger("code")==FunctionType.SINGLE_CHART.getCode()){
                                if(chart.talkUser!=null&&!chart.talkUser.isGroup&&chart.talkUser.getId()==object.getInteger("fromId")){
                                    DisplayData data=new DisplayData();
                                    data.setData(object.getString("msg"));
                                    data.setIsLeft(true);
                                    data.setName(chart.talkUser.getName());
                                    chart.talkUser.addMsg(data);
                                    chart.listModel.addElement(data);
                                }else{
                                    //不是当前聊天窗口则加入消息队列
                                    Enumeration<User> enumeration= chart.listModelUser.elements();
                                    while(enumeration.hasMoreElements()){
                                        User user= enumeration.nextElement();
                                        if(!user.isGroup&&user.getId()==object.getInteger("fromId")){
                                            DisplayData data=new DisplayData();
                                            data.setData(object.getString("msg"));
                                            data.setIsLeft(true);
                                            data.setName(user.getName());
                                            user.addMsg(data);
                                            break;
                                        }
                                    }
                                }
                            }else if(object.getInteger("code")==FunctionType.LOGIN.getCode()){//用户登录请求处理，以及其他用户登录
                                if(object.getString("msg").equals("success")){
                                    if(object.get("fromId")==null){//为自己登录后发回的信息
                                        JOptionPane.showMessageDialog(null, "登录成功");
                                        List<JSONObject> list= (List<JSONObject>) object.get("data");
                                        for(JSONObject u:list){
                                            if(u==null)continue;
                                            if(u.getInteger("isGroup")==0&&u.getInteger("id")==mine.getId()){
                                                mine.setName(u.getString("name"));
                                                chart.nameLabel.setText(mine.getName());
                                                chart.logButton.setText("退出");
                                                continue;
                                            }
                                            User nUser=new User();
                                            if(u.getInteger("isGroup")==1){
                                                nUser.isGroup=true;
                                            }else{
                                                nUser.isGroup=false;
                                            }
                                            nUser.setId(u.getInteger("id"));
                                            nUser.setName(u.getString("name"));
                                            chart.listModelUser.addElement(nUser);
                                        }
                                    }else{//接受别人登录后发送的信息
                                        JSONObject n=object.getJSONObject("data");
                                        User nUser=new User();
                                        nUser.setId(n.getInteger("id"));
                                        nUser.setName(n.getString("name"));
                                        chart.listModelUser.addElement(nUser);
                                    }
                                }else{
                                    JOptionPane.showMessageDialog(null, "登录失败");
                                    System.exit(0);
                                }
                            }else if(object.getInteger("code")==FunctionType.QUIT.getCode()){
                                if(chart.talkUser.getId()==object.getInteger("fromId")){
                                    chart.listModel.clear();
                                    chart.talkUser=null;
                                }
                                for(int i=0;i<chart.listModelUser.size();i++){
                                    if(chart.listModelUser.getElementAt(i).getId()==object.getInteger("fromId")){
                                        chart.listModelUser.remove(i);
                                        break;
                                    }
                                }
                            }else if(object.getInteger("code")==FunctionType.PEOPLE_CHART.getCode()){
                                if(chart.talkUser!=null&&chart.talkUser.isGroup&&chart.talkUser.isGroup&&chart.talkUser.getId()==object.getInteger("fromId")){
                                    //当前聊天窗口，直接显示出来
                                    DisplayData data=new DisplayData();
                                    data.setData(object.getString("msg"));
                                    data.setIsLeft(true);
                                    data.setName(chart.talkUser.getName());
                                    chart.talkUser.addMsg(data);
                                    chart.listModel.addElement(data);
                                }else{
                                    //不是当前聊天窗口则加入消息队列
                                    Enumeration<User> enumeration= chart.listModelUser.elements();
                                    while(enumeration.hasMoreElements()){
                                        User user= enumeration.nextElement();
                                        if(user.isGroup&&user.getId()==object.getInteger("fromId")){
                                            DisplayData data=new DisplayData();
                                            data.setData(object.getString("msg"));
                                            data.setIsLeft(true);
                                            data.setName(user.getName());
                                            user.addMsg(data);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "服务器关闭，请重新尝试连接");
                System.exit(0);
                isLogin = false;
            }
        }
    }
}
