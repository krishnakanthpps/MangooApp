package app.mangoofood.mangooapp.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import app.mangoofood.mangooapp.Model.User;
import app.mangoofood.mangooapp.Remote.APIService;
import app.mangoofood.mangooapp.Remote.GoogleRetrofitClient;
import app.mangoofood.mangooapp.Remote.IGoogleService;
import app.mangoofood.mangooapp.Remote.RetrofitClient;

public class Common {

    public static String topicName = "News";
    public static User currentUser;

    public static String PHONE_TEXT = "userPhone";

    public static final String INTENT_FOOD_ID = "FoodId";

    private static final String BASE_URL="https://fcm.googleapis.com/";
    private static final String GOOGLE_API_URL="https://maps.googleapis.com/";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGoogleService getGoogleMapAPI()
    {
        return GoogleRetrofitClient.getGoogleClient(GOOGLE_API_URL).create(IGoogleService.class);
    }

    public static final String USER_KEY="User";
    public static final String DELETE="Delete";
    public static final String PWD_KEY="Password";


    public static boolean isConnectedToInternet(Context context)
    {
        ConnectivityManager connectivityManager=(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager !=null)
        {
            NetworkInfo[] info=connectivityManager.getAllNetworkInfo();
            if(info!=null)
            {
                for(int i=0;i<info.length;i++)
                {
                    if(info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static BigDecimal formatCurrrency(String amount, Locale locale) throws ParseException, java.text.ParseException {
        NumberFormat format = NumberFormat.getCurrencyInstance(locale);
        if(format instanceof DecimalFormat)
        {
            ((DecimalFormat)format).setParseBigDecimal(true);
        }
        return (BigDecimal)format.parse(amount.replace("[^\\d.,]",""));
    }
}
