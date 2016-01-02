package com.ourclother.lab.model;

/**
 * Created by Administrator on 2015/3/15.
 */
public class Cloth {
    private int id;
    private String describ;
    private String picture;
    private String mainPict;

    public void setId(int id) {
        this.id = id;
    }

    public void setDescrib(String describ) {
        this.describ = describ;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setMainPict(String mainPict) {
        this.mainPict = mainPict;
    }
    public int getId() {
        return id;
    }

    public String getDescrib() {
        return describ;
    }

    public String getPicture() {
        return picture;
    }

    public String getMainPict() {
        return mainPict;
    }
}
