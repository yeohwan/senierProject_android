package com.example.cwss1.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class json1 extends AppCompatActivity {

    private static String TAG = "SetJSon";

    private static final String TAG_ID = "ID";  //Key값
    private static final String TAG_NAME = "MEDICINE";  //Key값
    private static final String TAG_EFFECT = "EFFECT";

    ArrayList<String> mArrayList1 = new ArrayList<String>();

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ListView mlistView;
    String mJsonString;
    Button button;
    String list,physical;
    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_json1);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();  //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mlistView = (ListView) findViewById(R.id.listView_main_list);
        mArrayList = new ArrayList<>();
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference("users").child(uid).addListenerForSingleValueEvent(new ValueEventListener() { //DB의 users 하위 uid에 있는 데이터 중
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) { // 모든 데이터를 돔
                    mArrayList1.add(snapshot.getValue().toString()); // 배열에 데이터 넣음
                }

                list = mArrayList1.get(0).toString(); // list 한약 복용 정보
                physical = mArrayList1.get(1).toString();  // physcial 체형 정보

                System.out.print(list);

                if (list.equals("감기치료 한약")) {
                    GetData task = new GetData();
                    task.execute(getResources().getString(R.string.usersa));

                }else if (list.equals("소화기 한약")) {
                    GetData task = new GetData();
                    task.execute(getResources().getString(R.string.usersb));

                }else if (list.equals("천식치료 한약")) {
                    GetData task = new GetData();
                    task.execute(getResources().getString(R.string.usersc));

                }else if (list.equals("아토피 치료 한약")) {
                    GetData task = new GetData();
                    task.execute(getResources().getString(R.string.usersf));

                }else if (list.equals("열이나 피부 관련 질환")) {
                    GetData task = new GetData();
                    task.execute(getResources().getString(R.string.userse));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });




    }


    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(json1.this,
                    "Please Wait", null, true, true);
        }


        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            progressDialog.dismiss();
            // mTextViewResult.setText(result);
            Log.d(TAG, "response  - " + result);

            if (result == null){

                mTextViewResult.setText(errorString);
            }
            else {

                mJsonString = result;
                showResult();
            }
        }


        @Override
        protected String doInBackground(String... params) {

            String serverURL = params[0];


            try {

                URL url = new URL(serverURL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();


                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.connect();


                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d(TAG, "response code - " + responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == HttpURLConnection.HTTP_OK) {
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }


                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line;

                while((line = bufferedReader.readLine()) != null){
                    sb.append(line);
                }


                bufferedReader.close();


                return sb.toString().trim();


            } catch (Exception e) {
                Log.d(TAG, "InsertData: Error ", e);
                errorString = e.toString();
                return null;
            }
        }
    }


    private void showResult(){
        try {
            JSONArray jsonArray = new JSONArray(mJsonString); //jsonArray 선언

            for(int i=0;i<jsonArray.length();i++){ // jsonArray.length 수 만큼 반복

                JSONObject item = jsonArray.getJSONObject(i); // json객체 데이터를 구별

                String id = item.getString(TAG_ID); // "NAME" 의
                String name = item.getString(TAG_NAME); // "NAME" 의
                String effect= item.getString(TAG_EFFECT);

                HashMap<String,String> hashMap = new HashMap<>(); // key, value 로 이루어진 배열같은 개념

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_NAME, name);
                hashMap.put(TAG_EFFECT , effect);
                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    json1.this, mArrayList, R.layout.list_item,
                    new String[]{TAG_NAME },
                    new int[]{R.id.textView_list_name}
            );

            mlistView.setAdapter(adapter);

            mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(
                            getApplicationContext(), // 현재화면의 제어권자
                            detailActivity.class); // 다음넘어갈 화면

                    intent.putExtra("id", mArrayList.get(position).get(TAG_NAME));
                    intent.putExtra("name", mArrayList.get(position).get(TAG_EFFECT));

                    startActivity(intent);
                }
            });

        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}
