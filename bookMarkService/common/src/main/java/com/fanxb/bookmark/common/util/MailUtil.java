package com.fanxb.bookmark.common.util;

import com.fanxb.bookmark.common.entity.MailInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2019/4/7
 * Time: 16:08
 */
@Component
public class MailUtil {
    private static JavaMailSender mailSender;

    private static String from;

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        MailUtil.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    public void setFrom(String from){
        MailUtil.from = from;
    }


    public static void sendTextMail(MailInfo info){
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(from);
        simpleMailMessage.setTo(info.getReceiver());
        simpleMailMessage.setSubject(info.getSubject());
        simpleMailMessage.setText(info.getContent());
        mailSender.send(simpleMailMessage);
    }
}
