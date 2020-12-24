package com.hzx.blog.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;


import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.util.Properties;

/**
 * @author Zixiang Hu
 * @description
 * @create 2020-12-18-11:01
 */
public class EmailUtil {
    private static Logger logger = LoggerFactory.getLogger(EmailUtil.class);

    /**
     * 配置邮件发送类，并返回实例
     *
     * @return
     * @throws IOException
     */
    public static JavaMailSenderImpl configEmail() throws IOException {
        Properties properties = new Properties();
        // 使用ClassLoader加载properties配置文件生成对应的输入流
        InputStream in = EmailUtil.class.getClassLoader().getResourceAsStream("email.properties");
        // 使用properties对象加载输入流
        properties.load(in);
        String emailUsername = properties.getProperty("emailUsername");
        String emailHost = properties.getProperty("emailHost");
        String emailPassword = properties.getProperty("emailPassword");
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(emailHost);
        javaMailSender.setUsername(emailUsername);
        javaMailSender.setPassword(emailPassword);
        javaMailSender.setDefaultEncoding("utf-8");
        //如果是部署在阿里云服务器上一定要开启ssl校验同时设置端口为587，因为阿里云屏蔽了25端口，如果只是在单机上测试，下面的代码可不加
        /*************************************************************************************************/
        javaMailSender.setPort(587);
        Properties properties1 = new Properties();
        properties1.setProperty("mail.smtp.auth", "true");//开启认证
        properties1.setProperty("mail.debug", "true");//启用调试
        properties1.setProperty("mail.smtp.timeout", "1000");//设置链接超时
        properties1.setProperty("mail.smtp.port", Integer.toString(587));//设置端口
        properties1.setProperty("mail.smtp.socketFactory.port", Integer.toString(587));//设置ssl端口
        properties.setProperty("mail.smtp.socketFactory.fallback", "false");
        properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        /***************************************************************************************************/
        javaMailSender.setJavaMailProperties(properties1);
        return javaMailSender;
    }

    /**
     * 发送邮件
     *
     * @param receiveEamil 接收者的邮箱
     * @param context      邮件内容
     */
    public static void sendEmail(String receiveEamil, String context) {
        try {
            JavaMailSenderImpl javaMailSender = configEmail();
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setText(context, true);
            mimeMessageHelper.setSubject("Blog回复通知");
            mimeMessageHelper.setTo(receiveEamil);
            mimeMessageHelper.setFrom(javaMailSender.getUsername());
            javaMailSender.send(mimeMessage);
            logger.info("邮件发送成功，邮箱是【{}】", receiveEamil);
        } catch (Exception e) {
            logger.error("邮件发送失败，邮箱是【{}】，原因是【{}】", receiveEamil, e.getCause());
        }
    }

    /**
     * 发送邮件
     *
     * @param receiveEmail 接收者的邮箱
     * @param blogTitle    博客标题
     * @param url          链接
     * @param isReply      是留言还是回复，true代表是新留言，false代表是回复
     */
    public static void sendEmail(String receiveEmail, String blogTitle, String url, boolean isReply) {
        url = "<a href=" + url + ">点击查看</a>。";
        String context;
        if (isReply)
            context = "你在《" + blogTitle + "》下的留言有了新的回复，" + url;
        else
            context = "你的博客《" + blogTitle + "》下有了新的留言，" + url;
        sendEmail(receiveEmail, context);
    }

    public static void main(String[] args) throws IOException {
        sendEmail("huzixiang_uestc@163.com", "开题报告阅读文献笔记", "http://47.110.126.140/blog/54", true);
    }
}
