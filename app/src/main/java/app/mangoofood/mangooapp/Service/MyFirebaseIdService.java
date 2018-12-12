package app.mangoofood.mangooapp.Service;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import app.mangoofood.mangooapp.Common.Common;
import app.mangoofood.mangooapp.Model.Token;

public class MyFirebaseIdService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();
        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if(Common.currentUser !=null)
            updataTokenToFirebase(tokenRefreshed);
    }

    private void updataTokenToFirebase(String tokenRefreshed) {
        FirebaseDatabase db=FirebaseDatabase.getInstance();
        DatabaseReference tokens=db.getReference("Tokens");
        Token token=new Token(tokenRefreshed,false);
        tokens.child(Common.currentUser.getPhone()).setValue(token);

    }
}
