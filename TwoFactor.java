package com.example.satisotomasyonu;

import de.taimos.totp.TOTP;
import javafx.scene.image.Image;
import net.glxn.qrgen.QRCode;
import net.glxn.qrgen.image.ImageType;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

public class TwoFactor {
    String oneTimeCode,secretKey;
    boolean status;
   protected void setSecretKey(String ky){
       this.secretKey=ky;
   }
    protected void startThread(){
        oneTimeCodeThread th= new oneTimeCodeThread();
        th.start();
    }

    private class oneTimeCodeThread extends Thread {
        public void run() {
                String lastCode = null;
                while (!status) {
                    String code = getTOTPCode(secretKey);
                    if (!code.equals(lastCode)) {
                        oneTimeCode=code;
                    }
                    lastCode = code;
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {};
                }

    }

    }
    protected void stopThread(){
        status=true;
    }

    private String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        String key= TOTP.getOTP(hexKey);
        return key ;
    }
    protected boolean checkTwoFactor(String code){
        if (code.equals(oneTimeCode)) {
            stopThread();
            return true;
        } else {
            return false;
        }

    }
    protected  String generateSecretKey() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        return base32.encodeToString(bytes);
    }
    private String getUrl(String secret,String username){
        String secretKey = secret;
        String email = username;
        String companyName = "Ak Turizm";
        String barCodeUrl = getGoogleAuthenticatorBarCode(secretKey, email, companyName);
        return barCodeUrl;

    }
    private  String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }
    protected Image getQr(String key,String username){

        String url=getUrl(key,username);
        ByteArrayOutputStream out = QRCode.from(url).to(ImageType.PNG).withSize(200, 200).stream();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        // SHOW QR CODE
        Image image = new Image(in);
        return image;

    }


}
