package com.assignment.fitsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class workout_screen extends AppCompatActivity {

    private static final String TAG = "workout_screen";

    BackgroundService mService;
    boolean mBound = false;
    Intent serviceIntent;
    public static boolean found = BackgroundService.found;

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static CollectionReference members = db.collection("members");
    public String userName = MainActivity.userID;
    public DocumentReference userRef = db.collection("members").document(userName);
    public static DocumentSnapshot userDoc;
    public static Context mContext;
    public static boolean started_activity = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_screen);

        mContext = this;

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryAdd(view);
            }
        });

        final Map<String, Object> default_dataPacket = new HashMap<>();   //Final data packet to be added to document
        List default_exercises = new ArrayList();                   //List of exercise maps

        default_dataPacket.put("Monday", default_exercises);
        default_dataPacket.put("Tuesday", default_exercises);
        default_dataPacket.put("Wednesday", default_exercises);
        default_dataPacket.put("Thursday", default_exercises);
        default_dataPacket.put("Friday", default_exercises);
        default_dataPacket.put("Saturday", default_exercises);
        default_dataPacket.put("Sunday", default_exercises);

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userDoc = task.getResult();
                    if (userDoc.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + userDoc.getData());
                    } else {
                        Log.d(TAG, "No such document");
                        userRef.set(default_dataPacket)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                        System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||");
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        System.out.println("*******************************************************");
                                        Log.w(TAG, "Error writing document", e);
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        System.out.println("I RUN HERE IN WOKROUT_SCREEN *******************************************************************************************************************************************************************");
        serviceIntent = new Intent(this, BackgroundService.class);
        startService(serviceIntent);
        bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE); // Start the background service. Then bind

    }

    @Override
    protected void onStop() {
        super.onStop();

        stopService(serviceIntent);

        mBound = false;
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            BackgroundService.LocalBinder binder = (BackgroundService.LocalBinder) service;
            mService = binder.getService();

            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {mBound = false;}
    };

    public void tryAdd(View view) {

        //intent to start other activity
        Intent add_exer = new Intent(this, add_exercise.class);
        startActivity(add_exer);


    }


    public void sync(View view) {

        //intent to start other activity
        Intent sync = new Intent(this, sync_screen.class);
        startActivity(sync);


    }

    public static void startSyncConfirm() {
        //work here after called from BS
        System.out.println("BEEP BOOOP BOOP BOP **********************************************************************************************************");
        if (started_activity == false) {
            Intent sync_confirm = new Intent(mContext, sync_confirm.class);
            mContext.startActivity(sync_confirm);
            started_activity = true;

        }


    }
}


