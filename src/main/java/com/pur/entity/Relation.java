package com.pur.entity;

import java.io.Serializable;
import java.util.List;

public class Relation implements Serializable {

    // 单据id
    private Long id;

    private String title;

    public String getSubString() {
        return subString;
    }

    public void setSubString(String subString) {
        this.subString = subString;
    }

    private String subString;

    // 父级id
    private Long parentId;

    // 是否实际节点
    private Boolean virtual;

    // 在设计器的横坐标
    private int x;

    // 在设计器的纵坐标
    private int y;

    // 节点的宽度
    private Integer width;

    // 节点的高度
    private Integer height;

    // 下级节点集合
    private List<Relation> targets;

    private String text1;
    private String text2;
    private String text3;

    public Relation() {
    }

    public String getText1() {
        return text1;
    }

    public void setText1(String text1) {
        this.text1 = text1;
    }

    public String getText2() {
        return text2;
    }

    public void setText2(String text2) {
        this.text2 = text2;
    }

    public String getText3() {
        return text3;
    }

    public void setText3(String text3) {
        this.text3 = text3;
    }

    public Relation(Long id, String title, Long parentId, Boolean virtual, Integer x, Integer y, Integer width, Integer height, List<Relation> targets, String text1, String text2, String text3) {
        this.id = id;
        this.title = title;
        this.parentId = parentId;
        this.virtual = virtual;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.targets = targets;
        this.text1 = text1;
        this.text2 = text2;
        this.text3 = text3;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Boolean getVirtual() {
        return virtual;
    }

    public void setVirtual(Boolean virtual) {
        this.virtual = virtual;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public List<Relation> getTargets() {
        return targets;
    }

    public void setTargets(List<Relation> targets) {
        this.targets = targets;
    }
}
