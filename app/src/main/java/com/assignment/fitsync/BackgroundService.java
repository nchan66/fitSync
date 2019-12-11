package com.assignment.fitsync;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class BackgroundService extends Service {


    private final IBinder binder = new LocalBinder();

    public class LocalBinder extends Binder {
        BackgroundService getService() {
            return BackgroundService.this;
        }

    }

    private static final String TAG = "Background Service";


    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static CollectionReference members = db.collection("members");
    public String userName = MainActivity.userID;
    public DocumentReference userRef = db.collection("members").document(userName);
    public static DocumentSnapshot userDoc;
    public static boolean found = false;
    //now get current user email
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String user_email = user.getEmail();

    public void onCreate() {



    }
    public void setFound(boolean exists) {
        if(exists) {
            this.found = true;
            workout_screen.startSyncConfirm();
        }

    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        final Handler handler = new Handler();
        final int delay = 3000; //3 seconds


        handler.postDelayed(new Runnable() {




            @Override
            public void run() {
                DocumentReference userEmailRef = null;
                userEmailRef = db.collection("members").document(user_email);

                userEmailRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "Found sync request: " + document.getData());
                                //Now its time to open up a dialoggue
                                setFound(document.exists());

                            } else {
                                Log.d(TAG, "No sync requests.");
                                setFound(document.exists());
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });


                handler.postDelayed(this, delay);
            }
        },delay);
        return START_STICKY;

    }


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Sync done", Toast.LENGTH_SHORT).show();
    }



}

