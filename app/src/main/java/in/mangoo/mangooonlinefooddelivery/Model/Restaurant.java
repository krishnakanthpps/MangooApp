package in.mangoo.mangooonlinefooddelivery.Model;

public class Restaurant {
    private String name,image,addr;

    public Restaurant() {
    }

    public Restaurant(String name, String image, String addr) {
        this.name = name;
        this.image = image;
        this.addr = addr;
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
