package com.assignment.fitsync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class workout_screen extends AppCompatActivity {

    // initialize addview layout
    LinearLayout llayout_m;
    LinearLayout llayout_t;
    LinearLayout llayout_w;
    LinearLayout llayout_th;
    LinearLayout llayout_f;
    LinearLayout llayout_s;
    LinearLayout llayout_su;
    String[] date = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};
    // fields column name
    String field1 = "exercise";
    String field2 = "reps";
    String field3 = "sets";
    // Button
    Button btn_delete;
    Button btn_edit;
    private static final String TAG = "workout_screen";

    public static FirebaseFirestore db = FirebaseFirestore.getInstance();
    public static CollectionReference members = db.collection("members");
    public String userName = "BroScienceLife"; //MainActivity.userID;                                //HARDCODING BROSCIENCE LIFE - CHANGE TO USER EMAIL FROM REGISTRATION
    public DocumentReference userRef = db.collection("members").document(userName);
    public static DocumentSnapshot userDoc;
    private Object object;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout_screen);

        // initialize linearlayout
        llayout_m = (LinearLayout) findViewById(R.id.llayout_m);
        llayout_t = (LinearLayout) findViewById(R.id.llayout_t);
        llayout_w = (LinearLayout) findViewById(R.id.llayout_w);
        llayout_th = (LinearLayout) findViewById(R.id.llayout_th);
        llayout_f = (LinearLayout) findViewById(R.id.llayout_f);
        llayout_s = (LinearLayout) findViewById(R.id.llayout_s);
        llayout_su = (LinearLayout) findViewById(R.id.llayout_su);

        fetchExercise();
        // goto add exercise
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryAdd(view);
            }
        });
        // goto delete exercise
        btn_delete = (Button)findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryDel(view);
            }
        });
        btn_edit = (Button)findViewById(R.id.btn_edit);
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tryEdt(view);
            }
        });
    }
    @Override
    public void onRestart() {
        super.onRestart();
        //fetchExercise();
    }
    /* Implementation Methods*/
    //sync: "onClick: sync" in workout_screen.xml
    public void sync(View view) {}

    public void fetchExercise() {
        final Map<String, Object> default_dataPacket = new HashMap<>();   //Final data packet to be added to document
        final List default_exercises = new ArrayList();                   //List of exercise maps
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
                        for(int i = 0; i < date.length; i++) {
                            // get field on Monday
                            Object day_list_obj = userDoc.get(date[i]);
                            System.out.println(date[i]);
                            //Map<String, Object> mymap = userDoc.getData();
                            ArrayList myobj = (ArrayList) day_list_obj;

                            Map<String, Object> field = new HashMap<>();

                            for(int j = 0; j < myobj.size(); j++) {
                                Object ex_map = myobj.get(j);
                                field = (Map<String, Object>)ex_map;

                                field.get(field1); // exercise
                                field.get(field2); // reps
                                field.get(field3); // sets

                                //
                                ExerciseInfo exeinfo = new ExerciseInfo();
                                exeinfo.setWorkout((String)field.get(field1));
                                String temp_sets = Long.toString((Long)field.get(field3));
                                String temp_reps = Long.toString((Long)field.get(field2));
                                exeinfo.setSets(Integer.parseInt(temp_sets));
                                exeinfo.setReps(Integer.parseInt(temp_reps));

                                String output = exeinfo.display();
                                fetchview(date[i], output);
                                Log.d(TAG, "DocumentSnapshot data: " + userDoc.getData());
                            }

                        }
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
    //
    public void fetchview(String d, String txt) {
        if(!txt.equals("")) {
            // create textview dynamically and set style
            TextView my_txt =new TextView(this);
            my_txt.setText(txt);
            my_txt.setId(0);
            my_txt.setBackgroundColor(Color.parseColor("#cce6ff"));
            my_txt.setTypeface(my_txt.getTypeface(), Typeface.BOLD);
            my_txt.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
            my_txt.setPadding(20, 15, 20,15);
            // margin the textview
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT );
            params.setMargins(10,0,20,0);

            // add text view into layout
            if( d.equals("Monday")) {
                llayout_m = (LinearLayout) findViewById(R.id.llayout_m);
                llayout_m.addView(my_txt,params);
            } else if (d.equals("Tuesday")) {
                llayout_t = (LinearLayout) findViewById(R.id.llayout_t);
                llayout_t.addView(my_txt,params);
            } else if (d.equals("Wednesday")) {
                llayout_w = (LinearLayout) findViewById(R.id.llayout_w);
                llayout_w.addView(my_txt,params);
            } else if (d.equals("Thursday")) {
                llayout_th = (LinearLayout) findViewById(R.id.llayout_th);
                llayout_th.addView(my_txt,params);
            } else if (d.equals("Friday")) {
                llayout_f = (LinearLayout) findViewById(R.id.llayout_f);
                llayout_f.addView(my_txt,params);
            } else if (d.equals("Saturday")) {
                llayout_s = (LinearLayout) findViewById(R.id.llayout_s);
                llayout_s.addView(my_txt,params);
            } else if (d.equals("Sunday")){
                llayout_su = (LinearLayout) findViewById(R.id.llayout_su);
                llayout_su.addView(my_txt,params);
            } else {
                Toast.makeText(workout_screen.this, "INCORRECT DATE!!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    /* Methods for Intent other Activities */
    // go to add
    public void tryAdd(View view) {
        //intent to start other activity
        Intent add_exer = new Intent(this, add_exercise.class);
        startActivity(add_exer);
    }
    //go to delete
    public void tryDel(View view) {
        Intent in = new Intent(this, delete_exercise.class);
        startActivity(in);
    }
    //go to edit
    public void tryEdt(View view) {
        Intent in = new Intent(this, edit_exercise.class);
        startActivity(in);
    }
}
