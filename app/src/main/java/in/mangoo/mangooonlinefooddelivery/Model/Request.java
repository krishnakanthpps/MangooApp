package in.mangoo.mangooonlinefooddelivery.Model;

import java.util.List;

public class Request {

    private String phone;
    private String name;
    private String address;
    private String paymentMethod;
    private String total;
    private String status;
    private String comment;
    private String latLng;
    private String restaurantId;
    private List<Order> foods;


    public Request() {
    }

    public Request(String phone, String name, String address, String paymentMethod, String total, String status, String comment, String latLng, String restaurantId, List<Order> foods) {
        this.phone = phone;
        this.name = name;
        this.address = address;
        this.paymentMethod = paymentMethod;
        this.total = total;
        this.status = status;
        this.comment = comment;
        this.latLng = latLng;
        this.restaurantId = restaurantId;
        this.foods = foods;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLatLng() {
        return latLng;
    }

    public void setLatLng(String latLng) {
        this.latLng = latLng;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<Order> getFoods() {
        return foods;
    }

    public void setFoods(List<Order> foods) {
        this.foods = foods;
    }
}
