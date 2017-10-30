package com.example.administrator.firebasebasic.model;

/**
 * Created by Administrator on 2017-10-30.
 */

public class Bbs {

    public String id;
    public String title;
    public String content;
    public String date;
    public String user_id;

    // FireBase 에서 parsing 할 때 default 생성자 필수
    public Bbs() {

    }

    public Bbs(String id, String title, String content, String date, String user_id) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.date = date;
        this.user_id = user_id;
    }
}
