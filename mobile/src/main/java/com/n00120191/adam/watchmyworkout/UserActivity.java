package com.n00120191.adam.watchmyworkout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

//this is the user activity class that will create the objects that are stored in the database.
// each object will have unique fields and data relevant to it.
public class UserActivity {
    private long id;
    private String activity;
    private int data;


    public long getId(){
        return id;
    }
    public void setId(long id){
        this.id = id;
    }
    public String getActivity(){
        return activity;
    }
    public void setActivity(String activity){
        this.activity = activity;
    }
    public double getData(){
        return data;
    }

    public void setData(int data){
        this.data = data;
    }

    @Override
    public String toString(){
        return activity;
    }




}
