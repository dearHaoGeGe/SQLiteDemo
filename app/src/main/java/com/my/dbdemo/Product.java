package com.my.dbdemo;

import com.my.dbdemo.db.annotation.Column;
import com.my.dbdemo.db.annotation.TableName;
import com.my.dbdemo.db.io.EntitySerializable;

/**
 * Created by YJH on 2017/3/28 10:53.
 */

@TableName("t_product")
public class Product extends EntitySerializable {

    /**
     * 产品名称
     */
    @Column
    private String name;

    /**
     * 日期
     */
    @Column(type = Column.Type.INTEGER)
    private long date;

    /**
     * 产品id
     */
    @Column
    private String productId;

    /**
     * 价格
     */
    @Column(type = Column.Type.REAL)
    private float price;

    /**
     * 测试double
     */
    @Column(type = Column.Type.REAL)
    private double dou;

    /**
     * 照片数量
     */
    @Column(type = Column.Type.INTEGER)
    private int photoCount;

    /**
     * 测试Integer类型
     */
    @Column(type = Column.Type.INTEGER)
    private Integer integer;

    /**
     * 备注
     */
    @Column
    private String beanExplainRemark;

    /**
     * 是否卖完：0否   1是
     */
    @Column(type = Column.Type.INTEGER)
    private int isSellOut;

    private boolean isSelected;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public double getDou() {
        return dou;
    }

    public void setDou(double dou) {
        this.dou = dou;
    }

    public int getPhotoCount() {
        return photoCount;
    }

    public void setPhotoCount(int photoCount) {
        this.photoCount = photoCount;
    }

    public Integer getInteger() {
        return integer;
    }

    public void setInteger(Integer integer) {
        this.integer = integer;
    }

    public String getBeanExplainRemark() {
        return beanExplainRemark;
    }

    public void setBeanExplainRemark(String beanExplainRemark) {
        this.beanExplainRemark = beanExplainRemark;
    }

    public int getIsSellOut() {
        return isSellOut;
    }

    public void setIsSellOut(int isSellOut) {
        this.isSellOut = isSellOut;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", date=" + date +
                ", productId='" + productId + '\'' +
                ", price=" + price +
                ", dou=" + dou +
                ", photoCount=" + photoCount +
                ", integer=" + integer +
                ", beanExplainRemark='" + beanExplainRemark + '\'' +
                ", isSellOut=" + isSellOut +
                ", isSelected=" + isSelected +
                '}';
    }
}
