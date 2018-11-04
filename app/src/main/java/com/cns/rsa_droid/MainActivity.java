package com.cns.rsa_droid;

import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.util.*;
import java.io.DataInputStream;

//import static com.cns.rsa_droid.R.id.spinner;


public class MainActivity extends AppCompatActivity implements MyRecyclerViewAdapter.ItemClickListener {

    public DatabaseReference mDatabase;
    public static String my_ph="";
    public static String my_id="";
    public static BigInteger n,d,p,q;
    MyRecyclerViewAdapter adapter;
    ArrayList<String> chatrows = new ArrayList<>();
    public static DataSnapshot db;
    public static String latest_message="";
    public static String prev_message="";
    String targetno="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       // getActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#075e54")));

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat
                    .requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 12);
        }





        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyRecyclerViewAdapter(this, chatrows);
        //adapter.setClickListener(this);

        recyclerView.setAdapter(adapter);

        try{//Read user's phone number from file
            my_ph="";
            String path = Environment.getExternalStorageDirectory().toString()+"/Download/data/phone.txt";
            File directory = new File(path);
            FileInputStream fis = new FileInputStream(directory);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                my_ph = my_ph + strLine;}
            in.close();

            String a3="";
            path = Environment.getExternalStorageDirectory().toString()+"/Download/data/keys.txt";
            directory = new File(path);
            fis = new FileInputStream(directory);
            in = new DataInputStream(fis);
            br = new BufferedReader(new InputStreamReader(in));
            while ((strLine = br.readLine()) != null) {
                a3 = a3 + strLine;}
            in.close();
            String[] stringArray = a3.split(",");
            n=new BigInteger(stringArray[0]);
            d=new BigInteger(stringArray[1]);
            p=new BigInteger(stringArray[2]);
            q=new BigInteger(stringArray[3]);

            path = Environment.getExternalStorageDirectory().toString()+"/Download/data/contact.txt";
            directory = new File(path);
            fis = new FileInputStream(directory);
            in = new DataInputStream(fis);
            br = new BufferedReader(new InputStreamReader(in));
            String contact_str="";
            while ((strLine = br.readLine()) != null) {
                contact_str = contact_str + strLine;}
            in.close();

            EditText et4=(EditText) findViewById(R.id.editText4);
            et4.setText(contact_str);
            targetno=contact_str;


        }catch(Exception e){
            e.printStackTrace();
        }


        //point to root of db
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                TextView tv2=(TextView)findViewById(R.id.textView2);


                try
                {
                    db=dataSnapshot;

                   // String name =dataSnapshot.getValue().toString();
                   // tv2.setText("");
                    chatrows.clear();


                    for(DataSnapshot childx:db.child("u_"+my_ph).child("Messages").child("u_"+targetno).getChildren())
                    {

                        String message=childx.getValue().toString();
                        Log.d("m",message);
                        BigInteger c= new BigInteger(message);

                        BigInteger m_dec=c.modPow(d,n);
                        byte[] decrypted=m_dec.toByteArray();

                        String op="";
                        try
                        {
                            op=new String(decrypted,"UTF-8");
                        }
                        catch(Exception exc)
                        {
                            exc.printStackTrace();
                        }
                        latest_message=op;
                       // tv2.setText(tv2.getText()+"\n"+op);
                        chatrows.add(op);

                    }
                    RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rvChat);
                    adapter.notifyDataSetChanged();

                    recyclerView.scrollToPosition(chatrows.size() - 1);

                }catch(Exception ex)
                {
                    ex.printStackTrace();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });





        String nval="";
        try{
            String path = Environment.getExternalStorageDirectory().toString()+"/Download/data/keys.txt";
            File directory = new File(path);
            FileInputStream fis = new FileInputStream(directory);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                nval = nval + strLine;}
            in.close();
        }catch(FileNotFoundException ex){

            Random rnd1=new Random();
            BigInteger p=BigInteger.probablePrime(1024,rnd1);
            BigInteger q=BigInteger.probablePrime(1024,rnd1);
            BigInteger n=p.multiply(q);
            BigInteger phi=p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
            Integer e =(65537);
            BigInteger e_=BigInteger.valueOf(e);
            BigInteger d=e_.modInverse(phi);

            try{
                String path = Environment.getExternalStorageDirectory().toString()+"/Download/data/keys.txt";
                File directory = new File(path);

                String pkey=n.toString()+","+d.toString()+","+p.toString()+","+q.toString();
                FileOutputStream fos = new FileOutputStream(directory);
                fos.write(pkey.getBytes());
                fos.close();

            }catch(Exception ex3){
                ex3.printStackTrace();
            }



        }catch(Exception ex2){
            ex2.printStackTrace();
        }


        try{
            Vector<String>str=new Vector<String>();
            File file = new File(Environment.getExternalStorageDirectory().toString()+"/Download/data/contacts.txt");
            FileInputStream fileInputStream = new FileInputStream(file);
            BufferedReader in = new BufferedReader(new InputStreamReader(fileInputStream));

            String line = in.readLine();
            //int index = 0;
            while (line != null) {

                str.add(line);
                line = in.readLine();
            }


        }catch(Exception er){
            er.printStackTrace();
        }

        final EditText et4=(EditText)findViewById((R.id.editText4));

        et4.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                targetno=et4.getText().toString().trim();

            }
        });


        startService(new Intent(this, BackgroundService.class));



        /*
        Spinner spinners = (Spinner) findViewById(R.id.spinnerx);
        spinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                targetno=selectedItem.trim();
                TextView tv2=(TextView)findViewById(R.id.textView2);

                try
                {



                    // String name =dataSnapshot.getValue().toString();
                    tv2.setText("");
                    Log.d("tr",db.toString());
                    for(DataSnapshot childx:db.child("u_"+my_ph).child("Messages").child("u_"+targetno).getChildren())
                    {

                        String message=childx.getValue().toString();
                        Log.d("m","rere");
                        BigInteger c= new BigInteger(message);

                        BigInteger m_dec=c.modPow(d,n);
                        byte[] decrypted=m_dec.toByteArray();

                        String op="";
                        try
                        {
                            op=new String(decrypted,"UTF-8");
                        }
                        catch(Exception exc)
                        {
                            exc.printStackTrace();
                        }

                        tv2.setText(tv2.getText()+"\n"+op);

                    }

                }catch(Exception ex)
                {
                    ex.printStackTrace();
                }

            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

*/
    }

    public void encrypt_global_fn(View view) {


        try {
            File f = new File(Environment.getExternalStorageDirectory() + "/Download/data");
            if (!f.isDirectory()) {
                new File(Environment.getExternalStorageDirectory() + "/Download/data").mkdirs();
            }
            String path = Environment.getExternalStorageDirectory().toString() + "/Download/data/contact.txt";
            File directory = new File(path);
            FileOutputStream fos = new FileOutputStream(directory);

            EditText e4=(EditText)findViewById(R.id.editText4);
            fos.write(e4.getText().toString().trim().getBytes());
            fos.close();

        }catch(Exception e){
            e.printStackTrace();
        }
        EditText e1=(EditText)findViewById(R.id.editText);
        String str1 =e1.getText().toString();
        String id_str=db.child("u_"+my_ph).child("id").getValue().toString();
        my_id=id_str;
        String str2=id_str+": "+str1;
        e1.setText("");

        TextView tv2=(TextView)findViewById(R.id.textView2);
        //tv2.setText(tv2.getText()+"\nYou: "+str1);

        String n_str="";
        boolean toggle=false;
        try{
        Log.d("hep",db.child("u_"+targetno).toString());
         n_str=db.child("u_"+targetno).child("N").getValue().toString();

    }catch(java.lang.NullPointerException np){
            np.printStackTrace();
            Toast.makeText(MainActivity.this,"Number is invalid!",Toast.LENGTH_SHORT);
            toggle=true;


    }


        if(toggle==false)
        {
            BigInteger n1=new BigInteger(n_str);
            BigInteger m=new BigInteger(str2.getBytes());
            BigInteger c=m.modPow(BigInteger.valueOf(65537),n1);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("u_"+targetno).child("Messages").child("u_"+my_ph);

            mDatabase.push().setValue(c.toString());

            str2="You: "+ str1;
            m=new BigInteger(str2.getBytes());
            c=m.modPow(BigInteger.valueOf(65537),n);
            mDatabase = FirebaseDatabase.getInstance().getReference().child("u_"+my_ph).child("Messages").child("u_"+targetno);
            mDatabase.push().setValue(c.toString());
        }


        /*
        BigInteger m_dec=c.modPow(d,n);
        byte[] decrypted=m_dec.toByteArray();

        String op="";
        try
        {
            op=new String(decrypted,"UTF-8");
        }
        catch(Exception exc)
        {
            exc.printStackTrace();
        }

        TextView tv2=(TextView)findViewById(R.id.textView2);
        tv2.setText(c.toString() + ',' + op);
    `   */


        //mDatabase.child("ciphertext").setValue(c.toString());


        /*
        HashMap<String,String> dataMap=new HashMap<String,String>();
        dataMap.put("Name","Jayanth");
        dataMap.put("email","jayanth.rajakumar@gmail.com");
        mDatabase.push().setValue(dataMap);



        mDatabase.push().setValue(dataMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this,"Sent",Toast.LENGTH_SHORT);

                }
                else
                    Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_SHORT);
            }
        });
        */


    }

    public void btn_fn(View view) {
        startActivity(new Intent(MainActivity.this, Settings.class));
    }

    public void targetno_fn(View view) {
       // EditText et=(EditText)findViewById(R.id.editText4);
       // targetno=et.getText().toString().trim();

        FirebaseDatabase.getInstance().getReference().child("u_"+my_ph).child("Messages").removeValue();


    }
    @Override
    public void onItemClick(View view, int position) {
        Toast.makeText(this, "You clicked " + adapter.getItem(position) + " on row number " + position, Toast.LENGTH_SHORT).show();
    }
}
