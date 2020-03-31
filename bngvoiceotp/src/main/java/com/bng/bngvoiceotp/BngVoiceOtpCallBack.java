package com.bng.bngvoiceotp;

 public interface BngVoiceOtpCallBack {
    void initialize(String status);
    void callInitiate(String cliNumber, String message);
    void success(String number, String status);
    void numberNotMatch(String cliNumber, String callLogs);
    void failure(String reason);
    void error(String error);
    void deInitialize(String message);
}
