package sener.blocksatportable.Others;


import android.content.Intent;
import android.net.Uri;

/**
 * <p>
 * This class contains auxiliary methods that are used in different classes
 * </p>
 *
 * @author  Sergio del Horno
 * @version 1.0.0
 * @since   2018-03-10
 */
public class Auxiliary {

    public static Intent sendEmail(String contact, String subject, String text) {
        String[] TO = {contact}; //aquí pon tu correo
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("message/rfc822");
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
// Esto podrás modificarlo si quieres, el asunto y el cuerpo del mensaje
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return emailIntent;
    }

}
