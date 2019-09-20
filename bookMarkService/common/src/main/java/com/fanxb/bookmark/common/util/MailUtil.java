package com.fanxb.bookmark.common.util;

import com.fanxb.bookmark.common.entity.MailInfo;
import com.fanxb.bookmark.common.exception.CustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

/**
 * Created with IntelliJ IDEA
 * Created By Fxb
 * Date: 2019/4/7
 * Time: 16:08
 */
@Component
@Slf4j
public class MailUtil {
    private static JavaMailSender mailSender;

    private static String from;

    @Autowired
    public void setMailSender(JavaMailSender mailSender) {
        MailUtil.mailSender = mailSender;
    }

    @Value("${spring.mail.username}")
    public void setFrom(String from) {
        MailUtil.from = from;
    }

    public static void sendMail(MailInfo info, boolean isHtml) {
        try {

            MimeMessageHelper helper = new MimeMessageHelper(mailSender.createMimeMessage());
            helper.setFrom(from);
            helper.setTo(info.getReceiver());
            helper.setSubject(info.getSubject());
            helper.setText(info.getContent(), isHtml);
            mailSender.send(helper.getMimeMessage());
        } catch (Exception e) {
            throw new CustomException("发送邮件失败:" + e.getMessage(), e);
        }

    }


    public static void sendTextMail(MailInfo info) {
        sendMail(info, false);
    }


}
