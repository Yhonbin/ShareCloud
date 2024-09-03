package com.firefly.sharemount.service.impl;

import com.firefly.sharemount.component.RedisTemplateComponent;
import com.firefly.sharemount.config.ApplicationConfiguration;
import com.firefly.sharemount.service.IdentityCheckingService;
import com.firefly.sharemount.utils.VerifyingCodeUtil;
import com.firefly.sharemount.utils.RegexUtil;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.concurrent.TimeUnit;

@Service
public class IdentityCheckingServiceImpl implements IdentityCheckingService {
    @Resource
    private RedisTemplateComponent redisTemplateComponent;

    @Resource
    private JavaMailSenderImpl mailSender;

    @Resource
    private ApplicationConfiguration applicationConfiguration;

    private static final Integer TIME_OUT_SECOND = 60;

    private static final String REDIS_EMAIL_VERIFICATION_FORMAT = "Email:%s";
    private static final String REDIS_SMS_VERIFICATION_FORMAT = "PhoneNumber:%s";

    @Override
    public void sendEmailCode(String email) throws MessagingException {
        applicationConfiguration.loadConfig();
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        String code = VerifyingCodeUtil.generateVerifyCode(6);
        helper.setText("<p style='color: blue'>你正在注册共享云盘服务，你的验证码为："+ code +"（有效期为三分钟）</p>",true);
        helper.setSubject("sharemount--验证码");
        helper.setTo(email);
        // 获取验证码
        String senderEmail = (String)applicationConfiguration.getNestedConfig("spring.mail.username");
        helper.setFrom(senderEmail);
        redisTemplateComponent.set(String.format(REDIS_EMAIL_VERIFICATION_FORMAT,email),code);
        redisTemplateComponent.setExpire(String.format(REDIS_EMAIL_VERIFICATION_FORMAT,email),TIME_OUT_SECOND* 3L, TimeUnit.SECONDS);
        mailSender.send(mimeMessage);

    }

    @Override
    public boolean checkEmailCode(String email, String code) {
        String catchCode = redisTemplateComponent.get(String.format(REDIS_EMAIL_VERIFICATION_FORMAT, email));
        if (catchCode != null && catchCode.equals(code)) {
            // 销毁验证码
            redisTemplateComponent.remove(String.format(REDIS_EMAIL_VERIFICATION_FORMAT, email));
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
        redisTemplateComponent.set(String.format(REDIS_SMS_VERIFICATION_FORMAT,phoneNumber),code);
        redisTemplateComponent.setExpire(String.format(REDIS_SMS_VERIFICATION_FORMAT,phoneNumber),TIME_OUT_SECOND,TimeUnit.SECONDS);
        //todo 发送短信验证码

        return true;
    }

    @Override
    public boolean checkSmsCode(String phoneNumber, String code) {
        //todo 检验短信验证码
        return false;
    }


}
