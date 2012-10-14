package edu.psu.sweng.ff.notification;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class Email {
	// Constants
	private static final String SERVER = "smtp.gmail.com";
	private static final int PORT = 587;
	private static final String USER_NAME = "PennStateFantasyFootball";
	private static final String PASSWORD = "4i#NA1vvTWE@G#Wf";
	
    // Suppress default constructor
    private Email() {
        throw new AssertionError();
    }
    
	/**
	 * @param fromName the name of the person sending the email
	 * @param fromAddress the source email address
	 * @param toAddress the destination email address
	 * @param subject the subject
	 * @param message the message
	 */
    public static boolean send(String fromName, String fromAddress, String toAddress, String subject, String message) {
        try {
            // setup the mail server properties
            Properties props = new Properties();
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
 
            // set up the message
            Session session = Session.getInstance(props);
 
            Message email = new MimeMessage(session);
 
            //Set to and from addresses
            if( hasNameAndDomain( fromAddress ) ) {
	        	InternetAddress from = new InternetAddress(fromAddress, fromName);
	        	InternetAddress[] fromArray = { from };
	            email.setFrom(from);
	            email.setReplyTo(fromArray);
            }
            else { return false; }
            if( hasNameAndDomain( toAddress ) ) {
	        	InternetAddress to = new InternetAddress(toAddress);
	            email.setRecipient(Message.RecipientType.TO, to);
            }
            else { return false; }
 
            //Set subject and content
            email.setSubject(subject);
            email.setContent(message, "text/plain");
 
            //Send the message
            Transport transport = session.getTransport("smtp");
            transport.connect(SERVER, PORT, USER_NAME, PASSWORD);
            transport.sendMessage(email, email.getAllRecipients());
            
            return true;
        } catch (Exception e) {
        	e.printStackTrace();
        	return false;
        }
    }
    
    private static boolean hasNameAndDomain(String emailAddress){
        String[] tokens = emailAddress.split("@");
        return 
            tokens.length == 2 &&
            notEmpty(tokens[0]) && 
            notEmpty(tokens[1]) ;
    }
    
    private static boolean notEmpty(String s) {
    	 return (s != null && s.length() > 0);
    }
}
