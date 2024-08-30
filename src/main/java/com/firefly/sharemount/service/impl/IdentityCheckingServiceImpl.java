package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.component.KeyValueTemplate;
import com.firefly.sharemount.service.IdentityCheckingService;
import com.firefly.sharemount.utils.VerifyingCodeUtil;
import com.firefly.sharemount.utils.RegexUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class IdentityCheckingServiceImpl implements IdentityCheckingService {
    @Resource
    private KeyValueTemplate keyValueTemplate;

    private static final Integer TIME_OUT_SECOND = 60;

    private static final String REDIS_EMAIL_VERIFICATION_FORMAT = "Email:%s";
    private static final String REDIS_SMS_VERIFICATION_FORMAT = "PhoneNumber:%s";

    @Override
    public void sendEmailCode(String email) {

        String code = VerifyingCodeUtil.generateVerifyCode(6);
        keyValueTemplate.set(String.format(REDIS_EMAIL_VERIFICATION_FORMAT,email),code);
        keyValueTemplate.setExpire(String.format(REDIS_EMAIL_VERIFICATION_FORMAT,email),TIME_OUT_SECOND);
        //todo
    }

    @Override
    public boolean checkEmailCode(String email, String code) {
        String catchCode = keyValueTemplate.get(String.format(REDIS_EMAIL_VERIFICATION_FORMAT, email));
        if (catchCode != null && catchCode.equals(code)) {
            // 销毁验证码
            keyValueTemplate.remove(String.format(REDIS_EMAIL_VERIFICATION_FORMAT, email));
            return true;
        }
        return false;
    }

    @Override
    public boolean sendSmsCode(String phoneNumber) {
        if (RegexUtil.isPhoneInvalid(phoneNumber)) {
            return false;
        }
        String code = VerifyingCodeUtil.generateVerifyCode(6);
        keyValueTemplate.set(String.format(REDIS_SMS_VERIFICATION_FORMAT,phoneNumber),code);
        keyValueTemplate.setExpire(String.format(REDIS_SMS_VERIFICATION_FORMAT,phoneNumber),TIME_OUT_SECOND);
        //todo

        return true;
    }

    @Override
    public boolean checkSmsCode(String phoneNumber, String code) {
        return false;
    }


}
