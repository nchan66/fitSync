package com.assignment.fitsync;

public class ExerciseInfo {
    private int id;
    private String date;
    private String workout;
    private int sets;
    private int reps;

    // Constructor
    public ExerciseInfo() {}

    // setters
    public void setDate(String d) {
        date = d;
    }
    public void setWorkout(String w) {
        workout = w;
    }
    public void setSets(int s) {
        sets = s;
    }
    public void setReps(int r) {
        reps = r;
    }
    public void setId(int i) {
        id = i;
    }
    //getters
    public String getDate() {
        return date;
    }
    public String getWorkout() {
        return workout;
    }
    public int getSets() {
        return sets;
    }
    public int getReps() {
        return reps;
    }
    public int getId() {
        return id;
    }

    //helper function to display workout information

    public String display() {
        String output = "";
        output = workout + "\n" + ("" + getSets()) + " X " + ("" + getReps());
        return output;
    }
}
