package nl.yrck.urbandictionary;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/*
 * The only purpose of this class is to enable persistent data in Firebase. We do it this way
 * to avoid crashes from trying to initialize persistence twice.
 */
public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        /* Enable disk persistence  */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
