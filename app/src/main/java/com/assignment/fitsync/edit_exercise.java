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
import java.util.List;
import java.util.Map;

public class edit_exercise extends AppCompatActivity {
    //initialize buttons
    Button btn_edit;
    Button btn_cancel;
    //spinner
    Spinner spin_date;
    //Initialize EditText
    EditText edit_exec;
    EditText edit_reps;
    EditText edit_sets;
    EditText edit_index;
    // initialize firestore referrence
    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static CollectionReference members = db.collection("members");
    public String userName = MainActivity.userID;                                //HARDCODING BROSCIENCE LIFE - CHANGE TO USER EMAIL FROM REGISTRATION
    public DocumentReference userRef = db.collection("members").document(userName);
    public static DocumentSnapshot userDoc;
    private static final String TAG = "Edit_Exercise";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);
        // set up Edittext
        edit_exec = (EditText)findViewById(R.id.edit_exec);
        edit_reps = (EditText)findViewById(R.id.edit_reps);
        edit_sets = (EditText)findViewById(R.id.edit_sets);
        edit_index = (EditText)findViewById(R.id.edit_index);
        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userDoc = task.getResult();
                    if (userDoc.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + userDoc.getData());
                    } else {
                        Log.d(TAG, "No such document");

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        // set up week date spinner
        spin_date = (Spinner) findViewById(R.id.spin_date);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.day_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spin_date.setAdapter(adapter);
        btn_edit = (Button)findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edit();
            }
        });
        btn_cancel = (Button)findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goBack(0);
            }
        });
    }

    public void edit() {
        final Map<String, Object> datapacket = new HashMap<>();
        final int index = Integer.parseInt(edit_index.getText().toString());
        final String date = spin_date.getSelectedItem().toString();

        userRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    userDoc = task.getResult();
                    if (userDoc.exists()) {
                        //set up edit value
                        String workout = edit_exec.getText().toString();
                        int reps = Integer.parseInt(edit_reps.getText().toString());
                        int sets = Integer.parseInt(edit_sets.getText().toString());
                        // get fields on the day
                        List all_exe = new ArrayList();
                        Object day_list_obj = userDoc.get(date);
                        ArrayList myobj = (ArrayList) day_list_obj;
                        // declaration for selected exercise
                        Map<String, Object> select_exercise = new HashMap<>();// map for selected exercise
                        Object exe_map = myobj.get(index);
                        select_exercise = (Map<String, Object>)exe_map;
                        select_exercise.put("exercise", workout);
                        select_exercise.put("sets", sets);
                        select_exercise.put("reps", reps);

                        myobj.set(index, (Object)select_exercise);
                        all_exe = myobj;
                        // put into the user document map
                        datapacket.put(date, all_exe);

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
        // back to workout_screen
        goBack(1);
    }
    // go back workout screen
    public void goBack(int i) {
        int miTimer = 3500;
        final ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("updating your profile...");
        if(i == 0) {
            pd.setMessage("backing to Workout Screen..");
            miTimer = 1000;
        }
        pd.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent in = new Intent(edit_exercise.this, workout_screen.class);
                pd.dismiss();
                startActivity(in);
            }
        }, miTimer);

    }
}
