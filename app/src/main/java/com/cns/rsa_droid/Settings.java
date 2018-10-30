package com.cns.rsa_droid;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import android.content.Context;
import android.graphics.Path;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.util.Random;

public class Settings extends AppCompatActivity {
    public DatabaseReference mDatabase;
    String a1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        try{
            a1=""; String a2="";

            String path = Environment.getExternalStorageDirectory().toString()+"/Download/data/phone.txt";
            File directory = new File(path);
            FileInputStream fis = new FileInputStream(directory);
            DataInputStream in = new DataInputStream(fis);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String strLine;
            while ((strLine = br.readLine()) != null) {
                a1 = a1 + strLine;}
            in.close();

            path = Environment.getExternalStorageDirectory().toString()+"/Download/data/username.txt";
             directory = new File(path);
             fis = new FileInputStream(directory);
             in = new DataInputStream(fis);
             br = new BufferedReader(new InputStreamReader(in));
            while ((strLine = br.readLine()) != null) {
                a2 = a2 + strLine;}
            in.close();

            EditText tv2=(EditText) findViewById(R.id.editText2);
            tv2.setText(a1);
            EditText tv3=(EditText) findViewById(R.id.editText3);
            tv3.setText(a2);

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

            TextView tv=(TextView)findViewById(R.id.textView);
            tv.setText("65537, " + stringArray[1]);

        }
        catch(Exception ex)
            {
                ex.printStackTrace();
            }


    }

    public void save_fn(View view) {
        FileOutputStream fos;

        EditText tv2=(EditText) findViewById(R.id.editText2);
        String phn=tv2.getText().toString();
        MainActivity.my_ph=phn;
        EditText tv3=(EditText) findViewById(R.id.editText3);
        String usr=tv3.getText().toString();

        try {

            File f = new File(Environment.getExternalStorageDirectory() + "/Download/data");
            if(!f.isDirectory())
            {
                new File(Environment.getExternalStorageDirectory() + "/Download/data").mkdirs();
            }

            String path = Environment.getExternalStorageDirectory().toString()+"/Download/data/phone.txt";
            File directory = new File(path);

            fos = new FileOutputStream(directory);
            fos.write(phn.getBytes());
            fos.close();

            path = Environment.getExternalStorageDirectory().toString()+"/Download/data/username.txt";
            directory = new File(path);
            fos = new FileOutputStream(directory);
            fos.write(usr.getBytes());
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void keygen(View view) {

        Random rnd1=new Random();
        BigInteger p=BigInteger.probablePrime(1024,rnd1);
        BigInteger q=BigInteger.probablePrime(1024,rnd1);
        BigInteger n=p.multiply(q);
        BigInteger phi=p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        Integer e =(65537);
        BigInteger e_=BigInteger.valueOf(e);
        BigInteger d=e_.modInverse(phi);

        try{
            File f = new File(Environment.getExternalStorageDirectory() + "/Download/data");
            if(!f.isDirectory())
            {
                new File(Environment.getExternalStorageDirectory() + "/Download/data").mkdirs();
            }
            String path = Environment.getExternalStorageDirectory().toString()+"/Download/data/keys.txt";
            File directory = new File(path);

            MainActivity.d=d;
            MainActivity.p=p;
            MainActivity.q=q;
            MainActivity.n=n;

            String pkey=n.toString()+","+d.toString()+","+p.toString()+","+q.toString();
            FileOutputStream fos = new FileOutputStream(directory);
            fos.write(pkey.getBytes());
            fos.close();

            TextView tv=(TextView)findViewById(R.id.textView);
            tv.setText("65537, " + n.toString());

            Log.d("d",n.toString());

        }catch(Exception ex3){
            ex3.printStackTrace();
        }
        EditText et3=(EditText)findViewById(R.id.editText3);
        EditText tv2=(EditText) findViewById(R.id.editText2);

        a1=tv2.getText().toString().trim();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("u_"+a1);
        mDatabase.child("N").setValue(n.toString());
        mDatabase.child("e").setValue("65537");
        mDatabase.child("id").setValue(et3.getText().toString());


    }

    public void clr_db(View view) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.removeValue();
    }
}
