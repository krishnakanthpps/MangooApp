package app.mangoofood.mangooapp.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import app.mangoofood.mangooapp.Model.User;
import app.mangoofood.mangooapp.Remote.APIService;
import app.mangoofood.mangooapp.Remote.RetrofitClient;

public class Common {

    public static User currentUser;
    private static final String BASE_URL="https://fcm.googleapis.com/";

    public static APIService getFCMService()
    {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
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

}
