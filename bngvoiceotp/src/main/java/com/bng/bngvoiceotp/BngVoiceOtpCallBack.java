package com.bng.bngvoiceotp;

 public interface BngVoiceOtpCallBack {
    public void initialize(String status);
    public void success(String number, String status);
    public void failure(String reason);
    public void error(String error);
    public void deInitialize(String message);
}
