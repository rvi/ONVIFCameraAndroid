package com.rvirin.onvif.onvifcamera;

import android.annotation.SuppressLint;

import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Random;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import static android.util.Base64.*;
import static com.rvirin.onvif.onvifcamera.OnvifDeviceKt.currentDevice;


public class OnvifHeaderBody {

    private static String utcTime;
    private static String nonce;

    public static String getAuthorizationHeader() {
        nonce = "" + new Random().nextInt();
        utcTime = getUTCTime();


        String envelopePart;
        String authorizationPart = "";

        envelopePart = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap:Envelope " +
               "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap=\"http://www.w3.org/2003/05/soap-envelope\" >";
        if (!currentDevice.username.equalsIgnoreCase("")) {
            authorizationPart = "<soap:Header>" +
                    "<wsse:Security xmlns:wsse=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd\">" +
                    "<wsse:UsernameToken xmlns:wsu=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd\">" +
                    "<wsse:Username>" +
                    currentDevice.username +
                    "</wsse:Username>" +
                    "<wsse:Password Type=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-username-token-profile-1.0#PasswordDigest\">" +
                    encryptPassword(currentDevice.password) +
                    "</wsse:Password>" +
                    "<wsse:Nonce EncodingType=\"http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary\">" +
                    toBase64(nonce) +
                    "</wsse:Nonce>" +
                    "<wsu:Created>" +
                    utcTime +
                    "</wsu:Created>" +
                    "</wsse:UsernameToken>" +
                    "</wsse:Security>" +
                    "</soap:Header>";
        }
        return envelopePart + authorizationPart + "<soap:Body>";
    }

    @SuppressWarnings("SameReturnValue")
    public static String getEnvelopeEnd() {
        return "</soap:Body></soap:Envelope>";
    }

    private static String encryptPassword(String password) {
        String beforeEncryption = nonce + utcTime + password;
        byte[] encryptedRaw;
        try {
            MessageDigest SHA1 = MessageDigest.getInstance(MessageDigestAlgorithms.SHA_1);
            SHA1.reset();
            SHA1.update(beforeEncryption.getBytes());
            encryptedRaw = SHA1.digest();
        }
        catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
        String encodedString = toBase64(encryptedRaw);
        encodedString= encodedString.replace("\n","");
        return encodedString;
    }


    /**
     * @param message the message to be encoded
     *
     * @return the enooded from of the message
     */
    private static String toBase64(String message) {
        byte[] data;
        try {
            data = message.getBytes("UTF-8");
            return toBase64(data);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static String toBase64(byte[] data) {
        return encodeToString(data, DEFAULT);
    }

    private static String getUTCTime() {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:sss'Z'");
        sdf.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
        Calendar cal = new GregorianCalendar(TimeZone.getTimeZone("UTC"));
        return sdf.format(cal.getTime());
    }
}
