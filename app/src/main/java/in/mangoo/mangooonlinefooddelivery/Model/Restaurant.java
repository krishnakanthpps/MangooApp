package in.mangoo.mangooonlinefooddelivery.Model;


public class Restaurant {
    private String name,image,addr,openingTime,closingTime;
    public Restaurant() {
    }

    public Restaurant(String name, String image, String addr, String openingTime, String closingTime) {
        this.name = name;
        this.image = image;
        this.addr = addr;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
    }

    public String getOpeningTime() {
        return openingTime;
    }

    public void setOpeningTime(String openingTime) {
        this.openingTime = openingTime;
    }

    public String getClosingTime() {
        return closingTime;
    }

    public void setClosingTime(String closingTime) {
        this.closingTime = closingTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }
}
