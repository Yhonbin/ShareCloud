package com.firefly.sharemount.service;

public interface IdentityCheckingService {

    void sendEmailCode(String email);

    boolean checkEmailCode(String email, String code);

    boolean sendSmsCode(String phoneNumber);

    boolean checkSmsCode(String phoneNumber, String code);


}
