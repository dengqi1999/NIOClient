package com.dq.client;

import java.io.UnsupportedEncodingException;

public class Test {
    public static void main(String[]args) throws UnsupportedEncodingException {
        Client client=new Client();
        String a="abcdef";
        String b="你好在干啥弄啥类阿斯顿v发";//你好在干啥弄啥类阿斯顿你好在干啥弄啥类阿斯顿你好在干啥弄啥类阿斯顿你好在干啥弄啥类阿斯顿你好在干啥弄啥类阿斯顿你好在干啥弄啥类阿斯顿你好在干啥弄啥类阿斯顿你好在干啥弄啥类阿斯顿你好在干啥弄啥类阿斯顿你好在干啥弄啥类阿斯顿";
        String c="opqrstuvwxyz";
        /*String content=b;
        String start="ab";
        int length=content.getBytes("UTF-8").length;
        content=start+content;
        byte[]bytes=content.getBytes("UTF-8");
        bytes[0]= (byte) (length&0xff);
        bytes[1]= (byte) ((length&0xffff)>>8);
        System.out.println(bytes[0]);
        System.out.println(bytes[1]);
        System.out.println((bytes[0]&0xff)+((bytes[1]&0xff)<<8));*/
        client.sendTest(a);
        client.sendTest(b);
        client.sendTest(c);
            /*try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        //}
    }
}
