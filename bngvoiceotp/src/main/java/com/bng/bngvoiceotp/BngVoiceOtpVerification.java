package com.bng.bngvoiceotp;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Handler;
import android.provider.CallLog;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;



import com.google.gson.JsonObject;

import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BngVoiceOtpVerification implements CallState {

    private String TAG = "BngVoiceOtpVer";
    private boolean showLogs  = false;
    private Activity mContext;
    public BngVoiceOtpCallBack bngVoiceOtpCallBack;
    private BroadcastReceiver broadcastReceiver;
    private String userMobileNo = "";
    private int callRetry = 1;
    private String cliNumber = "";
    private static BngVoiceOtpVerification instance;
    private Date dateT;
    private Handler handler;
    private long sdkTimeOutInSec = 60; // Default timeOut

    private BngVoiceOtpVerification(){
    }

    public static BngVoiceOtpVerification getInstance(){
        if(instance== null){
            instance = new BngVoiceOtpVerification();
        }
        return instance;
    }

   /* public BngVoiceOtpVerification(){

    }*/

    public void callState(String state) {
        try {
            if (state.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                endCallRequest();
            }
            if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)) {

            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    @RequiresPermission("android.permission.READ_PHONE_STATE")
    public void initialize(Activity context){
        this.mContext = context;

        if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            bngVoiceOtpCallBack.error("READ_PHONE_STATE permission not allowed");
            return;
        }
        if (ApiClient.BASE_URL.equals("")){
            bngVoiceOtpCallBack.error("Base url not found ");
            return;
        }

        if (userMobileNo.equals("")){
            bngVoiceOtpCallBack.error("A party number not found");
            return;
        }
        broadcastReceiver = new CallStateReceiver();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("android.intent.action.PHONE_STATE");
        mContext.registerReceiver(broadcastReceiver, mIntentFilter);
        bngVoiceOtpCallBack.initialize("initialized");
        initiateForCall();
    }

    public void deInitialize(){
        if (broadcastReceiver != null) {
            if (handler != null){
                handler.removeCallbacksAndMessages(null);
            }
            handler = null;
            Log.d(TAG,"deInitialize()");
            bngVoiceOtpCallBack.deInitialize("SDK DeInitialize");
            mContext.unregisterReceiver(broadcastReceiver);
            broadcastReceiver = null;
            userMobileNo = "";
            showLogs = false;
            dateT = null;
            ApiClient.BASE_URL = "";
            ApiClient.isHttpsRequest = false;
        }
    }


    public void setSdkTimeOut(long sdkTimeOutInSec){
        this.sdkTimeOutInSec = sdkTimeOutInSec;
    }

    public void setUserMobileNumber(String userMobileNo){
        this.userMobileNo = userMobileNo;
    }

    public void setBaseUrl(String baseUrl){
        ApiClient.BASE_URL = baseUrl;
    }

    public void enableLogs(boolean isShowLogs){
        showLogs = isShowLogs;
    }

    public void setConnectionTimeout(long timeInSecond){
        ApiClient.connectionTimeout = timeInSecond;
    }

    public void setRequestTimeout(long timeInSecond){
        ApiClient.requestTimeout = timeInSecond;
    }

    public void enableHttpsRequest(boolean isEnableHttpsRequest){
        ApiClient.isHttpsRequest = isEnableHttpsRequest;
    }

    public void setCallRetry (int callRetry){
        this.callRetry = callRetry;
    }

    private void printLogs(String message){
        if (showLogs){
            Log.d("BngVoiceOtpVeri", message);
        }
    }

    @RequiresPermission("android.permission.READ_CALL_LOG")
    public void bngVoiceOtpCallBack(BngVoiceOtpCallBack callBack) {
        bngVoiceOtpCallBack = callBack;
    }

    private void initiateForCall() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("msisdn", userMobileNo);
        if(dateT==null) {
            dateT = new Date();
        }
        requestForCall(jsonObject);

    }

    private void requestForCall(JsonObject requestMsisdn){

        ApiClient.getApiService().requestForCall(requestMsisdn).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    printLogs("requestForCall API response : " + response.body().toString());

                    String status = response.body().get("status").getAsString();
                    if (status.equalsIgnoreCase("success")) {
                        if (response.body().has("cli")){
                            cliNumber = response.body().get("cli").getAsString();
                            if (!cliNumber.equals("")){
                                verifySdkTimeOut();
                                bngVoiceOtpCallBack.callInitiate(cliNumber,response.body().get("reason").getAsString());
                            }else {
                                bngVoiceOtpCallBack.failure("Cli number not found");
                            }
                        }else {
                            bngVoiceOtpCallBack.failure("Cli number is missing");
                        }
                    } else {
                        if (response.body().has("reason")) {
                            bngVoiceOtpCallBack.failure(response.body().get("reason").getAsString());
                        }else {
                            bngVoiceOtpCallBack.failure("Reason not found");
                        }
                    }
                }
                else {
                    bngVoiceOtpCallBack.failure("response code " + response.code());
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                bngVoiceOtpCallBack.failure("response code " + t.getLocalizedMessage());
            }
        });
    }




    private void endCallRequest() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("msisdn", userMobileNo);
        endCallRequest(jsonObject);
    }


    private void verifySdkTimeOut(){

        handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bngVoiceOtpCallBack.failure("SDK time out");
                handler = null;
                deInitialize();
            }
        }, sdkTimeOutInSec * 1000);
    }




    private void endCallRequest(JsonObject requestMsisdn){
        ApiClient.getApiService().endCallRequest(requestMsisdn).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    printLogs("endCallRequest API response : " + response.body().toString());

                    String status = response.body().get("status").getAsString();
                    if (status.equalsIgnoreCase("success")) {
                        goForGetCalLogs();
                    } /*else {
                        bngVoiceOtpCallBack.failure("response code " + response.code());
                    }*/
                }
            }
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                bngVoiceOtpCallBack.failure("endCallRequest response code " + t.getLocalizedMessage());
            }
        });
    }

    private void goForGetCalLogs(){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                getCallDetails();
            }
        }, 2000);
    }

    private void getCallDetails() {
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor =  mContext.managedQuery(CallLog.Calls.CONTENT_URI, null, null,null,  CallLog.Calls.DATE  +" DESC limit 5");

        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        sb.append("Call Numbers :");
        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            long newDate = Long.parseLong(managedCursor.getString(date));
            if(dateT!=null) {
                if (dateT.getTime() < newDate) {
                    sb.append(phNumber).append(",");
                }
            }
        }

        verifyCliNumber(sb.toString());
        Log.d(TAG,"verifyCliNumber():"+sb.toString());
    }

    private void verifyCliNumber(String findNumbers){
        if (findNumbers.contains(cliNumber)){
            bngVoiceOtpCallBack.success(cliNumber, "Verified number ");
            deInitialize();
        }else {
            bngVoiceOtpCallBack.numberNotMatch(cliNumber, findNumbers);
        }
    }

}