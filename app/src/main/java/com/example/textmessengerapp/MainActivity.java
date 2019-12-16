package com.example.textmessengerapp;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.security.spec.ECField;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

    //    ActivityCompat.OnRequestPermissionsResultCallback
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.currentThread().setPriority(6);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ContactPermission();
        SMSpermission();
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView warning = (TextView) findViewById(R.id.warning);
                EditText message = (EditText) findViewById(R.id.message);
                EditText phone_number = (EditText) findViewById(R.id.phoneNumber);
                if (TextUtils.isEmpty(message.getText()) || !android.util.Patterns.PHONE.matcher(phone_number.getText().toString()).matches()
                        || phone_number.getText().toString().length() != 13) {
                    warning.setText("Either contact number OR message is invalid!");
                    warning.setVisibility(View.VISIBLE);
                } else {
                    SMSpermission();
                    warning.setVisibility(View.GONE);
                    String finalmessage = message.getText().toString();
                    String phone = phone_number.getText().toString();
                    sendMessage(finalmessage, phone);
                }
            }
        });

        ImageButton img = findViewById(R.id.imageButton);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContactPermission();
                TextView name = (TextView) findViewById(R.id.name);
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
                name.setVisibility(View.VISIBLE);

            }
        });
    }///end onCreate Method

    public void SMSpermission() {
        Thread.currentThread().setPriority(9);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS},
                    MY_PERMISSIONS_REQUEST_SEND_SMS);
        }
    }

    public void sendMessage(String finalMessage, String contact_number) {
        try {
            SmsManager sms = SmsManager.getDefault();
            ArrayList<String> parts = sms.divideMessage(finalMessage);
            sms.sendMultipartTextMessage(contact_number, null, parts, null, null);
            Toast.makeText(MainActivity.this, "Your message has been Sent Successfully", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to Send SMS!", Toast.LENGTH_SHORT).show();
        }
    }

    public void ContactPermission() {
        Thread.currentThread().setPriority(10);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }
    }

    @Override
    public void onActivityResult(int RequestCode, int ResultCode, Intent ResultIntent) {
        super.onActivityResult(RequestCode, ResultCode, ResultIntent);
        TextView name = (TextView) findViewById(R.id.name);
        EditText number = (EditText) findViewById(R.id.phoneNumber);
        switch (RequestCode) {
            case (MY_PERMISSIONS_REQUEST_READ_CONTACTS):
                if (ResultCode == Activity.RESULT_OK) {
                    try {
                        Uri uri;
                        Cursor cursor1, cursor2;
                        String TempNameHolder, TempNumberHolder, TempContactID, IDresult;
                        int IDresultHolder;
                        uri = ResultIntent.getData();
                        cursor1 = getContentResolver().query(uri, null, null, null, null);
                        if (cursor1.moveToFirst()) {
                            TempNameHolder = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            TempContactID = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts._ID));
                            IDresult = cursor1.getString(cursor1.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                            IDresultHolder = Integer.valueOf(IDresult);

                            if (IDresultHolder == 1) {
                                cursor2 = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + TempContactID, null, null);
                                while (cursor2.moveToNext()) {
                                    TempNumberHolder = cursor2.getString(cursor2.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                    name.setText(TempNameHolder);
                                    number.setText(TempNumberHolder);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "Failed to Fetch data from contacts!", Toast.LENGTH_SHORT).show();
                    }
                }
        }//end switch
    }//end OnActivityResult

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Please click 'Allow' to send SMS!", Toast.LENGTH_LONG).show();
                }
                break;
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Contact Access Permission Granted", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, "Please click 'Allow' to Access Contacts!", Toast.LENGTH_LONG).show();
                }
                break;
        }//end Switch
    }//end onRequestPermissionsResult
}//end MainActivity
