package app.mangoofood.mangooapp.Remote;



import app.mangoofood.mangooapp.Model.MyResponse;
import app.mangoofood.mangooapp.Model.Sender;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;


public interface APIService {

    @Headers(
            {
                    "Content-Type:application/json",
                    "Authorisation:key=AAAAtaAvVus:APA91bED-eO4P4TRModsCpaYI-otpIW02RxKzoS7VKHnFLT9jzjP1jfKm3vej1oDu_JpE4Myp7Y-M-5fjrKLW9_5Z6t5BAEyJNreGErrBBlkLcAivlBS6SrirJOZIwJ4CQoLM39qzWrI"

            }
    )

    @POST("fcm/send")
    Call<MyResponse> sendNotifiction(@Body Sender body);

}
