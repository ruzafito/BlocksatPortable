package sener.blocksatportable.Others;


import android.content.Intent;
import android.net.Uri;

import java.io.UnsupportedEncodingException;

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

    /**
     * Make an intent to send an email to a given contact with a given subject and text
     * @param contact The email direction where the email will be sent
     * @param subject The subject that will contain the email
     * @param text The text that will contain the email
     * @return The intent with email information to be started in an activity
     */
    public static Intent sendEmail(String contact, String subject, String text) {
        String[] TO = {contact}; //aquí pon tu correo
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setType("message/rfc822");
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        return emailIntent;
    }

    /**
     * Prepares given string message to the correct format to be sent through BLE interface
     * @param sendData The data in string format to be sent
     * @return The data in byte[] format to be sent
     */
    public static byte[] stringToSendData(String sendData){
        byte[] value = null;
        try {
            //convert string to byte[] data in order to send it.
            value = sendData.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  value;
    }

}
