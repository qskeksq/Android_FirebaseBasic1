package com.example.administrator.firebasebasic;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.administrator.firebasebasic.adapter.BbsAdapter;
import com.example.administrator.firebasebasic.adapter.UserAdapter;
import com.example.administrator.firebasebasic.model.Bbs;
import com.example.administrator.firebasebasic.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity implements UserAdapter.Callback {

    FirebaseDatabase database;
    DatabaseReference bbsRef, userRef;

    private RecyclerView userRecycler,bbsRecycler;
    private EditText editId, editName, editTitle;
    private UserAdapter adapter;
    private BbsAdapter bbsAdapter;

    private String user_id;
    private List<Bbs> bbsList;
    private List<List<Bbs>> bbsListList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initFireBase();
        initView();
        setAdapter();
        setListener();
    }

    /**
     * 파이어베이스 데이터베이스, 각 레퍼런스 초기화
     */
    private void initFireBase(){
        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");
        bbsRef = database.getReference("bbs");
    }

    /**
     * 뷰, 리스너
     */
    private void initView() {
        editId = (EditText) findViewById(R.id.editId);
        editName = (EditText) findViewById(R.id.editName);
        editTitle = (EditText) findViewById(R.id.itemTitle);
        userRecycler = (RecyclerView) findViewById(R.id.userRecycler);
        bbsRecycler = (RecyclerView) findViewById(R.id.bbsRecycler);
    }

    private void setListener() {
        userRef.addValueEventListener(userValueEventListener);
        bbsRef.addValueEventListener(bbsValueEventListener);
    }

    /**
     * 사용자 정보 저장
     */
    public void signup(View view) {

        String id = editId.getText().toString();
        String name = editName.getText().toString();
        String title = editTitle.getText().toString();

        User user = new User(name, 17, "none");
        // 현재 userRef 가 이미 "user"를 가리키고 있기 때문에 child(id)의 데이터셋으로 들어간다
        userRef.child(id).setValue(user);
    }

    /**
     * 사용자 계정으로 글 정보 저장
     */
    public void post(View view) {
        Bbs bbs = new Bbs();

        // 키 생성
        String id = bbsRef.push().getKey();
        // 아이템 고유 아이디
        bbs.id = id;
        // 사용자 아이디
        bbs.user_id = user_id;
        bbs.title = editTitle.getText().toString();
        bbs.date = (new Date()).toString();

        // bbs 레퍼런스에 저장
        bbsRef.child(id).setValue(bbs);
        // user 도 bbs 데이터를 복사해 저장하고 있다
        userRef.child(user_id).child("bbsList").child(id).setValue(bbs);
    }

    /**
     * 유저 ValueEventListener
     */
    ValueEventListener userValueEventListener = new ValueEventListener() {
        @Override               // 여기로 "user"(키) 아래 모든 데이터(값)가 찍혀 들어온다.
        public void onDataChange(DataSnapshot dataSnapshot) {
            // 각 유저가 보유한 데이터 리스트 저장
            bbsListList = new ArrayList<>();
            // 전체 유저 정보
            List<User> userList = new ArrayList<>();
            // 이미 id 레퍼런스를 참조한 상태에서 스냅샷을 찍기 때문에 바로 데이터로 넘어올 수 있다

            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                // 1. user_id
                String user_id = snapshot.getKey();
                // 2. age, email, username
                long age = (Long) snapshot.child("age").getValue();
                String email = (String) snapshot.child("email").getValue();
                String username = (String) snapshot.child("username").getValue();
                // 3. 키 값으로 저장된 데이터 리스트를 자동으로 변환해 주지 않기 때문에 따로 변환해준다
                List<Bbs> bbsList = new ArrayList<>();
                for (DataSnapshot bbsSnapshot : snapshot.child("bbsList").getChildren()) {
                    Bbs bbs = bbsSnapshot.getValue(Bbs.class);
                    bbsList.add(bbs);
                }

                User user = new User();
                user.user_id = user_id;
                user.age = (int) age;
                user.email = email;
                user.username = username;
                user.bbsList = bbsList;

                userList.add(user);
                bbsListList.add(bbsList);

            }

            // snapshot.getKey();   id 리턴(키)
            // snapshot.getValue();  id 를 제외한 데이터셋(값)

            // user 데이터 세팅
            adapter.setData(userList);
            adapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    /**
     * 글 목록 ValueEventListener
     * 참고로 위의 ValueEventListener 와 같이 초기에 세팅이 되기 때문에 굳이 위에서 글 목록을
     * 불러올 필요 없음
     */
    ValueEventListener bbsValueEventListener = new ValueEventListener() {

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            List<Bbs> bbsList = new ArrayList<>();
            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                Bbs bbs = snapshot.getValue(Bbs.class);
                bbsList.add(bbs);
            }
            bbsAdapter.setData(bbsList);
            bbsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

    public void setAdapter() {
        adapter = new UserAdapter(this);
        userRecycler.setAdapter(adapter);
        userRecycler.setLayoutManager(new LinearLayoutManager(this));

        bbsAdapter = new BbsAdapter();
        bbsRecycler.setAdapter(bbsAdapter);
        bbsRecycler.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * 반드시 생명주기 관리해준다
     */
    @Override
    protected void onPause() {
        super.onPause();
        userRef.removeEventListener(userValueEventListener);
        bbsRef.removeEventListener(bbsValueEventListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        userRef.addValueEventListener(userValueEventListener);
        bbsRef.addValueEventListener(bbsValueEventListener);

    }

    /**
     * 유저 선택시 글 목록 변경
     */
    @Override
    public void setCurrentUser(String user, int position) {
        this.user_id = user;
        Toast.makeText(this, "현재 id : " + user, Toast.LENGTH_SHORT).show();
        List<Bbs> bbsList = bbsListList.get(position);
        bbsAdapter.setData(bbsList);
        bbsAdapter.notifyDataSetChanged();
    }

}
