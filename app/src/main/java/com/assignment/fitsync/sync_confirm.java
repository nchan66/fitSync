package com.assignment.fitsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class sync_confirm extends AppCompatActivity {

    private static final String TAG = "sync_confirm";


    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static DocumentSnapshot syncDoc;
    public static CollectionReference members = db.collection("members");
    public String userName = MainActivity.userID;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    String user_email = user.getEmail();

    public DocumentReference userEmailRef = db.collection("members").document(user_email);



    public void setSender() {
        String sender = (String) syncDoc.get("sender");
        TextView ask = (TextView)findViewById(R.id.ask);

        ask.setText(sender + ", would like to sync with you.");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_confirm);



        userEmailRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    syncDoc = task.getResult();
                    if (syncDoc.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + syncDoc.getData());
                        setSender();
                    } else {
                        Log.d(TAG, "No such document");

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

    }

    public void initialSync(View view) {

        Object day_obj;
        //Do stuff
        Map<String, Object> new_dataPacket = new HashMap<>();   //Final data packet to be added to document
        List exercises = new ArrayList();                   //List of exercise maps

        day_obj = syncDoc.get("Monday");
        ArrayList m_list = (ArrayList) day_obj ;

        day_obj = syncDoc.get("Tuesday");
        ArrayList t_list = (ArrayList) day_obj ;

        day_obj = syncDoc.get("Wednesday");
        ArrayList w_list = (ArrayList) day_obj ;

        day_obj = syncDoc.get("Thursday");
        ArrayList th_list = (ArrayList) day_obj ;

        day_obj = syncDoc.get("Friday");
        ArrayList f_list = (ArrayList) day_obj ;

        day_obj = syncDoc.get("Saturday");
        ArrayList sa_list = (ArrayList) day_obj ;

        day_obj = syncDoc.get("Sunday");
        ArrayList su_list = (ArrayList) day_obj ;

        new_dataPacket.put("Monday", m_list);
        new_dataPacket.put("Tuesday", t_list);
        new_dataPacket.put("Wednesday", w_list);
        new_dataPacket.put("Thursday", th_list);
        new_dataPacket.put("Friday", f_list);
        new_dataPacket.put("Saturday", sa_list);
        new_dataPacket.put("Sunday", su_list);

        members.document(userName).set(new_dataPacket)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "OVERWRITTEN DATA WOOHOOO YOU DID IT!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.w(TAG, "Error writing document", e);
                    }
                });



        members.document(user_email)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

        Intent go_back = new Intent(this, workout_screen.class);
        startActivity(go_back);

    }

    public void goBack(View view) {

        members.document(user_email)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });

        //intent to start other activity
        Intent go_back = new Intent(this, workout_screen.class);
        startActivity(go_back);


    }
}
