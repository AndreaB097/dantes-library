package danteslibrary.util;

import java.util.Date;
import java.util.Properties;
 
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * Classe utility responsabile dell'invio di email via SMTP. Viene utilizzata
 * per realizzare il caso d'uso Password Dimenticata.
 * @author Andrea Buongusto
 * @author Marco Salierno
 *
 */
public class EmailUtility {
    public static void sendEmail(String host, String port,
            final String username, final String password, String toAddress,
            String subject, String message) throws AddressException,
            MessagingException {
 
        // Imposta i parametri di configurazione SMTP
        Properties properties = new Properties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
 
        Authenticator auth = new Authenticator() {
            public PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        };
 
        Session session = Session.getInstance(properties, auth);
 
        // Viene creata la nuova email
        Message msg = new MimeMessage(session);
 
        msg.setFrom(new InternetAddress(username));
        InternetAddress[] toAddresses = { new InternetAddress(toAddress) };
        msg.setRecipients(Message.RecipientType.TO, toAddresses);
        msg.setSubject(subject);
        msg.setSentDate(new Date());
        msg.setText(message);
 
        // Invio dell'email
        Transport.send(msg);
 
    }
}