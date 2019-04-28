package com.cd.xq.beans;

public class BGetChatRoomList {
    private long room_id;
    private int status;
    private int room_status;
    private String start_time = "";
    private String end_time = "";
    private int room_role_type;
    private String creater = "";
    private String describe = "";
    private String title = "";
    private int isPublic;
    private String inner_id = "";

    public long getRoom_id() {
        return room_id;
    }

    public void setRoom_id(long room_id) {
        this.room_id = room_id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getRoom_role_type() {
        return room_role_type;
    }

    public void setRoom_role_type(int room_role_type) {
        this.room_role_type = room_role_type;
    }

    public String getCreater() {
        return creater;
    }

    public void setCreater(String creater) {
        this.creater = creater;
    }

    public String getDescribe() {
        return describe;
    }

    public void setDescribe(String describe) {
        this.describe = describe;
    }

    public int getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(int isPublic) {
        this.isPublic = isPublic;
    }

    public int getRoom_status() {
        return room_status;
    }

    public void setRoom_status(int room_status) {
        this.room_status = room_status;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInner_id() {
        return inner_id;
    }

    public void setInner_id(String inner_id) {
        this.inner_id = inner_id;
    }
}
