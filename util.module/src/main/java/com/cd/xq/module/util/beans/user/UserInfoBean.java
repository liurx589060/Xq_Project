package com.cd.xq.module.util.beans.user;

import com.cd.xq.module.util.Constant;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/5/14.
 */

public class UserInfoBean implements Serializable{
    private static final long serialVersionUID = -7620435178023928252L;

    private String user_id = "";
    private String user_name = "";
    private String nick_name = "";
    private String gender = "";
    private int level = 0;
    private long balance = 0;
    private String head_image = "";
    private String create_time = "";
    private String modify_time = "";
    private String password = "";
    private String roomId = "";
    private String role_type = Constant.ROLETYPE_GUEST;
    private int age = 0;
    private int tall = 0;
    private String scholling = "";
    private String professional = "";
    private String native_place = "";
    private int marrige = 0;
    private String job_address = "";
    private String phone = "";
    private String special_info = "";
    private int limitLevel = -1;
    private int limitLady = 10;
    private int limitMan = 1;
    private int limitAngel = 1;



    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getNick_name() {
        return nick_name;
    }

    public void setNick_name(String nick_name) {
        this.nick_name = nick_name;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getHead_image() {
        return head_image;
    }

    public void setHead_image(String head_image) {
        this.head_image = head_image;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getModify_time() {
        return modify_time;
    }

    public void setModify_time(String modify_time) {
        this.modify_time = modify_time;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getTall() {
        return tall;
    }

    public void setTall(int tall) {
        this.tall = tall;
    }

    public String getScholling() {
        return scholling;
    }

    public void setScholling(String scholling) {
        this.scholling = scholling;
    }

    public String getProfessional() {
        return professional;
    }

    public void setProfessional(String professional) {
        this.professional = professional;
    }

    public String getNative_place() {
        return native_place;
    }

    public void setNative_place(String native_place) {
        this.native_place = native_place;
    }

    public int getMarrige() {
        return marrige;
    }

    public void setMarrige(int marrige) {
        this.marrige = marrige;
    }

    public String getJob_address() {
        return job_address;
    }

    public void setJob_address(String job_address) {
        this.job_address = job_address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getBalance() {
        return balance;
    }

    public void setBalance(long balance) {
        this.balance = balance;
    }

    public String getRole_type() {
        return role_type;
    }

    public void setRole_type(String role_type) {
        this.role_type = role_type;
    }

    public int getLimitLevel() {
        return limitLevel;
    }

    public void setLimitLevel(int limitLevel) {
        this.limitLevel = limitLevel;
    }

    public int getLimitLady() {
        return limitLady;
    }

    public void setLimitLady(int limitLady) {
        this.limitLady = limitLady;
    }

    public int getLimitMan() {
        return limitMan;
    }

    public void setLimitMan(int limitMan) {
        this.limitMan = limitMan;
    }

    public int getLimitAngel() {
        return limitAngel;
    }

    public void setLimitAngel(int limitAngel) {
        this.limitAngel = limitAngel;
    }

    public String getSpecial_info() {
        return special_info;
    }

    public void setSpecial_info(String special_info) {
        this.special_info = special_info;
    }
}
