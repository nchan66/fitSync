package com.assignment.fitsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Transaction;


import org.w3c.dom.Text;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class add_exercise extends AppCompatActivity {

    private static final String TAG = "add_exercise";


    public EditText exercise_view;
    public EditText set_view;
    public EditText rep_view;
    public Spinner day_view;

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static CollectionReference members = db.collection("members");
    public String userName = MainActivity.userID;
    public DocumentReference userRef = db.collection("members").document(userName);
    public static DocumentSnapshot userDoc;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exercise);

        exercise_view = findViewById((R.id.exercise_name));
        set_view = findViewById((R.id.sets));
        rep_view = findViewById((R.id.reps));
        day_view = findViewById(R.id.day_spinner);


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

        Spinner spinner = (Spinner) findViewById(R.id.day_spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.day_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        spinner.setAdapter(adapter);
    }

    public void add(View view) {

        Map<String, Object> dataPacket = new HashMap<>();   //Final data packet to be added to document
        List exercises = new ArrayList();                   //List of exercise maps
        Map<String, Object> exercise_map = new HashMap<>(); //Exercise maps
        //Getting inputs from fields in app
        String exercise_name = exercise_view.getText().toString();
        int sets = Integer.parseInt(set_view.getText().toString());
        int reps = Integer.parseInt(rep_view.getText().toString());
        String day_string = day_view.getSelectedItem().toString();

        //adding values to proper keys in exercise map
        exercise_map.put("exercise", exercise_name);
        exercise_map.put("sets", sets);
        exercise_map.put("reps", reps);

        //Adding exercise map to list
        //check if day has a list already , if it does, if list exists -> make list = that, then add


        Object day_list_Obj = userDoc.get(day_string); //GETS DAY OF THE WEEK's LIST
        ArrayList new_array_list = (ArrayList) day_list_Obj ; //CASTS DEFAULT OBJ AS ARRAYLIST

        exercises = new_array_list;



        exercises.add(exercise_map);

        //Add exercise list to day_Obj with proper day from spinner view
        dataPacket.put(day_string ,exercises);




        members.document(userName).update(dataPacket)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Log.w(TAG, "Error writing document", e);
                    }
                });

        //intent to start other activity
        Intent go_back = new Intent(this, workout_screen.class);
        startActivity(go_back);

    }


    public void goBack(View view) {

        //intent to start other activity
        Intent go_back = new Intent(this, workout_screen.class);
        startActivity(go_back);


    }
}
