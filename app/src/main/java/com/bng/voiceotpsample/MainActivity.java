package com.bng.voiceotpsample;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;

import com.bng.bngvoiceotp.BngVoiceOtpCallBack;
import com.bng.bngvoiceotp.BngVoiceOtpVerification;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        BngVoiceOtpVerification bngVer = BngVoiceOtpVerification.getInstance();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        bngVer.bngVoiceOtpCallBack(new BngVoiceOtpCallBack() {
            @Override
            public void initialize(String status) {

            }

            @Override
            public void callInitiate(String cliNumber, String message) {

            }

            @Override
            public void success(String number, String status) {

            }

            @Override
            public void numberNotMatch(String cliNumber, String callLogs) {

            }

            @Override
            public void failure(String reason) {

            }

            @Override
            public void error(String error) {

            }

            @Override
            public void deInitialize(String message) {

            }
        });

        bngVer.setBaseUrl("app.magiccall.co/api/");

        bngVer.initialize(this);



    }
}
