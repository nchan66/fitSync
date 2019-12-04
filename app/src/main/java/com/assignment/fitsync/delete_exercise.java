package com.assignment.fitsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class delete_exercise extends AppCompatActivity {
    // initialize view widgets
    EditText del_index;
    Button btn_cancel;
    Spinner spin_d;
    // initialize firestore referrence
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static CollectionReference members = db.collection("members");
    public String userName = "BroScienceLife";//MainActivity.userID;                                //HARDCODING BROSCIENCE LIFE - CHANGE TO USER EMAIL FROM REGISTRATION
    public DocumentReference userRef = db.collection("members").document(userName);
    public static DocumentSnapshot userDoc;
    private static final String TAG = "Delete_Exercise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_exercise);
        // get widgets by id
        spin_d = (Spinner) findViewById(R.id.spin_d);
        del_index = (EditText) findViewById(R.id.del_index);
        btn_cancel = (Button) findViewById(R.id.btn_cancel);

        /* -----------set up spinner to Monday thru Sunday-------------- */
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.day_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spin_d.setAdapter(adapter);
        /* ---------------end of set spinner definition----------------- */

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack(0);
            }
        });
    }
    public void delete(View view) {
        final String date = spin_d.getSelectedItem().toString();
        final int index = Integer.parseInt(del_index.getText().toString());
        // if input date is available
        if(date.equals("Monday") || date.equals("Tuesday") || date.equals("Wednesday")
        || date.equals("Thursday") || date.equals("Friday") || date.equals("Saturday")
        || date.equals("Sunday")) {
            final Map<String, Object> datapacket = new HashMap<>();
            userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        userDoc = task.getResult();
                        if (userDoc.exists()) {
                            // get field on
                            Object day_list_obj = userDoc.get(date);

                            ArrayList myobj = (ArrayList) day_list_obj;
                            // remove from the date exercise
                            myobj.remove(index);
                            // put into the user document map
                            datapacket.put(date, myobj);

                            members.document(userName).update(datapacket).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "DocumentSnapshot successfully written!");
                                    System.out.println("|||||||||||||||||||||||||||||||||||||||||||||||||||||");
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    System.out.println("*******************************************************");
                                    Log.w(TAG, "Error writing document", e);
                                }
                            });
                            //
                            Log.d(TAG, "DocumentSnapshot data: " + userDoc.getData());

                        }
                    }
                }
            });
        }
        // back to workout screen
        goBack(1);
    }

    // go back workout screen
    public void goBack(int i) {
        int miTimer = 3000;
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("deleting...");
        if(i == 0) {
            pd.setMessage("backing to Workout Screen..");
            miTimer = 1000;
        }
        pd.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent in = new Intent(delete_exercise.this, workout_screen.class);
                pd.dismiss();
                startActivity(in);
            }
        }, miTimer);

    }
}
