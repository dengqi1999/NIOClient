package com.dq.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class User {
    Integer id;
    String name;
    String password;
    Boolean isGroup;
    List<DisplayData>list;
    public User() {
        isGroup=false;
        this.list=new ArrayList<DisplayData>();
    }
    public User(Integer id, String name, String password,Boolean isGroup) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.isGroup=isGroup;
        this.list=new ArrayList<DisplayData>();
    }

    public void addMsg(DisplayData msg){
        list.add(msg);
    }
    public List<DisplayData> getMsg(){
        return list;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
