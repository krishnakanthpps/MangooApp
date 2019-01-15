package in.mangoo.mangooonlinefooddelivery.Model;

public class Feedback {
    private String feedback,phone;

    public Feedback() {
    }

    public Feedback(String phone, String feedback) {
        this.phone = phone;
        this.feedback = feedback;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
