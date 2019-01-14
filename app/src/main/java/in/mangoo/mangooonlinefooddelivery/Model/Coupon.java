package in.mangoo.mangooonlinefooddelivery.Model;

import java.io.Serializable;

public class Coupon implements Serializable {

    private String desc, code, exp, maxprice, minprice, off, type;

    public Coupon() {
    }

    public Coupon(String desc, String code, String exp, String maxprice, String minprice, String off, String type) {
        this.desc = desc;
        this.code = code;
        this.exp = exp;
        this.maxprice = maxprice;
        this.minprice = minprice;
        this.off = off;
        this.type = type;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getExp() {
        return exp;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public String getMaxprice() {
        return maxprice;
    }

    public void setMaxprice(String maxprice) {
        this.maxprice = maxprice;
    }

    public String getMinprice() {
        return minprice;
    }

    public void setMinprice(String minprice) {
        this.minprice = minprice;
    }

    public String getOff() {
        return off;
    }

    public void setOff(String off) {
        this.off = off;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}



