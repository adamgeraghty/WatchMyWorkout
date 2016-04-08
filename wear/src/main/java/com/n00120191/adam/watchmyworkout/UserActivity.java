package com.n00120191.adam.watchmyworkout;

public class UserActivity {
    private long id;
    private String activity;
    private int data;
    private byte[] bytes;

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