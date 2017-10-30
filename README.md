# FireBase basics
- 실시간 유저 정보 추가, 유저에 따른 글 목록 변경

- Backend를 서비스 하는 방법
    - 웹호스팅 : 웹호스팅 업체에서 하드웨어 공간(업체가 소유한 HDD)을 가격만큼 임대
    - 서버호스팅 : 웹호스팅 업체에서 하드웨어 공간(업체가 소유한 HDD)을 전체 임대
    - 클라우드 : 여러개의 하드웨어를 논리적으로 하나의 공간처럼 사용. 하드웨어는 여러개이나 하나처럼 사용할 수 있으며
    사용자의 요구에 의해 자유롭게, 실시간으로 서비스를 차단하지 않고도 용량을 증설할 수 있다.
    - 주로 웹호스팅->클라우드 서버->서버호스팅 형식으로

- 업체에서 제공하는 Backend 서비스
    - Baas(Backend as a service)
        - 기본 로그인 기능
        - 테이블
    - Paas(Platform as a service)
        - Security
        - Application
        - Web
        - Database
    - AWS
        - 껍데기만 제공

- 사용 설명
    - Authentication
        - 사용자 인증 처리
    - Function
        - 서버 스크립
    - TestLab
        - 테스팅 툴
    - Crash Reporting
        - try-catch로 감싼 
        - 사용자 에러를 원격으로 받아봄
    - Notification
        - FCM
    - Remote Config
        - 사용자에게 설치된 앱 조작

- 수동으로 파이어베이스 추가하기
    - 1. json 파일 안드로이드에 설치(project 파일이 아니라 /app에 추가해야 함)
    - 2. 그래들 2개 추가

- 자동으로 추가하기

## RealTime Database 사용

- 데이터베이스 생성
- Rules true로 바꿔줌
- 외부에서 추가할 경우 compile 'com.google.firebase:firebase-database:11.0.1' 추가. 버전이 맞지 않으면 10.+로 바꿔준다

## NoSql 데이터구조의 이해

- NoSql은 원하는 데이터를 '값'으로 검색할 수 없고 오직 키로만 검색할 수 있다. 따라서 원하는 데이터를 태그라는 키로 따로 저장해야 한다.
- 검색 속도는 가장 빠르다. 왜냐하면 태그(키)값으로 1:1로 매핑이 되어 바로 찾을 수 있기 때문이다.
- 다만 #가 있는 모든 데이터가 tags에 복제되어야 하기 때문에 데이터 양은 8배 10배 늘어날 수 있다. 즉, 원 데이터를 태그(키)로 검색할 수 있도록 필요한 곳마다 복제를 되어야 한다. 
- 수정, 삭제는 복제된 모든 데이터가 수정, 삭제되어야 한다

![](https://github.com/qskeksq/FirebaseBasic/blob/master/NoSql%20%EA%B5%AC%EC%A1%B0.png)

### 초기화

```java
private void initFireBase(){
    database = FirebaseDatabase.getInstance();
    userRef = database.getReference("user");
    bbsRef = database.getReference("bbs");
}
```

### User 데이터 저장
```java
public void signup(View view) {

    String id = editId.getText().toString();
    String name = editName.getText().toString();
    String title = editTitle.getText().toString();

    User user = new User(name, 17, "none");
    // 현재 userRef 가 이미 "user"를 가리키고 있기 때문에 child(id)의 데이터셋으로 들어간다
    userRef.child(id).setValue(user);
}
```

### Bbs 데이터 저장
- push().getKey()를 통해 노드(키) 생성
- 레퍼런스의 child().child().child()를 통해 원하는 하위 데이터까지 찾아간다
- 사용자 계정(User)에도 저장한다. 이는 '값'으로 데이터를 검색할 수 없기 때문에 필요한 곳에 모든 데이터를 복사해서 저장하는 것이다
- 따로 Bbs 레퍼런스에 저장

```java
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
```


### ValueEventListener1 - User 데이터 변경 이벤트
- 초기 세팅시 호출
- 데이터 변경시 호출
- 지정한 child('키')까지 내려간 다음 스냅샷을 찍어 리턴, 리턴된 스냅샷에서 getKey(), getValue(), getChildren()을 통해 키, 값을 바로 꺼내거나 추가로 하위 데이터를 꺼내 사용한다
- snapshot.getKey();   id 리턴(키)
- snapshot.getValue();  id 를 제외한 데이터셋(값)
- snapshot.getChildren();   바로 데이터를 꺼내지 않고 하위의 데이터로 이동. 여기서 다시 DataSnapShot을 꺼낼 수 있다.


```java
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
        // user 데이터 세팅
        adapter.setData(userList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
};
```
### ValueEventListener2 - Bbs 데이터 변경 이벤트
- 초기 세팅시 호출
- 데이터 변경시 호출
- 위의 ValueEventListener 와 같이 초기에 세팅이 되기 때문에 굳이 위에서 글 목록을 불러올 필요 없음

```java
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
```