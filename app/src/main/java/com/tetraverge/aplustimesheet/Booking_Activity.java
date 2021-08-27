package com.tetraverge.aplustimesheet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.HashMap;

public class Booking_Activity extends AppCompatActivity {
    EditText nameuser, namecompany, contactcompany, typeofjob;
    TextView starttime, endtime;
    private FirebaseAuth mAuth;
    private DatabaseReference bookingDB;
    private String userId,username, companyname,companycontact,jobtype,jobstarttime,jobendtime;
    private ProgressDialog loadingbar;
    Button submitBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_);

        submitBtn = (Button) findViewById(R.id.subBtn);

        nameuser = (EditText) findViewById(R.id.userName);
        namecompany = (EditText) findViewById(R.id.companyName);
        contactcompany = (EditText) findViewById(R.id.companyContact);
        typeofjob = (EditText) findViewById(R.id.enterJobType);
        starttime = (TextView) findViewById(R.id.startTime);
        endtime = (TextView) findViewById(R.id.endTime);
        starttime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Booking_Activity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        starttime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });
        endtime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(Booking_Activity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        endtime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        bookingDB = FirebaseDatabase.getInstance().getReference().child("Booking");
        loadingbar = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                storeDataToDb();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentuser = mAuth.getCurrentUser();
        if (currentuser==null)
        {
            sendusertologinactivity();
            Toast.makeText(Booking_Activity.this,"You are Not Logged In Please Login or Register",Toast.LENGTH_LONG).show();
        }
        else
        {
            userId = mAuth.getCurrentUser().getUid();

        }
    }
    private void sendUsertoMainActivity() {
        Intent intent = new Intent(Booking_Activity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void sendusertologinactivity()
    {
        Intent intent = new Intent(Booking_Activity.this, Login_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


    private void storeDataToDb()
    {
        username = nameuser.getText().toString();
        companyname = namecompany.getText().toString();
        companycontact = contactcompany.getText().toString();
        jobtype = typeofjob.getText().toString();
        jobstarttime = starttime.getText().toString();
        jobendtime = endtime.getText().toString();

        if (username.isEmpty())
        {

            Toast.makeText(Booking_Activity.this, "Please Enter Your Name", Toast.LENGTH_SHORT).show();
        }
        else if ( companyname.isEmpty())
        {
            Toast.makeText(Booking_Activity.this, "Please Enter Company Name", Toast.LENGTH_SHORT).show();
        }
        else {

            loadingbar.setTitle("Wait Please");
            loadingbar.setMessage("Sending Your Booking...   ");
            loadingbar.show();

            HashMap postHash = new HashMap();
            postHash.put("userid",userId);
            postHash.put("username",username);
            postHash.put("companyname",companyname);
            postHash.put("companycontact",companycontact);
            postHash.put("jobtype",jobtype);
            postHash.put("jobstarttime",jobstarttime);
            postHash.put("jobendtime",jobendtime);
            bookingDB.push().setValue(postHash).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    if (task.isSuccessful())
                    {
                        loadingbar.dismiss();
                        Toast.makeText(Booking_Activity.this, "Thank You We Will Contact You Soon", Toast.LENGTH_SHORT).show();
                        sendUsertoMainActivity();
                    }
                    else {
                        loadingbar.dismiss();
                        String erroreMassage = task.getException().getMessage();
                        Toast.makeText(Booking_Activity.this, "Errore"+ erroreMassage, Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }

    }

}