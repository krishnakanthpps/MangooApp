package in.mangoo.mangooonlinefooddelivery.Model;

import java.util.List;

public class RestaurantID {

    private String UserPhone;
    private String restaurantName;
    private List<Order> restaurantList;

    public RestaurantID() {
    }

    public RestaurantID(String userPhone, String restaurantName, List<Order> restaurantList) {
        UserPhone = userPhone;
        this.restaurantName = restaurantName;
        this.restaurantList = restaurantList;
    }

    public String getUserPhone() {
        return UserPhone;
    }

    public void setUserPhone(String userPhone) {
        UserPhone = userPhone;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public List<Order> getRestaurantList() {
        return restaurantList;
    }

    public void setRestaurantList(List<Order> restaurantList) {
        this.restaurantList = restaurantList;
    }
}
