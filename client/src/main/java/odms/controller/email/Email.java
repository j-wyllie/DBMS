package odms.controller.email;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;


/**
 * Class to send an email.
 *
 */
@Slf4j
public final class Email {
    private static final String FROM = "HumanFarm@uclive.ac.nz";
    private static final String HOST = "localhost";
    private static final String SERVER = "mail.smtp.host";

    private static Properties properties = System.getProperties();
    private static Session session;

    /**
     * Private constructor to prevent it being called.
     */
    private Email() { }

    /**
     * Sends an email.
     *
     * @param emailAddress The email to send it to.
     * @param messageContent The message.
     * @param subject The content.
     */
    public static void sendMessage(String emailAddress, String messageContent, String subject) {
        setupMessage();

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(FROM));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(emailAddress));
            message.setSubject(subject);
            message.setText(messageContent);
            Transport.send(message);

            log.info("Email sent successfully.");
        } catch (MessagingException e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Sets up the message.
     *
     */
    private static void setupMessage() {
        properties.setProperty(SERVER, HOST);
        session = Session.getDefaultInstance(properties);
    }
}
