package com.assignment.fitsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class sync_screen extends AppCompatActivity {

    public static final String TAG = "sync_screen";

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static CollectionReference members = db.collection("members");
    public String userName = MainActivity.userID;
    public DocumentReference userRef = db.collection("members").document(userName);
    public static DocumentSnapshot userDoc = workout_screen.userDoc;

    public Map<String, Object> dataPacket = new HashMap<>();
    public Map<String, Object> userPacket = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sync_screen);


        //prepare data packet
        userPacket.put("Monday", userDoc.get("Monday"));
        userPacket.put("Tuesday", userDoc.get("Tuesday"));
        userPacket.put("Wednesday", userDoc.get("Wednesday"));
        userPacket.put("Thursday", userDoc.get("Thursday"));
        userPacket.put("Friday", userDoc.get("Friday"));
        userPacket.put("Saturday", userDoc.get("Saturday"));
        userPacket.put("Sunday", userDoc.get("Sunday"));


    }

    public void goBack(View view) {

        //intent to start other activity
        Intent go_back = new Intent(this, workout_screen.class);
        startActivity(go_back);


    }

    public void trySync(View view) {
        //attempt to sync here if user exists
        EditText userEmail =  findViewById(R.id.user_email);
        final String email = userEmail.getText().toString();
        final FirebaseAuth mAuth;
        mAuth = FirebaseAuth.getInstance();
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {

                boolean user_exists = task.getResult().getSignInMethods().isEmpty();

                if (user_exists) {
                    Toast.makeText(sync_screen.this,"User does not exist!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(sync_screen.this,"User exists! Sending to user...", Toast.LENGTH_SHORT).show();
                    //make document, and update with datapacket
                    final DocumentReference user_share_ref = db.collection("members").document(email);
                    user_share_ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful()) {
                                user_share_ref.set(userPacket)
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
                             else {
                                Log.d(TAG, "get failed with ", task.getException());
                            }
                        }
                    });

                }
            }
        });
    }
}
