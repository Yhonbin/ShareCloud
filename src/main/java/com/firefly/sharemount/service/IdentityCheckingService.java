package com.firefly.sharemount.service;

import javax.mail.MessagingException;

public interface IdentityCheckingService {

    void sendEmailCode(String email) throws MessagingException;

    boolean checkEmailCode(String email, String code);

    boolean sendSmsCode(String phoneNumber);

    boolean checkSmsCode(String phoneNumber, String code);


}
