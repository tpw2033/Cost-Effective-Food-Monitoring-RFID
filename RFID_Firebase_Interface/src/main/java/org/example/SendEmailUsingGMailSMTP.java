package org.rfidinterface;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class SendEmailUsingGMailSMTP {

    public static boolean sendEmailToUser(String toEmail, String emailSubject, String emailMessage) {

        String to = toEmail;
        String from = "REDACTED";
        final String username = "REDACTED";
        final String password = "REDACTED";


        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");


        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);


            message.setFrom(new InternetAddress(from));


            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));


            message.setSubject(emailSubject);


            message.setText(emailMessage);

            Transport.send(message);

            return true;


        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


    public static void main(String[] args) {

        String to = "REDACTED";


        String from = "REDACTED";
        final String username = "REDACTED";
        final String password = "REDACTED";

        String host = "smtp.gmail.com";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", "587");
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");



        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });

        try {

            Message message = new MimeMessage(session);


            message.setFrom(new InternetAddress(from));


            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(to));


            message.setSubject("A food item is expiring soon!");


            message.setText("Expiring on 4/2/25:\n Oscar Mayer Bologna\n\nOpen the Food Monitoring app to see more information!");


            Transport.send(message);

            System.out.println("Sent message successfully....");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }


}
