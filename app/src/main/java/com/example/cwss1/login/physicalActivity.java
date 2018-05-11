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

public class physicalActivity extends AppCompatActivity {

    private static String TAG = "SetJSon";

    private static final String TAG_ID = "ID";  //Key값
    private static final String TAG_EFFECT = "EFFECT";
    private static final String TAG_GOOD = "GOOD";
    private static final String TAG_BAD = "BAD";

    ArrayList<String> mArrayList1 = new ArrayList<String>();

    private TextView mTextViewResult;
    ArrayList<HashMap<String, String>> mArrayList;
    ArrayList<HashMap<String, String>> mArrayList2;
    ListView mlistView;
    ListView mlistView1;
    String mJsonString;
    Button button;
    String list,physical;
    String uid;

    Button btn4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_physical);

        mTextViewResult = (TextView)findViewById(R.id.textView_main_result);
        mlistView = (ListView) findViewById(R.id.listView_main_list);
        mlistView1 = (ListView) findViewById(R.id.listView_main_list1);
        mArrayList = new ArrayList<>();
        mArrayList2 = new ArrayList<>();

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();  //채팅을 요구 하는 아아디 즉 단말기에 로그인된 UID

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
                    physicalActivity.GetData task = new physicalActivity.GetData();
                    task.execute(getResources().getString(R.string.list1));

                }else if (list.equals("소화기 한약")) {
                    physicalActivity.GetData task = new physicalActivity.GetData();
                    task.execute(getResources().getString(R.string.list2));

                }else if (list.equals("천식치료 한약")) {
                    physicalActivity.GetData task = new physicalActivity.GetData();
                    task.execute(getResources().getString(R.string.list3));

                }else if (list.equals("아토피 치료 한약")) {
                    physicalActivity.GetData task = new physicalActivity.GetData();
                    task.execute(getResources().getString(R.string.list4));

                }else if (list.equals("열이나 피부 관련 질환")) {
                    physicalActivity.GetData task = new physicalActivity.GetData();
                    task.execute(getResources().getString(R.string.list5));
                }

                if (physical.equals("태양인")) {
                    physicalActivity.GetData1 task = new physicalActivity.GetData1();
                    task.execute(getResources().getString(R.string.physical1));
                }else if (physical.equals("태음인")) {
                    physicalActivity.GetData1 task = new physicalActivity.GetData1();
                    task.execute(getResources().getString(R.string.physical2));
                }else if (physical.equals("소양인")) {
                    physicalActivity.GetData1 task = new physicalActivity.GetData1();
                    task.execute(getResources().getString(R.string.physical3));
                }else if (physical.equals("소음인")) {
                    physicalActivity.GetData1 task = new physicalActivity.GetData1();
                    task.execute(getResources().getString(R.string.physical4));
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btn4 = (Button) findViewById(R.id.button1);

        btn4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(physicalActivity.this, bluetooth.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private class GetData extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(physicalActivity.this,
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
                String effect= item.getString(TAG_EFFECT);

                HashMap<String,String> hashMap = new HashMap<>(); // key, value 로 이루어진 배열같은 개념

                hashMap.put(TAG_ID, id);
                hashMap.put(TAG_EFFECT , effect);
                mArrayList.add(hashMap);
            }

            ListAdapter adapter = new SimpleAdapter(
                    physicalActivity.this, mArrayList, R.layout.list1_item,
                    new String[]{TAG_EFFECT },
                    new int[]{R.id.textView_list_name}
            );

            mlistView.setAdapter(adapter);


        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }


    private class GetData1 extends AsyncTask<String, Void, String> {
        ProgressDialog progressDialog;
        String errorString = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog = ProgressDialog.show(physicalActivity.this,
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
                showResult1();
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


    private void showResult1(){
        try {
            JSONArray jsonArray = new JSONArray(mJsonString); //jsonArray 선언

            for(int i=0;i<jsonArray.length();i++){ // jsonArray.length 수 만큼 반복

                JSONObject item = jsonArray.getJSONObject(i); // json객체 데이터를 구별

                String id = item.getString(TAG_ID); // "NAME" 의
                String effect= item.getString(TAG_EFFECT);
                String good = item.getString(TAG_GOOD);
                String bad = item.getString(TAG_BAD);


                HashMap<String,String> hashMap2 = new HashMap<>(); // key, value 로 이루어진 배열같은 개념

                hashMap2.put(TAG_ID, id);
                hashMap2.put(TAG_EFFECT , effect);
                hashMap2.put(TAG_GOOD , good);
                hashMap2.put(TAG_BAD , bad);
                mArrayList2.add(hashMap2);
            }

            ListAdapter adapter = new SimpleAdapter(
                    physicalActivity.this, mArrayList2, R.layout.list2_item,
                    new String[]{TAG_EFFECT,TAG_GOOD,TAG_BAD },
                    new int[]{R.id.textView_list_name , R.id.textView_list_good, R.id.textView_list_bad}
            );

            mlistView1.setAdapter(adapter);


        } catch (JSONException e) {
            Log.d(TAG, "showResult : ", e);
        }
    }
}


