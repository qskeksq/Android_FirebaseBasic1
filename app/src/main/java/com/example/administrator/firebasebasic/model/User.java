package com.example.administrator.firebasebasic.model;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

/**
 * Created by Administrator on 2017-10-30.
 */

@IgnoreExtraProperties
public class User {

    public String user_id;
    public String username;
    public String email;
    public int age;

    // 내가 작성한 글 목록을 user 가 가지고 있다
    public List<Bbs> bbsList;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String username, int age, String email) {
        this.username = username;
        this.age = age;
        this.email = email;
    }

}