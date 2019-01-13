package in.mangoo.mangooonlinefooddelivery.Model;

public class DeliveryBoy {

    Long phone;
    String name;

    public DeliveryBoy() {
    }

    public DeliveryBoy(Long phone, String name) {
        this.phone = phone;
        this.name = name;
    }

    public Long getPhone() {
        return phone;
    }

    public void setPhone(Long phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
