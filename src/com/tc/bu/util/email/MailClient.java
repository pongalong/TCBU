package com.tc.bu.util.email;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.SendFailedException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailClient {
  public static final String SYSTEM_SENDER = "system-tcbu@truconnect.com";
  public static final String telscapeSMTPHost = "209.127.162.26";
  public static final String truConnectSMTPHost = "209.127.161.5";
  Set<Address> to = new HashSet<Address>();
  Set<Address> bcc = new HashSet<Address>();

  public MailClient() {
    try {
      bcc.add(new InternetAddress("peter.maas@truconnect.com", "Peter Maas"));
      bcc.add(new InternetAddress("trualert@truconnect.com", "TruConnect Alerts"));
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
  }

  public void postMail(String subject, String message, String from) {
    try {
      postMail(to, subject, message, from);
    } catch (MessagingException me) {
      me.printStackTrace();
    }
  }

  private Message buildMessage(Set<Address> to, String from, String subject, String message) {
    Properties props = new Properties();
    props.put("mail.smtp.host", truConnectSMTPHost);
    Session session = Session.getDefaultInstance(props, null);
    session.setDebug(false);
    try {
      Message msg = new MimeMessage(session);
      msg.setFrom(new InternetAddress(from, "TruConnect TCBU System"));
      msg.setRecipients(Message.RecipientType.TO, to.toArray(new InternetAddress[0]));
      msg.setRecipients(Message.RecipientType.BCC, bcc.toArray(new InternetAddress[0]));
      msg.addHeader("MyHeaderName", "myHeaderValue");
      msg.setSubject(subject);
      msg.setContent(message, "text/html");
      return msg;
    } catch (MessagingException e) {
      e.printStackTrace();
      return new MimeMessage(session);
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
      return new MimeMessage(session);
    }
  }

  public void postMail(Set<Address> to, String from, String subject, String message) throws MessagingException {
    Message msg = buildMessage(to, from, subject, message);
    try {
      Transport.send(msg);
    } catch (SendFailedException e) {
      e.printStackTrace();
      Set<Address> validAddresses = new HashSet<Address>(Arrays.asList(e.getValidSentAddresses()));
      postMail(validAddresses, from, subject, message);
      try {
        Set<Address> errorRecipients = new HashSet<Address>();
        InternetAddress ia = new InternetAddress("trualert@truconnect.com", "TruConnect TCBU System");
        errorRecipients.add(ia);
        String exceptionMessage = "EXCEPTION MESSAGE:\n" + e.getMessage();
        String originalTo = "ORIGINAL RECIPIENTS:\n" + to.toString();
        String validTo = "VALID RECIPIENTS:\n" + e.getValidSentAddresses().toString();
        String invalidTo = "INVALID RECIPIENTS:\n" + e.getInvalidAddresses().toString();
        String errorMessageBody = exceptionMessage + "\n\n" + originalTo + "\n\n" + validTo + "\n\n" + invalidTo;
        postMail(errorRecipients, from, "Error Sending RPI Notification", errorMessageBody);
      } catch (UnsupportedEncodingException uee) {
        uee.printStackTrace();
      }
    }
  }
}
